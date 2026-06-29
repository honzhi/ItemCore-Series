package com.minemart.itemcore.command;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.config.Messages;
import com.minemart.itemcore.utils.MessageUtil;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand extends BaseSubCommand {

    public HelpCommand(ItemCore plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getUsage() {
        return "help [page]";
    }

    @Override
    public String getDescription() {
        return "显示帮助信息";
    }

    @Override
    public String getPermission() {
        return "itemcore.command.help";
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

        int page = 1;
        if (args.length > 1) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored) {
            }
        }

        CommandManager cmdManager = plugin.getCommandManager();
        List<SubCommand> commands = new ArrayList<>();
        for (SubCommand cmd : cmdManager.getCommands()) {
            if (cmd.getPermission() == null || hasPermission(sender, cmd.getPermission())) {
                commands.add(cmd);
            }
        }

        int itemsPerPage = 5;
        int totalPages = Math.max(1, (int) Math.ceil((double) commands.size() / itemsPerPage));
        page = Math.max(1, Math.min(page, totalPages));

        String header = Messages.HELP_HEADER
            .replace("{page}", String.valueOf(page))
            .replace("{total}", String.valueOf(totalPages));
        sendMessage(sender, header);
        sendMessage(sender, "");

        int start = (page - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, commands.size());

        for (int i = start; i < end; i++) {
            SubCommand cmd = commands.get(i);
            String helpLine = Messages.HELP_ITEM
                .replace("{command}", "itemcore")
                .replace("{usage}", cmd.getUsage())
                .replace("{desc}", cmd.getDescription());
            sendMessage(sender, helpLine);
        }

        return true;
    }

    private boolean hasPermission(CommandSender sender, String permission) {
        return com.minemart.itemcore.utils.PermissionUtil.hasPermission(sender, permission);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            List<String> pages = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                pages.add(String.valueOf(i));
            }
            return pages;
        }
        return new ArrayList<>();
    }
}
