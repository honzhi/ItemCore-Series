package com.minemart.itemcoreforge.task;

import com.minemart.itemcoreforge.ItemCoreForge;
import com.minemart.itemcoreforge.core.CraftTask;
import com.minemart.itemcoreforge.core.Forge;
import com.minemart.itemcoreforge.core.RecipeQueue;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerQueueManager {

    private final ItemCoreForge plugin;
    private final Map<UUID, Map<String, RecipeQueue>> playerQueues = new ConcurrentHashMap<>();

    public PlayerQueueManager(ItemCoreForge plugin) {
        this.plugin = plugin;
    }

    public RecipeQueue getQueue(Player player, String forgeId) {
        UUID playerId = player.getUniqueId();
        
        Map<String, RecipeQueue> playerForges = playerQueues.computeIfAbsent(
            playerId, 
            k -> new ConcurrentHashMap<>()
        );
        
        return playerForges.computeIfAbsent(
            forgeId,
            k -> {
                Forge forge = plugin.getForgeLoader().getForge(forgeId);
                int maxSize = forge != null ? forge.getMaxQueueSize() : 6;
                return new RecipeQueue(maxSize);
            }
        );
    }

    public Map<String, RecipeQueue> getAllQueues(Player player) {
        return playerQueues.getOrDefault(player.getUniqueId(), new ConcurrentHashMap<>());
    }

    public boolean hasActiveQueue(Player player) {
        Map<String, RecipeQueue> queues = playerQueues.get(player.getUniqueId());
        if (queues == null || queues.isEmpty()) {
            return false;
        }
        
        for (RecipeQueue queue : queues.values()) {
            if (!queue.isEmpty()) {
                return true;
            }
        }
        
        return false;
    }

    public CraftTask getCurrentTask(Player player, String forgeId) {
        RecipeQueue queue = getQueue(player, forgeId);
        return queue.getCurrentTask();
    }

    public int getTotalQueueSize(Player player) {
        Map<String, RecipeQueue> queues = playerQueues.get(player.getUniqueId());
        if (queues == null) {
            return 0;
        }
        
        int total = 0;
        for (RecipeQueue queue : queues.values()) {
            total += queue.getQueueSize();
        }
        return total;
    }

    public void clearQueue(Player player, String forgeId) {
        RecipeQueue queue = getQueue(player, forgeId);
        queue.cancelAll();
    }

    public void clearAllQueues(Player player) {
        Map<String, RecipeQueue> queues = playerQueues.get(player.getUniqueId());
        if (queues != null) {
            for (RecipeQueue queue : queues.values()) {
                queue.cancelAll();
            }
            queues.clear();
        }
    }

    public void removePlayer(UUID playerId) {
        Map<String, RecipeQueue> queues = playerQueues.remove(playerId);
        if (queues != null) {
            for (RecipeQueue queue : queues.values()) {
                queue.cancelAll();
            }
        }
    }

    public void saveAll() {
    }
}
