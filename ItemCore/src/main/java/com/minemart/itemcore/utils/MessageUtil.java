package com.minemart.itemcore.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;

public final class MessageUtil {

    private static final LegacyComponentSerializer LEGACY_SERIALIZER = 
        LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .build();

    private MessageUtil() {}

    @SuppressWarnings("deprecation")
    public static String colorizeString(String message) {
        if (message == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static Component colorize(String message) {
        if (message == null) {
            return Component.empty();
        }
        return Component.empty()
            .decoration(TextDecoration.ITALIC, false)
            .append(LEGACY_SERIALIZER.deserialize(message));
    }

    public static Component colorize(String message, String prefix) {
        return colorize(prefix + message);
    }

    public static String format(String message, Object... args) {
        String result = message;
        for (int i = 0; i < args.length; i += 2) {
            if (i + 1 < args.length) {
                result = result.replace("{" + args[i] + "}", String.valueOf(args[i + 1]));
            }
        }
        return result;
    }

    public static Component formatAndColorize(String message, Object... args) {
        return colorize(format(message, args));
    }
}