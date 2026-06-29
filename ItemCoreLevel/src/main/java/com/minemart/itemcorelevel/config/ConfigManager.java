package com.minemart.itemcorelevel.config;

import com.minemart.itemcorelevel.ItemCoreLevel;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import com.minemart.itemcore.item.attribute.CustomAttribute;
import java.util.*;

public class ConfigManager {

    private final ItemCoreLevel plugin;
    private FileConfiguration config;

    public ConfigManager(ItemCoreLevel plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public void reload() {
        load();
    }

    // ── Debug ──
    public boolean isDebug() { return config.getBoolean("debug", false); }

    // ── 基础等级 ──
    public int getStartLevel() { return config.getInt("start-level", 1); }
    public int getStartExp() { return config.getInt("start-exp", 0); }
    public int getMaxLevel() { return config.getInt("max-level", 100); }

    // ── 等级曲线 ──
    public String getLevelCurveMode() { return config.getString("level-curve.mode", "formula"); }
    public String getLevelCurveExpression() { return config.getString("level-curve.expression", "100*{level}^2+50*{level}"); }
    public List<Integer> getLevelCurveList() { return config.getIntegerList("level-curve.list"); }

    // ── 等级奖励 ──
    public Map<CustomAttribute, Double> getGeneralRewards() {
        Map<CustomAttribute, Double> result = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("level-rewards.general");
        if (section == null) return result;
        for (String key : section.getKeys(false)) {
            CustomAttribute attr = CustomAttribute.fromConfigKey(key);
            if (attr != null) {
                result.put(attr, section.getDouble(key));
            }
        }
        return result;
    }

    public Map<Integer, Map<CustomAttribute, Double>> getSpecificRewards() {
        Map<Integer, Map<CustomAttribute, Double>> result = new LinkedHashMap<>();
        ConfigurationSection section = config.getConfigurationSection("level-rewards.specific");
        if (section == null) return result;
        for (String levelKey : section.getKeys(false)) {
            try {
                int level = Integer.parseInt(levelKey);
                ConfigurationSection attrSection = section.getConfigurationSection(levelKey);
                Map<CustomAttribute, Double> attrs = new HashMap<>();
                if (attrSection != null) {
                    for (String attrKey : attrSection.getKeys(false)) {
                        CustomAttribute attr = CustomAttribute.fromConfigKey(attrKey);
                        if (attr != null) {
                            attrs.put(attr, attrSection.getDouble(attrKey));
                        }
                    }
                }
                result.put(level, attrs);
            } catch (NumberFormatException ignored) {}
        }
        return result;
    }

    // ── 怪物经验 ──
    public int getMonsterExpDefault() { return config.getInt("monster-exp.default", 10); }
    public boolean isUseMobLevel() { return config.getBoolean("monster-exp.use-mob-level", true); }
    public String getMobLevelFormula() { return config.getString("monster-exp.mob-level-formula", "{level}*5+10"); }
    public Map<String, Integer> getMonsterExpMobs() {
        Map<String, Integer> result = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("monster-exp.mobs");
        if (section == null) return result;
        for (String key : section.getKeys(false)) {
            result.put(key, section.getInt(key));
        }
        return result;
    }

    // ── 经验来源 ──
    public boolean isMythicMobsExpEnabled() { return config.getBoolean("exp-sources.mythicmobs.enabled", true); }
    public boolean isKillerOnly() { return config.getBoolean("exp-sources.mythicmobs.killer-only", true); }
    public boolean isShareInParty() { return config.getBoolean("exp-sources.mythicmobs.share-in-party", false); }
    public double getExpMultiplier() { return config.getDouble("exp-sources.mythicmobs.multiplier", 1.0); }

    // ── 经验提示 ──
    public boolean isExpGainEnabled() { return config.getBoolean("exp-gain.enabled", true); }
    public String getExpGainMessage() { return config.getString("exp-gain.message", "&a+{amount} &7经验值 (&f{progress}&7)"); }
    public String getExpGainSound() { return config.getString("exp-gain.sound", "ENTITY_EXPERIENCE_ORB_PICKUP"); }
    public float getExpGainSoundVolume() { return (float) config.getDouble("exp-gain.sound-volume", 0.5); }
    public float getExpGainSoundPitch() { return (float) config.getDouble("exp-gain.sound-pitch", 1.0); }

    // ── 升级提示 ──
    public boolean isLevelUpEnabled() { return config.getBoolean("level-up.enabled", true); }
    public String getLevelUpMode() { return config.getString("level-up.mode", "title"); }
    public String getLevelUpChatMessage() { return config.getString("level-up.chat.message", "&b&l\u2726 {player} &f升级了！ &7({old_level} \u2192 &b{level}&7)"); }
    public String getLevelUpActionBarMessage() { return config.getString("level-up.actionbar.message", "&b&l\u2726 升级！ &7你现在是 &bLv.{level}"); }
    public String getLevelUpTitleMain() { return config.getString("level-up.title.main", "&b&l\u2726 升级！"); }
    public String getLevelUpTitleSub() { return config.getString("level-up.title.sub", "&7Lv.{level}"); }
    public int getLevelUpTitleFadeIn() { return config.getInt("level-up.title.fade-in", 10); }
    public int getLevelUpTitleStay() { return config.getInt("level-up.title.stay", 40); }
    public int getLevelUpTitleFadeOut() { return config.getInt("level-up.title.fade-out", 10); }
    public String getLevelUpSound() { return config.getString("level-up.sound", "ENTITY_PLAYER_LEVELUP"); }
    public float getLevelUpSoundVolume() { return (float) config.getDouble("level-up.sound-volume", 0.5); }
    public float getLevelUpSoundPitch() { return (float) config.getDouble("level-up.sound-pitch", 1.0); }

    // ── 数据库 ──
    public String getDatabaseHost() { return config.getString("database.host", "localhost"); }
    public int getDatabasePort() { return config.getInt("database.port", 3306); }
    public String getDatabaseName() { return config.getString("database.database", "itemcore"); }
    public String getDatabaseTablePrefix() { return config.getString("database.table-prefix", "ic_level_"); }
    public String getDatabaseUsername() { return config.getString("database.username", "root"); }
    public String getDatabasePassword() { return config.getString("database.password", ""); }
    public int getDatabaseMaxPoolSize() { return config.getInt("database.pool-settings.maximum-pool-size", 10); }
    public int getDatabaseMinIdle() { return config.getInt("database.pool-settings.minimum-idle", 2); }
    public int getDatabaseConnTimeout() { return config.getInt("database.pool-settings.connection-timeout", 5000); }

    // ── Placeholder ──
    public int getProgressBarLength() { return config.getInt("placeholder.progress-bar.length", 20); }
    public String getProgressBarDoneChar() { return config.getString("placeholder.progress-bar.done-char", "\u2588"); }
    public String getProgressBarDoneColor() { return config.getString("placeholder.progress-bar.done-color", "&a"); }
    public String getProgressBarNotDoneChar() { return config.getString("placeholder.progress-bar.not-done-char", "\u2591"); }
    public String getProgressBarNotDoneColor() { return config.getString("placeholder.progress-bar.not-done-color", "&7"); }
}