package com.minemart.itemcore.command;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.utils.MessageUtil;
import com.minemart.itemcore.utils.PermissionUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public abstract class BaseSubCommand implements SubCommand {

    protected final ItemCore plugin;

    protected BaseSubCommand(ItemCore plugin) {
        this.plugin = plugin;
    }

    protected void sendMessage(CommandSender sender, String message) {
        if (message != null && !message.isEmpty()) {
            sender.sendMessage(MessageUtil.colorize(message));
        }
    }

    protected void sendPrefixedMessage(CommandSender sender, String message) {
        if (plugin.getMessagesManager() != null) {
            String prefix = plugin.getMessagesManager().getPrefix();
            sendMessage(sender, prefix + message);
        } else {
            sendMessage(sender, "&7[&bItemCore&7] " + message);
        }
    }

    protected boolean checkPermission(CommandSender sender) {
        if (getPermission() == null || getPermission().isEmpty()) {
            return true;
        }
        if (PermissionUtil.hasPermission(sender, getPermission())) {
            return true;
        }
        if (plugin.getMessagesManager() != null) {
            sendPrefixedMessage(sender, plugin.getMessagesManager().getNoPermission());
        } else {
            sendPrefixedMessage(sender, "&c你没有权限执行此命令");
        }
        return false;
    }

    protected boolean checkPlayerOnly(CommandSender sender) {
        if (!isPlayerOnly()) {
            return true;
        }
        if (sender instanceof Player) {
            return true;
        }
        sendPrefixedMessage(sender, "&c此命令只能由玩家执行");
        return false;
    }

    protected boolean validate(CommandSender sender) {
        return checkPermission(sender) && checkPlayerOnly(sender);
    }

    protected int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    protected boolean isValidAmount(int amount) {
        return amount > 0 && amount <= 64;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
