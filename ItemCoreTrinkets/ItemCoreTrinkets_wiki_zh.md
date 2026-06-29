# ItemCoreTrinkets 使用文档

> 基于 ItemCore 的饰品槽位系统

---

## 1. 概述

ItemCoreTrinkets 是一个**饰品槽位系统**插件，依赖于 ItemCore。玩家可以通过 GUI 界面将 ItemCore 物品装备到各个饰品槽位中，装备后饰品自带的属性会自动叠加到玩家身上。

### 核心特性

- 可视化 GUI 管理界面，支持自定义布局
- 任意数量、任意类型的饰品槽位
- 按物品类型匹配槽位（如戒指 → 戒指槽）
- 槽位条件系统（权限/等级限制）
- 属性自动计算（通过 ItemCore 的 AttributeProvider 机制）

---

## 📖 目录


1. [1. 概述](#1-概述)
  - [核心特性](#核心特性)
1. [2. 快速开始](#2-快速开始)
  - [基础流程](#基础流程)
  - [权限](#权限)
1. [3. 配置文件详解](#3-配置文件详解)
  - [3.1 config.yml — 插件设置](#31-configyml-插件设置)
  - [3.2 slots.yml — 槽位定义](#32-slotsyml-槽位定义)
    - [格式](#格式)
    - [示例](#示例)
  - [3.3 gui.yml — 界面布局](#33-guiyml-界面布局)
    - [settings — 基础设置](#settings-基础设置)
    - [layout — 字符映射布局](#layout-字符映射布局)
    - [slots — 槽位具体定义](#slots-槽位具体定义)
    - [所有 function 类型](#所有-function-类型)
    - [关于锁定状态](#关于锁定状态)
  - [3.4 messages.yml — 消息文本](#34-messagesyml-消息文本)
1. [4. 教程：配置一个饰品](#4-教程配置一个饰品)
  - [目标](#目标)
  - [步骤 1：在 ItemCore 中创建物品类型](#步骤-1在-itemcore-中创建物品类型)
  - [步骤 2：在 ItemCore 中创建物品](#步骤-2在-itemcore-中创建物品)
  - [步骤 3：在 ItemCoreTrinkets/slot.yml 中添加槽位](#步骤-3在-itemcoretrinketsslotyml-中添加槽位)
  - [步骤 4：在 gui.yml 中添加槽位](#步骤-4在-guiyml-中添加槽位)

---


## 2. 快速开始

### 基础流程

1. 使用 ItemCore 创建物品
2. 在物品的 `type` 字段填写饰品类型（如 `ring`）
3. 打开饰品界面 `/ict`
4. 将物品拖拽到对应槽位

### 权限

| 权限节点 | 默认 | 说明 |
|---|---|---|
| `itemcoretrinkets.gui` | 所有人 | 打开饰品界面 |
| `itemcoretrinkets.admin` | OP | 管理员总权限（包含 reload） |
| `itemcoretrinkets.admin.reload` | OP | 重载配置文件 |

---

## 3. 配置文件详解

插件的数据文件位于 `plugins/ItemCoreTrinkets/` 目录下，共 4 个配置文件。

---

### 3.1 config.yml — 插件设置

```yaml
# 通用设置
general:
  # 自动保存间隔（秒）
  # 插件会定期将玩家装备数据写入磁盘
  # 设置为 0 则禁用自动保存
  auto-save-interval: 300

  # 调试模式
  # 开启后控制台输出详细日志，用于排查问题
  debug-mode: false
```

| 配置项 | 类型 | 默认值 | 说明 |
|---|---|---|---|
| `general.auto-save-interval` | 整数 | 300 | 自动保存间隔（秒），0=禁用 |
| `general.debug-mode` | 布尔 | false | 调试日志开关 |

---

### 3.2 slots.yml — 槽位定义

定义服务器有哪些饰品槽位，以及每个槽位的匹配规则和启用条件。

#### 格式

```yaml
slots:
  <槽位ID>:                       # 唯一标识符，必须唯一
    type: <物品类型>               # 匹配的 ItemCore 物品类型
    require:                       # （可选）启用条件
      permission: "<权限节点>"      # 需要的权限
      level: <等级>                # 需要的原版等级
```

#### 示例

```yaml
slots:
  ring_left:
    type: ring                     # 只有 type=ring 的物品能装备

  ring_right:
    type: ring

  amulet:
    type: amulet
    require:
      permission: "trinkets.amulet"  # 需要 trinkets.amulet 权限

  bracelet:
    type: bracelet
    require:
      level: 10                      # 需要 10 级

  belt:
    type: belt
    require:
      permission: "trinkets.belt"     # 两个条件同时满足
      level: 20
```

---

### 3.3 gui.yml — 界面布局

定义饰品管理界面的外观、布局和每个槽位的显示。

#### settings — 基础设置

```yaml
settings:
  rows: 6                          # 界面行数（1-6）
  title: "&6饰品管理"              # 界面标题，支持 & 颜色代码
```

#### layout — 字符映射布局

每行 9 个字符，用不同字母代表不同功能区域：

```yaml
layout:
  - 'AAAAAAAAA'     
  - 'AAAABAAXA'     
  - 'AAAFCGAAA'     
  - 'AAAADAAAA'     
  - 'AAAAEAAAA'     
  - 'AAAAAAAAA'     
```

每个字符对应下方 `slots` 中的一个定义。未在 `slots` 中定义的字符不会显示任何物品。

#### slots — 槽位具体定义

**边框装饰：**
```yaml
A:
  material: BLACK_STAINED_GLASS_PANE
  display-name: " "
  function: border
```

**原版装备显示槽位（只读）：**
```yaml
B:
  material: AIR
  display-name: "&7头盔"
  function: equipment_slot
  slot-type: helmet
```

| 字段 | 必填 | 说明 |
|---|---|---|
| `material` | 是 | 未装备时显示的材质（英文名） |
| `display-name` | 是 | 显示名称，支持 & 颜色代码 |
| `function` | 是 | 功能类型 |
| `slot-type` | 是 | 原版槽位类型：`helmet`/`chestplate`/`leggings`/`boots`/`offhand` |

**饰品槽位（可交互）：**
```yaml
F:
  material: glass_pane
  display-name: "&6左手戒指"
  custom-model-data: 0
  function: trinket_slot
  trinket-slot-id: ring_left
  lore:
    - "&7类型: 戒指"
    - ""
    - "&e点击放入饰品"
  locked-material: BARRIER
  locked-lore:
    - "&c未解锁"
    - "&7需要满足条件才能使用此槽位"
```

| 字段 | 必填 | 默认值 | 说明 |
|---|---|---|---|
| `material` | 是 | — | 空槽位的显示材质（英文名） |
| `display-name` | 是 | — | 空槽位的显示名称，支持 & 颜色 |
| `custom-model-data` | 否 | 0 | 自定义模型数据（资源包用） |
| `function` | 是 | — | 必须为 `trinket_slot` |
| `trinket-slot-id` | 是 | — | 关联的 slots.yml 槽位 ID |
| `lore` | 否 | 无 | 空槽位的说明文本，支持 & 颜色 |
| `locked-material` | 否 | `RED_STAINED_GLASS_PANE` | 条件未满足时的材质 |
| `locked-lore` | 否 | 自动生成 | 条件未满足时的说明文本 |

**关闭按钮：**
```yaml
→:
  material: ARROW
  display-name: "&a关闭"
  custom-model-data: 0
  function: close
```

#### 所有 function 类型

| 值 | 说明 | 必需字段 |
|---|---|---|
| `border` | 不可交互的装饰边框 | material, display-name |
| `trinket_slot` | 可交互的饰品槽位 | material, display-name, trinket-slot-id |
| `equipment_slot` | 只读的原版装备显示 | material, display-name, slot-type |
| `close` | 关闭界面的按钮 | material, display-name |

#### 关于锁定状态

当玩家不满足 `slots.yml` 中某个槽位的 `require` 条件时：

- **未装备时：** 显示 `locked-material` 指定的材质 + `locked-lore` 指定的说明
- **已装备时：** 属性不计算（相当于该饰品无效），GUI 中也显示锁定状态
- **尝试装备时：** 提示"槽位未解锁，无法使用"

---

### 3.4 messages.yml — 消息文本

```yaml
messages:
  prefix: "&6[Trinkets] "
  no-permission: "&c没有权限执行此命令"
  type-mismatch: "&c物品类型不匹配槽位要求"
  slot-locked: "&c槽位未解锁，无法使用"
  config-reloaded: "&a配置已重载"
```

---

## 4. 教程：配置一个饰品

### 目标

为服务器添加一个**项链**饰品槽，只有 VIP 玩家可以使用，并且佩戴后增加生命值。

### 步骤 1：在 ItemCore 中创建物品类型

使用 `ItemCore/categories.yml` 创建一个新类型（或在物品配置文件中添加）：

```yaml
categories:
  ring:
    name: 戒指
    icon: emerald
    slot: 23
    display-name: "&d戒指"
    lore:
      - "&7其他物品"
      - "&7点击查看所有特殊物品"
    items-file: ring.yml
```

> 物品的 `type` 字段值必须与 `slots.yml` 中的 `type` 一致（大小写不敏感）。

### 步骤 2：在 ItemCore 中创建物品

手动在items文件夹创建一个ring.yml文件，然后在文件内创建一个物品，将active-slots设置为trinkets 

```yaml
戒指:
  material: emerald
  type: ring_left
  display-name: "测试戒指"
  lore:
    - "&7测试戒指"
  unbreakable: true
  attributes:
    HEALTH: 20
  active-slots:
    - trinkets 
  item-flags:
    - HIDE_ENCHANTS
    - HIDE_ATTRIBUTES
    - HIDE_UNBREAKABLE
```
### 步骤 3：在 ItemCoreTrinkets/slot.yml 中添加槽位

type就是在ItemCore中定义的物品的类型。

```yaml
slots:
  ring_left:
    type: ring
    require:
      permission: "player.vip"
```

### 步骤 4：在 gui.yml 中添加槽位

先修改布局，在空位加上一个新字符（比如 `N`）：

```yaml
layout:
  - 'AAAAAAAAA'
  - 'AAAABAAXA'
  - 'AAANCGAAA'
  - 'AAAADAAAA'
  - 'AAAAEAAAA'
  - 'AAAAAAAAA'
```

然后在 `slots` 中添加 N 的定义：

```yaml
  N:
    material: IRON_NUGGET
    display-name: "&b戒指槽位"
    custom-model-data: 0
    function: trinket_slot
    trinket-slot-id: ring_left
    lore:
      - "&7类型: 项链"
      - ""
      - "&e点击放入项链"
    locked-material: BARRIER
    locked-lore:
      - "&c未解锁"
      - "&7需要 VIP 权限"
```

