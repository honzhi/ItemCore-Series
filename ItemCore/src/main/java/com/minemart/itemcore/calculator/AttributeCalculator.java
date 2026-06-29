package com.minemart.itemcore.calculator;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.config.AttributesConfig;
import com.minemart.itemcore.item.CustomItem;
import com.minemart.itemcore.item.attribute.AttributeContainer;
import com.minemart.itemcore.item.attribute.CustomAttribute;
import com.minemart.itemcore.item.attribute.DamageTag;
import com.minemart.itemcore.item.attribute.ElementType;
import com.minemart.itemcore.damage.DamageManager;
import com.minemart.itemcore.util.FormulaEvaluator;
import com.minemart.itemcore.api.AttributeProvider;
import com.minemart.itemcore.api.ItemCoreAPI;
import com.minemart.itemcore.utils.ItemIdentifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AttributeCalculator {

    private static final Logger LOGGER = Logger.getLogger(AttributeCalculator.class.getName());

    private AttributeCalculator() {}

    public static AttributeContainer calculatePlayerAttributes(Player player) {
        AttributeContainer result = new AttributeContainer();

        java.util.Map<com.minemart.itemcore.item.ItemSlot, java.util.List<CustomItem>> equipped = ItemIdentifier.getEquippedItems(player);
        for (java.util.Map.Entry<com.minemart.itemcore.item.ItemSlot, java.util.List<CustomItem>> entry : equipped.entrySet()) {
            com.minemart.itemcore.item.ItemSlot slot = entry.getKey();
            for (CustomItem ci : entry.getValue()) {
                // 合并元素精通/抗性
                for (Map.Entry<ElementType, Double> e : ci.getAttributes().getElementMastery().entrySet()) {
                    result.setElementMastery(e.getKey(), result.getElementMastery(e.getKey()) + e.getValue());
                }
                for (Map.Entry<ElementType, Double> e : ci.getAttributes().getElementResistance().entrySet()) {
                    result.setElementResistance(e.getKey(), result.getElementResistance(e.getKey()) + e.getValue());
                }
                // 数值属性优先 PDC，再 config，再范围中值
                for (CustomAttribute attr : CustomAttribute.values()) {
                    double val = 0;
                    // 优先 PDC
                    org.bukkit.inventory.ItemStack actualItem = ItemIdentifier.getItemInSlot(player, slot);
                    if (actualItem != null && actualItem.hasItemMeta()) {
                        org.bukkit.inventory.meta.ItemMeta itemMeta = actualItem.getItemMeta();
                        if (itemMeta != null) {
                            org.bukkit.NamespacedKey key = com.minemart.itemcore.utils.ItemBuilder.getAttributeKey(attr);
                            if (itemMeta.getPersistentDataContainer().has(key, org.bukkit.persistence.PersistentDataType.DOUBLE)) {
                                val = itemMeta.getPersistentDataContainer().get(key, org.bukkit.persistence.PersistentDataType.DOUBLE);
                            }
                        }
                    }
                    // PDC 没有则用 config 值
                    if (val == 0) {
                        val = ci.getAttributes().getAttribute(attr);
                    }
                    // config 也没有且是范围属性，用范围中值
                    if (val == 0 && ci.getAttributes().hasAttributeRange(attr)) {
                        double[] range = ci.getAttributes().getAttributeRange(attr);
                        val = (range[0] + range[1]) / 2.0;
                    }
                    if (val != 0) {
                        result.addAttribute(attr, val);
                    }
                }
                org.bukkit.inventory.ItemStack actualItem = ItemIdentifier.getItemInSlot(player, slot);
                if (actualItem != null && actualItem.hasItemMeta()) {
                    org.bukkit.inventory.meta.ItemMeta meta = actualItem.getItemMeta();
                    if (meta != null) {
                        for (CustomAttribute attr : CustomAttribute.values()) {
                            org.bukkit.NamespacedKey key = com.minemart.itemcore.utils.ItemBuilder.getAttributeKey(attr);
                            if (meta.getPersistentDataContainer().has(key, org.bukkit.persistence.PersistentDataType.DOUBLE)) {
                                double pdcVal = meta.getPersistentDataContainer().get(key, org.bukkit.persistence.PersistentDataType.DOUBLE);
                                if (pdcVal != 0) {
                                    result.setAttribute(attr, pdcVal);
                                }
                            }
                        }
                    }
                }
            }
        }


        // 合并注册属性提供者的贡献（如饰品插件）
        for (AttributeProvider provider : ItemCoreAPI.getAttributeProviders()) {
            AttributeContainer contributed = provider.getAttributes(player);
            if (contributed != null) {
                result.merge(contributed);
            }
        }

        applyAdaptiveForce(result);

        // 鎼存梻鏁ゅ鍌氱埗閺佸牊鐏夋稉顓犳畱鐏炵偞鈧傛叏閺€?(ATTRIBUTE_MOD)
        DamageManager.applyAilmentAttributeMods(player, result);

        return result;
    }

    public static AttributeContainer calculateFromItems(List<CustomItem> items) {
        AttributeContainer result = new AttributeContainer();
        for (CustomItem item : items) {
            result.merge(item.getAttributes());
        }
        applyAdaptiveForce(result);
        return result;
    }

    private static void applyAdaptiveForce(AttributeContainer attrs) {
        double adaptiveForce = attrs.getAttribute(CustomAttribute.ADAPTIVE_FORCE);
        if (adaptiveForce <= 0) {
            return;
        }

        double attackDamage = attrs.getAttribute(CustomAttribute.ATTACK_DAMAGE);
        double spellPower = attrs.getAttribute(CustomAttribute.SPELL_POWER);

        double attackConversion = 1.0;
        double spellConversion = 1.0;

        try {
            ItemCore plugin = ItemCore.getInstance();
            if (plugin != null && plugin.getAttributesConfig() != null) {
                AttributesConfig config = plugin.getAttributesConfig();
                attackConversion = config.getAdaptiveForceAttackConversion();
                spellConversion = config.getAdaptiveForceSpellConversion();
                if (attackConversion < 0 || spellConversion < 0) {
                    LOGGER.log(Level.WARNING,
                        "Invalid adaptive force conversion config (attack={0}, spell={1}), values clamped to 0",
                        new Object[]{attackConversion, spellConversion});
                    attackConversion = Math.max(0, attackConversion);
                    spellConversion = Math.max(0, spellConversion);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING,
                "Failed to load adaptive force conversion config, using defaults (attack=1.0, spell=1.0): {0}",
                e.getMessage());
        }

        double bonusAttack = adaptiveForce * attackConversion;
        double bonusSpell = adaptiveForce * spellConversion;

        if (attackDamage >= spellPower) {
            attrs.setAttribute(CustomAttribute.ATTACK_DAMAGE, attackDamage + bonusAttack);
        } else {
            attrs.setAttribute(CustomAttribute.SPELL_POWER, spellPower + bonusSpell);
        }
    }

    public static double calculateDamage(AttributeContainer attackerAttrs, AttributeContainer defenderAttrs, double baseDamage, boolean isCrit, double critDamage, Set<DamageTag> damageTags) {
        double damage = baseDamage;

        double attackDamage = attackerAttrs.getAttribute(CustomAttribute.ATTACK_DAMAGE);
        double physicalDamage = attackerAttrs.getAttribute(CustomAttribute.PHYSICAL_DAMAGE);
        double projectileDamage = attackerAttrs.getAttribute(CustomAttribute.PROJECTILE_DAMAGE);
        double spellDamage = attackerAttrs.getAttribute(CustomAttribute.SPELL_DAMAGE);

        damage += attackDamage;

        if (damageTags.contains(DamageTag.PHYSICAL)) {
            damage *= (1 + physicalDamage / 100.0);
        }

        if (damageTags.contains(DamageTag.PROJECTILE)) {
            damage *= (1 + projectileDamage / 100.0);
        }

        if (damageTags.contains(DamageTag.SPELL)) {
            damage *= (1 + spellDamage / 100.0);
        }

        if (isCrit && critDamage > 0) {
            double critMultiplier = critDamage / 100.0;
            damage *= critMultiplier;
        }

        double physicalResist = defenderAttrs.getAttribute(CustomAttribute.PHYSICAL_RESIST);
        double physicalPenetration = attackerAttrs.getAttribute(CustomAttribute.PHYSICAL_PENETRATION);
        double physicalPenetrationPercent = attackerAttrs.getAttribute(CustomAttribute.PHYSICAL_PENETRATION_PERCENT);
        double effectiveResist = calculateEffectiveDefense(physicalResist, physicalPenetration, physicalPenetrationPercent, false);
        if (effectiveResist > 0) {
            damage = applyDefenseReduction(damage, effectiveResist, false);
        }

        // DAMAGE_REDUCTION handled by CombatListener.onEntityDamage
        // damage *= (1 - damageReduction / 100.0);

        return Math.max(0, damage);
    }

    private static double calculateEffectiveDefense(double defense, double flatPenetration, double percentPenetration, boolean isSpell) {
        AttributesConfig.PenetrationOrder order = AttributesConfig.PenetrationOrder.PERCENT_FIRST;

        try {
            ItemCore plugin = ItemCore.getInstance();
            if (plugin != null && plugin.getAttributesConfig() != null) {
                AttributesConfig.PenetrationOrder configOrder = plugin.getAttributesConfig().getPenetrationOrder();
                if (configOrder != null) {
                    order = configOrder;
                } else {
                    LOGGER.log(Level.WARNING,
                        "Penetration order config is null, using default PERCENT_FIRST");
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING,
                "Failed to load penetration order config, using default PERCENT_FIRST: {0}",
                e.getMessage());
        }

        double effectiveDefense = defense;

        if (order == AttributesConfig.PenetrationOrder.PERCENT_FIRST) {
            effectiveDefense = defense * (1 - percentPenetration / 100.0);
            effectiveDefense = effectiveDefense - flatPenetration;
        } else {
            effectiveDefense = defense - flatPenetration;
            effectiveDefense = effectiveDefense * (1 - percentPenetration / 100.0);
        }

        return Math.max(0, effectiveDefense);
    }

    public static double applyDefenseReduction(double damage, double defenseValue, boolean isSpell) {
        try {
            ItemCore plugin = ItemCore.getInstance();
            if (plugin != null && plugin.getAttributesConfig() != null) {
                AttributesConfig config = plugin.getAttributesConfig();
                String formula = isSpell ? config.getSpellResistFormula() : config.getPhysicalResistFormula();
                if (formula == null || formula.isEmpty()) {
                    LOGGER.log(Level.WARNING,
                        "{0} formula config is null or empty, using default calculation",
                        isSpell ? "Spell resist" : "Armor");
                } else {
                    Map<String, Double> variables = new HashMap<>();
                    variables.put("damage", damage);
                    variables.put("armor", defenseValue);
                    return FormulaEvaluator.evaluate(formula, variables);
                }
            }
        } catch (NullPointerException e) {
            LOGGER.log(Level.SEVERE,
                "Null pointer when accessing {0} formula config, using default calculation: {1}",
                new Object[]{isSpell ? "spell resist" : "armor", e.getMessage()});
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING,
                "Invalid formula syntax for {0}, using default calculation: {1}",
                new Object[]{isSpell ? "spell resist" : "armor", e.getMessage()});
        } catch (Exception e) {
            LOGGER.log(Level.WARNING,
                "Failed to evaluate {0} formula, using default calculation: {1}",
                new Object[]{isSpell ? "spell resist" : "armor", e.getMessage()});
        }

        double reduction = defenseValue / (defenseValue + 100);
        return damage * (1 - reduction);
    }

    public static boolean rollCrit(AttributeContainer attackerAttrs) {
        double critChance = attackerAttrs.getAttribute(CustomAttribute.CRIT_CHANCE);
        if (critChance <= 0) {
            return false;
        }
        double roll = Math.random();
        return roll < critChance / 100.0;
    }

    public static double calculateHealth(AttributeContainer attrs) {
        return 20.0 + attrs.getAttribute(CustomAttribute.HEALTH);
    }

    public static double calculateMovementSpeed(AttributeContainer attrs) {
        return 0.1 + attrs.getAttribute(CustomAttribute.MOVEMENT_SPEED);
    }

    public static double calculateKnockbackMultiplier(AttributeContainer attackerAttrs) {
        double knockback = attackerAttrs.getAttribute(CustomAttribute.KNOCKBACK);
        return 1.0 + knockback;
    }

    public static double calculateLuck(AttributeContainer attrs) {
        return Math.max(0, attrs.getAttribute(CustomAttribute.LUCK));
    }

    public static double calculateRegeneration(AttributeContainer attrs) {
        return Math.max(0, attrs.getAttribute(CustomAttribute.REGENERATION));
    }

    public static double calculateAttackRange(AttributeContainer attrs) {
        return attrs.getAttribute(CustomAttribute.ATTACK_RANGE);
    }

    public static double calculateAttackSpeed(AttributeContainer attrs) {
        return 4.0 + attrs.getAttribute(CustomAttribute.ATTACK_SPEED);
    }

    private static double getDefaultCritDamage() {
        try {
            ItemCore plugin = ItemCore.getInstance();
            if (plugin != null && plugin.getAttributesConfig() != null) {
                double configured = plugin.getAttributesConfig().getDefaultCritDamage();
                if (configured > 0) {
                    return configured;
                }
                LOGGER.log(Level.WARNING,
                    "Default crit damage config is invalid ({0}), using default 150.0",
                    configured);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING,
                "Failed to load default crit damage config, using default 150.0: {0}",
                e.getMessage());
        }
        return 150.0;
    }

    public static double getTotalCritDamage(AttributeContainer attrs) {
        return getDefaultCritDamage() + attrs.getAttribute(CustomAttribute.CRIT_DAMAGE);
    }

    public static class DamageResult {
        private double physicalDamage;
        private final boolean isCrit;
        private final double critDamage;
        private final Set<DamageTag> damageTags;
        private final ElementType element;

        public DamageResult(double physicalDamage, boolean isCrit, double critDamage, Set<DamageTag> damageTags) {
            this(physicalDamage, isCrit, critDamage, damageTags, ElementType.NONE);
        }

        public DamageResult(double physicalDamage, boolean isCrit, double critDamage, Set<DamageTag> damageTags, ElementType element) {
            this.physicalDamage = physicalDamage;
            this.isCrit = isCrit;
            this.critDamage = critDamage;
            this.damageTags = damageTags != null ? damageTags : new HashSet<>();
            this.element = element != null ? element : ElementType.NONE;
        }

        public double getPhysicalDamage() {
            return physicalDamage;
        }

        public double getTotalDamage() {
            return physicalDamage;
        }

        public void setDamage(double damage) {
            this.physicalDamage = damage;
        }

        public boolean isCrit() {
            return isCrit;
        }

        public double getCritDamage() {
            return critDamage;
        }

        public Set<DamageTag> getDamageTags() {
            return damageTags;
        }

        public ElementType getElement() {
            return element;
        }

        public boolean hasDamageTag(DamageTag tag) {
            return damageTags.contains(tag);
        }

        public boolean isSpellDamage() {
            return hasDamageTag(DamageTag.SPELL);
        }

        public boolean isProjectileDamage() {
            return hasDamageTag(DamageTag.PROJECTILE);
        }

        public boolean isPhysicalDamage() {
            return hasDamageTag(DamageTag.PHYSICAL) || (!isSpellDamage() && !isProjectileDamage());
        }
    }

    public static DamageResult calculateFullDamage(
        AttributeContainer attackerAttrs,
        AttributeContainer defenderAttrs,
        double baseDamage,
        boolean forceCrit,
        Set<DamageTag> damageTags
    ) {
        boolean canCrit = damageTags != null;
        
        boolean isCrit = false;
        double critDamage = 0;
        
        if (canCrit && (forceCrit || rollCrit(attackerAttrs))) {
            isCrit = true;
            critDamage = getTotalCritDamage(attackerAttrs);
        }

        double physicalDamage = calculateDamage(attackerAttrs, defenderAttrs, baseDamage, isCrit, critDamage, damageTags);

        return new DamageResult(physicalDamage, isCrit, critDamage, damageTags);
    }
}