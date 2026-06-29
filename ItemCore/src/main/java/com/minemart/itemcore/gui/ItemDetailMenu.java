package com.minemart.itemcore.gui;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.api.event.ItemObtainedEvent;
import com.minemart.itemcore.item.CustomItem;
import com.minemart.itemcore.item.attribute.AttributeContainer;
import com.minemart.itemcore.item.attribute.CustomAttribute;
import com.minemart.itemcore.item.attribute.ElementType;
import com.minemart.itemcore.utils.PermissionUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemDetailMenu extends BaseMenu {

    private final CustomItem item;

    public ItemDetailMenu(ItemCore plugin, Player player, CustomItem item) {
        super(plugin, player, "物品详情: " + item.getId(), GUI_SIZE);
        this.item = item;
    }

    @Override
    protected void createInventory() {
        inventory = createInventory(title, size);

        drawBorder(false, true, true, true);

        // 返回按钮
        setItem(0, createItem(Material.ARROW, "&c返回列表", null));

        ItemStack displayItem = item.toItemStack();
        if (displayItem != null) {
            setItem(13, displayItem);
        }

        ItemStack infoItem = createInfoItem();
        setItem(22, infoItem);

        if (hasObtainPermission()) {
            ItemStack obtainItem = createObtainItem();
            setItem(31, obtainItem);
        }
    }

    private ItemStack createInfoItem() {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("&7ID: &f" + item.getId());
        lore.add("&7材质: &f" + item.getMaterial().name());
        
        if (item.hasType()) {
            lore.add("&7分类: &f" + item.getType());
        }
        
        if (item.hasPermission()) {
            lore.add("&7权限: &f" + item.getPermission());
        }
        
        if (item.getMaxStack() > 0) {
            lore.add("&7最大堆叠: &f" + item.getMaxStack());
        }

        AttributeContainer attrs = item.getAttributes();
        if (!attrs.isEmpty()) {
            lore.add("");
            lore.add("&b=== 属性 ===");

            for (CustomAttribute attr : CustomAttribute.values()) {
                double value = attrs.getAttribute(attr);
                if (value != 0) {
                    String display = attr.isPercentage() 
                        ? (int) (value * 100) + "%" 
                        : String.valueOf(value);
                    lore.add("  &7" + attr.getDisplayName() + ": &f" + display);
                }
            }

            boolean hasElement = false;
            for (ElementType element : ItemCore.getElementRegistry().getAll()) {
                double mastery = attrs.getElementMastery(element);
                double resist = attrs.getElementResistance(element);
                if (mastery != 0 || resist != 0) {
                    if (!hasElement) {
                        lore.add("");
                        lore.add("&b=== 元素 ===");
                        hasElement = true;
                    }
                    if (mastery != 0) {
                        lore.add("  &7" + element.getDisplayName() + "精通: &f" + (int) (mastery * 100) + "%");
                    }
                    if (resist != 0) {
                        lore.add("  &7" + element.getDisplayName() + "抗性: &f" + (int) (resist * 100) + "%");
                    }
                }
            }
        }

        return createItem(Material.PAPER, "&e物品信息", lore);
    }

    private ItemStack createObtainItem() {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("&7点击获取此物品(1个)");
        lore.add("");
        lore.add("&e提示: 使用命令 /ic give <玩家> <物品> <数量>");
        lore.add("&e可以获取指定数量的物品");

        return createItem(Material.LIME_DYE, "&a获取物品", lore);
    }

    private boolean hasObtainPermission() {
        String permission = "itemcore.gui.obtain";
        if (item.hasPermission()) {
            permission = item.getPermission();
        }
        return PermissionUtil.hasPermission(player, permission);
    }

    @Override
    protected void onItemClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        if (slot < 0 || slot >= size) {
            return;
        }

        if (slot == 0) {
            if (item.hasType()) {
                new ItemListMenu(plugin, player, item.getType(), 1).open();
            } else {
                new CategoryMenu(plugin, player).open();
            }
            return;
        }

        if (slot == 8) {
            close();
            return;
        }

        if (slot == 31 && hasObtainPermission()) {
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