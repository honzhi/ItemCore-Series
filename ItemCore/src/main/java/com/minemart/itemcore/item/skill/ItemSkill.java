package com.minemart.itemcore.item.skill;

import org.bukkit.Material;

import java.util.Collections;
import java.util.Set;

public class ItemSkill {
    private final SkillTrigger trigger;
    private final String provider;
    private final String skillName;
    private final int timerDuration;
    private final double chance;
    private final Set<Material> blockTypes;

    public ItemSkill(SkillTrigger trigger, String skillName) {
        this(trigger, "mythicmobs", skillName, 20, 100.0, null);
    }

    public ItemSkill(SkillTrigger trigger, String skillName, int timerDuration) {
        this(trigger, "mythicmobs", skillName, timerDuration, 100.0, null);
    }

    public ItemSkill(SkillTrigger trigger, String provider, String skillName, int timerDuration) {
        this(trigger, provider, skillName, timerDuration, 100.0, null);
    }

    public ItemSkill(SkillTrigger trigger, String provider, String skillName, int timerDuration,
                     double chance, Set<Material> blockTypes) {
        this.trigger = trigger;
        this.provider = provider;
        this.skillName = skillName;
        this.timerDuration = timerDuration;
        this.chance = Math.max(0.0, Math.min(100.0, chance));
        this.blockTypes = blockTypes == null ? null : Set.copyOf(blockTypes);
    }

    public SkillTrigger getTrigger() {
        return trigger;
    }

    public String getProvider() {
        return provider;
    }

    public String getSkillName() {
        return skillName;
    }

    public int getTimerDuration() {
        return timerDuration;
    }

    public double getChance() {
        return chance;
    }

    public boolean hasBlockFilter() {
        return blockTypes != null;
    }

    public Set<Material> getBlockTypes() {
        return blockTypes == null ? Collections.emptySet() : blockTypes;
    }

    public boolean matchesBlock(Material material) {
        return blockTypes == null || blockTypes.contains(material);
    }

    public boolean hasTimer() {
        return trigger == SkillTrigger.TIMER && timerDuration > 0;
    }
}
