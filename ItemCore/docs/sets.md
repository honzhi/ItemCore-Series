# 套装系统

## 物品加入套装

在物品配置中使用 `set` 指定套装 ID：

```yaml
煤炭头盔:
  material: LEATHER_HELMET
  display_name: "煤炭头盔"
  set: coal_set
  active_slots:
    - head
```

## 生效件数

套装件数只统计实际生效的 `active_slots`，每个实际槽位最多计算一件。未配置
`active_slots` 时默认使用 `any`。损坏或无使用权限的物品不参与计数。

例如，两件 `active_slots: [any]` 的物品分别放在主手和副手时计为两件；两件
只允许 `main_hand` 的物品分别放在主手和副手时只计一件。

## 套装配置

套装定义位于 `plugins/ItemCore/sets.yml`：

```yaml
coal_set:
  display_name: "&8煤炭套装"
  activation_mode: cumulative
  lore:
    - '&7Arcane Set Bonus:'
    - '&8[3] +20% Magic Damage'

  bonuses:
    2:
      attributes:
        ARMOR: 5
        HEALTH: 10

    4:
      attributes:
        PHYSICAL_DAMAGE: 15

      skills:
        attack:
          provider: mythicmobs
          skill: CoalFlame
```

`lore` 会显示在所有属于该套装的物品上。其显示位置由
`plugins/ItemCore/tooltip/lore.yml` 中的 `#set_lore#` 占位符决定：

```yaml
lore_format:
  - '#item-lore#'
  - '#set_lore#'
  - '{bar}'
```

## 激活模式

- `cumulative`：累计激活所有已达到的档位，默认模式。
- `highest_only`：只激活当前达到的最高档位。

套装技能支持 `left_click`、`right_click`、`attack` 和 `timer`。TIMER 技能使用
`duration` 配置触发间隔，单位为 tick。
