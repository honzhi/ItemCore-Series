package com.minemart.itemcoreforge.config;

import com.minemart.itemcoreforge.ItemCoreForge;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ConfigManager {

    private final ItemCoreForge plugin;
    private FileConfiguration config;
    private FileConfiguration messages;

    public ConfigManager(ItemCoreForge plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        createDefaultDirectories();
        createDefaultFiles();
        loadMessages();
    }

    public void reload() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        loadMessages();
    }

    private void loadMessages() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    private void createDefaultDirectories() {
        createDirectoryIfNotExists(getForgesDirectory());
        createDirectoryIfNotExists(getLayoutsDirectory());
    }

    private void createDirectoryIfNotExists(String dirName) {
        File dir = new File(plugin.getDataFolder(), dirName);
        if (!dir.exists()) {
            dir.mkdirs();
            plugin.getLogger().info("创建目录: " + dirName);
        }
    }

    private void createDefaultFiles() {
        createDefaultFileIfNotExists("forges/example_template.yml");
        createDefaultFileIfNotExists("forges/example_crafting_table.yml");
        createDefaultFileIfNotExists("forges/example_furnace.yml");
        createDefaultFileIfNotExists("layouts/Default.yml");
    }

    private void createDefaultFileIfNotExists(String filePath) {
        File file = new File(plugin.getDataFolder(), filePath);
        if (!file.exists()) {
            try (InputStream inputStream = plugin.getResource(filePath)) {
                if (inputStream != null) {
                    Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    plugin.getLogger().info("创建默认配置文件: " + filePath);
                }
            } catch (IOException e) {
                plugin.getLogger().severe("创建默认配置文件失败: " + filePath + " - " + e.getMessage());
            }
        }
    }

    public String getForgesDirectory() {
        return config.getString("settings.forges-directory", "forges");
    }

    public String getLayoutsDirectory() {
        return config.getString("settings.layouts-directory", "layouts");
    }

    public boolean isDebug() {
        return config != null && config.getBoolean("settings.debug", true);
    }

    public int getRecipesPerPage() {
        return config.getInt("gui.recipes-per-page", 28);
    }

    public String getBorderItem() {
        return config.getString("gui.border-item", "GRAY_STAINED_GLASS_PANE");
    }

    public String getPreviousItem() {
        return config.getString("gui.previous-item", "ARROW");
    }

    public String getNextItem() {
        return config.getString("gui.next-item", "ARROW");
    }

    public String getBackItem() {
        return config.getString("gui.back-item", "BARRIER");
    }

    public String getCraftItem() {
        return config.getString("gui.craft-item", "GREEN_WOOL");
    }

    public String getCraftItemName() {
        return config.getString("gui.craft-item-name", "&a开始制作");
    }

    public String getCraftDisabledItem() {
        return config.getString("gui.craft-disabled-item", "RED_WOOL");
    }

    public String getCraftDisabledName() {
        return config.getString("gui.craft-disabled-name", "&c材料不足");
    }

    public long getCraftClickCooldown() {
        return config.getLong("gui.craft-click-cooldown", 500);
    }

    public String getMessage(String key) {
        if (messages == null) {
            return "";
        }
        return messages.getString(key, "");
    }

    public String getPrefix() {
        if (messages == null) {
            return "&8[&6Forge&8] &7";
        }
        return messages.getString("prefix", "&8[&6Forge&8] &7");
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getMessages() {
        return messages;
    }
}
