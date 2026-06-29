package com.minemart.itemcoretrinkets.config;

import com.minemart.itemcoretrinkets.ItemCoreTrinkets;
import com.minemart.itemcoretrinkets.api.TrinketSlot;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class SlotLoader {

    private final ItemCoreTrinkets plugin;
    private final Map<String, TrinketSlot> slots;

    public SlotLoader(ItemCoreTrinkets plugin) {
        this.plugin = plugin;
        this.slots = new HashMap<>();
    }

    public void loadSlots() {
        slots.clear();
        ConfigurationSection slotsSection = plugin.getConfigManager().getSlotsConfig().getConfigurationSection("slots");

        if (slotsSection == null) {
            plugin.getLogger().warning("未找到槽位配置");
            return;
        }

        for (String slotId : slotsSection.getKeys(false)) {
            ConfigurationSection slotSection = slotsSection.getConfigurationSection(slotId);
            if (slotSection == null) continue;

            String type = slotSection.getString("type", "trinket");

            // 解析 require 条件
            String requiredPermission = null;
            int requiredLevel = 0;
            if (slotSection.contains("require")) {
                ConfigurationSection requireSection = slotSection.getConfigurationSection("require");
                if (requireSection != null) {
                    requiredPermission = requireSection.getString("permission");
                    requiredLevel = requireSection.getInt("level", 0);
                }
            }

            TrinketSlot slot = new TrinketSlot(slotId, type, requiredPermission, requiredLevel);
            slots.put(slotId, slot);

            if (plugin.getConfigManager().isDebugMode()) {
                StringBuilder debug = new StringBuilder("[Debug] 加载槽位: " + slotId + " (类型: " + type);
                if (requiredPermission != null) debug.append(", 权限: ").append(requiredPermission);
                if (requiredLevel > 0) debug.append(", 等级: ").append(requiredLevel);
                debug.append(")");
                plugin.getLogger().info(debug.toString());
            }
        }

        plugin.getLogger().info("成功加载 " + slots.size() + " 个饰品槽位");
    }

    public Map<String, TrinketSlot> getSlots() {
        return new HashMap<>(slots);
    }

    public TrinketSlot getSlot(String slotId) {
        return slots.get(slotId);
    }

    public boolean hasSlot(String slotId) {
        return slots.containsKey(slotId);
    }

    public int getSlotCount() {
        return slots.size();
    }
}