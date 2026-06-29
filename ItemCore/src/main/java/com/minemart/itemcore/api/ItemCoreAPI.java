package com.minemart.itemcore.api;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.api.event.ItemObtainedEvent;
import com.minemart.itemcore.calculator.AttributeCalculator;
import com.minemart.itemcore.damage.DamageManager;
import com.minemart.itemcore.damage.DamageRequest;
import com.minemart.itemcore.damage.HealManager;
import com.minemart.itemcore.damage.HealingRequest;
import com.minemart.itemcore.element.AccumulationManager;
import com.minemart.itemcore.element.ElementConfig;
import com.minemart.itemcore.element.AccumulationManager.AccumulationSnapshot;
import com.minemart.itemcore.element.DamageContext;
import com.minemart.itemcore.item.CustomItem;
import com.minemart.itemcore.item.ItemCategory;
import com.minemart.itemcore.item.attribute.AttributeContainer;
import com.minemart.itemcore.item.attribute.CustomAttribute;
import com.minemart.itemcore.item.attribute.ElementType;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemCoreAPI {

    private static ItemRegistry itemRegistry;
    private static CategoryRegistry categoryRegistry;
    private static final List<AttributeProvider> attributeProviders = new ArrayList<>();

    private ItemCoreAPI() {}

    public static void registerAttributeProvider(AttributeProvider provider) {
        if (provider != null) {
            attributeProviders.add(provider);
        }
    }

    public static List<AttributeProvider> getAttributeProviders() {
        return attributeProviders;
    }

    public static void refreshPlayerAttributes(Player player) {
        if (player == null || !player.isOnline()) return;

        AttributeContainer attrs = AttributeCalculator.calculatePlayerAttributes(player);

        double maxHealth = 20.0 + attrs.getAttribute(CustomAttribute.HEALTH);
        AttributeInstance healthAttr = player.getAttribute(Attribute.MAX_HEALTH);
        if (healthAttr != null) {
            healthAttr.setBaseValue(maxHealth);
            if (player.getHealth() > maxHealth) {
                player.setHealth(maxHealth);
            }
        }

        double movementSpeed = 0.1 + attrs.getAttribute(CustomAttribute.MOVEMENT_SPEED);
        AttributeInstance speedAttr = player.getAttribute(Attribute.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.setBaseValue(movementSpeed);
        }
    }

    public static String getElementIcon(String elementId) {
        try {
            ItemCore plugin = ItemCore.getInstance();
            if (plugin != null && plugin.getElementConfig() != null) {
                ElementConfig.ElementData data = plugin.getElementConfig().getElementData(elementId);
                if (data != null && data.getIcon() != null) {
                    return data.getIcon();
                }
            }
        } catch (Exception ignored) {}
        return "\u25C7";
    }

    public static String getElementColor(String elementId) {
        try {
            ItemCore plugin = ItemCore.getInstance();
            if (plugin != null && plugin.getElementConfig() != null) {
                ElementConfig.ElementData data = plugin.getElementConfig().getElementData(elementId);
                if (data != null && data.getColor() != null) {
                    return data.getColor();
                }
            }
        } catch (Exception ignored) {}
        return "&f";
    }

    public static void setItemRegistry(ItemRegistry registry) {
        if (itemRegistry == null) {
            itemRegistry = registry;
        }
    }

    public static void setCategoryRegistry(CategoryRegistry registry) {
        if (categoryRegistry == null) {
            categoryRegistry = registry;
        }
    }

    public static ItemRegistry getItemRegistry() {
        return itemRegistry;
    }

    public static CategoryRegistry getCategoryRegistry() {
        return categoryRegistry;
    }

    public static CustomItem getCustomItem(String itemId) {
        if (itemRegistry == null) {
            return null;
        }
        return itemRegistry.getCustomItem(itemId);
    }

    public static ItemStack getItemStack(String itemId) {
        if (itemRegistry == null) {
            return null;
        }
        return itemRegistry.getItemStack(itemId);
    }

    public static ItemStack getItemStack(String itemId, int amount) {
        if (itemRegistry == null) {
            return null;
        }
        return itemRegistry.getItemStack(itemId, amount);
    }

    public static boolean hasItem(String itemId) {
        return itemRegistry != null && itemRegistry.hasItem(itemId);
    }

    public static Collection<String> getItemIds() {
        if (itemRegistry == null) {
            return java.util.Collections.emptyList();
        }
        return itemRegistry.getItemIds();
    }

    public static Collection<CustomItem> getItems() {
        if (itemRegistry == null) {
            return java.util.Collections.emptyList();
        }
        return itemRegistry.getItems();
    }

    public static Collection<CustomItem> getItemsByCategory(String categoryId) {
        if (itemRegistry == null) {
            return java.util.Collections.emptyList();
        }
        return itemRegistry.getItemsByCategory(categoryId);
    }

    public static ItemCategory getCategory(String categoryId) {
        if (categoryRegistry == null) {
            return null;
        }
        return categoryRegistry.getCategory(categoryId);
    }

    public static boolean hasCategory(String categoryId) {
        return categoryRegistry != null && categoryRegistry.hasCategory(categoryId);
    }

    public static Collection<String> getCategoryIds() {
        if (categoryRegistry == null) {
            return java.util.Collections.emptyList();
        }
        return categoryRegistry.getCategoryIds();
    }

    public static Collection<ItemCategory> getCategories() {
        if (categoryRegistry == null) {
            return java.util.Collections.emptyList();
        }
        return categoryRegistry.getCategories();
    }

    public static boolean giveItem(Player player, String itemId, int amount) {
        if (player == null || itemId == null || amount <= 0) {
            return false;
        }

        ItemStack itemStack = getItemStack(itemId, amount);
        if (itemStack == null) {
            return false;
        }

        ItemObtainedEvent event = new ItemObtainedEvent(
            player, itemId, itemStack, amount, ItemObtainedEvent.ObtainSource.API
        );
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        int finalAmount = event.getAmount();
        if (finalAmount != amount) {
            itemStack.setAmount(finalAmount);
        }

        Map<Integer, ItemStack> leftover = player.getInventory().addItem(itemStack);
        for (ItemStack leftoverItem : leftover.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), leftoverItem);
        }

        return true;
    }

    public static boolean giveItem(Player player, String itemId) {
        return giveItem(player, itemId, 1);
    }

    public static AttributeContainer getPlayerAttributes(Player player) {
        return AttributeCalculator.calculatePlayerAttributes(player);
    }

    public static double getAttribute(Player player, CustomAttribute attribute) {
        AttributeContainer attrs = AttributeCalculator.calculatePlayerAttributes(player);
        return attrs.getAttribute(attribute);
    }

    public static double getSpellPower(Player player) {
        AttributeContainer attrs = AttributeCalculator.calculatePlayerAttributes(player);
        return attrs.getAttribute(CustomAttribute.SPELL_POWER);
    }

    public static double getAttackDamage(Player player) {
        AttributeContainer attrs = AttributeCalculator.calculatePlayerAttributes(player);
        return attrs.getAttribute(CustomAttribute.ATTACK_DAMAGE);
    }

    public static double getAdaptiveForceValue(Player player) {
        AttributeContainer attrs = AttributeCalculator.calculatePlayerAttributes(player);
        return attrs.getAttribute(CustomAttribute.ADAPTIVE_FORCE);
    }

    public static double getElementMastery(Player player, ElementType element) {
        AttributeContainer attrs = AttributeCalculator.calculatePlayerAttributes(player);
        return attrs.getElementMastery(element);
    }

    public static double getElementResistance(Player player, ElementType element) {
        AttributeContainer attrs = AttributeCalculator.calculatePlayerAttributes(player);
        return attrs.getElementResistance(element);
    }

    public static AccumulationSnapshot getElementProgress(LivingEntity entity, ElementType element) {
        ItemCore plugin = ItemCore.getInstance();
        if (plugin == null || plugin.getAccumulationManager() == null) return null;
        return plugin.getAccumulationManager().getProgress(entity, element);
    }

    public static void addElementProgress(LivingEntity entity, ElementType element, double amount) {
        ItemCore plugin = ItemCore.getInstance();
        if (plugin == null || plugin.getAccumulationManager() == null) return;
        plugin.getAccumulationManager().addProgress(entity, element, amount);
    }

    public static void clearElementProgress(LivingEntity entity, ElementType element) {
        ItemCore plugin = ItemCore.getInstance();
        if (plugin == null || plugin.getAccumulationManager() == null) return;
        plugin.getAccumulationManager().clearProgress(entity, element);
    }

    public static boolean hasAilment(LivingEntity entity, String ailmentId) {
        ItemCore plugin = ItemCore.getInstance();
        if (plugin == null || plugin.getAilmentManager() == null) return false;
        return plugin.getAilmentManager().hasAilment(entity, ailmentId);
    }

    public static void applyElementDamage(LivingEntity target, DamageContext ctx) {
        ItemCore plugin = ItemCore.getInstance();
        if (plugin == null || plugin.getAccumulationManager() == null) return;
        plugin.getAccumulationManager().onElementDamage(target, ctx);
    }

    public static void processDamage(DamageRequest request) {
        DamageManager.processDamage(request);
    }

    public static void processHeal(HealingRequest request) {
        HealManager.processHeal(request);
    }
}