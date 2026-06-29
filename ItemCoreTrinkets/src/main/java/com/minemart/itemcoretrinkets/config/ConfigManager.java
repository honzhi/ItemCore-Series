package com.minemart.itemcoretrinkets.config;

import com.minemart.itemcoretrinkets.ItemCoreTrinkets;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final ItemCoreTrinkets plugin;
    private FileConfiguration config;
    private FileConfiguration slotsConfig;
    private FileConfiguration guiConfig;
    private FileConfiguration messagesConfig;

    private File configFile;
    private File slotsFile;
    private File guiFile;
    private File messagesFile;

    public ConfigManager(ItemCoreTrinkets plugin) {
        this.plugin = plugin;
        createDefaultFiles();
    }

    private void createDefaultFiles() {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        configFile = new File(dataFolder, "config.yml");
        slotsFile = new File(dataFolder, "slots.yml");
        guiFile = new File(dataFolder, "gui.yml");
        messagesFile = new File(dataFolder, "messages.yml");

        saveDefaultConfig("config.yml", configFile);
        saveDefaultConfig("slots.yml", slotsFile);
        saveDefaultConfig("gui.yml", guiFile);
        saveDefaultConfig("messages.yml", messagesFile);
    }

    private void saveDefaultConfig(String resourceName, File targetFile) {
        if (!targetFile.exists()) {
            try (InputStream inputStream = plugin.getResource(resourceName)) {
                if (inputStream != null) {
                    Files.copy(inputStream, targetFile.toPath());
                }
            } catch (IOException e) {
                plugin.getLogger().severe("无法保存默认配置文件: " + resourceName);
                e.printStackTrace();
            }
        }
    }

    public void loadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void loadSlots() {
        slotsConfig = YamlConfiguration.loadConfiguration(slotsFile);
    }

    public void loadGuiLayout() {
        guiConfig = YamlConfiguration.loadConfiguration(guiFile);
    }

    public void reload() {
        loadConfig();
        loadSlots();
        loadGuiLayout();
        plugin.getSlotLoader().loadSlots();
        plugin.getTrinketManager().reloadSlots();
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getSlotsConfig() {
        return slotsConfig;
    }

    public FileConfiguration getGuiConfig() {
        return guiConfig;
    }

    public FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }

    public String getMessage(String key) {
        String prefix = messagesConfig.getString("messages.prefix", "&6[Trinkets] ");
        String message = messagesConfig.getString("messages." + key, key);
        return ChatColor.translateAlternateColorCodes('&', prefix + message);
    }

    public int getAutoSaveInterval() {
        return config.getInt("general.auto-save-interval", 300);
    }

    public boolean isDebugMode() {
        return config.getBoolean("general.debug-mode", false);
    }

    public boolean shouldApplyHealth() {
        return config.getBoolean("attributes.apply-health", true);
    }

    public boolean shouldApplyRegeneration() {
        return config.getBoolean("attributes.apply-regeneration", true);
    }

    public boolean shouldApplyDamageBoosts() {
        return config.getBoolean("attributes.apply-damage-boosts", true);
    }
}