package com.minemart.itemcorelevel.manager;

import com.minemart.itemcorelevel.ItemCoreLevel;
import com.minemart.itemcorelevel.PlayerData;
import com.minemart.itemcorelevel.api.ExpSource;
import com.minemart.itemcorelevel.api.event.PlayerGainExpEvent;
import com.minemart.itemcorelevel.config.ConfigManager;
import com.minemart.itemcorelevel.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

public class ExpManager {

    private final ItemCoreLevel plugin;
    private final LevelManager levelManager;
    private final ConfigManager configManager;

    public ExpManager(ItemCoreLevel plugin) {
        this.plugin = plugin;
        this.levelManager = plugin.getLevelManager();
        this.configManager = plugin.getConfigManager();
    }

    public void addExp(Player player, int amount, ExpSource source) {
        if (player == null || !player.isOnline() || amount <= 0) return;

        PlayerGainExpEvent event = new PlayerGainExpEvent(player, amount, source);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) return;

        int finalAmount = event.getAmount();
        if (finalAmount <= 0) return;

        PlayerData data = levelManager.getOrCreatePlayerData(player.getUniqueId());
        int maxLevel = configManager.getMaxLevel();
        if (maxLevel > 0 && data.getLevel() >= maxLevel) return;

        data.setExp(data.getExp() + finalAmount);
        data.touch();

        if (configManager.isExpGainEnabled()) {
            sendExpGainMessage(player, finalAmount, data);
        }

        levelManager.checkLevelUp(player);
    }

    public void removeExp(Player player, int amount) {
        if (player == null || amount <= 0) return;
        PlayerData data = levelManager.getPlayerData(player.getUniqueId());
        if (data == null) return;
        data.setExp(Math.max(0, data.getExp() - amount));
        data.touch();
    }

    private void sendExpGainMessage(Player player, int amount, PlayerData data) {
        int level = data.getLevel();
        int exp = data.getExp();
        int required = levelManager.getRequiredExp(level);
        double progress = required > 0 ? Math.min(1.0, (double) exp / required) : 0;
        String progressStr = String.format("%.1f%%", progress * 100);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", player.getName());
        placeholders.put("amount", String.valueOf(amount));
        placeholders.put("level", String.valueOf(level));
        placeholders.put("exp", String.valueOf(exp));
        placeholders.put("required", String.valueOf(required));
        placeholders.put("progress", progressStr);

        String msg = MessageUtil.replacePlaceholders(configManager.getExpGainMessage(), placeholders);
        MessageUtil.sendChat(player, msg);

        String soundName = configManager.getExpGainSound();
        if (soundName != null && !soundName.isEmpty()) {
            try {
                Sound sound = Sound.valueOf(soundName);
                player.playSound(player.getLocation(), sound, configManager.getExpGainSoundVolume(), configManager.getExpGainSoundPitch());
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void sendLevelUpNotification(Player player, int oldLevel, int newLevel) {
        if (!configManager.isLevelUpEnabled()) return;

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", player.getName());
        placeholders.put("level", String.valueOf(newLevel));
        placeholders.put("old_level", String.valueOf(oldLevel));

        String mode = configManager.getLevelUpMode();
        switch (mode.toLowerCase()) {
            case "chat":
                String chatMsg = MessageUtil.replacePlaceholders(configManager.getLevelUpChatMessage(), placeholders);
                MessageUtil.sendChat(player, chatMsg);
                break;
            case "actionbar":
                String actionMsg = MessageUtil.replacePlaceholders(configManager.getLevelUpActionBarMessage(), placeholders);
                MessageUtil.sendActionBar(player, actionMsg);
                break;
            case "title":
                String title = MessageUtil.replacePlaceholders(configManager.getLevelUpTitleMain(), placeholders);
                String sub = MessageUtil.replacePlaceholders(configManager.getLevelUpTitleSub(), placeholders);
                MessageUtil.sendTitle(player, title, sub,
                    configManager.getLevelUpTitleFadeIn(),
                    configManager.getLevelUpTitleStay(),
                    configManager.getLevelUpTitleFadeOut());
                break;
        }

        String soundName = configManager.getLevelUpSound();
        if (soundName != null && !soundName.isEmpty()) {
            try {
                Sound sound = Sound.valueOf(soundName);
                player.playSound(player.getLocation(), sound, configManager.getLevelUpSoundVolume(), configManager.getLevelUpSoundPitch());
            } catch (IllegalArgumentException ignored) {}
        }
    }
}