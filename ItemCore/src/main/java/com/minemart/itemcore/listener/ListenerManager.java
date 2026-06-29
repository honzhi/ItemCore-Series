package com.minemart.itemcore.listener;

import com.minemart.itemcore.ItemCore;

import java.util.ArrayList;
import java.util.List;

public class ListenerManager {

    private final ItemCore plugin;
    private final List<BaseListener> listeners;

    public ListenerManager(ItemCore plugin) {
        this.plugin = plugin;
        this.listeners = new ArrayList<>();
    }

    public void registerAll() {
        listeners.clear();

        listeners.add(new ItemInteractListener(plugin));
        listeners.add(new ItemDropListener(plugin));
        listeners.add(new CombatListener(plugin));
        listeners.add(new AnvilListener(plugin));
        listeners.add(new EnchantListener(plugin));

        for (BaseListener listener : listeners) {
            listener.register();
        }

        plugin.getLogger().info("已注册 " + listeners.size() + " 个事件监听器");
    }

    public void unregisterAll() {
        for (BaseListener listener : listeners) {
            listener.unregister();
        }
        listeners.clear();
    }

    public int getListenerCount() {
        return listeners.size();
    }
}
