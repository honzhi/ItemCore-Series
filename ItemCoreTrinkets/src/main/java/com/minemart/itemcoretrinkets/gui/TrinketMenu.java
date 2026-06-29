package com.minemart.itemcoretrinkets.gui;

import com.minemart.itemcore.api.ItemCoreAPI;
import com.minemart.itemcoretrinkets.ItemCoreTrinkets;
import com.minemart.itemcoretrinkets.api.TrinketSlot;
import com.minemart.itemcoretrinkets.core.PlayerTrinketData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrinketMenu {

    private final ItemCoreTrinkets plugin;
    private final Player player;
    private Inventory inventory;
    private Map<Integer, SlotConfig> slotConfigs;
    private ConfigurationSection guiConfig;

    public TrinketMenu(ItemCoreTrinkets plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.slotConfigs = new HashMap<>();
    }

    public Player getPlayer() {
        return player;
    }

    public void open() {
        guiConfig = plugin.getConfigManager().getGuiConfig();
        createInventory();
        player.openInventory(inventory);
        plugin.getGuiListener().registerMenu(this);

        if (plugin.getConfigManager().isDebugMode()) {
            plugin.getLogger().info("[Debug] 玩家 " + player.getName() + " 打开了饰品界面");
        }
    }

    private void createInventory() {
        List<String> layoutRows = guiConfig.getStringList("layout");
        int rows = layoutRows.size();
        if (rows < 1) rows = 3;
        if (rows > 6) rows = 6;

        String title = guiConfig.getString("settings.title", "&6饰品管理");

        inventory = Bukkit.createInventory(null, rows * 9,
            ChatColor.translateAlternateColorCodes('&', title));

        slotConfigs.clear();
        parseLayout(guiConfig);
        fillSlots();
    }

    private void parseLayout(ConfigurationSection guiConfig) {
        List<String> layoutRows = guiConfig.getStringList("layout");
        ConfigurationSection slotsSection = guiConfig.getConfigurationSection("slots");

        if (layoutRows.isEmpty() || slotsSection == null) {
            return;
        }

        int slotIndex = 0;
        for (String row : layoutRows) {
            for (char c : row.toCharArray()) {
                String charKey = String.valueOf(c);
                if (slotsSection.contains(charKey)) {
                    ConfigurationSection slotConfig = slotsSection.getConfigurationSection(charKey);
                    if (slotConfig != null) {
                        slotConfigs.put(slotIndex, new SlotConfig(slotConfig));
                    }
                }
                slotIndex++;
            }
        }
    }

    private void fillSlots() {
        PlayerTrinketData data = plugin.getTrinketManager().getPlayerData(player);

        for (Map.Entry<Integer, SlotConfig> entry : slotConfigs.entrySet()) {
            int slot = entry.getKey();
            if (slot >= inventory.getSize()) continue;

            SlotConfig config = entry.getValue();
            ItemStack itemStack = createSlotItem(config, data);
            inventory.setItem(slot, itemStack);
        }
    }

    private ItemStack createSlotItem(SlotConfig config, PlayerTrinketData data) {
        Material material;
        try {
            material = Material.valueOf(config.material.toUpperCase());
        } catch (IllegalArgumentException e) {
            material = Material.GRAY_STAINED_GLASS_PANE;
        }

        if ("trinket_slot".equals(config.function) && config.trinketSlotId != null) {
            String equippedItemId = data.getEquippedTrinket(config.trinketSlotId);
            if (equippedItemId != null) {
                ItemStack equipped = ItemCoreAPI.getItemStack(equippedItemId);
                if (equipped != null) {
                    TrinketSlot slotInfo = plugin.getTrinketManager().getSlot(config.trinketSlotId);
                    if (slotInfo != null && !slotInfo.canUse(player)) {
                        return createLockedSlotItem(config, slotInfo);
                    }
                    return equipped;
                }
            }
        } else if ("equipment_slot".equals(config.function) && config.slotType != null) {
            return getPlayerEquipment(config.slotType);
        }

        if ("trinket_slot".equals(config.function) && config.trinketSlotId != null) {
            TrinketSlot slotInfo = plugin.getTrinketManager().getSlot(config.trinketSlotId);
            if (slotInfo != null && !slotInfo.canUse(player)) {
                return createLockedSlotItem(config, slotInfo);
            }
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.displayName));

            if (config.lore != null && !config.lore.isEmpty()) {
                List<String> translatedLore = new ArrayList<>();
                for (String line : config.lore) {
                    translatedLore.add(ChatColor.translateAlternateColorCodes('&', line));
                }
                meta.setLore(translatedLore);
            }

            if (config.customModelData > 0) {
                meta.setCustomModelData(config.customModelData);
            }

            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createLockedSlotItem(SlotConfig config, TrinketSlot slotInfo) {
        Material mat = Material.RED_STAINED_GLASS_PANE;
        if (config.lockedMaterial != null) {
            try {
                mat = Material.valueOf(config.lockedMaterial.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String name = config.displayName;
            if (name == null || name.trim().isEmpty()) {
                name = slotInfo.getId();
            }
            meta.setDisplayName(ChatColor.RED + "🔒 " + ChatColor.translateAlternateColorCodes('&', name));

            if (config.lockedLore != null && !config.lockedLore.isEmpty()) {
                List<String> translatedLore = new ArrayList<>();
                for (String line : config.lockedLore) {
                    translatedLore.add(ChatColor.translateAlternateColorCodes('&', line));
                }
                meta.setLore(translatedLore);
            } else {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.RED + "槽位未解锁");
                if (slotInfo.getRequiredPermission() != null) {
                    lore.add(ChatColor.GRAY + "需要权限: " + ChatColor.WHITE + slotInfo.getRequiredPermission());
                }
                if (slotInfo.getRequiredLevel() > 0) {
                    lore.add(ChatColor.GRAY + "需要等级: " + ChatColor.WHITE + slotInfo.getRequiredLevel());
                }
                meta.setLore(lore);
            }

            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        return item;
    }
    private ItemStack getPlayerEquipment(String slotType) {
        return switch (slotType.toLowerCase()) {
            case "helmet" -> player.getInventory().getHelmet();
            case "chestplate" -> player.getInventory().getChestplate();
            case "leggings" -> player.getInventory().getLeggings();
            case "boots" -> player.getInventory().getBoots();
            case "offhand" -> player.getInventory().getItemInOffHand();
            default -> null;
        };
    }

    public void handleClick(InventoryClickEvent event) {
        if (event.getWhoClicked() != player) return;
        if (event.getClickedInventory() != inventory) return;

        event.setCancelled(true);

        int slot = event.getSlot();
        SlotConfig config = slotConfigs.get(slot);
        if (config == null) return;

        switch (config.function) {
            case "close":
                player.closeInventory();
                break;

            case "trinket_slot":
                handleTrinketSlotClick(event, config);
                break;

            case "border":
            case "equipment_slot":
            default:
                break;
        }
    }

    private void handleTrinketSlotClick(InventoryClickEvent event, SlotConfig config) {
        ItemStack cursorItem = event.getCursor();

        if (cursorItem != null && !cursorItem.getType().isAir()) {
            String oldItemId = plugin.getTrinketManager().getPlayerData(player).getEquippedTrinket(config.trinketSlotId);

            if (oldItemId != null) {
                ItemStack oldItem = ItemCoreAPI.getItemStack(oldItemId);
                if (oldItem != null) {
                    Map<Integer, ItemStack> leftover = player.getInventory().addItem(oldItem);
                    for (ItemStack overflow : leftover.values()) {
                        player.getWorld().dropItemNaturally(player.getLocation(), overflow);
                    }
                    if (plugin.getConfigManager().isDebugMode()) {
                        plugin.getLogger().info("[Debug] 玩家 " + player.getName() + " 替换饰品: " + oldItemId + " -> 已归还");
                    }
                }
            }

            TrinketSlot slotCheck = plugin.getTrinketManager().getSlot(config.trinketSlotId);
            if (slotCheck != null && !slotCheck.canUse(player)) {
                player.sendMessage(plugin.getConfigManager().getMessage("slot-locked"));
            } else if (plugin.getTrinketManager().canEquip(player, config.trinketSlotId, cursorItem)) {
                String itemId = com.minemart.itemcore.utils.ItemIdentifier.getItemId(cursorItem);
                if (itemId != null) {
                    plugin.getTrinketManager().equipTrinket(player, config.trinketSlotId, itemId);
                    event.setCursor(new ItemStack(Material.AIR));
                    refresh();
                }
            } else {
                player.sendMessage(plugin.getConfigManager().getMessage("type-mismatch"));
            }
        } else if (event.getCurrentItem() != null && !event.getCurrentItem().getType().isAir()) {
            String itemId = com.minemart.itemcore.utils.ItemIdentifier.getItemId(event.getCurrentItem());
            if (itemId != null) {
                plugin.getTrinketManager().unequipTrinket(player, config.trinketSlotId);
                event.setCursor(ItemCoreAPI.getItemStack(itemId));
                refresh();

                if (plugin.getConfigManager().isDebugMode()) {
                    plugin.getLogger().info("[Debug] 玩家 " + player.getName() + " 卸下饰品: " + itemId);
                }
            }
        }
    }

    public void refresh() {
        inventory.clear();
        fillSlots();
        player.updateInventory();
    }

    public void handleClose(InventoryCloseEvent event) {
        plugin.getGuiListener().unregisterMenu(player.getUniqueId());

        if (plugin.getConfigManager().isDebugMode()) {
            plugin.getLogger().info("[Debug] 玩家 " + player.getName() + " 关闭了饰品界面");
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    private static class SlotConfig {
        String material;
        String displayName;
        int customModelData;
        String function;
        String trinketSlotId;
        String slotType;
        List<String> lore;
        String lockedMaterial;
        List<String> lockedLore;

        SlotConfig(ConfigurationSection section) {
            this.material = section.getString("material", "GRAY_STAINED_GLASS_PANE");
            this.displayName = section.getString("display-name", " ");
            this.customModelData = section.getInt("custom-model-data", 0);
            this.function = section.getString("function", "border");
            this.trinketSlotId = section.getString("trinket-slot-id");
            this.slotType = section.getString("slot-type");
            this.lore = section.getStringList("lore");
            this.lockedMaterial = section.getString("locked-material");
            this.lockedLore = section.getStringList("locked-lore");
        }
    }
}
