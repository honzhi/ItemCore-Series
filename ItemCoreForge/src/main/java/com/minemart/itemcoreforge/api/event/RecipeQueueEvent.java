package com.minemart.itemcoreforge.api.event;

import com.minemart.itemcoreforge.core.Forge;
import com.minemart.itemcoreforge.core.RecipeQueue;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RecipeQueueEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    
    private final Player player;
    private final Forge forge;
    private final RecipeQueue queue;

    public RecipeQueueEvent(Player player, Forge forge, RecipeQueue queue) {
        this.player = player;
        this.forge = forge;
        this.queue = queue;
    }

    public Player getPlayer() {
        return player;
    }

    public Forge getForge() {
        return forge;
    }

    public RecipeQueue getQueue() {
        return queue;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
