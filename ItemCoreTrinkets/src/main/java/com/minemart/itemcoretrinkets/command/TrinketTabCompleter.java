package com.minemart.itemcoretrinkets.command;

import com.minemart.itemcoretrinkets.ItemCoreTrinkets;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TrinketTabCompleter implements TabCompleter {

    private final ItemCoreTrinkets plugin;

    public TrinketTabCompleter(ItemCoreTrinkets plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("itemcoretrinkets.admin.reload")) {
                completions.add("reload");
            }
        }

        return completions;
    }
}