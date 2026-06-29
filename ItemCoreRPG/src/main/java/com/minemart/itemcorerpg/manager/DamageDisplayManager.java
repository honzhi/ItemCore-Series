package com.minemart.itemcorerpg.manager;

import com.minemart.itemcorerpg.ItemCoreRPG;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

public class DamageDisplayManager {

    private final ItemCoreRPG plugin;

    public DamageDisplayManager(ItemCoreRPG plugin) {
        this.plugin = plugin;
    }

    public void showDamage(LivingEntity target, String damageText) {
        showDamage(target, damageText, 0);
    }

    public void showDamage(LivingEntity target, String damageText, double extraOffset) {
        double offset = plugin.getConfigManager().getVerticalOffset() + extraOffset;
        double height = target.getHeight() + offset;

        ArmorStand armorStand = target.getWorld().spawn(
            target.getLocation().add(0, height, 0),
            ArmorStand.class,
            as -> {
                as.setGravity(false);
                as.setInvulnerable(true);
                as.setVisible(false);
                as.setMarker(true);
                as.setSmall(true);
                as.setCustomNameVisible(true);
                as.setCustomName(damageText);
            }
        );

        animateDamageNumber(armorStand);
    }

    private void animateDamageNumber(ArmorStand armorStand) {
        int duration = plugin.getConfigManager().getDurationTicks();
        double riseHeight = plugin.getConfigManager().getRiseHeight();
        double risePerTick = riseHeight / duration;

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= duration || armorStand.isDead()) {
                    armorStand.remove();
                    cancel();
                    return;
                }

                armorStand.teleport(armorStand.getLocation().add(0, risePerTick, 0));
                ticks++;
            }
        }.runTaskTimer(plugin, 0, 1);
    }
}