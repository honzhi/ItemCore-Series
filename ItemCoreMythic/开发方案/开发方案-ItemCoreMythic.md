# ItemCoreMythic 开发方案 (v1.0)

## 一、插件定位

> **纯桥接插件**。不实现任何战斗逻辑、属性计算或伤害公式。所有计算委托给 ItemCore API。仅负责将 MythicMobs 的技能、Mechanic、Condition、Placeholder 映射到 ItemCore 能力。

```
┌─────────────────────────────────────────────────┐
│                  MythicMobs                      │
│  skills / mechanics / conditions / placeholders  │
└──────────────────┬──────────────────────────────┘
                   │ (MM API)
┌──────────────────▼──────────────────────────────┐
│              ItemCoreMythic (Bridge)             │
│  SkillExecutor  ICDamageMechanic  Placeholders   │
│           │            │             │           │
│     Provider 层：全部委托给 ItemCore API           │
└──────────────┬─────┬──┴──────┬──────┬───────────┘
               │     │         │      │
┌──────────────▼─────▼─────────▼──────▼───────────┐
│                  ItemCore                        │
│  DamageManager  AttributeCalculator  API         │
└─────────────────────────────────────────────────┘
```

---

## 二、核心功能模块

### 模块 A：技能执行桥接

**方案：** ItemCoreMythic 不修改 ItemCore 代码，通过事件机制实现。

#### A1. ItemCore 事件：`ItemSkillTriggerEvent`

扩展后的事件设计：

```java
public class ItemSkillTriggerEvent extends Event implements Cancellable {
    private final Player player;
    private final CustomItem item;
    private final ItemSkill skill;
    private final SkillTrigger trigger;
    
    // 扩展字段，支持 summon/projectile/icdamage 等技能
    private LivingEntity target;
    private Location location;
    
    // + getter/setter/cancellable
}
```

#### A2. 技能执行流程

```
玩家触发 → ItemSkillListener
            ↓
    ItemSkillTriggerEvent (新事件)
            ↓
ItemCoreMythic.SkillTriggerListener
            ↓
    1. 获取 provider (mythicmobs/mmoitems/...)
    2. 获取 skillName
    3. 构建 SkillMetadata (含 castId)
    4. 根据 provider 路由执行
    5. 清理 SkillMetadata
```

---

### 模块 B：ICDamage Mechanic

**Mechanic 语法：**

```yaml
# MM技能配置
Skills:
  - icdamage{
      amount="<ic.attack_damage>*2 + <ic.spell_damage>*1.5",
      type=spell,
      element=fire,
      crit=true
    } @EIR{r=5}
```

**参数说明：**

| 参数 | 必填 | 默认值 | 说明 |
|------|:---:|--------|------|
| `amount` | ✓ | — | 伤害数值，支持 MM 内置 Placeholder/Variable |
| `type` | ✗ | `physical` | `physical` / `spell` / `projectile` |
| `element` | ✗ | `none` | `fire` / `ice` / `thunder` / `none` |
| `crit` | ✗ | `true` | 是否允许暴击 |
| `penetration` | ✗ | `0` | 额外穿透值 |

**实现：** 继承 `ITargetedEntitySkill`，利用 MM 的 `PlaceholderString` 解析表达式

```java
public class ICDamageMechanic extends SkillMechanic implements ITargetedEntitySkill {
    private final PlaceholderString amount;
    private final DamageTag damageType;
    private final ElementType element;
    private final boolean canCrit;
    private final double penetration;
    
    @Override
    public boolean castAtEntity(SkillMetadata data, AbstractEntity target) {
        // 1. 使用 MM PlaceholderString 解析 amount
        double parsedAmount = amount.getDouble(data);
        
        // 2. 构建 DamageRequest
        DamageRequest request = DamageRequest.builder()
            .attacker(data.getCaster().getEntity())
            .victim((LivingEntity) target.getBukkitEntity())
            .baseDamage(parsedAmount)
            .damageType(damageType)
            .element(element)
            .canCrit(canCrit)
            .penetration(penetration)
            .castId(data.getCastId())
            .build();
        
        // 3. 委托 ItemCore API
        ItemCoreAPI.processDamage(request);
        return true;
    }
}
```

---

### 模块 C：Placeholder 注入（动态注册）

**实现方式：** 启动时遍历 `CustomAttribute.values()` 自动注册

```java
// 自动映射
for(CustomAttribute attr : CustomAttribute.values()) {
    String placeholder = "ic." + attr.getConfigKey();
    placeholderManager.register(placeholder, (player) -> {
        return ItemCoreAPI.getAttribute(player, attr);
    });
}
```

**支持的占位符：**
- `<ic.attack_damage>` → `ItemCoreAPI.getAttribute(player, ATTACK_DAMAGE)`
- `<ic.spell_damage>` → `ItemCoreAPI.getAttribute(player, SPELL_DAMAGE)`
- `<ic.crit_chance>` → `ItemCoreAPI.getAttribute(player, CRIT_CHANCE)`
- ...（自动覆盖全部 CustomAttribute）

