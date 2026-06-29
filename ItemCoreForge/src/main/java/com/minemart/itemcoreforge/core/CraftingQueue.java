package com.minemart.itemcoreforge.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CraftingQueue {

    private final String forgeId;
    private final int maxSize;
    private final List<QueueItem> items = new ArrayList<>();

    public CraftingQueue(String forgeId, int maxSize) {
        this.forgeId = forgeId;
        this.maxSize = Math.min(Math.max(maxSize, 1), 64);
    }

    public String getForgeId() {
        return forgeId;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public boolean isFull() {
        return items.size() >= maxSize;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int size() {
        return items.size();
    }

    public int getActiveCount() {
        int count = 0;
        for (QueueItem item : items) {
            if (item.isActive()) {
                count++;
            }
        }
        return count;
    }

    public List<QueueItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public List<QueueItem> getActiveItems() {
        List<QueueItem> active = new ArrayList<>();
        for (QueueItem item : items) {
            if (item.isActive()) {
                active.add(item);
            }
        }
        return active;
    }

    public QueueItem add(String recipeId, int amount, long craftingTimeMs) {
        if (isFull()) {
            return null;
        }

        long start = System.currentTimeMillis();
        long completion = items.isEmpty()
            ? start + craftingTimeMs
            : Math.max(items.get(items.size() - 1).getCompletion(), start) + craftingTimeMs;

        QueueItem item = new QueueItem(forgeId, recipeId, amount, start, completion);
        items.add(item);
        return item;
    }

    public QueueItem get(UUID taskId) {
        for (QueueItem item : items) {
            if (item.getTaskId().equals(taskId)) {
                return item;
            }
        }
        return null;
    }

    public int indexOf(UUID taskId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getTaskId().equals(taskId)) {
                return i;
            }
        }
        return -1;
    }

    public QueueItem getItemAtSlot(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < items.size()) {
            QueueItem item = items.get(slotIndex);
            if (item.isActive()) {
                return item;
            }
        }
        return null;
    }

    public QueueItem getCurrentCrafting() {
        if (!items.isEmpty() && items.get(0).isCrafting()) {
            return items.get(0);
        }
        return null;
    }

    public List<QueueItem> getCompleted() {
        List<QueueItem> completed = new ArrayList<>();
        for (QueueItem item : items) {
            if (item.isReady()) {
                completed.add(item);
            }
        }
        return completed;
    }

    public void removeForClaim(UUID taskId) {
        int index = indexOf(taskId);
        if (index >= 0) {
            QueueItem item = items.get(index);
            item.setClaimed(true);
            items.remove(index);
            
            // 更新后续任务的完成时间
            if (index < items.size()) {
                long savedTime = item.getCompletion() - System.currentTimeMillis();
                if (savedTime > 0) {
                    for (int i = index; i < items.size(); i++) {
                        items.get(i).advanceCompletion(savedTime);
                    }
                }
            }
        }
    }

    public void removeForCancel(UUID taskId, long craftingTimeMs) {
        int index = indexOf(taskId);
        if (index < 0) {
            return;
        }

        QueueItem removed = items.get(index);

        if (removed.isReady()) {
            removed.setCancelled(true);
            items.remove(index);
            return;
        }

        long savedTime = Math.min(removed.getRemaining(), craftingTimeMs);
        removed.setCancelled(true);
        items.remove(index);

        for (int i = index; i < items.size(); i++) {
            items.get(i).advanceCompletion(savedTime);
        }
    }

    public void addRestored(QueueItem item) {
        items.add(item);
    }

    public void remove(UUID taskId) {
        items.removeIf(item -> item.getTaskId().equals(taskId));
    }

    public void clear() {
        items.clear();
    }
}