package com.minemart.itemcorelevel.api.event;

import com.minemart.itemcorelevel.api.ExpSource;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerGainExpEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private final Player player;
    private final ExpSource source;
    private int amount;

    public PlayerGainExpEvent(Player player, int amount, ExpSource source) {
        this.player = player;
        this.amount = amount;
        this.source = source;
    }

    public Player getPlayer() { return player; }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = Math.max(0, amount); }
    public ExpSource getSource() { return source; }
    @Override public boolean isCancelled() { return cancelled; }
    @Override public void setCancelled(boolean cancel) { this.cancelled = cancel; }
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}