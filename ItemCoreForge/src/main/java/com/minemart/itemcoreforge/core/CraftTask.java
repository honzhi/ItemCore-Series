package com.minemart.itemcoreforge.core;

import org.bukkit.entity.Player;

import java.util.UUID;

public class CraftTask {

    public enum TaskStatus {
        CRAFTING,
        COMPLETED,
        CLAIMED
    }

    private final UUID taskId;
    private final UUID playerId;
    private final String forgeId;
    private final String recipeId;
    private final int amount;
    private final long startTime;
    private final long duration;
    private TaskStatus status = TaskStatus.CRAFTING;
    private boolean cancelled = false;

    public CraftTask(Player player, Forge forge, Forge.Recipe recipe, int amount) {
        this.taskId = UUID.randomUUID();
        this.playerId = player.getUniqueId();
        this.forgeId = forge.getForgeId();
        this.recipeId = recipe.getRecipeId();
        this.amount = amount;
        this.startTime = System.currentTimeMillis();
        this.duration = (long) (recipe.getCraftTime() * 1000);
    }
    
    public CraftTask(Player player, Forge forge, Forge.Recipe recipe, int amount, long remainingTime) {
        this.taskId = UUID.randomUUID();
        this.playerId = player.getUniqueId();
        this.forgeId = forge.getForgeId();
        this.recipeId = recipe.getRecipeId();
        this.amount = amount;
        this.duration = (long) (recipe.getCraftTime() * 1000);
        this.startTime = System.currentTimeMillis() - (this.duration - remainingTime);
    }

    public UUID getTaskId() {
        return taskId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getForgeId() {
        return forgeId;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public int getAmount() {
        return amount;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return duration;
    }

    public long getRemainingTime() {
        if (status == TaskStatus.COMPLETED || status == TaskStatus.CLAIMED) {
            return 0;
        }
        long elapsed = System.currentTimeMillis() - startTime;
        long remaining = duration - elapsed;
        return Math.max(0, remaining);
    }

    public double getRemainingSeconds() {
        return getRemainingTime() / 1000.0;
    }

    public double getProgress() {
        if (duration <= 0) {
            return 1.0;
        }
        if (status == TaskStatus.COMPLETED || status == TaskStatus.CLAIMED) {
            return 1.0;
        }
        long elapsed = System.currentTimeMillis() - startTime;
        return Math.min(1.0, (double) elapsed / duration);
    }

    public boolean isComplete() {
        return status == TaskStatus.COMPLETED || getRemainingTime() <= 0;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void cancel() {
        this.cancelled = true;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public boolean isCompleted() {
        return status == TaskStatus.COMPLETED;
    }

    public boolean isClaimed() {
        return status == TaskStatus.CLAIMED;
    }
}
