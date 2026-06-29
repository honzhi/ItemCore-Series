package com.minemart.itemcore.element;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.element.AccumulationConfig.AccumulationMode;
import com.minemart.itemcore.element.DamageContext.DamageSource;
import com.minemart.itemcore.element.ElementConfig.ElementData;
import com.minemart.itemcore.event.ElementAccumulationEvent;
import com.minemart.itemcore.item.attribute.AttributeContainer;
import com.minemart.itemcore.item.attribute.CustomAttribute;
import com.minemart.itemcore.item.attribute.ElementType;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AccumulationManager {

    private final ItemCore plugin;
    private final Map<UUID, AccumulationTracker> trackers = new ConcurrentHashMap<>();

    public AccumulationManager(ItemCore plugin) {
        this.plugin = plugin;
    }

    public void startDecayScheduler() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Map.Entry<UUID, AccumulationTracker> entry : trackers.entrySet()) {
                AccumulationTracker tracker = entry.getValue();
                ElementConfig elementConfig = plugin.getElementConfig();
                if (elementConfig == null) continue;

                for (ElementData elementData : elementConfig.getAllElementData().values()) {
                    ElementType elementType = resolveElement(elementData.getId());
                    if (elementType != null) {
                        tracker.decay(elementType, elementData.getDecayPerSecond());
                    }
                }

                if (tracker.isAllZero()) {
                    trackers.remove(entry.getKey());
                }
            }
        }, 20L, 20L);
    }

    public void onElementDamage(LivingEntity target, DamageContext ctx) {
        if (target == null || ctx == null || ctx.getElement() == null) return;

        AccumulationTracker tracker = trackers.computeIfAbsent(target.getUniqueId(),
            k -> new AccumulationTracker());

        ElementData elementData = plugin.getElementConfig().getElementData(ctx.getElement().getId());
        if (elementData == null) return;

        AccumulationConfig accConfig = elementData.getAccumulation();
        if (!isSourceAllowed(accConfig, ctx.getSource())) return;

        double targetResist = 0;
        if (target instanceof Player) {
            AttributeContainer attrs = com.minemart.itemcore.calculator.AttributeCalculator
                .calculatePlayerAttributes((Player) target);
            targetResist = attrs.getElementResistance(ctx.getElement());
        }

        double rawAccumulation;
        switch (accConfig.getMode()) {
            case DAMAGE_PERCENT:
                rawAccumulation = ctx.getDamageAmount() * accConfig.getValue();
                break;
            case FIXED:
                rawAccumulation = accConfig.getValue();
                break;
            case ATTRIBUTE:
                double attrValue = 0;
                LivingEntity attacker = ctx.getAttacker();
                if (attacker instanceof Player) {
                    AttributeContainer attrs = com.minemart.itemcore.calculator.AttributeCalculator
                        .calculatePlayerAttributes((Player) attacker);
                    String attr = accConfig.getAttribute() != null ? accConfig.getAttribute().toUpperCase() : "";
                    switch (attr) {
                        case "SPELL_POWER": attrValue = attrs.getAttribute(CustomAttribute.SPELL_POWER); break;
                        case "ATTACK_DAMAGE": attrValue = attrs.getAttribute(CustomAttribute.ATTACK_DAMAGE); break;
                        default: attrValue = 0;
                    }
                }
                rawAccumulation = attrValue * accConfig.getMultiplier();
                break;
            default:
                rawAccumulation = ctx.getDamageAmount();
        }

        double oldValue = tracker.get(ctx.getElement());
        tracker.add(ctx.getElement(), rawAccumulation, targetResist);
        double newValue = tracker.get(ctx.getElement());

        Bukkit.getPluginManager().callEvent(
            new ElementAccumulationEvent(target, ctx.getElement(), oldValue, newValue));

        if (newValue >= elementData.getThreshold()) {
            AilmentManager ailmentManager = getAilmentManager();
            if (ailmentManager != null) {
                Player sourcePlayer = ctx.getAttacker() instanceof Player ? (Player) ctx.getAttacker() : null;
                ailmentManager.triggerAilment(target, elementData.getAilmentId(), sourcePlayer);
            }
            tracker.reset(ctx.getElement());
        }
    }

    public AccumulationSnapshot getProgress(LivingEntity target, ElementType element) {
        AccumulationTracker tracker = trackers.get(target.getUniqueId());
        if (tracker == null) return new AccumulationSnapshot(element, 0, 0);

        ElementData elementData = plugin.getElementConfig().getElementData(element.getId());
        double threshold = elementData != null ? elementData.getThreshold() : 100;
        return new AccumulationSnapshot(element, tracker.get(element), threshold);
    }

    public void addProgress(LivingEntity target, ElementType element, double amount) {
        AccumulationTracker tracker = trackers.computeIfAbsent(target.getUniqueId(),
            k -> new AccumulationTracker());
        tracker.addRaw(element, amount);
    }

    public void clearProgress(LivingEntity target, ElementType element) {
        AccumulationTracker tracker = trackers.get(target.getUniqueId());
        if (tracker != null) {
            tracker.reset(element);
            if (tracker.isAllZero()) {
                trackers.remove(target.getUniqueId());
            }
        }
    }

    public void remove(LivingEntity entity) {
        trackers.remove(entity.getUniqueId());
    }

    public AccumulationTracker getTracker(LivingEntity target) {
        return trackers.get(target.getUniqueId());
    }

    private boolean isSourceAllowed(AccumulationConfig config, DamageSource source) {
        if (config.getAllowSources().isEmpty()) return true;
        return config.getAllowSources().stream()
            .anyMatch(s -> s.equalsIgnoreCase(source.name()));
    }

    private ElementType resolveElement(String id) {
        return ItemCore.getElementRegistry().get(id);
    }

    private AilmentManager getAilmentManager() {
        try {
            Object ailmentManager = plugin.getClass().getDeclaredMethod("getAilmentManager").invoke(plugin);
            return (AilmentManager) ailmentManager;
        } catch (Exception e) {
            return null;
        }
    }

    public static class AccumulationSnapshot {
        private final ElementType element;
        private final double current;
        private final double threshold;

        public AccumulationSnapshot(ElementType element, double current, double threshold) {
            this.element = element;
            this.current = current;
            this.threshold = threshold;
        }

        public ElementType getElement() { return element; }
        public double getValue() { return current; }
        public double getThreshold() { return threshold; }
        public double getProgressPercent() { return threshold > 0 ? current / threshold : 0; }
    }
}
