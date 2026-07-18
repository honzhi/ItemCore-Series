package com.minemart.itemcorerpg;

import com.minemart.itemcorerpg.command.CommandManager;
import com.minemart.itemcorerpg.config.ConfigManager;
import com.minemart.itemcorerpg.gui.GuiListener;
import com.minemart.itemcorerpg.listener.CombatListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemCoreRPG extends JavaPlugin {

    private static ItemCoreRPG instance;
    private ConfigManager configManager;
    private CombatListener combatListener;
    private CommandManager commandManager;

    public static ItemCoreRPG getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        configManager = new ConfigManager(this);
        configManager.load();

        combatListener = new CombatListener(this);
        Bukkit.getPluginManager().registerEvents(combatListener, this);

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
        getLogger().info("ItemCoreRPG \u5df2\u7981\u7528");
    }
}