**特性：** ItemCore 新增属性后，MM 自动支持，无需修改插件。

---

### 模块 D：通用 Condition

**单一通用 Condition：**

```yaml
# 物品条件
icAttribute{
  attr=attack_damage;
  op=>=;
  value=50
}
```

**参数说明：**

| 参数 | 必填 | 默认值 | 说明 |
|------|:---:|--------|------|
| `attr` | ✓ | — | 属性名（CustomAttribute 的 configKey） |
| `op` | ✓ | — | 比较操作符：`=`, `!=`, `>`, `<`, `>=`, `<=` |
| `value` | ✓ | — | 比较值 |

**实现：**

```java
public class ICAttributeCondition extends Condition {
    private final CustomAttribute attribute;
    private final Operator operator;
    private final double value;
    
    @Override
    public boolean check(SkillMetadata data) {
        Player player = data.getCaster().getPlayer();
        if (player == null) return false;
        
        double current = ItemCoreAPI.getAttribute(player, attribute);
        return operator.compare(current, value);
    }
}
```

---

### 模块 E：ICHeal Mechanic

**Mechanic 语法：**

```yaml
Skills:
  - icheal{
      amount="<ic.spell_power>*2"
    } @P{self}
```

**参数说明：**

| 参数 | 必填 | 默认值 | 说明 |
|------|:---:|--------|------|
| `amount` | ✓ | — | 治疗数值，支持 MM 内置 Placeholder/Variable |

**实现：**

```java
public class ICHealMechanic extends SkillMechanic implements ITargetedEntitySkill {
    private final PlaceholderString amount;
    
    @Override
    public boolean castAtEntity(SkillMetadata data, AbstractEntity target) {
        double parsedAmount = amount.getDouble(data);
        HealingRequest request = HealingRequest.builder()
            .healer(data.getCaster().getEntity())
            .target((LivingEntity) target.getBukkitEntity())
            .amount(parsedAmount)
            .build();
        ItemCoreAPI.processHeal(request);
        return true;
    }
}
```

---

## 三、Provider 架构设计

```
com.minemart.itemcoremythic
├── ItemCoreMythic.java                    # 主类：插件启用/禁用，初始化 Provider
├── bridge/
│   └── SkillBridge.java                   # 技能桥接器：根据 provider 路由
├── provider/
│   ├── SkillProvider.java                 # 接口：执行技能
│   │   └── MythicMobsSkillProvider.java   # 实现：通过 MM API castSkill()
│   ├── DamageProvider.java                # 接口：委托伤害计算
│   │   └── ItemCoreDamageProvider.java    # 实现：ItemCoreAPI.processDamage()
│   ├── AttributeProvider.java             # 接口：查询玩家属性
│   │   └── ItemCoreAttributeProvider.java # 实现：ItemCoreAPI.getAttribute()
│   └── PlaceholderProvider.java           # 接口：解析 <ic.xxx> 占位符
│       └── ItemCorePlaceholderProvider.java # 实现：动态遍历 CustomAttribute
├── mechanic/
│   ├── ICDamageMechanic.java              # MM Mechanic: icdamage{...}
│   └── ICHealMechanic.java                # MM Mechanic: icheal{...}
├── condition/
│   └── ICAttributeCondition.java          # MM Condition: icAttribute{...}
├── placeholder/
│   └── ICPlaceholderExpansion.java        # 注册到 MM PlaceholderManager
└── listener/
    ├── SkillTriggerListener.java          # 监听 ItemSkillTriggerEvent
    └── MMLoadListener.java               # 监听 MythicMobs 加载完成
```

### Provider 接口示例

```java
public interface SkillProvider {
    String getProviderId();
    boolean executeSkill(Player caster, String skillName, LivingEntity target, Location location);
}

public interface DamageProvider {
    void processDamage(DamageRequest request);
}

public interface AttributeProvider {
    double getAttribute(Player player, CustomAttribute attribute);
    AttributeContainer getAttributes(Player player);
}

public interface PlaceholderProvider {
    void registerPlaceholders(PlaceholderManager manager);
}
```

### SkillMetadata 管理（避免同 Tick 多技能覆盖）

```java
public class SkillMetadataManager {
    private static final Map<UUID, DamageContext> damageContexts = new ConcurrentHashMap<>();
    
    public static void setDamageContext(UUID castId, DamageContext ctx) {
        damageContexts.put(castId, ctx);
    }
    
    public static DamageContext getDamageContext(UUID castId) {
        return damageContexts.remove(castId);
    }
}
```

---

## 四、ItemCore 扩展 API

### 4.1 ItemSkill 扩展（Provider Namespace）

```yaml
# 物品配置示例
skills:
  on_hit:
    provider: mythicmobs
    skill: fire_slash
  on_timer:
    provider: mmoitems
    skill: passive_aura
```

