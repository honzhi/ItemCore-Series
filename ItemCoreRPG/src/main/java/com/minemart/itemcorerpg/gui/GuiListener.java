package com.minemart.itemcorerpg.gui;

import com.minemart.itemcorerpg.ItemCoreRPG;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GuiListener implements Listener {

    private static final String GUI_TITLE = "\u00a78\u25a0\u25a0 \u00a76\u73a9\u5bb6\u4fe1\u606f \u00a78\u25a0\u25a0";

    private static final Map<UUID, UUID> viewingMap = new ConcurrentHashMap<>();

    public static void trackView(UUID viewerId, UUID targetId) {
        viewingMap.put(viewerId, targetId);
    }

    public static void untrackView(UUID viewerId) {
        viewingMap.remove(viewerId);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getView().getTitle().equals(GUI_TITLE)) return;

        event.setCancelled(true);

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
        if (event.getCurrentItem().getType() == Material.GRAY_STAINED_GLASS_PANE) return;

        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        if (slot == 48) {
            // Refresh
            UUID viewerId = player.getUniqueId();
            UUID targetId = viewingMap.get(viewerId);
            if (targetId != null) {
                Player target = Bukkit.getPlayer(targetId);
                if (target != null) {
                    StatsMenu.open(player, target);
                }
            }
        } else if (slot == 50) {
            player.closeInventory();
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            untrackView(event.getPlayer().getUniqueId());
        }
    }
}