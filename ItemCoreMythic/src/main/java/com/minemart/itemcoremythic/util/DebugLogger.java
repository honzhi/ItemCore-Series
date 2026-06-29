package com.minemart.itemcoremythic.util;

import com.minemart.itemcoremythic.ItemCoreMythic;
import org.bukkit.Bukkit;

public class DebugLogger {

    public static void debug(String msg) {
        if (!ItemCoreMythic.DEBUG) return;
        Bukkit.getLogger().info("[ItemCoreMythic] " + msg);
    }

    public static void debug(String prefix, String msg) {
        if (!ItemCoreMythic.DEBUG) return;
        Bukkit.getLogger().info("[ItemCoreMythic] [" + prefix + "] " + msg);
    }
}
