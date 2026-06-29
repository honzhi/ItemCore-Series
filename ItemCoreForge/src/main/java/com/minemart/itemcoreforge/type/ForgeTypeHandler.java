package com.minemart.itemcoreforge.type;

import com.minemart.itemcoreforge.ItemCoreForge;
import com.minemart.itemcoreforge.core.Forge;

public interface ForgeTypeHandler {

    String getType();

    void onEnable(ItemCoreForge plugin);

    void onDisable();

    void onReload();

    void registerForge(Forge forge);

    default void unregisterForge(Forge forge) {
    }
}
