package com.minemart.itemcorerpg.manager;

import com.minemart.itemcorerpg.ItemCoreRPG;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HealthCompressionManager implements Listener {

    private final ItemCoreRPG plugin;
    private final Map<UUID, HealthScaleState> previousStates = new HashMap<>();

    public HealthCompressionManager(ItemCoreRPG plugin) {
        this.plugin = plugin;
    }

    public void refreshAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            refresh(player);
        }
    }

    public void shutdown() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            restore(player);
        }
        previousStates.clear();
    }

    private void refresh(Player player) {
        if (!plugin.getConfigManager().isHealthCompressionEnabled()) {
            restore(player);
            return;
        }

        previousStates.computeIfAbsent(player.getUniqueId(), ignored ->
                new HealthScaleState(player.isHealthScaled(), player.getHealthScale()));
        player.setHealthScale(plugin.getConfigManager().getHealthScale());
        player.setHealthScaled(true);
    }

    private void restore(Player player) {
        HealthScaleState previousState = previousStates.remove(player.getUniqueId());
        if (previousState == null) {
            return;
        }

        if (previousState.scaled()) {
            player.setHealthScale(previousState.scale());
            player.setHealthScaled(true);
        } else {
            player.setHealthScaled(false);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        refresh(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        previousStates.remove(event.getPlayer().getUniqueId());
    }

    private record HealthScaleState(boolean scaled, double scale) {
    }
}
