package com.minemart.itemcorelevel.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.time.Duration;
import java.util.Map;

public final class MessageUtil {

    private MessageUtil() {}

    public static String colorize(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static Component toComponent(String text) {
        if (text == null || text.isEmpty()) return Component.empty();
        return LegacyComponentSerializer.legacySection().deserialize(colorize(text));
    }

    public static String replacePlaceholders(String text, Map<String, String> placeholders) {
        if (text == null || text.isEmpty()) return text;
        String result = text;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue() != null ? entry.getValue() : "");
        }
        return result;
    }

    public static void sendChat(Player player, String message) {
        if (message == null || message.isEmpty()) return;
        player.sendMessage(toComponent(message));
    }

    public static void sendActionBar(Player player, String message) {
        if (message == null || message.isEmpty()) return;
        player.sendActionBar(toComponent(message));
    }

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        Component titleComp = title != null && !title.isEmpty() ? toComponent(title) : Component.empty();
        Component subComp = subtitle != null && !subtitle.isEmpty() ? toComponent(subtitle) : Component.empty();
        Title.Times times = Title.Times.times(
            Duration.ofMillis(fadeIn * 50L),
            Duration.ofMillis(stay * 50L),
            Duration.ofMillis(fadeOut * 50L)
        );
        player.showTitle(Title.title(titleComp, subComp, times));
    }
}