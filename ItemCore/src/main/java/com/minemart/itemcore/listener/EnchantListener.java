package com.minemart.itemcore.listener;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.utils.ItemBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class EnchantListener extends BaseListener {

    public EnchantListener(ItemCore plugin) {
        super(plugin);
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        ItemMeta meta = event.getItem().getItemMeta();
        if (meta == null) return;

        if (meta.getPersistentDataContainer().getOrDefault(ItemBuilder.DISABLE_ENCHANTING_KEY, PersistentDataType.INTEGER, 0) == 1) {
            event.setCancelled(true);
        }
    }
}
