package com.minemart.itemcoremythic.placeholder;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.calculator.AttributeCalculator;
import com.minemart.itemcore.item.attribute.AttributeContainer;
import com.minemart.itemcore.item.attribute.CustomAttribute;
import com.minemart.itemcoremythic.ItemCoreMythic;
import com.minemart.itemcoremythic.util.DebugLogger;
import io.lumine.mythic.api.skills.placeholders.PlaceholderManager;
import io.lumine.mythic.core.skills.placeholders.Placeholder;
import org.bukkit.entity.Player;
import com.minemart.itemcore.api.ItemCoreAPI;
import com.minemart.itemcore.item.attribute.ElementType;

public class ICPlaceholderExpansion {

    private final ItemCoreMythic plugin;

    public ICPlaceholderExpansion(ItemCoreMythic plugin) {
        this.plugin = plugin;
    }

    public void register() {
        DebugLogger.debug("ICPlaceholderExpansion", "Registering IC placeholders via PlaceholderManager");
        PlaceholderManager pm = plugin.getMythicMobs().getPlaceholderManager();

        for (CustomAttribute attr : CustomAttribute.values()) {
            String key = "ic." + attr.getConfigKey().toLowerCase();

            pm.register(key, Placeholder.entity((abstractEntity, placeholderName) -> {
                try {
                    if (abstractEntity == null) {
                        return "0";
                    }
                    if (abstractEntity.getBukkitEntity() instanceof Player player) {
                        AttributeContainer attrs = AttributeCalculator.calculatePlayerAttributes(player);
                        double value = attrs.getAttribute(attr);
                        DebugLogger.debug("ICPlaceholderExpansion", key + " = " + value);
                        return String.valueOf(value);
                    }
                } catch (Exception e) {
                    DebugLogger.debug("ICPlaceholderExpansion", "Error resolving " + placeholderName + ": " + e.getMessage());
                }
                return "0";
            }));

            DebugLogger.debug("ICPlaceholderExpansion", "Registered: " + key);
        }

        // Element mastery/resistance placeholders (not in CustomAttribute enum)
        String[][] elements = {{"LIUHUO", "liuhuo"}, {"HANSHUANG", "hanshuang"}, {"LEIZHE", "leizhe"}};
        int elementCount = elements.length * 2;
        for (String[] element : elements) {
            String eid = element[0];
            String ekey = element[1];

            pm.register("ic.element_mastery." + ekey, Placeholder.entity((entity, name) -> {
                try {
                    if (entity != null && entity.getBukkitEntity() instanceof Player p) {
                        return String.valueOf(ItemCoreAPI.getElementMastery(p, new ElementType(eid, "")));
                    }
                } catch (Exception ex) {
                    DebugLogger.debug("ICPlaceholderExpansion", "Error: " + ex.getMessage());
                }
                return "0";
            }));

            pm.register("ic.element_resist." + ekey, Placeholder.entity((entity, name) -> {
                try {
                    if (entity != null && entity.getBukkitEntity() instanceof Player p) {
                        return String.valueOf(ItemCoreAPI.getElementResistance(p, new ElementType(eid, "")));
                    }
                } catch (Exception ex) {
                    DebugLogger.debug("ICPlaceholderExpansion", "Error: " + ex.getMessage());
                }
                return "0";
            }));
        }
        plugin.getLogger().info("Registered " + (CustomAttribute.values().length + elementCount) + " IC placeholders");
    }
}
