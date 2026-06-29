package com.minemart.itemcore.element;

import com.minemart.itemcore.ItemCore;
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

public class AilmentConfig {

    private final ItemCore plugin;
    private FileConfiguration config;
    private File configFile;
    private Map<String, AilmentData> ailmentDataMap = new HashMap<>();

    public AilmentConfig(ItemCore plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "ailments.yml");
    }

    public void load() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        if (!configFile.exists()) {
            plugin.saveResource("ailments.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        try {
            InputStream defaultStream = plugin.getResource("ailments.yml");
            if (defaultStream != null) {
                YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8)
                );
                config.setDefaults(defaultConfig);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "无法加载默认异常配置", e);
        }

        ailmentDataMap.clear();
        ConfigurationSection ailmentsSection = config.getConfigurationSection("ailments");
        if (ailmentsSection != null) {
            for (String ailmentId : ailmentsSection.getKeys(false)) {
                ConfigurationSection ailmentSection = ailmentsSection.getConfigurationSection(ailmentId);
                if (ailmentSection == null) continue;

                String display = ailmentSection.getString("display", ailmentId);
                int duration = ailmentSection.getInt("duration", 100);
                String refreshPolicy = ailmentSection.getString("refresh_policy", "RESET");
                int maxStacks = ailmentSection.getInt("max_stacks", 1);

                List<AilmentTrigger> triggers = new ArrayList<>();
                List<Map<?, ?>> triggerList = ailmentSection.getMapList("triggers");
                for (Map<?, ?> triggerMap : triggerList) {
                    String type = (String) triggerMap.get("type");
                    Double value = triggerMap.get("value") instanceof Number
                        ? ((Number) triggerMap.get("value")).doubleValue() : null;
                    Integer interval = triggerMap.get("interval") instanceof Number
                        ? ((Number) triggerMap.get("interval")).intValue() : null;
                    String attribute = (String) triggerMap.get("attribute");
                    triggers.add(new AilmentTrigger(type, value, interval, attribute));
                }

                AilmentData data = new AilmentData(ailmentId, display, duration, refreshPolicy,
                    maxStacks, triggers);
                ailmentDataMap.put(ailmentId.toUpperCase(), data);
            }
        }

        plugin.getLogger().info("异常配置已加载 (" + ailmentDataMap.size() + " 个异常)");
    }

    public void reload() {
        load();
    }

    public AilmentData getAilmentData(String ailmentId) {
        return ailmentDataMap.get(ailmentId.toUpperCase());
    }

    public Map<String, AilmentData> getAllAilmentData() {
        return ailmentDataMap;
    }

    public static class AilmentData {
        private final String id;
        private final String display;
        private final int duration;
        private final String refreshPolicy;
        private final int maxStacks;
        private final List<AilmentTrigger> triggers;

        public AilmentData(String id, String display, int duration, String refreshPolicy,
                           int maxStacks, List<AilmentTrigger> triggers) {
            this.id = id;
            this.display = display;
            this.duration = duration;
            this.refreshPolicy = refreshPolicy;
            this.maxStacks = maxStacks;
            this.triggers = triggers;
        }

        public String getId() { return id; }
        public String getDisplay() { return display; }
        public int getDuration() { return duration; }
        public String getRefreshPolicy() { return refreshPolicy; }
        public int getMaxStacks() { return maxStacks; }
        public List<AilmentTrigger> getTriggers() { return triggers; }
    }
}
