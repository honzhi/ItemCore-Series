package com.minemart.itemcore.util;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.utils.ItemBuilder;
import com.minemart.itemcore.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DurabilityManager {

    private static ItemCore plugin;

    public static void init(ItemCore plugin) {
        DurabilityManager.plugin = plugin;
    }

    /**
     * 获取物品当前耐久
     */
    public static int getDurability(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return -1;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return -1;
        if (!meta.getPersistentDataContainer().has(ItemBuilder.DURABILITY_KEY, PersistentDataType.INTEGER)) {
            return -1;
        }
        return meta.getPersistentDataContainer().get(ItemBuilder.DURABILITY_KEY, PersistentDataType.INTEGER);
    }

    /**
     * 获取物品最大耐久
     */
    public static int getMaxDurability(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return -1;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return -1;
        if (!meta.getPersistentDataContainer().has(ItemBuilder.MAX_DURABILITY_KEY, PersistentDataType.INTEGER)) {
            return -1;
        }
        return meta.getPersistentDataContainer().get(ItemBuilder.MAX_DURABILITY_KEY, PersistentDataType.INTEGER);
    }

    /**
     * 设置物品当前耐久
     */
    public static void setDurability(ItemStack item, int durability) {
        if (item == null || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        meta.getPersistentDataContainer().set(ItemBuilder.DURABILITY_KEY, PersistentDataType.INTEGER, Math.max(0, durability));
        item.setItemMeta(meta);
    }

    /**
     * 判断物品是否有自定义耐久
     */
    public static boolean hasDurability(ItemStack item) {
        return getMaxDurability(item) > 0;
    }

    /**
     * 判断物品是否已损坏（耐久为 0）
     */
    public static boolean isBroken(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        if (!meta.getPersistentDataContainer().has(ItemBuilder.MAX_DURABILITY_KEY, PersistentDataType.INTEGER)) return false;
        int current = meta.getPersistentDataContainer().getOrDefault(ItemBuilder.DURABILITY_KEY, PersistentDataType.INTEGER, -1);
        return current == 0;
    }

    /**
     * 消耗物品耐久
     * @param player 持有者（用于播放音效，可为null）
     * @param item 物品
     * @param amount 消耗量
     * @return true 如果物品已损坏
     */
    public static boolean damageItem(Player player, ItemStack item, int amount) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        int maxDura = meta.getPersistentDataContainer().get(ItemBuilder.MAX_DURABILITY_KEY, PersistentDataType.INTEGER);
        if (maxDura <= 0) return false;

        int current = meta.getPersistentDataContainer().get(ItemBuilder.DURABILITY_KEY, PersistentDataType.INTEGER);
        if (current <= 0) return true;

        int newDura = Math.max(0, current - amount);

        meta.getPersistentDataContainer().set(ItemBuilder.DURABILITY_KEY, PersistentDataType.INTEGER, newDura);

        // 触发 lore 刷新
        ItemCore icPlugin = ItemCore.getInstance();
        if (icPlugin != null) {
            meta.getPersistentDataContainer().set(ItemBuilder.LORE_VERSION_KEY, PersistentDataType.INTEGER, 0);
        }

        item.setItemMeta(meta);

        if (newDura <= 0) {
            // 检查是否允许销毁
            boolean shouldBreak = meta.getPersistentDataContainer().getOrDefault(ItemBuilder.DURABILITY_BREAK_KEY, PersistentDataType.INTEGER, 1) == 1;
            if (shouldBreak) {
                breakItem(player, item);
            }
            return true;
        }

        // 低耐久警告
        if (newDura <= 10 && player != null && ThreadLocalRandom.current().nextInt(3) == 0) {
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.3f, 1.5f);
        }

        return false;
    }

    /**
     * 修复物品耐久
     */
    public static void repairItem(ItemStack item, int amount) {
        if (item == null || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        int maxDura = meta.getPersistentDataContainer().get(ItemBuilder.MAX_DURABILITY_KEY, PersistentDataType.INTEGER);
        if (maxDura <= 0) return;

        int current = meta.getPersistentDataContainer().get(ItemBuilder.DURABILITY_KEY, PersistentDataType.INTEGER);
        int newDura = Math.min(maxDura, current + amount);

        meta.getPersistentDataContainer().set(ItemBuilder.DURABILITY_KEY, PersistentDataType.INTEGER, newDura);

        // 触发 lore 刷新
        ItemCore icPlugin = ItemCore.getInstance();
        if (icPlugin != null) {
            meta.getPersistentDataContainer().set(ItemBuilder.LORE_VERSION_KEY, PersistentDataType.INTEGER, 0);
        }

        item.setItemMeta(meta);
    }

    /**
     * 物品损坏
     */
    private static void breakItem(Player player, ItemStack item) {
        if (item == null) return;
        Location loc = player != null ? player.getLocation() : null;

        if (loc != null) {
            loc.getWorld().playSound(loc, Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
            try { loc.getWorld().spawnParticle(Particle.ITEM, loc.clone().add(0, 1, 0), 20, 0.3, 0.3, 0.3, 0.1, item); } catch (Exception ignored) {}
        }

        item.setAmount(0);
        item.setType(org.bukkit.Material.AIR);
    }
}
