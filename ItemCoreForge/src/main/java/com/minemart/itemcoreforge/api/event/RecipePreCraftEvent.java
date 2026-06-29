package com.minemart.itemcoreforge.api.event;

import com.minemart.itemcoreforge.core.CraftTask;
import com.minemart.itemcoreforge.core.Forge;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RecipePreCraftEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    
    private final Player player;
    private final Forge.Recipe recipe;
    private final CraftTask task;
    private boolean cancelled = false;

    public RecipePreCraftEvent(Player player, Forge.Recipe recipe, CraftTask task) {
        this.player = player;
        this.recipe = recipe;
        this.task = task;
    }

    public Player getPlayer() {
        return player;
    }

    public Forge.Recipe getRecipe() {
        return recipe;
    }

    public CraftTask getTask() {
        return task;
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
