package com.minemart.itemcore.damage;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.calculator.AttributeCalculator;
import com.minemart.itemcore.calculator.AttributeCalculator.DamageResult;
import com.minemart.itemcore.config.AttributesConfig;
import com.minemart.itemcore.event.ItemCoreDamageEvent;
import com.minemart.itemcore.item.attribute.AttributeContainer;
import com.minemart.itemcore.item.attribute.CustomAttribute;
import com.minemart.itemcore.item.attribute.DamageTag;
import com.minemart.itemcore.item.attribute.ElementType;
import com.minemart.itemcore.element.AilmentManager;
import com.minemart.itemcore.element.DamageContext;
import com.minemart.itemcore.element.AccumulationManager;
import com.minemart.itemcore.util.FormulaEvaluator;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class DamageManager {
    private static ItemCore plugin;

    public static void init(ItemCore plugin) {
        DamageManager.plugin = plugin;
    }

    public static void processDamage(DamageRequest request) {
        if (request == null || request.getVictim() == null) {
            return;
        }

        LivingEntity attacker = request.getAttacker();
        LivingEntity victim = request.getVictim();

        AttributeContainer attackerAttrs = null;
        if (attacker instanceof Player) {
            attackerAttrs = AttributeCalculator.calculatePlayerAttributes((Player) attacker);
        }

        AttributeContainer defenderAttrs;
        if (victim instanceof Player) {
            defenderAttrs = AttributeCalculator.calculatePlayerAttributes((Player) victim);
        } else {
            defenderAttrs = new AttributeContainer();
        }

        // 应用异常效果中的属性修改 (ATTRIBUTE_MOD)
        applyAilmentAttributeMods(victim, defenderAttrs);

        double damage = request.getBaseDamage();
        AttackType attackType = request.getAttackType();

        // ── 根据 attackType 确定伤害标签和暴击参�?──
        DamageTag effectiveType;
        double critChance;
        double critDamage;
        boolean canCrit = request.isCanCrit();

        if (attackType == AttackType.ATTACK && attackerAttrs != null) {
            // 普攻模式：强�?PHYSICAL + 使用玩家暴击属�?
            effectiveType = DamageTag.PHYSICAL;
            critChance = attackerAttrs.getAttribute(CustomAttribute.CRIT_CHANCE);
            critDamage = AttributeCalculator.getTotalCritDamage(attackerAttrs);

            // 添加 ATTACK_DAMAGE 加成
            damage += attackerAttrs.getAttribute(CustomAttribute.ATTACK_DAMAGE);

            // 添加 PHYSICAL_DAMAGE 百分比加�?
            damage *= (1 + attackerAttrs.getAttribute(CustomAttribute.PHYSICAL_DAMAGE) / 100.0);
        } else {
            // 技能模式：使用传入�?damageType，属性由技�?amount 自行控制
            effectiveType = request.getDamageType();
            critChance = request.getCritChance();
            critDamage = request.getCritDamage();
        }

        Set<DamageTag> tags = new HashSet<>();
        tags.add(effectiveType);

        // ── 暴击判定 ──
        boolean isCrit = false;
        if (canCrit && critChance > 0) {
            double roll = Math.random();
            if (roll < critChance / 100.0) {
                double critMultiplier = critDamage / 100.0;
                damage *= critMultiplier;
                isCrit = true;
            }
        }

        // ── 穿透计�?──
        double requestPen = request.getPenetration();
        double attackerPen = 0;
        double attackerPenPercent = 0;
        if (attackerAttrs != null) {
            if (effectiveType == DamageTag.PHYSICAL) {
                attackerPen = attackerAttrs.getAttribute(CustomAttribute.PHYSICAL_PENETRATION);
                attackerPenPercent = attackerAttrs.getAttribute(CustomAttribute.PHYSICAL_PENETRATION_PERCENT);
            } else if (effectiveType == DamageTag.SPELL) {
                attackerPen = attackerAttrs.getAttribute(CustomAttribute.SPELL_PENETRATION);
                attackerPenPercent = attackerAttrs.getAttribute(CustomAttribute.SPELL_PENETRATION_PERCENT);
            }
        }

        double totalFlatPen = requestPen + attackerPen;
        double totalPercentPen = attackerPenPercent;

        boolean hasElement = request.getElement() != null && request.getElement() != ElementType.NONE;
        // ── 防御计算（物理/法术抗性，仅非元素伤害生效）──
        if (defenderAttrs != null && !hasElement) {
            double effectiveResist;
            if (effectiveType == DamageTag.SPELL) {
                double spellResist = defenderAttrs.getAttribute(CustomAttribute.SPELL_RESIST);
                effectiveResist = calculateEffectiveDefense(spellResist, totalFlatPen, totalPercentPen);
            } else {
                double physicalResist = defenderAttrs.getAttribute(CustomAttribute.PHYSICAL_RESIST);
                effectiveResist = calculateEffectiveDefense(physicalResist, totalFlatPen, totalPercentPen);
            }

            if (effectiveResist > 0) {
                damage = applyDefenseReduction(damage, effectiveResist, effectiveType == DamageTag.SPELL);
            }
        }

        // ── 元素伤害 + 伤害减免（加法叠加，上限翻倍）──
        ElementType element = request.getElement();
        if (defenderAttrs != null) {
            double amplification = 0;
            double reduction = 0;

            if (element != null && element != ElementType.NONE && attackerAttrs != null) {
                double resist = defenderAttrs.getElementResistance(element) / 100.0;
                if (resist < 0) amplification += -resist;
                else if (resist > 0) reduction += resist;
            }

            // 异常效果中的全元素抗性降低 (RESISTANCE_REDUCTION)
            if (victim != null) {
                try {
                    ItemCore icPlugin = ItemCore.getInstance();
                    if (icPlugin != null && icPlugin.getAilmentManager() != null) {
                        double ailmentResistReduction = icPlugin.getAilmentManager().getResistanceReduction(victim.getUniqueId());
                        if (ailmentResistReduction < 0) {
                            amplification += Math.abs(ailmentResistReduction);
                        }
                    }
                } catch (Exception e) {
                    // Silently handle
                }
            }

            double dmgReduction = defenderAttrs.getAttribute(CustomAttribute.DAMAGE_REDUCTION);
            if (dmgReduction < 0) amplification += -dmgReduction / 100.0;
            else if (dmgReduction > 0) reduction += dmgReduction / 100.0;

            // amplification = Math.min(amplification, 0.5); // 无上限加法叠加
            reduction = Math.min(reduction, 1.0);

            if (amplification > 0) damage *= (1 + amplification);
            if (reduction > 0) damage *= (1 - reduction);
        }

        if (damage <= 0) {
            return;
        }

        // ── 吸血 ──
        double lifesteal = request.getLifesteal();
        if (lifesteal > 0 && attacker instanceof Player player && player.isOnline()) {
            double heal = damage * lifesteal;
            double maxHealth = player.getMaxHealth();
            player.setHealth(Math.min(maxHealth, player.getHealth() + heal));
        }

        // ── 事件 ──
        if (attacker instanceof Player attackerPlayer) {
            DamageResult result = new DamageResult(damage, isCrit, critDamage, tags, element);
            ItemCoreDamageEvent damageEvent = new ItemCoreDamageEvent(attackerPlayer, victim, result);
            Bukkit.getPluginManager().callEvent(damageEvent);
            if (damageEvent.isCancelled()) {
                return;
            }
            damage = damageEvent.getTotalDamage();
        }

        // ── 应用伤害 ──
        // victim.damage(damage, attacker) would re-trigger onEntityDamageByEntity
        // Use damage() without attacker: ItemCoreDamageEvent already provides RPG display data
        victim.damage(damage);

        // ── 元素积累 ──
        if (element != null && element != ElementType.NONE && damage > 0) {
            ItemCore plugin = ItemCore.getInstance();
            AccumulationManager accManager = plugin != null ? plugin.getAccumulationManager() : null;
            if (accManager != null) {
                DamageContext.DamageSource source = attackType == AttackType.ATTACK ?
                    DamageContext.DamageSource.ATTACK : DamageContext.DamageSource.SKILL;
                accManager.onElementDamage(victim, DamageContext.builder()
                    .element(element)
                    .damageAmount(damage)
                    .source(source)
                    .attacker(attacker instanceof LivingEntity ? (LivingEntity) attacker : null)
                    .build());
            }
        }

        // ── 暴击击退 ──
        if (isCrit && attackerAttrs != null) {
            applyKnockback(attacker, victim, attackerAttrs);
        }
    }

    private static double calculateEffectiveDefense(double defense, double flatPenetration, double percentPenetration) {
        AttributesConfig.PenetrationOrder order = AttributesConfig.PenetrationOrder.PERCENT_FIRST;
        try {
            if (plugin != null && plugin.getAttributesConfig() != null) {
                AttributesConfig.PenetrationOrder configOrder = plugin.getAttributesConfig().getPenetrationOrder();
                if (configOrder != null) {
                    order = configOrder;
                }
            }
        } catch (Exception e) {
            // use default
        }

        if (order == AttributesConfig.PenetrationOrder.PERCENT_FIRST) {
            double effective = defense * (1 - percentPenetration / 100.0);
            effective = effective - flatPenetration;
            return Math.max(0, effective);
        } else {
            double effective = defense - flatPenetration;
            effective = effective * (1 - percentPenetration / 100.0);
            return Math.max(0, effective);
        }
    }

    private static double applyDefenseReduction(double damage, double defenseValue, boolean isSpell) {
        try {
            if (plugin != null && plugin.getAttributesConfig() != null) {
                AttributesConfig config = plugin.getAttributesConfig();
                String formula = isSpell ? config.getSpellResistFormula() : config.getPhysicalResistFormula();
                if (formula != null && !formula.isEmpty()) {
                    Map<String, Double> variables = new HashMap<>();
                    variables.put("damage", damage);
                    variables.put("armor", defenseValue);
                    return FormulaEvaluator.evaluate(formula, variables);
                }
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to evaluate defense formula", e);
        }

        double reduction = defenseValue / (defenseValue + 100);
        return damage * (1 - reduction);
    }

    public static void applyAilmentAttributeMods(LivingEntity entity, AttributeContainer attrs) {
        if (entity == null || attrs == null) return;
        try {
            ItemCore icPlugin = ItemCore.getInstance();
            if (icPlugin == null || icPlugin.getAilmentManager() == null) return;
            java.util.Map<String, Double> mods = icPlugin.getAilmentManager().getActiveAttributeMods(entity.getUniqueId());
            if (mods.isEmpty()) return;
            for (java.util.Map.Entry<String, Double> entry : mods.entrySet()) {
                for (CustomAttribute attr : CustomAttribute.values()) {
                    if (attr.getConfigKey().equalsIgnoreCase(entry.getKey())) {
                        double current = attrs.getAttribute(attr);
                        attrs.setAttribute(attr, current * (1 + entry.getValue()));
                        if (plugin != null && plugin.getConfigManager() != null && plugin.getConfigManager().isDebugMode()) {
                            plugin.getLogger().info("[AilmentMod] entity=" + entity.getName() + " attr=" + attr.getConfigKey() + " mod=" + entry.getValue() + " result=" + (current + entry.getValue()));
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            // Silently handle
        }
    }
    private static void applyKnockback(LivingEntity attacker, LivingEntity victim, AttributeContainer attrs) {
        double knockback = attrs.getAttribute(CustomAttribute.KNOCKBACK);
        if (knockback > 0) {
            double multiplier = 1.0 + knockback;
            victim.setVelocity(victim.getVelocity().multiply(multiplier));
        }
    }
}