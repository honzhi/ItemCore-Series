package com.minemart.itemcore.item.attribute;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ElementRegistry {

    private final Map<String, ElementType> elements = new LinkedHashMap<>();

    public ElementRegistry() {
        registerDefaults();
    }

    private void registerDefaults() {
        register(ElementType.LIUHUO);
        register(ElementType.HANSHUANG);
        register(ElementType.LEIZHE);
    }

    public void register(ElementType element) {
        elements.put(element.getId().toUpperCase(), element);
    }

    public ElementType get(String id) {
        return elements.get(id.toUpperCase());
    }

    public Collection<ElementType> getAll() {
        return elements.values();
    }
}
