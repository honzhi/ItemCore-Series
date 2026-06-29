# 元素系统

## 默认元素

| 元素 | 异常效果 |
|------|---------|
| 流火 | 灼烧：8秒 DOT，每秒2%当前生命 |
| 寒霜 | 虚弱：4秒，物抗/法抗 -30% |
| 雷蛰 | 易伤：6秒，元素抗性 -25% |

## 元素配置

```yaml
# elements.yml
LIUHUO:
  display: "&c流火"
  icon: '🔟'
  color: '&c'
  threshold: 30
  decay-per-second: 1
  accumulation:
    mode: DAMAGE_PERCENT
    value: 0.5
  ailment: LIUHUO_DOT
```

## 积累模式

- `DAMAGE_PERCENT`：积累 = 伤害 × `value`
- `FIXED`：每次固定积累
- `ATTRIBUTE`：基于攻击者属性

## 异常配置

```yaml
# ailments.yml
LIUHUO_DOT:
  duration: 160
  refresh-policy: RESET
  triggers:
    - type: DAMAGE_PERCENT
      value: 0.02
      interval: 20

HANSHUANG_WEAKEN:
  duration: 80
  triggers:
    - type: ATTRIBUTE_MOD
      attribute: PHYSICAL_RESIST
      value: -0.3
    - type: ATTRIBUTE_MOD
      attribute: SPELL_RESIST
      value: -0.3

LEIZHE_BREAK:
  duration: 120
  triggers:
    - type: RESISTANCE_REDUCTION
      value: -0.25
```

## 拓展自定义元素

```java
ItemCore.getInstance().getElementRegistry()
    .register(new ElementType("ARCANE", "奥术"));
```