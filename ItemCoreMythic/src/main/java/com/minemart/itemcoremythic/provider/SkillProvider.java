package com.minemart.itemcoremythic.provider;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface SkillProvider {

    String getProviderId();

    boolean executeSkill(Player caster, String skillName, LivingEntity target, Location location);
}