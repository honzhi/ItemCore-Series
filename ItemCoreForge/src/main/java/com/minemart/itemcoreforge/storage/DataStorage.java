package com.minemart.itemcoreforge.storage;

import com.minemart.itemcoreforge.ItemCoreForge;
import com.minemart.itemcoreforge.core.CraftingQueue;
import com.minemart.itemcoreforge.core.Forge;
import com.minemart.itemcoreforge.core.QueueItem;
import com.minemart.itemcoreforge.task.CraftingQueueManager;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class DataStorage {

    private final ItemCoreForge plugin;
    private final Path dataDir;
    private final Yaml yaml;
    private final Map<UUID, ReentrantLock> fileLocks = new ConcurrentHashMap<>();

    public DataStorage(ItemCoreForge plugin) {
        this.plugin = plugin;
        this.dataDir = Paths.get(plugin.getDataFolder().getPath(), "data");
        this.yaml = createYamlInstance();

        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            plugin.getLogger().severe("无法创建数据目录: " + e.getMessage());
        }
    }

    private Yaml createYamlInstance() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        return new Yaml(options);
    }

    public void loadPlayerData(Player player) {
        UUID playerId = player.getUniqueId();
        Path playerFile = dataDir.resolve(playerId.toString() + ".yml");

        if (!Files.exists(playerFile)) {
            return;
        }

        try (Reader reader = new FileReader(playerFile.toFile())) {
            Map<String, Object> playerData = yaml.load(reader);
            if (playerData == null) {
                return;
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> queues = (List<Map<String, Object>>) playerData.get("queues");
            if (queues == null || queues.isEmpty()) {
                return;
            }

            CraftingQueueManager queueManager = plugin.getCraftingQueueManager();

            for (Map<String, Object> queueData : queues) {
                String forgeId = (String) queueData.get("forge_id");
                Forge forge = plugin.getForgeLoader().getForge(forgeId);

                if (forge == null) {
                    plugin.getLogger().warning("加载玩家数据时找不到锻造台: " + forgeId);
                    continue;
                }

                @SuppressWarnings("unchecked")
                List<Map<String, Object>> items = (List<Map<String, Object>>) queueData.get("items");
                if (items == null || items.isEmpty()) {
                    continue;
                }

                CraftingQueue queue = queueManager.getQueue(player, forgeId);
                List<QueueItem> restoredItems = new ArrayList<>();

                for (Map<String, Object> itemData : items) {
                    String recipeId = (String) itemData.get("recipe_id");
                    Forge.Recipe recipe = forge.getRecipe(recipeId);

                    if (recipe == null) {
                        plugin.getLogger().warning("加载玩家数据时找不到配方: " + recipeId);
                        continue;
                    }

                    UUID taskId = UUID.fromString((String) itemData.get("task_id"));
                    long start = ((Number) itemData.get("start")).longValue();
                    long completion = ((Number) itemData.get("completion")).longValue();

                    QueueItem item = new QueueItem(
                        taskId,
                        forgeId,
                        recipeId,
                        ((Number) itemData.get("amount")).intValue(),
                        start,
                        completion
                    );

                    restoredItems.add(item);
                }

                long now = System.currentTimeMillis();
                for (int i = 0; i < restoredItems.size(); i++) {
                    QueueItem item = restoredItems.get(i);

                    if (i > 0) {
                        QueueItem prev = restoredItems.get(i - 1);
                        if (prev.getRemaining() <= 0) {
                            long craftTime = item.getCompletion() - prev.getCompletion();
                            if (craftTime <= 0) {
                                craftTime = item.getCompletion() - item.getStart();
                            }
                            item.setCompletion(now + craftTime);
                        }
                        now = item.getCompletion();
                    }

                    queue.addRestored(item);
                    plugin.getLogger().info("恢复玩家任务: " + player.getName() + " - " + item.getRecipeId() + " (剩余: " + item.getRemainingSeconds() + "秒)");
                }
            }
        } catch (IOException e) {
            plugin.getLogger().severe("加载玩家数据失败 (" + playerId + "): " + e.getMessage());
        }
    }

    public void savePlayerData(Player player) {
        UUID playerId = player.getUniqueId();
        ReentrantLock lock = fileLocks.computeIfAbsent(playerId, k -> new ReentrantLock());

        if (!lock.tryLock()) {
            plugin.getLogger().warning("玩家数据文件正在被写入，跳过保存: " + playerId);
            return;
        }

        try {
            Path playerFile = dataDir.resolve(playerId.toString() + ".yml");

            Map<String, Object> playerData = new LinkedHashMap<>();
            playerData.put("player_id", playerId.toString());
            playerData.put("saved_at", System.currentTimeMillis());

            List<Map<String, Object>> queues = new ArrayList<>();
            CraftingQueueManager queueManager = plugin.getCraftingQueueManager();
            Map<String, CraftingQueue> playerQueues = queueManager.getAllQueues(player);

            boolean hasPendingTasks = false;
            for (Map.Entry<String, CraftingQueue> entry : playerQueues.entrySet()) {
                String forgeId = entry.getKey();
                CraftingQueue queue = entry.getValue();

                List<Map<String, Object>> items = new ArrayList<>();
                for (QueueItem item : queue.getItems()) {
                    if (!item.isCancelled() && !item.isClaimed()) {
                        Map<String, Object> itemData = new LinkedHashMap<>();
                        itemData.put("task_id", item.getTaskId().toString());
                        itemData.put("forge_id", item.getForgeId());
                        itemData.put("recipe_id", item.getRecipeId());
                        itemData.put("amount", item.getAmount());
                        itemData.put("start", item.getStart());
                        itemData.put("completion", item.getCompletion());
                        items.add(itemData);
                        hasPendingTasks = true;
                    }
                }

                if (!items.isEmpty()) {
                    Map<String, Object> queueData = new LinkedHashMap<>();
                    queueData.put("forge_id", forgeId);
                    queueData.put("items", items);
                    queues.add(queueData);
                }
            }

            playerData.put("queues", queues);

            if (hasPendingTasks) {
                try (Writer writer = new FileWriter(playerFile.toFile())) {
                    yaml.dump(playerData, writer);
                }
            } else if (Files.exists(playerFile)) {
                Files.delete(playerFile);
                plugin.getLogger().info("清理已完成任务的数据文件: " + playerFile.getFileName());
            }
        } catch (IOException e) {
            plugin.getLogger().severe("保存玩家数据失败 (" + playerId + "): " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    public void saveAllData() {
        CraftingQueueManager queueManager = plugin.getCraftingQueueManager();

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            savePlayerData(player);
        }
    }

    public void cleanupOldData() {
        try {
            Files.walk(dataDir)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".yml"))
                .filter(path -> {
                    try {
                        return Files.getLastModifiedTime(path).toMillis() < System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000;
                    } catch (IOException e) {
                        return false;
                    }
                })
                .forEach(path -> {
                    try {
                        Files.delete(path);
                        plugin.getLogger().info("清理过期数据文件: " + path.getFileName());
                    } catch (IOException e) {
                        plugin.getLogger().warning("清理数据文件失败: " + path.getFileName());
                    }
                });
        } catch (IOException e) {
            plugin.getLogger().severe("清理旧数据失败: " + e.getMessage());
        }
    }
}