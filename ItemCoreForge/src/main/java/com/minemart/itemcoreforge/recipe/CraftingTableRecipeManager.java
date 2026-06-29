package com.minemart.itemcoreforge.recipe;

import com.minemart.itemcoreforge.ItemCoreForge;
import com.minemart.itemcoreforge.core.Forge;
import com.minemart.itemcoreforge.utils.ConditionChecker;
import com.minemart.itemcoreforge.utils.DebugLogger;
import com.minemart.itemcoreforge.utils.MaterialChecker;
import com.minemart.itemcoreforge.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CraftingTableRecipeManager implements Listener {

    private final ItemCoreForge plugin;
    private final Map<String, Forge> registeredRecipes = new HashMap<>();
    private final ConditionChecker conditionChecker = new ConditionChecker();
    private MaterialChecker materialChecker;

    public CraftingTableRecipeManager(ItemCoreForge plugin) {
        this.plugin = plugin;
    }

    public void setMaterialChecker(MaterialChecker materialChecker) {
        this.materialChecker = materialChecker;
    }

    public void registerForgeRecipes(Forge forge) {
        if (!forge.isCraftingTableType()) {
            return;
        }

        for (Map.Entry<String, Forge.Recipe> entry : forge.getRecipes().entrySet()) {
            Forge.Recipe recipe = entry.getValue();
            registerRecipe(forge, recipe);
            registeredRecipes.put(recipe.getRecipeId(), forge);
        }

        plugin.getLogger().info("已注册工作台配方: " + forge.getForgeId() + " (" + forge.getRecipes().size() + " 个配方)");
    }

    public void unregisterAll() {
        for (Forge forge : registeredRecipes.values()) {
            unregisterForge(forge);
        }
        registeredRecipes.clear();
    }

    private void registerRecipe(Forge forge, Forge.Recipe recipe) {
        NamespacedKey key = new NamespacedKey(plugin, forge.getForgeId() + "/" + recipe.getRecipeId());

        ItemStack result = resolveItemStack(recipe.getOutput());
        if (result == null) {
            plugin.getLogger().warning("无法解析输出物品: " + recipe.getRecipeId());
            return;
        }

        DebugLogger.info("CraftingTable", "注册配方: " + key);

        if (recipe.isExactPlacement()) {
            registerShapedRecipe(forge, recipe, key, result);
        } else {
            registerShapelessRecipe(forge, recipe, key, result);
        }
    }

    private void registerShapedRecipe(Forge forge, Forge.Recipe recipe, NamespacedKey key, ItemStack result) {
        ShapedRecipe shapedRecipe = new ShapedRecipe(key, result);
        shapedRecipe.setCategory(CraftingBookCategory.MISC);

        String[] shape = new String[3];
        Map<Character, RecipeChoice> ingredientMap = new HashMap<>();

        Map<Integer, Forge.ItemReference> slotMaterials = new HashMap<>();
        for (Forge.ItemReference material : recipe.getMaterials()) {
            if (material.hasSlot()) {
                slotMaterials.put(material.getSlot(), material);
            }
        }

        int materialIndex = 0;
        for (int row = 0; row < 3; row++) {
            StringBuilder rowBuilder = new StringBuilder();
            for (int col = 0; col < 3; col++) {
                int slot = row * 3 + col;
                Forge.ItemReference mat = slotMaterials.get(slot);
                if (mat != null) {
                    char c = (char) ('A' + materialIndex);
                    rowBuilder.append(c);
                    RecipeChoice choice = createRecipeChoice(mat);
                    if (choice != null) {
                        ingredientMap.put(c, choice);
                        materialIndex++;
                    } else {
                        rowBuilder.append(' ');
                    }
                } else {
                    rowBuilder.append(' ');
                }
            }
            shape[row] = rowBuilder.toString();
        }

        shapedRecipe.shape(shape);
        for (Map.Entry<Character, RecipeChoice> entry : ingredientMap.entrySet()) {
            shapedRecipe.setIngredient(entry.getKey(), entry.getValue());
        }

        Bukkit.addRecipe(shapedRecipe);
    }

    private void registerShapelessRecipe(Forge forge, Forge.Recipe recipe, NamespacedKey key, ItemStack result) {
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(key, result);
        shapelessRecipe.setCategory(CraftingBookCategory.MISC);

        for (Forge.ItemReference material : recipe.getMaterials()) {
            RecipeChoice choice = createRecipeChoice(material);
            if (choice != null) {
                shapelessRecipe.addIngredient(choice);
                DebugLogger.info("CraftingTable", "  - 添加材料: " + material.getId() + " (amount=" + material.getAmount() + ")");
            }
        }

        Bukkit.addRecipe(shapelessRecipe);
    }

    private RecipeChoice createRecipeChoice(Forge.ItemReference material) {
        if (material.isItemCore()) {
            ItemStack item = materialChecker.resolveItem(material);
            if (item != null) {
                return new RecipeChoice.ExactChoice(item);
            }
            return null;
        } else {
            try {
                Material mat = Material.matchMaterial(material.getId().toUpperCase());
                if (mat != null) {
                    return new RecipeChoice.MaterialChoice(mat);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("无效的材料: " + material.getId());
            }
            return null;
        }
    }

    private ItemStack resolveItemStack(Forge.ItemReference ref) {
        return materialChecker.resolveItem(ref);
    }

    private void unregisterForge(Forge forge) {
        for (Forge.Recipe recipe : forge.getRecipes().values()) {
            NamespacedKey key = new NamespacedKey(plugin, forge.getForgeId() + "/" + recipe.getRecipeId());
            Bukkit.removeRecipe(key);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraftItem(CraftItemEvent event) {
        if (!(event.getView().getPlayer() instanceof Player player)) {
            return;
        }

        Recipe recipe = event.getRecipe();
        if (!(recipe instanceof Keyed keyedRecipe)) {
            return;
        }

        NamespacedKey key = keyedRecipe.getKey();
        DebugLogger.info("CraftingTable", "检测到制作事件: " + key + " (namespace=" + key.getNamespace() + ", key=" + key.getKey() + ")");

        if (!key.getNamespace().equals(plugin.getName().toLowerCase())) {
            return;
        }

        String[] keyParts = key.getKey().split("/", 2);
        if (keyParts.length < 2) {
            return;
        }

        String forgeId = keyParts[0];
        String recipeId = keyParts[1];

        DebugLogger.info("CraftingTable", "匹配到我们的配方: forgeId=" + forgeId + ", recipeId=" + recipeId);

        Forge forge = plugin.getForgeLoader().getForge(forgeId);
        if (forge == null || !forge.isCraftingTableType()) {
            return;
        }

        Forge.Recipe forgeRecipe = forge.getRecipe(recipeId);
        if (forgeRecipe == null) {
            return;
        }

        DebugLogger.info("CraftingTable", "找到配方: " + forgeRecipe.getRecipeId());

        if (!conditionChecker.allConditionsMet(player, forgeRecipe.getConditions())) {
            event.setCancelled(true);
            MessageUtil.sendMessage(player, "conditions-not-met");
            return;
        }

        event.setCancelled(true);

        if (forgeRecipe.isExactPlacement()) {
            processShapedRecipe(event, forgeRecipe, player);
        } else {
            processShapelessRecipe(event, forgeRecipe, player);
        }
    }

    private void processShapedRecipe(CraftItemEvent event, Forge.Recipe recipe, Player player) {
        int minSlotInSavedRec = getMinSlotInSavedRec(recipe);
        int minSlotInEventInv = Integer.MAX_VALUE;
        for (int i = 1; i <= 9; i++) {
            if (event.getInventory().getItem(i) != null && event.getInventory().getItem(i).getType() != Material.AIR) {
                minSlotInEventInv = Math.min(minSlotInEventInv, i);
            }
        }
        int savedRecToEventInvOffset = minSlotInEventInv - minSlotInSavedRec;

        Map<Integer, Integer> requiredAmounts = new HashMap<>();
        for (Forge.ItemReference mat : recipe.getMaterials()) {
            if (mat.hasSlot()) {
                requiredAmounts.put(mat.getSlot(), mat.getAmount());
            }
        }

        int maxCraftsAvailable = Integer.MAX_VALUE;
        for (int slotNum = 0; slotNum < 9; slotNum++) {
            if (!requiredAmounts.containsKey(slotNum)) {
                continue;
            }
            int requiredAmount = requiredAmounts.get(slotNum);
            ItemStack itemStackInGrid = event.getInventory().getItem(slotNum + savedRecToEventInvOffset);
            if (itemStackInGrid == null || itemStackInGrid.getType() == Material.AIR) {
                MessageUtil.sendMessage(player, "materials-insufficient");
                return;
            }
            int craftsAvailable = itemStackInGrid.getAmount() / requiredAmount;
            maxCraftsAvailable = Math.min(maxCraftsAvailable, craftsAvailable);
        }

        if (maxCraftsAvailable == 0) {
            MessageUtil.sendMessage(player, "materials-insufficient");
            return;
        }

        int actualAmountCrafted = processCraft(recipe, player, event, maxCraftsAvailable);
        if (actualAmountCrafted <= 0) {
            return;
        }

        for (int slotNum = 0; slotNum < 9; slotNum++) {
            if (!requiredAmounts.containsKey(slotNum)) {
                continue;
            }
            int requiredAmount = requiredAmounts.get(slotNum);
            ItemStack stack = event.getInventory().getItem(slotNum + savedRecToEventInvOffset);
            if (stack != null) {
                stack.setAmount(stack.getAmount() - requiredAmount * actualAmountCrafted);
                event.getInventory().setItem(slotNum + savedRecToEventInvOffset, stack);
            }
        }

        MessageUtil.sendMessage(player, "crafting-complete", "result", recipe.getOutput().getId());
    }

    private int getMinSlotInSavedRec(Forge.Recipe recipe) {
        int minSlot = Integer.MAX_VALUE;
        for (Forge.ItemReference mat : recipe.getMaterials()) {
            if (mat.hasSlot()) {
                minSlot = Math.min(minSlot, mat.getSlot());
            }
        }
        return minSlot;
    }

    private void processShapelessRecipe(CraftItemEvent event, Forge.Recipe recipe, Player player) {
        DebugLogger.info("CraftingTable", "处理无序配方: " + recipe.getRecipeId());

        List<Forge.ItemReference> requiredMaterials = new ArrayList<>(recipe.getMaterials());
        List<ItemStack> inventorySlotItems = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            inventorySlotItems.add(event.getInventory().getItem(i));
        }

        int[] requiredSlotMapping = new int[9];
        int maxCraftsAvailable = Integer.MAX_VALUE;

        requiredMaterials.sort((a, b) -> Integer.compare(b.getAmount(), a.getAmount()));

        for (int reqIndex = 0; reqIndex < requiredMaterials.size(); reqIndex++) {
            Forge.ItemReference reqMat = requiredMaterials.get(reqIndex);
            int highestCount = 0;
            int highestCountSlot = -1;
            for (int slotNum = 0; slotNum < 9; slotNum++) {
                ItemStack invItem = inventorySlotItems.get(slotNum);
                if (invItem == null || invItem.getType() == Material.AIR) {
                    continue;
                }
                if (matchesMaterial(invItem, reqMat)) {
                    if (highestCount < invItem.getAmount()) {
                        highestCount = invItem.getAmount();
                        highestCountSlot = slotNum;
                    }
                }
            }
            if (highestCountSlot == -1) {
                DebugLogger.warning("CraftingTable", "找不到匹配的材料: " + reqMat.getId());
                MessageUtil.sendMessage(player, "materials-insufficient");
                return;
            }
            inventorySlotItems.set(highestCountSlot, null);
            int craftsPossible = highestCount / reqMat.getAmount();
            maxCraftsAvailable = Math.min(maxCraftsAvailable, craftsPossible);
            requiredSlotMapping[highestCountSlot] = reqIndex;
            DebugLogger.info("CraftingTable", "  材料 " + reqMat.getId() + " (需要" + reqMat.getAmount() + ") -> 槽位 " + highestCountSlot + " (有" + highestCount + "), 可制作 " + craftsPossible + " 次");
        }

        if (maxCraftsAvailable == 0) {
            MessageUtil.sendMessage(player, "materials-insufficient");
            return;
        }

        DebugLogger.info("CraftingTable", "最大可制作次数: " + maxCraftsAvailable);

        int actualAmountCrafted = processCraft(recipe, player, event, maxCraftsAvailable);
        DebugLogger.info("CraftingTable", "实际制作次数: " + actualAmountCrafted);
        if (actualAmountCrafted <= 0) {
            return;
        }

        for (int slotIndex = 0; slotIndex < 9; slotIndex++) {
            ItemStack stack = event.getInventory().getItem(slotIndex + 1);
            if (stack == null || stack.getType() == Material.AIR) {
                continue;
            }
            int requiredAmount = requiredMaterials.get(requiredSlotMapping[slotIndex]).getAmount();
            int removeAmount = requiredAmount * actualAmountCrafted;
            DebugLogger.info("CraftingTable", "  槽位 " + slotIndex + ": 扣除 " + removeAmount + " (原有 " + stack.getAmount() + ")");
            stack.setAmount(stack.getAmount() - removeAmount);
            event.getInventory().setItem(slotIndex + 1, stack);
        }

        MessageUtil.sendMessage(player, "crafting-complete", "result", recipe.getOutput().getId());
    }

    private boolean matchesMaterial(ItemStack item, Forge.ItemReference ref) {
        if (ref.isItemCore()) {
            ItemStack sample = materialChecker.resolveItem(ref);
            return sample != null && item.isSimilar(sample);
        } else {
            try {
                Material mat = Material.valueOf(ref.getId().toUpperCase());
                return item.getType() == mat;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
    }

    private int processCraft(Forge.Recipe recipe, Player player, CraftItemEvent event, int maxCraftsAvailable) {
        ItemStack result = resolveItemStack(recipe.getOutput());
        if (result == null) {
            return 0;
        }

        int actualAmountCrafted;
        if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
            actualAmountCrafted = performShiftClickCraft(player, result, maxCraftsAvailable);
        } else {
            if (player.getItemOnCursor() == null || player.getItemOnCursor().getType() == Material.AIR) {
                actualAmountCrafted = 1;
                player.setItemOnCursor(result.clone());
            } else if (player.getItemOnCursor().isSimilar(result)
                    && player.getItemOnCursor().getAmount() + result.getAmount() <= result.getMaxStackSize()) {
                actualAmountCrafted = 1;
                player.getItemOnCursor().setAmount(result.getAmount() + player.getItemOnCursor().getAmount());
            } else {
                return 0;
            }
        }

        return actualAmountCrafted;
    }

    private int performShiftClickCraft(Player player, ItemStack result, int maxCraftsAvailable) {
        int resultAmount = result.getAmount();
        int maxStackSize = result.getMaxStackSize();
        int itemsToAdd = resultAmount * maxCraftsAvailable;
        int itemsAdded = 0;
        int craftsMade = 0;

        while (itemsAdded < itemsToAdd && craftsMade < maxCraftsAvailable) {
            int canAddNow = Math.min(resultAmount, itemsToAdd - itemsAdded);
            ItemStack toAdd = result.clone();
            toAdd.setAmount(canAddNow);

            HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(toAdd);
            if (leftover.isEmpty()) {
                itemsAdded += canAddNow;
                craftsMade++;
            } else {
                break;
            }
        }

        return craftsMade;
    }

    public void registerEvents() {
        PluginManager pm = plugin.getServer().getPluginManager();
        pm.registerEvents(this, plugin);
    }
}
