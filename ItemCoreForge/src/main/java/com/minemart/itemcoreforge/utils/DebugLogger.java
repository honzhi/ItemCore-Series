package com.minemart.itemcoreforge.utils;

import com.minemart.itemcoreforge.ItemCoreForge;

public class DebugLogger {

    private DebugLogger() {
    }

    public static void info(String message) {
        if (isEnabled()) {
            ItemCoreForge.getInstance().getLogger().info("[DEBUG] " + message);
        }
    }

    public static void info(String module, String message) {
        if (isEnabled()) {
            ItemCoreForge.getInstance().getLogger().info("[DEBUG][" + module + "] " + message);
        }
    }

    public static void warning(String message) {
        if (isEnabled()) {
            ItemCoreForge.getInstance().getLogger().warning("[DEBUG] " + message);
        }
    }

    public static void warning(String module, String message) {
        if (isEnabled()) {
            ItemCoreForge.getInstance().getLogger().warning("[DEBUG][" + module + "] " + message);
        }
    }

    public static void severe(String message) {
        if (isEnabled()) {
            ItemCoreForge.getInstance().getLogger().severe("[DEBUG] " + message);
        }
    }

    public static void severe(String module, String message) {
        if (isEnabled()) {
            ItemCoreForge.getInstance().getLogger().severe("[DEBUG][" + module + "] " + message);
        }
    }

    public static boolean isEnabled() {
        ItemCoreForge plugin = ItemCoreForge.getInstance();
        return plugin != null && plugin.getConfigManager() != null
            && plugin.getConfigManager().isDebug();
    }
}
