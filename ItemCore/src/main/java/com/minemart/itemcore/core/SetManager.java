package com.minemart.itemcore.core;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.item.CustomItem;
import com.minemart.itemcore.item.ItemSlot;
import com.minemart.itemcore.item.attribute.AttributeContainer;
import com.minemart.itemcore.item.set.ItemSet;
import com.minemart.itemcore.item.set.SetBonus;
import com.minemart.itemcore.item.skill.ItemSkill;
import com.minemart.itemcore.item.skill.SkillTrigger;
import com.minemart.itemcore.utils.ItemIdentifier;
import com.minemart.itemcore.utils.PermissionUtil;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class SetManager {

    private static final UUID SET_ARMOR_MODIFIER_UUID =
            UUID.fromString("4d1fa5e8-3664-49fc-93e1-35d545166f51");
    private final ItemCore plugin;
    private final Map<String, ItemSet> itemSets = new LinkedHashMap<>();

    public SetManager(ItemCore plugin) {
        this.plugin = plugin;
    }

    public void loadAll() {
        itemSets.clear();
        if (plugin.getLoaderManager() != null) {
            itemSets.putAll(plugin.getLoaderManager().getItemSets());
        }
    }

    public void reload() {
        loadAll();
    }

    public ItemSet getItemSet(String setId) {
        if (setId == null) {
            return null;
        }
        return itemSets.get(setId.toLowerCase(Locale.ROOT));
    }

    public Collection<ItemSet> getItemSets() {
        return Collections.unmodifiableCollection(itemSets.values());
    }

    public AttributeContainer calculateActiveAttributes(Player player,
            Map<ItemSlot, List<CustomItem>> equippedItems) {
        AttributeContainer result = new AttributeContainer();
        for (ActiveSetBonus activeBonus : getActiveBonuses(player, equippedItems)) {
            result.merge(activeBonus.getBonus().getAttributes());
        }
        return result;
    }

    public AttributeContainer calculateActiveAttributes(Player player) {
        return calculateActiveAttributes(player, ItemIdentifier.getEquippedItems(player));
    }

    public void applyPassiveBonuses(Player player) {
        AttributeInstance armorAttribute = player.getAttribute(Attribute.ARMOR);
        if (armorAttribute == null) {
            return;
        }

        AttributeModifier previousModifier = armorAttribute.getModifier(SET_ARMOR_MODIFIER_UUID);
        if (previousModifier != null) {
            armorAttribute.removeModifier(previousModifier);
        }

        double armorBonus = calculateActiveAttributes(player)
                .getAttribute(com.minemart.itemcore.item.attribute.CustomAttribute.ARMOR);
        if (armorBonus != 0) {
            armorAttribute.addModifier(new AttributeModifier(
                    SET_ARMOR_MODIFIER_UUID,
                    "ItemCore Set Armor",
                    armorBonus,
                    AttributeModifier.Operation.ADD_NUMBER));
        }
    }

    public void clearPassiveBonuses(Player player) {
        AttributeInstance armorAttribute = player.getAttribute(Attribute.ARMOR);
        if (armorAttribute == null) {
            return;
        }
        AttributeModifier modifier = armorAttribute.getModifier(SET_ARMOR_MODIFIER_UUID);
        if (modifier != null) {
            armorAttribute.removeModifier(modifier);
        }
    }

    public List<SetSkillActivation> getActiveSkills(Player player, SkillTrigger trigger) {
        List<SetSkillActivation> result = new ArrayList<>();
        for (ActiveSetBonus activeBonus : getActiveBonuses(player, ItemIdentifier.getEquippedItems(player))) {
            List<ItemSkill> skills = activeBonus.getBonus().getSkills();
            for (int index = 0; index < skills.size(); index++) {
                ItemSkill skill = skills.get(index);
                if (skill.getTrigger() == trigger) {
                    String activationKey = activeBonus.getItemSet().getId().toLowerCase(Locale.ROOT)
                            + ":" + activeBonus.getBonus().getRequiredPieces() + ":" + index;
                    result.add(new SetSkillActivation(activationKey, activeBonus.getSourceItem(), skill));
                }
            }
        }
        return result;
    }

    private List<ActiveSetBonus> getActiveBonuses(Player player,
            Map<ItemSlot, List<CustomItem>> equippedItems) {
        Map<String, SetProgress> progressBySet = new LinkedHashMap<>();

        for (Map.Entry<ItemSlot, List<CustomItem>> entry : equippedItems.entrySet()) {
            for (CustomItem item : entry.getValue()) {
                String setId = item.getSetId();
                if (setId == null || setId.isEmpty()) {
                    continue;
                }
                if (item.hasPermission() && !PermissionUtil.hasPermission(player, item.getPermission())) {
                    continue;
                }

                ItemSet itemSet = getItemSet(setId);
                if (itemSet == null) {
                    continue;
                }

                SetProgress progress = progressBySet.computeIfAbsent(
                        itemSet.getId().toLowerCase(Locale.ROOT), ignored -> new SetProgress(itemSet));
                progress.addPiece(item);
                break;
            }
        }

        List<ActiveSetBonus> activeBonuses = new ArrayList<>();
        for (SetProgress progress : progressBySet.values()) {
            for (SetBonus bonus : progress.itemSet.getActiveBonuses(progress.pieces)) {
                activeBonuses.add(new ActiveSetBonus(
                        progress.itemSet, bonus, progress.pieces, progress.sourceItem));
            }
        }
        return activeBonuses;
    }

    public static class SetSkillActivation {
        private final String activationKey;
        private final CustomItem sourceItem;
        private final ItemSkill skill;

        public SetSkillActivation(String activationKey, CustomItem sourceItem, ItemSkill skill) {
            this.activationKey = activationKey;
            this.sourceItem = sourceItem;
            this.skill = skill;
        }

        public String getActivationKey() {
            return activationKey;
        }

        public CustomItem getSourceItem() {
            return sourceItem;
        }

        public ItemSkill getSkill() {
            return skill;
        }
    }

    private static class ActiveSetBonus {
        private final ItemSet itemSet;
        private final SetBonus bonus;
        private final int equippedPieces;
        private final CustomItem sourceItem;

        private ActiveSetBonus(ItemSet itemSet, SetBonus bonus, int equippedPieces,
                               CustomItem sourceItem) {
            this.itemSet = itemSet;
            this.bonus = bonus;
            this.equippedPieces = equippedPieces;
            this.sourceItem = sourceItem;
        }

        public ItemSet getItemSet() {
            return itemSet;
        }

        public SetBonus getBonus() {
            return bonus;
        }

        public int getEquippedPieces() {
            return equippedPieces;
        }

        public CustomItem getSourceItem() {
            return sourceItem;
        }
    }

    private static class SetProgress {
        private final ItemSet itemSet;
        private int pieces;
        private CustomItem sourceItem;

        private SetProgress(ItemSet itemSet) {
            this.itemSet = itemSet;
        }

        private void addPiece(CustomItem item) {
            pieces++;
            if (sourceItem == null) {
                sourceItem = item;
            }
        }
    }
}
