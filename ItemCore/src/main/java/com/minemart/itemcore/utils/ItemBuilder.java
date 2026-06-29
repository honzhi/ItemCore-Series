package com.minemart.itemcore.utils;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.item.CustomItem;
import com.minemart.itemcore.item.EnchantmentInfo;
import com.minemart.itemcore.item.PotionEffectInfo;
import com.minemart.itemcore.item.attribute.AttributeContainer;
import com.minemart.itemcore.item.attribute.CustomAttribute;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ItemBuilder {

    public static final NamespacedKey ITEM_ID_KEY = new NamespacedKey("itemcore", "item_id");
    public static final NamespacedKey LORE_VERSION_KEY = new NamespacedKey("itemcore", "lore_ver");
    public static final NamespacedKey DURABILITY_KEY = new NamespacedKey("itemcore", "durability");
    public static final NamespacedKey MAX_DURABILITY_KEY = new NamespacedKey("itemcore", "max_durability");
    public static final NamespacedKey DURABILITY_BREAK_KEY = new NamespacedKey("itemcore", "durability_break");
    public static final NamespacedKey DISABLE_ANVIL_REPAIR_KEY = new NamespacedKey("itemcore", "disable_anvil_repair");
    public static final NamespacedKey DISABLE_ENCHANTING_KEY = new NamespacedKey("itemcore", "disable_enchanting");
    
    // MMOItems 风格：添加假的属性修饰符来覆盖原版属性
    private static final UUID DECOY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final AttributeModifier FAKE_MODIFIER = new AttributeModifier(
            DECOY_UUID, 
            "ItemCore Decoy", 
            0, 
            AttributeModifier.Operation.ADD_NUMBER,
            org.bukkit.inventory.EquipmentSlot.HAND
    );

    public static NamespacedKey getAttributeKey(CustomAttribute attr) {
        return new NamespacedKey("itemcore", "a_" + attr.getConfigKey().toLowerCase());
    }

    public static ItemStack build(CustomItem customItem) {
        return build(customItem, 1);
    }

    public static ItemStack build(CustomItem customItem, int amount) {
        Material material = customItem.getMaterial();
        int finalAmount = customItem.getMaxStack() > 0 ? Math.min(amount, customItem.getMaxStack()) : amount;

        ItemStack itemStack = new ItemStack(material, finalAmount);
        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) {
            return itemStack;
        }

        if (customItem.getDisplayName() != null) {
            Component displayName = MessageUtil.colorize(customItem.getDisplayName());
            meta.displayName(displayName);
        }

        List<String> loreToUse;
        ItemCore plugin = ItemCore.getInstance();
        if (plugin != null && plugin.getLoreManager() != null) {
            loreToUse = plugin.getLoreManager().generateLore(customItem);
        } else {
            loreToUse = customItem.getLore();
        }

        if (!loreToUse.isEmpty()) {
            List<Component> lore = new ArrayList<>();
            for (String line : loreToUse) {
                lore.add(MessageUtil.colorize(line));
            }
            meta.lore(lore);
        }

        for (EnchantmentInfo enchantInfo : customItem.getEnchantments()) {
            Enchantment enchantment = enchantInfo.getEnchantment();
            if (enchantment != null) {
                meta.addEnchant(enchantment, enchantInfo.getLevel(), true);
            }
        }

        if (customItem.hasCustomModelData()) {
            meta.setCustomModelData(customItem.getCustomModelData());
        }

        if (customItem.isUnbreakable()) {
            meta.setUnbreakable(true);
        }

        // 如果配置了 HIDE_ATTRIBUTES flag，先移除物品的原版属性，再设置 flag
        boolean hasHideAttributes = customItem.getItemFlags().contains(ItemFlag.HIDE_ATTRIBUTES);
        if (hasHideAttributes) {
            removeVanillaAttributes(meta);
        }

        // 设置所有 item flags（包括 HIDE_ATTRIBUTES）
        for (ItemFlag flag : customItem.getItemFlags()) {
            meta.addItemFlags(flag);
        }

        double armorValue = customItem.getAttributes().getAttribute(CustomAttribute.ARMOR);
        if (armorValue > 0) {
            meta.removeAttributeModifier(Attribute.ARMOR);
            meta.addAttributeModifier(Attribute.ARMOR,
                new AttributeModifier(UUID.randomUUID(), "ItemCore Armor", armorValue,
                    AttributeModifier.Operation.ADD_NUMBER, getArmorSlot(customItem.getMaterial())));
        }

        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, customItem.getId());
        ItemCore icPlugin = ItemCore.getInstance();
        if (icPlugin != null) {
            meta.getPersistentDataContainer().set(LORE_VERSION_KEY, PersistentDataType.INTEGER, icPlugin.getLoreVersion());
        }

        // 隐藏原版耐久，使用 Lore 自定义耐久显示
        if (customItem.hasDurability() && !customItem.isUnbreakable()) {
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }

        // [Debug] 写入自定义耐久数据
        if (customItem.hasDurability()) {
            System.out.println("[ItemCore-Debug] ItemBuilder: id=" + customItem.getId() + " hasDurability=" + customItem.hasDurability() + " unbreakable=" + customItem.isUnbreakable() + " durability=" + customItem.getDurability());
        }
        if (customItem.hasDurability() && !customItem.isUnbreakable()) {
            meta.getPersistentDataContainer().set(MAX_DURABILITY_KEY, PersistentDataType.INTEGER, customItem.getDurability());
            if (!meta.getPersistentDataContainer().has(DURABILITY_KEY, PersistentDataType.INTEGER)) {
                meta.getPersistentDataContainer().set(DURABILITY_KEY, PersistentDataType.INTEGER, customItem.getDurability());
            }
            meta.getPersistentDataContainer().set(DURABILITY_BREAK_KEY, PersistentDataType.INTEGER, customItem.isDurabilityBreak() ? 1 : 0);
            meta.getPersistentDataContainer().set(DISABLE_ANVIL_REPAIR_KEY, PersistentDataType.INTEGER, customItem.isDisableAnvilRepair() ? 1 : 0);
            meta.getPersistentDataContainer().set(DISABLE_ENCHANTING_KEY, PersistentDataType.INTEGER, customItem.isDisableEnchanting() ? 1 : 0);
        }

        // 写入属性值到 PDC（固定值直接写入，范围值随机后写入）
        if (customItem.hasDurability() && !customItem.isUnbreakable()) {
            // 有自定义耐久的物品才写入属性PDC，避免非IC物品异常
        }
        for (Map.Entry<CustomAttribute, Double> entry : customItem.getAttributes().getBaseAttributes().entrySet()) {
            CustomAttribute attr = entry.getKey();
            meta.getPersistentDataContainer().set(getAttributeKey(attr), PersistentDataType.DOUBLE, entry.getValue());
        }
        for (Map.Entry<CustomAttribute, double[]> entry : customItem.getAttributes().getAttributeRanges().entrySet()) {
            CustomAttribute attr = entry.getKey();
            double[] range = entry.getValue();
            double rolled = Math.round(ThreadLocalRandom.current().nextDouble(range[0], range[1]) * 10.0) / 10.0;
            meta.getPersistentDataContainer().set(getAttributeKey(attr), PersistentDataType.DOUBLE, rolled);
        }

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public static String getItemId(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta()) {
            return null;
        }
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return null;
        }
        return meta.getPersistentDataContainer().get(ITEM_ID_KEY, PersistentDataType.STRING);
    }

    public static boolean isCustomItem(ItemStack itemStack) {
        return getItemId(itemStack) != null;
    }

    public static List<PotionEffect> buildPotionEffects(CustomItem customItem) {
        List<PotionEffect> effects = new ArrayList<>();
        for (PotionEffectInfo info : customItem.getEffects()) {
            PotionEffect effect = info.toPotionEffect();
            if (effect != null) {
                effects.add(effect);
            }
        }
        return effects;
    }

    private static void removeVanillaAttributes(ItemMeta meta) {
        if (meta == null) {
            return;
        }

        // MMOItems 风格的属性移除
        try {
            // 方法1: 获取所有属性修饰符并清空
            com.google.common.collect.Multimap<Attribute, AttributeModifier> modifiers = meta.getAttributeModifiers();
            if (modifiers != null) {
                for (Attribute attribute : modifiers.keySet()) {
                    meta.removeAttributeModifier(attribute);
                }
            }

            // 方法2: 强制清空所有属性
            meta.setAttributeModifiers(null);

            // 方法3: 添加一个假的属性修饰符来覆盖原版属性（MMOItems 的关键技巧）
            // 这会覆盖 Minecraft 原版自动添加的属性显示
            meta.addAttributeModifier(Attribute.ATTACK_SPEED, FAKE_MODIFIER);
            
        } catch (Exception e) {
            // 保持静默，至少 flag 会隐藏显示
        }
    }

    private static org.bukkit.inventory.EquipmentSlot getArmorSlot(Material material) {
        if (material == null) {
            return org.bukkit.inventory.EquipmentSlot.CHEST;
        }
        String name = material.name();
        if (name.endsWith("_HELMET") || name.endsWith("_SKULL") || name.equals("CARVED_PUMPKIN")) {
            return org.bukkit.inventory.EquipmentSlot.HEAD;
        }
        if (name.endsWith("_CHESTPLATE") || name.equals("ELYTRA")) {
            return org.bukkit.inventory.EquipmentSlot.CHEST;
        }
        if (name.endsWith("_LEGGINGS")) {
            return org.bukkit.inventory.EquipmentSlot.LEGS;
        }
        if (name.endsWith("_BOOTS")) {
            return org.bukkit.inventory.EquipmentSlot.FEET;
        }
        return org.bukkit.inventory.EquipmentSlot.CHEST;
    }
}
