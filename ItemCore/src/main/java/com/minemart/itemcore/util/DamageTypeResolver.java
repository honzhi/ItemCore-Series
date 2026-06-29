package com.minemart.itemcore.util;

import com.minemart.itemcore.item.attribute.DamageTag;
import org.bukkit.Material;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class DamageTypeResolver {

    public static Set<DamageTag> getDamageTags(ItemStack item, Projectile projectile) {
        Set<DamageTag> tags = new HashSet<>();

        if (item != null && isProjectileWeapon(item.getType())) {
            tags.add(DamageTag.PROJECTILE);
        }

        if (projectile != null) {
            tags.add(DamageTag.PROJECTILE);
        }

        if (tags.isEmpty()) {
            tags.add(DamageTag.PHYSICAL);
        }

        return tags;
    }

    public static Set<DamageTag> getDefaultTags() {
        Set<DamageTag> tags = new HashSet<>();
        tags.add(DamageTag.PHYSICAL);
        return tags;
    }

    public static boolean isProjectileWeapon(Material material) {
        if (material == null) {
            return false;
        }
        return material == Material.BOW ||
               material == Material.CROSSBOW ||
               material == Material.TRIDENT;
    }

    public static boolean isProjectile(Projectile projectile) {
        if (projectile == null) {
            return false;
        }
        return true;
    }

    public static boolean isFromProjectileWeapon(Projectile projectile) {
        if (projectile == null) {
            return false;
        }
        ProjectileSource source = projectile.getShooter();
        return source instanceof org.bukkit.entity.LivingEntity;
    }
}
