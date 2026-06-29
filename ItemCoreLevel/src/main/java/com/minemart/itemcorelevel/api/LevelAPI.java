package com.minemart.itemcorelevel.api;

import com.minemart.itemcorelevel.ItemCoreLevel;
import com.minemart.itemcorelevel.PlayerData;
import org.bukkit.entity.Player;

public final class LevelAPI {

    private LevelAPI() {}

    public static int getLevel(Player player) {
        ItemCoreLevel plugin = ItemCoreLevel.getInstance();
        if (plugin == null) return 1;
        PlayerData data = plugin.getLevelManager().getPlayerData(player.getUniqueId());
        return data != null ? data.getLevel() : plugin.getConfigManager().getStartLevel();
    }

    public static int getExp(Player player) {
        ItemCoreLevel plugin = ItemCoreLevel.getInstance();
        if (plugin == null) return 0;
        PlayerData data = plugin.getLevelManager().getPlayerData(player.getUniqueId());
        return data != null ? data.getExp() : plugin.getConfigManager().getStartExp();
    }

    public static int getRequiredExp(int level) {
        ItemCoreLevel plugin = ItemCoreLevel.getInstance();
        if (plugin == null) return Integer.MAX_VALUE;
        return plugin.getLevelManager().getRequiredExp(level);
    }

    public static int getRequiredExp(Player player) {
        return getRequiredExp(getLevel(player));
    }

    public static double getExpProgress(Player player) {
        int exp = getExp(player);
        int required = getRequiredExp(player);
        if (required <= 0) return 1.0;
        return Math.min(1.0, (double) exp / required);
    }

    public static void setLevel(Player player, int level) {
        ItemCoreLevel plugin = ItemCoreLevel.getInstance();
        if (plugin != null) plugin.getLevelManager().setLevel(player, level);
    }

    public static void addExp(Player player, int amount) {
        ItemCoreLevel plugin = ItemCoreLevel.getInstance();
        if (plugin != null) plugin.getExpManager().addExp(player, amount, ExpSource.API);
    }

    public static void setExp(Player player, int amount) {
        ItemCoreLevel plugin = ItemCoreLevel.getInstance();
        if (plugin != null) plugin.getLevelManager().setExp(player.getUniqueId(), amount);
    }

    public static void resetPlayer(Player player) {
        ItemCoreLevel plugin = ItemCoreLevel.getInstance();
        if (plugin != null) plugin.getLevelManager().resetPlayer(player);
    }
}