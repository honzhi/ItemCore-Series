package com.minemart.itemcorelevel.listener;

import com.minemart.itemcorelevel.ItemCoreLevel;
import com.minemart.itemcorelevel.PlayerData;
import com.minemart.itemcorelevel.config.ConfigManager;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import java.util.Map;

public class MythicExpListener implements Listener {

    private final ItemCoreLevel plugin;
    private final Map<String, Integer> mobExpMap;

    public MythicExpListener(ItemCoreLevel plugin) {
        this.plugin = plugin;
        this.mobExpMap = plugin.getConfigManager().getMonsterExpMobs();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onMythicMobDeath(MythicMobDeathEvent event) {
        ConfigManager cfg = plugin.getConfigManager();
        if (!cfg.isMythicMobsExpEnabled()) return;

        Player killer = null;
        if (event.getKiller() instanceof Player) {
            killer = (Player) event.getKiller();
        }

        if (cfg.isKillerOnly()) {
            if (killer == null) return;
            int exp = calculateExp(event);
            if (exp > 0) {
                plugin.getExpManager().addExp(killer, exp, com.minemart.itemcorelevel.api.ExpSource.MOB_KILL);
                if (cfg.isDebug()) {
                    plugin.getLogger().info("MythicMobs击杀经验: " + killer.getName() + " 击杀 " + event.getMobType().getInternalName() + " 获得 " + exp + " 经验");
                }
            }
        } else {
            double radius = 30.0;
            int totalExp = calculateExp(event);
            if (totalExp <= 0) return;

            for (Player online : plugin.getServer().getOnlinePlayers()) {
                if (online.getLocation().distanceSquared(event.getEntity().getLocation()) <= radius * radius) {
                    plugin.getExpManager().addExp(online, totalExp, com.minemart.itemcorelevel.api.ExpSource.MOB_KILL);
                }
            }
        }
    }

    private int calculateExp(MythicMobDeathEvent event) {
        ConfigManager cfg = plugin.getConfigManager();
        String mobId = event.getMobType().getInternalName();

        if (mobExpMap.containsKey(mobId)) {
            return (int) Math.round(mobExpMap.get(mobId) * cfg.getExpMultiplier());
        }

        if (cfg.isUseMobLevel()) {
            try {
                double mobLevel = event.getMob().getLevel();
                String formula = cfg.getMobLevelFormula();
                String expr = formula.replace("{level}", String.valueOf(mobLevel));
                int exp = (int) Math.round(evaluate(expr));
                return (int) Math.round(exp * cfg.getExpMultiplier());
            } catch (Exception ignored) {}
        }

        return (int) Math.round(cfg.getMonsterExpDefault() * cfg.getExpMultiplier());
    }

    private double evaluate(String expr) {
        ExpressionParser parser = new ExpressionParser(expr);
        return parser.parse();
    }

    private static class ExpressionParser {
        private final String expr;
        private int pos = -1;
        private int ch;

        ExpressionParser(String expr) {
            this.expr = expr.replaceAll("\\s+", "");
        }

        private void nextChar() { ch = (++pos < expr.length()) ? expr.charAt(pos) : -1; }
        private boolean eat(int c) { while (ch == ' ') nextChar(); if (ch == c) { nextChar(); return true; } return false; }

        double parse() { nextChar(); return parseExpression(); }

        private double parseExpression() {
            double x = parseTerm();
            for (;;) { if (eat('+')) x += parseTerm(); else if (eat('-')) x -= parseTerm(); else return x; }
        }

        private double parseTerm() {
            double x = parseFactor();
            for (;;) { if (eat('*')) x *= parseFactor(); else if (eat('/')) x /= parseFactor(); else return x; }
        }

        private double parseFactor() {
            if (eat('+')) return parseFactor(); if (eat('-')) return -parseFactor();
            double x; int sp = this.pos;
            if (eat('(')) { x = parseExpression(); eat(')'); }
            else if ((ch >= '0' && ch <= '9') || ch == '.') { while ((ch >= '0' && ch <= '9') || ch == '.') nextChar(); x = Double.parseDouble(expr.substring(sp, this.pos)); }
            else throw new RuntimeException("Unexpected: " + (char) ch);
            if (eat('^')) x = Math.pow(x, parseFactor());
            return x;
        }
    }
}