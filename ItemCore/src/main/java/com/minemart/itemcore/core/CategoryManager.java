package com.minemart.itemcore.core;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.api.CategoryRegistry;
import com.minemart.itemcore.item.ItemCategory;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class CategoryManager implements CategoryRegistry {

    private final ItemCore plugin;
    private final Map<String, ItemCategory> categories;

    public CategoryManager(ItemCore plugin) {
        this.plugin = plugin;
        this.categories = new LinkedHashMap<>();
    }

    public void loadAll() {
        categories.clear();
        if (plugin.getLoaderManager() != null) {
            categories.putAll(plugin.getLoaderManager().getCategories());
            plugin.getLogger().info("分类管理器加载了 " + categories.size() + " 个分类");
        }
    }

    public void reload() {
        loadAll();
    }

    @Override
    public ItemCategory getCategory(String categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categories.get(categoryId.toLowerCase());
    }

    @Override
    public boolean hasCategory(String categoryId) {
        return categoryId != null && categories.containsKey(categoryId.toLowerCase());
    }

    @Override
    public Collection<String> getCategoryIds() {
        return Collections.unmodifiableCollection(categories.keySet());
    }

    @Override
    public Collection<ItemCategory> getCategories() {
        return Collections.unmodifiableCollection(categories.values());
    }

    public int getCategoryCount() {
        return categories.size();
    }
}
