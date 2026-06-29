package com.minemart.itemcore.item;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentInfo {

    private final String name;
    private final int level;

    private static final Map<String, Enchantment> ENCHANTMENT_CACHE = new HashMap<>();

    static {
        for (Enchantment ench : Enchantment.values()) {
            String key = ench.getKey().getKey().toUpperCase();
            ENCHANTMENT_CACHE.put(key, ench);
            if (ench.getName() != null) {
                ENCHANTMENT_CACHE.put(ench.getName().toUpperCase(), ench);
            }
        }
    }

    public EnchantmentInfo(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public Enchantment getEnchantment() {
        return fromName(name);
    }

    public static Enchantment fromName(String name) {
        if (name == null) {
            return null;
        }

        String upper = name.toUpperCase();
        if (ENCHANTMENT_CACHE.containsKey(upper)) {
            return ENCHANTMENT_CACHE.get(upper);
        }

        Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(name.toLowerCase()));
        if (enchantment != null) {
            ENCHANTMENT_CACHE.put(upper, enchantment);
            return enchantment;
        }

        return null;
    }

    public boolean isValid() {
        return getEnchantment() != null && level > 0;
    }
}
