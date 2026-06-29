# 属性系统

## 攻击类

| 配置键 | 显示名 | 百分比 | 说明 |
|--------|--------|--------|------|
| `ATTACK_DAMAGE` | 攻击伤害 | — | 基础攻击力 |
| `ATTACK_SPEED` | 攻击速度 | — | 最终攻速值 |
| `ATTACK_RANGE` | 攻击范围 | — | 攻击距离加成 |
| `KNOCKBACK` | 击退 | — | 击退加成 |
| `CRIT_CHANCE` | 暴击几率 | % | 暴击概率 |
| `CRIT_DAMAGE` | 暴击伤害 | % | 额外暴击倍率 |

## 法术/伤害类型

| 配置键 | 显示名 | 百分比 | 说明 |
|--------|--------|--------|------|
| `SPELL_POWER` | 法术强度 | — | 法术伤害基础值 |
| `PHYSICAL_DAMAGE` | 物理加成 | % | 物理伤害百分比 |
| `SPELL_DAMAGE` | 法术加成 | % | 法术伤害百分比 |
| `PROJECTILE_DAMAGE` | 射弹加成 | % | 射弹伤害百分比 |
| `ADAPTIVE_FORCE` | 适应之力 | — | 自动转为攻击或法强 |

## 防御类

| 配置键 | 显示名 | 百分比 | 说明 |
|--------|--------|--------|------|
| `PHYSICAL_RESIST` | 物理抗性 | — | 物理减伤 |
| `SPELL_RESIST` | 法术抗性 | — | 法术减伤 |
| `ARMOR` | 护甲 | — | 原版护甲值 |
| `DAMAGE_REDUCTION` | 伤害减免 | % | 最终减伤 |

## 穿透类

| 配置键 | 显示名 | 百分比 | 说明 |
|--------|--------|--------|------|
| `PHYSICAL_PENETRATION` | 物理穿透 | — | 固定值穿透 |
| `PHYSICAL_PENETRATION_PERCENT` | 物理穿透% | % | 百分比穿透 |
| `SPELL_PENETRATION` | 法术穿透 | — | 固定值穿透 |
| `SPELL_PENETRATION_PERCENT` | 法术穿透% | % | 百分比穿透 |

## 生存类

| 配置键 | 显示名 | 百分比 | 说明 |
|--------|--------|--------|------|
| `HEALTH` | 生命值 | — | 额外生命（基础20） |
| `MOVEMENT_SPEED` | 移动速度 | — | 额外移速（基础0.1） |
| `REGENERATION` | 生命恢复 | — | 每秒恢复 |
| `LUCK` | 幸运值 | — | 幸运加成 |

## 适应之力

```yaml
# attributes.yml
adaptive-force:
  attack_conversion: 0.5   # 每1点 → 0.5攻击
  spell_conversion: 1.0    # 每1点 → 1.0法强
```

比较 `ATTACK_DAMAGE` 和 `SPELL_POWER`，数值高者获得加成。

## 暴击机制

`CRIT_DAMAGE` 为额外加成。总暴击伤害 = 默认值（150%）+ `CRIT_DAMAGE`。

## 攻击速度

直接配置最终值：
- `ATTACK_SPEED: 1.6` = 铁剑速度
- `ATTACK_SPEED: 4.0` = 极快

## 防御公式

```yaml
# 默认：百分比减伤
defense_formulas:
  physical_resist: '{damage} * (1 - {armor} / ({armor} + 100))'
  spell_resist: '{damage} * (1 - {armor} / ({armor} + 100))'
```

## 百分比属性

直接填整数：`CRIT_CHANCE: 30` = 30%