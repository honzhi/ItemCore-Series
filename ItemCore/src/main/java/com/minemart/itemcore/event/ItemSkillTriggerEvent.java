package com.minemart.itemcore.event;

import com.minemart.itemcore.item.CustomItem;
import com.minemart.itemcore.item.skill.ItemSkill;
import com.minemart.itemcore.item.skill.SkillTrigger;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ItemSkillTriggerEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final CustomItem item;
    private final ItemSkill skill;
    private final SkillTrigger trigger;

    private LivingEntity target;
    private Location location;

    private boolean cancelled = false;

    public ItemSkillTriggerEvent(Player player, CustomItem item, ItemSkill skill, SkillTrigger trigger) {
        this(player, item, skill, trigger, null, null);
    }

    public ItemSkillTriggerEvent(Player player, CustomItem item, ItemSkill skill, SkillTrigger trigger,
                                  LivingEntity target, Location location) {
        this.player = player;
        this.item = item;
        this.skill = skill;
        this.trigger = trigger;
        this.target = target;
        this.location = location;
    }

    public Player getPlayer() {
        return player;
    }

    public CustomItem getItem() {
        return item;
    }

    public ItemSkill getSkill() {
        return skill;
    }

    public SkillTrigger getTrigger() {
        return trigger;
    }

    public LivingEntity getTarget() {
        return target;
    }

    public void setTarget(LivingEntity target) {
        this.target = target;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}