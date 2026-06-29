package com.minemart.itemcore.item.attribute;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AttributeContainer {

    private final Map<CustomAttribute, Double> baseAttributes;
    private final Map<CustomAttribute, double[]> attributeRanges; // {min, max}
    private final Map<ElementType, Double> elementMastery;
    private final Map<ElementType, Double> elementResist;

    public AttributeContainer() {
        this.baseAttributes = new HashMap<>();
        this.attributeRanges = new HashMap<>();
        this.elementMastery = new LinkedHashMap<>();
        this.elementResist = new LinkedHashMap<>();
    }

    public AttributeContainer(AttributeContainer other) {
        this.baseAttributes = new HashMap<>(other.baseAttributes);
        this.attributeRanges = new HashMap<>(other.attributeRanges);
        this.elementMastery = new LinkedHashMap<>(other.elementMastery);
        this.elementResist = new LinkedHashMap<>(other.elementResist);
    }

    public double getAttribute(CustomAttribute attribute) {
        return baseAttributes.getOrDefault(attribute, 0.0);
    }

    public void setAttribute(CustomAttribute attribute, double value) {
        baseAttributes.put(attribute, value);
        attributeRanges.remove(attribute);
    }

    public void setAttributeRange(CustomAttribute attribute, double min, double max) {
        attributeRanges.put(attribute, new double[]{min, max});
    }

    public boolean hasAttributeRange(CustomAttribute attribute) {
        return attributeRanges.containsKey(attribute);
    }

    public double[] getAttributeRange(CustomAttribute attribute) {
        return attributeRanges.get(attribute);
    }

    public void addAttribute(CustomAttribute attribute, double value) {
        double current = getAttribute(attribute);
        baseAttributes.put(attribute, current + value);
    }

    public double getElementMastery(ElementType element) {
        return elementMastery.getOrDefault(element, 0.0);
    }

    public void setElementMastery(ElementType element, double value) {
        elementMastery.put(element, value);
    }

    public double getElementResistance(ElementType element) {
        return elementResist.getOrDefault(element, 0.0);
    }

    public void setElementResistance(ElementType element, double value) {
        elementResist.put(element, value);
    }

    public Map<CustomAttribute, Double> getBaseAttributes() {
        return Collections.unmodifiableMap(baseAttributes);
    }

    public Map<CustomAttribute, double[]> getAttributeRanges() {
        return Collections.unmodifiableMap(attributeRanges);
    }

    public Map<ElementType, Double> getElementMastery() {
        return Collections.unmodifiableMap(elementMastery);
    }

    public Map<ElementType, Double> getElementResistance() {
        return Collections.unmodifiableMap(elementResist);
    }

    public boolean isEmpty() {
        return baseAttributes.isEmpty() &&
               attributeRanges.isEmpty() &&
               elementMastery.isEmpty() &&
               elementResist.isEmpty();
    }

    public void merge(AttributeContainer other) {
        for (Map.Entry<CustomAttribute, Double> entry : other.baseAttributes.entrySet()) {
            addAttribute(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<CustomAttribute, double[]> entry : other.attributeRanges.entrySet()) {
            attributeRanges.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<ElementType, Double> entry : other.elementMastery.entrySet()) {
            double current = getElementMastery(entry.getKey());
            setElementMastery(entry.getKey(), current + entry.getValue());
        }
        for (Map.Entry<ElementType, Double> entry : other.elementResist.entrySet()) {
            double current = getElementResistance(entry.getKey());
            setElementResistance(entry.getKey(), current + entry.getValue());
        }
    }
}