package com.minemart.itemcorerpg.command;

import com.minemart.itemcorerpg.ItemCoreRPG;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final ItemCoreRPG plugin;
    private static final List<String> SUB_COMMANDS = List.of("reload", "help", "info");

    public CommandManager(ItemCoreRPG plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "reload":
                handleReload(sender);
                break;
            case "info":
                handleInfo(sender, args);
                break;
            case "help":
                sendHelp(sender);
                break;
            default:
                sender.sendMessage(ChatColor.RED + "\u672a\u77e5\u547d\u4ee4! \u4f7f\u7528 /icrpg help \u67e5\u770b\u5e2e\u52a9");
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String input = args[0].toLowerCase();
            for (String subCmd : SUB_COMMANDS) {
                if (subCmd.startsWith(input)) {
                    completions.add(subCmd);
                }
            }
        }

        return completions;
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("itemcorerpg.command.reload")) {
            sender.sendMessage(ChatColor.RED + "\u4f60\u6ca1\u6709\u6743\u9650\u6267\u884c\u6b64\u547d\u4ee4");
            return;
        }

        plugin.getConfigManager().load();
        plugin.getHealthCompressionManager().refreshAll();
        sender.sendMessage(ChatColor.GREEN + "[ItemCoreRPG] \u914d\u7f6e\u6587\u4ef6\u5df2\u91cd\u8f7d");
    }

    private void handleInfo(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "\u8be5\u547d\u4ee4\u53ea\u80fd\u7531\u73a9\u5bb6\u6267\u884c");
            return;
        }

        Player viewer = (Player) sender;

        if (args.length >= 2) {
            if (!sender.hasPermission("itemcorerpg.command.info.other")) {
                sender.sendMessage(ChatColor.RED + "\u4f60\u6ca1\u6709\u6743\u9650\u67e5\u770b\u5176\u4ed6\u73a9\u5bb6\u7684\u4fe1\u606f");
                return;
            }
            Player target = plugin.getServer().getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "\u73a9\u5bb6 " + args[1] + " \u4e0d\u5728\u7ebf");
                return;
            }
            com.minemart.itemcorerpg.gui.StatsMenu.open(viewer, target);
        } else {
            if (!sender.hasPermission("itemcorerpg.command.info")) {
                sender.sendMessage(ChatColor.RED + "\u4f60\u6ca1\u6709\u6743\u9650\u6267\u884c\u6b64\u547d\u4ee4");
                return;
            }
            com.minemart.itemcorerpg.gui.StatsMenu.open(viewer, viewer);
        }
    }

    private void sendHelp(CommandSender sender) {
        if (!sender.hasPermission("itemcorerpg.command.help")) {
            sender.sendMessage(ChatColor.RED + "\u4f60\u6ca1\u6709\u6743\u9650\u6267\u884c\u6b64\u547d\u4ee4");
            return;
        }

        sender.sendMessage(ChatColor.BLUE + "===== ItemCoreRPG \u547d\u4ee4\u5e2e\u52a9 =====");
        sender.sendMessage(ChatColor.GRAY + "/icrpg reload - \u91cd\u8f7d\u914d\u7f6e\u6587\u4ef6");
        sender.sendMessage(ChatColor.GRAY + "/icrpg info [\u73a9\u5bb6] - \u67e5\u770b\u5c5e\u6027\u4fe1\u606f");
        sender.sendMessage(ChatColor.GRAY + "/icrpg help - \u663e\u793a\u6b64\u5e2e\u52a9\u4fe1\u606f");
        sender.sendMessage(ChatColor.BLUE + "================================");
    }
}
