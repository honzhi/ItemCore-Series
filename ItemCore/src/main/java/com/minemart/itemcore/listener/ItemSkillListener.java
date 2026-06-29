package com.minemart.itemcore.listener;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.event.ItemSkillTriggerEvent;
import com.minemart.itemcore.item.CustomItem;
import com.minemart.itemcore.item.skill.ItemSkill;
import com.minemart.itemcore.item.skill.SkillTrigger;
import com.minemart.itemcore.utils.ItemIdentifier;
import com.minemart.itemcore.item.ItemSlot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemSkillListener extends BaseListener {
    private final Map<UUID, Map<String, BukkitRunnable>> activeTimers = new HashMap<>();
    private final Map<UUID, Long> lastClickTime = new HashMap<>();
    private static final long CLICK_DELAY_MS = 500;

    public ItemSkillListener(ItemCore plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        EquipmentSlot hand = event.getHand();
        if (hand == null) {
            return;
        }

        ItemStack item = hand == EquipmentSlot.HAND
            ? player.getInventory().getItemInMainHand()
            : player.getInventory().getItemInOffHand();

        if (!ItemIdentifier.isCustomItem(item)) {
            return;
        }

        CustomItem customItem = ItemIdentifier.getCustomItem(item);
        if (customItem == null || !customItem.hasSkills()) {
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

        if (trigger != null) {
            // 检查当前手持位置是否在 active-slots 内
            ItemSlot handSlot = (hand == EquipmentSlot.HAND) ? ItemSlot.MAIN_HAND : ItemSlot.OFF_HAND;
            if (customItem.canSlot(handSlot)) {
                triggerSkills(player, customItem, trigger);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();

        Long lastClick = lastClickTime.get(player.getUniqueId());
        if (lastClick != null && System.currentTimeMillis() - lastClick < CLICK_DELAY_MS) {
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        if (!ItemIdentifier.isCustomItem(item)) {
            return;
        }

        CustomItem customItem = ItemIdentifier.getCustomItem(item);
        if (customItem == null || !customItem.hasSkills()) {
            return;
        }

        // 检查主手是否在 active-slots 内
        if (customItem.canSlot(ItemSlot.MAIN_HAND)) {
            triggerSkills(player, customItem, SkillTrigger.ATTACK, (org.bukkit.entity.LivingEntity) event.getEntity());
        }
    }

    public void registerTimerSkills(Player player, CustomItem item) {
        UUID playerId = player.getUniqueId();
        String itemId = item.getId();

        activeTimers.computeIfAbsent(playerId, k -> new HashMap<>());
        Map<String, BukkitRunnable> playerTimers = activeTimers.get(playerId);

        if (playerTimers.containsKey(itemId)) {
            playerTimers.get(itemId).cancel();
        }

        for (ItemSkill skill : item.getSkills()) {
            if (skill.getTrigger() == SkillTrigger.TIMER && skill.hasTimer()) {
                BukkitRunnable runnable = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (player.isOnline() && ItemIdentifier.getCustomItem(player.getInventory().getItemInMainHand()) == item) {
                            ItemSkillTriggerEvent event = new ItemSkillTriggerEvent(
                                player, item, skill, SkillTrigger.TIMER, null, player.getLocation()
                            );
                            Bukkit.getPluginManager().callEvent(event);
                        } else {
                            this.cancel();
                        }
                    }
                };
                runnable.runTaskTimer(plugin, 0, skill.getTimerDuration());
                playerTimers.put(itemId, runnable);
            }
        }
    }

    public void unregisterTimerSkills(Player player) {
        UUID playerId = player.getUniqueId();
        if (activeTimers.containsKey(playerId)) {
            activeTimers.get(playerId).values().forEach(BukkitRunnable::cancel);
            activeTimers.remove(playerId);
        }
    }

    private void triggerSkills(Player player, CustomItem item, SkillTrigger trigger) {
        triggerSkills(player, item, trigger, null);
    }

    private void triggerSkills(Player player, CustomItem item, SkillTrigger trigger, org.bukkit.entity.LivingEntity target) {
        for (ItemSkill skill : item.getSkills()) {
            if (skill.getTrigger() == trigger) {
                ItemSkillTriggerEvent event = new ItemSkillTriggerEvent(
                    player, item, skill, trigger, target, player.getLocation()
                );
                Bukkit.getPluginManager().callEvent(event);
            }
        }
    }
}