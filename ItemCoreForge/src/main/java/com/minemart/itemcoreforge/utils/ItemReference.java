package com.minemart.itemcoreforge.utils;

import com.minemart.itemcoreforge.ItemCoreForge;
import com.minemart.itemcoreforge.core.Forge;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemReference {

    private String source = "itemcore";
    private String category = "";
    private String id = "";
    private int amount = 1;
    private boolean checkDurability = false;

    public ItemReference() {
    }

    public static ItemReference fromString(String str) {
        ItemReference ref = new ItemReference();
        
        if (str == null || str.isEmpty()) {
            return ref;
        }
        
        if (str.startsWith("itemcore:")) {
            ref.setSource("itemcore");
            String remaining = str.substring(9);
            parseItemCoreFormat(ref, remaining);
        } else if (str.startsWith("vanilla:")) {
            ref.setSource("vanilla");
            String remaining = str.substring(8);
            parseVanillaFormat(ref, remaining);
        } else if (str.startsWith("mythicmobs:")) {
            ref.setSource("mythicmobs");
            String remaining = str.substring(10);
            parseMythicMobsFormat(ref, remaining);
        } else {
            ref.setSource("itemcore");
            ref.setId(str);
        }
        
        return ref;
    }

    private static void parseItemCoreFormat(ItemReference ref, String str) {
        String[] parts = str.split("/");
        if (parts.length == 2) {
            ref.setCategory(parts[0]);
            String idAndAmount = parts[1];
            parseIdAndAmount(ref, idAndAmount);
        } else {
            parseIdAndAmount(ref, str);
        }
    }

    private static void parseVanillaFormat(ItemReference ref, String str) {
        parseIdAndAmount(ref, str);
    }

    private static void parseMythicMobsFormat(ItemReference ref, String str) {
        parseIdAndAmount(ref, str);
    }

    private static void parseIdAndAmount(ItemReference ref, String str) {
        if (str.contains("*")) {
            String[] parts = str.split("\\*");
            ref.setId(parts[0]);
            if (parts.length > 1) {
                try {
                    ref.setAmount(Integer.parseInt(parts[1]));
                } catch (NumberFormatException e) {
                    ref.setAmount(1);
                }
            }
        } else {
            ref.setId(str);
        }
    }

    public ItemStack toItemStack() {
        if (isItemCore()) {
            return resolveItemCoreItem();
        } else if (isMythicMobs()) {
            return resolveMythicMobsItem();
        } else {
            return resolveVanillaItem();
        }
    }

    private ItemStack resolveItemCoreItem() {
        if (category.isEmpty()) {
            return getPlaceholderItem("ItemCore 物品必须指定 category");
        }
        
        try {
            Class<?> apiClass = Class.forName("com.minemart.itemcore.api.ItemCoreAPI");
            Object api = apiClass.getMethod("getInstance").invoke(null);
            
            String fullId = category + "/" + id;
            
            Object itemStack = apiClass.getMethod("getItemStack", String.class, int.class)
                .invoke(api, fullId, amount);
            
            if (itemStack == null || ((ItemStack) itemStack).getType() == Material.AIR) {
                return getPlaceholderItem("未找到 ItemCore 物品: " + fullId);
            }
            
            return (ItemStack) itemStack;
        } catch (Exception e) {
            return getPlaceholderItem("获取 ItemCore 物品失败: " + e.getMessage());
        }
    }
    
    private ItemStack getPlaceholderItem(String reason) {
        ItemStack item = new ItemStack(Material.BARRIER);
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§c" + reason);
            item.setItemMeta(meta);
        }
        item.setAmount(amount);
        return item;
    }

    private ItemStack resolveVanillaItem() {
        try {
            Material material = Material.valueOf(id.toUpperCase());
            return new ItemStack(material, amount);
        } catch (IllegalArgumentException e) {
            return new ItemStack(Material.AIR);
        }
    }

    private ItemStack resolveMythicMobsItem() {
        try {
            Class<?> mythicBukkitClass = Class.forName("io.lumine.mythic.bukkit.MythicBukkit");
            Object mythicBukkit = mythicBukkitClass.getMethod("inst").invoke(null);
            Object itemManager = mythicBukkitClass.getMethod("getItemManager").invoke(mythicBukkit);
            Class<?> itemManagerClass = itemManager.getClass();
            
            ItemStack result = tryGetItemStackDirect(itemManager, itemManagerClass);
            if (result != null && result.getType() != Material.AIR) {
                result.setAmount(amount);
                return result;
            }
            
            result = tryGetItemNewAPI(itemManager, itemManagerClass);
            if (result != null && result.getType() != Material.AIR) {
                result.setAmount(amount);
                return result;
            }
            
            return getPlaceholderItem();
        } catch (Exception e) {
            logWarning("获取 MythicMobs 物品失败: " + id + " - " + e.getMessage());
            return getPlaceholderItem();
        }
    }
    
    private ItemStack tryGetItemStackDirect(Object itemManager, Class<?> itemManagerClass) {
        try {
            java.lang.reflect.Method getItemStackMethod = null;
            for (java.lang.reflect.Method m : itemManagerClass.getMethods()) {
                if (m.getName().equals("getItemStack") && m.getParameterCount() == 1 && 
                    m.getReturnType() == ItemStack.class) {
                    getItemStackMethod = m;
                    break;
                }
            }
            
            if (getItemStackMethod != null) {
                Object result = getItemStackMethod.invoke(itemManager, id);
                if (result instanceof ItemStack) {
                    return (ItemStack) result;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }
    
    private ItemStack tryGetItemNewAPI(Object itemManager, Class<?> itemManagerClass) {
        try {
            java.lang.reflect.Method getItemMethod = null;
            for (java.lang.reflect.Method m : itemManagerClass.getMethods()) {
                if (m.getName().equals("getItem") && m.getParameterCount() == 1) {
                    getItemMethod = m;
                    break;
                }
            }
            
            if (getItemMethod == null) {
                return null;
            }
            
            Object mythicItemOpt = getItemMethod.invoke(itemManager, id);
            if (mythicItemOpt == null) {
                return null;
            }
            
            Object mythicItem = null;
            if (mythicItemOpt instanceof java.util.Optional) {
                java.util.Optional<?> opt = (java.util.Optional<?>) mythicItemOpt;
                if (opt.isPresent()) {
                    mythicItem = opt.get();
                } else {
                    return null;
                }
            } else {
                mythicItem = mythicItemOpt;
            }
            
            if (mythicItem == null) {
                return null;
            }
            
            Object abstractItemStack = null;
            try {
                java.lang.reflect.Method generateMethod = mythicItem.getClass().getMethod("generateItemStack");
                abstractItemStack = generateMethod.invoke(mythicItem);
            } catch (NoSuchMethodException e) {
                return null;
            }
            
            if (abstractItemStack == null) {
                return null;
            }
            
            Class<?> bukkitItemStackClass = null;
            try {
                bukkitItemStackClass = Class.forName("io.lumine.mythic.bukkit.adapters.BukkitItemStack");
            } catch (ClassNotFoundException e) {
                return null;
            }
            
            if (bukkitItemStackClass.isInstance(abstractItemStack)) {
                try {
                    java.lang.reflect.Method buildMethod = abstractItemStack.getClass().getMethod("build");
                    Object result = buildMethod.invoke(abstractItemStack);
                    if (result instanceof ItemStack) {
                        return (ItemStack) result;
                    }
                } catch (NoSuchMethodException e) {
                }
                
                try {
                    java.lang.reflect.Method asBukkitMethod = abstractItemStack.getClass().getMethod("asBukkit");
                    Object result = asBukkitMethod.invoke(abstractItemStack);
                    if (result instanceof ItemStack) {
                        return (ItemStack) result;
                    }
                } catch (NoSuchMethodException e) {
                }
            }
            
            return null;
        } catch (Exception e) {
        }
        return null;
    }
    
    private void logWarning(String message) {
        ItemCoreForge plugin = ItemCoreForge.getInstance();
        if (plugin != null) {
            plugin.getLogger().warning("[MythicMobs-Debug] " + message);
        }
    }
    
    private void logSevere(String message) {
        ItemCoreForge plugin = ItemCoreForge.getInstance();
        if (plugin != null) {
            plugin.getLogger().severe("[MythicMobs-Debug] " + message);
        }
    }
    
    private ItemStack getPlaceholderItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§c未找到 MythicMobs 物品: " + id);
            item.setItemMeta(meta);
        }
        item.setAmount(amount);
        return item;
    }

    public boolean isValid() {
        return id != null && !id.isEmpty() && amount > 0;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isItemCore() {
        return "itemcore".equalsIgnoreCase(source);
    }

    public boolean isMythicMobs() {
        return "mythicmobs".equalsIgnoreCase(source);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = Math.max(1, amount);
    }

    public boolean isCheckDurability() {
        return checkDurability;
    }

    public void setCheckDurability(boolean checkDurability) {
        this.checkDurability = checkDurability;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(source).append(":");
        if (isItemCore() && !category.isEmpty()) {
            sb.append(category).append("/");
        }
        sb.append(id);
        if (amount > 1) {
            sb.append("*").append(amount);
        }
        return sb.toString();
    }
}
