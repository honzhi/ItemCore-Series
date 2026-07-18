# 技能系统（ItemCoreMythic）

## 技能配置

```yaml
skills:
  Right_Click:
    skill: fireball
    provider: mythicmobs
  Left_Click:
    skill: slash
    provider: mythicmobs
  Timer:
    skill: regen
    provider: mythicmobs
    duration: 20
```

## 触发类型

| 类型 | 说明 |
|------|------|
| `Right_Click` | 右键触发 |
| `Left_Click` | 左键触发 |
| `Timer` | 定时器 |

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
- 技能：主/副手决定触发
- 药水效果：装备位决定给予
