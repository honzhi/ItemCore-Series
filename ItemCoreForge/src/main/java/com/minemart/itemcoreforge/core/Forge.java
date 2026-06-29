package com.minemart.itemcoreforge.core;

import com.minemart.itemcoreforge.trigger.Trigger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Forge {

    private final String forgeId;
    private final String displayName;
    private final int maxQueueSize;
    private final String type;
    private final String layoutFile;
    private final Map<String, Recipe> recipes = new HashMap<>();

    public Forge(String forgeId, String displayName, int maxQueueSize, String type, String layoutFile) {
        this.forgeId = forgeId;
        this.displayName = displayName;
        this.maxQueueSize = Math.min(Math.max(maxQueueSize, 1), 64);
        this.type = type;
        this.layoutFile = layoutFile;
    }

    public String getForgeId() {
        return forgeId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMaxQueueSize() {
        return maxQueueSize;
    }

    public String getType() {
        return type;
    }

    public String getLayoutFile() {
        return layoutFile;
    }

    public boolean isCustomType() {
        return "custom".equalsIgnoreCase(type);
    }

    public boolean isCraftingTableType() {
        return "crafting_table".equalsIgnoreCase(type);
    }

    public boolean isFurnaceType() {
        return "furnace".equalsIgnoreCase(type);
    }

    public void addRecipe(String key, Recipe recipe) {
        recipes.put(key, recipe);
    }

    public Recipe getRecipe(String key) {
        return recipes.get(key);
    }

    public Map<String, Recipe> getRecipes() {
        return recipes;
    }

    public static class Recipe {
        private final String recipeId;
        private ItemReference input;
        private ItemReference output;
        private final List<ItemReference> materials = new ArrayList<>();
        private final List<Condition> conditions = new ArrayList<>();
        private double craftTime = 1.0;
        private int cookTime = 200;
        private boolean exactPlacement = false;
        private final List<Trigger> onStart = new ArrayList<>();
        private final List<Trigger> onClaim = new ArrayList<>();
        private final List<Trigger> onCancel = new ArrayList<>();
        private int successRate = 100;
        private boolean consumeOnFail = true;

        public Recipe(String recipeId) {
            this.recipeId = recipeId;
        }

        public String getRecipeId() {
            return recipeId;
        }

        public ItemReference getInput() {
            return input;
        }

        public void setInput(ItemReference input) {
            this.input = input;
        }

        public ItemReference getOutput() {
            return output;
        }

        public void setOutput(ItemReference output) {
            this.output = output;
        }

        public List<ItemReference> getMaterials() {
            return materials;
        }

        public void addMaterial(ItemReference material) {
            this.materials.add(material);
        }

        public List<Condition> getConditions() {
            return conditions;
        }

        public void addCondition(Condition condition) {
            this.conditions.add(condition);
        }

        public double getCraftTime() {
            return craftTime;
        }

        public void setCraftTime(double craftTime) {
            this.craftTime = craftTime;
        }

        public int getCookTime() {
            return cookTime;
        }

        public void setCookTime(int cookTime) {
            this.cookTime = cookTime;
        }

        public boolean isExactPlacement() {
            return exactPlacement;
        }

        public void setExactPlacement(boolean exactPlacement) {
            this.exactPlacement = exactPlacement;
        }

        public List<Trigger> getOnStart() {
            return onStart;
        }

        public void addOnStart(Trigger trigger) {
            this.onStart.add(trigger);
        }

        public List<Trigger> getOnClaim() {
            return onClaim;
        }

        public void addOnClaim(Trigger trigger) {
            this.onClaim.add(trigger);
        }

        public List<Trigger> getOnCancel() {
            return onCancel;
        }

        public void addOnCancel(Trigger trigger) {
            this.onCancel.add(trigger);
        }

        public int getSuccessRate() {
            return successRate;
        }

        public void setSuccessRate(int successRate) {
            this.successRate = Math.min(Math.max(successRate, 0), 100);
        }

        public boolean isConsumeOnFail() {
            return consumeOnFail;
        }

        public void setConsumeOnFail(boolean consumeOnFail) {
            this.consumeOnFail = consumeOnFail;
        }
    }

    public static class ItemReference {
        private String source = "itemcore";
        private String category = "";
        private String id = "";
        private int amount = 1;
        private boolean checkDurability = false;
        private int slot = -1;

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public boolean isItemCore() {
            return "itemcore".equalsIgnoreCase(source);
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public boolean isCheckDurability() {
            return checkDurability;
        }

        public void setCheckDurability(boolean checkDurability) {
            this.checkDurability = checkDurability;
        }

        public int getSlot() {
            return slot;
        }

        public void setSlot(int slot) {
            this.slot = slot;
        }

        public boolean hasSlot() {
            return slot >= 0;
        }
    }

    public static class Condition {
        private final String type;
        private int value = 0;
        private String node = "";
        private double amount = 0;

        public Condition(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getNode() {
            return node;
        }

        public void setNode(String node) {
            this.node = node;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }
    }
}
