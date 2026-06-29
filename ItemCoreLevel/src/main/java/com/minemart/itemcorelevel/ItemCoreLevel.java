package com.minemart.itemcorelevel;

import com.minemart.itemcore.api.ItemCoreAPI;
import com.minemart.itemcorelevel.command.LevelCommand;
import com.minemart.itemcorelevel.command.LevelTabCompleter;
import com.minemart.itemcorelevel.config.ConfigManager;
import com.minemart.itemcorelevel.listener.MythicExpListener;
import com.minemart.itemcorelevel.listener.PlayerListener;
import com.minemart.itemcorelevel.manager.ExpManager;
import com.minemart.itemcorelevel.manager.LevelManager;
import com.minemart.itemcorelevel.placeholder.LevelExpansion;
import com.minemart.itemcorelevel.provider.LevelAttributeProvider;
import com.minemart.itemcorelevel.storage.DataStorage;
import com.minemart.itemcorelevel.storage.MySQLStorage;
import com.minemart.itemcorelevel.storage.SQLiteStorage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;

public class ItemCoreLevel extends JavaPlugin {

    private static ItemCoreLevel instance;
    private ConfigManager configManager;
    private DataStorage dataStorage;
    private LevelManager levelManager;
    private ExpManager expManager;
    private boolean itemCoreAvailable = false;
    private boolean mythicMobsAvailable = false;
    private int autoSaveTaskId = -1;

    public static ItemCoreLevel getInstance() { return instance; }
    public ConfigManager getConfigManager() { return configManager; }
    public DataStorage getDataStorage() { return dataStorage; }
    public LevelManager getLevelManager() { return levelManager; }
    public ExpManager getExpManager() { return expManager; }

    @Override
    public void onEnable() {
        instance = this;
        long start = System.currentTimeMillis();

        getLogger().info("========================================");
        getLogger().info("  ItemCoreLevel v1.0.0");
        getLogger().info("========================================");

        // ── 加载配置 ──
        configManager = new ConfigManager(this);
        configManager.load();
        getLogger().info("配置文件已加载");

        // ── 检查软依赖 ──
        checkSoftDependencies();

        // ── 初始化存储 ──
        initDatabase();

        // ── 初始化管理器 ──
        levelManager = new LevelManager(this);
        expManager = new ExpManager(this);

        // ── 注册监听器 ──
        registerListeners();

        // ── 注册指令 ──
        registerCommands();

        // ── 注册 PlaceholderAPI ──
        registerPlaceholderAPI();

        // ── 注册属性提供者 ──
        if (itemCoreAvailable) {
            registerAttributeProvider();
        }

        // ── 加载在线玩家数据 ──
        loadOnlinePlayers();

        // ── 启动自动保存 ──
        startAutoSave();

        long elapsed = System.currentTimeMillis() - start;

        getLogger().info("ItemCoreLevel 已启用 (" + elapsed + "ms)");
    }

    @Override
    public void onDisable() {
        if (autoSaveTaskId != -1) {
            Bukkit.getScheduler().cancelTask(autoSaveTaskId);
        }
        if (levelManager != null && dataStorage != null) {
            Map<UUID, PlayerData> allData = levelManager.getAllPlayerData();
            if (!allData.isEmpty()) {
                for (PlayerData data : allData.values()) {
                    data.touch();
                }
                dataStorage.saveAll(allData);
                getLogger().info("已保存 " + allData.size() + " 名玩家的数据");
            }
        }
        if (dataStorage != null) {
            dataStorage.shutdown();
        }
        getLogger().info("ItemCoreLevel 已禁用");
    }

    private void checkSoftDependencies() {
        Plugin itemCore = Bukkit.getPluginManager().getPlugin("ItemCore");
        if (itemCore != null && itemCore.isEnabled()) {
            itemCoreAvailable = true;
            getLogger().info("ItemCore v" + itemCore.getDescription().getVersion() + " ✓ (属性系统已启用)");
        } else {
            getLogger().info("ItemCore 未安装 — 属性系统已禁用，等级经验系统仍可独立运行");
        }

        Plugin mm = Bukkit.getPluginManager().getPlugin("MythicMobs");
        if (mm != null && mm.isEnabled()) {
            mythicMobsAvailable = true;
            getLogger().info("MythicMobs v" + mm.getDescription().getVersion() + " ✓");
        }

        Plugin papi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        if (papi != null && papi.isEnabled()) {
            getLogger().info("PlaceholderAPI v" + papi.getDescription().getVersion() + " ✓");
        }
    }

