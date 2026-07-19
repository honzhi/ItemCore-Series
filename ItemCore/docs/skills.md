# 技能系统（ItemCoreMythic）

## 技能配置

```yaml
skills:
  right_click:
    skill: fireball
    provider: mythicmobs
    chance: 25
  left_click:
    skill: slash
    provider: mythicmobs
  block_break:
    skill: coal_reward
    provider: mythicmobs
    chance: 10
    blocks:
      - COAL_ORE
      - DEEPSLATE_COAL_ORE
  timer:
    skill: regen
    provider: mythicmobs
    duration: 20
```

## 触发类型

| 类型 | 说明 |
|------|------|
| `right_click` | 右键触发 |
| `left_click` | 左键触发 |
| `attack` | 使用主手物品攻击实体时触发 |
| `block_break` | 使用主手物品成功破坏方块时触发 |
| `timer` | 定时触发，`duration` 单位为 tick |

## 概率与方块过滤

- `chance` 适用于物品和套装的全部技能，取值为 `0-100`，支持小数，默认 `100`。
- `block_break.blocks` 可填写一个或多个 Bukkit 方块材质；不配置时任意方块均可触发。
- 被其他插件取消的方块破坏事件不会触发技能。
- 挖掘技能的施法者是玩家，技能位置是被破坏方块的中心。

## ICDamageMechanic

```yaml
Skills:
- icdamage{amount="<ic.attack_damage> * 2",type=physical,element=LIUHUO} @EIR{r=5}
```

| 参数 | 别名 | 默认值 | 说明 |
|------|------|--------|------|
| `amount` | `a` | `1` | 伤害公式 |
| `type` | `t` | `physical` | 伤害类型 |
| `element` | `e` | `none` | 元素类型 |
| `crit` | `c` | `true` | 可暴击 |
| `attacktype` | `at` | `skill` | 攻击类型 |
| `penetration` | `p` | `0` | 穿透 |
| `lifesteal` | `ls` | `0` | 吸血 |

## active_slots 影响

- 属性：装备位决定生效
- 技能：只有符合 `active_slots` 的实际生效物品能够触发
- 药水效果：装备位决定给予
