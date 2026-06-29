package com.minemart.itemcore.loader;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.item.ItemCategory;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class CategoryLoader {

    private final ItemCore plugin;
    private final Map<String, ItemCategory> categories;

    public CategoryLoader(ItemCore plugin) {
        this.plugin = plugin;
        this.categories = new LinkedHashMap<>();
    }

    public Map<String, ItemCategory> load(File file) {
        categories.clear();

        if (!file.exists()) {
            plugin.getLogger().info("分类文件不存在，使用默认分类");
            loadDefaultCategories();
            return categories;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection categoriesSection = config.getConfigurationSection("categories");

        if (categoriesSection == null) {
            plugin.getLogger().warning("未找到 categories 配置节");
            loadDefaultCategories();
            return categories;
        }

        for (String categoryId : categoriesSection.getKeys(false)) {
            ConfigurationSection catSection = categoriesSection.getConfigurationSection(categoryId);
            if (catSection == null) {
                continue;
            }

            ItemCategory category = parseCategory(categoryId, catSection);
            if (category != null) {
                categories.put(categoryId, category);
                plugin.getLogger().fine("加载分类: " + categoryId);
            }
        }

        if (categories.isEmpty()) {
            loadDefaultCategories();
        }

        return categories;
    }

    private void loadDefaultCategories() {
        InputStream defaultStream = plugin.getResource("categories.yml");
        if (defaultStream != null) {
            try {
                YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8)
                );
                ConfigurationSection defaultSection = defaultConfig.getConfigurationSection("categories");
                if (defaultSection != null) {
                    for (String categoryId : defaultSection.getKeys(false)) {
                        ConfigurationSection catSection = defaultSection.getConfigurationSection(categoryId);
                        if (catSection != null) {
                            ItemCategory category = parseCategory(categoryId, catSection);
                            if (category != null) {
                                categories.put(categoryId, category);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "加载默认分类失败", e);
            }
        }

        if (categories.isEmpty()) {
            ItemCategory weapons = ItemCategory.builder("weapons")
                .name("武器")
                .icon(Material.DIAMOND_SWORD)
                .slot(19)
                .displayName("&c武器")
                .build();
            categories.put("weapons", weapons);

            ItemCategory armors = ItemCategory.builder("armors")
                .name("护甲")
                .icon(Material.DIAMOND_CHESTPLATE)
                .slot(20)
                .displayName("&b护甲")
                .build();
            categories.put("armors", armors);
        }
    }

    private ItemCategory parseCategory(String categoryId, ConfigurationSection section) {
        String name = section.getString("name", categoryId);
        String displayName = section.getString("display-name", name);

        String materialStr = section.getString("icon", "DIRT");
        Material material = YamlParserUtil.parseMaterial(materialStr);
        if (material == null) {
            material = Material.DIRT;
        }

        int slot = section.getInt("slot", -1);
        String permission = section.getString("permission", null);
        String itemsFile = section.getString("items-file", null);

        List<String> items = section.getStringList("items");
        if (items == null) {
            items = new ArrayList<>();
        }

        List<String> lore = section.getStringList("lore");
        if (lore == null) {
            lore = new ArrayList<>();
        }

        return ItemCategory.builder(categoryId)
            .name(name)
            .icon(material)
            .slot(slot)
            .displayName(displayName)
            .lore(lore)
            .items(items)
            .itemsFile(itemsFile)
            .permission(permission)
            .build();
    }

    public void saveDefaultCategories(File dataFolder) {
        File categoryFile = new File(dataFolder, "categories.yml");
        if (!categoryFile.exists()) {
            plugin.saveResource("categories.yml", false);
        }
    }

    public Map<String, ItemCategory> getCategories() {
        return categories;
    }

    public ItemCategory getCategory(String id) {
        return categories.get(id);
    }
}
