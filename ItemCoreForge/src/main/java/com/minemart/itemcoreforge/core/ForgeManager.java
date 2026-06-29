package com.minemart.itemcoreforge.core;

import com.minemart.itemcoreforge.ItemCoreForge;

public class ForgeManager {

    private final ItemCoreForge plugin;

    public ForgeManager(ItemCoreForge plugin) {
        this.plugin = plugin;
    }

    public Forge getForge(String forgeId) {
        return plugin.getForgeLoader().getForge(forgeId);
    }

    public boolean hasForge(String forgeId) {
        return plugin.getForgeLoader().hasForge(forgeId);
    }
}
