# 开发日志 DEV-004 - Debug 日志系统实现

## 概述

本次开发为 ItemCoreMythic 添加了完整的 Debug 日志系统，包括配置开关、工具类和各关键模块的调试日志输出。

## 新增文件

### 1. config.yml - 配置文件
**文件**: `src/main/resources/config.yml`

```yaml
debug: false
```

### 2. DebugLogger.java - 调试日志工具类
**文件**: `src/main/java/com/minemart/itemcoremythic/util/DebugLogger.java`

提供静态方法，根据 DEBUG 开关决定是否输出日志。

---

## 修改文件

### 1. ItemCoreMythic.java - 主类
**修改**:
- 添加 `public static boolean DEBUG` 静态字段
- 在 `onEnable()` 中读取配置：`DEBUG = getConfig().getBoolean("debug", false)`
- 添加启动自检横幅输出

**启动日志示例**:
```
===== ItemCoreMythic =====
Version: 1.0.0
Debug: true
=========================
ItemCore: OK (v1.0.0)
MythicMobs: OK (v5.11.0)
SkillProvider: MythicMobsSkillProvider
```

### 2. SkillTriggerListener.java - 技能触发监听
**修改**: 添加技能触发时的详细日志

**日志示例**:
```
[ItemCoreMythic] [SkillTrigger] Skill Triggered | Player=Steve Provider=mythicmobs Skill=fire_slash Trigger=ON_HIT
[ItemCoreMythic] [SkillTrigger] Target=Zombie
[ItemCoreMythic] [SkillTrigger] Location=world (100, 64, 200)
```

### 3. SkillBridge.java - 技能桥接器
**修改**: 添加路由前后的日志

**日志示例**:
```
[ItemCoreMythic] [SkillBridge] Routing skill to provider=mythicmobs, skill=fire_slash
[ItemCoreMythic] [SkillBridge] Provider result=true, skill=fire_slash
```

### 4. MythicMobsSkillProvider.java - MM技能提供者
**修改**: 添加技能执行前后的日志，异常时记录完整堆栈

**日志示例**:
```
[ItemCoreMythic] [MythicMobsSkillProvider] Casting MM skill=fire_slash
[ItemCoreMythic] [MythicMobsSkillProvider] Found cast method: cast
[ItemCoreMythic] [MythicMobsSkillProvider] MM skill executed successfully: fire_slash
```

### 5. ICDamageMechanic.java - 伤害机制
**修改**: 添加完整的执行流程日志

**日志示例**:
```
[ItemCoreMythic] [ICDamageMechanic] ICDamage invoked
[ItemCoreMythic] [ICDamageMechanic] Amount=120.0, Type=SPELL, Element=FIRE
[ItemCoreMythic] [ICDamageMechanic] Target=Zombie
[ItemCoreMythic] [ICDamageMechanic] Sending DamageRequest
```

### 6. ICHealMechanic.java - 治疗机制
**修改**: 添加完整的执行流程日志

**日志示例**:
```
[ItemCoreMythic] [ICHealMechanic] ICHeal invoked
[ItemCoreMythic] [ICHealMechanic] Amount=300.0
[ItemCoreMythic] [ICHealMechanic] Target=Steve
[ItemCoreMythic] [ICHealMechanic] Sending HealingRequest
```

### 7. MMLoadListener.java - MM加载监听
**修改**: 添加 Mechanic 注册日志

**日志示例**:
```
[ItemCoreMythic] [MMLoadListener] MythicMobs reloaded, registering mechanics
[ItemCoreMythic] [MMLoadListener] Registering mechanic: icdamage
[ItemCoreMythic] [MMLoadListener] Registering mechanic: icheal
[ItemCoreMythic] [MMLoadListener] MythicMobs mechanics registered
```

### 8. ICPlaceholderExpansion.java - 占位符扩展
**修改**: 添加占位符注册和解析日志

**日志示例**:
```
[ItemCoreMythic] [ICPlaceholderExpansion] Registered placeholder: ic.attack_damage
[ItemCoreMythic] [ICPlaceholderExpansion] Placeholder ic.attack_damage = 185.0
```

---

## 核心调试链路

```
ItemCore                    ↓ ItemSkillTriggerEvent                    [SkillTrigger] Skill Triggered

ItemCoreMythic.SkillTriggerListener                    ↓ executeSkill()                    [SkillBridge] Routing skill to provider

ItemCoreMythic.SkillBridge                    ↓ executeSkill()                    [MythicMobsSkillProvider] Casting MM skill

MythicMobs                    ↓ Skill Execution                    [ICDamageMechanic] ICDamage invoked

ItemCoreMythic.ICDamageMechanic                    ↓ processDamage()                    [ICDamageMechanic] Sending DamageRequest

ItemCore                    ↓ DamageManager                    (完成)
```

---

## 配置使用

在 `plugins/ItemCoreMythic/config.yml` 中设置：

```yaml
debug: true  # 启用调试日志
# debug: false  # 禁用调试日志（默认）
```

---

## 构建结果

**产物**: `target/itemcoremythic-1.0.0.jar`
**状态**: ✅ 构建成功

---

## 下一步计划

1. **DEV-005**: 实现 SkillMetadataManager（技能上下文管理）
2. **DEV-006**: 实现 YAML Loader（技能配置解析）
3. 服务器测试联调
