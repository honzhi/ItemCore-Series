
package com.minemart.itemcoretrinkets.api.event;

import com.minemart.itemcore.item.CustomItem;
import com.minemart.itemcoretrinkets.api.TrinketSlot;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TrinketEquipEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final TrinketSlot slot;
    private final CustomItem item;
    private boolean cancelled;

    public TrinketEquipEvent(Player player, TrinketSlot slot, CustomItem item) {
        this.player = player;
        this.slot = slot;
        this.item = item;
        this.cancelled = false;
    }

    public Player getPlayer() {
        return player;
    }

    public TrinketSlot getSlot() {
        return slot;
    }

    public CustomItem getItem() {
        return item;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
