package com.minemart.itemcore.command;

import com.minemart.itemcore.ItemCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final ItemCore plugin;
    private final Map<String, SubCommand> commands;

    public CommandManager(ItemCore plugin) {
        this.plugin = plugin;
        this.commands = new HashMap<>();
        registerCommands();
    }

    private void registerCommands() {
        register(new HelpCommand(plugin));
        GuiCommand gui = new GuiCommand(plugin);
        register(gui);
        register(gui, "gui");
        register(new GiveCommand(plugin));
        register(new ReloadCommand(plugin));
    }

    private void register(SubCommand command) {
        commands.put(command.getName().toLowerCase(), command);
    }

    private void register(SubCommand command, String alias) {
        commands.put(alias.toLowerCase(), command);
    }

    public SubCommand getCommand(String name) {
        if (name == null) {
            return null;
        }
        return commands.get(name.toLowerCase());
    }

    public List<SubCommand> getCommands() {
        return new ArrayList<>(commands.values());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            if (plugin.getCommandManager() != null) {
                SubCommand help = getCommand("help");
                if (help != null) {
                    return help.execute(sender, new String[] {"help"});
                }
            }
            sender.sendMessage(" ");
            sender.sendMessage("§bItemCore §fv" + plugin.getDescription().getVersion());
            sender.sendMessage("§7输入 §f/ic help §7查看帮助");
            sender.sendMessage(" ");
            return true;
        }

        SubCommand subCmd = getCommand(args[0]);
        if (subCmd == null) {
            sender.sendMessage("§c未知命令. 使用 §f/ic help §c查看帮助");
            return true;
        }

        return subCmd.execute(sender, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            for (SubCommand subCmd : commands.values()) {
                if (subCmd.getPermission() == null || hasPermission(sender, subCmd.getPermission())) {
                    completions.add(subCmd.getName());
                }
            }
            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        } else if (args.length > 1) {
            SubCommand subCmd = getCommand(args[0]);
            if (subCmd != null) {
                List<String> results = subCmd.onTabComplete(sender, args);
                if (results != null && !results.isEmpty()) {
                    return StringUtil.copyPartialMatches(args[args.length - 1], results, new ArrayList<>());
                }
            }
        }

        return Collections.emptyList();
    }

    private boolean hasPermission(CommandSender sender, String permission) {
        return com.minemart.itemcore.utils.PermissionUtil.hasPermission(sender, permission);
    }
}