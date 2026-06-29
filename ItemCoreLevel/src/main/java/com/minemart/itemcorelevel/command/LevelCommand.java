package com.minemart.itemcorelevel.command;

import com.minemart.itemcore.api.ItemCoreAPI;
import com.minemart.itemcore.item.attribute.CustomAttribute;
import com.minemart.itemcorelevel.ItemCoreLevel;
import com.minemart.itemcorelevel.PlayerData;
import com.minemart.itemcorelevel.api.ExpSource;
import com.minemart.itemcorelevel.config.ConfigManager;
import com.minemart.itemcorelevel.manager.LevelManager;
import com.minemart.itemcorelevel.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.*;

public class LevelCommand implements CommandExecutor {

    private final ItemCoreLevel plugin;
    private final ConfigManager configManager;
    private final LevelManager levelManager;

    private static final String PREFIX = "&8[&bItemCoreLevel&8] &7";

    public LevelCommand(ItemCoreLevel plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.levelManager = plugin.getLevelManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            return handleHelp(sender);
        }
        switch (args[0].toLowerCase()) {
            case "info": return handleInfo(sender, args);
            case "add": return handleAdd(sender, args);
            case "remove": return handleRemove(sender, args);
            case "set": return handleSet(sender, args);
            case "setexp": return handleSetExp(sender, args);
            case "reset": return handleReset(sender, args);
            case "reload": return handleReload(sender);
            default:
                sender.sendMessage(MessageUtil.toComponent(PREFIX + "未知指令，使用 &b/icl help &7查看帮助"));
                return true;
        }
    }

    private boolean handleHelp(CommandSender sender) {
        List<String> msgs = new ArrayList<>();
        msgs.add("&8========= &bItemCoreLevel &7帮助 &8=========");
        msgs.add("&b/icl help &7- 显示此帮助");
        msgs.add("&b/icl info [玩家] &7- 查看等级信息");
        msgs.add("&b/icl add <玩家> <数量> &7- 增加经验");
        msgs.add("&b/icl remove <玩家> <数量> &7- 扣除经验");
        msgs.add("&b/icl set <玩家> <等级> &7- 设置等级");
        msgs.add("&b/icl setexp <玩家> <数量> &7- 设置经验值");
        msgs.add("&b/icl reset <玩家> &7- 重置玩家数据");
        msgs.add("&b/icl reload &7- 重载配置");
        msgs.add("&8==========================================");
        for (String m : msgs) sender.sendMessage(MessageUtil.toComponent(m));
        return true;
    }

    private boolean handleInfo(CommandSender sender, String[] args) {
        Player target;
        if (args.length >= 2) {
            if (!sender.hasPermission("itemcorelevel.command.info.other")) {
                sender.sendMessage(MessageUtil.toComponent(PREFIX + "&c你没有权限查看其他玩家的信息"));
                return true;
            }
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(MessageUtil.toComponent(PREFIX + "&c玩家不在线: " + args[1]));
                return true;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(MessageUtil.toComponent(PREFIX + "&c请指定要查看的玩家"));
            return true;
        }

        PlayerData data = levelManager.getOrCreatePlayerData(target.getUniqueId());
        int level = data.getLevel();
        int exp = data.getExp();
        int required = levelManager.getRequiredExp(level);
        double progress = required > 0 ? Math.min(1.0, (double) exp / required) : 0;
        String progressStr = String.format("%.1f%%", progress * 100);

        List<String> msgs = new ArrayList<>();
        msgs.add("&8========= &b等级信息 &8=========");
        msgs.add("&f玩家: &b" + target.getName());
        msgs.add("&f等级: &e" + level + "&7/&e" + (configManager.getMaxLevel() > 0 ? String.valueOf(configManager.getMaxLevel()) : "\u221e"));
        msgs.add("&f经验: &e" + exp + " &7/ &e" + required + " &7(&a" + progressStr + "&7)");

        Map<CustomAttribute, Double> generalRewards = configManager.getGeneralRewards();
        if (!generalRewards.isEmpty()) {
            msgs.add("");
            msgs.add("&7每级属性加成:");
            for (Map.Entry<CustomAttribute, Double> entry : generalRewards.entrySet()) {
                msgs.add(" &f" + entry.getKey().getDisplayName() + ": &a+" + String.format("%.1f", entry.getValue() * level));
            }
        }
        msgs.add("&8================================");
        for (String m : msgs) sender.sendMessage(MessageUtil.toComponent(m));
        return true;
    }

    private boolean handleAdd(CommandSender sender, String[] args) {
        if (!sender.hasPermission("itemcorelevel.command.add")) {
            sender.sendMessage(MessageUtil.toComponent(PREFIX + "&c你没有权限使用此指令"));
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(MessageUtil.toComponent(PREFIX + "&c用法: /icl add <玩家> <数量>"));
            return true;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(MessageUtil.toComponent(PREFIX + "&c玩家不在线: " + args[1]));
            return true;
        }
        try {
            int amount = Integer.parseInt(args[2]);
            if (amount <= 0) throw new NumberFormatException();
            plugin.getExpManager().addExp(target, amount, ExpSource.COMMAND);
            sender.sendMessage(MessageUtil.toComponent(PREFIX + "&a已给 &f" + target.getName() + " &a增加 &e" + amount + " &a经验值"));
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageUtil.toComponent(PREFIX + "&c数量必须是大于 0 的整数"));
        }
        return true;
    }

    private boolean handleRemove(CommandSender sender, String[] args) {
        if (!sender.hasPermission("itemcorelevel.command.remove")) {
            sender.sendMessage(MessageUtil.toComponent(PREFIX + "&c你没有权限使用此指令"));
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(MessageUtil.toComponent(PREFIX + "&c用法: /icl remove <玩家> <数量>"));
            return true;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(MessageUtil.toComponent(PREFIX + "&c玩家不在线: " + args[1]));
            return true;
        }
        try {
            int amount = Integer.parseInt(args[2]);
            if (amount <= 0) throw new NumberFormatException();
            plugin.getExpManager().removeExp(target, amount);
            sender.sendMessage(MessageUtil.toComponent(PREFIX + "&a已扣除 &f" + target.getName() + " &a的 &e" + amount + " &a经验值"));
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageUtil.toComponent(PREFIX + "&c数量必须是大于 0 的整数"));
        }
        return true;
    }

    private boolean handleSet(CommandSender sender, String[] args) {
        if (!sender.hasPermission("itemcorelevel.command.set")) {
            sender.sendMessage(MessageUtil.toComponent(PREFIX + "&c你没有权限使用此指令"));
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(MessageUtil.toComponent(PREFIX + "&c用法: /icl set <玩家> <等级>"));
            return true;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(MessageUtil.toComponent(PREFIX + "&c玩家不在线: " + args[1]));
            return true;
        }
        try {
            int level = Integer.parseInt(args[2]);
            if (level < 1) throw new NumberFormatException();
            levelManager.setLevel(target, level);
            sender.sendMessage(MessageUtil.toComponent(PREFIX + "&a已设置 &f" + target.getName() + " &a的等级为 &e" + level));
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageUtil.toComponent(PREFIX + "&c等级必须是大于 0 的整数"));
        }
        return true;
    }

    private boolean handleSetExp(CommandSender sender, String[] args) {
        if (!sender.hasPermission("itemcorelevel.command.setexp")) {
            sender.sendMessage(MessageUtil.toComponent(PREFIX + "&c你没有权限使用此指令"));
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(MessageUtil.toComponent(PREFIX + "&c用法: /icl setexp <玩家> <数量>"));
            return true;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(MessageUtil.toComponent(PREFIX + "&c玩家不在线: " + args[1]));
            return true;
        }
        try {
            int amount = Integer.parseInt(args[2]);
            if (amount < 0) throw new NumberFormatException();
            levelManager.setExp(target.getUniqueId(), amount);
            sender.sendMessage(MessageUtil.toComponent(PREFIX + "&a已设置 &f" + target.getName() + " &a的经验值为 &e" + amount));
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageUtil.toComponent(PREFIX + "&c数量必须是不小于 0 的整数"));
        }
        return true;
    }

    private boolean handleReset(CommandSender sender, String[] args) {
        if (!sender.hasPermission("itemcorelevel.command.reset")) {
            sender.sendMessage(MessageUtil.toComponent(PREFIX + "&c你没有权限使用此指令"));
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(MessageUtil.toComponent(PREFIX + "&c用法: /icl reset <玩家>"));
            return true;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(MessageUtil.toComponent(PREFIX + "&c玩家不在线: " + args[1]));
            return true;
        }
        levelManager.resetPlayer(target);
        sender.sendMessage(MessageUtil.toComponent(PREFIX + "&a已重置 &f" + target.getName() + " &a的等级和经验"));
        return true;
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("itemcorelevel.command.reload")) {
            sender.sendMessage(MessageUtil.toComponent(PREFIX + "&c你没有权限使用此指令"));
            return true;
        }
        configManager.reload();
        for (Player online : Bukkit.getOnlinePlayers()) {
            ItemCoreAPI.refreshPlayerAttributes(online);
        }
        sender.sendMessage(MessageUtil.toComponent(PREFIX + "&a配置已重载"));
        return true;
    }
}