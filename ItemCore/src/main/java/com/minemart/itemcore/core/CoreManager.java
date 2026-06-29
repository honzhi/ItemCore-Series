package com.minemart.itemcore.core;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.api.ItemCoreAPI;
import com.minemart.itemcore.api.event.ItemObtainedEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CoreManager {

    private final ItemCore plugin;
    private final ItemManager itemManager;
    private final CategoryManager categoryManager;

    public CoreManager(ItemCore plugin) {
        this.plugin = plugin;
        this.itemManager = new ItemManager(plugin);
        this.categoryManager = new CategoryManager(plugin);
    }

    public void initialize() {
        itemManager.loadAll();
        categoryManager.loadAll();

        ItemCoreAPI.setItemRegistry(itemManager);
        ItemCoreAPI.setCategoryRegistry(categoryManager);

        plugin.getLogger().info("核心管理器初始化完成");
    }

    public void reload() {
        itemManager.reload();
        categoryManager.reload();
        plugin.getLogger().info("核心管理器重载完成");
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public CategoryManager getCategoryManager() {
        return categoryManager;
    }

    public org.bukkit.inventory.ItemStack getItemStack(String itemId) {
        return itemManager.getItemStack(itemId);
    }

    public org.bukkit.inventory.ItemStack getItemStack(String itemId, int amount) {
        return itemManager.getItemStack(itemId, amount);
    }

    public boolean giveItem(Player player, String itemId, int amount, ItemObtainedEvent.ObtainSource source) {
        return itemManager.giveItem(player, itemId, amount, source);
    }

    public boolean giveItem(Player player, String itemId, int amount) {
        return itemManager.giveItem(player, itemId, amount);
    }
}
