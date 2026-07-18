package com.minemart.itemcore;

import com.minemart.itemcore.api.ItemCoreAPI;
import com.minemart.itemcore.command.CommandManager;
import com.minemart.itemcore.config.AttributesConfig;
import com.minemart.itemcore.config.ConfigManager;
import com.minemart.itemcore.config.LoreManager;
import com.minemart.itemcore.config.MessagesManager;
import com.minemart.itemcore.core.CoreManager;
import com.minemart.itemcore.damage.DamageManager;
import com.minemart.itemcore.element.AccumulationManager;
import com.minemart.itemcore.element.AilmentConfig;
import com.minemart.itemcore.element.AilmentManager;
import com.minemart.itemcore.element.ElementConfig;
import com.minemart.itemcore.gui.GuiListener;
import com.minemart.itemcore.item.CustomItem;
import com.minemart.itemcore.item.attribute.ElementRegistry;
import com.minemart.itemcore.listener.ItemSkillListener;
import com.minemart.itemcore.listener.ListenerManager;
import com.minemart.itemcore.loader.LoaderManager;
import com.minemart.itemcore.placeholder.ItemCoreExpansion;
import com.minemart.itemcore.utils.ItemBuilder;
import com.minemart.itemcore.utils.MessageUtil;
import com.minemart.itemcore.util.DurabilityManager;
import org.bstats.bukkit.Metrics;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemCore extends JavaPlugin {

    private static ItemCore instance;
    private ElementRegistry elementRegistry;
    private ElementConfig elementConfig;
    private AilmentConfig ailmentConfig;
    private AccumulationManager accumulationManager;
    private AilmentManager ailmentManager;
    private ConfigManager configManager;
    private MessagesManager messagesManager;
    private LoreManager loreManager;
    private AttributesConfig attributesConfig;
    private LoaderManager loaderManager;
    private CoreManager coreManager;
    private ListenerManager listenerManager;
    private CommandManager commandManager;
    private GuiListener guiListener;
    private ItemSkillListener skillListener;
    private DurabilityManager durabilityManager;

    private final AtomicInteger loreVersion = new AtomicInteger(1);
    private int loreTaskId = -1;

    public static ItemCore getInstance() {
        return instance;
    }

    public static ElementRegistry getElementRegistry() {
        return instance.elementRegistry;
    }

    public ElementConfig getElementConfig() {
        return elementConfig;
    }

    public AilmentConfig getAilmentConfig() {
        return ailmentConfig;
    }

    public AccumulationManager getAccumulationManager() {
        return accumulationManager;
    }

    public AilmentManager getAilmentManager() {
        return ailmentManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    public LoreManager getLoreManager() {
        return loreManager;
    }

    public AttributesConfig getAttributesConfig() {
        return attributesConfig;
    }

    public LoaderManager getLoaderManager() {
        return loaderManager;
    }

    public int getLoreVersion() { return loreVersion.get(); }

    public CoreManager getCoreManager() {
        return coreManager;
    }

    public ListenerManager getListenerManager() {
        return listenerManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public GuiListener getGuiListener() {
        return guiListener;
    }

    public DurabilityManager getDurabilityManager() {
        return durabilityManager;
    }

    public ItemSkillListener getSkillListener() {
        return skillListener;
    }

    @Override
    public void onEnable() {
        instance = this;
        
        // 提前保存和加载配置，以便能显示语言信息
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        configManager.load();
        
        // 立即输出启动信息 - 简洁风格，丝滑蓝→青渐变
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_RESET = "\u001B[0m";
        
        // 256色渐变：深红→红→橙→金 (ItemCore 每个字符独立颜色)
        int[] colors = {52, 88, 124, 160, 166, 208, 214, 220};
        String nameGradient = "\u001B[38;5;" + colors[0] + "mI"    // 深红
                            + "\u001B[38;5;" + colors[1] + "mt"    // 暗红
                            + "\u001B[38;5;" + colors[2] + "me"    // 红
                            + "\u001B[38;5;" + colors[3] + "mm"    // 暖红
                            + "\u001B[38;5;" + colors[4] + "mC"    // 红橙
                            + "\u001B[38;5;" + colors[5] + "mo"    // 橙
                            + "\u001B[38;5;" + colors[6] + "mr"    // 亮橙
                            + "\u001B[38;5;" + colors[7] + "me";   // 金
        
        getLogger().info("========================================");
        getLogger().info("    " + nameGradient + ANSI_GREEN + "  v" + getDescription().getVersion() + ANSI_RESET + "    语言: " + configManager.getLanguage());
        getLogger().info("========================================");

        elementRegistry = new ElementRegistry();

        elementConfig = new ElementConfig(this);
        elementConfig.load();

        ailmentConfig = new AilmentConfig(this);
        ailmentConfig.load();

        accumulationManager = new AccumulationManager(this);
        accumulationManager.startDecayScheduler();

        ailmentManager = new AilmentManager(this);
        ailmentManager.startTickScheduler();

        messagesManager = new MessagesManager(this);
        messagesManager.load();

        attributesConfig = new AttributesConfig(this);
        attributesConfig.load();

        DamageManager.init(this);

        loreManager = new LoreManager(this);
        loreManager.load();

        loaderManager = new LoaderManager(this);
        loaderManager.loadAll();

        coreManager = new CoreManager(this);
        coreManager.initialize();

        listenerManager = new ListenerManager(this);
        listenerManager.registerAll();

        guiListener = new GuiListener(this);
        getServer().getPluginManager().registerEvents(guiListener, this);

        skillListener = new ItemSkillListener(this);
        getServer().getPluginManager().registerEvents(skillListener, this);

        commandManager = new CommandManager(this);
        getCommand("itemcore").setExecutor(commandManager);
        getCommand("itemcore").setTabCompleter(commandManager);

        // ── bStats 使用统计 ──
        new Metrics(this, 32281);

        // 初始化耐久系统
        DurabilityManager.init(this);

                if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new ItemCoreExpansion(this).register();
            getLogger().info("PlaceholderAPI 扩展已注册");
        }

        // ── Lore 自动刷新（由配置控制） ──
        if (configManager != null && configManager.isLoreRefreshEnabled()) {
            startLoreRefreshScheduler();
        }

        getLogger().info("========================================");
        getLogger().info(ANSI_GREEN + "  ItemCore 已启用" + ANSI_RESET);
        getLogger().info("========================================");
    }

    @Override
    public void onDisable() {
        stopLoreRefreshScheduler();

        if (listenerManager != null) {
            listenerManager.unregisterAll();
        }
        if (skillListener != null) {
            Bukkit.getOnlinePlayers().forEach(player -> 
                skillListener.unregisterTimerSkills(player)
            );
        }
        getLogger().info("ItemCore 已禁用");
    }

    public void reload() {
        DurabilityManager.init(this);
        if (skillListener != null) {
            Bukkit.getOnlinePlayers().forEach(skillListener::unregisterTimerSkills);
        }
        loreVersion.incrementAndGet();
        elementConfig.reload();
        configManager.reload();
        messagesManager.reload();
        attributesConfig.reload();
        loreManager.reload();
        loaderManager.reload();
        if (coreManager != null) {
            coreManager.reload();
        }

        // ── 重启 lore 刷新调度器（以应用配置变更） ──
        stopLoreRefreshScheduler();
        if (configManager != null && configManager.isLoreRefreshEnabled()) {
            startLoreRefreshScheduler();
        }

        getLogger().info("配置已重载");
    }

    private void startLoreRefreshScheduler() {
        int interval = configManager != null ? configManager.getLoreRefreshInterval() : 100;
        if (interval < 20) interval = 20; // 最小 1 秒

        getLogger().info("Lore 自动刷新已启动 (间隔=" + interval + " tick, 当前版本=" + loreVersion.get() + ")");

        loreTaskId = Bukkit.getScheduler().runTaskTimer(this, () -> {
            int currentVersion = loreVersion.get();
            if (currentVersion == 0) return;
            int totalPlayers = Bukkit.getOnlinePlayers().size();
            if (configManager != null && configManager.isDebugMode()) {
                getLogger().info("[LoreRefresh] 开始扫描 " + totalPlayers + " 位玩家, 当前版本=" + currentVersion);
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                refreshInventoryLore(player, currentVersion);
            }
        }, interval, interval).getTaskId();
    }

    private void stopLoreRefreshScheduler() {
        if (loreTaskId != -1) {
            Bukkit.getScheduler().cancelTask(loreTaskId);
            loreTaskId = -1;
        }
    }

    private void refreshInventoryLore(Player player, int currentVersion) {
        boolean debug = configManager != null && configManager.isDebugMode();
        int refreshed = 0;
        PlayerInventory inv = player.getInventory();
        for (int slot = 0; slot < 41; slot++) {
            ItemStack item = inv.getItem(slot);
            if (item == null || item.getType() == Material.AIR) continue;
            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;
            if (!meta.getPersistentDataContainer().has(ItemBuilder.ITEM_ID_KEY, PersistentDataType.STRING)) continue;

            Integer storedVersion = meta.getPersistentDataContainer().get(ItemBuilder.LORE_VERSION_KEY, PersistentDataType.INTEGER);
            if (storedVersion == null || storedVersion != currentVersion) {
                String itemId = meta.getPersistentDataContainer().get(ItemBuilder.ITEM_ID_KEY, PersistentDataType.STRING);
                if (itemId == null) continue;
                if (debug) {
                    getLogger().info("[LoreRefresh] 玩家=" + player.getName() + " 物品=" + itemId + " 槽位=" + slot + " 旧版本=" + storedVersion + " 新版本=" + currentVersion);
                }
                CustomItem customItem = ItemCoreAPI.getCustomItem(itemId);
                if (customItem == null) {
                    if (debug) getLogger().info("[LoreRefresh] 跳过: 找不到物品定义 " + itemId);
                    continue;
                }
                LoreManager lm = getLoreManager();
                if (lm == null) continue;
                List<String> newLore = lm.generateLore(customItem, item);
                List<Component> loreComponents = new ArrayList<>();
                for (String line : newLore) {
                    loreComponents.add(MessageUtil.colorize(line));
                }
                meta.lore(loreComponents);
                meta.getPersistentDataContainer().set(ItemBuilder.LORE_VERSION_KEY, PersistentDataType.INTEGER, currentVersion);
                item.setItemMeta(meta);
                refreshed++;
            }
        }
        if (debug && refreshed > 0) {
            getLogger().info("[LoreRefresh] 玩家=" + player.getName() + " 本次刷新 " + refreshed + " 个物品");
        }
    }
}
