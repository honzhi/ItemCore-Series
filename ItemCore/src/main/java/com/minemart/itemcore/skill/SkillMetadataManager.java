package com.minemart.itemcore.skill;

import com.minemart.itemcore.item.attribute.DamageTag;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SkillMetadataManager {
    private static final Map<UUID, Set<DamageTag>> entityDamageTags = new HashMap<>();
    private static final Map<UUID, Long> damageTagTimestamps = new HashMap<>();
    private static final long TAG_DURATION_MS = 1000;

    public static void setDamageTags(LivingEntity entity, Set<DamageTag> tags) {
        if (entity == null) {
            return;
        }
        UUID uuid = entity.getUniqueId();
        entityDamageTags.put(uuid, new HashSet<>(tags));
        damageTagTimestamps.put(uuid, System.currentTimeMillis());
    }

    public static Set<DamageTag> getAndClearDamageTags(LivingEntity entity) {
        if (entity == null) {
            return new HashSet<>();
        }
        
        UUID uuid = entity.getUniqueId();
        Long timestamp = damageTagTimestamps.get(uuid);
        
        if (timestamp == null || System.currentTimeMillis() - timestamp > TAG_DURATION_MS) {
            clearDamageTags(entity);
            return new HashSet<>();
        }
        
        Set<DamageTag> tags = entityDamageTags.remove(uuid);
        damageTagTimestamps.remove(uuid);
        
        return tags != null ? tags : new HashSet<>();
    }

    public static void clearDamageTags(LivingEntity entity) {
        if (entity == null) {
            return;
        }
        UUID uuid = entity.getUniqueId();
        entityDamageTags.remove(uuid);
        damageTagTimestamps.remove(uuid);
    }

    public static boolean hasDamageTags(LivingEntity entity) {
        if (entity == null) {
            return false;
        }
        
        UUID uuid = entity.getUniqueId();
        Long timestamp = damageTagTimestamps.get(uuid);
        
        if (timestamp == null || System.currentTimeMillis() - timestamp > TAG_DURATION_MS) {
            clearDamageTags(entity);
            return false;
        }
        
        return entityDamageTags.containsKey(uuid);
    }

    public static void cleanup() {
        long currentTime = System.currentTimeMillis();
        entityDamageTags.entrySet().removeIf(entry -> {
            Long timestamp = damageTagTimestamps.get(entry.getKey());
            return timestamp != null && currentTime - timestamp > TAG_DURATION_MS;
        });
        damageTagTimestamps.entrySet().removeIf(entry -> 
            currentTime - entry.getValue() > TAG_DURATION_MS
        );
    }
}
