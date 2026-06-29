package com.minemart.itemcore.element;

import com.minemart.itemcore.item.attribute.ElementType;
import com.minemart.itemcore.element.AccumulationConfig;

import java.util.LinkedHashMap;
import java.util.Map;

public class AccumulationTracker {

    private final Map<ElementType, Double> values = new LinkedHashMap<>();

    public void add(ElementType element, double rawAccumulation, double targetResist) {
        double effective = rawAccumulation * (1 - Math.min(targetResist, 0.75));
        values.merge(element, effective, Double::sum);
    }

    public void addRaw(ElementType element, double amount) {
        values.merge(element, Math.max(0, amount), Double::sum);
    }

    public void decay(ElementType element, double amount) {
        values.computeIfPresent(element, (k, v) -> Math.max(0, v - amount));
    }

    public boolean isAllZero() {
        return values.values().stream().allMatch(v -> v <= 0);
    }

    public void reset(ElementType element) {
        values.put(element, 0.0);
    }

    public double get(ElementType element) {
        return values.getOrDefault(element, 0.0);
    }

    public Map<ElementType, Double> getAllValues() {
        return values;
    }
}
