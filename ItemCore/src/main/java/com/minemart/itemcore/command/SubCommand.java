package com.minemart.itemcore.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand {

    String getName();

    String getUsage();

    String getDescription();

    String getPermission();

    boolean isPlayerOnly();

    boolean execute(CommandSender sender, String[] args);

    List<String> onTabComplete(CommandSender sender, String[] args);
}
