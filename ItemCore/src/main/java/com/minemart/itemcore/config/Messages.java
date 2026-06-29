package com.minemart.itemcore.config;

public final class Messages {

    private Messages() {}

    public static final String PREFIX = "&7[&bItemCore&7] ";

    public static final String PLUGIN_ENABLED = "&a插件已启用";
    public static final String PLUGIN_DISABLED = "&c插件已禁用";

    public static final String RELOAD_START = "&e正在重载配置...";
    public static final String RELOAD_SUCCESS = "&a配置重载成功";
    public static final String RELOAD_FAILED = "&c配置重载失败，请查看控制台";

    public static final String NO_PERMISSION = "&c你没有权限执行此命令";
    public static final String PLAYER_ONLY = "&c此命令只能由玩家执行";

    public static final String ITEM_NOT_FOUND = "&c未找到物品: &7{item}";
    public static final String ITEM_GIVEN = "&a已给予 &7{player} &a物品: &7{item} &ax{amount}";
    public static final String ITEM_OBTAINED = "&a你获得了物品: &7{item}";

    public static final String PLAYER_NOT_FOUND = "&c玩家未找到: &7{player}";
    public static final String INVALID_AMOUNT = "&c无效的数量: &7{amount}";

    public static final String LIST_HEADER = "&e=== 物品列表 === (共 {count} 个)";
    public static final String LIST_ITEM = "&7- &f{item}";

    public static final String GUI_PREV_PAGE = "&a上一页";
    public static final String GUI_NEXT_PAGE = "&a下一页";
    public static final String GUI_BACK = "&c返回";
    public static final String GUI_CLOSE = "&c关闭";

    public static final String INFO_HEADER = "&e=== 物品信息: &f{item} &e===";
    public static final String INFO_MATERIAL = "&7材质: &f{material}";
    public static final String INFO_DISPLAY_NAME = "&7显示名称: &f{name}";
    public static final String INFO_TYPE = "&7分类: &f{type}";
    public static final String INFO_PERMISSION = "&7权限: &f{permission}";

    public static final String HELP_HEADER = "&e=== ItemCore 帮助 === (page {page}/{total})";
    public static final String HELP_ITEM = "&b/{command} &7{usage} - &f{desc}";
}
