package com.minemart.itemcore.config;

import com.minemart.itemcore.ItemCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class AttributesConfig {

    public enum PenetrationOrder {
        PERCENT_FIRST,
        FLAT_FIRST
    }

    private final ItemCore plugin;
    private FileConfiguration config;
    private File configFile;

    public AttributesConfig(ItemCore plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "attributes.yml");
    }

    public void load() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        if (!configFile.exists()) {
            plugin.saveResource("attributes.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        try {
            InputStream defaultStream = plugin.getResource("attributes.yml");
            if (defaultStream != null) {
                YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8)
                );
                config.setDefaults(defaultConfig);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "无法加载默认属性配置", e);
        }

        plugin.getLogger().info("属性配置已加载");
    }

    public void reload() {
        if (!configFile.exists()) {
            plugin.saveResource("attributes.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);

        try {
            InputStream defaultStream = plugin.getResource("attributes.yml");
            if (defaultStream != null) {
                YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8)
                );
                config.setDefaults(defaultConfig);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "无法加载默认属性配置", e);
        }

        plugin.getLogger().info("属性配置已重载");
    }

    public void save() {
        if (config == null || configFile == null) {
            return;
        }
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "无法保存属性配置文件", e);
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public double getDefaultCritDamage() {
        return config.getDouble("crit.default_crit_damage", 150.0);
    }

    public double getAdaptiveForceAttackConversion() {
        return config.getDouble("adaptive_force.attack_conversion", 1.0);
    }

    public double getAdaptiveForceSpellConversion() {
        return config.getDouble("adaptive_force.spell_conversion", 1.0);
    }

    public String getArmorFormula() {
        return getPhysicalResistFormula();
    }

    public String getSpellResistFormula() {
        return config.getString("defense_formulas.spell_resist", "{damage} * (1 - {armor} / ({armor} + 100))");
    }

    public String getPhysicalResistFormula() {
        return config.getString("defense_formulas.physical_resist", "{damage} * (1 - {armor} / ({armor} + 100))");
    }

    public PenetrationOrder getPenetrationOrder() {
        String orderStr = config.getString("penetration_order", "percent_first");
        if ("flat_first".equalsIgnoreCase(orderStr)) {
            return PenetrationOrder.FLAT_FIRST;
        }
        return PenetrationOrder.PERCENT_FIRST;
    }
}
