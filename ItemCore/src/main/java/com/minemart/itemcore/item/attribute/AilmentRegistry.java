package com.minemart.itemcore.item.attribute;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class AilmentRegistry {

    private final Map<String, AilmentType> ailments = new LinkedHashMap<>();

    public void register(AilmentType ailment) {
        ailments.put(ailment.getId().toUpperCase(), ailment);
    }

    public AilmentType get(String id) {
        return ailments.get(id.toUpperCase());
    }

    public Collection<AilmentType> getAll() {
        return ailments.values();
    }
}
