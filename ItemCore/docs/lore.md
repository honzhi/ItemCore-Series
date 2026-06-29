# Lore 展示系统

## 布局配置

```yaml
# tooltip/lore.yml
lore-format:
  - '#item-lore#'
  - '{bar}'
  - '#attack_damage#'
  - '#attack_speed#'
  - '#health#'
  ...
```

## 占位符

| 占位符 | 说明 |
|--------|------|
| `#属性名#` | 显示属性值（为0自动隐藏） |
| `#item-lore#` | 物品描述文本 |
| `{bar}` | 条件分隔线 |
| `{sbar}` | 始终显示的分隔线 |

## 属性显示格式

```yaml
# tooltip/stats.yml
attack_damage: '&f攻击伤害: &6<plus>{value}'
crit_chance: '&f暴击几率: &6<plus>{value}%'
```

变量：`{value}` 数值、`<plus>` 自动 +/- 号

## 配色

- 词条名：`&f`（白色）
- 数值：`&6`（橙色）

## 自动刷新

```yaml
lore-refresh:
  enabled: true
  interval: 100  # tick
```