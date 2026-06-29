package com.minemart.itemcore.element;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.element.AilmentConfig.AilmentData;
import com.minemart.itemcore.event.AilmentExpireEvent;
import com.minemart.itemcore.event.AilmentTriggerEvent;
import com.minemart.itemcore.item.attribute.ElementType;
import com.minemart.itemcore.damage.DamageManager;
import com.minemart.itemcore.damage.DamageRequest;
import com.minemart.itemcore.damage.AttackType;
import com.minemart.itemcore.item.attribute.DamageTag;
import com.minemart.itemcore.element.DamageContext.DamageSource;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.Particle;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AilmentManager {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(AilmentManager.class.getName());

    private final ItemCore plugin;
    private final Map<UUID, List<ActiveAilment>> activeAilments = new ConcurrentHashMap<>();

    public AilmentManager(ItemCore plugin) {
        this.plugin = plugin;
    }

    public void startTickScheduler() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Map.Entry<UUID, List<ActiveAilment>> entry : activeAilments.entrySet()) {
                List<ActiveAilment> ailments = entry.getValue();
                LivingEntity target = getEntity(entry.getKey());
                if (target == null) {
                    activeAilments.remove(entry.getKey());
                    continue;
                }

                for (ActiveAilment ailment : new ArrayList<>(ailments)) {
                    ailment.tick();

                    AilmentData data = plugin.getAilmentConfig().getAilmentData(ailment.getAilmentId());
                    if (data != null) {
                        applyTriggers(target, ailment, data);
                        spawnAilmentParticles(target, ailment.getAilmentId());
                    }

                    if (ailment.isExpired()) {
                        ailments.remove(ailment);
                        onAilmentExpire(target, ailment, data);
                    }
                }

                if (ailments.isEmpty()) {
                    activeAilments.remove(entry.getKey());
                }
            }
        }, 1L, 1L);
    }

    public void triggerAilment(LivingEntity target, String ailmentId, Player source) {
        if (target == null || ailmentId == null) return;

        AilmentData data = plugin.getAilmentConfig().getAilmentData(ailmentId);
        if (data == null) return;

        List<ActiveAilment> ailments = activeAilments.computeIfAbsent(
            target.getUniqueId(), k -> new ArrayList<>());

        ActiveAilment existing = null;
        for (ActiveAilment a : ailments) {
            if (a.getAilmentId().equalsIgnoreCase(ailmentId)) {
                existing = a;
                break;
            }
        }

        if (existing != null) {
            String policy = data.getRefreshPolicy();
            switch (policy.toUpperCase()) {
                case "RESET":
                    existing.resetDuration(data.getDuration());
                    return;
                case "IGNORE":
                    return;
                case "STACK":
                    int max = data.getMaxStacks();
                    int count = (int) ailments.stream()
                        .filter(a -> a.getAilmentId().equalsIgnoreCase(ailmentId)).count();
                    if (count >= max) return;
                    break;
                case "REPLACE":
                    ailments.remove(existing);
                    break;
            }
        }

        UUID sourceId = source != null ? source.getUniqueId() : null;
        ActiveAilment newAilment = new ActiveAilment(ailmentId, target.getUniqueId(),
            sourceId, data.getDuration());
        ailments.add(newAilment);

        Bukkit.getPluginManager().callEvent(
            new AilmentTriggerEvent(target, ailmentId, resolveElement(ailmentId, data), source));
    }

    public boolean hasAilment(LivingEntity target, String ailmentId) {
        List<ActiveAilment> ailments = activeAilments.get(target.getUniqueId());
        if (ailments == null) return false;
        return ailments.stream().anyMatch(a -> a.getAilmentId().equalsIgnoreCase(ailmentId));
    }

    public void remove(LivingEntity entity) {
        activeAilments.remove(entity.getUniqueId());
    }

    private void applyTriggers(LivingEntity target, ActiveAilment ailment, AilmentData data) {
        for (AilmentTrigger trigger : data.getTriggers()) {
            switch (trigger.getType().toUpperCase()) {
                case "DAMAGE_PERCENT":
                    if (ailment.getRemainingTicks() % (trigger.getInterval() != null ? trigger.getInterval() : 20) == 0) {
                        double percent = trigger.getValue() != null ? trigger.getValue() : 0.02;
                        double damage = target.getHealth() * percent;
                        if (damage > 0) {
                            ElementType ailmentElement = resolveElement(ailment.getAilmentId(), data);
                            if (ailmentElement != null && ailmentElement != ElementType.NONE) {
                                LivingEntity source = ailment.getSourceId() != null ? Bukkit.getEntity(ailment.getSourceId()) instanceof LivingEntity ? (LivingEntity) Bukkit.getEntity(ailment.getSourceId()) : null : null;
                                DamageRequest req = DamageRequest.builder()
                                    .attacker(source)
                                    .victim(target)
                                    .baseDamage(damage)
                                    .damageType(DamageTag.SPELL)
                                    .element(ailmentElement)
                                    .canCrit(false)
                                    .attackType(AttackType.SKILL)
                                    .build();
                                LOGGER.info("[DOT_DEBUG] Ailment=" + ailment.getAilmentId() + " damage=" + damage + " element=" + ailmentElement.getId() + " target=" + target.getName());
                                DamageManager.processDamage(req);
                            } else {
                                target.damage(damage);
                            }
                        }
                    }
                    break;

                case "DAMAGE_FIXED":
                    if (ailment.getRemainingTicks() % (trigger.getInterval() != null ? trigger.getInterval() : 20) == 0) {
                        double fixedDamage = trigger.getValue() != null ? trigger.getValue() : 1;
                        target.damage(fixedDamage);
                    }
                    break;

                case "ATTRIBUTE_MOD":
                    break;

                case "RESISTANCE_REDUCTION":
                    break;

                case "POTION_EFFECT":
                    break;
            }
        }
    }

    private void onAilmentExpire(LivingEntity target, ActiveAilment ailment, AilmentData data) {
        Bukkit.getPluginManager().callEvent(
            new AilmentExpireEvent(target, ailment.getAilmentId()));
    }

    private ElementType resolveElement(String ailmentId, AilmentData data) {
        for (ElementConfig.ElementData elementData : plugin.getElementConfig().getAllElementData().values()) {
            if (elementData.getAilmentId().equalsIgnoreCase(ailmentId)) {
                return ItemCore.getElementRegistry().get(elementData.getId());
            }
        }
        return null;
    }

    private LivingEntity getEntity(UUID uuid) {
        return Bukkit.getEntity(uuid) instanceof LivingEntity ? (LivingEntity) Bukkit.getEntity(uuid) : null;
    }


    public java.util.Map<String, Double> getActiveAttributeMods(java.util.UUID entityId) {
        java.util.Map<String, Double> mods = new java.util.HashMap<>();
        java.util.List<ActiveAilment> ailments = activeAilments.get(entityId);
        if (ailments == null) return mods;

        for (ActiveAilment ailment : ailments) {
            AilmentData data = plugin.getAilmentConfig().getAilmentData(ailment.getAilmentId());
            if (data == null) continue;
            for (AilmentTrigger trigger : data.getTriggers()) {
                if (!"ATTRIBUTE_MOD".equalsIgnoreCase(trigger.getType())) continue;
                if (trigger.getAttribute() == null || trigger.getValue() == null) continue;
                String attrKey = trigger.getAttribute().toUpperCase();
                mods.merge(attrKey, trigger.getValue(), Double::sum);
            }
        }
        return mods;
    }

    public double getResistanceReduction(java.util.UUID entityId) {
        double reduction = 0.0;
        java.util.List<ActiveAilment> ailments = activeAilments.get(entityId);
        if (ailments == null) return reduction;

        for (ActiveAilment ailment : ailments) {
            AilmentData data = plugin.getAilmentConfig().getAilmentData(ailment.getAilmentId());
            if (data == null) continue;
            for (AilmentTrigger trigger : data.getTriggers()) {
                if ("RESISTANCE_REDUCTION".equalsIgnoreCase(trigger.getType()) && trigger.getValue() != null) {
                    reduction += trigger.getValue();
                }
            }
        }
        return reduction;
    }

    private void spawnAilmentParticles(LivingEntity target, String ailmentId) {
        if (target == null || target.isDead()) return;
        org.bukkit.Location loc = target.getLocation().add(0, 1.0, 0);
        switch (ailmentId.toUpperCase()) {
            case "LIUHUO_DOT":
                target.getWorld().spawnParticle(Particle.FLAME, loc, 20, 0.5, 0.5, 0.5, 0.01);
                break;
            case "HANSHUANG_WEAKEN":
                target.getWorld().spawnParticle(Particle.SNOWFLAKE, loc, 20, 0.5, 0.5, 0.5, 0.01);
                break;
            case "LEIZHE_BREAK":
                Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(138, 43, 226), 1.0f);
                target.getWorld().spawnParticle(Particle.DUST, loc, 20, 0.5, 0.5, 0.5, 0, dust);
                break;
        }
    }
}