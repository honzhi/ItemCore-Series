package com.minemart.itemcoreforge.config;

import com.minemart.itemcoreforge.ItemCoreForge;
import com.minemart.itemcoreforge.core.Forge;
import com.minemart.itemcoreforge.trigger.Trigger;
import com.minemart.itemcoreforge.trigger.TriggerManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ForgeLoader {

    private final ItemCoreForge plugin;
    private final Map<String, Forge> forges = new HashMap<>();

    public ForgeLoader(ItemCoreForge plugin) {
        this.plugin = plugin;
    }

    public void loadAll() {
        forges.clear();
        
        File forgesDir = new File(plugin.getDataFolder(), plugin.getConfigManager().getForgesDirectory());
        if (!forgesDir.exists()) {
            forgesDir.mkdirs();
            saveDefaultForges(forgesDir);
        }
        
        File[] files = forgesDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) {
            return;
        }
        
        for (File file : files) {
            try {
                Forge forge = loadForge(file);
                if (forge != null) {
                    forges.put(forge.getForgeId(), forge);
                    plugin.getLogger().info("已加载锻造台: " + forge.getForgeId());
                }
            } catch (Exception e) {
                plugin.getLogger().severe("加载锻造台配置失败: " + file.getName() + " - " + e.getMessage());
            }
        }
    }

    private Forge loadForge(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        String forgeId = config.getString("forge-id");
        if (forgeId == null || forgeId.isEmpty()) {
            plugin.getLogger().warning("锻造台配置缺少 forge-id: " + file.getName());
            return null;
        }
        
        String displayName = config.getString("display-name", forgeId);
        
        int maxQueueSize = config.getInt("settings.max-queue-size", 6);
        String type = config.getString("settings.type", "custom");
        String layoutFile = config.getString("settings.layout-file", "Default.yml");
        
        Forge forge = new Forge(forgeId, displayName, maxQueueSize, type, layoutFile);
        
        if (config.contains("recipes")) {
            for (String recipeKey : config.getConfigurationSection("recipes").getKeys(false)) {
                Forge.Recipe recipe = loadRecipe(config, "recipes." + recipeKey, recipeKey);
                if (recipe != null) {
                    forge.addRecipe(recipeKey, recipe);
                }
            }
        }
        
        return forge;
    }

    private Forge.Recipe loadRecipe(FileConfiguration config, String path, String recipeId) {
        Forge.Recipe recipe = new Forge.Recipe(recipeId);
        
        if (config.contains(path + ".input")) {
            recipe.setInput(loadItemReference(config, path + ".input"));
        }
        
        if (config.contains(path + ".output")) {
            recipe.setOutput(loadItemReference(config, path + ".output"));
        }
        
        if (config.contains(path + ".materials")) {
            for (Map<?, ?> materialMap : config.getMapList(path + ".materials")) {
                Forge.ItemReference ref = loadItemReferenceFromMap(materialMap);
                if (ref != null) {
                    recipe.addMaterial(ref);
                }
            }
        }
        
        if (config.contains(path + ".conditions")) {
            for (Map<?, ?> conditionMap : config.getMapList(path + ".conditions")) {
                Forge.Condition condition = loadConditionFromMap(conditionMap);
                if (condition != null) {
                    recipe.addCondition(condition);
                }
            }
        }
        
        recipe.setCraftTime(config.getDouble(path + ".craft-time", 1.0));
        recipe.setCookTime(config.getInt(path + ".cook-time", 200));
        recipe.setExactPlacement(config.getBoolean(path + ".exact_placement", false));
        recipe.setSuccessRate(config.getInt(path + ".success-rate", 100));
        recipe.setConsumeOnFail(config.getBoolean(path + ".consume-on-fail", true));

        ConfigurationSection onStartSection = config.getConfigurationSection(path + ".triggers.on-start");
        if (onStartSection != null) {
            for (Trigger trigger : TriggerManager.loadTriggers(onStartSection)) {
                recipe.addOnStart(trigger);
            }
        }

        ConfigurationSection onClaimSection = config.getConfigurationSection(path + ".triggers.on-claim");
        if (onClaimSection != null) {
            for (Trigger trigger : TriggerManager.loadTriggers(onClaimSection)) {
                recipe.addOnClaim(trigger);
            }
        }

        ConfigurationSection onCancelSection = config.getConfigurationSection(path + ".triggers.on-cancel");
        if (onCancelSection != null) {
            for (Trigger trigger : TriggerManager.loadTriggers(onCancelSection)) {
                recipe.addOnCancel(trigger);
            }
        }
        
        return recipe;
    }

    private Forge.ItemReference loadItemReference(FileConfiguration config, String path) {
        Forge.ItemReference ref = new Forge.ItemReference();
        
        ref.setSource(config.getString(path + ".source", "itemcore"));
        ref.setCategory(config.getString(path + ".category", ""));
        ref.setId(config.getString(path + ".id", ""));
        ref.setAmount(config.getInt(path + ".amount", 1));
        ref.setCheckDurability(config.getBoolean(path + ".check-durability", false));
        
        return ref;
    }

    private Forge.ItemReference loadItemReferenceFromMap(Map<?, ?> map) {
        Forge.ItemReference ref = new Forge.ItemReference();
        
        ref.setSource(getString(map, "source", "itemcore"));
        ref.setCategory(getString(map, "category", ""));
        ref.setId(getString(map, "id", ""));
        ref.setAmount(getInt(map, "amount", 1));
        ref.setCheckDurability(getBoolean(map, "check-durability", false));
        ref.setSlot(getInt(map, "slot", -1));
        
        return ref;
    }

    private Forge.Condition loadConditionFromMap(Map<?, ?> map) {
        String type = getString(map, "type", "level");
        Forge.Condition condition = new Forge.Condition(type);
        
        switch (type) {
            case "level" -> condition.setValue(getInt(map, "value", 0));
            case "permission" -> condition.setNode(getString(map, "node", ""));
            case "money" -> condition.setAmount(getDouble(map, "amount", 0));
        }
        
        return condition;
    }

    private String getString(Map<?, ?> map, String key, String def) {
        Object value = map.get(key);
        return value != null ? value.toString() : def;
    }

    private int getInt(Map<?, ?> map, String key, int def) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return def;
    }

    private double getDouble(Map<?, ?> map, String key, double def) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return def;
    }

    private boolean getBoolean(Map<?, ?> map, String key, boolean def) {
        Object value = map.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return def;
    }

    private void saveDefaultForges(File forgesDir) {
    }

    public Forge getForge(String forgeId) {
        return forges.get(forgeId);
    }

    public Collection<Forge> getAllForges() {
        return forges.values();
    }

    public Set<String> getForgeIds() {
        return forges.keySet();
    }

    public int getForgeCount() {
        return forges.size();
    }

    public boolean hasForge(String forgeId) {
        return forges.containsKey(forgeId);
    }
}
