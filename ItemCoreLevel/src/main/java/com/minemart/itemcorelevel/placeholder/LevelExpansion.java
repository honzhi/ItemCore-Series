package com.minemart.itemcorelevel.placeholder;

import com.minemart.itemcorelevel.ItemCoreLevel;
import com.minemart.itemcorelevel.PlayerData;
import com.minemart.itemcorelevel.config.ConfigManager;
import com.minemart.itemcorelevel.manager.LevelManager;
import com.minemart.itemcorelevel.util.MessageUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LevelExpansion extends PlaceholderExpansion {

    private final ItemCoreLevel plugin;
    private final ConfigManager configManager;
    private final LevelManager levelManager;

    public LevelExpansion(ItemCoreLevel plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.levelManager = plugin.getLevelManager();
    }

    @Override
    public @NotNull String getIdentifier() { return "itemcorelevel"; }

    @Override
    public @NotNull String getAuthor() { return "MineMart"; }

    @Override
    public @NotNull String getVersion() { return "1.0.0"; }

    @Override
    public boolean persist() { return true; }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "";
        PlayerData data = levelManager.getPlayerData(player.getUniqueId());

        switch (params.toLowerCase()) {
            case "level":
                return String.valueOf(data != null ? data.getLevel() : configManager.getStartLevel());
            case "exp":
                return String.valueOf(data != null ? data.getExp() : configManager.getStartExp());
            case "exp_required": {
                int level = data != null ? data.getLevel() : configManager.getStartLevel();
                int required = levelManager.getRequiredExp(level);
                return required == Integer.MAX_VALUE ? "MAX" : String.valueOf(required);
            }
            case "exp_progress": {
                int level = data != null ? data.getLevel() : configManager.getStartLevel();
                int exp = data != null ? data.getExp() : configManager.getStartExp();
                int required = levelManager.getRequiredExp(level);
                if (required <= 0 || required == Integer.MAX_VALUE) return "1.0";
                return String.format("%.4f", Math.min(1.0, (double) exp / required));
            }
            case "exp_progress_bar": {
                int level = data != null ? data.getLevel() : configManager.getStartLevel();
                int exp = data != null ? data.getExp() : configManager.getStartExp();
                int required = levelManager.getRequiredExp(level);
                if (required <= 0 || required == Integer.MAX_VALUE) {
                    String done = configManager.getProgressBarDoneColor() + configManager.getProgressBarDoneChar();
                    return MessageUtil.colorize(done.repeat(configManager.getProgressBarLength()));
                }
                double progress = Math.min(1.0, (double) exp / required);
                int doneCount = (int) Math.round(progress * configManager.getProgressBarLength());
                int notDoneCount = configManager.getProgressBarLength() - doneCount;
                StringBuilder sb = new StringBuilder();
                sb.append(configManager.getProgressBarDoneColor());
                sb.append(String.valueOf(configManager.getProgressBarDoneChar()).repeat(Math.max(0, doneCount)));
                sb.append(configManager.getProgressBarNotDoneColor());
                sb.append(String.valueOf(configManager.getProgressBarNotDoneChar()).repeat(Math.max(0, notDoneCount)));
                return MessageUtil.colorize(sb.toString());
            }
            case "exp_remaining": {
                int level = data != null ? data.getLevel() : configManager.getStartLevel();
                int exp = data != null ? data.getExp() : configManager.getStartExp();
                int required = levelManager.getRequiredExp(level);
                if (required == Integer.MAX_VALUE) return "0";
                return String.valueOf(Math.max(0, required - exp));
            }
            case "max_level":
                int maxLevel = configManager.getMaxLevel();
                return maxLevel > 0 ? String.valueOf(maxLevel) : "\u221e";
        }
        return null;
    }
}