package com.minemart.itemcore.event;

import com.minemart.itemcore.item.attribute.ElementType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ElementAccumulationEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final LivingEntity target;
    private final ElementType element;
    private final double oldValue;
    private final double newValue;

    public ElementAccumulationEvent(LivingEntity target, ElementType element, double oldValue, double newValue) {
        this.target = target;
        this.element = element;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public static HandlerList getHandlerList() { return HANDLERS; }

    @Override
    public HandlerList getHandlers() { return HANDLERS; }

    public LivingEntity getTarget() { return target; }
    public ElementType getElement() { return element; }
    public double getOldValue() { return oldValue; }
    public double getNewValue() { return newValue; }
    public double getChange() { return newValue - oldValue; }
}
