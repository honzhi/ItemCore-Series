package com.minemart.itemcoretrinkets;

import com.minemart.itemcore.api.AttributeProvider;
import com.minemart.itemcore.api.ItemCoreAPI;
import com.minemart.itemcoretrinkets.api.TrinketAPI;
import com.minemart.itemcoretrinkets.command.TrinketCommand;
import com.minemart.itemcoretrinkets.command.TrinketTabCompleter;
import com.minemart.itemcoretrinkets.config.ConfigManager;
import com.minemart.itemcoretrinkets.config.SlotLoader;
import com.minemart.itemcoretrinkets.core.TrinketManager;
import com.minemart.itemcoretrinkets.calculator.AttributeCalculator;
import com.minemart.itemcoretrinkets.gui.GuiListener;
import com.minemart.itemcoretrinkets.listener.PlayerListener;
import com.minemart.itemcoretrinkets.storage.DataStorage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ItemCoreTrinkets extends JavaPlugin {

    private ConfigManager configManager;
    private SlotLoader slotLoader;
    private DataStorage dataStorage;
    private TrinketManager trinketManager;
    private AttributeCalculator attributeCalculator;
    private GuiListener guiListener;

    @Override
    public void onEnable() {
        initializeManagers();
        registerListeners();
        registerCommands();
        setupAutoSave();
        registerAttributeProvider();

        TrinketAPI.setPlugin(this);

        getLogger().info("ItemCoreTrinkets 饰品插件已启用");
        getLogger().info("已加载 " + slotLoader.getSlotCount() + " 个饰品槽位");

        if (configManager.isDebugMode()) {
            getLogger().info("[Debug] ItemCoreTrinkets 启动完成，debug 模式已开启");
        }
    }

    private void registerAttributeProvider() {
        ItemCoreAPI.registerAttributeProvider(player -> {
            if (!player.isOnline()) return null;
            return attributeCalculator.calculatePlayerTrinketAttributes(player);
        });
        getLogger().info("饰品属性提供者已注册到 ItemCore");
    }

    @Override
    public void onDisable() {
        if (trinketManager != null) {
            trinketManager.saveAll();
        }
        getLogger().info("ItemCoreTrinkets 饰品插件已禁用");
    }

    private void initializeManagers() {
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        configManager.loadSlots();
        configManager.loadGuiLayout();

        slotLoader = new SlotLoader(this);
        slotLoader.loadSlots();

        dataStorage = new DataStorage(this);

        trinketManager = new TrinketManager(this);

        attributeCalculator = new AttributeCalculator(this);

        guiListener = new GuiListener(this);
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new PlayerListener(this), this);
        pm.registerEvents(guiListener, this);
    }

    private void registerCommands() {
        getCommand("ict").setExecutor(new TrinketCommand(this));
        getCommand("ict").setTabCompleter(new TrinketTabCompleter(this));
    }

    private void setupAutoSave() {
        int interval = configManager.getAutoSaveInterval();
        if (interval > 0) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    trinketManager.saveAll();
                    if (configManager.isDebugMode()) {
                        getLogger().info("[Debug] 自动保存玩家饰品数据完成");
                    }
                }
            }.runTaskTimer(this, 0, interval * 20);
        }
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public SlotLoader getSlotLoader() {
        return slotLoader;
    }

    public DataStorage getDataStorage() {
        return dataStorage;
    }

    public TrinketManager getTrinketManager() {
        return trinketManager;
    }

    public AttributeCalculator getAttributeCalculator() {
        return attributeCalculator;
    }

    public GuiListener getGuiListener() {
        return guiListener;
    }
}