package com.minemart.itemcore.command;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.gui.CategoryMenu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GuiCommand extends BaseSubCommand {

    public GuiCommand(ItemCore plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "open";
    }

    @Override
    public String getUsage() {
        return "open";
    }

    @Override
    public String getDescription() {
        return "打开物品库 GUI";
    }

    @Override
    public String getPermission() {
        return "itemcore.command.gui";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public boolean execute(org.bukkit.command.CommandSender sender, String[] args) {
        if (!validate(sender)) {
            return true;
        }

        Player player = (Player) sender;
        new CategoryMenu(plugin, player).open();
        return true;
    }

    @Override
    public List<String> onTabComplete(org.bukkit.command.CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}