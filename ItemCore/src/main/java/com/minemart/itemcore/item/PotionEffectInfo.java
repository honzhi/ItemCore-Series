package com.minemart.itemcore.item;

import org.bukkit.NamespacedKey;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class PotionEffectInfo {

    private final String name;
    private final int level;
    private final int duration;

    private static final Map<String, PotionEffectType> EFFECT_CACHE = new HashMap<>();

    static {
        for (PotionEffectType type : PotionEffectType.values()) {
            String key = type.getKey().getKey().toUpperCase();
            EFFECT_CACHE.put(key, type);
            if (type.getName() != null) {
                EFFECT_CACHE.put(type.getName().toUpperCase(), type);
            }
        }
    }

    public PotionEffectInfo(String name, int level, int duration) {
        this.name = name;
        this.level = level;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getDuration() {
        return duration;
    }

    public PotionEffectType getType() {
        return fromName(name);
    }

    public static PotionEffectType fromName(String name) {
        if (name == null) {
            return null;
        }

        String upper = name.toUpperCase();
        if (EFFECT_CACHE.containsKey(upper)) {
            return EFFECT_CACHE.get(upper);
        }

        PotionEffectType type = PotionEffectType.getByKey(NamespacedKey.minecraft(name.toLowerCase()));
        if (type != null) {
            EFFECT_CACHE.put(upper, type);
            return type;
        }

        return null;
    }

    public PotionEffect toPotionEffect() {
        PotionEffectType type = getType();
        if (type == null) {
            return null;
        }
        return new PotionEffect(type, duration, level - 1);
    }

    public boolean isValid() {
        return getType() != null && level > 0 && duration > 0;
    }
}
