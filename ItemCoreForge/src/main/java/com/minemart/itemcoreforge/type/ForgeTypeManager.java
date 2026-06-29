package com.minemart.itemcoreforge.type;

import com.minemart.itemcoreforge.ItemCoreForge;
import com.minemart.itemcoreforge.core.Forge;
import com.minemart.itemcoreforge.type.craftingtable.CraftingTableHandler;
import com.minemart.itemcoreforge.type.furnace.FurnaceHandler;
import com.minemart.itemcoreforge.utils.DebugLogger;

import java.util.HashMap;
import java.util.Map;

public class ForgeTypeManager {

    private final ItemCoreForge plugin;
    private final Map<String, ForgeTypeHandler> handlers = new HashMap<>();

    public ForgeTypeManager(ItemCoreForge plugin) {
        this.plugin = plugin;
    }

    public void init() {
        registerHandler(new CraftingTableHandler());
        registerHandler(new FurnaceHandler());
        DebugLogger.info("TypeManager", "已初始化类型处理器");
    }

    public void registerHandler(ForgeTypeHandler handler) {
        String type = handler.getType().toLowerCase();
        handlers.put(type, handler);
        DebugLogger.info("TypeManager", "已注册类型处理器: " + type);
    }

    public void unregisterHandler(String type) {
        String lowerType = type.toLowerCase();
        ForgeTypeHandler handler = handlers.remove(lowerType);
        if (handler != null) {
            try {
                handler.onDisable();
                DebugLogger.info("TypeManager", "已注销类型处理器: " + lowerType);
            } catch (Exception e) {
                plugin.getLogger().severe("注销类型处理器失败: " + lowerType + " - " + e.getMessage());
            }
        }
    }

    public ForgeTypeHandler getHandler(String type) {
        if (type == null) {
            return null;
        }
        return handlers.get(type.toLowerCase());
    }

    public boolean hasHandler(String type) {
        return type != null && handlers.containsKey(type.toLowerCase());
    }

    public void enableAll() {
        for (Map.Entry<String, ForgeTypeHandler> entry : handlers.entrySet()) {
            try {
                entry.getValue().onEnable(plugin);
                DebugLogger.info("TypeManager", "已启用类型处理器: " + entry.getKey());
            } catch (Exception e) {
                plugin.getLogger().severe("启用类型处理器失败: " + entry.getKey() + " - " + e.getMessage());
            }
        }
    }

    public void disableAll() {
        for (Map.Entry<String, ForgeTypeHandler> entry : handlers.entrySet()) {
            try {
                entry.getValue().onDisable();
                DebugLogger.info("TypeManager", "已禁用类型处理器: " + entry.getKey());
            } catch (Exception e) {
                plugin.getLogger().severe("禁用类型处理器失败: " + entry.getKey() + " - " + e.getMessage());
            }
        }
    }

    public void reloadAll() {
        for (Map.Entry<String, ForgeTypeHandler> entry : handlers.entrySet()) {
            try {
                entry.getValue().onReload();
                DebugLogger.info("TypeManager", "已重载类型处理器: " + entry.getKey());
            } catch (Exception e) {
                plugin.getLogger().severe("重载类型处理器失败: " + entry.getKey() + " - " + e.getMessage());
            }
        }
    }

    public void registerForgeToHandler(Forge forge) {
        ForgeTypeHandler handler = getHandler(forge.getType());
        if (handler != null) {
            try {
                handler.registerForge(forge);
                DebugLogger.info("TypeManager", "已注册锻造台 " + forge.getForgeId() + " 到类型: " + forge.getType());
            } catch (Exception e) {
                plugin.getLogger().severe("注册锻造台到类型处理器失败: " + forge.getForgeId() + " - " + e.getMessage());
            }
        }
    }

    public void unregisterForgeFromHandler(Forge forge) {
        ForgeTypeHandler handler = getHandler(forge.getType());
        if (handler != null) {
            try {
                handler.unregisterForge(forge);
                DebugLogger.info("TypeManager", "已注销锻造台 " + forge.getForgeId() + " 从类型: " + forge.getType());
            } catch (Exception e) {
                plugin.getLogger().severe("注销锻造台从类型处理器失败: " + forge.getForgeId() + " - " + e.getMessage());
            }
        }
    }
}
