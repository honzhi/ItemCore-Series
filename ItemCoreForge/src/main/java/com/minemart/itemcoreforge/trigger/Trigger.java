package com.minemart.itemcoreforge.trigger;

import org.bukkit.entity.Player;

public interface Trigger {

    void execute(Player player);

    String getType();
}
