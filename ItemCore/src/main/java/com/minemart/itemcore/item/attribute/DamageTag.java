package com.minemart.itemcore.item.attribute;

public enum DamageTag {
    PHYSICAL("physical", "物理伤害"),
    SPELL("spell", "法术伤害"),
    PROJECTILE("projectile", "射弹伤害");

    private final String configKey;
    private final String displayName;

    DamageTag(String configKey, String displayName) {
        this.configKey = configKey;
        this.displayName = displayName;
    }

    public String getConfigKey() {
        return configKey;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static DamageTag fromConfigKey(String key) {
        if (key == null) {
            return null;
        }
        String upperKey = key.toUpperCase();
        for (DamageTag tag : values()) {
            if (tag.name().equals(upperKey) || tag.configKey.equalsIgnoreCase(key)) {
                return tag;
            }
        }
        return null;
    }
}
