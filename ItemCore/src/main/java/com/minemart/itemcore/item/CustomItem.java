package com.minemart.itemcore.item;

import com.minemart.itemcore.item.attribute.AttributeContainer;
import com.minemart.itemcore.item.skill.ItemSkill;
import com.minemart.itemcore.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CustomItem {

    private final String id;
    private final String type;
    private final Material material;
    private final String displayName;
    private final List<String> lore;
    private final List<EnchantmentInfo> enchantments;
    private final List<ItemFlag> itemFlags;
    private final int customModelData;
    private final boolean unbreakable;
    private final int maxStack;
    private final List<PotionEffectInfo> effects;
    private final String permission;
    private final AttributeContainer attributes;
    private final List<ItemSlot> activeSlots;
    private final boolean rightClickable;
    private final boolean leftClickable;
    private final boolean droppable;
    private final boolean clickable;
    private final boolean keepOnDeath;
    private final List<ItemSkill> skills;

    public CustomItem(String id, String type, Material material, String displayName,
                      List<String> lore, List<EnchantmentInfo> enchantments,
                      List<ItemFlag> itemFlags, int customModelData,
                      boolean unbreakable, int maxStack, List<PotionEffectInfo> effects,
                      String permission, AttributeContainer attributes,
                      List<ItemSlot> activeSlots, boolean rightClickable,
                      boolean leftClickable, boolean droppable, boolean clickable,
                      boolean keepOnDeath, List<ItemSkill> skills) {
        this.id = id;
        this.type = type;
        this.material = material;
        this.displayName = displayName;
        this.lore = lore != null ? lore : new ArrayList<>();
        this.enchantments = enchantments != null ? enchantments : new ArrayList<>();
        this.itemFlags = itemFlags != null ? itemFlags : new ArrayList<>();
        this.customModelData = customModelData;
        this.unbreakable = unbreakable;
        this.maxStack = maxStack;
        this.effects = effects != null ? effects : new ArrayList<>();
        this.permission = permission;
        this.attributes = attributes != null ? attributes : new AttributeContainer();
        this.activeSlots = activeSlots != null ? activeSlots : ItemSlot.defaultSlots();
        this.rightClickable = rightClickable;
        this.leftClickable = leftClickable;
        this.droppable = droppable;
        this.clickable = clickable;
        this.keepOnDeath = keepOnDeath;
        this.skills = skills != null ? skills : new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Material getMaterial() {
        return material;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    public List<EnchantmentInfo> getEnchantments() {
        return enchantments;
    }

    public List<ItemFlag> getItemFlags() {
        return itemFlags;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public boolean isUnbreakable() {
        return unbreakable;
    }

    public int getMaxStack() {
        return maxStack;
    }

    public List<PotionEffectInfo> getEffects() {
        return effects;
    }

    public String getPermission() {
        return permission;
    }

    public AttributeContainer getAttributes() {
        return attributes;
    }

    public List<ItemSlot> getActiveSlots() {
        return activeSlots;
    }

    public boolean isRightClickable() {
        return rightClickable;
    }

    public boolean isLeftClickable() {
        return leftClickable;
    }

    public boolean isDroppable() {
        return droppable;
    }

    public boolean isClickable() {
        return clickable;
    }

    public boolean isKeepOnDeath() {
        return keepOnDeath;
    }

    public List<ItemSkill> getSkills() {
        return skills;
    }

    public boolean hasSkills() {
        return !skills.isEmpty();
    }

    public boolean hasType() {
        return type != null && !type.isEmpty();
    }

    public boolean hasPermission() {
        return permission != null && !permission.isEmpty();
    }

    public boolean hasCustomModelData() {
        return customModelData > 0;
    }

    public boolean hasAttributes() {
        return !attributes.isEmpty();
    }

    public boolean canSlot(ItemSlot slot) {
        if (slot == null) {
            return false;
        }
        if (activeSlots.contains(ItemSlot.TRINKETS) && !activeSlots.contains(ItemSlot.ANY)) {
            return activeSlots.contains(slot);
        }
        return activeSlots.contains(ItemSlot.ANY) || activeSlots.contains(slot);
    }

    public boolean isTrinketOnly() {
        return activeSlots.contains(ItemSlot.TRINKETS) && 
               !activeSlots.contains(ItemSlot.ANY) &&
               activeSlots.stream().noneMatch(s -> s != ItemSlot.TRINKETS);
    }

    public ItemStack toItemStack() {
        return toItemStack(1);
    }

    public ItemStack toItemStack(int amount) {
        return ItemBuilder.build(this, amount);
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public static class Builder {
        private final String id;
        private String type;
        private Material material;
        private String displayName;
        private final List<String> lore = new ArrayList<>();
        private final List<EnchantmentInfo> enchantments = new ArrayList<>();
        private final List<ItemFlag> itemFlags = new ArrayList<>();
        private int customModelData = -1;
        private boolean unbreakable = false;
        private int maxStack = -1;
        private final List<PotionEffectInfo> effects = new ArrayList<>();
        private String permission;
        private AttributeContainer attributes = new AttributeContainer();
        private final List<ItemSlot> activeSlots = new ArrayList<>();
        private boolean rightClickable = true;
        private boolean leftClickable = true;
        private boolean droppable = true;
        private boolean clickable = true;
        private boolean keepOnDeath = false;
        private final List<ItemSkill> skills = new ArrayList<>();

        private Builder(String id) {
            this.id = id;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder material(Material material) {
            this.material = material;
            return this;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder lore(String line) {
            if (line != null) {
                this.lore.add(line);
            }
            return this;
        }

        public Builder lore(List<String> lines) {
            if (lines != null) {
                this.lore.addAll(lines);
            }
            return this;
        }

        public Builder enchantment(EnchantmentInfo enchantment) {
            if (enchantment != null && enchantment.isValid()) {
                this.enchantments.add(enchantment);
            }
            return this;
        }

        public Builder itemFlag(ItemFlag flag) {
            if (flag != null) {
                this.itemFlags.add(flag);
            }
            return this;
        }

        public Builder customModelData(int customModelData) {
            this.customModelData = customModelData;
            return this;
        }

        public Builder unbreakable(boolean unbreakable) {
            this.unbreakable = unbreakable;
            return this;
        }

        public Builder maxStack(int maxStack) {
            this.maxStack = maxStack;
            return this;
        }

        public Builder effect(PotionEffectInfo effect) {
            if (effect != null && effect.isValid()) {
                this.effects.add(effect);
            }
            return this;
        }

        public Builder permission(String permission) {
            this.permission = permission;
            return this;
        }

        public Builder attributes(AttributeContainer attributes) {
            if (attributes != null) {
                this.attributes = attributes;
            }
            return this;
        }

        public Builder activeSlot(ItemSlot slot) {
            if (slot != null) {
                this.activeSlots.add(slot);
            }
            return this;
        }

        public Builder activeSlots(List<ItemSlot> slots) {
            if (slots != null) {
                this.activeSlots.addAll(slots);
            }
            return this;
        }

        public Builder rightClickable(boolean rightClickable) {
            this.rightClickable = rightClickable;
            return this;
        }

        public Builder leftClickable(boolean leftClickable) {
            this.leftClickable = leftClickable;
            return this;
        }

        public Builder droppable(boolean droppable) {
            this.droppable = droppable;
            return this;
        }

        public Builder clickable(boolean clickable) {
            this.clickable = clickable;
            return this;
        }

        public Builder keepOnDeath(boolean keepOnDeath) {
            this.keepOnDeath = keepOnDeath;
            return this;
        }

        public Builder skill(ItemSkill skill) {
            if (skill != null) {
                this.skills.add(skill);
            }
            return this;
        }

        public Builder skills(List<ItemSkill> skills) {
            if (skills != null) {
                this.skills.addAll(skills);
            }
            return this;
        }

        public CustomItem build() {
            if (material == null) {
                throw new IllegalArgumentException("Material must be set for item: " + id);
            }
            List<ItemSlot> slots = activeSlots.isEmpty() ? ItemSlot.defaultSlots() : activeSlots;
            return new CustomItem(id, type, material, displayName, lore, enchantments,
                    itemFlags, customModelData, unbreakable, maxStack, effects,
                    permission, attributes, slots, rightClickable, leftClickable,
                    droppable, clickable, keepOnDeath, skills);
        }
    }
}
