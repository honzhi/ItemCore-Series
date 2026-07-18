# 物品创建

## 基本格式

在 `items/` 目录下的 `.yml` 文件中定义：

```yaml
legendary_blade:
  material: NETHERITE_SWORD
  type: weapons
  display_name: "&4&l传说之刃"
  lore:
    - "&7传说中的神秘武器"
  attributes:
    ATTACK_DAMAGE: 15
    CRIT_CHANCE: 25
  active_slots:
    - main_hand
```

## 完整配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `material` | 材质名 | **必填** | 物品材质 |
| `type` | 字符串 | `misc` | 分类 ID |
| `display_name` | 字符串 | — | 显示名称（支持 `&` 颜色码） |
| `color` | RGB | — | 皮革护甲颜色，例如 `255, 0, 0` |
| `lore` | 列表 | — | 物品描述 |
| `attributes` | Map | — | 属性配置 |
| `enchantments` | Map | — | 附魔：`sharpness: 5` |
| `item_flags` | 列表 | — | ItemFlag：`HIDE_ATTRIBUTES` |
| `unbreakable` | 布尔 | false | 不可破坏 |
| `max_stack` | 整数 | 64 | 最大堆叠 |
| `custom_model_data` | 整数 | — | 自定义模型数据 |
| `active_slots` | 列表 | `any` | 生效装备位；配置后仅按列表中的槽位生效 |
| `skills` | 列表 | — | 技能配置 |
| `effects` | 列表 | — | 药水效果 |
| `keep_on_death` | 布尔 | false | 死亡保留 |

## 皮革护甲染色

`color` 使用红、绿、蓝三个 `0-255` 的整数，仅对支持染色的皮革装备生效：

```yaml
煤炭头盔:
  material: LEATHER_HELMET
  display_name: "煤炭头盔"
  color: 255, 0, 0
```

## 完整示例

```yaml
fire_sword:
  material: DIAMOND_SWORD
  type: weapons
  display_name: "&c烈焰之剑"
  lore:
    - "&7燃烧一切的烈焰之剑"
  enchantments:
    sharpness: 3
    fire_aspect: 2
  item_flags:
    - HIDE_ATTRIBUTES
  unbreakable: true
  active_slots:
    - main_hand
  attributes:
    ATTACK_DAMAGE: 12
    ATTACK_SPEED: 1.6
    CRIT_CHANCE: 15
    CRIT_DAMAGE: 30
    PHYSICAL_PENETRATION: 5
  skills:
    Right_Click:
      skill: fireball
      provider: mythicmobs
```
