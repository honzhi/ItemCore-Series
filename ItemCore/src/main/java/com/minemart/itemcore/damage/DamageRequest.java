package com.minemart.itemcore.damage;

import com.minemart.itemcore.item.attribute.DamageTag;
import com.minemart.itemcore.item.attribute.ElementType;
import org.bukkit.entity.LivingEntity;

import java.util.UUID;

public class DamageRequest {
    private final LivingEntity attacker;
    private final LivingEntity victim;
    private final double baseDamage;
    private final DamageTag damageType;
    private final ElementType element;
    private final boolean canCrit;
    private final double critChance;
    private final double critDamage;
    private final double penetration;
    private final UUID castId;
    private final AttackType attackType;
    private final double lifesteal;

    private DamageRequest(Builder builder) {
        this.attacker = builder.attacker;
        this.victim = builder.victim;
        this.baseDamage = builder.baseDamage;
        this.damageType = builder.damageType;
        this.element = builder.element;
        this.canCrit = builder.canCrit;
        this.critChance = builder.critChance;
        this.critDamage = builder.critDamage;
        this.penetration = builder.penetration;
        this.castId = builder.castId;
        this.attackType = builder.attackType;
        this.lifesteal = builder.lifesteal;
    }

    public LivingEntity getAttacker() { return attacker; }
    public LivingEntity getVictim() { return victim; }
    public double getBaseDamage() { return baseDamage; }
    public DamageTag getDamageType() { return damageType; }
    public ElementType getElement() { return element; }
    public boolean isCanCrit() { return canCrit; }
    public double getCritChance() { return critChance; }
    public double getCritDamage() { return critDamage; }
    public double getPenetration() { return penetration; }
    public UUID getCastId() { return castId; }
    public AttackType getAttackType() { return attackType; }
    public double getLifesteal() { return lifesteal; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LivingEntity attacker;
        private LivingEntity victim;
        private double baseDamage = 0;
        private DamageTag damageType = DamageTag.PHYSICAL;
        private ElementType element = ElementType.NONE;
        private boolean canCrit = true;
        private double critChance = 0;
        private double critDamage = 150;
        private double penetration = 0;
        private UUID castId;
        private AttackType attackType = AttackType.SKILL;
        private double lifesteal = 0;

        public Builder attacker(LivingEntity attacker) { this.attacker = attacker; return this; }
        public Builder victim(LivingEntity victim) { this.victim = victim; return this; }
        public Builder baseDamage(double baseDamage) { this.baseDamage = baseDamage; return this; }
        public Builder damageType(DamageTag damageType) { this.damageType = damageType; return this; }
        public Builder element(ElementType element) { this.element = element; return this; }
        public Builder canCrit(boolean canCrit) { this.canCrit = canCrit; return this; }
        public Builder critChance(double critChance) { this.critChance = critChance; return this; }
        public Builder critDamage(double critDamage) { this.critDamage = critDamage; return this; }
        public Builder penetration(double penetration) { this.penetration = penetration; return this; }
        public Builder castId(UUID castId) { this.castId = castId; return this; }
        public Builder attackType(AttackType attackType) { this.attackType = attackType; return this; }
        public Builder lifesteal(double lifesteal) { this.lifesteal = lifesteal; return this; }

        public DamageRequest build() {
            return new DamageRequest(this);
        }
    }
}