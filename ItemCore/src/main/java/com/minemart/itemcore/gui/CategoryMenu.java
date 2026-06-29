package com.minemart.itemcore.gui;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.api.ItemCoreAPI;
import com.minemart.itemcore.item.ItemCategory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CategoryMenu extends BaseMenu {

    private final List<ItemCategory> categories;

    public CategoryMenu(ItemCore plugin, Player player) {
        super(plugin, player, plugin.getConfigManager().getGuiName(), GUI_SIZE);
        this.categories = new ArrayList<>(ItemCoreAPI.getCategories());
    }

    @Override
    protected void createInventory() {
        inventory = createInventory(title, size);

        drawBorder(false, true, false, false);

        // "所有物品"放在右上角
        setItem(8, createAllItemsIcon());

        // 分类图标放在中间内容区
        int slotIndex = 0;
        for (int i = 0; i < categories.size(); i++) {
            ItemCategory category = categories.get(i);
            int slot = getContentSlot(slotIndex);

            ItemStack icon = createCategoryIcon(category);
            setItem(slot, icon);
            slotIndex++;
        }
    }

    private ItemStack createCategoryIcon(ItemCategory category) {
        Material material = category.getIcon();
        if (material == null) {
            material = Material.CHEST;
        }

        List<String> lore = new ArrayList<>();
        lore.add("&7点击查看此分类下的物品");
        lore.add("");
        lore.add("&e物品数: &f" + ItemCoreAPI.getItemsByCategory(category.getId()).size());

        return createItem(material, category.getDisplayName(), lore);
    }

    private ItemStack createAllItemsIcon() {
        List<String> lore = new ArrayList<>();
        lore.add("&7点击查看所有物品");
        lore.add("");
        lore.add("&e物品数: &f" + ItemCoreAPI.getItemIds().size());
        return createItem(Material.BOOKSHELF, "&b所有物品", lore);
    }

    @Override
    protected void onItemClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        if (slot < 0 || slot >= size) {
            return;
        }

        // 所有物品
        if (slot == 8) {
            new ItemListMenu(plugin, player, null, 1).open();
            return;
        }

        // 检查是否点击了边框
        int row = slot / 9;
        int col = slot % 9;
        if (row == 0 || row == ROWS - 1 || col == 0 || col == 8) {
            return;
        }

        // 计算是第几个物品
        int itemIndex = (row - 1) * getContentSlotsPerRow() + (col - 1);

        if (itemIndex >= 0 && itemIndex < categories.size()) {
            ItemCategory category = categories.get(itemIndex);
            new ItemListMenu(plugin, player, category.getId(), 1).open();
        }
    }
}