```java
public class ItemSkill {
    private final SkillTrigger trigger;
    private final String provider;      // 新增：技能提供者
    private final String skillName;     // 技能名（相对于 provider）
    private final int timerDuration;
}
```

### 4.2 ItemCoreAPI 公共入口

```java
public class ItemCoreAPI {
    // 伤害处理入口
    public static void processDamage(DamageRequest request) {
        DamageManager.processDamage(request);
    }
    
    // 治疗处理入口
    public static void processHeal(HealingRequest request) {
        HealManager.processHeal(request);
    }
}
```

---

## 五、依赖关系

### 5.1 编译时依赖

```xml
<!-- pom.xml -->
<dependencies>
    <!-- ItemCore API（编译时，provided） -->
    <dependency>
        <groupId>com.minemart</groupId>
        <artifactId>itemcore</artifactId>
        <version>1.0.0</version>
        <scope>provided</scope>
    </dependency>

    <!-- MythicMobs API（编译时） -->
    <dependency>
        <groupId>io.lumine</groupId>
        <artifactId>Mythic-Dist</artifactId>
        <version>5.7.2</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### 5.2 运行时依赖

```yaml
# plugin.yml
depend: [ItemCore]
softdepend: [MythicMobs]
```

| 依赖 | 必选/可选 | 说明 |
|------|:---:|------|
| ItemCore | **必选** | 所有计算委托目标 |
| MythicMobs | **必选** | 技能执行引擎 |
| PlaceholderAPI | 可选 | 已有 ItemCore 自己的 PAPI 扩展 |

---

## 六、与 ItemCore 的双向交互

### 6.1 ItemCore → ItemCoreMythic

```
ItemCore: ItemSkillListener 触发技能
    ↓ 发布 ItemSkillTriggerEvent (含 target/location)
ItemCoreMythic: SkillTriggerListener 监听
    ↓ 根据 provider 路由
MythicMobs: 执行技能（通过 SkillBridge）
```

### 6.2 ItemCoreMythic → ItemCore

```
MythicMobs: 技能中使用 icdamage/icheal Mechanic
    ↓
ItemCoreMythic: 构建 DamageRequest/HealingRequest
    ↓
ItemCore: ItemCoreAPI.processDamage() / processHeal()
    ↓
DamageManager / HealManager 处理完整计算链路
```

---

## 七、实施计划（优先顺序）

| 阶段 | 内容 | 产出 | 优先级 |
|------|------|------|--------|
| **Phase 1** | ItemCore 新增 `ItemSkillTriggerEvent`（扩展 target/location） | 1个新Event类 | **P0** |
| **Phase 2** | ItemCore 修改 `ItemSkill` 添加 provider namespace | 修改1个类 | **P0** |
| **Phase 3** | ItemCore 添加 `ItemCoreAPI.processDamage()` / `processHeal()` | 修改1个类 | **P0** |
| **Phase 4** | ItemCore 修改 `ItemSkillListener` 触发新事件 | 修改1个类 | **P0** |
| **Phase 5** | ItemCoreMythic 搭建项目骨架 | pom.xml, plugin.yml, 主类 | **P1** |
| **Phase 6** | ItemCoreMythic 实现 Provider 层 | 4接口 + 4实现 | **P1** |
| **Phase 7** | ItemCoreMythic 实现 ICDamageMechanic（ITargetedEntitySkill） | 核心 Mechanic | **P0** |
| **Phase 8** | ItemCoreMythic 实现 ICHealMechanic | 治疗 Mechanic | **P1** |
| **Phase 9** | ItemCoreMythic 实现动态 Attribute Placeholder | 自动注册 | **P0** |
| **Phase 10** | ItemCoreMythic 实现通用 ICAttributeCondition | 条件系统 | **P1** |
| **Phase 11** | ItemCoreMythic 实现 SkillTriggerListener | 技能执行链路 | **P0** |

---

## 八、风险与对策

| 风险 | 对策 |
|------|------|
| MM API 版本差异 (v5 vs v6) | Provider 接口隔离实现细节，可添加适配器层 |
| MM 类加载时机 | `MMLoadListener` 等待 `MythicReloadedEvent` 后再注册 |
| 占位符性能（高频调用） | 缓存 `CustomAttribute.values()` 遍历结果 |
| icdamage 的 amount 表达式解析 | 利用 MM 的 `PlaceholderString`，不做自定义解析 |
| 同 Tick 多技能覆盖 | 使用 UUID castId 作为 key |
| DamageManager 重构影响 | 桥接层仅依赖 `ItemCoreAPI`，不直接依赖 Manager |

---

## 九、设计原则

1. **职责单一**：每个 Provider 仅 1~3 个方法
2. **接口隔离**：桥接层仅依赖 ItemCoreAPI，不依赖内部 Manager
3. **动态注册**：Placeholder/Condition 自动适配 ItemCore 属性扩展
4. **Provider 路由**：通过 namespace 支持多技能系统（MM/MMOItems/...）
5. **事件驱动**：避免直接侵入 ItemCore 代码