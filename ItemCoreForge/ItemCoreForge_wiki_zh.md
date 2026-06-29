# ItemCoreForge Wiki

***

## 概述

ItemCoreForge 是一个高级锻造系统插件，为 Minecraft 服务器提供自定义锻造台功能。

**核心特性**：

- 支持多个独立配置的锻造台
- 多队列并行制作
- 丰富的触发系统（命令、音效、消息）
- 可自定义的 GUI 界面
- 支持 ItemCore 和 MythicMobs 物品

**依赖要求**：

- **可选**：ItemCore v1.0.0+
- **可选**：MythicMobs v5.0+

***

## 安装

1. 将 `ItemCoreForge-1.0.0.jar` 放入服务器的 `plugins/` 目录
2. 启动服务器，插件会自动生成配置文件
3. 根据需要配置锻造台

***

## 命令

| 命令                    | 描述          | 权限                     |
| --------------------- | ----------- | ---------------------- |
| `/icf`                | 显示帮助信息      | 无                      |
| `/icf gui <forge-id>` | 打开指定锻造台 GUI | `itemcoreforge.gui`    |
| `/icf list`           | 列出所有锻造台     | `itemcoreforge.list`   |
| `/icf reload`         | 重载配置文件      | `itemcoreforge.reload` |

***

## 权限

| 权限节点                   | 描述        | 默认  |
| ---------------------- | --------- | --- |
| `itemcoreforge.*`      | 所有权限      | OP  |
| `itemcoreforge.gui`    | 打开锻造台 GUI | 所有人 |
| `itemcoreforge.list`   | 列出锻造台     | OP  |
| `itemcoreforge.reload` | 重载配置      | OP  |
| `itemcoreforge.admin`  | 管理员权限     | OP  |

***

## 配置文件

### forge.yml

定义锻造台及其配方。

```yaml
forge-id: "example_template"
# 显示名称（支持颜色代码）
# 使用 & 符号进行颜色代码
display-name: "&6示例锻造台"
# 锻造台设置
settings:
  # 最大队列大小（允许同时排队的物品数量）
  # 建议值：3-10
  max-queue-size: 6
  # 锻造台类型
  # custom: 使用自定义布局文件（需要 GUI 界面）
  # crafting_table: 原版工作台配方（直接在原版工作台中使用）
  # furnace: 熔炉类型
  type: custom
  # 使用的布局文件（仅 type 为 custom 时生效）
  # 布局文件位于 layouts/ 目录下
  layout-file: "Default.yml"
# 配方列表
# 可以定义多个配方
recipes:
  # ====================
  # 示例1：原版物品配方
  # ====================
  iron_sword:
    # 产出物品
    output:
      # 物品来源
      # vanilla: Minecraft原版物品
      # itemcore: ItemCore插件自定义物品
      # mythicmobs: MythicMobs插件自定义物品
      source: "vanilla"
      # 物品ID
      # 原版物品使用物品名称（如 IRON_SWORD）
      # ItemCore物品使用物品ID
      id: "IRON_SWORD"
      # 产出数量
      amount: 1
      # 是否检查耐久度（仅对可修复物品有效）
      check-durability: false
    # 所需材料（列表形式）
    materials:
      - source: "vanilla"
        id: "IRON_INGOT"
        amount: 2
        check-durability: false
      - source: "vanilla"
        id: "STICK"
        amount: 1
        check-durability: false
    # 制作条件（可选）
    # 可以定义多个条件，所有条件都满足才能制作
    conditions:
      # 等级条件
      - type: "level"
        value: 10              # 玩家需要达到10级
      # 权限条件（可选）
      # - type: "permission"
      #   value: "myplugin.forge.use"
      # 经济条件（可选，需要Vault插件）
      # - type: "money"
      #   value: 500            # 需要500金币
    # 制作时间（秒）
    # 设置为 0 或负数表示即时完成（不加入队列）
    craft-time: 5.0
    # 成功率（百分比），100表示必定成功，50表示50%成功率
    # 当成功率小于100时，制作时有概率失败
    success-rate: 100 
    # 制作失败时是否消耗材料（仅当成功率<100时生效）
    # true: 失败时材料被消耗
    # false: 失败时材料返还给玩家
    consume-on-fail: true
    # 触发器（可选）
    # 在特定时机执行的动作
    triggers:
      # 开始制作时触发
      on-start:
        messages:
          - "&6开始锻造铁剑..."
        sounds:
          - "BLOCK_ANVIL_USE 1 1"   # 格式: 声音名 音量 音调
        commands:
          - "say {player} 开始锻造铁剑"   # 控制台执行
        # player-commands:            # 玩家执行
        #   - "heal"
      # 领取物品时触发  
      on-claim:
        messages:
          - "&a成功锻造铁剑！"
        sounds:
          - "ENTITY_PLAYER_LEVELUP 1 1"
      # 取消制作时触发
      on-cancel:
        messages:
          - "&c已取消铁剑锻造"
        sounds:
          - "ENTITY_VILLAGER_NO 1 1"
```

