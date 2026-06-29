package com.minemart.itemcore.listener;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.utils.ItemBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class AnvilListener extends BaseListener {

    public AnvilListener(ItemCore plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        // 检查输入物品(slot 0)而非结果物品
        ItemStack input = event.getInventory().getItem(0);

        if (input == null || !input.hasItemMeta()) return;

        ItemMeta meta = input.getItemMeta();
        if (meta == null) return;

        // 检查是否禁止铁砧修复
        if (meta.getPersistentDataContainer().getOrDefault(ItemBuilder.DISABLE_ANVIL_REPAIR_KEY, PersistentDataType.INTEGER, 0) == 1) {
            event.setResult(null);
            event.getInventory().setMaximumRepairCost(999);
            return;
        }

        // 检查是否禁止附魔（铁砧附魔书）
        if (meta.getPersistentDataContainer().getOrDefault(ItemBuilder.DISABLE_ENCHANTING_KEY, PersistentDataType.INTEGER, 0) == 1) {
            ItemStack secondSlot = event.getInventory().getItem(1);
            if (secondSlot != null && secondSlot.getType().name().endsWith("ENCHANTED_BOOK")) {
                event.setResult(null);
                event.getInventory().setMaximumRepairCost(999);
            }
        }
    }
}
