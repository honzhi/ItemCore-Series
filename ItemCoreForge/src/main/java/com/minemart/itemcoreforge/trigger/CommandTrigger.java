package com.minemart.itemcoreforge.trigger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandTrigger implements Trigger {

    private final String command;
    private final boolean asPlayer;

    public CommandTrigger(String command, boolean asPlayer) {
        this.command = command;
        this.asPlayer = asPlayer;
    }

    public CommandTrigger(String command) {
        this(command, false);
    }

    @Override
    public void execute(Player player) {
        String cmd = command
            .replace("{player}", player.getName())
            .replace("{player_uuid}", player.getUniqueId().toString());

        if (asPlayer) {
            player.performCommand(cmd);
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
    }

    @Override
    public String getType() {
        return "command";
    }

    public String getCommand() {
        return command;
    }

    public boolean isAsPlayer() {
        return asPlayer;
    }
}
