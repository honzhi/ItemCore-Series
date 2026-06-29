package com.minemart.itemcore.item.skill;

public class ItemSkill {
    private final SkillTrigger trigger;
    private final String provider;
    private final String skillName;
    private final int timerDuration;

    public ItemSkill(SkillTrigger trigger, String skillName) {
        this(trigger, "mythicmobs", skillName, 20);
    }

    public ItemSkill(SkillTrigger trigger, String skillName, int timerDuration) {
        this(trigger, "mythicmobs", skillName, timerDuration);
    }

    public ItemSkill(SkillTrigger trigger, String provider, String skillName, int timerDuration) {
        this.trigger = trigger;
        this.provider = provider;
        this.skillName = skillName;
        this.timerDuration = timerDuration;
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

    public boolean hasTimer() {
        return trigger == SkillTrigger.TIMER && timerDuration > 0;
    }
}