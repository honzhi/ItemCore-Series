package com.minemart.itemcoreforge.gui;

import com.minemart.itemcoreforge.ItemCoreForge;
import com.minemart.itemcoreforge.core.Forge;
import com.minemart.itemcoreforge.utils.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class RecipeSelectGUI extends BaseMenu {

    private final Forge forge;
    private int currentPage = 0;
    private final int recipesPerPage = 28;
    private final List<String> recipeIds;

    public RecipeSelectGUI(ItemCoreForge plugin, Player player, Forge forge) {
        super(plugin, player, forge.getDisplayName() + " - 选择配方", 6);
        this.forge = forge;
        this.recipeIds = new ArrayList<>(forge.getRecipes().keySet());
        
        renderRecipes();
    }

    private void renderRecipes() {
        int startIndex = currentPage * recipesPerPage;
        int endIndex = Math.min(startIndex + recipesPerPage, recipeIds.size());
        
        int slot = 10;
        for (int i = startIndex; i < endIndex; i++) {
            String recipeId = recipeIds.get(i);
            Forge.Recipe recipe = forge.getRecipe(recipeId);
            
            if (recipe != null) {
                renderRecipeItem(slot, recipeId, recipe);
            }
            
            slot++;
            if ((slot % 9) == 8) {
                slot += 2;
            }
            if (slot >= 44) {
                break;
            }
        }
        
        renderNavigation();
    }

    private void renderRecipeItem(int slot, String recipeId, Forge.Recipe recipe) {
        com.minemart.itemcoreforge.utils.ItemReference itemRef = 
            new com.minemart.itemcoreforge.utils.ItemReference();
        itemRef.setSource(recipe.getOutput().getSource());
        itemRef.setCategory(recipe.getOutput().getCategory());
        itemRef.setId(recipe.getOutput().getId());
        itemRef.setAmount(recipe.getOutput().getAmount());
        
        ItemStack item = itemRef.toItemStack();
        if (item == null || item.getType() == Material.AIR) {
            item = new ItemStack(Material.BARRIER);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.displayName(MessageUtil.toComponent("&c无效物品: " + recipe.getOutput().getId()));
                item.setItemMeta(meta);
            }
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            setItem(slot, item);
            return;
        }
        
        meta.displayName(MessageUtil.toComponent("&e" + recipeId));
        
        List<Component> lore = new ArrayList<>();
        lore.add(MessageUtil.toComponent("&7制作时间: &e" + recipe.getCraftTime() + "秒"));
        lore.add(MessageUtil.toComponent("&7材料数量: &e" + recipe.getMaterials().size()));
        
        if (!recipe.getConditions().isEmpty()) {
            lore.add(MessageUtil.toComponent("&7条件:"));
            for (Forge.Condition condition : recipe.getConditions()) {
                lore.add(MessageUtil.toComponent("  &8- &7" + getConditionText(condition)));
            }
        }
        
        lore.add(MessageUtil.toComponent(""));
        lore.add(MessageUtil.toComponent("&a点击选择此配方"));
        
        meta.lore(lore);
        item.setItemMeta(meta);
        
        setItem(slot, item, event -> {
            openForgeGUI(recipeId);
        });
    }

    private void renderNavigation() {
        if (currentPage > 0) {
            ItemStack prevItem = new ItemStack(Material.ARROW);
            ItemMeta meta = prevItem.getItemMeta();
            meta.displayName(MessageUtil.toComponent("&a上一页"));
            prevItem.setItemMeta(meta);
            setItem(45, prevItem, event -> {
                currentPage--;
                renderRecipes();
            });
        }
        
        ItemStack queueItem = new ItemStack(Material.CHEST);
        ItemMeta queueMeta = queueItem.getItemMeta();
        queueMeta.displayName(MessageUtil.toComponent("&a查看制作队列"));
        List<Component> queueLore = new ArrayList<>();
        queueLore.add(MessageUtil.toComponent("&7点击查看当前制作队列"));
        queueMeta.lore(queueLore);
        queueItem.setItemMeta(queueMeta);
        setItem(41, queueItem, event -> {
            close();
            QueueDisplayGUI queueGUI = new QueueDisplayGUI(plugin, player, forge);
            queueGUI.open();
        });
        
        ItemStack closeItem = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeItem.getItemMeta();
        closeMeta.displayName(MessageUtil.toComponent("&c关闭"));
        closeItem.setItemMeta(closeMeta);
        setItem(49, closeItem, event -> close());
        
        if ((currentPage + 1) * recipesPerPage < recipeIds.size()) {
            ItemStack nextItem = new ItemStack(Material.ARROW);
            ItemMeta meta = nextItem.getItemMeta();
            meta.displayName(MessageUtil.toComponent("&a下一页"));
            nextItem.setItemMeta(meta);
            setItem(53, nextItem, event -> {
                currentPage++;
                renderRecipes();
            });
        }
    }

    private void openForgeGUI(String recipeId) {
        CustomForgeGUI forgeGUI = new CustomForgeGUI(plugin, player, forge);
        forgeGUI.selectRecipe(recipeId);
        forgeGUI.open();
    }

    private String getConditionText(Forge.Condition condition) {
        return switch (condition.getType().toLowerCase()) {
            case "level" -> "等级 " + condition.getValue();
            case "permission" -> "权限 " + condition.getNode();
            case "money" -> "金币 " + condition.getAmount();
            default -> condition.getType();
        };
    }
}
