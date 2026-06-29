package com.minemart.itemcoreforge.type.furnace;

import com.minemart.itemcoreforge.ItemCoreForge;
import com.minemart.itemcoreforge.core.Forge;
import com.minemart.itemcoreforge.recipe.FurnaceRecipeManager;
import com.minemart.itemcoreforge.type.ForgeTypeHandler;
import com.minemart.itemcoreforge.utils.DebugLogger;

public class FurnaceHandler implements ForgeTypeHandler {

    private ItemCoreForge plugin;
    private FurnaceRecipeManager recipeManager;

    @Override
    public String getType() {
        return "furnace";
    }

    @Override
    public void onEnable(ItemCoreForge plugin) {
        this.plugin = plugin;
        this.recipeManager = new FurnaceRecipeManager(plugin);
        this.recipeManager.setMaterialChecker(plugin.getMaterialChecker());
        DebugLogger.info("Furnace", "Furnace 类型处理器已启用");
    }

    @Override
    public void onDisable() {
        if (recipeManager != null) {
            recipeManager.unregisterAll();
            DebugLogger.info("Furnace", "Furnace 类型处理器已禁用");
        }
    }

    @Override
    public void onReload() {
        if (recipeManager != null) {
            recipeManager.unregisterAll();
            DebugLogger.info("Furnace", "Furnace 类型处理器已重载（配方已清除）");
        }
    }

    @Override
    public void registerForge(Forge forge) {
        if (recipeManager != null) {
            recipeManager.registerForgeRecipes(forge);
            DebugLogger.info("Furnace", "已注册锻造台到 Furnace: " + forge.getForgeId());
        }
    }

    @Override
    public void unregisterForge(Forge forge) {
        if (recipeManager != null) {
            DebugLogger.info("Furnace", "已注销锻造台从 Furnace: " + forge.getForgeId());
        }
    }
}
