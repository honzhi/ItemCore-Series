package com.minemart.itemcore.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class ItemObtainedEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;

    private final Player player;
    private final String itemId;
    private final ItemStack itemStack;
    private final ObtainSource source;
    private int amount;

    public ItemObtainedEvent(Player player, String itemId, ItemStack itemStack, int amount, ObtainSource source) {
        this.player = player;
        this.itemId = itemId;
        this.itemStack = itemStack;
        this.amount = amount;
        this.source = source;
    }

    public Player getPlayer() {
        return player;
    }

    public String getItemId() {
        return itemId;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public ObtainSource getSource() {
        return source;
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

    public enum ObtainSource {
        GUI,
        COMMAND,
        API
    }
}
