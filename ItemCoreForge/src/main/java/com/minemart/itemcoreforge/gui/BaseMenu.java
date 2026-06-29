package com.minemart.itemcoreforge.gui;

import com.minemart.itemcoreforge.ItemCoreForge;
import com.minemart.itemcoreforge.utils.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class BaseMenu implements InventoryHolder {

    protected final ItemCoreForge plugin;
    protected final Player player;
    protected final Inventory inventory;
    protected final Map<Integer, Consumer<InventoryClickEvent>> clickHandlers = new HashMap<>();

    public BaseMenu(ItemCoreForge plugin, Player player, String title, int rows) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(this, rows * 9, MessageUtil.toComponent(title));
    }

    public void setItem(int slot, ItemStack item) {
        inventory.setItem(slot, item);
    }

    public void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
        inventory.setItem(slot, item);
        clickHandlers.put(slot, handler);
    }

    public void setClickHandler(int slot, Consumer<InventoryClickEvent> handler) {
        clickHandlers.put(slot, handler);
    }

    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        Consumer<InventoryClickEvent> handler = clickHandlers.get(slot);
        if (handler != null) {
            handler.accept(event);
        }
    }

    public void open() {
        plugin.getGuiListener().registerMenu(player, this);
        player.openInventory(inventory);
    }

    public void close() {
        player.closeInventory();
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
