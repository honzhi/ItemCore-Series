package com.minemart.itemcoreforge.recipe;

import com.minemart.itemcoreforge.ItemCoreForge;
import com.minemart.itemcoreforge.core.Forge;
import com.minemart.itemcoreforge.utils.DebugLogger;
import com.minemart.itemcoreforge.utils.MaterialChecker;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.HashMap;
import java.util.Map;

public class FurnaceRecipeManager {

    private final ItemCoreForge plugin;
    private final Map<String, Forge> registeredRecipes = new HashMap<>();
    private MaterialChecker materialChecker;

    public FurnaceRecipeManager(ItemCoreForge plugin) {
        this.plugin = plugin;
    }

    public void setMaterialChecker(MaterialChecker materialChecker) {
        this.materialChecker = materialChecker;
    }

    public void registerForgeRecipes(Forge forge) {
        if (!forge.isFurnaceType()) {
            return;
        }

        for (Map.Entry<String, Forge.Recipe> entry : forge.getRecipes().entrySet()) {
            Forge.Recipe recipe = entry.getValue();
            registerRecipe(forge, recipe);
            registeredRecipes.put(recipe.getRecipeId(), forge);
        }

        plugin.getLogger().info("已注册熔炉配方: " + forge.getForgeId() + " (" + forge.getRecipes().size() + " 个配方)");
    }

    public void unregisterAll() {
        for (Forge forge : registeredRecipes.values()) {
            unregisterForge(forge);
        }
        registeredRecipes.clear();
    }

    private void registerRecipe(Forge forge, Forge.Recipe recipe) {
        if (recipe.getInput() == null || recipe.getOutput() == null) {
            plugin.getLogger().warning("熔炉配方缺少 input 或 output: " + recipe.getRecipeId());
            return;
        }

        NamespacedKey key = new NamespacedKey(plugin, forge.getForgeId() + "/" + recipe.getRecipeId());

        ItemStack result = materialChecker.resolveItem(recipe.getOutput());
        if (result == null) {
            plugin.getLogger().warning("无法解析输出物品: " + recipe.getRecipeId());
            return;
        }

        DebugLogger.info("Furnace", "注册配方: " + key);

        RecipeChoice choice = createRecipeChoice(recipe.getInput());
        if (choice == null) {
            plugin.getLogger().warning("无法解析输入物品: " + recipe.getInput().getId());
            return;
        }

        FurnaceRecipe furnaceRecipe = new FurnaceRecipe(key, result, choice, 0, recipe.getCookTime());
        Bukkit.addRecipe(furnaceRecipe);

        DebugLogger.info("Furnace", "  - 输入: " + recipe.getInput().getId() + " -> 输出: " + recipe.getOutput().getId());
    }

    private RecipeChoice createRecipeChoice(Forge.ItemReference material) {
        if (material.isItemCore()) {
            ItemStack item = materialChecker.resolveItem(material);
            if (item != null) {
                return new RecipeChoice.ExactChoice(item);
            }
            return null;
        } else {
            try {
                Material mat = Material.matchMaterial(material.getId().toUpperCase());
                if (mat != null) {
                    return new RecipeChoice.MaterialChoice(mat);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("无效的材料: " + material.getId());
            }
            return null;
        }
    }

    private void unregisterForge(Forge forge) {
        for (Forge.Recipe recipe : forge.getRecipes().values()) {
            NamespacedKey key = new NamespacedKey(plugin, forge.getForgeId() + "/" + recipe.getRecipeId());
            Bukkit.removeRecipe(key);
        }
    }
}
