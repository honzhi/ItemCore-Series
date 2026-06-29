package com.minemart.itemcore.listener;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.calculator.AttributeCalculator;
import com.minemart.itemcore.damage.DamageManager;
import com.minemart.itemcore.calculator.AttributeCalculator.DamageResult;
import com.minemart.itemcore.event.ItemCoreDamageEvent;
import com.minemart.itemcore.item.attribute.AttributeContainer;
import com.minemart.itemcore.item.attribute.CustomAttribute;
import com.minemart.itemcore.item.attribute.DamageTag;
import com.minemart.itemcore.util.DamageTypeResolver;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.minemart.itemcore.utils.ItemIdentifier;
import com.minemart.itemcore.item.CustomItem;
import com.minemart.itemcore.item.ItemSlot;
import com.minemart.itemcore.skill.SkillMetadataManager;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CombatListener extends BaseListener {

    // 跟踪每个玩家当前由 IC 装备激活的药水效果
    private final Map<UUID, Set<PotionEffectType>> activeIcEffects = new ConcurrentHashMap<>();

    public CombatListener(ItemCore plugin) {
        super(plugin);
        startRegenerationTask();
    }

    private void startRegenerationTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (player.isDead()) {
                        continue;
                    }
                    AttributeContainer attrs = AttributeCalculator.calculatePlayerAttributes(player);
                    double regen = AttributeCalculator.calculateRegeneration(attrs);
                    if (regen > 0) {
                        double maxHealth = player.getMaxHealth();
                        double currentHealth = player.getHealth();
                        if (currentHealth < maxHealth) {
                            double newHealth = Math.min(maxHealth, currentHealth + regen);
                            player.setHealth(newHealth);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Entity damager = event.getDamager();
        Entity victim = event.getEntity();

        if (!(damager instanceof Player) && !(damager instanceof Projectile)) {
            return;
        }

        if (!(victim instanceof LivingEntity)) {
            return;
        }

        Player attacker;
        Projectile projectile = null;

        if (damager instanceof Player) {
            attacker = (Player) damager;
        } else if (damager instanceof Projectile) {
            projectile = (Projectile) damager;
            if (projectile.getShooter() instanceof Player) {
                attacker = (Player) projectile.getShooter();
            } else {
                return;
            }
        } else {
            return;
        }

        LivingEntity defender = (LivingEntity) victim;

        AttributeContainer attackerAttrs = AttributeCalculator.calculatePlayerAttributes(attacker);
        AttributeContainer defenderAttrs;

        if (defender instanceof Player) {
            defenderAttrs = AttributeCalculator.calculatePlayerAttributes((Player) defender);
        } else {
            defenderAttrs = new AttributeContainer();
        }

        // 应用异常效果中的属性修改 (ATTRIBUTE_MOD)
        DamageManager.applyAilmentAttributeMods(defender, defenderAttrs);

        double baseDamage = event.getFinalDamage();

        ItemStack mainHand = attacker.getInventory().getItemInMainHand();
        if (ItemIdentifier.isCustomItem(mainHand)) {
            CustomItem customItem = ItemIdentifier.getCustomItem(mainHand);
            if (customItem != null && customItem.getItemFlags().contains(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES)) {
                baseDamage = 1.0;
            }
        }

        Set<DamageTag> damageTags = SkillMetadataManager.getAndClearDamageTags(attacker);
        
        if (damageTags.isEmpty()) {
            damageTags = DamageTypeResolver.getDamageTags(mainHand, projectile);
        }

        // 禁用原版暴击检测，只使用自定义暴击系统
        boolean isVanillaCrit = false;

        DamageResult result = AttributeCalculator.calculateFullDamage(
            attackerAttrs,
            defenderAttrs,
            baseDamage,
            isVanillaCrit,
            damageTags
        );

        double totalDamage = result.getTotalDamage();

        if (totalDamage <= 0) {
            event.setCancelled(true);
            return;
        }

        event.setDamage(totalDamage);

        // DAMAGE_REDUCTION (applied here instead of onEntityDamage to avoid priority ordering issues)
        if (defenderAttrs != null) {
            double dmgReduction = defenderAttrs.getAttribute(CustomAttribute.DAMAGE_REDUCTION);
            if (dmgReduction > 0) {
            double drOriginal = event.getDamage(); double drReduced = drOriginal * (1 - dmgReduction / 100.0); plugin.getLogger().info("[DR] event=" + event.getClass().getSimpleName() + " dmg=" + drOriginal + " DR=" + dmgReduction + " reduced=" + drReduced);
                event.setDamage(Math.max(0, event.getDamage() * (1 - dmgReduction / 100.0)));
            }
            result.setDamage(event.getDamage());
        }

        if (result.isCrit()) {
            applyKnockback(attacker, defender, attackerAttrs);
        }

        applyOnHitEffects(attacker, attackerAttrs);

        ItemCoreDamageEvent damageEvent = new ItemCoreDamageEvent(attacker, defender, result);
        plugin.getServer().getPluginManager().callEvent(damageEvent);
    }

    private void applyKnockback(Player attacker, LivingEntity defender, AttributeContainer attrs) {
        double knockbackMultiplier = AttributeCalculator.calculateKnockbackMultiplier(attrs);
        if (knockbackMultiplier > 1.0) {
            double extra = knockbackMultiplier - 1.0;
            attacker.setVelocity(attacker.getVelocity().setY(attacker.getVelocity().getY() * (1 + extra)));
        }
    }

    private void applyOnHitEffects(Player attacker, AttributeContainer attrs) {
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        // 实体间伤害已在 onEntityDamageByEntity 中由 IC 系统处理
        // DAMAGE_REDUCTION unified handling for all damage sources
        if (event instanceof EntityDamageByEntityEvent) return;

        Player player = (Player) entity;
        AttributeContainer attrs = AttributeCalculator.calculatePlayerAttributes(player);

        double damageReduction = attrs.getAttribute(CustomAttribute.DAMAGE_REDUCTION);
        if (damageReduction > 0) {
            double originalDamage = event.getDamage();
            double reduced = originalDamage * (1 - damageReduction / 100.0);
            event.setDamage(Math.max(0, reduced));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        schedulePassiveAttributeUpdate(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClickForEquip(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        // 检测是否涉及护甲槽的点击（包括右键快捷穿戴）
        InventoryType.SlotType slotType = event.getSlotType();
        if (slotType == InventoryType.SlotType.ARMOR || event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
            schedulePassiveAttributeUpdate((Player) event.getWhoClicked());
            return;
        }
        // 也检测从快捷栏双击/右键穿戴到护甲槽
        if (event.getInventory().getType() == InventoryType.CRAFTING || event.getInventory().getType() == InventoryType.PLAYER) {
            schedulePassiveAttributeUpdate((Player) event.getWhoClicked());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteractForEquip(PlayerInteractEvent event) {
        if (event.getAction().isRightClick()) {
            schedulePassiveAttributeUpdate(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerItemHeldChange(PlayerItemHeldEvent event) {
        schedulePassiveAttributeUpdate(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        schedulePassiveAttributeUpdate(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        schedulePassiveAttributeUpdate(event.getPlayer());
    }

    private void schedulePassiveAttributeUpdate(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                applyPassiveAttributes(player);
            }
        }.runTaskLater(plugin, 2L);
    }

    private void applyPassiveAttributes(Player player) {
        if (!player.isOnline()) {
            return;
        }

        AttributeContainer attrs = AttributeCalculator.calculatePlayerAttributes(player);

        double maxHealth = AttributeCalculator.calculateHealth(attrs);
        AttributeInstance healthAttr = player.getAttribute(Attribute.MAX_HEALTH);
        if (healthAttr != null) {
            healthAttr.setBaseValue(maxHealth);
            if (player.getHealth() > maxHealth) {
                player.setHealth(maxHealth);
            }
        }

        double movementSpeed = AttributeCalculator.calculateMovementSpeed(attrs);
        AttributeInstance speedAttr = player.getAttribute(Attribute.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.setBaseValue(movementSpeed);
        }

        // 攻速规则：
        // 1. 只从主手武器读取 ATTACK_SPEED，护甲等不影响攻速
        // 2. ATTACK_SPEED 配置值直接作为最终攻速（如 1 = 1.0 慢速，4 = 4.0 快速）
        // 3. 不清除修饰符，让物品自带的+0假修饰符自然叠加
        // 4. 原版武器不受影响（重置 base 为 4.0 后原版修饰符正常生效）
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        double customAttackSpeed = 0;
        if (ItemIdentifier.isCustomItem(mainHand)) {
            CustomItem customWeapon = ItemIdentifier.getCustomItem(mainHand);
            if (customWeapon != null) {
                customAttackSpeed = customWeapon.getAttributes().getAttribute(CustomAttribute.ATTACK_SPEED);
            }
        }

        AttributeInstance attackSpeedAttr = player.getAttribute(Attribute.ATTACK_SPEED);
        if (attackSpeedAttr != null) {
            if (customAttackSpeed != 0) {
                // IC 武器：直接使用配置值作为最终攻速
                attackSpeedAttr.setBaseValue(customAttackSpeed);
            } else {
                // 原版武器：重置为 4.0，原版修饰符自动生效
                attackSpeedAttr.setBaseValue(4.0);
            }
        }

        // 药水效果：仅对 active-slots 允许的装备位生效
        applyPotionEffects(player);
    }

    private void applyPotionEffects(Player player) {
        UUID playerId = player.getUniqueId();

        // 移除之前由 IC 装备添加的所有药水效果
        Set<PotionEffectType> previousEffects = activeIcEffects.remove(playerId);
        if (previousEffects != null) {
            for (PotionEffectType type : previousEffects) {
                player.removePotionEffect(type);
            }
        }

        // 获取当前已装备的 IC 物品（已按 active-slots 过滤）
        Map<ItemSlot, java.util.List<CustomItem>> equipped = ItemIdentifier.getEquippedItems(player);
        Set<PotionEffectType> newEffects = new HashSet<>();

        for (java.util.List<CustomItem> slotItems : equipped.values()) {
            for (CustomItem item : slotItems) {
                for (com.minemart.itemcore.item.PotionEffectInfo effectInfo : item.getEffects()) {
                    PotionEffect effect = effectInfo.toPotionEffect();
                    if (effect != null) {
                        player.addPotionEffect(effect);
                        newEffects.add(effect.getType());
                    }
                }
            }
        }

        if (!newEffects.isEmpty()) {
            activeIcEffects.put(playerId, newEffects);
        }
    }
}