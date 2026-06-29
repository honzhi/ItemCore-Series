package com.minemart.itemcorelevel.listener;

import com.minemart.itemcorelevel.ItemCoreLevel;
import com.minemart.itemcorelevel.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final ItemCoreLevel plugin;

    public PlayerListener(ItemCoreLevel plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            PlayerData data;
            if (plugin.getDataStorage() != null) {
                data = plugin.getDataStorage().load(player.getUniqueId());
            } else {
                data = null;
            }
            if (data == null) {
                data = new PlayerData(player.getUniqueId());
                data.setPlayerName(player.getName());
                data.setLevel(plugin.getConfigManager().getStartLevel());
                data.setExp(plugin.getConfigManager().getStartExp());
            } else {
                data.setPlayerName(player.getName());
            }
            data.touch();
            plugin.getLevelManager().cachePlayerData(player.getUniqueId(), data);
            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().info("玩家数据已加载: " + player.getName() + " Lv." + data.getLevel());
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.getLevelManager().getPlayerData(player.getUniqueId());
        if (data != null) {
            data.touch();
            if (plugin.getDataStorage() != null) {
                plugin.getDataStorage().save(player.getUniqueId(), data);
            }
            plugin.getLevelManager().removePlayerData(player.getUniqueId());
        }
    }
}