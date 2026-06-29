package com.minemart.itemcore.api;

import com.minemart.itemcore.item.ItemCategory;

import java.util.Collection;

public interface CategoryRegistry {

    ItemCategory getCategory(String categoryId);

    boolean hasCategory(String categoryId);

    Collection<String> getCategoryIds();

    Collection<ItemCategory> getCategories();
}
