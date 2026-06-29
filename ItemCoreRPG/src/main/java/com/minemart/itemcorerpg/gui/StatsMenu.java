package com.minemart.itemcorerpg.gui;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.api.ItemCoreAPI;
import com.minemart.itemcore.item.attribute.AttributeContainer;
import com.minemart.itemcore.item.attribute.CustomAttribute;
import com.minemart.itemcore.item.attribute.ElementType;
import com.minemart.itemcore.element.AccumulationManager.AccumulationSnapshot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class StatsMenu {

    private static final int INVENTORY_SIZE = 54;
    private static final String TITLE = "\u00a78\u25a0\u25a0 \u00a76\u73a9\u5bb6\u4fe1\u606f \u00a78\u25a0\u25a0";

    public static void open(Player viewer, Player target) {
        Inventory inv = Bukkit.createInventory(null, INVENTORY_SIZE, TITLE);

        AttributeContainer attrs = ItemCoreAPI.getPlayerAttributes(target);
        if (attrs == null) {
            viewer.sendMessage("\u00a7c\u65e0\u6cd5\u83b7\u53d6\u5c5e\u6027\u6570\u636e");
            return;
        }

        // Border
        ItemStack border = createBorder();
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, border);
            inv.setItem(INVENTORY_SIZE - 9 + i, border);
        }
        int[] borderSlots = {9, 17, 18, 26, 27, 35, 36, 44};
        for (int s : borderSlots) {
            inv.setItem(s, border);
        }

        // Player head (Slot 4)
        inv.setItem(4, createPlayerHead(target));

        // Stat category items (Slots 19-26)
        inv.setItem(19, createCombatStats(attrs));
        inv.setItem(20, createDefenseStats(attrs));
        inv.setItem(21, createCritStats(attrs));
        inv.setItem(22, createSurvivalStats(target, attrs));
        inv.setItem(23, createElementStats(target));
        inv.setItem(24, createAdvancedStats(attrs));
        inv.setItem(25, createDamageBonusStats(attrs));
        inv.setItem(26, createPenetrationStats(attrs));

        // Refresh + Close (Slots 48, 50)
        inv.setItem(48, createRefreshItem());
        inv.setItem(50, createCloseItem());

        GuiListener.trackView(viewer.getUniqueId(), target.getUniqueId());
        viewer.openInventory(inv);
    }

    private static String r(String s) {
        return "\u00a7r" + s;
    }

    private static List<String> lore(List<String> lines) {
        List<String> result = new ArrayList<>();
        for (String line : lines) {
            result.add("\u00a7r" + line);
        }
        return result;
    }

    private static ItemStack createBorder() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("\u00a7r");
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createPlayerHead(Player player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(r("\u00a76\u00a7l" + player.getName()));
            meta.setOwningPlayer(player);
            List<String> l = new ArrayList<>();
            l.add("\u00a77\u5728\u7ebf: " + (player.isOnline() ? "\u00a7a\u25cf" : "\u00a7c\u25cb"));
            l.add("\u00a77\u751f\u547d\u503c: \u00a7c" + String.format("%.1f", player.getHealth())
                + " \u00a77/ \u00a7c" + String.format("%.1f", player.getMaxHealth()));
            l.add("\u00a77\u7b49\u7ea7(\u7ecf\u9a8c): \u00a7e" + player.getLevel());
            l.add("\u00a77\u9965\u997f\u503c: \u00a7e" + String.format("%.0f", player.getSaturation()));
            meta.setLore(lore(l));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createCombatStats(AttributeContainer attrs) {
        ItemStack item = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(r("\u00a7c\u2694 \u6218\u6597\u5c5e\u6027"));
            List<String> l = new ArrayList<>();
            l.add("\u00a78\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501");
            double atk = attrs.getAttribute(CustomAttribute.ATTACK_DAMAGE);
            double aspd = attrs.getAttribute(CustomAttribute.ATTACK_SPEED);
            double range = attrs.getAttribute(CustomAttribute.ATTACK_RANGE);
            double knock = attrs.getAttribute(CustomAttribute.KNOCKBACK);
            l.add("\u00a77\u653b\u51fb\u4f24\u5bb3: \u00a7a" + formatNum(atk));
            double spell = attrs.getAttribute(CustomAttribute.SPELL_POWER);
            l.add("\u00a77\u6cd5\u672f\u5f3a\u5ea6: " + (spell > 0 ? "\u00a7d+" : "\u00a77") + formatNum(spell));
            l.add("\u00a77\u653b\u51fb\u901f\u5ea6: " + (aspd != 0 ? colorCodes(aspd, true) : "\u00a77") + formatNum(aspd));
            l.add("\u00a77\u653b\u51fb\u8303\u56f4: " + (range > 0 ? "\u00a7a+" : "\u00a77") + formatNum(range));
            l.add("\u00a77\u51fb\u9000:     " + (knock > 0 ? "\u00a7a+" : "\u00a77") + formatNum(knock));
            l.add("\u00a78\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501");
            meta.setLore(lore(l));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createDefenseStats(AttributeContainer attrs) {
        ItemStack item = new ItemStack(Material.SHIELD);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(r("\u00a7b\ud83d\udee1 \u9632\u5fa1\u5c5e\u6027"));
            List<String> l = new ArrayList<>();
            l.add("\u00a78\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501");
            double pResist = attrs.getAttribute(CustomAttribute.PHYSICAL_RESIST);
            double sResist = attrs.getAttribute(CustomAttribute.SPELL_RESIST);
            double dReduction = attrs.getAttribute(CustomAttribute.DAMAGE_REDUCTION);
            l.add("\u00a77\u7269\u7406\u6297\u6027: \u00a7a" + formatNum(pResist)
                + " \u00a77(" + formatNum(calcReductionPercent(pResist)) + "%)");
            l.add("\u00a77\u6cd5\u672f\u6297\u6027: \u00a7b" + formatNum(sResist)
                + " \u00a77(" + formatNum(calcReductionPercent(sResist)) + "%)");
            l.add("\u00a77\u4f24\u5bb3\u51cf\u514d: " + (dReduction > 0 ? "\u00a7a+" : "\u00a77")
                + formatNum(dReduction * 100) + "%");
            l.add("\u00a78\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501");
            meta.setLore(lore(l));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createCritStats(AttributeContainer attrs) {
        ItemStack item = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(r("\u00a74\u26a1 \u66b4\u51fb\u5c5e\u6027"));
            List<String> l = new ArrayList<>();
            l.add("\u00a78\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501");
            double critChance = attrs.getAttribute(CustomAttribute.CRIT_CHANCE);
            double critDmg = attrs.getAttribute(CustomAttribute.CRIT_DAMAGE);
            double totalCritDmg = com.minemart.itemcore.calculator.AttributeCalculator.getTotalCritDamage(attrs);
            l.add("\u00a77\u66b4\u51fb\u51e0\u7387: " + (critChance > 0 ? "\u00a7c+" : "\u00a77") + formatNum(critChance) + "%");
            l.add("\u00a77\u66b4\u51fb\u4f24\u5bb3: \u00a7c" + formatNum(totalCritDmg) + "%"
                + (critDmg > 0 ? " \u00a77(\u00a7c+" + formatNum(critDmg) + "%)" : ""));
            l.add("\u00a78\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501");
            meta.setLore(lore(l));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createSurvivalStats(Player player, AttributeContainer attrs) {
        ItemStack item = new ItemStack(Material.APPLE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(r("\u00a7a\u2764 \u751f\u5b58\u5c5e\u6027"));
            List<String> l = new ArrayList<>();
            l.add("\u00a78\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501");
            double health = attrs.getAttribute(CustomAttribute.HEALTH);
            double maxHp = 20.0 + health;
            l.add("\u00a77\u751f\u547d\u503c: \u00a7c" + formatNum(maxHp));
            double moveSpeed = 0.1 + attrs.getAttribute(CustomAttribute.MOVEMENT_SPEED);
            l.add("\u00a77\u79fb\u52a8\u901f\u5ea6: \u00a7e" + formatNum(moveSpeed));
            double regen = attrs.getAttribute(CustomAttribute.REGENERATION);
            l.add("\u00a77\u751f\u547d\u6062\u590d: " + (regen > 0 ? "\u00a7a+" : "\u00a77") + formatNum(regen) + " \u00a77/\u79d2");
            double armor = attrs.getAttribute(CustomAttribute.ARMOR);
            l.add("\u00a77\u62a4\u7532: \u00a77" + formatNum(armor));
            double luck = attrs.getAttribute(CustomAttribute.LUCK);
            l.add("\u00a77\u5e78\u8fd0\u503c: " + (luck > 0 ? "\u00a7e+" : "\u00a77") + formatNum(luck));
            l.add("\u00a78\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501");
            meta.setLore(lore(l));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createElementStats(Player player) {
        ItemStack item = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(r("\u00a7d\u2726 \u5143\u7d20\u5c5e\u6027"));
            List<String> l = new ArrayList<>();
            l.add("\u00a78\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501");
            var registry = ItemCore.getElementRegistry();
            if (registry != null) {
                AttributeContainer attrs = ItemCoreAPI.getPlayerAttributes(player);
                if (attrs != null) {
                    for (ElementType element : registry.getAll()) {
                        if (element == ElementType.NONE) continue;
                        String color = getElementColorCode(element);
                        double mastery = attrs.getElementMastery(element);
                        double resist = attrs.getElementResistance(element);
                        l.add("\u00a77" + element.getDisplayName() + "\u7cbe\u901a: " + (mastery > 0 ? color + "+" : "\u00a77") + formatNum(mastery));
                        l.add("\u00a77" + element.getDisplayName() + "\u6297\u6027: " + (resist > 0 ? color + "+" : "\u00a77") + formatNum(resist) + "%");
                        // Accumulation progress
                        AccumulationSnapshot progress = ItemCore.getInstance().getAccumulationManager()
                            .getProgress(player, element);
                        if (progress.getValue() > 0) {
                            double pct = progress.getProgressPercent() * 100;
                            l.add(" \u00a77\u79ef\u7d2f: " + color + formatNum(pct) + "%");
                        }
                    }
                }
            }
            l.add("\u00a78\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501");
            meta.setLore(lore(l));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createAdvancedStats(AttributeContainer attrs) {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(r("\u00a75\u2728 \u8fdb\u9636\u5c5e\u6027"));
            List<String> l = new ArrayList<>();
            l.add("\u00a78\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501");
            double aForce = attrs.getAttribute(CustomAttribute.ADAPTIVE_FORCE);
            double luck = attrs.getAttribute(CustomAttribute.LUCK);
            double atk = attrs.getAttribute(CustomAttribute.ATTACK_DAMAGE);
            double spell = attrs.getAttribute(CustomAttribute.SPELL_POWER);
            String aForceDisplay;
            if (aForce <= 0) {
                aForceDisplay = "\u00a77" + formatNum(aForce);
            } else {
                double conversion = 1.0;
                try {
                    conversion = ItemCore.getInstance().getAttributesConfig().getAdaptiveForceAttackConversion();
                } catch (Exception ignored) {}
                double bonusAtk = aForce * conversion;
                if (atk >= spell) {
                    aForceDisplay = "\u00a7a+" + formatNum(aForce) + " \u00a77(\u00a7a+" + formatNum(bonusAtk) + " \u653b\u51fb)";
                } else {
                    double spellConv = 1.0;
                    try {
                        spellConv = ItemCore.getInstance().getAttributesConfig().getAdaptiveForceSpellConversion();
                    } catch (Exception ignored) {}
                    double bonusSpell = aForce * spellConv;
                    aForceDisplay = "\u00a7d+" + formatNum(aForce) + " \u00a77(\u00a7d+" + formatNum(bonusSpell) + " \u6cd5\u5f3a)";
                }
            }
            l.add("\u00a77\u9002\u5e94\u4e4b\u529b: " + aForceDisplay);
            l.add("\u00a77\u5e78\u8fd0\u503c:   " + (luck > 0 ? "\u00a7e+" : "\u00a77") + formatNum(luck));
            l.add("\u00a78\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501");
            meta.setLore(lore(l));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createDamageBonusStats(AttributeContainer attrs) {
        ItemStack item = new ItemStack(Material.BOW);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(r("\u00a7a\ud83c\udff9 \u4f24\u5bb3\u52a0\u6210"));
            List<String> l = new ArrayList<>();
            l.add("\u00a78\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501");
            double phys = attrs.getAttribute(CustomAttribute.PHYSICAL_DAMAGE);
            double proj = attrs.getAttribute(CustomAttribute.PROJECTILE_DAMAGE);
            double spell = attrs.getAttribute(CustomAttribute.SPELL_DAMAGE);
            l.add("\u00a77\u7269\u7406\u52a0\u6210: " + (phys > 0 ? "\u00a7a+" : "\u00a77") + formatNum(phys) + "%");
            l.add("\u00a77\u5c04\u5f39\u52a0\u6210: " + (proj > 0 ? "\u00a7a+" : "\u00a77") + formatNum(proj) + "%");
            l.add("\u00a77\u6cd5\u672f\u52a0\u6210: " + (spell > 0 ? "\u00a7d+" : "\u00a77") + formatNum(spell) + "%");
            l.add("\u00a78\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501");
            meta.setLore(lore(l));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createPenetrationStats(AttributeContainer attrs) {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(r("\u00a7f\ud83d\udccc \u7a7f\u900f\u5c5e\u6027"));
            List<String> l = new ArrayList<>();
            l.add("\u00a78\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501");
            double pFlat = attrs.getAttribute(CustomAttribute.PHYSICAL_PENETRATION);
            double pPct = attrs.getAttribute(CustomAttribute.PHYSICAL_PENETRATION_PERCENT);
            double sFlat = attrs.getAttribute(CustomAttribute.SPELL_PENETRATION);
            double sPct = attrs.getAttribute(CustomAttribute.SPELL_PENETRATION_PERCENT);
            l.add("\u00a77\u7269\u7406\u7a7f\u900f(\u56fa\u5b9a): \u00a7c" + formatNum(pFlat));
            l.add("\u00a77\u7269\u7406\u7a7f\u900f(\u767e\u5206\u6bd4): \u00a7c" + formatNum(pPct) + "%");
            l.add("\u00a77\u6cd5\u672f\u7a7f\u900f(\u56fa\u5b9a): \u00a7d" + formatNum(sFlat));
            l.add("\u00a77\u6cd5\u672f\u7a7f\u900f(\u767e\u5206\u6bd4): \u00a7d" + formatNum(sPct) + "%");
            l.add("\u00a78\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501");
            meta.setLore(lore(l));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createRefreshItem() {
        ItemStack item = new ItemStack(Material.FEATHER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(r("\u00a7a\u21bb \u5237\u65b0"));
            List<String> l = new ArrayList<>();
            l.add("\u00a77\u70b9\u51fb\u5237\u65b0\u5f53\u524d\u6570\u636e");
            meta.setLore(lore(l));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createCloseItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(r("\u00a7c\u2715 \u5173\u95ed"));
            List<String> l = new ArrayList<>();
            l.add("\u00a77\u70b9\u51fb\u5173\u95ed\u754c\u9762");
            meta.setLore(lore(l));
            item.setItemMeta(meta);
        }
        return item;
    }

    // Utility methods
    private static String formatNum(double value) {
        return String.format("%.1f", value);
    }

    private static String colorCodes(double value, boolean isPositiveGood) {
        if (value == 0) return "\u00a77";
        if (isPositiveGood) return value > 0 ? "\u00a7a" : "\u00a7c";
        return value > 0 ? "\u00a7c" : "\u00a7a";
    }

    private static double calcReductionPercent(double resist) {
        if (resist <= 0) return 0;
        return resist / (resist + 100) * 100;
    }

    private static String getElementColorCode(ElementType element) {
        if (element == ElementType.LIUHUO || element == ElementType.FIRE) return "\u00a7c";
        if (element == ElementType.HANSHUANG || element == ElementType.ICE) return "\u00a7b";
        if (element == ElementType.LEIZHE || element == ElementType.THUNDER) return "\u00a7e";
        return "\u00a77";
    }
}