package com.minemart.itemcoreforge.command;

import com.minemart.itemcoreforge.ItemCoreForge;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class ForgeTabCompleter implements TabCompleter {

    private final ItemCoreForge plugin;

    public ForgeTabCompleter(ItemCoreForge plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        
        if (args.length == 1) {
            suggestions.add("gui");
            suggestions.add("list");
            suggestions.add("reload");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("gui")) {
            for (String forgeId : plugin.getForgeLoader().getForgeIds()) {
                com.minemart.itemcoreforge.core.Forge forge = plugin.getForgeLoader().getForge(forgeId);
                if (forge != null && !forge.isCraftingTableType() && !forge.isFurnaceType()) {
                    suggestions.add(forgeId);
                }
            }
        }
        
        return suggestions;
    }
}