    private void initDatabase() {
        // 尝试 MySQL
        if (isMySQLConfigured()) {
            try {
                dataStorage = new MySQLStorage(
                    configManager.getDatabaseHost(),
                    configManager.getDatabasePort(),
                    configManager.getDatabaseName(),
                    configManager.getDatabaseTablePrefix(),
                    configManager.getDatabaseUsername(),
                    configManager.getDatabasePassword(),
                    configManager.getDatabaseMaxPoolSize(),
                    configManager.getDatabaseMinIdle(),
                    configManager.getDatabaseConnTimeout(),
                    getLogger()
                );
                getLogger().info("使用 MySQL 存储");
                return;
            } catch (Exception e) {
                getLogger().warning("MySQL 连接失败: " + e.getMessage());
                getLogger().info("自动切换至 SQLite 存储");
            }
        } else {
            getLogger().info("未配置 MySQL，使用 SQLite 存储");
        }

        // 回退到 SQLite
        try {
            dataStorage = new SQLiteStorage(getDataFolder(), getLogger());
        } catch (Exception e) {
            getLogger().severe("SQLite 初始化失败: " + e.getMessage());
        }
    }

    private boolean isMySQLConfigured() {
        String host = configManager.getDatabaseHost();
        String user = configManager.getDatabaseUsername();
        String pass = configManager.getDatabasePassword();
        // 如果 host 是 localhost 但 user/pass 都是默认空值，认为未配置
        if ("localhost".equals(host) && "root".equals(user) && "".equals(pass)) {
            return false;
        }
        return true;
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerListener(this), this);
        if (mythicMobsAvailable) {
            pm.registerEvents(new MythicExpListener(this), this);
            getLogger().info("MythicMobs 经验掉落监听器已注册");
        }
    }

    private void registerCommands() {
        LevelCommand executor = new LevelCommand(this);
        getCommand("itemcorelevel").setExecutor(executor);
        getCommand("itemcorelevel").setTabCompleter(new LevelTabCompleter());
    }

    private void registerPlaceholderAPI() {
        Plugin papi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        if (papi != null && papi.isEnabled()) {
            new LevelExpansion(this).register();
            getLogger().info("PlaceholderAPI 扩展已注册");
        }
    }

    private void registerAttributeProvider() {
        ItemCoreAPI.registerAttributeProvider(new LevelAttributeProvider(this));
        getLogger().info("等级属性提供者已注册到 ItemCore");
    }

    private void loadOnlinePlayers() {
        if (dataStorage == null) {
            getLogger().warning("数据库未连接，无法加载在线玩家数据");
            return;
        }
        Bukkit.getOnlinePlayers().forEach(player -> {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                com.minemart.itemcorelevel.PlayerData data = dataStorage.load(player.getUniqueId());
                if (data == null) {
                    data = new com.minemart.itemcorelevel.PlayerData(player.getUniqueId());
                    data.setPlayerName(player.getName());
                    data.setLevel(configManager.getStartLevel());
                    data.setExp(configManager.getStartExp());
                } else {
                    data.setPlayerName(player.getName());
                }
                data.touch();
                levelManager.cachePlayerData(player.getUniqueId(), data);
            });
        });
    }

    private void startAutoSave() {
        if (dataStorage == null) {
            getLogger().warning("数据库未连接，自动保存已禁用");
            return;
        }
        autoSaveTaskId = Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            Map<UUID, PlayerData> allData = levelManager.getAllPlayerData();
            if (!allData.isEmpty()) {
                dataStorage.saveAll(allData);
                if (configManager.isDebug()) {
                    getLogger().info("自动保存完成: " + allData.size() + " 名玩家");
                }
            }
        }, 6000L, 6000L).getTaskId(); // 5分钟 (6000 ticks)
    }

    public void reloadAttributeProviders() {
        // 重新注册 AttributeProvider（先解注册再注册）
        // 由于 ItemCoreAPI 没有提供取消注册的方法，我们直接重新注册
        // AttributeProvider 列表是 ArrayList，多次注册会重复
        // 这里我们不做解注册，而是 rely on 配置重载后 LevelAttributeProvider 会读取新配置
        getLogger().info("配置已重载");
    }
}
