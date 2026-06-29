package com.minemart.itemcore.damage;

public class HealManager {

    public static void processHeal(HealingRequest request) {
        if (request == null || request.getTarget() == null) {
            return;
        }

        double amount = request.getAmount();
        if (amount <= 0) {
            return;
        }

        double currentHealth = request.getTarget().getHealth();
        double maxHealth = request.getTarget().getMaxHealth();
        double newHealth = Math.min(maxHealth, currentHealth + amount);
        request.getTarget().setHealth(newHealth);
    }
}