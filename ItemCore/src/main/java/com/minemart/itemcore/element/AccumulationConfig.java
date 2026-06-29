package com.minemart.itemcore.element;

import java.util.Collections;
import java.util.List;

public class AccumulationConfig {

    private final AccumulationMode mode;
    private final double value;
    private final String attribute;
    private final double multiplier;
    private final List<String> allowSources;

    public AccumulationConfig(AccumulationMode mode, double value, String attribute,
                               double multiplier, List<String> allowSources) {
        this.mode = mode;
        this.value = value;
        this.attribute = attribute;
        this.multiplier = multiplier;
        this.allowSources = allowSources != null ? allowSources : Collections.emptyList();
    }

    public AccumulationMode getMode() { return mode; }
    public double getValue() { return value; }
    public String getAttribute() { return attribute; }
    public double getMultiplier() { return multiplier; }
    public List<String> getAllowSources() { return allowSources; }

    public enum AccumulationMode {
        DAMAGE_PERCENT,
        FIXED,
        ATTRIBUTE
    }
}
