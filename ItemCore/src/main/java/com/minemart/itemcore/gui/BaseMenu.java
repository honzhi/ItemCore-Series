package com.minemart.itemcore.gui;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.utils.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class BaseMenu {

    protected static final int GUI_SIZE = 54;
    protected static final int ROWS = 6;

    protected final ItemCore plugin;
    protected final Player player;
    protected Inventory inventory;
    protected String title;
    protected int size;
    protected boolean cancelClicks = true;
    protected Consumer<InventoryCloseEvent> onClose = e -> {};

    protected BaseMenu(ItemCore plugin, Player player, String title, int size) {
        this.plugin = plugin;
        this.player = player;
        this.title = title;
        this.size = size;
    }

    protected abstract void createInventory();

    protected abstract void onItemClick(InventoryClickEvent event);

    public void open() {
        createInventory();
        player.openInventory(inventory);
        plugin.getGuiListener().registerMenu(this);
    }

    public void close() {
        player.closeInventory();
    }

    public void refresh() {
        inventory.clear();
        createInventory();
        player.updateInventory();
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Inventory createInventory(String title, int size) {
        Component component = MessageUtil.colorize(title);
        return Bukkit.createInventory(null, size, component);
    }

    protected void setItem(int slot, ItemStack item) {
        inventory.setItem(slot, item);
    }

    protected ItemStack createItem(Material material, String displayName, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (displayName != null) {
                meta.displayName(MessageUtil.colorize(displayName));
            }
            if (lore != null && !lore.isEmpty()) {
                List<Component> coloredLore = new ArrayList<>();
                for (String line : lore) {
                    coloredLore.add(MessageUtil.colorize(line));
                }
                meta.lore(coloredLore);
            }
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        return item;
    }

    protected List<String> createLore(String... lines) {
        List<String> lore = new ArrayList<>();
        for (String line : lines) {
            lore.add(line);
        }
        return lore;
    }

    /**
     * 四周放置玻璃板边框
     * @param hasBack 是否在左上角留空放返回按钮
     * @param hasClose 是否在右上角留空放关闭按钮
     * @param hasPrev 是否在左下角留空放上一页按钮
     * @param hasNext 是否在右下角留空放下一页按钮
     */
    protected void drawBorder(boolean hasBack, boolean hasClose, boolean hasPrev, boolean hasNext) {
        ItemStack glass = createItem(Material.GRAY_STAINED_GLASS_PANE, " ", null);
        int rows = size / 9;

        // 顶行
        for (int col = 0; col < 9; col++) {
            if (hasBack && col == 0) continue;
            if (hasClose && col == 8) continue;
            setItem(col, glass);
        }

        // 底行
        int bottomStart = (rows - 1) * 9;
        for (int col = 0; col < 9; col++) {
            if (hasPrev && col == 0) continue;
            if (hasNext && col == 8) continue;
            setItem(bottomStart + col, glass);
        }

        // 左列（跳过顶行和底行）
        for (int row = 1; row < rows - 1; row++) {
            setItem(row * 9, glass);
        }

        // 右列（跳过顶行和底行）
        for (int row = 1; row < rows - 1; row++) {
            setItem(row * 9 + 8, glass);
        }
    }

    /**
     * 获取物品区的起始 slot（第一行第一个可用的 slot，跳过边框）
     */
    protected int getContentStartSlot() {
        return 10; // row 1, col 1 (0-based)
    }

    /**
     * 每行物品数（扣除左右边框）
     */
    protected int getContentSlotsPerRow() {
        return 7;
    }

    /**
     * 物品区的总行数（扣除顶行和底行边框）
     */
    protected int getContentRows() {
        return 4;
    }

    /**
     * 计算物品在 GUI 中的 slot
     */
    protected int getContentSlot(int index) {
        int row = index / getContentSlotsPerRow();
        int col = index % getContentSlotsPerRow();
        return (row + 1) * 9 + col + 1;
    }

    /**
     * 最多可放的物品数
     */
    protected int getMaxItemsPerPage() {
        return getContentSlotsPerRow() * getContentRows();
    }

    public void setOnClose(Consumer<InventoryCloseEvent> onClose) {
        this.onClose = onClose;
    }

    public void handleClose(InventoryCloseEvent event) {
        onClose.accept(event);
    }

    public boolean shouldCancelClicks() {
        return cancelClicks;
    }
}