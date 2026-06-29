package com.minemart.itemcore.item;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class ItemCategory {

    private final String id;
    private final String name;
    private final Material icon;
    private final int slot;
    private final String displayName;
    private final List<String> lore;
    private final List<String> items;
    private final String itemsFile;
    private final String permission;

    public ItemCategory(String id, String name, Material icon, int slot, String displayName,
                        List<String> lore, List<String> items, String itemsFile, String permission) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.slot = slot;
        this.displayName = displayName;
        this.lore = lore != null ? lore : new ArrayList<>();
        this.items = items != null ? items : new ArrayList<>();
        this.itemsFile = itemsFile;
        this.permission = permission;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Material getIcon() {
        return icon;
    }

    public int getSlot() {
        return slot;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    public List<String> getItems() {
        return items;
    }

    public String getItemsFile() {
        return itemsFile;
    }

    public String getPermission() {
        return permission;
    }

    public boolean hasPermission() {
        return permission != null && !permission.isEmpty();
    }

    public boolean hasItemsFile() {
        return itemsFile != null && !itemsFile.isEmpty();
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public static class Builder {
        private final String id;
        private String name;
        private Material icon = Material.DIRT;
        private int slot = -1;
        private String displayName;
        private final List<String> lore = new ArrayList<>();
        private final List<String> items = new ArrayList<>();
        private String itemsFile;
        private String permission;

        private Builder(String id) {
            this.id = id;
            this.name = id;
            this.displayName = id;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder icon(Material icon) {
            if (icon != null) {
                this.icon = icon;
            }
            return this;
        }

        public Builder slot(int slot) {
            this.slot = slot;
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

        public Builder addItem(String itemId) {
            if (itemId != null) {
                this.items.add(itemId);
            }
            return this;
        }

        public Builder items(List<String> items) {
            if (items != null) {
                this.items.addAll(items);
            }
            return this;
        }

        public Builder itemsFile(String itemsFile) {
            this.itemsFile = itemsFile;
            return this;
        }

        public Builder permission(String permission) {
            this.permission = permission;
            return this;
        }

        public ItemCategory build() {
            return new ItemCategory(id, name, icon, slot, displayName, lore, items, itemsFile, permission);
        }
    }
}
