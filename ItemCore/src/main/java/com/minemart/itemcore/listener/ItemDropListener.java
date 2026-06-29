package com.minemart.itemcore.listener;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.item.CustomItem;
import com.minemart.itemcore.utils.ItemIdentifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class ItemDropListener extends BaseListener {

    public ItemDropListener(ItemCore plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();

        if (!ItemIdentifier.isCustomItem(item)) {
            return;
        }

        CustomItem customItem = ItemIdentifier.getCustomItem(item);
        if (customItem == null) {
            return;
        }

        if (!customItem.isDroppable()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        java.util.List<ItemStack> toRemove = new java.util.ArrayList<>();
        java.util.List<ItemStack> toKeep = new java.util.ArrayList<>();

        for (ItemStack item : event.getDrops()) {
            if (ItemIdentifier.isCustomItem(item)) {
                CustomItem customItem = ItemIdentifier.getCustomItem(item);
                if (customItem != null && customItem.isKeepOnDeath()) {
                    toRemove.add(item);
                    toKeep.add(item);
                }
            }
        }

        for (ItemStack item : toRemove) {
            event.getDrops().remove(item);
        }

        for (ItemStack item : toKeep) {
            player.getInventory().addItem(item);
        }
    }
}
