package com.minemart.itemcore.gui;

import com.minemart.itemcore.ItemCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class GuiListener implements Listener {

    private final ItemCore plugin;
    private final Map<Inventory, BaseMenu> menus;

    public GuiListener(ItemCore plugin) {
        this.plugin = plugin;
        this.menus = new HashMap<>();
    }

    public void registerMenu(BaseMenu menu) {
        menus.put(menu.getInventory(), menu);
    }

    public void unregisterMenu(BaseMenu menu) {
        menus.remove(menu.getInventory());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        BaseMenu menu = menus.get(inventory);

        if (menu != null) {
            if (menu.shouldCancelClicks()) {
                event.setCancelled(true);
            }
            menu.onItemClick(event);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        BaseMenu menu = menus.get(inventory);
        if (menu != null) {
            menu.handleClose(event);
            unregisterMenu(menu);
        }
    }
}
