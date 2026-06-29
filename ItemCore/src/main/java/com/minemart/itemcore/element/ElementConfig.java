package com.minemart.itemcore.element;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.item.attribute.AilmentRegistry;
import com.minemart.itemcore.item.attribute.AilmentType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ElementConfig {

    private final ItemCore plugin;
    private FileConfiguration config;
    private File configFile;
    private Map<String, ElementData> elementDataMap = new HashMap<>();

    public ElementConfig(ItemCore plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "elements.yml");
    }

    public void load() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        if (!configFile.exists()) {
            plugin.saveResource("elements.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        try {
            InputStream defaultStream = plugin.getResource("elements.yml");
            if (defaultStream != null) {
                YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8)
                );
                config.setDefaults(defaultConfig);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "无法加载默认元素配置", e);
        }

        elementDataMap.clear();
        ConfigurationSection elementsSection = config.getConfigurationSection("elements");
        if (elementsSection != null) {
            for (String elementId : elementsSection.getKeys(false)) {
                ConfigurationSection elementSection = elementsSection.getConfigurationSection(elementId);
                if (elementSection == null) continue;

                String display = elementSection.getString("display", elementId);
                double threshold = elementSection.getDouble("threshold", 100);
                double decayPerSecond = elementSection.getDouble("decay-per-second", 5);

                ConfigurationSection accSection = elementSection.getConfigurationSection("accumulation");
                AccumulationConfig accumulationConfig = parseAccumulation(accSection);

                String ailmentId = elementSection.getString("ailment", "");

                String icon = elementSection.getString("icon", "✦");
                String color = elementSection.getString("color", "&f");
                ElementData data = new ElementData(elementId, display, icon, color, threshold, decayPerSecond,
                    accumulationConfig, ailmentId);
                elementDataMap.put(elementId.toUpperCase(), data);
                plugin.getLogger().fine("加载元素: " + elementId);
            }
        }
        plugin.getLogger().info("元素配置已加载 (" + elementDataMap.size() + " 个元素)");
    }

    private AccumulationConfig parseAccumulation(ConfigurationSection section) {
        if (section == null) {
            return new AccumulationConfig(AccumulationConfig.AccumulationMode.DAMAGE_PERCENT, 0.15,
                null, 0, new ArrayList<>());
        }

        String modeStr = section.getString("mode", "DAMAGE_PERCENT");
        AccumulationConfig.AccumulationMode mode;
        try {
            mode = AccumulationConfig.AccumulationMode.valueOf(modeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            mode = AccumulationConfig.AccumulationMode.DAMAGE_PERCENT;
        }

        double value = section.getDouble("value", 0.15);
        String attribute = section.getString("attribute");
        double multiplier = section.getDouble("multiplier", 0);
        List<String> allowSources = section.getStringList("allow-sources");

        return new AccumulationConfig(mode, value, attribute, multiplier, allowSources);
    }

    public void reload() {
        load();
    }

    public ElementData getElementData(String elementId) {
        return elementDataMap.get(elementId.toUpperCase());
    }

    public Map<String, ElementData> getAllElementData() {
        return elementDataMap;
    }

    public AilmentRegistry buildAilmentRegistry() {
        AilmentRegistry registry = new AilmentRegistry();
        File ailmentsFile = new File(plugin.getDataFolder(), "ailments.yml");
        if (!ailmentsFile.exists()) {
            plugin.saveResource("ailments.yml", false);
        }
        FileConfiguration ailmentsConfig = YamlConfiguration.loadConfiguration(ailmentsFile);

        ConfigurationSection ailmentsSection = ailmentsConfig.getConfigurationSection("ailments");
        if (ailmentsSection != null) {
            for (String ailmentId : ailmentsSection.getKeys(false)) {
                ConfigurationSection ailmentSection = ailmentsSection.getConfigurationSection(ailmentId);
                if (ailmentSection == null) continue;

                String display = ailmentSection.getString("display", ailmentId);
                AilmentType ailmentType = new AilmentType(ailmentId, display);
                registry.register(ailmentType);
            }
        }

        return registry;
    }

    public AilmentConfig buildAilmentConfig() {
        AilmentConfig ailmentConfig = new AilmentConfig(plugin);
        ailmentConfig.load();
        return ailmentConfig;
    }

    public static class ElementData {
        private final String id;
        private final String display;
        private final String icon;
        private final String color;
        private final double threshold;
        private final double decayPerSecond;
        private final AccumulationConfig accumulation;
        private final String ailmentId;

        public ElementData(String id, String display, String icon, String color, double threshold, double decayPerSecond,
                           AccumulationConfig accumulation, String ailmentId) {
            this.id = id;
            this.display = display;
            this.icon = icon;
            this.color = color;
            this.threshold = threshold;
            this.decayPerSecond = decayPerSecond;
            this.accumulation = accumulation;
            this.ailmentId = ailmentId;
        }

        public String getId() { return id; }
        public String getDisplay() { return display; }
        public String getIcon() { return icon; }
        public String getColor() { return color; }
        public double getThreshold() { return threshold; }
        public double getDecayPerSecond() { return decayPerSecond; }
        public AccumulationConfig getAccumulation() { return accumulation; }
        public String getAilmentId() { return ailmentId; }
    }
}