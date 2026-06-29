package com.minemart.itemcoreforge.api.event;

import com.minemart.itemcoreforge.core.CraftTask;
import com.minemart.itemcoreforge.core.Forge;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RecipeCraftedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    
    private final Player player;
    private final Forge.Recipe recipe;
    private final CraftTask task;
    private final boolean success;

    public RecipeCraftedEvent(Player player, Forge.Recipe recipe, CraftTask task, boolean success) {
        this.player = player;
        this.recipe = recipe;
        this.task = task;
        this.success = success;
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

    public boolean isSuccess() {
        return success;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
