package com.minemart.itemcore.loader;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class YamlParserUtil {

    private static final Map<String, Material> MATERIAL_CACHE = new HashMap<>();
    private static final Map<String, ItemFlag> ITEM_FLAG_CACHE = new HashMap<>();

    static {
        for (Material material : Material.values()) {
            MATERIAL_CACHE.put(material.name().toUpperCase(), material);
        }
        for (ItemFlag flag : ItemFlag.values()) {
            ITEM_FLAG_CACHE.put(flag.name().toUpperCase(), flag);
        }
    }

    private YamlParserUtil() {}

    public static double parsePercentage(Object value) {
        if (value == null) {
            return 0.0;
        }

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        if (value instanceof String) {
            String str = ((String) value).trim();

            if (str.endsWith("%")) {
                String numPart = str.substring(0, str.length() - 1).trim();
                try {
                    return Double.parseDouble(numPart);
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            }

            try {
                return Double.parseDouble(str);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }

        return 0.0;
    }

    public static int parseInt(Object value, int defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static double parseDouble(Object value, double defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static boolean parseBoolean(Object value, boolean defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return defaultValue;
    }

    public static Material parseMaterial(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        String upper = name.toUpperCase().replace('-', '_').replace(' ', '_');
        return MATERIAL_CACHE.get(upper);
    }

    public static ItemFlag parseItemFlag(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        String upper = name.toUpperCase().replace('-', '_').replace(' ', '_');
        return ITEM_FLAG_CACHE.get(upper);
    }

    public static Enchantment parseEnchantment(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        String upper = name.toUpperCase().replace('-', '_').replace(' ', '_');

        Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(upper.toLowerCase()));
        if (enchantment != null) {
            return enchantment;
        }

        for (Enchantment ench : Enchantment.values()) {
            if (ench.getKey().getKey().equalsIgnoreCase(name)) {
                return ench;
            }
            if (ench.getName() != null && ench.getName().equalsIgnoreCase(name)) {
                return ench;
            }
        }

        return null;
    }

    public static PotionEffectType parsePotionEffect(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        String upper = name.toUpperCase().replace('-', '_').replace(' ', '_');

        PotionEffectType type = PotionEffectType.getByKey(NamespacedKey.minecraft(upper.toLowerCase()));
        if (type != null) {
            return type;
        }

        for (PotionEffectType t : PotionEffectType.values()) {
            if (t.getKey().getKey().equalsIgnoreCase(name)) {
                return t;
            }
            if (t.getName() != null && t.getName().equalsIgnoreCase(name)) {
                return t;
            }
        }

        return null;
    }

    public static List<String> parseStringList(ConfigurationSection section, String path) {
        List<String> result = new ArrayList<>();
        if (section == null) {
            return result;
        }

        Object value = section.get(path);
        if (value instanceof List) {
            List<?> list = (List<?>) value;
            for (Object obj : list) {
                if (obj != null) {
                    result.add(obj.toString());
                }
            }
        } else if (value instanceof String) {
            result.add((String) value);
        }

        return result;
    }

    public static List<ItemFlag> parseItemFlagList(List<?> values) {
        List<ItemFlag> result = new ArrayList<>();
        if (values == null) {
            return result;
        }
        for (Object value : values) {
            if (value instanceof String) {
                ItemFlag flag = parseItemFlag((String) value);
                if (flag != null) {
                    result.add(flag);
                }
            }
        }
        return result;
    }

    public static Map<String, Integer> parseEnchantmentMap(ConfigurationSection section) {
        Map<String, Integer> result = new HashMap<>();
        if (section == null) {
            return result;
        }

        for (String key : section.getKeys(false)) {
            Object levelValue = section.get(key);
            if (levelValue instanceof ConfigurationSection) {
                ConfigurationSection levelSection = (ConfigurationSection) levelValue;
                int level = levelSection.getInt("level", 1);
                result.put(key, level);
            } else {
                int level = parseInt(levelValue, 1);
                result.put(key, level);
            }
        }

        return result;
    }

    public static Map<String, Object> parseEffectsMap(ConfigurationSection section) {
        Map<String, Object> result = new HashMap<>();
        if (section == null) {
            return result;
        }

        for (String key : section.getKeys(false)) {
            Object value = section.get(key);
            if (value instanceof ConfigurationSection) {
                ConfigurationSection effectSection = (ConfigurationSection) value;
                int level = effectSection.getInt("level", 1);
                int duration = effectSection.getInt("duration", 200);
                result.put(key, new int[]{level, duration});
            }
        }

        return result;
    }

    public static Map<String, Double> parseAttributesMap(ConfigurationSection section) {
        Map<String, Double> result = new HashMap<>();
        if (section == null) {
            return result;
        }

        for (String key : section.getKeys(false)) {
            Object value = section.get(key);
            double parsed = parsePercentage(value);
            result.put(key.toUpperCase(), parsed);
        }

        return result;
    }
}
