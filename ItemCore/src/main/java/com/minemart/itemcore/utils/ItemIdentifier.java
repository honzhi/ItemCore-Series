package com.minemart.itemcore.utils;

import com.minemart.itemcore.item.CustomItem;
import com.minemart.itemcore.item.ItemSlot;
import com.minemart.itemcore.util.DurabilityManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ItemIdentifier {

    private ItemIdentifier() {}

    public static boolean isCustomItem(ItemStack itemStack) {
        return ItemBuilder.isCustomItem(itemStack);
    }

    public static String getItemId(ItemStack itemStack) {
        return ItemBuilder.getItemId(itemStack);
    }

    public static CustomItem getCustomItem(ItemStack itemStack) {
        String itemId = getItemId(itemStack);
        if (itemId == null) {
            return null;
        }
        return com.minemart.itemcore.api.ItemCoreAPI.getCustomItem(itemId);
    }

    public static Map<ItemSlot, List<CustomItem>> getEquippedItems(Player player) {
        Map<ItemSlot, List<CustomItem>> result = new EnumMap<>(ItemSlot.class);

        PlayerInventory inv = player.getInventory();

        ItemStack mainHand = inv.getItemInMainHand();
        if (isCustomItem(mainHand) && !DurabilityManager.isBroken(mainHand)) {
            CustomItem item = getCustomItem(mainHand);
            if (item != null && item.canSlot(ItemSlot.MAIN_HAND)) {
                result.computeIfAbsent(ItemSlot.MAIN_HAND, k -> new ArrayList<>()).add(item);
            }
        }

        ItemStack offHand = inv.getItemInOffHand();
        if (isCustomItem(offHand) && !DurabilityManager.isBroken(offHand)) {
            CustomItem item = getCustomItem(offHand);
            if (item != null && item.canSlot(ItemSlot.OFF_HAND)) {
                result.computeIfAbsent(ItemSlot.OFF_HAND, k -> new ArrayList<>()).add(item);
            }
        }

        ItemStack[] armor = inv.getArmorContents();
        ItemSlot[] armorSlots = { ItemSlot.FEET, ItemSlot.LEGS, ItemSlot.CHEST, ItemSlot.HEAD };

        for (int i = 0; i < armor.length && i < armorSlots.length; i++) {
            ItemStack armorItem = armor[i];
            if (armorItem != null && isCustomItem(armorItem) && !DurabilityManager.isBroken(armorItem)) {
                CustomItem item = getCustomItem(armorItem);
                if (item != null && item.canSlot(armorSlots[i])) {
                    result.computeIfAbsent(armorSlots[i], k -> new ArrayList<>()).add(item);
                }
            }
        }

        return result;
    }

    public static List<CustomItem> getEquippedItemsAsList(Player player) {
        List<CustomItem> result = new ArrayList<>();

        Map<ItemSlot, List<CustomItem>> equipped = getEquippedItems(player);
        for (List<CustomItem> items : equipped.values()) {
            result.addAll(items);
        }

        return result;
    }

    public static ItemStack getItemInSlot(Player player, ItemSlot slot) {
        PlayerInventory inv = player.getInventory();

        switch (slot) {
            case MAIN_HAND:
                return inv.getItemInMainHand();
            case OFF_HAND:
                return inv.getItemInOffHand();
            case HEAD:
                return inv.getHelmet();
            case CHEST:
                return inv.getChestplate();
            case LEGS:
                return inv.getLeggings();
            case FEET:
                return inv.getBoots();
            default:
                return null;
        }
    }

    public static CustomItem getCustomItemInSlot(Player player, ItemSlot slot) {
        ItemStack item = getItemInSlot(player, slot);
        return getCustomItem(item);
    }

    public static boolean isItemInHand(Player player, String itemId) {
        if (itemId == null) {
            return false;
        }

        String id = itemId.toLowerCase();
        PlayerInventory inv = player.getInventory();

        ItemStack mainHand = inv.getItemInMainHand();
        String mainId = getItemId(mainHand);
        if (id.equals(mainId)) {
            return true;
        }

        ItemStack offHand = inv.getItemInOffHand();
        String offId = getItemId(offHand);
        if (id.equals(offId)) {
            return true;
        }

        return false;
    }

    public static boolean isWeapon(ItemStack itemStack) {
        if (DurabilityManager.isBroken(itemStack)) return false;
        CustomItem customItem = getCustomItem(itemStack);
        if (customItem != null) {
            return customItem.canSlot(ItemSlot.MAIN_HAND);
        }
        if (itemStack == null) {
            return false;
        }
        Material mat = itemStack.getType();
        String name = mat.name();
        return name.endsWith("_SWORD") || name.endsWith("_AXE");
    }

    public static boolean isArmor(ItemStack itemStack) {
        if (DurabilityManager.isBroken(itemStack)) return false;
        CustomItem customItem = getCustomItem(itemStack);
        if (customItem != null) {
            return customItem.canSlot(ItemSlot.HEAD)
                || customItem.canSlot(ItemSlot.CHEST)
                || customItem.canSlot(ItemSlot.LEGS)
                || customItem.canSlot(ItemSlot.FEET);
        }
        if (itemStack == null) {
            return false;
        }
        return itemStack.getType().isEdible() ? false : itemStack.getType().name().endsWith("_HELMET")
            || itemStack.getType().name().endsWith("_CHESTPLATE")
            || itemStack.getType().name().endsWith("_LEGGINGS")
            || itemStack.getType().name().endsWith("_BOOTS");
    }

    public static int countCustomItems(Player player) {
        int count = 0;
        PlayerInventory inv = player.getInventory();

        for (ItemStack item : inv) {
            if (isCustomItem(item)) {
                count++;
            }
        }

        return count;
    }

    public static List<ItemStack> findCustomItems(Player player, String itemId) {
        if (itemId == null) {
            return java.util.Collections.emptyList();
        }

        String id = itemId.toLowerCase();
        List<ItemStack> result = new ArrayList<>();
        PlayerInventory inv = player.getInventory();

        for (ItemStack item : inv) {
            if (id.equals(getItemId(item))) {
                result.add(item);
            }
        }

        return result;
    }
}
