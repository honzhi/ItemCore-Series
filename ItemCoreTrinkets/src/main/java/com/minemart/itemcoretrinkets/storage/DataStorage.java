package com.minemart.itemcoretrinkets.storage;

import com.minemart.itemcoretrinkets.ItemCoreTrinkets;
import com.minemart.itemcoretrinkets.core.PlayerTrinketData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataStorage {

    private final ItemCoreTrinkets plugin;
    private final File dataFile;
    private FileConfiguration dataConfig;

    public DataStorage(ItemCoreTrinkets plugin) {
        this.plugin = plugin;
        File dataFolder = new File(plugin.getDataFolder(), "data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        this.dataFile = new File(dataFolder, "player_data.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("无法创建数据文件");
                e.printStackTrace();
            }
        }
        loadData();
    }

    public void loadData() {
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public PlayerTrinketData loadPlayerData(UUID playerId) {
        String key = playerId.toString();
        ConfigurationSection playerSection = dataConfig.getConfigurationSection("players." + key);
        
        if (playerSection == null) {
            return new PlayerTrinketData(playerId);
        }

        Map<String, String> equippedTrinkets = new HashMap<>();
        for (String slotId : playerSection.getKeys(false)) {
            String itemId = playerSection.getString(slotId);
            if (itemId != null && !itemId.isEmpty()) {
                equippedTrinkets.put(slotId, itemId);
            }
        }

        if (plugin.getConfigManager().isDebugMode()) {
            plugin.getLogger().info("[Debug] 从文件加载玩家数据: " + playerId + " - " + equippedTrinkets.size() + " 个饰品");
        }

        return new PlayerTrinketData(playerId, equippedTrinkets);
    }

    public void savePlayerData(PlayerTrinketData data) {
        String key = data.getPlayerId().toString();
        String path = "players." + key;

        dataConfig.set(path, null);

        for (Map.Entry<String, String> entry : data.getEquippedTrinkets().entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                dataConfig.set(path + "." + entry.getKey(), entry.getValue());
            }
        }

        saveData();
    }

    public void saveAll() {
        saveData();
    }

    private void saveData() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("无法保存数据文件");
            e.printStackTrace();
        }
    }
}