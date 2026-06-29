# ItemCore

> 版本: v1.0.0 | Paper 1.21.x | 依赖: PlaceholderAPI(可选)

---

ItemCore 是一套面向 Paper 1.21.x 的自定义物品系统插件，包含三个子插件：

| 插件 | 用途 |
|------|------|
| **ItemCore** | 核心插件：物品管理、属性计算、伤害系统、元素系统、GUI |
| **ItemCoreRPG** | RPG 拓展：玩家信息 GUI、伤害飘字显示 |
| **ItemCoreMythic** | MythicMobs 桥接：技能触发、ICDamageMechanic |

## 核心特性

- 22 种自定义属性（攻击、防御、暴击、穿透等）
- 3 种默认元素类型（流火/寒霜/雷蛰），框架级可扩展
- 自定义伤害计算系统（物理/法术/射弹 + 元素混合伤害）
- 元素积累/异常机制（灼烧 DOT、寒霜减双抗、雷蛰增伤）
- 自动 Lore 生成（可配置布局 + 白/橙色配色）
- 物品库 GUI（分类浏览、获取物品）
- PlaceholderAPI 集成（23 个占位符）
- MythicMobs 技能桥接（ICDamageMechanic）
- 热重载：修改配置后 `/ic reload` 即可生效

## 开始使用

- [安装与配置](getting-started.md)
- [命令系统](commands.md)
- [物品创建](items.md)
- [属性系统](attributes.md)
- [元素系统](elements.md)

## 项目地址

[GitHub - honzhi/ItemCore](https://github.com/honzhi/ItemCore)