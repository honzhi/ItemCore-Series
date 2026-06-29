package com.minemart.itemcoretrinkets.core;

import com.minemart.itemcore.api.ItemCoreAPI;
import com.minemart.itemcore.item.CustomItem;
import com.minemart.itemcoretrinkets.ItemCoreTrinkets;
import com.minemart.itemcoretrinkets.api.TrinketSlot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TrinketManager {

    private final ItemCoreTrinkets plugin;
    private final Map<UUID, PlayerTrinketData> playerDataMap;

    public TrinketManager(ItemCoreTrinkets plugin) {
        this.plugin = plugin;
        this.playerDataMap = new HashMap<>();
    }

    public void loadPlayerData(Player player) {
        PlayerTrinketData data = plugin.getDataStorage().loadPlayerData(player.getUniqueId());
        playerDataMap.put(player.getUniqueId(), data);

        if (plugin.getConfigManager().isDebugMode()) {
            plugin.getLogger().info("[Debug] 加载玩家饰品数据: " + player.getName() + " - " + data.getEquippedTrinkets().size() + " 个饰品");
        }
    }

    public void unloadPlayerData(Player player) {
        PlayerTrinketData data = playerDataMap.remove(player.getUniqueId());
        if (data != null) {
            plugin.getDataStorage().savePlayerData(data);
        }
    }

    public PlayerTrinketData getPlayerData(Player player) {
        return playerDataMap.computeIfAbsent(player.getUniqueId(),
            uuid -> plugin.getDataStorage().loadPlayerData(uuid));
    }

    public PlayerTrinketData getPlayerData(UUID playerId) {
        return playerDataMap.get(playerId);
    }

    public TrinketSlot getSlot(String slotId) {
        return plugin.getSlotLoader().getSlot(slotId);
    }

    public Map<String, TrinketSlot> getAllSlots() {
        return plugin.getSlotLoader().getSlots();
    }

    public boolean hasSlot(String slotId) {
        return plugin.getSlotLoader().hasSlot(slotId);
    }

    public void reloadSlots() {
        plugin.getSlotLoader().loadSlots();
    }

    public boolean canEquip(Player player, String slotId, CustomItem item) {
        TrinketSlot slot = getSlot(slotId);
        if (slot == null || !slot.canUse(player)) {
            return false;
        }
        return item.hasType() && item.getType().equalsIgnoreCase(slot.getType());
    }

    public boolean canEquip(Player player, String slotId, ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        CustomItem item = ItemCoreAPI.getCustomItem(
            com.minemart.itemcore.utils.ItemIdentifier.getItemId(itemStack)
        );
        return item != null && canEquip(player, slotId, item);
    }

    public boolean equipTrinket(Player player, String slotId, String itemId) {
        TrinketSlot slot = getSlot(slotId);
        if (slot == null || !slot.canUse(player)) {
            return false;
        }

        if (itemId == null || itemId.isEmpty()) {
            unequipTrinket(player, slotId);
            return true;
        }

        CustomItem item = ItemCoreAPI.getCustomItem(itemId);
        if (item == null) {
            return false;
        }

        if (!canEquip(player, slotId, item)) {
            return false;
        }

        PlayerTrinketData data = getPlayerData(player);
        data.equipTrinket(slotId, itemId);

        ItemCoreAPI.refreshPlayerAttributes(player);

        if (plugin.getConfigManager().isDebugMode()) {
            plugin.getLogger().info("[Debug] 玩家 " + player.getName() + " 在槽位 " + slotId + " 装备了饰品 " + itemId);
        }

        return true;
    }

    public boolean equipTrinket(Player player, String slotId, ItemStack itemStack) {
        if (itemStack == null) {
            return unequipTrinket(player, slotId);
        }

        String itemId = com.minemart.itemcore.utils.ItemIdentifier.getItemId(itemStack);
        if (itemId == null) {
            return false;
        }

        return equipTrinket(player, slotId, itemId);
    }

    public boolean unequipTrinket(Player player, String slotId) {
        PlayerTrinketData data = getPlayerData(player);
        String oldItemId = data.getEquippedTrinket(slotId);

        if (oldItemId != null) {
            data.unequipTrinket(slotId);

            ItemCoreAPI.refreshPlayerAttributes(player);

            if (plugin.getConfigManager().isDebugMode()) {
                plugin.getLogger().info("[Debug] 玩家 " + player.getName() + " 从槽位 " + slotId + " 卸下了饰品 " + oldItemId);
            }
            return true;
        }
        return false;
    }

    public ItemStack getEquippedItem(Player player, String slotId) {
        PlayerTrinketData data = getPlayerData(player);
        String itemId = data.getEquippedTrinket(slotId);
        if (itemId == null) {
            return null;
        }
        return ItemCoreAPI.getItemStack(itemId);
    }

    public Map<String, CustomItem> getEquippedTrinkets(Player player) {
        Map<String, CustomItem> result = new HashMap<>();
        PlayerTrinketData data = getPlayerData(player);

        for (Map.Entry<String, String> entry : data.getEquippedTrinkets().entrySet()) {
            String slotId = entry.getKey();
            TrinketSlot slot = getSlot(slotId);
            if (slot != null && !slot.canUse(player)) {
                continue;
            }
            CustomItem item = ItemCoreAPI.getCustomItem(entry.getValue());
            if (item != null) {
                result.put(entry.getKey(), item);
            }
        }
        return result;
    }

    public void saveAll() {
        for (PlayerTrinketData data : playerDataMap.values()) {
            plugin.getDataStorage().savePlayerData(data);
        }

        if (plugin.getConfigManager().isDebugMode()) {
            plugin.getLogger().info("[Debug] 已保存 " + playerDataMap.size() + " 位玩家的饰品数据");
        }
    }
}