package com.minemart.itemcoreforge.utils;

import com.minemart.itemcoreforge.core.Forge;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

public class MaterialChecker {

    public static class MaterialCheckResult {
        private final boolean success;
        private final List<MaterialStatus> materialStatuses;
        private final String failReason;

        public MaterialCheckResult(boolean success, List<MaterialStatus> materialStatuses, String failReason) {
            this.success = success;
            this.materialStatuses = materialStatuses;
            this.failReason = failReason;
        }

        public static MaterialCheckResult success(List<MaterialStatus> statuses) {
            return new MaterialCheckResult(true, statuses, null);
        }

        public static MaterialCheckResult fail(List<MaterialStatus> statuses, String reason) {
            return new MaterialCheckResult(false, statuses, reason);
        }

        public boolean isSuccess() {
            return success;
        }

        public List<MaterialStatus> getMaterialStatuses() {
            return materialStatuses;
        }

        public String getFailReason() {
            return failReason;
        }
    }

    public static class MaterialStatus {
        private final ItemReference reference;
        private final int required;
        private final int available;
        private final boolean hasEnough;

        public MaterialStatus(ItemReference reference, int required, int available, boolean hasEnough) {
            this.reference = reference;
            this.required = required;
            this.available = available;
            this.hasEnough = hasEnough;
        }

        public ItemReference getReference() {
            return reference;
        }

        public int getRequired() {
            return required;
        }

        public int getAvailable() {
            return available;
        }

        public boolean hasEnough() {
            return hasEnough;
        }
    }

    public MaterialCheckResult checkMaterials(Player player, List<Forge.ItemReference> materials) {
        List<MaterialStatus> statuses = new ArrayList<>();
        boolean allSatisfied = true;
        String failReason = null;

        for (Forge.ItemReference materialRef : materials) {
            ItemReference ref = convertToItemReference(materialRef);
            int required = ref.getAmount();
            int available = countItem(player, ref);
            boolean hasEnough = available >= required;

            statuses.add(new MaterialStatus(ref, required, available, hasEnough));

            if (!hasEnough) {
                allSatisfied = false;
                failReason = "材料不足: " + ref.getId();
            }
        }

        if (allSatisfied) {
            return MaterialCheckResult.success(statuses);
        } else {
            return MaterialCheckResult.fail(statuses, failReason);
        }
    }

    public boolean hasMaterials(Player player, List<Forge.ItemReference> materials) {
        return checkMaterials(player, materials).isSuccess();
    }

    public boolean consumeMaterials(Player player, List<Forge.ItemReference> materials) {
        if (!hasMaterials(player, materials)) {
            return false;
        }

        for (Forge.ItemReference materialRef : materials) {
            ItemReference ref = convertToItemReference(materialRef);
            ItemStack item = ref.toItemStack();
            
            if (materialRef.isCheckDurability()) {
                consumeWithDurabilityCheck(player, item, ref.getAmount());
            } else {
                ItemStack toRemove = item.clone();
                toRemove.setAmount(ref.getAmount());
                player.getInventory().removeItem(toRemove);
            }
        }

        return true;
    }

    private int countItem(Player player, ItemReference ref) {
        if (ref.isItemCore()) {
            return countItemCoreItem(player, ref);
        } else if (ref.isMythicMobs()) {
            return countMythicMobsItem(player, ref);
        } else {
            return countVanillaItem(player, ref);
        }
    }

    private int countItemCoreItem(Player player, ItemReference ref) {
        if (ref.getCategory().isEmpty()) {
            return 0;
        }
        
        try {
            Class<?> apiClass = Class.forName("com.minemart.itemcore.api.ItemCoreAPI");
            Object api = apiClass.getMethod("getInstance").invoke(null);
            
            Class<?> itemBuilderClass = Class.forName("com.minemart.itemcore.utils.ItemBuilder");
            java.lang.reflect.Method isCustomItemMethod = itemBuilderClass.getMethod("isCustomItem", ItemStack.class);
            java.lang.reflect.Method getItemIdMethod = itemBuilderClass.getMethod("getItemId", ItemStack.class);
            
            String targetId = ref.getCategory() + "/" + ref.getId();
            
            int count = 0;
            PlayerInventory inv = player.getInventory();
            
            for (ItemStack item : inv.getContents()) {
                if (item == null || item.getType() == Material.AIR) {
                    continue;
                }
                
                boolean isCustom = (boolean) isCustomItemMethod.invoke(null, item);
                if (!isCustom) {
                    continue;
                }
                
                String itemId = (String) getItemIdMethod.invoke(null, item);
                if (targetId.equalsIgnoreCase(itemId)) {
                    count += item.getAmount();
                }
            }
            
            return count;
        } catch (Exception e) {
            return 0;
        }
    }

