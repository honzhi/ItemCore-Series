package com.minemart.itemcoreforge.core;

import java.util.UUID;

public class QueueItem {

    private final UUID taskId;
    private final String forgeId;
    private final String recipeId;
    private final int amount;
    private final long start;
    private long completion;
    private boolean claimed = false;
    private boolean cancelled = false;

    public QueueItem(String forgeId, String recipeId, int amount, long start, long completion) {
        this.taskId = UUID.randomUUID();
        this.forgeId = forgeId;
        this.recipeId = recipeId;
        this.amount = amount;
        this.start = start;
        this.completion = completion;
    }

    public QueueItem(UUID taskId, String forgeId, String recipeId, int amount, long start, long completion) {
        this.taskId = taskId;
        this.forgeId = forgeId;
        this.recipeId = recipeId;
        this.amount = amount;
        this.start = start;
        this.completion = completion;
    }

    public UUID getTaskId() {
        return taskId;
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

    public long getStart() {
        return start;
    }

    public long getCompletion() {
        return completion;
    }

    public void setCompletion(long completion) {
        this.completion = completion;
    }

    public void advanceCompletion(long amount) {
        this.completion -= amount;
    }

    public boolean isReady() {
        return getRemaining() <= 0 && !claimed && !cancelled;
    }

    public long getRemaining() {
        if (claimed || cancelled) {
            return 0;
        }
        return Math.max(0, completion - System.currentTimeMillis());
    }

    public double getRemainingSeconds() {
        return getRemaining() / 1000.0;
    }

    public long getElapsed() {
        return System.currentTimeMillis() - start;
    }

    public double getProgress() {
        long total = completion - start;
        if (total <= 0) {
            return 1.0;
        }
        return Math.min(1.0, (double) getElapsed() / total);
    }

    public boolean isClaimed() {
        return claimed;
    }

    public void setClaimed(boolean claimed) {
        this.claimed = claimed;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCompleted() {
        return isReady() || claimed;
    }

    public boolean isCrafting() {
        return !claimed && !cancelled && getRemaining() > 0;
    }

    public boolean isActive() {
        return !claimed && !cancelled;
    }
}