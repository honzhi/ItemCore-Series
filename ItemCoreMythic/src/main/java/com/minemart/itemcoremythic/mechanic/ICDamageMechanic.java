package com.minemart.itemcoremythic.mechanic;
import com.minemart.itemcore.api.ItemCoreAPI;
import com.minemart.itemcore.calculator.AttributeCalculator;
import com.minemart.itemcore.damage.AttackType;
import com.minemart.itemcore.damage.DamageRequest;
import com.minemart.itemcore.item.attribute.AttributeContainer;
import com.minemart.itemcore.item.attribute.CustomAttribute;
import com.minemart.itemcore.item.attribute.DamageTag;
import com.minemart.itemcore.item.attribute.ElementType;
import com.minemart.itemcore.util.AttributePlaceholderResolver;
import com.minemart.itemcoremythic.util.DebugLogger;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
public class ICDamageMechanic implements ITargetedEntitySkill {
    private String rawAmount;
    private final DamageTag damageType;
    private final ElementType element;
    private final boolean canCrit;
    private final double penetration;
    private final AttackType attackType;
    private final double lifesteal;
    public ICDamageMechanic(MythicLineConfig config) {
        this.rawAmount = config.getString(new String[]{"amount", "a"}, "1");
        // MM 参数解析有 Bug，会漏掉 type= 等后续参数，手动从 rawAmount 尾部提取
        String raw = rawAmount;
        String typeStr = "spell";
        String elementStr = "none";
        String atStr = "skill";
        double penVal = 0;
        boolean critVal = true;
        double lsVal = 0;
        int commaIdx = raw.indexOf(',');
        if (commaIdx > 0) {
            this.rawAmount = raw.substring(0, commaIdx).trim();
            // 去除外层引号
            if (this.rawAmount.startsWith("\"") && this.rawAmount.endsWith("\"")) {
                this.rawAmount = this.rawAmount.substring(1, this.rawAmount.length() - 1).trim();
            }
            // 解析额外参数
            String[] extraParams = raw.substring(commaIdx + 1).split(",");
            for (String param : extraParams) {
                param = param.trim();
                int eqIdx = param.indexOf('=');
                if (eqIdx <= 0) continue;
                String key = param.substring(0, eqIdx).trim().toLowerCase();
                String val = param.substring(eqIdx + 1).trim();
                switch (key) {
                    case "type": case "t": typeStr = val; break;
                    case "element": case "e": elementStr = val; break;
                    case "crit": case "c": critVal = Boolean.parseBoolean(val); break;
                    case "penetration": case "p": try { penVal = Double.parseDouble(val); } catch (Exception ignored) {} break;
                    case "attacktype": case "at": atStr = val; break;
                    case "lifesteal": case "ls": try { lsVal = Double.parseDouble(val); } catch (Exception ignored) {} break;
                }
            }
        } else {
            // 没有额外参数，直接清理引号
            if (raw.startsWith("\"") && raw.endsWith("\"")) {
                this.rawAmount = raw.substring(1, raw.length() - 1).trim();
            }
        }
        typeStr = typeStr.toLowerCase();
        elementStr = elementStr.toLowerCase();
        atStr = atStr.toLowerCase();
        this.damageType = parseDamageTag(typeStr);
        this.element = parseElementType(elementStr);
        this.canCrit = critVal;
        this.penetration = penVal;
        this.attackType = "attack".equals(atStr) ? AttackType.ATTACK : AttackType.SKILL;
        this.lifesteal = lsVal;
        DebugLogger.debug("ICDamageMechanic", "Init amount=" + this.rawAmount + " type=" + this.damageType + " element=" + this.element + " at=" + this.attackType);
        DebugLogger.debug("ICDamageMechanic",
            "Initialized | Amount=" + rawAmount
            + ", Type=" + damageType
            + ", Element=" + element
            + ", AttackType=" + attackType
            + ", Penetration=" + penetration
            + ", Lifesteal=" + lifesteal);
    }
    private DamageTag parseDamageTag(String type) {
        return switch (type) {
            case "spell" -> DamageTag.SPELL;
            case "projectile" -> DamageTag.PROJECTILE;
            default -> DamageTag.PHYSICAL;
        };
    }
    private ElementType parseElementType(String element) {
        return switch (element) {
            case "liuhuo", "fire" -> ElementType.LIUHUO;
            case "hanshuang", "ice" -> ElementType.HANSHUANG;
            case "leizhe", "thunder" -> ElementType.LEIZHE;
            default -> ElementType.NONE;
        };
    }
    private static double evaluateMath(String expr) {
        if (expr == null || expr.isBlank()) return 0;
        expr = expr.trim();
        int commaIdx = expr.indexOf(',');
        if (commaIdx > 0) {
            expr = expr.substring(0, commaIdx).trim();
        }
        if (expr.startsWith("\"") && expr.endsWith("\"")) {
            expr = expr.substring(1, expr.length() - 1).trim();
        }
        expr = expr.replaceAll("\\s+", "");
        expr = expr.replace("<&sp>", "");
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < expr.length()) {
            if (expr.charAt(i) == '%') {
                int start = i - 1;
                while (start >= 0 && (Character.isDigit(expr.charAt(start)) || expr.charAt(start) == '.')) {
                    start--;
                }
                start++;
                String numStr = expr.substring(start, i);
                try {
                    double val = Double.parseDouble(numStr) / 100.0;
                    sb.setLength(sb.length() - (i - start));
                    sb.append(val);
                } catch (NumberFormatException e) {
                    sb.append(expr.charAt(i));
                }
            } else {
                sb.append(expr.charAt(i));
            }
            i++;
        }
        expr = sb.toString();
        try {
            return Double.parseDouble(expr);
        } catch (NumberFormatException ignored) {}
        for (String op : new String[]{"*", "/"}) {
            int idx = findOperatorIndex(expr, op);
            if (idx > 0) {
                try {
                    double l = Double.parseDouble(expr.substring(0, idx));
                    double r = Double.parseDouble(expr.substring(idx + 1));
                    return op.equals("*") ? l * r : (r != 0 ? l / r : 0);
                } catch (NumberFormatException ignored) {}
            }
        }
        for (String op : new String[]{"+", "-"}) {
            int idx = findOperatorIndex(expr, op);
            if (idx > 0) {
                try {
                    double l = Double.parseDouble(expr.substring(0, idx));
                    double r = Double.parseDouble(expr.substring(idx + 1));
                    return op.equals("+") ? l + r : l - r;
                } catch (NumberFormatException ignored) {}
            }
        }
        DebugLogger.debug("ICDamageMechanic", "Cannot evaluate expression: " + expr);
        return 0;
    }
    private static int findOperatorIndex(String expr, String op) {
        if (op.equals("-")) {
            for (int i = 1; i < expr.length(); i++) {
                if (expr.charAt(i) == '-' && Character.isDigit(expr.charAt(i - 1))) return i;
            }
            return -1;
        }
        int idx = expr.indexOf(op);
        if (idx <= 0) return -1;
        if (idx + 1 < expr.length() && Character.isDigit(expr.charAt(idx - 1))) return idx;
        return -1;
    }
    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        DebugLogger.debug("ICDamageMechanic", "ICDamage invoked");
        if (!(target.getBukkitEntity() instanceof LivingEntity victim)) {
            DebugLogger.debug("ICDamageMechanic", "Invalid target type");
            return SkillResult.INVALID_TARGET;
        }
        try {
            LivingEntity attacker = null;
            if (data.getCaster().getEntity() != null) {
                org.bukkit.entity.Entity bukkitCaster = data.getCaster().getEntity().getBukkitEntity();
                if (bukkitCaster instanceof LivingEntity le) {
                    attacker = le;
                }
            }
            String resolvedExpr = rawAmount;
            if (attacker instanceof Player icPlayer) {
                resolvedExpr = AttributePlaceholderResolver.resolve(rawAmount, icPlayer);
            }
            double parsedAmount = evaluateMath(resolvedExpr);
            DamageRequest.Builder builder = DamageRequest.builder()
                .attacker(attacker)
                .victim(victim)
                .baseDamage(parsedAmount)
                .damageType(damageType)
                .element(element)
                .canCrit(canCrit)
                .penetration(penetration)
                .attackType(attackType)
                .lifesteal(lifesteal);
            if (attackType == AttackType.ATTACK && attacker instanceof Player player) {
                AttributeContainer attrs = AttributeCalculator.calculatePlayerAttributes(player);
                builder.critChance(attrs.getAttribute(CustomAttribute.CRIT_CHANCE));
                builder.critDamage(AttributeCalculator.getTotalCritDamage(attrs));
            } else {
                builder.critChance(0);
                builder.critDamage(150);
            }
            DamageRequest request = builder.build();
            DebugLogger.debug("ICDamageMechanic",
                "Amount=" + parsedAmount
                + " (raw=" + rawAmount + ", resolved=" + resolvedExpr + ")"
                + ", Type=" + damageType
                + ", Element=" + element
                + ", AttackType=" + attackType
                + ", Target=" + victim.getName());
            ItemCoreAPI.processDamage(request);
            return SkillResult.SUCCESS;
        } catch (Exception e) {
            DebugLogger.debug("ICDamageMechanic", "Exception: " + e.getMessage());
            return SkillResult.ERROR;
        }
    }
}