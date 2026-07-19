package com.minemart.itemcoreforge;

import com.minemart.itemcoreforge.config.ConfigManager;
import com.minemart.itemcoreforge.config.ForgeLoader;
import com.minemart.itemcoreforge.config.LayoutLoader;
import com.minemart.itemcoreforge.core.ForgeManager;
import com.minemart.itemcoreforge.gui.GuiListener;
import com.minemart.itemcoreforge.storage.DataStorage;
import com.minemart.itemcoreforge.task.CraftingQueueManager;
import com.minemart.itemcoreforge.type.ForgeTypeManager;
import com.minemart.itemcoreforge.utils.MaterialChecker;
import com.minemart.itemcoreforge.utils.MessageUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemCoreForge extends JavaPlugin implements Listener {

    private static ItemCoreForge instance;
    
    private ConfigManager configManager;
    private ForgeLoader forgeLoader;
    private LayoutLoader layoutLoader;
    private ForgeManager forgeManager;
    private CraftingQueueManager craftingQueueManager;
    private GuiListener guiListener;
    private DataStorage dataStorage;
    private ForgeTypeManager typeManager;
    private MaterialChecker materialChecker;

    @Override
    public void onEnable() {
        instance = this;
        
        // === 红橙渐变色代码 ===
        final String R1 = "\u001B[38;5;52m";
        final String R2 = "\u001B[38;5;88m";
        final String R3 = "\u001B[38;5;124m";
        final String R4 = "\u001B[38;5;160m";
        final String R5 = "\u001B[38;5;166m";
        final String R6 = "\u001B[38;5;172m";
        final String R7 = "\u001B[38;5;178m";
        final String O1 = "\u001B[38;5;202m";
        final String O2 = "\u001B[38;5;208m";
        final String O3 = "\u001B[38;5;214m";
        final String O4 = "\u001B[38;5;220m";
        final String GOLD = "\u001B[38;5;226m";
        final String O5   = "\u001B[38;5;228m";
        final String RESET = "\u001B[0m";
        final String GREEN = "\u001B[32m";
        
        String nameGradient = R1 + "I" + R2 + "t" + R3 + "e" + R4 + "m"
                            + R5 + "C" + R6 + "o" + R7 + "r" + O1 + "e"
                            + O2 + "F" + O3 + "o" + O4 + "r" + GOLD + "g" + O5 + "e" + RESET;
        
        getLogger().info("========================================");
        getLogger().info("    " + nameGradient + "  " + GREEN + "v" + getDescription().getVersion() + RESET);
        getLogger().info("========================================");
        
        initManagers();
        loadConfigs();
        
        if (checkItemCore()) {
            getLogger().info("  ✓ 检测到 ItemCore 插件");
        } else {
            getLogger().warning("  ✗ 未检测到 ItemCore 插件");
        }
        
        if (checkMythicMobs()) {
            getLogger().info("  ✓ 检测到 MythicMobs 插件");
            if (isDebugEnabled()) {
                debugMythicMobsAPI();
            }
        } else {
            getLogger().warning("  ✗ 未检测到 MythicMobs 插件");
        }
        
        registerCommands();
        registerListeners();
        registerTypeHandlers();
        
        getLogger().info("  ItemCoreForge 已启用  |  已加载 " + forgeLoader.getForgeCount() + " 个锻造台");
    }

    @Override
    public void onDisable() {
        if (dataStorage != null) {
            dataStorage.saveAllData();
        }
        if (craftingQueueManager != null) {
            craftingQueueManager.shutdown();
        }
        if (typeManager != null) {
            typeManager.disableAll();
        }
        
        getLogger().info("ItemCoreForge 已禁用！");
    }

    private boolean checkItemCore() {
        return getServer().getPluginManager().getPlugin("ItemCore") != null;
    }

    private boolean checkMythicMobs() {
        return getServer().getPluginManager().getPlugin("MythicMobs") != null;
    }
    
    private boolean isDebugEnabled() {
        return configManager != null && configManager.isDebug();
    }
    
    private void debugMythicMobsAPI() {
        getLogger().info("--- MythicMobs API 检测 ---");
        try {
            Class<?> mythicBukkitClass = Class.forName("io.lumine.mythic.bukkit.MythicBukkit");
            getLogger().info("✓ MythicBukkit 类: " + mythicBukkitClass.getName());
            
            Object mythicBukkit = mythicBukkitClass.getMethod("inst").invoke(null);
            getLogger().info("✓ 获取 MythicBukkit 实例成功");
            
            Object itemManager = mythicBukkitClass.getMethod("getItemManager").invoke(mythicBukkit);
            Class<?> itemManagerClass = itemManager.getClass();
            getLogger().info("✓ ItemManager 类: " + itemManagerClass.getName());
            
            boolean hasGetItemStack = false;
            boolean hasGetItem = false;
            for (java.lang.reflect.Method m : itemManagerClass.getMethods()) {
                if (m.getName().equals("getItemStack") && m.getParameterCount() == 1) {
                    hasGetItemStack = true;
                    getLogger().info("✓ 找到 getItemStack(String) 方法，返回类型: " + m.getReturnType().getSimpleName());
                }
                if (m.getName().equals("getItem") && m.getParameterCount() == 1) {
                    hasGetItem = true;
                    getLogger().info("✓ 找到 getItem(String) 方法，返回类型: " + m.getReturnType().getSimpleName());
                }
            }
            
            if (!hasGetItemStack && !hasGetItem) {
                getLogger().warning("✗ 未找到 getItemStack 或 getItem 方法！");
            }
            
            getLogger().info("--- MythicMobs API 检测完成 ---");
        } catch (ClassNotFoundException e) {
            getLogger().warning("✗ 无法找到 MythicBukkit 类: " + e.getMessage());
        } catch (Exception e) {
            getLogger().warning("✗ MythicMobs API 检测失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initManagers() {
        configManager = new ConfigManager(this);
        forgeLoader = new ForgeLoader(this);
        layoutLoader = new LayoutLoader(this);
        forgeManager = new ForgeManager(this);
        materialChecker = new MaterialChecker();
        craftingQueueManager = new CraftingQueueManager(this);
        dataStorage = new DataStorage(this);
        typeManager = new ForgeTypeManager(this);
    }

    private void registerTypeHandlers() {
        typeManager.init();
        typeManager.enableAll();
        for (com.minemart.itemcoreforge.core.Forge forge : forgeLoader.getAllForges()) {
            typeManager.registerForgeToHandler(forge);
        }
    }

    private void loadConfigs() {
        configManager.load();
        forgeLoader.loadAll();
        layoutLoader.loadAll();
    }

    private void registerCommands() {
        getCommand("itemcoreforge").setExecutor(new com.minemart.itemcoreforge.command.ForgeCommand(this));
        getCommand("itemcoreforge").setTabCompleter(new com.minemart.itemcoreforge.command.ForgeTabCompleter(this));
    }

    private void registerListeners() {
        guiListener = new GuiListener(this);
        getServer().getPluginManager().registerEvents(guiListener, this);
        getServer().getPluginManager().registerEvents(this, this);
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (dataStorage != null) {
            dataStorage.loadPlayerData(event.getPlayer());
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (dataStorage != null) {
            dataStorage.savePlayerData(event.getPlayer());
        }
    }

    public void reload() {
        configManager.reload();
        forgeLoader.loadAll();
        layoutLoader.loadAll();
        getLogger().info("配置已重载！已加载 " + forgeLoader.getForgeCount() + " 个锻造台");
        
        if (typeManager != null) {
            typeManager.reloadAll();
            for (com.minemart.itemcoreforge.core.Forge forge : forgeLoader.getAllForges()) {
                typeManager.registerForgeToHandler(forge);
            }
        }
    }

    public static ItemCoreForge getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ForgeLoader getForgeLoader() {
        return forgeLoader;
    }

    public LayoutLoader getLayoutLoader() {
        return layoutLoader;
    }

    public ForgeManager getForgeManager() {
        return forgeManager;
    }

    public CraftingQueueManager getCraftingQueueManager() {
        return craftingQueueManager;
    }

    public GuiListener getGuiListener() {
        return guiListener;
    }
    
    public DataStorage getDataStorage() {
        return dataStorage;
    }
    
    public ForgeTypeManager getTypeManager() {
        return typeManager;
    }
    
    public MaterialChecker getMaterialChecker() {
        return materialChecker;
    }
}
