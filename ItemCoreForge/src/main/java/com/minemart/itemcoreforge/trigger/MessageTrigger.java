package com.minemart.itemcoreforge.trigger;

import com.minemart.itemcoreforge.utils.MessageUtil;
import org.bukkit.entity.Player;

public class MessageTrigger implements Trigger {

    private final String message;

    public MessageTrigger(String message) {
        this.message = message;
    }

    @Override
    public void execute(Player player) {
        MessageUtil.sendWithPrefix(player, message);
    }

    @Override
    public String getType() {
        return "message";
    }

    public String getMessage() {
        return message;
    }
}
