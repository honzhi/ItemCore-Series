package com.minemart.itemcore.command;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.config.Messages;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ReloadCommand extends BaseSubCommand {

    public ReloadCommand(ItemCore plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getUsage() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "重载插件配置";
    }

    @Override
    public String getPermission() {
        return "itemcore.command.reload";
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

        sendPrefixedMessage(sender, Messages.RELOAD_START);

        try {
            plugin.reload();
            sendPrefixedMessage(sender, Messages.RELOAD_SUCCESS);
        } catch (Exception e) {
            plugin.getLogger().severe("重载失败: " + e.getMessage());
            e.printStackTrace();
            sendPrefixedMessage(sender, Messages.RELOAD_FAILED);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
