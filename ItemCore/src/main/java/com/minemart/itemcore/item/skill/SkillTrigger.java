package com.minemart.itemcore.item.skill;

public enum SkillTrigger {
    LEFT_CLICK("Left_Click"),
    RIGHT_CLICK("Right_Click"),
    TIMER("Timer"),
    ATTACK("Attack"),
    HIT("Hit");

    private final String configKey;

    SkillTrigger(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigKey() {
        return configKey;
    }

    public static SkillTrigger fromConfigKey(String key) {
        if (key == null) {
            return null;
        }
        String normalizedKey = key.toLowerCase();
        
        if (normalizedKey.startsWith("on_")) {
            normalizedKey = normalizedKey.substring(3);
        }
        
        normalizedKey = normalizedKey.replace("-", "_");
        
        for (SkillTrigger trigger : values()) {
            if (trigger.configKey.equalsIgnoreCase(key) || 
                trigger.name().equalsIgnoreCase(normalizedKey) ||
                trigger.configKey.replace("_", "").equalsIgnoreCase(normalizedKey.replace("_", ""))) {
                return trigger;
            }
        }
        return null;
    }
}
