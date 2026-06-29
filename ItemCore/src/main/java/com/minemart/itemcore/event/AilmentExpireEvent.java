package com.minemart.itemcore.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AilmentExpireEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final LivingEntity target;
    private final String ailmentId;

    public AilmentExpireEvent(LivingEntity target, String ailmentId) {
        this.target = target;
        this.ailmentId = ailmentId;
    }

    public static HandlerList getHandlerList() { return HANDLERS; }

    @Override
    public HandlerList getHandlers() { return HANDLERS; }

    public LivingEntity getTarget() { return target; }
    public String getAilmentId() { return ailmentId; }
}
