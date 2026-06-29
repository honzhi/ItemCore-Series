package com.minemart.itemcoreforge.gui;

import com.minemart.itemcoreforge.ItemCoreForge;
import com.minemart.itemcoreforge.core.CraftingQueue;
import com.minemart.itemcoreforge.core.CraftResult;
import com.minemart.itemcoreforge.core.Forge;
import com.minemart.itemcoreforge.core.QueueItem;
import com.minemart.itemcoreforge.utils.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class QueueDisplayGUI extends BaseMenu implements QueueUpdateable {

    private final Forge forge;
    private int updateTaskId = -1;
    private long lastQueueClickTime = 0;

    public QueueDisplayGUI(ItemCoreForge plugin, Player player, Forge forge) {
        super(plugin, player, "制作队列 - " + forge.getDisplayName(), 6);
        this.forge = forge;
        render();
        startUpdateTask();
    }

    private void render() {
        CraftingQueue queue = plugin.getCraftingQueueManager().getQueue(player, forge.getForgeId());
        
        inventory.clear();
        
        for (int slotIndex = 0; slotIndex < 53; slotIndex++) {
            QueueItem item = queue.getItemAtSlot(slotIndex);
            
            if (item != null) {
                Forge.Recipe recipe = forge.getRecipe(item.getRecipeId());
                if (recipe != null) {
                    ItemStack guiItem = createQueueItem(recipe, item, slotIndex);
                    final QueueItem finalItem = item;
                    setItem(slotIndex, guiItem, event -> handleQueueClick(finalItem));
                }
            } else {
                ItemStack guiItem = createEmptySlot(slotIndex);
                inventory.setItem(slotIndex, guiItem);
            }
        }
        
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta meta = backButton.getItemMeta();
        meta.displayName(MessageUtil.toComponent("&a返回配方列表"));
        List<Component> lore = new ArrayList<>();
        lore.add(MessageUtil.toComponent("&7点击返回"));
        meta.lore(lore);
        backButton.setItemMeta(meta);
        
        setItem(53, backButton, event -> {
            close();
            RecipeSelectGUI recipeGUI = new RecipeSelectGUI(plugin, player, forge);
            recipeGUI.open();
        });
    }

    private ItemStack createQueueItem(Forge.Recipe recipe, QueueItem item, int slotIndex) {
        Forge.ItemReference outputRef = recipe.getOutput();
        ItemStack guiItem = convertToItemStack(outputRef);
        
        ItemMeta meta = guiItem.getItemMeta();
        
        List<Component> lore = new ArrayList<>();
        lore.add(MessageUtil.toComponent("&7物品: &f" + recipe.getRecipeId()));
        lore.add(MessageUtil.toComponent("&7数量: &f" + item.getAmount()));
        
        if (item.isReady()) {
            meta.displayName(MessageUtil.toComponent("&6已完成"));
            lore.add(MessageUtil.toComponent("&a点击领取"));
        } else if (isFirstActiveTask(slotIndex)) {
            meta.displayName(MessageUtil.toComponent("&a正在制作"));
            double remainingSeconds = item.getRemainingSeconds();
            lore.add(MessageUtil.toComponent("&7剩余时间: &e" + (int) Math.ceil(remainingSeconds) + "秒"));
            lore.add(MessageUtil.toComponent("&c点击取消"));
        } else {
            meta.displayName(MessageUtil.toComponent("&e等待中"));
            lore.add(MessageUtil.toComponent("&c点击取消"));
        }
        
        lore.add(MessageUtil.toComponent(""));
        lore.add(MessageUtil.toComponent("&7槽位: &f" + (slotIndex + 1)));
        
        meta.lore(lore);
        guiItem.setItemMeta(meta);
        return guiItem;
    }

    private void handleQueueClick(QueueItem item) {
        long now = System.currentTimeMillis();
        if (now - lastQueueClickTime >= plugin.getConfigManager().getCraftClickCooldown()) {
            lastQueueClickTime = now;
            if (item.isReady()) {
                claimItem(item);
            } else {
                cancelTask(item);
            }
        }
    }

    private boolean isFirstActiveTask(int slotIndex) {
        CraftingQueue queue = plugin.getCraftingQueueManager().getQueue(player, forge.getForgeId());
        List<QueueItem> items = queue.getItems();
        
        for (int i = 0; i < slotIndex; i++) {
            if (i < items.size()) {
                QueueItem prevItem = items.get(i);
                if (!prevItem.isCompleted()) {
                    return false;
                }
            }
        }
        
        return true;
    }

    private void claimItem(QueueItem item) {
        CraftResult result = plugin.getCraftingQueueManager().claimItem(
            player,
            forge.getForgeId(),
            item.getTaskId()
        );
        
        if (result.isSuccess()) {
            render();
        } else {
            MessageUtil.sendWithPrefix(player, "&c" + result.getMessage());
        }
    }

    private void cancelTask(QueueItem item) {
        CraftResult result = plugin.getCraftingQueueManager().cancelCraft(
            player,
            forge.getForgeId(),
            item.getTaskId()
        );
        
        if (result.isSuccess()) {
            render();
        } else {
            MessageUtil.sendWithPrefix(player, "&c" + result.getMessage());
        }
    }

    private ItemStack createEmptySlot(int index) {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtil.toComponent("&7空槽位"));
        List<Component> lore = new ArrayList<>();
        lore.add(MessageUtil.toComponent("&7槽位 " + (index + 1)));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack convertToItemStack(Forge.ItemReference ref) {
        if (ref == null) {
            return new ItemStack(Material.AIR);
        }
        
        if ("vanilla".equalsIgnoreCase(ref.getSource())) {
            try {
                Material material = Material.valueOf(ref.getId().toUpperCase());
                return new ItemStack(material, ref.getAmount());
            } catch (IllegalArgumentException e) {
                return new ItemStack(Material.BARRIER);
            }
        } else {
            try {
                Class<?> apiClass = Class.forName("com.minemart.itemcore.api.ItemCoreAPI");
                Object api = apiClass.getMethod("getInstance").invoke(null);
                Object itemStack = apiClass.getMethod("getItemStack", String.class, int.class)
                    .invoke(api, ref.getId(), ref.getAmount());
                return (ItemStack) itemStack;
            } catch (Exception e) {
                return new ItemStack(Material.BARRIER);
            }
        }
    }

    private void startUpdateTask() {
        if (updateTaskId != -1) {
            return;
        }
        
        updateTaskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (player.isOnline() && inventory.getViewers().contains(player)) {
                render();
            } else {
                stopUpdateTask();
            }
        }, 20L, 20L);
    }

    private void stopUpdateTask() {
        if (updateTaskId != -1) {
            plugin.getServer().getScheduler().cancelTask(updateTaskId);
            updateTaskId = -1;
        }
    }

    @Override
    public void updateQueueDisplay() {
        render();
    }

    @Override
    public void close() {
        stopUpdateTask();
        super.close();
    }
}