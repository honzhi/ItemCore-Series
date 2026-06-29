package com.minemart.itemcoremythic.listener;

import com.minemart.itemcore.event.ItemSkillTriggerEvent;
import com.minemart.itemcoremythic.ItemCoreMythic;
import com.minemart.itemcoremythic.util.DebugLogger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SkillTriggerListener implements Listener {

    private final ItemCoreMythic plugin;

    public SkillTriggerListener(ItemCoreMythic plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSkillTrigger(ItemSkillTriggerEvent event) {
        DebugLogger.debug("SkillTrigger", 
            "Skill Triggered | Player=" + event.getPlayer().getName()
            + " Provider=" + event.getSkill().getProvider()
            + " Skill=" + event.getSkill().getSkillName()
            + " Trigger=" + event.getTrigger()
        );

        if (event.getTarget() != null) {
            DebugLogger.debug("SkillTrigger", 
                "Target=" + event.getTarget().getName()
            );
        }
        
        if (event.getLocation() != null) {
            DebugLogger.debug("SkillTrigger", 
                "Location=" + event.getLocation().getWorld().getName()
                + " (" + event.getLocation().getBlockX() + "," 
                + event.getLocation().getBlockY() + "," 
                + event.getLocation().getBlockZ() + ")"
            );
        }

        plugin.getSkillBridge().executeSkill(
            event.getPlayer(),
            event.getSkill().getProvider(),
            event.getSkill().getSkillName(),
            event.getTarget(),
            event.getLocation()
        );
    }
}
