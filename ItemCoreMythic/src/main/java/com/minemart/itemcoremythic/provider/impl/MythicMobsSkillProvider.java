package com.minemart.itemcoremythic.provider.impl;

import com.minemart.itemcoremythic.ItemCoreMythic;
import com.minemart.itemcoremythic.provider.SkillProvider;
import com.minemart.itemcoremythic.util.DebugLogger;
import io.lumine.mythic.bukkit.BukkitAPIHelper;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MythicMobsSkillProvider implements SkillProvider {

    private final ItemCoreMythic plugin;
    private final Logger logger;

    public MythicMobsSkillProvider(ItemCoreMythic plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    @Override
    public String getProviderId() {
        return "mythicmobs";
    }

    @Override
    public boolean executeSkill(Player caster, String skillName, LivingEntity target, Location location) {
        DebugLogger.debug("MythicMobsSkillProvider", "Casting MM skill=" + skillName);
        
        try {
            BukkitAPIHelper apiHelper = MythicBukkit.inst().getAPIHelper();
            
            if (target != null) {
                DebugLogger.debug("MythicMobsSkillProvider", 
                    "castSkill with target: caster=" + caster.getName() + ", skill=" + skillName + 
                    ", target=" + target.getName() + ", loc=" + (location != null ? location : caster.getLocation()));
                
                return apiHelper.castSkill(caster, skillName, target,
                    location != null ? location : caster.getLocation(),
                    Collections.emptyList(), Collections.emptyList(), 1.0f);
            } else {
                DebugLogger.debug("MythicMobsSkillProvider",
                    "castSkill without target: caster=" + caster.getName() + ", skill=" + skillName);
                
                if (location != null) {
                    return apiHelper.castSkill(caster, skillName, location);
                } else {
                    return apiHelper.castSkill(caster, skillName);
                }
            }
            
        } catch (Exception e) {
            DebugLogger.debug("MythicMobsSkillProvider", "Exception while casting skill: " + e.getMessage());
            logger.log(Level.SEVERE, "Failed to cast Mythic skill: " + skillName, e);
            return false;
        }
    }
}
