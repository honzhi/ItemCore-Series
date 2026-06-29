package com.minemart.itemcoreforge.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

public class RecipeQueue {

    private final int maxSize;
    private CraftTask currentTask;
    private final Queue<CraftTask> waitingTasks = new LinkedList<>();
    private final List<CraftTask> completedTasks = new ArrayList<>();

    public RecipeQueue(int maxSize) {
        this.maxSize = Math.min(Math.max(maxSize, 1), 64);
    }

    public boolean addTask(CraftTask task) {
        if (getQueueSize() >= maxSize) {
            return false;
        }
        
        if (currentTask == null || currentTask.isClaimed()) {
            currentTask = task;
        } else {
            waitingTasks.offer(task);
        }
        
        return true;
    }

    public CraftTask getCurrentTask() {
        return currentTask;
    }

    public CraftTask getTask(UUID taskId) {
        if (currentTask != null && currentTask.getTaskId().equals(taskId)) {
            return currentTask;
        }
        for (CraftTask task : waitingTasks) {
            if (task.getTaskId().equals(taskId)) {
                return task;
            }
        }
        for (CraftTask task : completedTasks) {
            if (task.getTaskId().equals(taskId)) {
                return task;
            }
        }
        return null;
    }

    public int getTaskIndex(UUID taskId) {
        List<CraftTask> allTasks = getAllTasks();
        for (int i = 0; i < allTasks.size(); i++) {
            if (allTasks.get(i).getTaskId().equals(taskId)) {
                return i;
            }
        }
        return -1;
    }

    public void markTaskCompleted(UUID taskId) {
        if (currentTask != null && currentTask.getTaskId().equals(taskId)) {
            currentTask.setStatus(CraftTask.TaskStatus.COMPLETED);
            completedTasks.add(currentTask);
            currentTask = null;
            startNextTask();
        } else {
            for (CraftTask task : waitingTasks) {
                if (task.getTaskId().equals(taskId)) {
                    task.setStatus(CraftTask.TaskStatus.COMPLETED);
                    completedTasks.add(task);
                    waitingTasks.remove(task);
                    break;
                }
            }
        }
    }

    public void removeClaimedTask(UUID taskId) {
        Iterator<CraftTask> it = completedTasks.iterator();
        while (it.hasNext()) {
            CraftTask task = it.next();
            if (task.getTaskId().equals(taskId)) {
                task.setStatus(CraftTask.TaskStatus.CLAIMED);
                it.remove();
                break;
            }
        }
    }

    public boolean cancelTask(UUID taskId) {
        if (currentTask != null && currentTask.getTaskId().equals(taskId)) {
            if (currentTask.getStatus() == CraftTask.TaskStatus.CRAFTING) {
                currentTask.cancel();
                currentTask = null;
                startNextTask();
                return true;
            }
            return false;
        }
        
        Iterator<CraftTask> it = waitingTasks.iterator();
        while (it.hasNext()) {
            CraftTask task = it.next();
            if (task.getTaskId().equals(taskId)) {
                task.cancel();
                it.remove();
                return true;
            }
        }
        return false;
    }

    private void startNextTask() {
        if (currentTask == null && !waitingTasks.isEmpty()) {
            currentTask = waitingTasks.poll();
        }
    }

    public boolean hasNextTask() {
        return !waitingTasks.isEmpty();
    }

    public int getQueueSize() {
        return (currentTask != null ? 1 : 0) + waitingTasks.size() + completedTasks.size();
    }

    public int getWaitingCount() {
        return waitingTasks.size();
    }

    public int getCompletedCount() {
        return completedTasks.size();
    }

    public int getMaxSize() {
        return maxSize;
    }

    public boolean isFull() {
        return getQueueSize() >= maxSize;
    }

    public boolean isEmpty() {
        return currentTask == null && waitingTasks.isEmpty() && completedTasks.isEmpty();
    }

    public void cancelAll() {
        if (currentTask != null) {
            currentTask.cancel();
        }
        for (CraftTask task : waitingTasks) {
            task.cancel();
        }
        waitingTasks.clear();
        currentTask = null;
    }

    public List<CraftTask> getAllTasks() {
        List<CraftTask> allTasks = new ArrayList<>();
        if (currentTask != null) {
            allTasks.add(currentTask);
        }
        allTasks.addAll(waitingTasks);
        allTasks.addAll(completedTasks);
        return allTasks;
    }

    public List<CraftTask> getWaitingTasks() {
        return new ArrayList<>(waitingTasks);
    }

    public List<CraftTask> getCompletedTasks() {
        return new ArrayList<>(completedTasks);
    }
}
