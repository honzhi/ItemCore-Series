package com.minemart.itemcoremythic.bridge;

import com.minemart.itemcoremythic.ItemCoreMythic;
import com.minemart.itemcoremythic.provider.SkillProvider;
import com.minemart.itemcoremythic.provider.impl.MythicMobsSkillProvider;
import com.minemart.itemcoremythic.util.DebugLogger;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SkillBridge {

    private final ItemCoreMythic plugin;
    private final Map<String, SkillProvider> providers = new HashMap<>();

    public SkillBridge(ItemCoreMythic plugin) {
        this.plugin = plugin;
        registerProviders();
    }

    private void registerProviders() {
        providers.put("mythicmobs", new MythicMobsSkillProvider(plugin));
        DebugLogger.debug("SkillBridge", "Registered providers: " + providers.keySet());
    }

    public void executeSkill(Player player, String providerId, String skillName, LivingEntity target, Location location) {
        DebugLogger.debug("SkillBridge", "Routing skill to provider=" + providerId + ", skill=" + skillName);
        
        SkillProvider provider = providers.get(providerId);
        if (provider == null) {
            DebugLogger.debug("SkillBridge", "Provider not found: " + providerId);
            return;
        }

        boolean success = provider.executeSkill(player, skillName, target, location);
        
        DebugLogger.debug("SkillBridge", "Provider result=" + success + ", skill=" + skillName);
        
        if (!success) {
            plugin.getLogger().warning("Failed to execute skill: " + skillName + " via provider: " + providerId);
        }
    }

    public void registerProvider(SkillProvider provider) {
        providers.put(provider.getProviderId(), provider);
        DebugLogger.debug("SkillBridge", "Registered new provider: " + provider.getProviderId());
    }

    public SkillProvider getProvider(String providerId) {
        return providers.get(providerId);
    }
}
