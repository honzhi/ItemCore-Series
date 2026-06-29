package com.minemart.itemcoreforge.config;

import com.minemart.itemcoreforge.ItemCoreForge;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LayoutLoader {

    private final ItemCoreForge plugin;
    private final Map<String, Layout> layouts = new HashMap<>();

    public LayoutLoader(ItemCoreForge plugin) {
        this.plugin = plugin;
    }

    public void loadAll() {
        layouts.clear();
        
        File layoutsDir = new File(plugin.getDataFolder(), plugin.getConfigManager().getLayoutsDirectory());
        if (!layoutsDir.exists()) {
            layoutsDir.mkdirs();
            saveDefaultLayouts(layoutsDir);
        }
        
        File[] files = layoutsDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) {
            return;
        }
        
        for (File file : files) {
            try {
                Layout layout = loadLayout(file);
                if (layout != null) {
                    String layoutName = file.getName().replace(".yml", "");
                    layouts.put(layoutName, layout);
                    plugin.getLogger().info("已加载布局: " + layoutName);
                }
            } catch (Exception e) {
                plugin.getLogger().severe("加载布局配置失败: " + file.getName() + " - " + e.getMessage());
            }
        }
    }

    private Layout loadLayout(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        int rows = config.getInt("settings.rows", 6);
        Layout layout = new Layout(rows);
        
        List<String> layoutLines = config.getStringList("layout");
        int row = 0;
        for (String line : layoutLines) {
            for (int col = 0; col < line.length() && col < 9; col++) {
                char c = line.charAt(col);
                layout.setSlot(row, col, c);
            }
            row++;
        }
        
        if (config.contains("slots")) {
            for (String slotKey : config.getConfigurationSection("slots").getKeys(false)) {
                String path = "slots." + slotKey;
                SlotConfig slotConfig = loadSlotConfig(config, path);
                if (slotConfig != null) {
                    layout.addSlotConfig(slotKey.charAt(0), slotConfig);
                }
            }
        }
        
        return layout;
    }

    private SlotConfig loadSlotConfig(FileConfiguration config, String path) {
        SlotConfig slotConfig = new SlotConfig();
        
        String materialName = config.getString(path + ".material", "AIR");
        try {
            slotConfig.setMaterial(Material.valueOf(materialName.toUpperCase()));
        } catch (IllegalArgumentException e) {
            slotConfig.setMaterial(Material.AIR);
        }
        
        slotConfig.setDisplayName(config.getString(path + ".display-name", ""));
        slotConfig.setFunction(config.getString(path + ".function", "border"));
        slotConfig.setShowItem(config.getBoolean(path + ".show-item", true));
        
        if (config.contains(path + ".lore")) {
            slotConfig.setLore(config.getStringList(path + ".lore"));
        }
        
        return slotConfig;
    }

    private void saveDefaultLayouts(File layoutsDir) {
        try {
            File defaultLayout = new File(layoutsDir, "Default.yml");
            if (!defaultLayout.exists()) {
                try (InputStream inputStream = plugin.getResource("layouts/Default.yml")) {
                    if (inputStream != null) {
                        java.nio.file.Files.copy(inputStream, defaultLayout.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        plugin.getLogger().info("创建默认布局文件: Default.yml");
                    }
                }
            }
        } catch (IOException e) {
            plugin.getLogger().severe("创建默认布局文件失败: " + e.getMessage());
        }
    }

    public Layout getLayout(String name) {
        return layouts.get(name);
    }

    public boolean hasLayout(String name) {
        return layouts.containsKey(name);
    }

    public static class Layout {
        private final int rows;
        private final char[][] slots;
        private final Map<Character, SlotConfig> slotConfigs = new HashMap<>();

        public Layout(int rows) {
            this.rows = Math.min(Math.max(rows, 1), 6);
            this.slots = new char[this.rows][9];
        }

        public void setSlot(int row, int col, char c) {
            if (row >= 0 && row < rows && col >= 0 && col < 9) {
                slots[row][col] = c;
            }
        }

        public char getSlot(int row, int col) {
            if (row >= 0 && row < rows && col >= 0 && col < 9) {
                return slots[row][col];
            }
            return ' ';
        }

        public void addSlotConfig(char key, SlotConfig config) {
            slotConfigs.put(key, config);
        }

        public SlotConfig getSlotConfig(char key) {
            return slotConfigs.get(key);
        }

        public int getRows() {
            return rows;
        }

        public int getSize() {
            return rows * 9;
        }

        public Map<Character, SlotConfig> getSlotConfigs() {
            return slotConfigs;
        }
    }

    public static class SlotConfig {
        private Material material = Material.AIR;
        private String displayName = "";
        private String function = "border";
        private boolean showItem = true;
        private List<String> lore = List.of();

        public Material getMaterial() {
            return material;
        }

        public void setMaterial(Material material) {
            this.material = material;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getFunction() {
            return function;
        }

        public void setFunction(String function) {
            this.function = function;
        }

        public boolean isShowItem() {
            return showItem;
        }

        public void setShowItem(boolean showItem) {
            this.showItem = showItem;
        }

        public List<String> getLore() {
            return lore;
        }

        public void setLore(List<String> lore) {
            this.lore = lore;
        }
    }
}
