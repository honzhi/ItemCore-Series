package com.minemart.itemcore.loader;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.item.CustomItem;
import com.minemart.itemcore.item.ItemCategory;
import com.minemart.itemcore.item.set.ItemSet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;

public class LoaderManager {

    private final ItemCore plugin;
    private final CategoryLoader categoryLoader;
    private final ItemLoader itemLoader;
    private final SetLoader setLoader;

    public LoaderManager(ItemCore plugin) {
        this.plugin = plugin;
        this.categoryLoader = new CategoryLoader(plugin);
        this.itemLoader = new ItemLoader(plugin);
        this.setLoader = new SetLoader(plugin);
    }

    public void loadAll() {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File itemsDir = new File(dataFolder, "items");
        if (!itemsDir.exists()) {
            itemsDir.mkdirs();
            plugin.getLogger().info("创建物品目录: " + itemsDir.getPath());
            copyDefaultItems(itemsDir);
        }

        File categoryFile = new File(dataFolder, "categories.yml");
        if (!categoryFile.exists()) {
            plugin.saveResource("categories.yml", false);
            plugin.getLogger().info("已复制默认分类配置");
        }

        File setsFile = new File(dataFolder, "sets.yml");
        if (!setsFile.exists()) {
            plugin.saveResource("sets.yml", false);
            plugin.getLogger().info("已复制默认套装配置");
        }

        setLoader.load(setsFile);
        categoryLoader.load(categoryFile);
        itemLoader.load(itemsDir, categoryLoader.getCategories());
        for (CustomItem item : itemLoader.getItems().values()) {
            if (item.getSetId() != null
                    && !setLoader.getItemSets().containsKey(item.getSetId().toLowerCase(Locale.ROOT))) {
                plugin.getLogger().warning("物品 " + item.getId() + " 引用了不存在的套装: " + item.getSetId());
            }
        }

        int setCount = setLoader.getItemSets().size();
        int categoryCount = categoryLoader.getCategories().size();
        int itemCount = itemLoader.getItems().size();
        plugin.getLogger().info("加载完成: " + setCount + " 个套装, "
                + categoryCount + " 个分类, " + itemCount + " 个物品");
    }

    private void copyDefaultItems(File itemsDir) {
        String[] defaultItems = {
            "weapons.yml",
            "armors.yml",
            "tools.yml",
            "misc.yml"
        };

        int copied = 0;
        for (String fileName : defaultItems) {
            File targetFile = new File(itemsDir, fileName);
            if (copyResource("items/" + fileName, targetFile)) {
                copied++;
            }
        }

        if (copied > 0) {
            plugin.getLogger().info("已复制默认物品配置: " + copied + " 个文件");
        }
    }

    private boolean copyResource(String resourcePath, File targetFile) {
        try (InputStream in = plugin.getResource(resourcePath)) {
            if (in == null) {
                plugin.getLogger().warning("资源文件不存在: " + resourcePath);
                return false;
            }

            try (FileOutputStream out = new FileOutputStream(targetFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
                return true;
            }
        } catch (IOException e) {
            plugin.getLogger().warning("复制资源文件失败: " + targetFile.getName());
            return false;
        }
    }

    public void reload() {
        plugin.getLogger().info("正在重载物品配置...");
        loadAll();
        plugin.getLogger().info("物品配置重载完成");
    }

    public CategoryLoader getCategoryLoader() {
        return categoryLoader;
    }

    public ItemLoader getItemLoader() {
        return itemLoader;
    }

    public SetLoader getSetLoader() {
        return setLoader;
    }

    public Map<String, ItemCategory> getCategories() {
        return categoryLoader.getCategories();
    }

    public Map<String, CustomItem> getItems() {
        return itemLoader.getItems();
    }

    public Map<String, ItemSet> getItemSets() {
        return setLoader.getItemSets();
    }

    public ItemCategory getCategory(String id) {
        return categoryLoader.getCategory(id);
    }

    public CustomItem getItem(String id) {
        return itemLoader.getItem(id);
    }

    public boolean hasItem(String id) {
        return itemLoader.getItem(id) != null;
    }

    public boolean hasCategory(String id) {
        return categoryLoader.getCategory(id) != null;
    }
}
