package com.minemart.itemcoremythic.listener;

import com.minemart.itemcoremythic.ItemCoreMythic;
import com.minemart.itemcoremythic.placeholder.ICPlaceholderExpansion;
import com.minemart.itemcoremythic.mechanic.ICDamageMechanic;
import com.minemart.itemcoremythic.mechanic.ICHealMechanic;
import com.minemart.itemcoremythic.util.DebugLogger;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.bukkit.events.MythicReloadedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MMLoadListener implements Listener {

    private final ItemCoreMythic plugin;

    public MMLoadListener(ItemCoreMythic plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMythicMechanicLoad(MythicMechanicLoadEvent event) {
        String mechanicName = event.getMechanicName().toUpperCase();
        DebugLogger.debug("MMLoadListener", "MM mechanic load request: " + mechanicName);

        switch (mechanicName) {
            case "ICDAMAGE":
                event.register(new ICDamageMechanic(event.getConfig()));
                plugin.getLogger().info("Mechanic registered: ICDAMAGE");
                DebugLogger.debug("MMLoadListener", "Registered ICDamageMechanic");
                break;
            case "ICHEAL":
                event.register(new ICHealMechanic(event.getConfig()));
                plugin.getLogger().info("Mechanic registered: ICHEAL");
                DebugLogger.debug("MMLoadListener", "Registered ICHealMechanic");
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void onMythicReload(MythicReloadedEvent event) {
        DebugLogger.debug("MMLoadListener", "MM reloaded, re-registering placeholders");
        new ICPlaceholderExpansion(plugin).register();
    }
}
