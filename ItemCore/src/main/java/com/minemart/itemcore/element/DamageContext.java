package com.minemart.itemcore.element;

import com.minemart.itemcore.item.attribute.ElementType;
import org.bukkit.entity.LivingEntity;

public class DamageContext {

    private final ElementType element;
    private final double damageAmount;
    private final DamageSource source;
    private final LivingEntity attacker;

    public DamageContext(ElementType element, double damageAmount, DamageSource source, LivingEntity attacker) {
        this.element = element;
        this.damageAmount = damageAmount;
        this.source = source;
        this.attacker = attacker;
    }

    public ElementType getElement() { return element; }
    public double getDamageAmount() { return damageAmount; }
    public DamageSource getSource() { return source; }
    public LivingEntity getAttacker() { return attacker; }

    public enum DamageSource {
        ATTACK,
        SKILL,
        DOT
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ElementType element;
        private double damageAmount;
        private DamageSource source = DamageSource.SKILL;
        private LivingEntity attacker;

        public Builder element(ElementType element) { this.element = element; return this; }
        public Builder damageAmount(double damageAmount) { this.damageAmount = damageAmount; return this; }
        public Builder source(DamageSource source) { this.source = source; return this; }
        public Builder attacker(LivingEntity attacker) { this.attacker = attacker; return this; }

        public DamageContext build() {
            return new DamageContext(element, damageAmount, source, attacker);
        }
    }
}
