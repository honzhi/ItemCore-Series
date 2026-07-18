package com.minemart.itemcorerpg;

import com.minemart.itemcorerpg.command.CommandManager;
import com.minemart.itemcorerpg.config.ConfigManager;
import com.minemart.itemcorerpg.gui.GuiListener;
import com.minemart.itemcorerpg.listener.CombatListener;
import com.minemart.itemcorerpg.manager.HealthCompressionManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemCoreRPG extends JavaPlugin {

    private static ItemCoreRPG instance;
    private ConfigManager configManager;
    private CombatListener combatListener;
    private HealthCompressionManager healthCompressionManager;
    private CommandManager commandManager;

    public static ItemCoreRPG getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public HealthCompressionManager getHealthCompressionManager() {
        return healthCompressionManager;
    }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        configManager = new ConfigManager(this);
        configManager.load();

        combatListener = new CombatListener(this);
        Bukkit.getPluginManager().registerEvents(combatListener, this);

        healthCompressionManager = new HealthCompressionManager(this);
        Bukkit.getPluginManager().registerEvents(healthCompressionManager, this);
        healthCompressionManager.refreshAll();

        Bukkit.getPluginManager().registerEvents(new GuiListener(), this);

        commandManager = new CommandManager(this);
        getCommand("itemcorerpg").setExecutor(commandManager);
        getCommand("itemcorerpg").setTabCompleter(commandManager);

        getLogger().info("ItemCoreRPG \u5df2\u542f\u7528 - \u4f24\u5bb3\u663e\u793a\u529f\u80fd");
    }

    @Override
    public void onDisable() {
        if (combatListener != null) {
            combatListener.shutdown();
        }
        if (healthCompressionManager != null) {
            healthCompressionManager.shutdown();
        }
        getLogger().info("ItemCoreRPG \u5df2\u7981\u7528");
    }
}
