package com.minemart.itemcoremythic;

import com.minemart.itemcoremythic.bridge.SkillBridge;
import com.minemart.itemcoremythic.listener.MMLoadListener;
import com.minemart.itemcoremythic.listener.SkillTriggerListener;
import com.minemart.itemcoremythic.placeholder.ICPlaceholderExpansion;
import com.minemart.itemcoremythic.util.DebugLogger;
import io.lumine.mythic.api.MythicPlugin;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemCoreMythic extends JavaPlugin {

    private static ItemCoreMythic instance;
    public static boolean DEBUG = false;
    
    private MythicPlugin mythicMobs;
    private SkillBridge skillBridge;
    private ICPlaceholderExpansion placeholderExpansion;

    @Override
    public void onEnable() {
        instance = this;
        
        saveDefaultConfig();
        DEBUG = getConfig().getBoolean("debug", false);
        
        printStartupBanner();

        if (!checkDependencies()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        initializeComponents();
        registerListeners();

        getLogger().info("ItemCoreMythic v1.0.0 has been enabled!");
        DebugLogger.debug("Plugin enabled successfully");
    }

    @Override
    public void onDisable() {
        getLogger().info("ItemCoreMythic has been disabled!");
        DebugLogger.debug("Plugin disabled");
    }

    private void printStartupBanner() {
        getLogger().info("===== ItemCoreMythic =====");
        getLogger().info("Version: 1.0.0");
        getLogger().info("Debug: " + DEBUG);
        getLogger().info("=========================");
    }

    private boolean checkDependencies() {
        boolean allOk = true;
        
        Plugin itemCore = Bukkit.getPluginManager().getPlugin("ItemCore");
        if (itemCore == null || !itemCore.isEnabled()) {
            getLogger().severe("ItemCore is not installed or not enabled!");
            allOk = false;
        } else {
            getLogger().info("ItemCore: OK (v" + itemCore.getDescription().getVersion() + ")");
            DebugLogger.debug("Dependency", "ItemCore found: v" + itemCore.getDescription().getVersion());
        }

        Plugin mm = Bukkit.getPluginManager().getPlugin("MythicMobs");
        if (mm == null || !mm.isEnabled()) {
            getLogger().severe("MythicMobs is not installed or not enabled!");
            allOk = false;
        } else {
            mythicMobs = (MythicBukkit) mm;
            getLogger().info("MythicMobs: OK (v" + mm.getDescription().getVersion() + ")");
            DebugLogger.debug("Dependency", "MythicMobs found: v" + mm.getDescription().getVersion());
        }
        
        if (allOk) {
            getLogger().info("SkillProvider: MythicMobsSkillProvider");
            DebugLogger.debug("Setup", "SkillProvider: MythicMobsSkillProvider");
        }
        
        return allOk;
    }

    private void initializeComponents() {
        skillBridge = new SkillBridge(this);
        placeholderExpansion = new ICPlaceholderExpansion(this);
        placeholderExpansion.register();
        DebugLogger.debug("Setup", "Components initialized");
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new SkillTriggerListener(this), this);
        pm.registerEvents(new MMLoadListener(this), this);
        DebugLogger.debug("Setup", "Listeners registered");
    }

    public static ItemCoreMythic getInstance() {
        return instance;
    }

    public MythicPlugin getMythicMobs() {
        return mythicMobs;
    }

    public SkillBridge getSkillBridge() {
        return skillBridge;
    }
}
