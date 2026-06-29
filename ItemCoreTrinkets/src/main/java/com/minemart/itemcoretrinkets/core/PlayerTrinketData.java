
package com.minemart.itemcoretrinkets.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerTrinketData {

    private final UUID playerId;
    private final Map<String, String> equippedTrinkets;

    public PlayerTrinketData(UUID playerId) {
        this.playerId = playerId;
        this.equippedTrinkets = new HashMap<>();
    }

    public PlayerTrinketData(UUID playerId, Map<String, String> equippedTrinkets) {
        this.playerId = playerId;
        this.equippedTrinkets = equippedTrinkets != null ? new HashMap<>(equippedTrinkets) : new HashMap<>();
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public Map<String, String> getEquippedTrinkets() {
        return new HashMap<>(equippedTrinkets);
    }

    public String getEquippedTrinket(String slotId) {
        return equippedTrinkets.get(slotId);
    }

    public void equipTrinket(String slotId, String itemId) {
        if (itemId == null || itemId.isEmpty()) {
            equippedTrinkets.remove(slotId);
        } else {
            equippedTrinkets.put(slotId, itemId);
        }
    }

    public void unequipTrinket(String slotId) {
        equippedTrinkets.remove(slotId);
    }

    public boolean hasTrinketEquipped(String slotId) {
        return equippedTrinkets.containsKey(slotId) && equippedTrinkets.get(slotId) != null && !equippedTrinkets.get(slotId).isEmpty();
    }

    public void clearAll() {
        equippedTrinkets.clear();
    }
}
