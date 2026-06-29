package com.minemart.itemcoreforge.gui;

import com.minemart.itemcoreforge.ItemCoreForge;
import com.minemart.itemcoreforge.api.event.RecipeQueueEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuiListener implements Listener {

    private final ItemCoreForge plugin;
    private final Map<UUID, BaseMenu> openMenus = new HashMap<>();

    public GuiListener(ItemCoreForge plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof BaseMenu menu)) {
            return;
        }

        event.setCancelled(true);

        if (event.getClickedInventory() == event.getInventory()) {
            menu.handleClick(event);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof BaseMenu) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof BaseMenu) {
            openMenus.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onRecipeQueueUpdate(RecipeQueueEvent event) {
        Player player = event.getPlayer();
        BaseMenu menu = openMenus.get(player.getUniqueId());
        
        if (menu instanceof QueueUpdateable) {
            ((QueueUpdateable) menu).updateQueueDisplay();
        }
    }

    public void registerMenu(Player player, BaseMenu menu) {
        openMenus.put(player.getUniqueId(), menu);
    }

    public void unregisterMenu(Player player) {
        openMenus.remove(player.getUniqueId());
    }

    public BaseMenu getOpenMenu(Player player) {
        return openMenus.get(player.getUniqueId());
    }

    public boolean hasOpenMenu(Player player) {
        return openMenus.containsKey(player.getUniqueId());
    }

    public void updatePlayerQueueDisplay(Player player) {
        BaseMenu menu = openMenus.get(player.getUniqueId());
        if (menu instanceof QueueUpdateable) {
            ((QueueUpdateable) menu).updateQueueDisplay();
        }
    }
}
