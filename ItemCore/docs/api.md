# 开发者 API

## 获取 API 实例

```java
import com.minemart.itemcore.api.ItemCoreAPI;

// 获取玩家属性
AttributeContainer attrs = ItemCoreAPI.getPlayerAttributes(player);
double attackDamage = attrs.getAttribute(CustomAttribute.ATTACK_DAMAGE);

// 获取物品
CustomItem item = ItemCoreAPI.getCustomItem("legendary_blade");

// 获取分类
Collection<ItemCategory> categories = ItemCoreAPI.getCategories();

// 处理自定义伤害
DamageRequest request = DamageRequest.builder()
    .attacker(attacker)
    .victim(victim)
    .baseDamage(100)
    .damageType(DamageTag.SPELL)
    .element(ElementType.LIUHUO)
    .canCrit(true)
    .attackType(AttackType.SKILL)
    .build();
ItemCoreAPI.processDamage(request);
```

## 注册自定义元素

```java
ItemCore.getInstance().getElementRegistry()
    .register(new ElementType("ARCANE", "奥术"));
```