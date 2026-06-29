# DEV-004: 玩家信息 GUI

**日期**: 2026-06-02
**类型**: 新功能
**影响范围**: ItemCoreRPG 全插件

---

## 概述

新增 `/icrpg info` 命令，打开 GUI 展示玩家的所有基础属性。

---

## 新增文件

| 文件 | 说明 |
|------|------|
| `gui/StatsMenu.java` | GUI 界面构建，8 个分类属性物品 + 玩家头像 + 刷新/关闭 |
| `gui/GuiListener.java` | 点击事件处理 + 查看者→目标映射追踪 |

## 修改文件

| 文件 | 修改 |
|------|------|
| `command/CommandManager.java` | 添加 `info` 子命令，支持 `/icrpg info [玩家]` |
| `ItemCoreRPG.java` | 注册 GuiListener |
| `plugin.yml` | 添加 `itemcorerpg.command.info` 和 `info.other` 权限 |

---

## GUI 设计

54 格物品栏，**8 个分类物品**展示所有属性：

| 槽位 | 图标 | 分类 | 包含属性 |
|------|------|------|---------|
| 19 | 铁剑 | ⚔ 战斗属性 | ATTACK_DAMAGE, ATTACK_SPEED, ATTACK_RANGE, KNOCKBACK |
| 20 | 盾牌 | 🛡 防御属性 | PHYSICAL_RESIST(含减伤%), SPELL_RESIST(含减伤%), DAMAGE_REDUCTION |
| 21 | 烈焰粉 | 💥 暴击属性 | CRIT_CHANCE, CRIT_DAMAGE |
| 22 | 金苹果 | ❤ 生存属性 | HEALTH(额外), MOVEMENT_SPEED, REGENERATION |
| 23 | 末影眼 | 🔥 元素属性 | 各元素精通/抗性/积累进度 |
| 24 | 下界星 | ✨ 进阶属性 | SPELL_POWER, ADAPTIVE_FORCE, LUCK |
| 25 | 弓 | 🏹 伤害加成 | PHYSICAL_DAMAGE%, PROJECTILE_DAMAGE%, SPELL_DAMAGE% |
| 26 | 箭 | 📌 穿透属性 | PHYSICAL_PENETRATION(含%), SPELL_PENETRATION(含%) |

**槽位 4**：玩家头颅（显示头像、在线状态、生命值、等级、饥饿值）
**槽位 48**：刷新按钮
**槽位 50**：关闭按钮

## 命令

- `/icrpg info` — 查看自己的属性
- `/icrpg info <玩家>` — 查看其他玩家属性（需 `info.other` 权限）

## 数据来源

所有属性数据通过 `ItemCoreAPI.getPlayerAttributes(player)` 获取，元素系统数据通过 `ItemCoreAPI.getElementProgress()` / `getElementMastery()` / `getElementResistance()` 获取。
