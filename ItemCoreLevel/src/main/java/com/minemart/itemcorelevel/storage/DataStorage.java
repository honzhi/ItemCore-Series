package com.minemart.itemcorelevel.storage;

import com.minemart.itemcorelevel.PlayerData;
import java.util.Map;
import java.util.UUID;

public interface DataStorage {

    PlayerData load(UUID uuid);

    void save(UUID uuid, PlayerData data);

    void saveAll(Map<UUID, PlayerData> dataMap);

    boolean hasData(UUID uuid);

    void shutdown();
}