package com.minemart.itemcorelevel.provider;

import com.minemart.itemcore.api.AttributeProvider;
import com.minemart.itemcore.item.attribute.AttributeContainer;
import com.minemart.itemcore.item.attribute.CustomAttribute;
import com.minemart.itemcorelevel.ItemCoreLevel;
import com.minemart.itemcorelevel.PlayerData;
import com.minemart.itemcorelevel.config.ConfigManager;
import org.bukkit.entity.Player;
import java.util.Map;

public class LevelAttributeProvider implements AttributeProvider {

    private final ItemCoreLevel plugin;
    private final ConfigManager configManager;

    public LevelAttributeProvider(ItemCoreLevel plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    @Override
    public AttributeContainer getAttributes(Player player) {
        PlayerData data = plugin.getLevelManager().getPlayerData(player.getUniqueId());
        if (data == null) return null;

        int level = data.getLevel();
        AttributeContainer container = new AttributeContainer();

        Map<CustomAttribute, Double> generalRewards = configManager.getGeneralRewards();
        for (Map.Entry<CustomAttribute, Double> entry : generalRewards.entrySet()) {
            container.addAttribute(entry.getKey(), entry.getValue() * level);
        }

        Map<Integer, Map<CustomAttribute, Double>> specificRewards = configManager.getSpecificRewards();
        for (Map.Entry<Integer, Map<CustomAttribute, Double>> entry : specificRewards.entrySet()) {
            if (level >= entry.getKey()) {
                for (Map.Entry<CustomAttribute, Double> attrEntry : entry.getValue().entrySet()) {
                    container.addAttribute(attrEntry.getKey(), attrEntry.getValue());
                }
            }
        }

        return container.isEmpty() ? null : container;
    }
}