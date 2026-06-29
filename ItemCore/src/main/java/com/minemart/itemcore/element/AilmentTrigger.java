package com.minemart.itemcore.element;

public class AilmentTrigger {

    private final String type;
    private final Double value;
    private final Integer interval;
    private final String attribute;

    public AilmentTrigger(String type, Double value, Integer interval, String attribute) {
        this.type = type;
        this.value = value;
        this.interval = interval;
        this.attribute = attribute;
    }

    public String getType() { return type; }
    public Double getValue() { return value; }
    public Integer getInterval() { return interval; }
    public String getAttribute() { return attribute; }
}
