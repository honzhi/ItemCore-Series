package com.minemart.itemcore.item.set;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class ItemSet {

    public enum ActivationMode {
        CUMULATIVE,
        HIGHEST_ONLY;

        public static ActivationMode fromConfig(String value) {
            if (value != null && value.equalsIgnoreCase("highest_only")) {
                return HIGHEST_ONLY;
            }
            return CUMULATIVE;
        }
    }

    private final String id;
    private final String displayName;
    private final ActivationMode activationMode;
    private final NavigableMap<Integer, SetBonus> bonuses;

    public ItemSet(String id, String displayName, ActivationMode activationMode,
                   Map<Integer, SetBonus> bonuses) {
        this.id = id;
        this.displayName = displayName;
        this.activationMode = activationMode != null ? activationMode : ActivationMode.CUMULATIVE;
        this.bonuses = new TreeMap<>();
        if (bonuses != null) {
            this.bonuses.putAll(bonuses);
        }
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ActivationMode getActivationMode() {
        return activationMode;
    }

    public Map<Integer, SetBonus> getBonuses() {
        return Collections.unmodifiableMap(bonuses);
    }

    public List<SetBonus> getActiveBonuses(int equippedPieces) {
        if (equippedPieces <= 0 || bonuses.isEmpty()) {
            return Collections.emptyList();
        }

        if (activationMode == ActivationMode.HIGHEST_ONLY) {
            Map.Entry<Integer, SetBonus> entry = bonuses.floorEntry(equippedPieces);
            return entry != null ? Collections.singletonList(entry.getValue()) : Collections.emptyList();
        }

        return new ArrayList<>(bonuses.headMap(equippedPieces, true).values());
    }
}
