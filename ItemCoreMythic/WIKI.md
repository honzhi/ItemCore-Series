# ItemCoreMythic Wiki

## 概述

ItemCoreMythic 是一个桥接插件，用于连接 ItemCore 和 MythicMobs，实现物品技能与 MM 技能系统的无缝集成。

**核心特性**:

- 将 ItemCore 物品技能触发事件路由到 MythicMobs 执行
- 提供 ICDamage Mechanic（完整支持 amount/type/element/attackType/crit/penetration/lifesteal）
- 提供 ICHeal Mechanic（支持 PlaceholderString 表达式）
- 动态注册所有 ItemCore 属性作为 MM 占位符（`<ic.xxx>`）
- attackType 系统：普攻模式享受玩家暴击/吸血，技能模式走独立参数
- 元素系统：流火/寒霜/雷蛰，支持中英双语参数名

***

## 安装与配置

| 插件 | 版本 | 说明 |
| ---- | ---- | ---- |
| Paper | 1.21+ | 服务端 |
| ItemCore | 1.0.0 | 物品核心 |
| MythicMobs | 5.11.0 | 技能引擎 |

1. 将 `itemcoremythic-1.0.0.jar` 放入 `plugins/` 目录
2. 启动服务器自动生成配置文件
3. 编辑 `plugins/ItemCoreMythic/config.yml`

### 配置文件

```yaml
# 是否启用调试日志
debug: false

# 技能提供者配置
providers:
  mythicmobs:
    enabled: true
```

***

## 架构链路

```
ItemCore 物品技能触发 → ItemSkillTriggerEvent
  → SkillTriggerListener (ItemCoreMythic)
    → SkillBridge (路由到 mythicmobs provider)
      → MythicMobsSkillProvider
        → BukkitAPIHelper.castSkill(player, skillName, location)

MM 技能内使用:
  icdamage → DamageRequest → ItemCoreAPI.processDamage()
  icheal   → HealingRequest → ItemCoreAPI.processHeal()
```

***

## MythicMobs 技能写法

### 1. ICDamage Mechanic

在 MM 技能中触发 ItemCore 完整伤害计算链路（属性加成、元素、暴击、穿透、吸血）。

**语法**: `- icdamage{参数} @目标选择器`

#### 参数一览

| 参数 | 缩写 | 类型 | 默认值 | 说明 |
|------|------|------|--------|------|
| amount | a | String | `1` | 伤害数值，支持 `<ic.xxx>` 占位符与四则运算 |
| type | t | String | `spell` | 伤害类型: `physical` / `spell` / `projectile` |
| attackType | at | String | `skill` | 攻击模式: `attack`(普攻) / `skill`(技能) |
| element | e | String | `none` | 元素类型: `none` / `fire`(liuhuo) / `ice`(hanshuang) / `thunder`(leizhe) |
| crit | c | Boolean | `true` | 是否允许暴击 |
| penetration | p | Double | `0` | 额外穿透值（叠加玩家属性穿透） |
| lifesteal | ls | Double | `0` | 吸血比例 0.0~1.0（仅 attackType=attack 生效） |

> **注意**: MythicLineConfig 对带引号的复合参数存在解析 Bug，`type=` 等后续参数会被吞入 `amount`。
> 本 Mechanic 已实现**手动参数解析**，从 amount 字符串尾部提取所有附加参数，不受此 Bug 影响。

#### 元素类型（中英双语）

| 配置值 | 元素 | 颜色 |
|--------|------|------|
| `none` | 无元素 | — |
| `fire` / `liuhuo` | 流火 🔥 | 红色 |
| `ice` / `hanshuang` | 寒霜 ❄ | 蓝色 |
| `thunder` / `leizhe` | 雷蛰 ⚡ | 紫色 |

#### attackType 行为差异

| 维度 | `attack`（普攻模式） | `skill`（技能模式，默认） |
|------|---------------------|--------------------------|
| 伤害类型 | **强制 PHYSICAL** | 按 `type` 参数指定 |
| 伤害加成 | `attack_damage` + `physical_damage`% | 使用传入的 type |
| 暴击率来源 | 玩家 `crit_chance` 属性 | 不暴击（critChance=0）|
| 暴击伤害 | 玩家 `crit_damage` 属性 | 固定 150% |
| 吸血 | ✅ `lifesteal` 参数生效 | ❌ 不触发 |
| `crit=false` | 关闭暴击，吸血不受影响 | 关闭暴击 |

#### 示例