    private int countVanillaItem(Player player, ItemReference ref) {
        try {
            Material material = Material.valueOf(ref.getId().toUpperCase());
            ItemStack sampleItem = new ItemStack(material);
            return countMatchingItems(player.getInventory(), sampleItem);
        } catch (IllegalArgumentException e) {
            return 0;
        }
    }

    private int countMythicMobsItem(Player player, ItemReference ref) {
        ItemStack sampleItem = ref.toItemStack();
        if (sampleItem == null || sampleItem.getType() == Material.AIR || sampleItem.getType() == Material.BARRIER) {
            return 0;
        }
        return countMatchingItems(player.getInventory(), sampleItem);
    }

    private int countMatchingItems(PlayerInventory inventory, ItemStack sample) {
        int count = 0;
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.isSimilar(sample)) {
                count += item.getAmount();
            }
        }
        return count;
    }

    private void consumeWithDurabilityCheck(Player player, ItemStack item, int amount) {
        int remaining = amount;
        ItemStack[] contents = player.getInventory().getContents();

        for (int i = 0; i < contents.length && remaining > 0; i++) {
            ItemStack content = contents[i];
            if (content != null && content.isSimilar(item)) {
                int itemAmount = content.getAmount();
                if (itemAmount <= remaining) {
                    player.getInventory().setItem(i, null);
                    remaining -= itemAmount;
                } else {
                    content.setAmount(itemAmount - remaining);
                    remaining = 0;
                }
            }
        }
    }

    private ItemReference convertToItemReference(Forge.ItemReference forgeRef) {
        ItemReference ref = new ItemReference();
        ref.setSource(forgeRef.getSource());
        ref.setCategory(forgeRef.getCategory());
        ref.setId(forgeRef.getId());
        ref.setAmount(forgeRef.getAmount());
        ref.setCheckDurability(forgeRef.isCheckDurability());
        return ref;
    }

    public boolean canGiveMaterials(Player player, List<Forge.ItemReference> materials) {
        for (Forge.ItemReference materialRef : materials) {
            ItemReference ref = convertToItemReference(materialRef);
            if (!canAddItem(player, ref)) {
                return false;
            }
        }
        return true;
    }

    private boolean canAddItem(Player player, ItemReference ref) {
        if (ref.isItemCore() || ref.isMythicMobs()) {
            return true;
        } else {
            try {
                Material material = Material.valueOf(ref.getId().toUpperCase());
                ItemStack item = new ItemStack(material, ref.getAmount());
                return player.getInventory().firstEmpty() != -1 || 
                       hasMatchingStack(player.getInventory(), item);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
    }

    private boolean hasMatchingStack(PlayerInventory inventory, ItemStack item) {
        for (ItemStack content : inventory.getContents()) {
            if (content != null && content.isSimilar(item) && 
                content.getAmount() + item.getAmount() <= content.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

    public boolean giveMaterials(Player player, List<Forge.ItemReference> materials) {
        if (!canGiveMaterials(player, materials)) {
            return false;
        }
        
        for (Forge.ItemReference materialRef : materials) {
            giveItem(player, materialRef);
        }
        return true;
    }

    public void giveItem(Player player, Forge.ItemReference materialRef) {
        ItemReference ref = convertToItemReference(materialRef);
        if (ref.isItemCore()) {
            try {
                Class<?> apiClass = Class.forName("com.minemart.itemcore.api.ItemCoreAPI");
                Object api = apiClass.getMethod("getInstance").invoke(null);
                apiClass.getMethod("giveItem", Player.class, String.class, int.class)
                    .invoke(api, player, ref.getId(), ref.getAmount());
            } catch (Exception e) {
            }
        } else {
            ItemStack item = ref.toItemStack();
            if (item != null && item.getType() != Material.AIR && item.getType() != Material.BARRIER) {
                player.getInventory().addItem(item);
            }
        }
    }

    public ItemStack resolveItem(Forge.ItemReference materialRef) {
        ItemReference ref = convertToItemReference(materialRef);
        if (ref.isItemCore()) {
            try {
                Class<?> apiClass = Class.forName("com.minemart.itemcore.api.ItemCoreAPI");
                Object api = apiClass.getMethod("getInstance").invoke(null);
                return (ItemStack) apiClass.getMethod("getItemStack", String.class, int.class)
                    .invoke(api, ref.getId(), ref.getAmount());
            } catch (Exception e) {
                return null;
            }
        } else {
            ItemStack item = ref.toItemStack();
            if (item != null && item.getType() != Material.AIR && item.getType() != Material.BARRIER) {
                return item;
            }
            return null;
        }
    }
}
