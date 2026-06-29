package com.minemart.itemcoretrinkets.calculator;

import com.minemart.itemcore.api.ItemCoreAPI;
import com.minemart.itemcore.item.CustomItem;
import com.minemart.itemcore.item.attribute.AttributeContainer;
import com.minemart.itemcoretrinkets.ItemCoreTrinkets;
import com.minemart.itemcoretrinkets.api.TrinketSlot;
import com.minemart.itemcoretrinkets.core.PlayerTrinketData;
import org.bukkit.entity.Player;

import java.util.Map;

public class AttributeCalculator {

    private final ItemCoreTrinkets plugin;

    public AttributeCalculator(ItemCoreTrinkets plugin) {
        this.plugin = plugin;
    }

    /**
     * 计算玩家所有已装备饰品的总属性
     * 此方法由 AttributeProvider 调用，ItemCore 会在计算总属性时纳入结果
     */
    public AttributeContainer calculatePlayerTrinketAttributes(Player player) {
        AttributeContainer result = new AttributeContainer();
        PlayerTrinketData data = plugin.getTrinketManager().getPlayerData(player);

        for (Map.Entry<String, String> entry : data.getEquippedTrinkets().entrySet()) {
            String slotId = entry.getKey();
            String itemId = entry.getValue();

            // 跳过未解锁槽位的饰品属性
            TrinketSlot slot = plugin.getTrinketManager().getSlot(slotId);
            if (slot == null || !slot.canUse(player)) {
                continue;
            }

            CustomItem item = ItemCoreAPI.getCustomItem(itemId);
            if (item != null && item.hasAttributes()) {
                result.merge(item.getAttributes());
            }
        }

        return result;
    }
}