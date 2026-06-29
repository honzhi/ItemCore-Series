package com.minemart.itemcoretrinkets.listener;

import com.minemart.itemcoretrinkets.ItemCoreTrinkets;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final ItemCoreTrinkets plugin;

    public PlayerListener(ItemCoreTrinkets plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getTrinketManager().loadPlayerData(event.getPlayer());
        // 属性由 ItemCore 的 CombatListener 在加入时自动计算（含饰品贡献）

        if (plugin.getConfigManager().isDebugMode()) {
            plugin.getLogger().info("[Debug] 玩家 " + event.getPlayer().getName() + " 加入 - 已加载饰品数据");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getTrinketManager().unloadPlayerData(event.getPlayer());

        if (plugin.getConfigManager().isDebugMode()) {
            plugin.getLogger().info("[Debug] 玩家 " + event.getPlayer().getName() + " 退出 - 已保存饰品数据");
        }
    }
}