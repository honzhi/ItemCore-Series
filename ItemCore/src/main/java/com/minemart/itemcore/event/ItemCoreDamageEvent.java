package com.minemart.itemcore.event;

import com.minemart.itemcore.calculator.AttributeCalculator;
import com.minemart.itemcore.item.attribute.DamageTag;
import com.minemart.itemcore.item.attribute.ElementType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Set;

public class ItemCoreDamageEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player attacker;
    private final LivingEntity defender;
    private final AttributeCalculator.DamageResult damageResult;
    private boolean cancelled;

    public ItemCoreDamageEvent(Player attacker, LivingEntity defender, AttributeCalculator.DamageResult damageResult) {
        this.attacker = attacker;
        this.defender = defender;
        this.damageResult = damageResult;
        this.cancelled = false;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Player getAttacker() {
        return attacker;
    }

    public LivingEntity getDefender() {
        return defender;
    }

    public AttributeCalculator.DamageResult getDamageResult() {
        return damageResult;
    }

    public double getTotalDamage() {
        return damageResult.getTotalDamage();
    }

    public double getPhysicalDamage() {
        return damageResult.getPhysicalDamage();
    }

    public boolean isCrit() {
        return damageResult.isCrit();
    }

    public double getCritDamage() {
        return damageResult.getCritDamage();
    }

    public Set<DamageTag> getDamageTags() {
        return damageResult.getDamageTags();
    }

    public boolean hasDamageTag(DamageTag tag) {
        return damageResult.hasDamageTag(tag);
    }

    public boolean isSpellDamage() {
        return damageResult.isSpellDamage();
    }

    public boolean isProjectileDamage() {
        return damageResult.isProjectileDamage();
    }

    public boolean isPhysicalDamage() {
        return damageResult.isPhysicalDamage();
    }

    public ElementType getElement() {
        return damageResult.getElement();
    }
}