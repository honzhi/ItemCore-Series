package com.minemart.itemcore.listener;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.core.SetManager;
import com.minemart.itemcore.event.ItemSkillTriggerEvent;
import com.minemart.itemcore.item.CustomItem;
import com.minemart.itemcore.item.skill.ItemSkill;
import com.minemart.itemcore.item.skill.SkillTrigger;
import com.minemart.itemcore.util.DurabilityManager;
import com.minemart.itemcore.utils.ItemIdentifier;
import com.minemart.itemcore.utils.PermissionUtil;
import com.minemart.itemcore.item.ItemSlot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ItemSkillListener extends BaseListener {
    private final Map<UUID, Map<String, BukkitRunnable>> activeTimers = new HashMap<>();
    private final Map<UUID, Long> lastClickTime = new HashMap<>();
    private final Map<UUID, Long> lastSetClickTime = new HashMap<>();
    private static final long CLICK_DELAY_MS = 500;
    private static final long SET_CLICK_DEDUPLICATION_MS = 50;

    public ItemSkillListener(ItemCore plugin) {
        super(plugin);
        startTimerSyncTask();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        EquipmentSlot hand = event.getHand();
        if (hand == null) {
            return;
        }

        SkillTrigger trigger = null;
        if (event.getAction().isLeftClick()) {
            trigger = SkillTrigger.LEFT_CLICK;
            lastClickTime.put(player.getUniqueId(), System.currentTimeMillis());
        } else if (event.getAction().isRightClick()) {
            trigger = SkillTrigger.RIGHT_CLICK;
            lastClickTime.put(player.getUniqueId(), System.currentTimeMillis());
        }

        if (trigger == null) {
            return;
        }

        ItemStack item = hand == EquipmentSlot.HAND
            ? player.getInventory().getItemInMainHand()
            : player.getInventory().getItemInOffHand();
        if (ItemIdentifier.isCustomItem(item)) {
            CustomItem customItem = ItemIdentifier.getCustomItem(item);
            ItemSlot handSlot = (hand == EquipmentSlot.HAND) ? ItemSlot.MAIN_HAND : ItemSlot.OFF_HAND;
            if (customItem != null && customItem.hasSkills()
                    && canTriggerSkills(player, item, customItem) && customItem.canSlot(handSlot)) {
                triggerSkills(player, customItem, trigger);
            }
        }

        triggerSetSkillsOnce(player, trigger, null);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        Player player = (Player) event.getDamager();

        Long lastClick = lastClickTime.get(player.getUniqueId());
        if (lastClick != null && System.currentTimeMillis() - lastClick < CLICK_DELAY_MS) {
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        LivingEntity target = (LivingEntity) event.getEntity();
        if (ItemIdentifier.isCustomItem(item)) {
            CustomItem customItem = ItemIdentifier.getCustomItem(item);
            if (customItem != null && customItem.hasSkills()
                    && canTriggerSkills(player, item, customItem)
                    && customItem.canSlot(ItemSlot.MAIN_HAND)) {
                triggerSkills(player, customItem, SkillTrigger.ATTACK, target);
            }
        }

        triggerSetSkills(player, SkillTrigger.ATTACK, target);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material blockType = event.getBlock().getType();
        Location blockLocation = event.getBlock().getLocation().add(0.5, 0.5, 0.5);
        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (ItemIdentifier.isCustomItem(itemStack)) {
            CustomItem customItem = ItemIdentifier.getCustomItem(itemStack);
            if (customItem != null && customItem.hasSkills()
                    && canTriggerSkills(player, itemStack, customItem)
                    && customItem.canSlot(ItemSlot.MAIN_HAND)) {
                triggerSkills(player, customItem, SkillTrigger.BLOCK_BREAK, null, blockLocation, blockType);
            }
        }

        triggerSetSkills(player, SkillTrigger.BLOCK_BREAK, null, blockLocation, blockType);
    }

    public void registerTimerSkills(Player player, CustomItem item) {
        if (player == null || item == null) {
            return;
        }
        syncTimerSkills(player);
    }

    private void startTimerSyncTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Set<UUID> onlinePlayers = new HashSet<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    onlinePlayers.add(player.getUniqueId());
                    syncTimerSkills(player);
                }

                for (UUID playerId : new HashSet<>(activeTimers.keySet())) {
                    if (!onlinePlayers.contains(playerId)) {
                        unregisterTimerSkills(playerId);
                    }
                }
            }
        }.runTaskTimer(plugin, 1L, 10L);
    }

    private void syncTimerSkills(Player player) {
        UUID playerId = player.getUniqueId();
        Map<String, TimerBinding> desiredTimers = new HashMap<>();

        for (List<CustomItem> slotItems : ItemIdentifier.getEquippedItems(player).values()) {
            for (CustomItem item : slotItems) {
                if (!hasItemPermission(player, item)) {
                    continue;
                }

                List<ItemSkill> skills = item.getSkills();
                for (int index = 0; index < skills.size(); index++) {
                    ItemSkill skill = skills.get(index);
                    if (skill.getTrigger() == SkillTrigger.TIMER && skill.hasTimer()) {
                        String timerKey = "item:" + item.getId().toLowerCase(Locale.ROOT) + ":" + index;
                        desiredTimers.putIfAbsent(timerKey, new TimerBinding(item, skill, null));
                    }
                }
            }
        }

        SetManager setManager = plugin.getSetManager();
        if (setManager != null) {
            for (SetManager.SetSkillActivation activation
                    : setManager.getActiveSkills(player, SkillTrigger.TIMER)) {
                String timerKey = "set:" + activation.getActivationKey();
                desiredTimers.putIfAbsent(timerKey, new TimerBinding(
                        activation.getSourceItem(), activation.getSkill(), activation.getActivationKey()));
            }
        }

        Map<String, BukkitRunnable> playerTimers = activeTimers.get(playerId);
        if (playerTimers != null) {
            playerTimers.entrySet().removeIf(entry -> {
                if (desiredTimers.containsKey(entry.getKey())) {
                    return false;
                }
                entry.getValue().cancel();
                return true;
            });
        }

        if (desiredTimers.isEmpty()) {
            if (playerTimers != null && playerTimers.isEmpty()) {
                activeTimers.remove(playerId);
            }
            return;
        }

        activeTimers.computeIfAbsent(playerId, k -> new HashMap<>());
        playerTimers = activeTimers.get(playerId);

        for (Map.Entry<String, TimerBinding> entry : desiredTimers.entrySet()) {
            if (!playerTimers.containsKey(entry.getKey())) {
                startTimer(player, entry.getKey(), entry.getValue());
            }
        }
    }

    private void startTimer(Player player, String timerKey, TimerBinding binding) {
        UUID playerId = player.getUniqueId();
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    removeTimer(playerId, timerKey);
                    return;
                }
                boolean active = binding.setActivationKey == null
                        ? isItemActiveAndUsable(player, binding.item)
                        : isSetSkillActive(player, binding.setActivationKey);
                if (!active) {
                    cancel();
                    removeTimer(playerId, timerKey);
                    return;
                }

                callSkillTriggerEvent(
                        player, binding.item, binding.skill, SkillTrigger.TIMER,
                        null, player.getLocation(), null);
            }
        };

        activeTimers.get(playerId).put(timerKey, runnable);
        runnable.runTaskTimer(plugin, 0L, binding.skill.getTimerDuration());
    }

    public void unregisterTimerSkills(Player player) {
        if (player == null) {
            return;
        }
        unregisterTimerSkills(player.getUniqueId());
        lastClickTime.remove(player.getUniqueId());
        lastSetClickTime.remove(player.getUniqueId());
    }

    private void unregisterTimerSkills(UUID playerId) {
        Map<String, BukkitRunnable> timers = activeTimers.remove(playerId);
        if (timers != null) {
            timers.values().forEach(BukkitRunnable::cancel);
        }
        lastClickTime.remove(playerId);
        lastSetClickTime.remove(playerId);
    }

    private void removeTimer(UUID playerId, String timerKey) {
        Map<String, BukkitRunnable> timers = activeTimers.get(playerId);
        if (timers == null) {
            return;
        }
        timers.remove(timerKey);
        if (timers.isEmpty()) {
            activeTimers.remove(playerId);
        }
    }

    private boolean canTriggerSkills(Player player, ItemStack itemStack, CustomItem item) {
        return !DurabilityManager.isBroken(itemStack) && hasItemPermission(player, item);
    }

    private boolean hasItemPermission(Player player, CustomItem item) {
        return !item.hasPermission() || PermissionUtil.hasPermission(player, item.getPermission());
    }

    private boolean isItemActiveAndUsable(Player player, CustomItem item) {
        if (!hasItemPermission(player, item)) {
            return false;
        }

        for (List<CustomItem> slotItems : ItemIdentifier.getEquippedItems(player).values()) {
            for (CustomItem activeItem : slotItems) {
                if (activeItem.getId().equalsIgnoreCase(item.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isSetSkillActive(Player player, String activationKey) {
        SetManager setManager = plugin.getSetManager();
        if (setManager == null) {
            return false;
        }
        for (SetManager.SetSkillActivation activation
                : setManager.getActiveSkills(player, SkillTrigger.TIMER)) {
            if (activation.getActivationKey().equals(activationKey)) {
                return true;
            }
        }
        return false;
    }

    private void triggerSkills(Player player, CustomItem item, SkillTrigger trigger) {
        triggerSkills(player, item, trigger, null);
    }

    private void triggerSkills(Player player, CustomItem item, SkillTrigger trigger, LivingEntity target) {
        triggerSkills(player, item, trigger, target, player.getLocation(), null);
    }

    private void triggerSkills(Player player, CustomItem item, SkillTrigger trigger, LivingEntity target,
                               Location location, Material blockType) {
        for (ItemSkill skill : item.getSkills()) {
            if (skill.getTrigger() == trigger) {
                callSkillTriggerEvent(player, item, skill, trigger, target, location, blockType);
            }
        }
    }

    private void triggerSetSkillsOnce(Player player, SkillTrigger trigger, LivingEntity target) {
        long currentTime = System.currentTimeMillis();
        Long lastTrigger = lastSetClickTime.get(player.getUniqueId());
        if (lastTrigger != null && currentTime - lastTrigger < SET_CLICK_DEDUPLICATION_MS) {
            return;
        }
        lastSetClickTime.put(player.getUniqueId(), currentTime);
        triggerSetSkills(player, trigger, target, player.getLocation(), null);
    }

    private void triggerSetSkills(Player player, SkillTrigger trigger, LivingEntity target) {
        triggerSetSkills(player, trigger, target, player.getLocation(), null);
    }

    private void triggerSetSkills(Player player, SkillTrigger trigger, LivingEntity target,
                                  Location location, Material blockType) {
        SetManager setManager = plugin.getSetManager();
        if (setManager == null) {
            return;
        }
        for (SetManager.SetSkillActivation activation : setManager.getActiveSkills(player, trigger)) {
            callSkillTriggerEvent(
                    player, activation.getSourceItem(), activation.getSkill(), trigger,
                    target, location, blockType);
        }
    }

    private void callSkillTriggerEvent(Player player, CustomItem item, ItemSkill skill, SkillTrigger trigger,
                                       LivingEntity target, Location location, Material blockType) {
        if (trigger == SkillTrigger.BLOCK_BREAK && (blockType == null || !skill.matchesBlock(blockType))) {
            return;
        }
        if (!passesChance(skill)) {
            return;
        }

        ItemSkillTriggerEvent event = new ItemSkillTriggerEvent(
                player, item, skill, trigger, target, location);
        Bukkit.getPluginManager().callEvent(event);
    }

    private boolean passesChance(ItemSkill skill) {
        double chance = skill.getChance();
        if (chance <= 0.0) {
            return false;
        }
        return chance >= 100.0 || ThreadLocalRandom.current().nextDouble(100.0) < chance;
    }

    private static class TimerBinding {
        private final CustomItem item;
        private final ItemSkill skill;
        private final String setActivationKey;

        private TimerBinding(CustomItem item, ItemSkill skill, String setActivationKey) {
            this.item = item;
            this.skill = skill;
            this.setActivationKey = setActivationKey;
        }
    }
}
