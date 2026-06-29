package com.minemart.itemcoreforge.trigger;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TriggerManager {

    public static List<Trigger> loadTriggers(ConfigurationSection section) {
        List<Trigger> triggers = new ArrayList<>();
        
        if (section == null) {
            return triggers;
        }

        if (section.isList("messages")) {
            for (String msg : section.getStringList("messages")) {
                triggers.add(new MessageTrigger(msg));
            }
        }

        if (section.isList("sounds")) {
            for (String soundStr : section.getStringList("sounds")) {
                try {
                    String[] parts = soundStr.split(" ");
                    Sound sound = Sound.valueOf(parts[0].toUpperCase());
                    float volume = parts.length > 1 ? Float.parseFloat(parts[1]) : 1.0f;
                    float pitch = parts.length > 2 ? Float.parseFloat(parts[2]) : 1.0f;
                    triggers.add(new SoundTrigger(sound, volume, pitch));
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning("无效的声音配置: " + soundStr);
                }
            }
        }

        if (section.isList("commands")) {
            for (String cmd : section.getStringList("commands")) {
                triggers.add(new CommandTrigger(cmd, false));
            }
        }

        if (section.isList("player-commands")) {
            for (String cmd : section.getStringList("player-commands")) {
                triggers.add(new CommandTrigger(cmd, true));
            }
        }

        return triggers;
    }

    public static void executeTriggers(Player player, List<Trigger> triggers) {
        if (triggers == null || triggers.isEmpty()) {
            return;
        }
        for (Trigger trigger : triggers) {
            try {
                trigger.execute(player);
            } catch (Exception e) {
                Bukkit.getLogger().severe("执行触发器失败: " + trigger.getType() + " - " + e.getMessage());
            }
        }
    }
}
