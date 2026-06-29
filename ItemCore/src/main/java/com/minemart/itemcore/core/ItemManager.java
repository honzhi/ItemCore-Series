package com.minemart.itemcore.core;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.api.ItemRegistry;
import com.minemart.itemcore.api.event.ItemObtainedEvent;
import com.minemart.itemcore.item.CustomItem;
import com.minemart.itemcore.utils.ItemBuilder;
import com.minemart.itemcore.utils.PermissionUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ItemManager implements ItemRegistry {

    private final ItemCore plugin;
    private final Map<String, CustomItem> items;

    public ItemManager(ItemCore plugin) {
        this.plugin = plugin;
        this.items = new LinkedHashMap<>();
    }

    public void loadAll() {
        items.clear();
        if (plugin.getLoaderManager() != null) {
            items.putAll(plugin.getLoaderManager().getItems());
            plugin.getLogger().info("物品管理器加载了 " + items.size() + " 个物品");
        }
    }

    public void reload() {
        loadAll();
    }

    @Override
    public CustomItem getCustomItem(String itemId) {
        if (itemId == null) {
            return null;
        }
        return items.get(itemId.toLowerCase());
    }

    @Override
    public ItemStack getItemStack(String itemId) {
        return getItemStack(itemId, 1);
    }

    @Override
    public ItemStack getItemStack(String itemId, int amount) {
        CustomItem customItem = getCustomItem(itemId);
        if (customItem == null) {
            return null;
        }
        return ItemBuilder.build(customItem, amount);
    }

    @Override
    public boolean hasItem(String itemId) {
        return itemId != null && items.containsKey(itemId.toLowerCase());
    }

    @Override
    public Collection<String> getItemIds() {
        return Collections.unmodifiableCollection(items.keySet());
    }

    @Override
    public Collection<CustomItem> getItems() {
        return Collections.unmodifiableCollection(items.values());
    }

    @Override
    public Collection<CustomItem> getItemsByCategory(String categoryId) {
        if (categoryId == null) {
            return Collections.emptyList();
        }

        List<CustomItem> result = new ArrayList<>();
        String catId = categoryId.toLowerCase();

        for (CustomItem item : items.values()) {
            if (item.hasType() && item.getType().toLowerCase().equals(catId)) {
                result.add(item);
            }
        }

        return result;
    }

    public boolean giveItem(Player player, String itemId, int amount, ItemObtainedEvent.ObtainSource source) {
        if (player == null || itemId == null || amount <= 0) {
            return false;
        }

        CustomItem customItem = getCustomItem(itemId);
        if (customItem == null) {
            return false;
        }

        if (customItem.hasPermission() && !PermissionUtil.hasPermission(player, customItem.getPermission())) {
            return false;
        }

        ItemStack itemStack = ItemBuilder.build(customItem, amount);

        ItemObtainedEvent event = new ItemObtainedEvent(
            player, itemId.toLowerCase(), itemStack, amount, source
        );
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        int finalAmount = event.getAmount();
        if (finalAmount != amount) {
            itemStack.setAmount(finalAmount);
        }

        Map<Integer, ItemStack> leftover = player.getInventory().addItem(itemStack);
        for (ItemStack leftoverItem : leftover.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), leftoverItem);
        }

        return true;
    }

    public boolean giveItem(Player player, String itemId, int amount) {
        return giveItem(player, itemId, amount, ItemObtainedEvent.ObtainSource.API);
    }

    public boolean giveItem(Player player, String itemId) {
        return giveItem(player, itemId, 1);
    }

    public int getItemCount() {
        return items.size();
    }
}
