package com.minemart.itemcoretrinkets.gui;

import com.minemart.itemcoretrinkets.ItemCoreTrinkets;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuiListener implements Listener {

    private final ItemCoreTrinkets plugin;
    private final Map<UUID, TrinketMenu> openMenus;

    public GuiListener(ItemCoreTrinkets plugin) {
        this.plugin = plugin;
        this.openMenus = new HashMap<>();
    }

    public void registerMenu(TrinketMenu menu) {
        openMenus.put(menu.getPlayer().getUniqueId(), menu);
    }

    public void unregisterMenu(UUID playerId) {
        openMenus.remove(playerId);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        TrinketMenu menu = openMenus.get(event.getWhoClicked().getUniqueId());
        if (menu != null) {
            menu.handleClick(event);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        TrinketMenu menu = openMenus.remove(playerId);
        if (menu != null) {
            menu.handleClose(event);
        }
    }
}