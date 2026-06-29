package com.minemart.itemcoreforge.task;

import com.minemart.itemcoreforge.ItemCoreForge;
import com.minemart.itemcoreforge.api.event.RecipeCraftedEvent;
import com.minemart.itemcoreforge.api.event.RecipePreCraftEvent;
import com.minemart.itemcoreforge.api.event.RecipeQueueEvent;
import com.minemart.itemcoreforge.core.CraftingQueue;
import com.minemart.itemcoreforge.core.CraftResult;
import com.minemart.itemcoreforge.core.Forge;
import com.minemart.itemcoreforge.core.QueueItem;
import com.minemart.itemcoreforge.trigger.TriggerManager;
import com.minemart.itemcoreforge.utils.ConditionChecker;
import com.minemart.itemcoreforge.utils.MaterialChecker;
import com.minemart.itemcoreforge.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CraftingQueueManager {

    private final ItemCoreForge plugin;
    private final Map<UUID, Map<String, CraftingQueue>> playerQueues = new ConcurrentHashMap<>();
    private BukkitTask checkTask;

    public CraftingQueueManager(ItemCoreForge plugin) {
        this.plugin = plugin;
        startCheckTask();
    }

    private void startCheckTask() {
        checkTask = Bukkit.getScheduler().runTaskTimer(plugin, this::checkCompletedTasks, 20L, 20L);
    }

    private void checkCompletedTasks() {
        for (UUID playerId : playerQueues.keySet()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player == null || !player.isOnline()) {
                continue;
            }

            Map<String, CraftingQueue> queues = playerQueues.get(playerId);
            if (queues == null) continue;

            for (CraftingQueue queue : queues.values()) {
                for (QueueItem item : queue.getCompleted()) {
                    plugin.getGuiListener().updatePlayerQueueDisplay(player);
                }
            }
        }
    }

    public CraftingQueue getQueue(Player player, String forgeId) {
        UUID playerId = player.getUniqueId();

        Map<String, CraftingQueue> playerForges = playerQueues.computeIfAbsent(
            playerId,
            k -> new ConcurrentHashMap<>()
        );

        return playerForges.computeIfAbsent(
            forgeId,
            k -> {
                Forge forge = plugin.getForgeLoader().getForge(forgeId);
                int maxSize = forge != null ? forge.getMaxQueueSize() : 6;
                return new CraftingQueue(forgeId, maxSize);
            }
        );
    }

    public CraftResult enqueueCraft(Player player, String forgeId, String recipeId) {
        Forge forge = plugin.getForgeLoader().getForge(forgeId);
        if (forge == null) {
            return CraftResult.failure("锻造台不存在: " + forgeId);
        }

        Forge.Recipe recipe = forge.getRecipe(recipeId);
        if (recipe == null) {
            return CraftResult.failure("配方不存在: " + recipeId);
        }

        CraftingQueue queue = getQueue(player, forgeId);
        if (queue.isFull()) {
            return CraftResult.queueFull();
        }

        MaterialChecker materialChecker = new MaterialChecker();
        MaterialChecker.MaterialCheckResult materialResult = materialChecker.checkMaterials(
            player,
            recipe.getMaterials()
        );

        if (!materialResult.isSuccess()) {
            return CraftResult.materialsInsufficient(materialResult.getFailReason());
        }

        ConditionChecker conditionChecker = new ConditionChecker();
        if (!conditionChecker.allConditionsMet(player, recipe.getConditions())) {
            return CraftResult.conditionsNotMet("条件未满足");
        }

        if (!materialChecker.consumeMaterials(player, recipe.getMaterials())) {
            return CraftResult.failure("材料消耗失败");
        }

        long craftingTimeMs = (long) (recipe.getCraftTime() * 1000);

        if (craftingTimeMs <= 0) {
            TriggerManager.executeTriggers(player, recipe.getOnStart());
            TriggerManager.executeTriggers(player, recipe.getOnClaim());
            giveOutput(player, recipe);
            MessageUtil.sendMessage(player, "crafting-started",
                "recipe", recipeId,
                "time", "0"
            );
            MessageUtil.sendMessage(player, "crafting-complete",
                "result", recipe.getOutput().getId()
            );
            RecipeQueueEvent queueEvent = new RecipeQueueEvent(player, forge, null);
            Bukkit.getPluginManager().callEvent(queueEvent);
            plugin.getGuiListener().updatePlayerQueueDisplay(player);
            return CraftResult.success(null);
        }

        QueueItem item = queue.add(recipeId, 1, craftingTimeMs);
        if (item == null) {
            materialChecker.giveMaterials(player, recipe.getMaterials());
            return CraftResult.queueFull();
        }

        TriggerManager.executeTriggers(player, recipe.getOnStart());

        RecipeQueueEvent queueEvent = new RecipeQueueEvent(player, forge, null);
        Bukkit.getPluginManager().callEvent(queueEvent);

        plugin.getGuiListener().updatePlayerQueueDisplay(player);

        MessageUtil.sendMessage(player, "crafting-started",
            "recipe", recipeId,
            "time", String.valueOf((int) Math.ceil(recipe.getCraftTime()))
        );

        return CraftResult.success(item);
    }

    public CraftResult cancelCraft(Player player, String forgeId, UUID taskId) {
        CraftingQueue queue = getQueue(player, forgeId);
        QueueItem item = queue.get(taskId);

        if (item == null) {
            return CraftResult.failure("任务不存在");
        }

        if (item.isClaimed()) {
            return CraftResult.failure("任务已领取，无法取消");
        }

        if (item.isReady()) {
            return CraftResult.failure("任务已完成，无法取消");
        }

        Forge forge = plugin.getForgeLoader().getForge(forgeId);
        if (forge == null) {
            return CraftResult.failure("锻造台不存在");
        }

        Forge.Recipe recipe = forge.getRecipe(item.getRecipeId());
        if (recipe == null) {
            return CraftResult.failure("配方不存在");
        }

        MaterialChecker materialChecker = new MaterialChecker();
        if (!materialChecker.canGiveMaterials(player, recipe.getMaterials())) {
            return CraftResult.failure("背包已满，请清理背包后再取消制作");
        }

        long craftingTimeMs = (long) (recipe.getCraftTime() * 1000);
        queue.removeForCancel(taskId, craftingTimeMs);

        materialChecker.giveMaterials(player, recipe.getMaterials());

        TriggerManager.executeTriggers(player, recipe.getOnCancel());

        RecipeQueueEvent queueEvent = new RecipeQueueEvent(player, forge, null);
        Bukkit.getPluginManager().callEvent(queueEvent);

        plugin.getGuiListener().updatePlayerQueueDisplay(player);

        MessageUtil.sendMessage(player, "crafting-cancelled",
            "recipe", recipe.getRecipeId()
        );

        return CraftResult.success(item);
    }

    public CraftResult claimItem(Player player, String forgeId, UUID taskId) {
        CraftingQueue queue = getQueue(player, forgeId);
        QueueItem item = queue.get(taskId);

        if (item == null) {
            return CraftResult.failure("任务不存在");
        }

        if (!item.isReady() && !item.isCompleted()) {
            return CraftResult.failure("任务尚未完成");
        }

        if (item.isClaimed()) {
            return CraftResult.failure("任务已领取");
        }

        Forge forge = plugin.getForgeLoader().getForge(forgeId);
        if (forge == null) {
            return CraftResult.failure("锻造台不存在");
        }

        Forge.Recipe recipe = forge.getRecipe(item.getRecipeId());
        if (recipe == null) {
            return CraftResult.failure("配方不存在");
        }

        return internalClaim(player, queue, item, forge, recipe);
    }

    private CraftResult internalClaim(Player player, CraftingQueue queue, QueueItem item, Forge forge, Forge.Recipe recipe) {
        RecipePreCraftEvent preEvent = new RecipePreCraftEvent(player, recipe, null);
        Bukkit.getPluginManager().callEvent(preEvent);

        if (preEvent.isCancelled()) {
            return CraftResult.failure("制作被取消");
        }

        if (!canGiveOutput(player, recipe)) {
            return CraftResult.failure("背包已满，请清理背包后再领取");
        }

        queue.removeForClaim(item.getTaskId());

        giveOutput(player, recipe);

        TriggerManager.executeTriggers(player, recipe.getOnClaim());

        RecipeCraftedEvent craftedEvent = new RecipeCraftedEvent(player, recipe, null, true);
        Bukkit.getPluginManager().callEvent(craftedEvent);

        RecipeQueueEvent queueEvent = new RecipeQueueEvent(player, forge, null);
        Bukkit.getPluginManager().callEvent(queueEvent);

        plugin.getGuiListener().updatePlayerQueueDisplay(player);

        MessageUtil.sendMessage(player, "item-claimed",
            "result", recipe.getOutput().getId()
        );

        return CraftResult.success(item);
    }

    private boolean canGiveOutput(Player player, Forge.Recipe recipe) {
        Forge.ItemReference outputRef = recipe.getOutput();
        Inventory inventory = player.getInventory();

        if (outputRef.getSource().equalsIgnoreCase("itemcore")) {
            for (int i = 0; i < 36; i++) {
                ItemStack item = inventory.getItem(i);
                if (item == null || item.getType() == Material.AIR) {
                    return true;
                }
            }
            return false;
        } else {
            Material material = Material.matchMaterial(outputRef.getId().toUpperCase());
            if (material == null) {
                material = Material.STONE;
            }
            ItemStack output = new ItemStack(material, 1);
            for (int i = 0; i < 36; i++) {
                ItemStack item = inventory.getItem(i);
                if (item == null || item.getType() == Material.AIR) {
                    return true;
                }
                if (item.isSimilar(output) && item.getAmount() + outputRef.getAmount() <= item.getMaxStackSize()) {
                    return true;
                }
            }
            return false;
        }
    }

    private void giveOutput(Player player, Forge.Recipe recipe) {
        Forge.ItemReference outputRef = recipe.getOutput();
        com.minemart.itemcoreforge.utils.ItemReference ref = new com.minemart.itemcoreforge.utils.ItemReference();
        ref.setSource(outputRef.getSource());
        ref.setCategory(outputRef.getCategory());
        ref.setId(outputRef.getId());
        ref.setAmount(outputRef.getAmount());
        
        ItemStack item = ref.toItemStack();
        if (item != null && item.getType() != Material.AIR && item.getType() != Material.BARRIER) {
            player.getInventory().addItem(item);
        }
    }

    public Map<UUID, Map<String, CraftingQueue>> getAllPlayerQueues() {
        return playerQueues;
    }

    public Map<String, CraftingQueue> getAllQueues(Player player) {
        return playerQueues.getOrDefault(player.getUniqueId(), new ConcurrentHashMap<>());
    }

    public boolean hasActiveQueue(Player player) {
        Map<String, CraftingQueue> queues = playerQueues.get(player.getUniqueId());
        if (queues == null || queues.isEmpty()) {
            return false;
        }

        for (CraftingQueue queue : queues.values()) {
            if (!queue.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    public int getTotalQueueSize(Player player) {
        Map<String, CraftingQueue> queues = playerQueues.get(player.getUniqueId());
        if (queues == null) {
            return 0;
        }

        int total = 0;
        for (CraftingQueue queue : queues.values()) {
            total += queue.getActiveCount();
        }
        return total;
    }

    public void clearQueue(Player player, String forgeId) {
        CraftingQueue queue = getQueue(player, forgeId);
        queue.clear();
    }

    public void clearAllQueues(Player player) {
        Map<String, CraftingQueue> queues = playerQueues.get(player.getUniqueId());
        if (queues != null) {
            for (CraftingQueue queue : queues.values()) {
                queue.clear();
            }
            queues.clear();
        }
    }

    public void removePlayer(UUID playerId) {
        playerQueues.remove(playerId);
    }

    public void shutdown() {
        if (checkTask != null) {
            checkTask.cancel();
        }
        playerQueues.clear();
    }

    public double getRemainingSeconds(UUID taskId) {
        for (Map<String, CraftingQueue> queues : playerQueues.values()) {
            for (CraftingQueue queue : queues.values()) {
                QueueItem item = queue.get(taskId);
                if (item != null) {
                    return item.getRemainingSeconds();
                }
            }
        }
        return 0;
    }
}