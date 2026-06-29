package com.minemart.itemcore.config;

public final class ConfigKeys {

    private ConfigKeys() {}

    public static final String LANGUAGE = "language";
    public static final String DEFAULT_LANGUAGE = "zh-CN";

    public static final String DEBUG_MODE = "debug-mode";

    public static final String ITEMS_FOLDER = "items-folder";
    public static final String DEFAULT_ITEMS_FOLDER = "items";

    public static final String CATEGORIES_FILE = "categories-file";
    public static final String DEFAULT_CATEGORIES_FILE = "categories.yml";

    public static final String GUI_NAME = "gui.name";
    public static final String DEFAULT_GUI_NAME = "物品库";

    public static final String GUI_SIZE = "gui.size";
    public static final int DEFAULT_GUI_SIZE = 54;

    public static final String GUI_ITEMS_PER_PAGE = "gui.items-per-page";
    public static final int DEFAULT_GUI_ITEMS_PER_PAGE = 45;

    public static final String PERMISSION_USE = "itemcore.use";
    public static final String PERMISSION_LIST = "itemcore.list";
    public static final String PERMISSION_INFO = "itemcore.info";
    public static final String PERMISSION_GIVE = "itemcore.give";
    public static final String PERMISSION_RELOAD = "itemcore.reload";
    public static final String PERMISSION_GUI_OBTAIN = "itemcore.gui.obtain";
    public static final String PERMISSION_ADMIN = "itemcore.admin";
    // ========================================
    // Lore 刷新配置
    // ========================================

    public static final String LORE_REFRESH_ENABLED = "lore-refresh.enabled";
    public static final boolean DEFAULT_LORE_REFRESH_ENABLED = false;

    public static final String LORE_REFRESH_INTERVAL = "lore-refresh.interval";
    public static final int DEFAULT_LORE_REFRESH_INTERVAL = 100;

}
