package com.minemart.itemcore.util;

import com.minemart.itemcore.calculator.AttributeCalculator;
import com.minemart.itemcore.item.attribute.AttributeContainer;
import com.minemart.itemcore.item.attribute.CustomAttribute;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AttributePlaceholderResolver {
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("<ic\\.([^>]+)>");
    private static final Map<String, CustomAttribute> ATTRIBUTE_MAP = new HashMap<>();
    
    static {
        ATTRIBUTE_MAP.put("attack_damage", CustomAttribute.ATTACK_DAMAGE);
        ATTRIBUTE_MAP.put("spell_damage", CustomAttribute.SPELL_DAMAGE);
        ATTRIBUTE_MAP.put("physical_damage", CustomAttribute.PHYSICAL_DAMAGE);
        ATTRIBUTE_MAP.put("projectile_damage", CustomAttribute.PROJECTILE_DAMAGE);
        ATTRIBUTE_MAP.put("crit_chance", CustomAttribute.CRIT_CHANCE);
        ATTRIBUTE_MAP.put("crit_damage", CustomAttribute.CRIT_DAMAGE);
        ATTRIBUTE_MAP.put("attack_speed", CustomAttribute.ATTACK_SPEED);
        ATTRIBUTE_MAP.put("attack_range", CustomAttribute.ATTACK_RANGE);
        ATTRIBUTE_MAP.put("regeneration", CustomAttribute.REGENERATION);
        ATTRIBUTE_MAP.put("adaptive_force", CustomAttribute.ADAPTIVE_FORCE);
        ATTRIBUTE_MAP.put("spell_power", CustomAttribute.SPELL_POWER);
        ATTRIBUTE_MAP.put("health", CustomAttribute.HEALTH);
        ATTRIBUTE_MAP.put("armor", CustomAttribute.ARMOR);
        ATTRIBUTE_MAP.put("physical_resist", CustomAttribute.PHYSICAL_RESIST);
        ATTRIBUTE_MAP.put("spell_resist", CustomAttribute.SPELL_RESIST);
        ATTRIBUTE_MAP.put("movement_speed", CustomAttribute.MOVEMENT_SPEED);
        ATTRIBUTE_MAP.put("luck", CustomAttribute.LUCK);
        ATTRIBUTE_MAP.put("knockback", CustomAttribute.KNOCKBACK);
        ATTRIBUTE_MAP.put("damage_reduction", CustomAttribute.DAMAGE_REDUCTION);
        ATTRIBUTE_MAP.put("physical_penetration", CustomAttribute.PHYSICAL_PENETRATION);
        ATTRIBUTE_MAP.put("physical_penetration_percent", CustomAttribute.PHYSICAL_PENETRATION_PERCENT);
        ATTRIBUTE_MAP.put("spell_penetration", CustomAttribute.SPELL_PENETRATION);
        ATTRIBUTE_MAP.put("spell_penetration_percent", CustomAttribute.SPELL_PENETRATION_PERCENT);
    }
    
    public static String resolve(String template, Player player) {
        if (template == null || template.isEmpty() || player == null) {
            return template;
        }
        
        AttributeContainer attrs = AttributeCalculator.calculatePlayerAttributes(player);
        
        StringBuffer result = new StringBuffer();
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);
        
        while (matcher.find()) {
            String attrName = matcher.group(1).toLowerCase();

            CustomAttribute attr = ATTRIBUTE_MAP.get(attrName);
            double value = 0;

            if (attr != null) {
                if (attr == CustomAttribute.CRIT_DAMAGE) {
                    value = AttributeCalculator.getTotalCritDamage(attrs);
                } else if (attr == CustomAttribute.HEALTH) {
                    value = AttributeCalculator.calculateHealth(attrs);
                } else if (attr == CustomAttribute.MOVEMENT_SPEED) {
                    value = AttributeCalculator.calculateMovementSpeed(attrs);
                } else {
                    value = attrs.getAttribute(attr);
                }
            }

            if (attr != null && attr.isPercentage()) {
                matcher.appendReplacement(result, formatPercent(value));
            } else {
                matcher.appendReplacement(result, String.valueOf((int) value));
            }
        }
        
        matcher.appendTail(result);
        return result.toString();
    }
    
    public static boolean hasPlaceholders(String template) {
        if (template == null || template.isEmpty()) {
            return false;
        }
        return PLACEHOLDER_PATTERN.matcher(template).find();
    }
    
    public static String getSupportedAttributes() {
        StringBuilder sb = new StringBuilder();
        for (String attr : ATTRIBUTE_MAP.keySet()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append("<ic.").append(attr).append(">");
        }
        return sb.toString();
    }

    private static String formatPercent(double value) {
        if (value == Math.floor(value) && !Double.isInfinite(value)) {
            return (int) value + "%";
        }
        double rounded = Math.round(value * 100.0) / 100.0;
        return rounded + "%";
    }
}
