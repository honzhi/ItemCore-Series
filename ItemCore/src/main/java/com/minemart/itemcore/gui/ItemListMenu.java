package com.minemart.itemcore.gui;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.api.ItemCoreAPI;
import com.minemart.itemcore.api.event.ItemObtainedEvent;
import com.minemart.itemcore.item.CustomItem;
import com.minemart.itemcore.utils.ItemBuilder;
import com.minemart.itemcore.utils.MessageUtil;
import com.minemart.itemcore.utils.PermissionUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemListMenu extends BaseMenu {

    private final String categoryId;
    private final List<CustomItem> items;
    private int currentPage;
    private final int totalPages;

    public ItemListMenu(ItemCore plugin, Player player, String categoryId, int page) {
        super(plugin, player, plugin.getConfigManager().getGuiName(), GUI_SIZE);
        this.categoryId = categoryId;
        this.items = loadItems();
        this.totalPages = Math.max(1, (int) Math.ceil((double) items.size() / getMaxItemsPerPage()));
        this.currentPage = Math.max(1, Math.min(page, totalPages));
    }

    private List<CustomItem> loadItems() {
        if (categoryId != null) {
            return new ArrayList<>(ItemCoreAPI.getItemsByCategory(categoryId));
        }
        return new ArrayList<>(ItemCoreAPI.getItems());
    }

    @Override
    protected void createInventory() {
        inventory = createInventory(title + " &7(第" + currentPage + "/" + totalPages + " 页)", size);

        drawBorder(false, false, false, false);

        // 导航按钮放在边框位置
        setItem(0, createItem(Material.ARROW, "&c返回", createLore("&7返回上级菜单")));
        setItem(8, createItem(Material.BARRIER, "&c关闭", null));

        if (currentPage > 1) {
            setItem(45, createItem(Material.FEATHER, "&a上一页", createLore("&7第 " + (currentPage - 1) + " / " + totalPages + " 页")));
        }
        if (currentPage < totalPages) {
            setItem(53, createItem(Material.FEATHER, "&a下一页", createLore("&7第 " + (currentPage + 1) + " / " + totalPages + " 页")));
        }

        // 放置物品
        int start = (currentPage - 1) * getMaxItemsPerPage();
        int end = Math.min(start + getMaxItemsPerPage(), items.size());

        for (int i = start; i < end; i++) {
            CustomItem item = items.get(i);
            int slot = getContentSlot(i - start);
            ItemStack itemStack = ItemBuilder.build(item, 1);
            setItem(slot, itemStack);
        }
    }

    @Override
    protected void onItemClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        if (slot < 0 || slot >= size) {
            return;
        }

        // 返回
        if (slot == 0) {
            new CategoryMenu(plugin, player).open();
            return;
        }

        // 关闭
        if (slot == 8) {
            close();
            return;
        }

        // 上一页
        if (slot == 45 && currentPage > 1) {
            new ItemListMenu(plugin, player, categoryId, currentPage - 1).open();
            return;
        }

        // 下一页
        if (slot == 53 && currentPage < totalPages) {
            new ItemListMenu(plugin, player, categoryId, currentPage + 1).open();
            return;
        }

        // 点击边框其他位置忽略
        int row = slot / 9;
        int col = slot % 9;
        if (row == 0 || row == ROWS - 1 || col == 0 || col == 8) {
            return;
        }

        // 计算是第几个物品
        int itemIndex = (row - 1) * getContentSlotsPerRow() + (col - 1);
        int globalIndex = (currentPage - 1) * getMaxItemsPerPage() + itemIndex;

        if (globalIndex >= 0 && globalIndex < items.size()) {
            CustomItem item = items.get(globalIndex);
            if (event.isShiftClick()) {
                new ItemDetailMenu(plugin, player, item).open();
            } else {
                if (item.hasPermission() && !PermissionUtil.hasPermission(player, item.getPermission())) {
                    player.sendMessage(plugin.getMessagesManager().getNoObtainPermission());
                    return;
                }
                if (!PermissionUtil.hasPermission(player, "itemcore.gui.obtain")) {
                    player.sendMessage(plugin.getMessagesManager().getNoGuiObtainPermission());
                    return;
                }
                boolean success = plugin.getCoreManager().giveItem(
                    player,
                    item.getId(),
                    1,
                    ItemObtainedEvent.ObtainSource.GUI
                );
                if (success) {
                    player.sendMessage(plugin.getMessagesManager().getItemObtained(item.getId()));
                }
            }
        }
    }
}