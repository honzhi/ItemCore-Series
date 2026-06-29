package com.minemart.itemcoreforge.gui;

import com.minemart.itemcoreforge.ItemCoreForge;
import com.minemart.itemcoreforge.config.LayoutLoader;
import com.minemart.itemcoreforge.core.CraftingQueue;
import com.minemart.itemcoreforge.core.CraftResult;
import com.minemart.itemcoreforge.core.Forge;
import com.minemart.itemcoreforge.core.QueueItem;
import com.minemart.itemcoreforge.utils.ConditionChecker;
import com.minemart.itemcoreforge.utils.MaterialChecker;
import com.minemart.itemcoreforge.utils.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CustomForgeGUI extends BaseMenu implements QueueUpdateable {

    private final Forge forge;
    private final LayoutLoader.Layout layout;
    private Forge.Recipe selectedRecipe;
    private final Map<Integer, String> slotFunctions = new HashMap<>();
    private final List<Integer> queueSlots = new ArrayList<>();
    private int updateTaskId = -1;
    private long lastCraftClickTime = 0;
    private long lastQueueClickTime = 0;

    public CustomForgeGUI(ItemCoreForge plugin, Player player, Forge forge) {
        super(plugin, player, forge.getDisplayName(), 6);
        this.forge = forge;
        String layoutName = forge.getLayoutFile().replace(".yml", "");
        this.layout = plugin.getLayoutLoader().getLayout(layoutName);
        
        if (layout != null) {
            renderLayout();
            updateQueueDisplay();
        }
        
        startUpdateTask();
    }

    private void startUpdateTask() {
        if (updateTaskId != -1) {
            return;
        }
        
        updateTaskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (player.isOnline() && inventory.getViewers().contains(player)) {
                updateQueueDisplay();
            } else {
                stopUpdateTask();
            }
        }, 0L, 20L);
    }

    private void stopUpdateTask() {
        if (updateTaskId != -1) {
            plugin.getServer().getScheduler().cancelTask(updateTaskId);
            updateTaskId = -1;
        }
    }

    @Override
    public void updateQueueDisplay() {
        if (layout == null) return;
        
        CraftingQueue queue = plugin.getCraftingQueueManager().getQueue(player, forge.getForgeId());
        List<QueueItem> items = queue.getItems();
        
        for (int i = 0; i < queueSlots.size(); i++) {
            int slot = queueSlots.get(i);
            LayoutLoader.SlotConfig config = getSlotConfigForSlot(slot);
            if (config != null) {
                QueueItem item = i < items.size() ? items.get(i) : null;
                renderQueueDisplay(slot, config, item, i);
            }
        }
    }

    private LayoutLoader.SlotConfig getSlotConfigForSlot(int slot) {
        if (layout == null) return null;
        int row = slot / 9;
        int col = slot % 9;
        char c = layout.getSlot(row, col);
        return layout.getSlotConfig(c);
    }

    @Override
    public void close() {
        stopUpdateTask();
        super.close();
    }

    private void renderLayout() {
        List<Integer> tempQueueSlots = new ArrayList<>();
        
        for (int row = 0; row < layout.getRows(); row++) {
            for (int col = 0; col < 9; col++) {
                int slot = row * 9 + col;
                char c = layout.getSlot(row, col);
                LayoutLoader.SlotConfig config = layout.getSlotConfig(c);
                
                if (config != null && "queue_display".equals(config.getFunction())) {
                    tempQueueSlots.add(slot);
                }
            }
        }
        
        tempQueueSlots.sort(Integer::compareTo);
        queueSlots.clear();
        queueSlots.addAll(tempQueueSlots);
        
        for (int row = 0; row < layout.getRows(); row++) {
            for (int col = 0; col < 9; col++) {
                int slot = row * 9 + col;
                char c = layout.getSlot(row, col);
                LayoutLoader.SlotConfig config = layout.getSlotConfig(c);
                
                if (config != null) {
                    renderSlot(slot, config);
                }
            }
        }
    }

    private void renderSlot(int slot, LayoutLoader.SlotConfig config) {
        String function = config.getFunction();
        slotFunctions.put(slot, function);
        
        switch (function) {
            case "border" -> renderBorder(slot, config);
            case "material_slot" -> renderMaterialSlot(slot, config);
            case "output_slot" -> renderOutputSlot(slot, config);
            case "queue_display" -> renderQueueDisplay(slot, config, null, queueSlots.indexOf(slot));
            case "confirm_button" -> renderConfirmButton(slot, config);
            case "cancel_button" -> renderCancelButton(slot, config);
            case "back_button" -> renderBackButton(slot, config);
        }
    }

    private void renderBorder(int slot, LayoutLoader.SlotConfig config) {
        ItemStack item = new ItemStack(config.getMaterial());
        ItemMeta meta = item.getItemMeta();
        if (!config.getDisplayName().isEmpty()) {
            meta.displayName(MessageUtil.toComponent(config.getDisplayName()));
        }
        item.setItemMeta(meta);
        setItem(slot, item);
    }

    private void renderMaterialSlot(int slot, LayoutLoader.SlotConfig config) {
        if (selectedRecipe == null || !config.isShowItem()) {
            setItem(slot, new ItemStack(Material.AIR));
            return;
        }
        
        List<Forge.ItemReference> materials = selectedRecipe.getMaterials();
        int materialIndex = getMaterialSlotIndex(slot);
        
        if (materialIndex >= 0 && materialIndex < materials.size()) {
            Forge.ItemReference ref = materials.get(materialIndex);
            com.minemart.itemcoreforge.utils.ItemReference itemRef = 
                convertToItemReference(ref);
            ItemStack item = itemRef.toItemStack();
            setItem(slot, item);
        } else {
            setItem(slot, new ItemStack(Material.AIR));
        }
    }

    private void renderOutputSlot(int slot, LayoutLoader.SlotConfig config) {
        if (selectedRecipe == null) {
            setItem(slot, new ItemStack(Material.AIR));
            return;
        }
        
        Forge.ItemReference outputRef = selectedRecipe.getOutput();
        com.minemart.itemcoreforge.utils.ItemReference itemRef = 
            convertToItemReference(outputRef);
        ItemStack item = itemRef.toItemStack();
        setItem(slot, item);
    }

    private void renderQueueDisplay(int slot, LayoutLoader.SlotConfig config, QueueItem item, int slotIndex) {
        if (item == null) {
            ItemStack guiItem = new ItemStack(config.getMaterial());
            ItemMeta meta = guiItem.getItemMeta();
            
            if (!config.getDisplayName().isEmpty()) {
                meta.displayName(MessageUtil.toComponent(config.getDisplayName()));
            }
            
            if (config.getLore() != null && !config.getLore().isEmpty()) {
                List<Component> lore = new ArrayList<>();
                for (String line : config.getLore()) {
                    lore.add(MessageUtil.toComponent(line));
                }
                meta.lore(lore);
            }
            
            guiItem.setItemMeta(meta);
            setItem(slot, guiItem);
        } else {
            Forge.Recipe recipe = forge.getRecipe(item.getRecipeId());
            if (recipe != null) {
                com.minemart.itemcoreforge.utils.ItemReference itemRef = 
                    convertToItemReference(recipe.getOutput());
                ItemStack guiItem = itemRef.toItemStack();
                
                if (guiItem == null || guiItem.getType() == Material.AIR) {
                    guiItem = new ItemStack(Material.BARRIER);
                    ItemMeta tempMeta = guiItem.getItemMeta();
                    if (tempMeta != null) {
                        tempMeta.displayName(MessageUtil.toComponent("&c无效物品"));
                        guiItem.setItemMeta(tempMeta);
                    }
                }
                
                ItemMeta meta = guiItem.getItemMeta();
                if (meta == null) {
                    setItem(slot, guiItem, event -> handleQueueClick(item));
                    return;
                }
                
                List<Component> lore = new ArrayList<>();
                lore.add(MessageUtil.toComponent("&7物品: &e" + recipe.getRecipeId()));
                lore.add(MessageUtil.toComponent("&7数量: &e" + item.getAmount()));
                
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
                
                meta.lore(lore);
                guiItem.setItemMeta(meta);
                
                final QueueItem finalItem = item;
                setItem(slot, guiItem, event -> handleQueueClick(finalItem));
            }
        }
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
            updateQueueDisplay();
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
            updateQueueDisplay();
        } else {
            MessageUtil.sendWithPrefix(player, "&c" + result.getMessage());
        }
    }

    private void renderConfirmButton(int slot, LayoutLoader.SlotConfig config) {
        if (selectedRecipe == null) {
            setItem(slot, new ItemStack(Material.AIR));
            return;
        }
        
        MaterialChecker materialChecker = new MaterialChecker();
        boolean hasMaterials = materialChecker.hasMaterials(player, selectedRecipe.getMaterials());
        
        ConditionChecker conditionChecker = new ConditionChecker();
        boolean conditionsMet = conditionChecker.allConditionsMet(player, selectedRecipe.getConditions());
        
        Material buttonMaterial = (hasMaterials && conditionsMet) ? 
            Material.valueOf(plugin.getConfigManager().getCraftItem()) :
            Material.valueOf(plugin.getConfigManager().getCraftDisabledItem());
        
        String buttonName = (hasMaterials && conditionsMet) ?
            plugin.getConfigManager().getCraftItemName() :
            plugin.getConfigManager().getCraftDisabledName();
        
        ItemStack item = new ItemStack(buttonMaterial);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtil.toComponent(buttonName));
        
        List<Component> lore = new ArrayList<>();
        lore.add(MessageUtil.toComponent("&7制作时间: &e" + selectedRecipe.getCraftTime() + "秒"));
        
        if (!hasMaterials) {
            lore.add(MessageUtil.toComponent("&c材料不足"));
        }
        if (!conditionsMet) {
            lore.add(MessageUtil.toComponent("&c条件不满足"));
        }
        
        meta.lore(lore);
        item.setItemMeta(meta);
        
        setItem(slot, item, event -> {
            if (hasMaterials && conditionsMet) {
                long now = System.currentTimeMillis();
                if (now - lastCraftClickTime >= plugin.getConfigManager().getCraftClickCooldown()) {
                    lastCraftClickTime = now;
                    startCraft();
                }
            }
        });
    }

    private void renderCancelButton(int slot, LayoutLoader.SlotConfig config) {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtil.toComponent("&c关闭"));
        item.setItemMeta(meta);
        
        setItem(slot, item, event -> close());
    }

    private void renderBackButton(int slot, LayoutLoader.SlotConfig config) {
        ItemStack item = new ItemStack(config.getMaterial());
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtil.toComponent(config.getDisplayName()));
        item.setItemMeta(meta);
        
        setItem(slot, item, event -> {
            close();
            RecipeSelectGUI recipeGUI = new RecipeSelectGUI(plugin, player, forge);
            recipeGUI.open();
        });
    }

    private int getMaterialSlotIndex(int slot) {
        int index = 0;
        for (int s = 0; s < slot; s++) {
            String func = slotFunctions.get(s);
            if ("material_slot".equals(func)) {
                index++;
            }
        }
        return index;
    }

    public void selectRecipe(String recipeId) {
        this.selectedRecipe = forge.getRecipe(recipeId);
        if (layout != null) {
            renderLayout();
            updateQueueDisplay();
        }
    }

    private void startCraft() {
        if (selectedRecipe == null) {
            return;
        }
        
        CraftResult result = plugin.getCraftingQueueManager().enqueueCraft(
            player, 
            forge.getForgeId(), 
            selectedRecipe.getRecipeId()
        );
        
        if (result.isSuccess()) {
            updateQueueDisplay();
        } else {
            MessageUtil.sendWithPrefix(player, "&c" + result.getMessage());
        }
    }

    public void update() {
        renderLayout();
        updateQueueDisplay();
    }

    private com.minemart.itemcoreforge.utils.ItemReference convertToItemReference(Forge.ItemReference ref) {
        com.minemart.itemcoreforge.utils.ItemReference itemRef = 
            new com.minemart.itemcoreforge.utils.ItemReference();
        itemRef.setSource(ref.getSource());
        itemRef.setCategory(ref.getCategory());
        itemRef.setId(ref.getId());
        itemRef.setAmount(ref.getAmount());
        itemRef.setCheckDurability(ref.isCheckDurability());
        return itemRef;
    }
}