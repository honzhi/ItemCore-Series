package com.minemart.itemcore.listener;

import com.minemart.itemcore.ItemCore;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class BaseListener implements Listener {

    protected final ItemCore plugin;

    public BaseListener(ItemCore plugin) {
        this.plugin = plugin;
    }

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }
}