### layout.yml

定义 GUI 布局。

```yaml
# 布局设置
settings:
  # GUI界面行数（1-6）
  rows: 6

# 布局定义
# 使用字符来代表不同的功能位置
# 每行必须是9个字符（GUI宽度固定为9格）
layout:
  - 'AAAAAAAAA'  
  - 'AAA000AAA'  
  - 'AZA000AEA'  
  - 'AAA000AAA'  
  - 'AAAAAAAAA'  
  - '←AXXXXXAA'  

# 字符定义
# 每个字符代表一个功能位置
slots:
  # A - 边框位置
  A:
    material: BLACK_STAINED_GLASS_PANE  # 边框物品
    display-name: ""                      # 显示名称（空表示不显示）
    function: border                      # 功能类型：border（边框）

  # 0 - 材料槽位置
  0:
    material: AIR                         # 显示为空气（实际显示玩家物品）
    function: material_slot               # 功能类型：material_slot（材料槽）
    show-item: true                       # 是否显示物品

  # E - 产出槽位置
  E:
    material: AIR                         # 显示为空气（实际显示产出物品）
    function: output_slot                 # 功能类型：output_slot（产出槽）

  # X - 队列显示位置
  X:
    material: GREEN_STAINED_GLASS_PANE    # 队列背景物品
    display-name: "&a制作队列"            # 显示名称
    function: queue_display               # 功能类型：queue_display（队列显示）

  # Z - 确认按钮位置
  Z:
    material: LIME_WOOL                  # 按钮物品
    display-name: "&a开始制作"            # 显示名称
    function: confirm_button              # 功能类型：confirm_button（确认按钮）


  ←:
    material: ARROW
    display-name: "&a返回"            
    function: back_button   
    lore:                                # 描述信息
      - "&7返回配方列表"        

# ================================================
# 功能类型说明
# ================================================
# border:           边框装饰，无交互功能
# material_slot:    材料显示槽，显示所需材料
# output_slot:      产出显示槽，显示制作结果
# queue_display:    队列显示区域，显示正在制作和等待的物品
# confirm_button:   确认按钮，点击开始制作
# back_button:      返回配方列表
# ================================================
```

### config.yml

插件主配置。

```yaml

settings:
  # 锻造台配置文件所在目录（相对于插件目录）
  forges-directory: "forges"
  # GUI布局文件所在目录（相对于插件目录）
  layouts-directory: "layouts"
  # 调试模式
  debug: true
# GUI界面设置
gui:
  # 配方选择界面每页显示的配方数量
  recipes-per-page: 28
  # 界面边框物品
  border-item: GRAY_STAINED_GLASS_PANE
  # 上一页按钮物品
  previous-item: ARROW
  # 下一页按钮物品
  next-item: ARROW
  # 返回按钮物品
  back-item: BARRIER
  # 制作按钮物品（材料充足时）
  craft-item: GREEN_WOOL
  # 制作按钮显示名称
  craft-item-name: "&a开始制作"
  # 制作按钮描述
  craft-item-lore:
    - "&7点击开始制作"
    - "&7制作时间: {craft_time}秒"
  # 制作按钮物品（材料不足时）
  craft-disabled-item: RED_WOOL
  # 材料不足时按钮显示名称
  craft-disabled-name: "&c材料不足"
  # 材料不足时按钮描述
  craft-disabled-lore:
    - "&7材料不足，无法制作"
    - "&7请先收集材料"
  # 点击冷却时间（毫秒），防止快速点击
  craft-click-cooldown: 150
```

<br />

