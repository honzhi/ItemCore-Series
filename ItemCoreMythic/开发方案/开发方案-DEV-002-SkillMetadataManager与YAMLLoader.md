# 开发方案 DEV-002 - SkillMetadataManager 与 YAML Loader

## 概述

本阶段开发 ItemCoreMythic 的核心缺失部分，完成 MVP 的最后两块拼图：
1. SkillMetadataManager - 技能上下文管理（解决同 Tick 多技能覆盖问题
2. YAML Loader - 完成技能配置的完整解析链路

---

## 一、DEV-002-01: SkillMetadataManager

### 目标

解决当前链路的上下文缺失问题：

```
当前：
ItemCore Skill
    ↓
MM Skill
    ↓
ICDamage
    ↓
(缺失上下文：谁、哪个技能、哪个物品、哪个cast?)

目标：
ItemCore Skill
    ↓
SkillTriggerEvent (带 castId)
    ↓
SkillMetadataManager (保存上下文)
    ↓
MM Skill (注入 castId)
    ↓
ICDamage (查询上下文)
    ↓
ItemCoreAPI.processDamage (带完整上下文)
```

### 设计

**SkillContext 类（ItemCore 或 ItemCoreMythic？）

```
class SkillContext {
    UUID castId;                    // 施法 ID，唯一标识
    UUID playerId;                  // 玩家 ID
    String provider;                // 提供者 (mythicmobs/mmoitems...)
    String skillName;               // 技能名
    CustomItem sourceItem;          // 来源物品
    long createTime;              // 创建时间戳
    SkillTrigger triggerType;         // 触发类型 (ON_HIT/ON_CRIT/ON_SKILL/...)
    LivingEntity target;             // 目标实体
    Location location;             // 触发位置
}
```

**SkillMetadataManager 类（ItemCoreMythic）

```java
public class SkillMetadataManager {
    private static final Map<UUID, SkillContext> activeContexts = new ConcurrentHashMap<>();
    private static final long CONTEXT_TTL_MS = 5000; // 5秒超时

    public static UUID createContext(
        Player player,
        CustomItem item,
        ItemSkill skill,
        SkillTrigger trigger,
        LivingEntity target,
        Location location
    ) {
        UUID castId = UUID.randomUUID();
        SkillContext context = new SkillContext(
            castId,
            player.getUniqueId(),
            skill.getProvider(),
            skill.getSkillName(),
            item,
            System.currentTimeMillis(),
            trigger,
            target,
            location
        );
        activeContexts.put(castId, context);
        return castId;
    }

    public static SkillContext getContext(UUID castId) {
        return activeContexts.get(castId);
    }

    public static void removeContext(UUID castId) {
        activeContexts.remove(castId);
    }

    // 定时清理过期 Context
    public static void cleanup() {
        long now = System.currentTimeMillis();
        activeContexts.entrySet().removeIf(e -> now - e.getValue().createTime > CONTEXT_TTL_MS);
    }
}
```

### 链路修改 ItemCore 侧修改

**ItemSkillTriggerEvent** 添加 castId**

```java
public class ItemSkillTriggerEvent extends Event implements Cancellable {
    // ... 现有字段 ...
    private UUID castId; // 新增

    public UUID getCastId() { return castId; }
}
```

**ItemSkillListener 触发事件时生成 castId**

```java
private void triggerSkills(Player player, CustomItem item, SkillTrigger trigger, LivingEntity target) {
    for (ItemSkill skill : item.getSkills()) {
        if (skill.getTrigger() == trigger) {
            UUID castId = SkillMetadataManager.createContext(player, item, skill, trigger, target, player.getLocation());
            ItemSkillTriggerEvent event = new ItemSkillTriggerEvent(player, item, skill, trigger, target, player.getLocation());
            event.setCastId(castId);
            Bukkit.getPluginManager().callEvent(event);
        }
    }
}
```

### ItemCoreMythic 侧修改

**MythicMobsSkillProvider** 将 castId 传递给 MM 技能

```java
public class MythicMobsSkillProvider {
    // ...

    public boolean executeSkill(Player player, String skillName, LivingEntity target, Location location, UUID castId) {
        // ... 保存上下文
        // ... 执行技能时，注入 castId
        // ... MM技能中，mechanic里通过 castId 查询上下文
    }
}
```

**ICDamageMechanic** 使用 castId 查询上下文

```java
public boolean castAtEntity(SkillMetadata data, AbstractEntity target) {
    // ...
    UUID castId = data.getCastId(); // MM API 获取
    SkillContext context = SkillMetadataManager.getContext(castId);
    // ... 构建 DamageRequest 时使用
    DamageRequest request = DamageRequest.builder()
        .attacker(...)
        .castId(castId)
        .build();
    // ...
}
```

---

## 二、DEV-002-02: YAML Loader (Skill 配置解析

### 目标

完成 `ItemCore 侧需要修改 ItemSkill 加载器，解析 provider/skill 配置：

**物品 YAML 配置示例：

```yaml
id: legendary_sword
name: "&6Legendary Sword
skills:
  on_hit:
    provider: mythicmobs
    skill: fire_slash
  on_crit:
    provider: mythicmobs
    skill: thunder_strike
  on_timer:
    provider: mythicmobs
    skill: passive_aura
    timer: 20
```

### ItemCore 侧修改

**ItemSkill 类** - 需要支持 provider 字段**

```java
public class ItemSkill {
    private final SkillTrigger trigger;
    private final String provider;  // 新增
    private final String skillName;
    private final int timerDuration;
    // ...
}
```

**ItemLoader 类** 加载 provider/skill 配置**

```java
// 从 YAML 加载 skills:
// skills:
//   on_hit:
//     provider: mythicmobs
//     skill: fire_slash
```

---

## 三、DEV-003: 更多 Condition（预留规划

```yaml
- icHasItem{item="weapons/legendary_sword"}
- icHasEquipped{slot="chest";item="armors/netherite_chestplate"}
- icSlotActive{slot="chest"}
```

## 四、DEV-004: 更多 Mechanic（预留规划）

```yaml
- icshield{amount=100;duration=200}
- icbuff{attr="haste;level=2;duration=100}
- icdebuff{attr="slow";level=2;duration=100}
```

---

## 实施优先级

| 阶段 | 优先级 |
|------|--------|
| SkillMetadataManager | **P0 (必须)** |
| YAML Loader | **P0 (必须)** |
| 更多 Condition | P1 |
| 更多 Mechanic | P1 |