```yaml
Skills:
  # 基础伤害
  - icdamage{amount=50} @P{self}

  # 法术伤害 + 火焰元素 + 群伤
  - icdamage{amount="<ic.spell_power>*2",type=spell,element=fire} @EIR{r=5}

  # 高穿甲物理伤害
  - icdamage{amount="<ic.attack_damage>*1.5",type=physical,penetration=30} @target

  # 普攻模式：享受玩家暴击 + 吸血
  - icdamage{amount="<ic.attack_damage>",attackType=attack,lifesteal=0.2} @target
```

***

### 2. ICHeal Mechanic

在 MM 技能中触发 ItemCore 治疗计算。

**语法**: `- icheal{参数} @目标选择器`

| 参数 | 别名 | 类型 | 默认值 | 说明 |
|------|------|------|--------|------|
| amount | a | String | `1` | 治疗数值，支持表达式 |

```yaml
Skills:
  - icheal{amount=100} @P{self}
  - icheal{amount="<ic.spell_power>*0.5"} @P{self}
  - icheal{amount=50} @PlayersNearOrigin{r=10}
```

***

### 3. ICAttribute Condition（开发中 🚧）

> 当前尚未实现，`condition` 包已预留。以下为规划语法。

```yaml
Conditions:
  - icattribute{attr=health;op=>=;value=50} true
```

| 参数 | 类型 | 说明 |
|------|------|------|
| attr | String | 属性名（ItemCore CustomAttribute 配置键）|
| op | String | 比较符: `>` / `>=` / `<` / `<=` / `==` / `!=` |
| value | Double | 比较值 |

***

## Placeholder 占位符

启动时自动遍历 ItemCore 的 `CustomAttribute.values()`，动态注册为 MM 占位符。

### 属性占位符

**语法**: `<ic.属性配置键>`（全小写）

常见属性（完整列表由 ItemCore 的 CustomAttribute 枚举决定）:

| 占位符 | 说明 | 类型 |
| -------- | ---- | ---- |
| `<ic.attack_damage>` | 攻击伤害 | 数值 |
| `<ic.spell_power>` | 法术强度 | 数值 |
| `<ic.adaptive_force>` | 适应之力 | 数值 |
| `<ic.crit_chance>` | 暴击几率 | 百分比 |
| `<ic.crit_damage>` | 暴击伤害 | 百分比 |
| `<ic.armor>` | 护甲 | 数值 |
| `<ic.health>` | 生命值 | 数值 |
| `<ic.movement_speed>` | 移动速度 | 数值 |
| ... | 其他 ItemCore 注册的属性 | — |

### 元素占位符

| 占位符 | 说明 |
| -------- | ---- |
| `<ic.element_mastery.liuhuo>` | 流火元素精通 |
| `<ic.element_mastery.hanshuang>` | 寒霜元素精通 |
| `<ic.element_mastery.leizhe>` | 雷蛰元素精通 |
| `<ic.element_resist.liuhuo>` | 流火元素抗性 |
| `<ic.element_resist.hanshuang>` | 寒霜元素抗性 |
| `<ic.element_resist.leizhe>` | 雷蛰元素抗性 |

***

## 物品技能配置

在 ItemCore 物品配置的 `skills` 字段中指定:

```yaml
skills:
  on_hit:
    provider: mythicmobs
    skill: fire_slash
  on_right_click:
    provider: mythicmobs
    skill: heal_aura
  on_spell:
    provider: mythicmobs
    skill: thunder_strike
```

**可用触发器**: 由 ItemCore 的 `SkillTrigger` 枚举定义，共 5 个:

| 配置写法 | 说明 |
|----------|------|
| `on_left_click` | 左键点击时 |
| `on_right_click` | 右键点击时 |
| `on_timer` | 定时循环触发 |
| `on_attack` | 攻击时 |
| `on_hit` | 击中时 |

> 物品技能配置暂不支持 `on_swing`、`on_critical`、`on_spell` 等触发器。

***

## 完整技能示例

### 示例 1: 烈焰斩

```yaml
# MythicMobs/Skills/fire_slash.yml
fire_slash:
  Skills:
    - icdamage{amount="<ic.attack_damage>*1.5",type=physical,element=fire} @target
    - effect:particles{p=flame;amount=30;speed=0.3} @target
    - sound{s=entity.blaze.ambient;v=1;p=1} @target
  Cooldown: 5
```

```yaml
# 物品配置
skills:
  on_hit:
    provider: mythicmobs
    skill: fire_slash
```

### 示例 2: 治疗光环

```yaml
# MythicMobs/Skills/heal_aura.yml
heal_aura:
  Skills:
    - icheal{amount="<ic.spell_power>*0.3"} @PlayersNearOrigin{r=5}
    - effect:particles{p=heart;amount=30;speed=0.2} @self
    - sound{s=entity.player.levelup;v=1;p=1} @self
  Cooldown: 10
```

