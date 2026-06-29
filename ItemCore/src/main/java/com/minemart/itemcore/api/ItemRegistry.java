package com.minemart.itemcore.api;

import com.minemart.itemcore.item.CustomItem;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public interface ItemRegistry {

    CustomItem getCustomItem(String itemId);

    ItemStack getItemStack(String itemId);

    ItemStack getItemStack(String itemId, int amount);

    boolean hasItem(String itemId);

    Collection<String> getItemIds();

    Collection<CustomItem> getItems();

    Collection<CustomItem> getItemsByCategory(String categoryId);
}
