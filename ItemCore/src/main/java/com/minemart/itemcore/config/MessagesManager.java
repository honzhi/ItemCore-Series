package com.minemart.itemcore.config;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.utils.MessageUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class MessagesManager {

    private final ItemCore plugin;
    private FileConfiguration messages;
    private File messagesFile;
    private final Map<String, String> messageCache;

    public MessagesManager(ItemCore plugin) {
        this.plugin = plugin;
        this.messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        this.messageCache = new HashMap<>();
    }

    public void load() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        messages = YamlConfiguration.loadConfiguration(messagesFile);

        try {
            InputStream defaultStream = plugin.getResource("messages.yml");
            if (defaultStream != null) {
                YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8)
                );
                messages.setDefaults(defaultConfig);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "无法加载默认消息配置", e);
        }

        messageCache.clear();
        plugin.getLogger().info("消息配置已加载");
    }

    public void reload() {
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);

        try {
            InputStream defaultStream = plugin.getResource("messages.yml");
            if (defaultStream != null) {
                YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8)
                );
                messages.setDefaults(defaultConfig);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "无法加载默认消息配置", e);
        }

        messageCache.clear();
        plugin.getLogger().info("消息配置已重载");
    }

    public void save() {
        if (messages == null || messagesFile == null) {
            return;
        }
        try {
            messages.save(messagesFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "无法保存消息配置文件", e);
        }
    }

    public String getMessage(String key, String defaultValue) {
        String cached = messageCache.get(key);
        if (cached != null) {
            return cached;
        }

        String value = messages.getString(key, defaultValue);
        if (value == null) {
            value = defaultValue;
        }
        value = MessageUtil.colorizeString(value);
        messageCache.put(key, value);
        return value;
    }

    public String getMessage(String key) {
        return getMessage(key, "");
    }

    public String format(String message, Object... replacements) {
        if (message == null) {
            return "";
        }

        String result = message;
        for (int i = 0; i < replacements.length - 1; i += 2) {
            String placeholder = String.valueOf(replacements[i]);
            String value = String.valueOf(replacements[i + 1]);
            result = result.replace(placeholder, value);
        }
        return result;
    }

    public String getPrefix() {
        return getMessage("general.prefix", "&7[&bItemCore&7] ");
    }

    public String getNoPermission() {
        return getMessage("permissions.no_permission", "&c你没有权限执行此命令");
    }

    public String getNoObtainPermission() {
        return getMessage("permissions.no_obtain_permission", "&c你没有权限获取此物品");
    }

    public String getNoGuiObtainPermission() {
        return getMessage("permissions.no_gui_obtain_permission", "&c你没有权限从GUI获取物品");
    }

    public String getPlayerNotFound(String player) {
        String msg = getMessage("commands.player_not_found", "&c玩家未找到: &7{player}");
        return format(msg, "{player}", player);
    }

    public String getInvalidAmount(String amount) {
        String msg = getMessage("commands.invalid_amount", "&c无效的数量: &7{amount}");
        return format(msg, "{amount}", amount);
    }

    public String getItemNotFound(String item) {
        String msg = getMessage("items.not_found", "&c未找到物品: &7{item}");
        return format(msg, "{item}", item);
    }

    public String getItemGiven(String player, String item, int amount) {
        String msg = getMessage("items.given", "&a已给予 &7{player} &a物品: &7{item} &ax{amount}");
        return format(msg, "{player}", player, "{item}", item, "{amount}", amount);
    }

    public String getItemObtained(String item) {
        String msg = getMessage("items.obtained", "&a你获得了物品: &7{item}");
        return format(msg, "{item}", item);
    }

    public FileConfiguration getMessages() {
        return messages;
    }
}
