package com.minemart.itemcorelevel.manager;

import com.minemart.itemcorelevel.ItemCoreLevel;
import com.minemart.itemcorelevel.PlayerData;
import com.minemart.itemcore.api.ItemCoreAPI;
import com.minemart.itemcorelevel.api.event.PlayerLevelUpEvent;
import com.minemart.itemcorelevel.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LevelManager {

    private final ItemCoreLevel plugin;
    private final ConfigManager configManager;
    private final Map<UUID, PlayerData> playerDataMap = new ConcurrentHashMap<>();

    public LevelManager(ItemCoreLevel plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    public PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.get(uuid);
    }

    public PlayerData getOrCreatePlayerData(UUID uuid) {
        return playerDataMap.computeIfAbsent(uuid, k -> {
            PlayerData data = plugin.getDataStorage().load(uuid);
            if (data == null) {
                data = new PlayerData(uuid);
                data.setLevel(configManager.getStartLevel());
                data.setExp(configManager.getStartExp());
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) data.setPlayerName(player.getName());
            }
            return data;
        });
    }

    public void cachePlayerData(UUID uuid, PlayerData data) {
        if (data != null) playerDataMap.put(uuid, data);
    }

    public void removePlayerData(UUID uuid) {
        playerDataMap.remove(uuid);
    }

    public Map<UUID, PlayerData> getAllPlayerData() {
        return Collections.unmodifiableMap(playerDataMap);
    }

    public int getLevel(UUID uuid) {
        PlayerData data = playerDataMap.get(uuid);
        return data != null ? data.getLevel() : configManager.getStartLevel();
    }

    public int getExp(UUID uuid) {
        PlayerData data = playerDataMap.get(uuid);
        return data != null ? data.getExp() : configManager.getStartExp();
    }

    public int getRequiredExp(int level) {
        int maxLevel = configManager.getMaxLevel();
        if (maxLevel > 0 && level >= maxLevel) return Integer.MAX_VALUE;

        String mode = configManager.getLevelCurveMode();
        if ("list".equalsIgnoreCase(mode)) {
            List<Integer> list = configManager.getLevelCurveList();
            int index = level - 1;
            if (index >= 0 && index < list.size()) {
                return list.get(index);
            }
            return Integer.MAX_VALUE;
        }

        String expression = configManager.getLevelCurveExpression();
        String expr = expression.replace("{level}", String.valueOf(level));
        try {
            return (int) Math.round(evaluateExpression(expr));
        } catch (Exception e) {
            return 100;
        }
    }

    public int getRequiredExpForPlayer(UUID uuid) {
        int level = getLevel(uuid);
        return getRequiredExp(level);
    }

    public boolean checkLevelUp(Player player) {
        PlayerData data = playerDataMap.get(player.getUniqueId());
        if (data == null) return false;

        int maxLevel = configManager.getMaxLevel();
        if (maxLevel > 0 && data.getLevel() >= maxLevel) return false;

        int required = getRequiredExp(data.getLevel());
        if (data.getExp() >= required) {
            int oldLevel = data.getLevel();
            data.setLevel(oldLevel + 1);
            data.setExp(data.getExp() - required);

            PlayerLevelUpEvent event = new PlayerLevelUpEvent(player, oldLevel, data.getLevel());
            Bukkit.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                int newLevel = event.getNewLevel();
                data.setLevel(newLevel);
                ItemCoreAPI.refreshPlayerAttributes(player);
                plugin.getExpManager().sendLevelUpNotification(player, oldLevel, newLevel);

                if (configManager.isDebug()) {
                    plugin.getLogger().info(player.getName() + " 升级: " + oldLevel + " -> " + newLevel);
                }

                if (maxLevel <= 0 || data.getLevel() < maxLevel) {
                    int remainingExp = data.getExp();
                    data.setExp(0);
                    checkLevelUp(player);
                    data.setExp(data.getExp() + remainingExp);
                }
                return true;
            }
        }
        return false;
    }

    public void setLevel(Player player, int level) {
        PlayerData data = getOrCreatePlayerData(player.getUniqueId());
        int maxLevel = configManager.getMaxLevel();
        if (maxLevel > 0) level = Math.min(level, maxLevel);
        data.setLevel(Math.max(1, level));
        data.setExp(0);
        data.touch();
        ItemCoreAPI.refreshPlayerAttributes(player);
    }

    public void setExp(UUID uuid, int amount) {
        PlayerData data = playerDataMap.get(uuid);
        if (data != null) {
            data.setExp(Math.max(0, amount));
            data.touch();
        }
    }

    public void resetPlayer(Player player) {
        PlayerData data = getOrCreatePlayerData(player.getUniqueId());
        data.setLevel(configManager.getStartLevel());
        data.setExp(configManager.getStartExp());
        data.touch();
        ItemCoreAPI.refreshPlayerAttributes(player);
    }

    private double evaluateExpression(String expr) {
        LevelExpressionParser parser = new LevelExpressionParser(expr);
        double result = parser.parse();
        if (parser.pos < parser.expr.length()) throw new RuntimeException("Unexpected: " + (char) parser.ch);
        return result;
    }

    private static class LevelExpressionParser {
        private final String expr;
        private int pos = -1;
        private int ch;

        LevelExpressionParser(String expr) {
            this.expr = expr.replaceAll("\\s+", "");
        }

        private void nextChar() { ch = (++pos < expr.length()) ? expr.charAt(pos) : -1; }

        private boolean eat(int charToEat) {
            while (ch == ' ') nextChar();
            if (ch == charToEat) { nextChar(); return true; }
            return false;
        }

        double parse() { nextChar(); return parseExpression(); }

        private double parseExpression() {
            double x = parseTerm();
            for (;;) {
                if (eat('+')) x += parseTerm();
                else if (eat('-')) x -= parseTerm();
                else return x;
            }
        }

        private double parseTerm() {
            double x = parseFactor();
            for (;;) {
                if (eat('*')) x *= parseFactor();
                else if (eat('/')) x /= parseFactor();
                else return x;
            }
        }

        private double parseFactor() {
            if (eat('+')) return parseFactor();
            if (eat('-')) return -parseFactor();
            double x;
            int startPos = this.pos;
            if (eat('(')) {
                x = parseExpression();
                eat(')');
            } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                x = Double.parseDouble(expr.substring(startPos, this.pos));
            } else if (ch >= 'a' && ch <= 'z') {
                while ((ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9')) nextChar();
                String func = expr.substring(startPos, this.pos);
                if (eat('(')) { x = parseExpression(); eat(')'); }
                else { x = parseFactor(); }
                switch (func) {
                    case "sqrt": x = Math.sqrt(x); break;
                    case "abs": x = Math.abs(x); break;
                    case "floor": x = Math.floor(x); break;
                    case "ceil": x = Math.ceil(x); break;
                    default: throw new RuntimeException("Unknown function: " + func);
                }
            } else {
                throw new RuntimeException("Unexpected: " + (char) ch);
            }
            if (eat('^')) x = Math.pow(x, parseFactor());
            return x;
        }
    }
}