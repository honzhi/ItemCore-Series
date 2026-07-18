package com.minemart.itemcorerpg.manager;

import com.minemart.itemcorerpg.ItemCoreRPG;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DamageDisplayManager {

    private final ItemCoreRPG plugin;
    private final Set<UUID> activeDisplays = new HashSet<>();

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
                as.setPersistent(false);
                as.setCustomNameVisible(true);
                as.setCustomName(damageText);
            }
        );

        activeDisplays.add(armorStand.getUniqueId());
        animateDamageNumber(armorStand);
    }

    private void animateDamageNumber(ArmorStand armorStand) {
        int duration = Math.max(1, plugin.getConfigManager().getDurationTicks());
        double riseHeight = plugin.getConfigManager().getRiseHeight();
        double risePerTick = riseHeight / duration;

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= duration || armorStand.isDead()) {
                    removeDisplay(armorStand);
                    cancel();
                    return;
                }

                armorStand.teleport(armorStand.getLocation().add(0, risePerTick, 0));
                ticks++;
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    private void removeDisplay(ArmorStand armorStand) {
        activeDisplays.remove(armorStand.getUniqueId());
        if (armorStand.isValid()) {
            armorStand.remove();
        }
    }

    public void clearDisplays() {
        for (UUID entityId : new HashSet<>(activeDisplays)) {
            Entity entity = Bukkit.getEntity(entityId);
            if (entity != null) {
                entity.remove();
            }
        }
        activeDisplays.clear();
    }
}
