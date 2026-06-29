package com.minemart.itemcore.damage;

import org.bukkit.entity.LivingEntity;

import java.util.UUID;

public class HealingRequest {
    private final LivingEntity healer;
    private final LivingEntity target;
    private final double amount;
    private final UUID castId;

    private HealingRequest(Builder builder) {
        this.healer = builder.healer;
        this.target = builder.target;
        this.amount = builder.amount;
        this.castId = builder.castId;
    }

    public LivingEntity getHealer() {
        return healer;
    }

    public LivingEntity getTarget() {
        return target;
    }

    public double getAmount() {
        return amount;
    }

    public UUID getCastId() {
        return castId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LivingEntity healer;
        private LivingEntity target;
        private double amount = 0;
        private UUID castId;

        public Builder healer(LivingEntity healer) {
            this.healer = healer;
            return this;
        }

        public Builder target(LivingEntity target) {
            this.target = target;
            return this;
        }

        public Builder amount(double amount) {
            this.amount = amount;
            return this;
        }

        public Builder castId(UUID castId) {
            this.castId = castId;
            return this;
        }

        public HealingRequest build() {
            return new HealingRequest(this);
        }
    }
}