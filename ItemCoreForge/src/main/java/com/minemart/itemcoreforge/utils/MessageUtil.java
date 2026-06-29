package com.minemart.itemcoreforge.utils;

import com.minemart.itemcoreforge.ItemCoreForge;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtil {

    private static final LegacyComponentSerializer SERIALIZER = 
        LegacyComponentSerializer.legacyAmpersand();

    public static void send(CommandSender sender, String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        sender.sendMessage(SERIALIZER.deserialize(message));
    }

    public static void sendWithPrefix(CommandSender sender, String message) {
        String prefix = ItemCoreForge.getInstance().getConfigManager().getPrefix();
        send(sender, prefix + message);
    }

    public static void sendMessage(CommandSender sender, String key) {
        String message = ItemCoreForge.getInstance().getConfigManager().getMessage(key);
        if (message != null && !message.isEmpty()) {
            sendWithPrefix(sender, message);
        }
    }

    public static void sendMessage(CommandSender sender, String key, String... replacements) {
        String message = ItemCoreForge.getInstance().getConfigManager().getMessage(key);
        if (message != null && !message.isEmpty()) {
            for (int i = 0; i < replacements.length - 1; i += 2) {
                message = message.replace("{" + replacements[i] + "}", replacements[i + 1]);
            }
            sendWithPrefix(sender, message);
        }
    }

    public static Component toComponent(String message) {
        return SERIALIZER.deserialize(message);
    }

    public static String colorize(String message) {
        return SERIALIZER.serialize(SERIALIZER.deserialize(message));
    }

    public static String formatTime(double seconds) {
        if (seconds < 60) {
            return String.format("%.1f秒", seconds);
        } else {
            int minutes = (int) (seconds / 60);
            double remainingSeconds = seconds % 60;
            return String.format("%d分%.0f秒", minutes, remainingSeconds);
        }
    }
}
