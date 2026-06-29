package com.minemart.itemcoreforge.command;

import com.minemart.itemcoreforge.ItemCoreForge;
import com.minemart.itemcoreforge.core.Forge;
import com.minemart.itemcoreforge.gui.RecipeSelectGUI;
import com.minemart.itemcoreforge.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ForgeCommand implements CommandExecutor {

    private final ItemCoreForge plugin;

    public ForgeCommand(ItemCoreForge plugin) {
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
            case "reload" -> {
                if (!sender.hasPermission("itemcoreforge.reload")) {
                    MessageUtil.sendMessage(sender, "no-permission");
                    return true;
                }
                plugin.reload();
                MessageUtil.sendMessage(sender, "reloaded");
            }
            case "list" -> {
                if (!sender.hasPermission("itemcoreforge.list")) {
                    MessageUtil.sendMessage(sender, "no-permission");
                    return true;
                }
                listForges(sender);
            }
            case "gui" -> {
                if (args.length < 2) {
                    sendHelp(sender);
                    return true;
                }
                if (sender instanceof Player player) {
                    openForge(player, args[1]);
                } else {
                    sender.sendMessage("该命令只能由玩家执行！");
                }
            }
            default -> {
                sendHelp(sender);
            }
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6=== ItemCoreForge 帮助 ===");
        sender.sendMessage("§e/icf gui <锻造台ID> §7- 打开指定锻造台");
        sender.sendMessage("§e/icf list §7- 列出所有锻造台");
        sender.sendMessage("§e/icf reload §7- 重载配置");
    }

    private void listForges(CommandSender sender) {
        sender.sendMessage("§6=== 锻造台列表 ===");
        for (String forgeId : plugin.getForgeLoader().getForgeIds()) {
            Forge forge = plugin.getForgeLoader().getForge(forgeId);
            if (forge != null && !forge.isCraftingTableType() && !forge.isFurnaceType()) {
                sender.sendMessage("§7- §e" + forgeId + " §8(配方: " + forge.getRecipes().size() + ")");
            }
        }
    }

    private void openForge(Player player, String forgeId) {
        if (!plugin.getForgeLoader().hasForge(forgeId)) {
            MessageUtil.sendMessage(player, "forge-not-found", "forge", forgeId);
            return;
        }

        if (!player.hasPermission("itemcoreforge.use")) {
            MessageUtil.sendMessage(player, "no-permission");
            return;
        }

        Forge forge = plugin.getForgeLoader().getForge(forgeId);
        if (forge == null) {
            MessageUtil.sendMessage(player, "forge-not-found", "forge", forgeId);
            return;
        }

        if (forge.isCraftingTableType() || forge.isFurnaceType()) {
            MessageUtil.sendMessage(player, "forge-not-found", "forge", forgeId);
            return;
        }

        RecipeSelectGUI gui = new RecipeSelectGUI(plugin, player, forge);
        gui.open();
    }
}