package com.minemart.itemcoretrinkets.command;

import com.minemart.itemcoretrinkets.ItemCoreTrinkets;
import com.minemart.itemcoretrinkets.gui.TrinketMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TrinketCommand implements CommandExecutor {

    private final ItemCoreTrinkets plugin;

    public TrinketCommand(ItemCoreTrinkets plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("此命令只能由玩家执行");
                return true;
            }
            Player player = (Player) sender;
            if (!player.hasPermission("itemcoretrinkets.gui")) {
                player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                return true;
            }
            TrinketMenu menu = new TrinketMenu(plugin, player);
            menu.open();

            if (plugin.getConfigManager().isDebugMode()) {
                plugin.getLogger().info("[Debug] " + player.getName() + " 执行了 /" + label);
            }
            return true;
        }

        String subCommand = args[0].toLowerCase();

        if ("reload".equals(subCommand)) {
            handleReload(sender);
        } else {
            sender.sendMessage(ChatColor.RED + "未知命令，使用 /ict 打开饰品界面");
        }

        return true;
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("itemcoretrinkets.admin.reload")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return;
        }

        plugin.getConfigManager().reload();
        sender.sendMessage(plugin.getConfigManager().getMessage("config-reloaded"));

        if (plugin.getConfigManager().isDebugMode()) {
            plugin.getLogger().info("[Debug] " + sender.getName() + " 重载了配置");
        }
    }
}