```yaml
skills:
  on_right_click:
    provider: mythicmobs
    skill: heal_aura
```

### 示例 3: 雷霆一击

```yaml
# MythicMobs/Skills/thunder_strike.yml
thunder_strike:
  Skills:
    # 技能模式：法术伤害 + 雷蛰元素
    - icdamage{amount="<ic.spell_power>*3",type=spell,element=thunder} @EIR{r=8}
    # 普攻模式：附带 10% 吸血
    - icdamage{amount="<ic.attack_damage>*0.5",attackType=attack,lifesteal=0.1} @EIR{r=8}
    - lightning @EIR{r=8}
    - effect:particles{p=endRod;amount=50;speed=0.5} @EIR{r=8}
    - sound{s=entity.lightning_bolt.thunder;v=2;p=2} @origin
  Cooldown: 8
```

```yaml
skills:
  on_spell:
    provider: mythicmobs
    skill: thunder_strike
```

***

## 调试指南

### 启用调试模式

```yaml
# config.yml
debug: true
```

### 调试日志

```
===== ItemCoreMythic =====
Version: 1.0.0
Debug: true
=========================
ItemCore: OK (v1.0.0)
MythicMobs: OK (v5.11.0)
SkillProvider: MythicMobsSkillProvider

[ItemCoreMythic] [SkillTrigger] Skill Triggered | Player=Steve Provider=mythicmobs Skill=fire_slash Trigger=ON_HIT
[ItemCoreMythic] [SkillBridge] Routing skill to provider=mythicmobs, skill=fire_slash
[ItemCoreMythic] [MythicMobsSkillProvider] Casting MM skill=fire_slash
[ItemCoreMythic] [ICDamageMechanic] ICDamage invoked | Amount=120.0, Type=SPELL, Element=FIRE, AttackType=SKILL
[ItemCoreMythic] [ICDamageMechanic] Target=Zombie
```

### 常见问题

| 问题 | 可能原因 | 解决方法 |
|------|----------|----------|
| 技能不触发 | ItemCore 未发送事件 | 检查 ItemCore 配置 |
| 技能执行失败 | MM 技能不存在 | 检查技能名称拼写 |
| 伤害为 0 | 属性占位符未解析 | 检查属性名拼写 |
| 插件加载失败 | 依赖缺失 | 确保 ItemCore/MM 已安装 |

### 伤害计算链路

```
ICDamageMechanic.castAtEntity()
  → AttributePlaceholderResolver.resolve(rawAmount, player)   # 替换 <ic.xxx>
  → evaluateMath(resolvedExpr)                                 # 四则运算求值
  → DamageRequest.builder()
      .attackType(attack/skill)                                # 攻击模式分流
      .critChance/critDamage (attack 模式读取玩家属性)          # 暴击来源
      .penetration (参数 + 玩家穿透)                            # 穿透
      .lifesteal                                              # 吸血
      .element (元素加成/抗性)                                  # 元素系统
      .build()
  → ItemCoreAPI.processDamage(request)                         # 委托 ItemCore
```

***

## API 说明

```java
// 获取技能桥接器
SkillBridge bridge = ItemCoreMythic.getInstance().getSkillBridge();

// 执行技能
bridge.executeSkill(player, "mythicmobs", "fire_slash", target, location);

// 注册自定义提供者
bridge.registerProvider(new CustomSkillProvider());
```

### 自定义 Provider

```java
public class CustomSkillProvider implements SkillProvider {
    @Override
    public String getProviderId() { return "custom"; }

    @Override
    public boolean executeSkill(Player player, String skillName, LivingEntity target, Location location) {
        // 自定义技能执行逻辑
        return true;
    }
}
```

***

## 版本历史

| 版本 | 日期 | 内容 |
|------|------|------|
| 1.0.1 | 2026-06-02 | 参数补全：attackType 系统、元素系统（中英双语）、穿透/吸血、手动参数解析绕过 MM Bug、type 默认改为 spell |
| 1.0.0 | 2026-06-02 | MVP 发布：ICDamage、ICHeal、占位符、Provider 路由 |

### v1.0.1 验收结果

```
attackType=attack   → 强制 PHYSICAL + 玩家暴击属性 + 吸血
attackType=skill    → 按 type 参数 + 无吸血
element=liuhuo      → LIUHUO (红色 🔥)
element=hanshuang   → HANSHUANG (蓝色 ❄)
element=leizhe      → LEIZHE (紫色 ⚡)
penetration=50      → 穿透叠加生效
lifesteal=0.2       → 吸血 20%
```

***

## License

MIT

