package com.minemart.itemcore.item.attribute;

import java.util.HashMap;
import java.util.Map;

public enum CustomAttribute {

    ATTACK_DAMAGE("attack_damage", "攻击伤害", false),
    ATTACK_SPEED("attack_speed", "攻击速度", false),
    ATTACK_RANGE("attack_range", "攻击范围", false),
    HEALTH("health", "生命值", false),
    MOVEMENT_SPEED("movement_speed", "移动速度", false),
    REGENERATION("regeneration", "生命恢复", false),
    KNOCKBACK("knockback", "击退", false),
    LUCK("luck", "幸运值", false),

    SPELL_DAMAGE("spell_damage", "法术加成", true),
    PHYSICAL_DAMAGE("physical_damage", "物理加成", true),
    PROJECTILE_DAMAGE("projectile_damage", "射弹加成", true),
    SPELL_POWER("spell_power", "法术强度", false),
    ADAPTIVE_FORCE("adaptive_force", "适应之力", false),

    CRIT_CHANCE("crit_chance", "暴击几率", true),
    CRIT_DAMAGE("crit_damage", "暴击伤害", true),

    ARMOR("armor", "护甲", false),
    PHYSICAL_RESIST("physical_resist", "物理抗性", false),
    SPELL_RESIST("spell_resist", "法抗", false),
    PHYSICAL_PENETRATION("physical_penetration", "物理穿透", false),
    PHYSICAL_PENETRATION_PERCENT("physical_penetration_percent", "物理穿透百分比", true),
    SPELL_PENETRATION("spell_penetration", "法术穿透", false),
    SPELL_PENETRATION_PERCENT("spell_penetration_percent", "法术穿透百分比", true),
    DAMAGE_REDUCTION("damage_reduction", "伤害减免", true);

    private final String configKey;
    private final String displayName;
    private final boolean percentage;

    private static final Map<String, CustomAttribute> BY_CONFIG_KEY = new HashMap<>();

    static {
        for (CustomAttribute attr : values()) {
            BY_CONFIG_KEY.put(attr.configKey.toUpperCase(), attr);
            BY_CONFIG_KEY.put(attr.name(), attr);
        }
    }

    CustomAttribute(String configKey, String displayName, boolean percentage) {
        this.configKey = configKey;
        this.displayName = displayName;
        this.percentage = percentage;
    }

    public String getConfigKey() {
        return configKey;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isPercentage() {
        return percentage;
    }

    public static CustomAttribute fromConfigKey(String key) {
        if (key == null) {
            return null;
        }
        return BY_CONFIG_KEY.get(key.toUpperCase());
    }
}
