package com.minemart.itemcorerpg.listener;

import com.minemart.itemcore.event.ItemCoreDamageEvent;
import com.minemart.itemcore.item.attribute.ElementType;
import com.minemart.itemcorerpg.ItemCoreRPG;
import com.minemart.itemcorerpg.manager.DamageDisplayManager;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.text.DecimalFormat;
import java.util.Map;

public class CombatListener implements Listener {

    private final ItemCoreRPG plugin;
    private final DamageDisplayManager displayManager;

    public CombatListener(ItemCoreRPG plugin) {
        this.plugin = plugin;
        this.displayManager = new DamageDisplayManager(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemCoreDamage(ItemCoreDamageEvent event) {
        if (!plugin.getConfigManager().isDamageDisplayEnabled()) {
            return;
        }

        double damage = event.getTotalDamage();
        if (damage < plugin.getConfigManager().getMinDamage()) {
            return;
        }

        String damageType = getDamageType(event);
        if (plugin.getConfig().getBoolean("debug", false)) {
            plugin.getLogger().info("[ICRPG_Damage] type=" + damageType + " isSpell=" + event.isSpellDamage() + " isPhys=" + event.isPhysicalDamage() + " isProj=" + event.isProjectileDamage() + " dmg=" + damage);
        }
        boolean isCrit = event.isCrit();
        
        String damageText = formatDamage(damage, damageType, isCrit);
        
        if (isCrit && plugin.getConfigManager().showCritIndicator()) {
            String indicatorText = formatCritIndicator();
            displayManager.showDamage(event.getDefender(), indicatorText, 0.3);
        }
        
        displayManager.showDamage(event.getDefender(), damageText);
    }

    private String getDamageType(ItemCoreDamageEvent event) {
        // 元素优先：有元素时返回元素 ID
        ElementType element = event.getElement();
        if (element != null && element != ElementType.NONE) {
            return element.getId().toLowerCase();
        }
        // 无元素时按照伤害类型
        if (event.isSpellDamage()) {
            return "spell";
        }
        return "physical";
    }

    private String formatDamage(double damage, String damageType, boolean isCrit) {
        String displayType = isCrit ? "crit" : damageType;
        String color = plugin.getConfigManager().getColor(displayType);
        String icon = plugin.getConfigManager().getIcon(displayType);
        
        String decimalFormat = plugin.getConfigManager().getDecimalFormat();
        DecimalFormat df = new DecimalFormat(decimalFormat);
        String formattedValue = df.format(damage);

        if (plugin.getConfigManager().isCustomFontEnabled()) {
            formattedValue = convertToCustomFont(formattedValue, displayType);
        }

        String format = plugin.getConfigManager().getFormat(displayType);
        String result = format.replace("{icon}", icon)
                              .replace("{value}", formattedValue);

        return ChatColor.translateAlternateColorCodes('&', color + result);
    }

    private String formatCritIndicator() {
        String indicator = plugin.getConfigManager().getCritIndicator();
        String color = plugin.getConfigManager().getCritIndicatorColor();
        return ChatColor.translateAlternateColorCodes('&', color + indicator);
    }

    private String convertToCustomFont(String value, String type) {
        Map<String, String> fontMap = plugin.getConfigManager().getFontMap(type);

        StringBuilder sb = new StringBuilder();
        for (char c : value.toCharArray()) {
            String key = String.valueOf(c);
            if (c == '.') {
                key = "dot";
            } else if (c == '-') {
                key = "inter";
            }

            String replacement = fontMap.getOrDefault(key, String.valueOf(c));
            sb.append(replacement);
        }
        return sb.toString();
    }
}