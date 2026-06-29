package com.minemart.itemcore.command;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.api.event.ItemObtainedEvent;
import com.minemart.itemcore.config.MessagesManager;
import com.minemart.itemcore.core.CoreManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GiveCommand extends BaseSubCommand {

    public GiveCommand(ItemCore plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getUsage() {
        return "give <player> <item> [amount]";
    }

    @Override
    public String getDescription() {
        return "给予玩家物品";
    }

    @Override
    public String getPermission() {
        return "itemcore.command.give";
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!validate(sender)) {
            return true;
        }

        if (args.length < 3) {
            sendPrefixedMessage(sender, "&e用法: &7/itemcore give <player> <item> [amount]");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sendPrefixedMessage(sender, plugin.getMessagesManager().getPlayerNotFound(args[1]));
            return true;
        }

        String itemId = args[2].toLowerCase();

        int amount = 1;
        if (args.length > 3) {
            amount = parseInt(args[3]);
            if (!isValidAmount(amount)) {
                sendPrefixedMessage(sender, plugin.getMessagesManager().getInvalidAmount(args[3]));
                return true;
            }
        }

        CoreManager coreManager = plugin.getCoreManager();
        if (coreManager == null) {
            sendPrefixedMessage(sender, "&c插件未正确初始化");
            return true;
        }

        if (!coreManager.getItemManager().hasItem(itemId)) {
            sendPrefixedMessage(sender, plugin.getMessagesManager().getItemNotFound(args[2]));
            return true;
        }

        boolean success = coreManager.giveItem(target, itemId, amount, ItemObtainedEvent.ObtainSource.COMMAND);
        if (success) {
            sendPrefixedMessage(sender, plugin.getMessagesManager().getItemGiven(target.getName(), itemId, amount));
            sendPrefixedMessage(target, plugin.getMessagesManager().getItemObtained(itemId));
        } else {
            sendPrefixedMessage(sender, "&c物品给予失败");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 2) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        } else if (args.length == 3) {
            completions.addAll(plugin.getCoreManager().getItemManager().getItemIds());
        } else if (args.length == 4) {
            for (int i = 1; i <= 64; i++) {
                completions.add(String.valueOf(i));
            }
        }

        return completions;
    }
}
