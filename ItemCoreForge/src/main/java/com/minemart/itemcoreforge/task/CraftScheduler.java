package com.minemart.itemcoreforge.task;

import com.minemart.itemcoreforge.ItemCoreForge;
import com.minemart.itemcoreforge.api.event.RecipeCraftedEvent;
import com.minemart.itemcoreforge.api.event.RecipePreCraftEvent;
import com.minemart.itemcoreforge.api.event.RecipeQueueEvent;
import com.minemart.itemcoreforge.core.*;
import com.minemart.itemcoreforge.utils.ConditionChecker;
import com.minemart.itemcoreforge.utils.MaterialChecker;
import com.minemart.itemcoreforge.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CraftScheduler {

    private final ItemCoreForge plugin;
    private final Map<UUID, BukkitTask> activeTasks = new ConcurrentHashMap<>();
    private final Map<UUID, Long> taskEndTimes = new ConcurrentHashMap<>();
    private BukkitTask updateTask;

    public CraftScheduler(ItemCoreForge plugin) {
        this.plugin = plugin;
        startUpdateTask();
    }

    private void startUpdateTask() {
        updateTask = Bukkit.getScheduler().runTaskTimer(plugin, this::updateAllQueues, 20L, 20L);
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

        CraftingQueue queue = plugin.getCraftingQueueManager().getQueue(player, forgeId);
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

        int successRate = recipe.getSuccessRate();
        boolean consumeOnFail = recipe.isConsumeOnFail();
        
        if (successRate < 100) {
            java.util.Random random = new java.util.Random();
            int roll = random.nextInt(100) + 1;
            if (roll > successRate) {
                if (!consumeOnFail) {
                    materialChecker.giveMaterials(player, recipe.getMaterials());
                }
                return CraftResult.failure("制作失败");
            }
        }

        long craftingTimeMs = (long) (recipe.getCraftTime() * 1000);
        QueueItem item = queue.add(recipeId, 1, craftingTimeMs);

        if (item == null) {
            materialChecker.giveMaterials(player, recipe.getMaterials());
            return CraftResult.queueFull();
        }

        RecipeQueueEvent queueEvent = new RecipeQueueEvent(player, forge, null);
        Bukkit.getPluginManager().callEvent(queueEvent);

        plugin.getGuiListener().updatePlayerQueueDisplay(player);

        MessageUtil.sendMessage(player, "crafting-started", 
            "recipe", recipeId,
            "time", String.valueOf((int) Math.ceil(recipe.getCraftTime()))
        );

        return CraftResult.success(item);
    }

    private void executeCraft(Player player, QueueItem item, CraftingQueue queue) {
        if (item.isCancelled() || item.isClaimed()) {
            return;
        }

        Forge forge = plugin.getForgeLoader().getForge(item.getForgeId());
        if (forge == null) {
            return;
        }

        Forge.Recipe recipe = forge.getRecipe(item.getRecipeId());
        if (recipe == null) {
            return;
        }

        RecipePreCraftEvent preEvent = new RecipePreCraftEvent(player, recipe, null);
        Bukkit.getPluginManager().callEvent(preEvent);
        
        if (preEvent.isCancelled()) {
            return;
        }

        RecipeCraftedEvent craftedEvent = new RecipeCraftedEvent(player, recipe, null, true);
        Bukkit.getPluginManager().callEvent(craftedEvent);

        MessageUtil.sendMessage(player, "crafting-complete", 
            "result", recipe.getOutput().getId()
        );

        RecipeQueueEvent queueEvent = new RecipeQueueEvent(player, forge, null);
        Bukkit.getPluginManager().callEvent(queueEvent);

        plugin.getGuiListener().updatePlayerQueueDisplay(player);
    }

    public CraftResult cancelCraft(Player player, String forgeId, UUID taskId) {
        CraftingQueue queue = plugin.getCraftingQueueManager().getQueue(player, forgeId);
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

        RecipeQueueEvent queueEvent = new RecipeQueueEvent(player, forge, null);
        Bukkit.getPluginManager().callEvent(queueEvent);

        plugin.getGuiListener().updatePlayerQueueDisplay(player);

        MessageUtil.sendMessage(player, "crafting-cancelled", 
            "recipe", recipe.getRecipeId()
        );

        return CraftResult.success(item);
    }

    public CraftResult claimItem(Player player, String forgeId, UUID taskId) {
        CraftingQueue queue = plugin.getCraftingQueueManager().getQueue(player, forgeId);
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

        RecipePreCraftEvent preEvent = new RecipePreCraftEvent(player, recipe, null);
        Bukkit.getPluginManager().callEvent(preEvent);

        if (preEvent.isCancelled()) {
            return CraftResult.failure("制作被取消");
        }

        MaterialChecker materialChecker = new MaterialChecker();
        if (!materialChecker.canGiveMaterials(player, recipe.getMaterials())) {
            return CraftResult.failure("背包已满，请清理背包后再领取");
        }

        queue.removeForClaim(taskId);

        giveOutput(player, recipe);

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

    public void cancelTask(Player player, UUID taskId) {
        BukkitTask bukkitTask = activeTasks.remove(taskId);
        if (bukkitTask != null) {
            bukkitTask.cancel();
        }
        taskEndTimes.remove(taskId);
    }

    public long getRemainingTime(UUID taskId) {
        Long endTime = taskEndTimes.get(taskId);
        if (endTime == null) {
            return 0;
        }
        return Math.max(0, endTime - System.currentTimeMillis());
    }

    public double getRemainingSeconds(UUID taskId) {
        return getRemainingTime(taskId) / 1000.0;
    }

    private void updateAllQueues() {
        for (Map<String, CraftingQueue> queues : plugin.getCraftingQueueManager().getAllPlayerQueues().values()) {
            for (CraftingQueue queue : queues.values()) {
                for (QueueItem item : queue.getCompleted()) {
                    // 队列更新由 CraftingQueueManager 的 checkTask 处理
                }
            }
        }
    }

    public void shutdown() {
        if (updateTask != null) {
            updateTask.cancel();
        }
        
        for (BukkitTask task : activeTasks.values()) {
            task.cancel();
        }
        activeTasks.clear();
        taskEndTimes.clear();
    }

    public boolean hasActiveTask(UUID taskId) {
        return activeTasks.containsKey(taskId);
    }
}