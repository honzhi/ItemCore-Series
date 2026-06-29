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

public class ConfigManager {

    private final ItemCore plugin;
    private FileConfiguration config;
    private File configFile;

    public ConfigManager(ItemCore plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
    }

    public void load() {
        plugin.saveDefaultConfig();

        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        try {
            InputStream defaultStream = plugin.getResource("config.yml");
            if (defaultStream != null) {
                YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8)
                );
                config.setDefaults(defaultConfig);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "无法加载默认配置", e);
        }
    }

    public void reload() {
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        }
        config = YamlConfiguration.loadConfiguration(configFile);

        try {
            InputStream defaultStream = plugin.getResource("config.yml");
            if (defaultStream != null) {
                YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8)
                );
                config.setDefaults(defaultConfig);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "无法加载默认配置", e);
        }
    }

    public void save() {
        if (config == null || configFile == null) {
            return;
        }
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "无法保存配置文件", e);
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public String getLanguage() {
        return config.getString(ConfigKeys.LANGUAGE, ConfigKeys.DEFAULT_LANGUAGE);
    }

    public boolean isDebugMode() {
        return config.getBoolean(ConfigKeys.DEBUG_MODE, false);
    }

    public String getItemsFolder() {
        return config.getString(ConfigKeys.ITEMS_FOLDER, ConfigKeys.DEFAULT_ITEMS_FOLDER);
    }

    public String getCategoriesFile() {
        return config.getString(ConfigKeys.CATEGORIES_FILE, ConfigKeys.DEFAULT_CATEGORIES_FILE);
    }

    public String getGuiName() {
        return config.getString(ConfigKeys.GUI_NAME, ConfigKeys.DEFAULT_GUI_NAME);
    }

    public int getGuiSize() {
        return config.getInt(ConfigKeys.GUI_SIZE, ConfigKeys.DEFAULT_GUI_SIZE);
    }


    public boolean isLoreRefreshEnabled() {
        return config.getBoolean(ConfigKeys.LORE_REFRESH_ENABLED, ConfigKeys.DEFAULT_LORE_REFRESH_ENABLED);
    }

    public int getLoreRefreshInterval() {
        return config.getInt(ConfigKeys.LORE_REFRESH_INTERVAL, ConfigKeys.DEFAULT_LORE_REFRESH_INTERVAL);
    }

    public int getGuiItemsPerPage() {
        return config.getInt(ConfigKeys.GUI_ITEMS_PER_PAGE, ConfigKeys.DEFAULT_GUI_ITEMS_PER_PAGE);
    }
}
