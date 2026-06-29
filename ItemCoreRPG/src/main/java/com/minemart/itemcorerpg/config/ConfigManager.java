package com.minemart.itemcorerpg.config;

import com.minemart.itemcorerpg.ItemCoreRPG;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final ItemCoreRPG plugin;
    private FileConfiguration config;
    private Map<String, String> physicalFontMap;
    private Map<String, String> spellFontMap;
    private Map<String, String> critFontMap;

    public ConfigManager(ItemCoreRPG plugin) {
        this.plugin = plugin;
        this.physicalFontMap = new HashMap<>();
        this.spellFontMap = new HashMap<>();
        this.critFontMap = new HashMap<>();
    }

    public void load() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        loadCustomFonts();
    }

    private void loadCustomFonts() {
        physicalFontMap.clear();
        spellFontMap.clear();
        critFontMap.clear();

        ConfigurationSection physicalSection = config.getConfigurationSection("damage_indicators.custom_font.physical");
        if (physicalSection != null) {
            for (String key : physicalSection.getKeys(false)) {
                physicalFontMap.put(key, physicalSection.getString(key));
            }
        }

        ConfigurationSection spellSection = config.getConfigurationSection("damage_indicators.custom_font.spell");
        if (spellSection != null) {
            for (String key : spellSection.getKeys(false)) {
                spellFontMap.put(key, spellSection.getString(key));
            }
        }

        ConfigurationSection critSection = config.getConfigurationSection("damage_indicators.custom_font.crit");
        if (critSection != null) {
            for (String key : critSection.getKeys(false)) {
                critFontMap.put(key, critSection.getString(key));
            }
        }
    }

    public boolean isDamageDisplayEnabled() {
        return config.getBoolean("damage_indicators.enabled", true);
    }

    public double getMinDamage() {
        return config.getDouble("damage_indicators.min_damage", 0.1);
    }

    public String getDecimalFormat() {
        return config.getString("damage_indicators.decimal_format", "0.#");
    }

    public int getDurationTicks() {
        return config.getInt("damage_indicators.animation.duration-ticks", 40);
    }

    public double getRiseHeight() {
        return config.getDouble("damage_indicators.animation.rise-height", 1.5);
    }

    public double getVerticalOffset() {
        return config.getDouble("damage_indicators.animation.vertical-offset", 0.5);
    }

    public String getFormat(String type) {
        return config.getString("damage_indicators.types." + type + ".format", "{icon} &f{value}");
    }

    public String getIcon(String type) {
        // 元素类型：优先从 IC 的 ElementConfig 读取
        if (!type.equals("physical") && !type.equals("spell") && !type.equals("crit")) {
            String icIcon = com.minemart.itemcore.api.ItemCoreAPI.getElementIcon(type.toUpperCase());
            if (icIcon != null) return icIcon;
        }
        // 再查类型配置
        String defaultIcon = type.equals("physical") ? "⚔" : 
                             type.equals("spell") ? "✨" : 
                             type.equals("crit") ? "⚡" : "❤";
        return config.getString("damage_indicators.types." + type + ".icon", defaultIcon);
    }

    public String getColor(String type) {
        // 元素类型：优先从 IC 的 ElementConfig 读取
        if (!type.equals("physical") && !type.equals("spell") && !type.equals("crit")) {
            String icColor = com.minemart.itemcore.api.ItemCoreAPI.getElementColor(type.toUpperCase());
            if (icColor != null) return icColor;
        }
        // 再查类型配置
        String defaultColor = type.equals("physical") ? "&7" : 
                              type.equals("spell") ? "&b" : 
                              type.equals("crit") ? "&c" : "&7";
        return config.getString("damage_indicators.types." + type + ".color", defaultColor);
    }

    public boolean isCustomFontEnabled() {
        return config.getBoolean("damage_indicators.custom_font.enabled", false);
    }

    public Map<String, String> getPhysicalFontMap() {
        return physicalFontMap;
    }

    public Map<String, String> getSpellFontMap() {
        return spellFontMap;
    }

    public Map<String, String> getCritFontMap() {
        return critFontMap;
    }

    public Map<String, String> getFontMap(String type) {
        if (type.equals("spell")) {
            return spellFontMap;
        } else if (type.equals("crit")) {
            return critFontMap;
        }
        return physicalFontMap;
    }

    public boolean showCritIndicator() {
        return config.getBoolean("damage_indicators.types.crit.show_indicator", true);
    }

    public String getCritIndicator() {
        return config.getString("damage_indicators.types.crit.indicator", "暴击!");
    }

    public String getCritIndicatorColor() {
        return config.getString("damage_indicators.types.crit.indicator_color", "&e");
    }
}