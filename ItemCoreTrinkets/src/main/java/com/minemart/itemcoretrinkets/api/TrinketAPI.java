
package com.minemart.itemcoretrinkets.api;

import com.minemart.itemcore.api.ItemCoreAPI;
import com.minemart.itemcore.item.CustomItem;
import com.minemart.itemcore.item.attribute.AttributeContainer;
import com.minemart.itemcoretrinkets.ItemCoreTrinkets;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;

public class TrinketAPI {

    private static ItemCoreTrinkets plugin;

    public static void setPlugin(ItemCoreTrinkets plugin) {
        if (TrinketAPI.plugin == null) {
            TrinketAPI.plugin = plugin;
        }
    }

    public static Map<String, CustomItem> getEquippedTrinkets(Player player) {
        if (plugin == null) return java.util.Collections.emptyMap();
        return plugin.getTrinketManager().getEquippedTrinkets(player);
    }

    public static boolean equipTrinket(Player player, String slotId, ItemStack item) {
        if (plugin == null) return false;
        return plugin.getTrinketManager().equipTrinket(player, slotId, item);
    }

    public static ItemStack unequipTrinket(Player player, String slotId) {
        if (plugin == null) return null;
        String itemId = plugin.getTrinketManager().getPlayerData(player).getEquippedTrinket(slotId);
        if (itemId != null) {
            plugin.getTrinketManager().unequipTrinket(player, slotId);
            return ItemCoreAPI.getItemStack(itemId);
        }
        return null;
    }

    public static TrinketSlot getSlot(String slotId) {
        if (plugin == null) return null;
        return plugin.getTrinketManager().getSlot(slotId);
    }

    public static Collection<TrinketSlot> getAllSlots() {
        if (plugin == null) return java.util.Collections.emptyList();
        return plugin.getTrinketManager().getAllSlots().values();
    }

    public static AttributeContainer getTrinketAttributes(Player player) {
        if (plugin == null) return new AttributeContainer();
        return plugin.getAttributeCalculator().calculatePlayerTrinketAttributes(player);
    }
}
