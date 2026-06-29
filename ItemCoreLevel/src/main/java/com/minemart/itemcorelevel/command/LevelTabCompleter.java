package com.minemart.itemcorelevel.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LevelTabCompleter implements TabCompleter {

    private static final List<String> SUB_COMMANDS = List.of(
        "help", "info", "add", "remove", "set", "setexp", "reset", "reload"
    );

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return filter(SUB_COMMANDS, args[0]);
        }

        if (args.length == 2) {
            List<String> playerNames = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerNames.add(player.getName());
            }
            return filter(playerNames, args[1]);
        }

        if (args.length == 3) {
            switch (args[0].toLowerCase()) {
                case "add":
                case "remove":
                    return List.of("<数量>");
                case "set":
                    return List.of("<等级>");
                case "setexp":
                    return List.of("<经验值>");
            }
        }

        return List.of();
    }

    private List<String> filter(List<String> list, String prefix) {
        if (prefix == null || prefix.isEmpty()) return list;
        List<String> result = new ArrayList<>();
        for (String s : list) {
            if (s.toLowerCase().startsWith(prefix.toLowerCase())) {
                result.add(s);
            }
        }
        return result;
    }
}