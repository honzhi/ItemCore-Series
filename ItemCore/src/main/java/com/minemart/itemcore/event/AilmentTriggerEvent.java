package com.minemart.itemcore.event;

import com.minemart.itemcore.item.attribute.ElementType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AilmentTriggerEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final LivingEntity target;
    private final String ailmentId;
    private final ElementType sourceElement;
    private final Player sourcePlayer;
    private boolean cancelled;

    public AilmentTriggerEvent(LivingEntity target, String ailmentId,
                                ElementType sourceElement, Player sourcePlayer) {
        this.target = target;
        this.ailmentId = ailmentId;
        this.sourceElement = sourceElement;
        this.sourcePlayer = sourcePlayer;
        this.cancelled = false;
    }

    public static HandlerList getHandlerList() { return HANDLERS; }

    @Override
    public HandlerList getHandlers() { return HANDLERS; }

    @Override
    public boolean isCancelled() { return cancelled; }

    @Override
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    public LivingEntity getTarget() { return target; }
    public String getAilmentId() { return ailmentId; }
    public ElementType getSourceElement() { return sourceElement; }
    public Player getSourcePlayer() { return sourcePlayer; }
}
