package com.minemart.itemcoreforge.type.craftingtable;

import com.minemart.itemcoreforge.ItemCoreForge;
import com.minemart.itemcoreforge.core.Forge;
import com.minemart.itemcoreforge.recipe.CraftingTableRecipeManager;
import com.minemart.itemcoreforge.type.ForgeTypeHandler;
import com.minemart.itemcoreforge.utils.DebugLogger;

public class CraftingTableHandler implements ForgeTypeHandler {

    private ItemCoreForge plugin;
    private CraftingTableRecipeManager recipeManager;

    @Override
    public String getType() {
        return "crafting_table";
    }

    @Override
    public void onEnable(ItemCoreForge plugin) {
        this.plugin = plugin;
        this.recipeManager = new CraftingTableRecipeManager(plugin);
        this.recipeManager.setMaterialChecker(plugin.getMaterialChecker());
        this.recipeManager.registerEvents();
        DebugLogger.info("CraftingTable", "CraftingTable 类型处理器已启用");
    }

    @Override
    public void onDisable() {
        if (recipeManager != null) {
            recipeManager.unregisterAll();
            DebugLogger.info("CraftingTable", "CraftingTable 类型处理器已禁用");
        }
    }

    @Override
    public void onReload() {
        if (recipeManager != null) {
            recipeManager.unregisterAll();
            DebugLogger.info("CraftingTable", "CraftingTable 类型处理器已重载（配方已清除）");
        }
    }

    @Override
    public void registerForge(Forge forge) {
        if (recipeManager != null) {
            recipeManager.registerForgeRecipes(forge);
            DebugLogger.info("CraftingTable", "已注册锻造台到 CraftingTable: " + forge.getForgeId());
        }
    }

    @Override
    public void unregisterForge(Forge forge) {
        if (recipeManager != null) {
            DebugLogger.info("CraftingTable", "已注销锻造台从 CraftingTable: " + forge.getForgeId());
        }
    }
}
