package com.minemart.itemcore.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class PermissionUtil {

    private PermissionUtil() {}

    public static boolean hasPermission(CommandSender sender, String permission) {
        if (sender == null) {
            return false;
        }
        if (sender.hasPermission("itemcore.admin")) {
            return true;
        }
        return sender.hasPermission(permission);
    }

    public static boolean hasPermission(Player player, String permission) {
        if (player == null) {
            return false;
        }
        if (player.hasPermission("itemcore.admin")) {
            return true;
        }
        return player.hasPermission(permission);
    }

    public static boolean hasPermissionOrOp(Player player, String permission) {
        if (player == null) {
            return false;
        }
        if (player.isOp()) {
            return true;
        }
        return hasPermission(player, permission);
    }
}
