package com.minemart.itemcoremythic.mechanic;

import com.minemart.itemcore.api.ItemCoreAPI;
import com.minemart.itemcore.damage.HealingRequest;
import com.minemart.itemcoremythic.util.DebugLogger;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import org.bukkit.entity.LivingEntity;

public class ICHealMechanic implements ITargetedEntitySkill {

    private final PlaceholderString amountExpr;

    public ICHealMechanic(MythicLineConfig config) {
        String rawAmount = config.getString(new String[]{"amount", "a"}, "1");
        rawAmount = stripQuotes(rawAmount);
        this.amountExpr = PlaceholderString.of(rawAmount);
        DebugLogger.debug("ICHealMechanic", "Initialized | Amount=" + amountExpr);
    }

    private static String stripQuotes(String s) {
        if (s == null || s.isEmpty()) return s;
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"")) {
            return s.substring(1, s.length() - 1).trim();
        }
        return s;
    }

    private static double evaluateMath(String expr) {
        if (expr == null || expr.isBlank()) return 0;
        expr = expr.trim().replace(" ", "");
        
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
        
        DebugLogger.debug("ICHealMechanic", "Cannot evaluate expression: " + expr);
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
        DebugLogger.debug("ICHealMechanic", "ICHeal invoked");
        
        if (!(target.getBukkitEntity() instanceof LivingEntity victim)) {
            DebugLogger.debug("ICHealMechanic", "Invalid target type");
            return SkillResult.INVALID_TARGET;
        }

        try {
            String resolvedExpr = amountExpr.get(data.getCaster());
            double parsedAmount = evaluateMath(resolvedExpr);

            LivingEntity healer = null;
            if (data.getCaster().getEntity() != null) {
                org.bukkit.entity.Entity bukkitCaster = data.getCaster().getEntity().getBukkitEntity();
                if (bukkitCaster instanceof LivingEntity le) {
                    healer = le;
                }
            }

            DebugLogger.debug("ICHealMechanic", "Amount=" + parsedAmount + " (expr=" + amountExpr + ", resolved=" + resolvedExpr + ")");
            
            if (parsedAmount <= 0) {
                DebugLogger.debug("ICHealMechanic", "Amount <= 0, skipping");
                return SkillResult.CONDITION_FAILED;
            }

            DebugLogger.debug("ICHealMechanic", "Target=" + victim.getName());

            HealingRequest request = HealingRequest.builder()
                .healer(healer)
                .target(victim)
                .amount(parsedAmount)
                .build();

            DebugLogger.debug("ICHealMechanic", "Sending HealingRequest");
            ItemCoreAPI.processHeal(request);
            
            return SkillResult.SUCCESS;
        } catch (Exception e) {
            DebugLogger.debug("ICHealMechanic", "Exception: " + e.getMessage());
            return SkillResult.ERROR;
        }
    }

}