package com.minemart.itemcore.listener;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.api.event.ItemObtainedEvent;
import com.minemart.itemcore.item.CustomItem;
import com.minemart.itemcore.item.ItemSlot;
import com.minemart.itemcore.utils.ItemIdentifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ItemInteractListener extends BaseListener {

    public ItemInteractListener(ItemCore plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        EquipmentSlot hand = event.getHand();

        if (hand == null) {
            return;
        }

        ItemStack item = hand == EquipmentSlot.HAND
            ? player.getInventory().getItemInMainHand()
            : player.getInventory().getItemInOffHand();

        if (!ItemIdentifier.isCustomItem(item)) {
            return;
        }

        CustomItem customItem = ItemIdentifier.getCustomItem(item);
        if (customItem == null) {
            return;
        }

        // 权限检查：如果是护甲（可装备到护甲槽），不阻止右键穿戴
        boolean isArmor = customItem.canSlot(ItemSlot.HEAD) || customItem.canSlot(ItemSlot.CHEST)
            || customItem.canSlot(ItemSlot.LEGS) || customItem.canSlot(ItemSlot.FEET);

        if (customItem.hasPermission() && !com.minemart.itemcore.utils.PermissionUtil.hasPermission(player, customItem.getPermission())) {
            if (isArmor && event.getAction().isRightClick()) {
                // 护甲右键穿戴：不取消事件，让装备流程继续
                return;
            }
            event.setCancelled(true);
            return;
        }

        if (!customItem.isRightClickable() && (event.getAction().isRightClick())) {
            // 护甲右键穿戴不被视为"不可右键点击"
            if (!isArmor) {
                event.setCancelled(true);
                return;
            }
        }

        if (!customItem.isLeftClickable() && (event.getAction().isLeftClick())) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        InventoryType.SlotType slotType = event.getSlotType();
        if (slotType == InventoryType.SlotType.ARMOR) {
            if (clickedItem != null && ItemIdentifier.isCustomItem(clickedItem)) {
                CustomItem customItem = ItemIdentifier.getCustomItem(clickedItem);
                if (customItem != null && !customItem.isDroppable()) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        if (clickedItem != null && ItemIdentifier.isCustomItem(clickedItem)) {
            CustomItem customItem = ItemIdentifier.getCustomItem(clickedItem);
            if (customItem != null && !customItem.isClickable()) {
                event.setCancelled(true);
            }
        }
    }
}