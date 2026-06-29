package com.minemart.itemcore.command;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.config.Messages;
import com.minemart.itemcore.util.DurabilityManager;
import com.minemart.itemcore.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RepairCommand extends BaseSubCommand {

    public RepairCommand(ItemCore plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "repair";
    }

    @Override
    public String getDescription() {
        return "修复物品耐久";
    }

    @Override
    public String getUsage() {
        return "/ic repair [player] [hand|all]";
    }

    @Override
    public String getPermission() {
        return "itemcore.command.repair";
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player target;
        boolean repairAll = false;

        if (args.length >= 2) {
            // /ic repair <player> [hand|all]
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(Messages.PLAYER_NOT_FOUND);
                return true;
            }
            if (args.length >= 3 && args[2].equalsIgnoreCase("all")) {
                repairAll = true;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage("控制台必须指定玩家: /ic repair <player> [hand|all]");
            return true;
        }

        if (repairAll) {
            int count = 0;
            for (ItemStack item : target.getInventory().getContents()) {
                if (item != null && DurabilityManager.hasDurability(item)) {
                    DurabilityManager.repairItem(item, DurabilityManager.getMaxDurability(item));
                    count++;
                }
            }
            sender.sendMessage("§a已修复 " + target.getName() + " 的全部 " + count + " 个物品");
        } else {
            ItemStack hand = target.getInventory().getItemInMainHand();
            if (!DurabilityManager.hasDurability(hand)) {
                sender.sendMessage("§c手中物品没有自定义耐久");
                return true;
            }
            DurabilityManager.repairItem(hand, DurabilityManager.getMaxDurability(hand));
            sender.sendMessage("§a已修复 " + target.getName() + " 手中的物品");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 2) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(player.getName());
                }
            }
        } else if (args.length == 3) {
            completions.add("hand");
            completions.add("all");
        }
        return completions;
    }
}
