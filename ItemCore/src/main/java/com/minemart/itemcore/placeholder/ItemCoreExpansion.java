package com.minemart.itemcore.placeholder;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.calculator.AttributeCalculator;
import com.minemart.itemcore.item.attribute.AttributeContainer;
import com.minemart.itemcore.item.attribute.CustomAttribute;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class ItemCoreExpansion extends PlaceholderExpansion {

    private final ItemCore plugin;

    public ItemCoreExpansion(ItemCore plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "itemcore";
    }

    @Override
    @NotNull
    public String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    @NotNull
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        if (params == null || params.isEmpty()) {
            return null;
        }

        if (!offlinePlayer.isOnline()) {
            return "0";
        }

        Player player = offlinePlayer.getPlayer();
        if (player == null) {
            return "0";
        }

        String normalizedParams = params.replace("-", "_").toUpperCase();

        CustomAttribute attr = CustomAttribute.fromConfigKey(normalizedParams);
        if (attr != null) {
            AttributeContainer attrs = AttributeCalculator.calculatePlayerAttributes(player);
            double value;

            if (attr == CustomAttribute.CRIT_DAMAGE) {
                value = AttributeCalculator.getTotalCritDamage(attrs);
            } else if (attr == CustomAttribute.HEALTH || attr == CustomAttribute.MOVEMENT_SPEED) {
                value = attr == CustomAttribute.HEALTH
                    ? AttributeCalculator.calculateHealth(attrs)
                    : AttributeCalculator.calculateMovementSpeed(attrs);
            } else {
                value = attrs.getAttribute(attr);
            }

            return formatValue(value, attr);
        }

        if (normalizedParams.equals("MAX_HEALTH")) {
            AttributeContainer attrs = AttributeCalculator.calculatePlayerAttributes(player);
            return formatNumber(AttributeCalculator.calculateHealth(attrs));
        }

        debug("Unknown placeholder param: " + params);
        return null;
    }

    private String formatValue(double value, CustomAttribute attr) {
        if (attr.isPercentage()) {
            return formatPercent(value);
        }
        return formatNumber(value);
    }

    private String formatNumber(double value) {
        if (value == Math.floor(value) && !Double.isInfinite(value)) {
            return String.valueOf((int) value);
        }
        return String.valueOf(Math.round(value * 10.0) / 10.0);
    }

    private String formatDecimal(double value) {
        return String.valueOf(Math.round(value * 100.0) / 100.0);
    }

    private String formatPercent(double value) {
        if (value == Math.floor(value) && !Double.isInfinite(value)) {
            return (int) value + "%";
        }
        return formatDecimal(value) + "%";
    }

    private void debug(String message) {
        if (plugin.getConfig().getBoolean("debug_mode", false)) {
            plugin.getLogger().log(Level.WARNING, "[PAPI-Debug] " + message);
        }
    }
}
