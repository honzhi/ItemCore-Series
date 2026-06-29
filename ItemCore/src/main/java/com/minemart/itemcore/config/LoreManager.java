package com.minemart.itemcore.config;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.item.CustomItem;
import com.minemart.itemcore.item.attribute.AttributeContainer;
import com.minemart.itemcore.item.attribute.CustomAttribute;
import com.minemart.itemcore.item.attribute.ElementType;
import com.minemart.itemcore.utils.ItemBuilder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.bukkit.persistence.PersistentDataType;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class LoreManager {

    private final ItemCore plugin;
    private FileConfiguration loreConfig;
    private FileConfiguration attributeConfig;
    private File loreFile;
    private File attributeFile;
    private static final String TOOLTIP_FOLDER = "tooltip";

    public LoreManager(ItemCore plugin) {
        this.plugin = plugin;
        this.loreFile = new File(new File(plugin.getDataFolder(), TOOLTIP_FOLDER), "lore.yml");
        this.attributeFile = new File(new File(plugin.getDataFolder(), TOOLTIP_FOLDER), "stats.yml");
    }

    public void load() {
        File tooltipFolder = new File(plugin.getDataFolder(), TOOLTIP_FOLDER);
        if (!tooltipFolder.exists()) {
            tooltipFolder.mkdirs();
        }

        if (!loreFile.exists()) {
            plugin.saveResource(TOOLTIP_FOLDER + "/lore.yml", false);
        }
        if (!attributeFile.exists()) {
            plugin.saveResource(TOOLTIP_FOLDER + "/stats.yml", false);
        }

        loreConfig = YamlConfiguration.loadConfiguration(loreFile);
        attributeConfig = YamlConfiguration.loadConfiguration(attributeFile);

        try {
            InputStream loreStream = plugin.getResource(TOOLTIP_FOLDER + "/lore.yml");
            if (loreStream != null) {
                YamlConfiguration defaultLore = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(loreStream, StandardCharsets.UTF_8)
                );
                loreConfig.setDefaults(defaultLore);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "无法加载默认 lore 配置", e);
        }

        try {
            InputStream attrStream = plugin.getResource(TOOLTIP_FOLDER + "/stats.yml");
            if (attrStream != null) {
                YamlConfiguration defaultAttr = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(attrStream, StandardCharsets.UTF_8)
                );
                attributeConfig.setDefaults(defaultAttr);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "无法加载默认属性配置", e);
        }

        plugin.getLogger().info("Lore 配置已加载");
    }

    public void reload() {
        File tooltipFolder = new File(plugin.getDataFolder(), TOOLTIP_FOLDER);
        if (!tooltipFolder.exists()) {
            tooltipFolder.mkdirs();
        }

        if (!loreFile.exists()) {
            plugin.saveResource(TOOLTIP_FOLDER + "/lore.yml", false);
        }
        if (!attributeFile.exists()) {
            plugin.saveResource(TOOLTIP_FOLDER + "/stats.yml", false);
        }

        loreConfig = YamlConfiguration.loadConfiguration(loreFile);
        attributeConfig = YamlConfiguration.loadConfiguration(attributeFile);

        try {
            InputStream loreStream = plugin.getResource(TOOLTIP_FOLDER + "/lore.yml");
            if (loreStream != null) {
                YamlConfiguration defaultLore = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(loreStream, StandardCharsets.UTF_8)
                );
                loreConfig.setDefaults(defaultLore);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "无法加载默认 lore 配置", e);
        }

        try {
            InputStream attrStream = plugin.getResource(TOOLTIP_FOLDER + "/stats.yml");
            if (attrStream != null) {
                YamlConfiguration defaultAttr = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(attrStream, StandardCharsets.UTF_8)
                );
                attributeConfig.setDefaults(defaultAttr);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "无法加载默认属性配置", e);
        }

        plugin.getLogger().info("Lore 配置已重载");
    }

    public void save() {
        try {
            if (loreConfig != null && loreFile != null) {
                loreConfig.save(loreFile);
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "无法保存 lore 配置文件", e);
        }
        try {
            if (attributeConfig != null && attributeFile != null) {
                attributeConfig.save(attributeFile);
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "无法保存属性配置文件", e);
        }
    }

    public boolean isEnabled() {
        return loreConfig.getBoolean("general.enabled", true);
    }

    public List<String> generateLore(CustomItem item) {
        return generateLore(item, null);
    }

    public List<String> generateLore(CustomItem item, ItemStack itemStack) {
        if (!isEnabled()) {
            return item.getLore();
        }

        List<String> loreFormatList = loreConfig.getStringList("lore_format");
        if (loreFormatList.isEmpty()) {
            return item.getLore();
        }

        AttributeContainer attributes = item.getAttributes();
        List<String> result = new ArrayList<>();
        List<String> pendingEmptyLines = new ArrayList<>();

        for (String placeholder : loreFormatList) {
            if (placeholder.equals("#item-lore#")) {
                List<String> itemLore = item.getLore();
                if (!itemLore.isEmpty()) {
                    for (String line : itemLore) {
                        result.add(line);
                    }
                    pendingEmptyLines.clear();
                }
            } else if (placeholder.equals("{bar}")) {
                if (pendingEmptyLines.isEmpty()) {
                    pendingEmptyLines.add("");
                }
            } else if (placeholder.equals("{sbar}")) {
                result.add("");
                pendingEmptyLines.clear();
            } else if (placeholder.equals("#durability#")) {
                int maxDura = item.getDurability();
                if (maxDura > 0 && !item.isUnbreakable()) {
                    for (String emptyLine : pendingEmptyLines) {
                        result.add(emptyLine);
                    }
                    pendingEmptyLines.clear();

                    int current = maxDura;
                    if (itemStack != null && itemStack.hasItemMeta()) {
                        var meta = itemStack.getItemMeta();
                        if (meta != null) {
                            var pdc = meta.getPersistentDataContainer();
                            if (pdc.has(ItemBuilder.DURABILITY_KEY, PersistentDataType.INTEGER)) {
                                current = pdc.get(ItemBuilder.DURABILITY_KEY, PersistentDataType.INTEGER);
                            }
                        }
                    }

                    if (current <= 0) {
                        result.add("&c&l已损坏");
                    } else {
                        String durabilFormat = attributeConfig.getString("durability");
                        if (durabilFormat != null && !durabilFormat.isEmpty()) {
                            String durabilLine = durabilFormat.replace("{current}", String.valueOf(current))
                                               .replace("{max}", String.valueOf(maxDura))
                                               .replace("{bar}", buildDurabilityBar(current, maxDura));
                            result.add(durabilLine);
                        }
                    }
                }
            } else if (placeholder.equals("#element_mastery#") || placeholder.equals("#element_resist#")) {
                boolean isMastery = placeholder.equals("#element_mastery#");
                String formatKey = isMastery ? "element_mastery" : "element_resist";
                String elementFormat = attributeConfig.getString(formatKey);
                if (elementFormat != null && !elementFormat.isEmpty()) {
                    ItemCore icPlugin = ItemCore.getInstance();
                    if (icPlugin != null) {
                        var registry = ItemCore.getElementRegistry();
                        if (registry != null) {
                            for (ElementType element : registry.getAll()) {
                                if (element == ElementType.NONE) continue;
                                double val = isMastery ? attributes.getElementMastery(element) : attributes.getElementResistance(element);
                                if (val == 0) continue;
                                String line = elementFormat.replace("{value}", String.valueOf(Math.abs(val)));
                                line = line.replace("<plus>", val > 0 ? "+" : "-");
                                line = line.replace("{display}", element.getDisplayName());
                                for (String emptyLine : pendingEmptyLines) {
                                    result.add(emptyLine);
                                }
                                pendingEmptyLines.clear();
                                result.add(line);
                            }
                        }
                    }
                }
            } else if (placeholder.startsWith("#") && placeholder.endsWith("#")) {
                String attrName = placeholder.substring(1, placeholder.length() - 1);
                String loreLine = getAttributeLoreLine(attrName, attributes);
                if (loreLine != null) {
                    for (String emptyLine : pendingEmptyLines) {
                        result.add(emptyLine);
                    }
                    pendingEmptyLines.clear();
                    result.add(loreLine);
                }
            }
        }

        return result;
    }private String getAttributeLoreLine(String attrName, AttributeContainer attributes) {
        String format = attributeConfig.getString(attrName);
        if (format == null || format.isEmpty()) {
            return null;
        }

        Double value = getAttributeValue(attrName, attributes);
        if (value == null) {
            return null;
        }

        String valueStr = formatValue(value, attrName);
        String plus = formatPlus(value);

        String result = format.replace("{value}", valueStr);
        result = result.replace("<plus>", plus);

        return result;
    }

    private Double getAttributeValue(String attrName, AttributeContainer attributes) {
        CustomAttribute customAttr = getCustomAttribute(attrName);
        if (customAttr != null) {
            double value = attributes.getAttribute(customAttr);
            if (value != 0) {
                return value;
            }
            return null;
        }

        return null;
    }

    private CustomAttribute getCustomAttribute(String attrName) {
        for (CustomAttribute attr : CustomAttribute.values()) {
            if (attr.getConfigKey().equalsIgnoreCase(attrName)) {
                return attr;
            }
        }
        return null;
    }

    private String formatValue(double value, String attrName) {
        CustomAttribute customAttr = getCustomAttribute(attrName);
        boolean isPercentage = false;

        if (customAttr != null) {
            isPercentage = customAttr.isPercentage();
        }

        if (isPercentage) {
            return formatPercentage(value);
        } else {
            return formatNumber(value);
        }
    }

    private String formatNumber(double value) {
        if (value == Math.floor(value) && !Double.isInfinite(value)) {
            return String.valueOf((int) Math.abs(value));
        }
        return String.format("%.1f", Math.abs(value));
    }

    private String formatPercentage(double value) {
        return String.valueOf((int) Math.abs(value));
    }

    private String formatPlus(double value) {
        if (value > 0) {
            return "+";
        } else if (value < 0) {
            return "-";
        }
        return "";
    }

    public FileConfiguration getLoreConfig() {
        return loreConfig;
    }

    public FileConfiguration getAttributeConfig() {
        return attributeConfig;
    }

    private String buildDurabilityBar(int current, int max) {
        if (max <= 0) return "";
        int totalBars = 10;
        int filled = (int) Math.round((double) current / max * totalBars);
        filled = Math.max(0, Math.min(totalBars, filled));
        int empty = totalBars - filled;

        StringBuilder bar = new StringBuilder();
        if (current <= 10) {
            bar.append("&c");
        } else if (current <= max * 0.3) {
            bar.append("&e");
        } else {
            bar.append("&a");
        }

        for (int i = 0; i < filled; i++) bar.append("|");
        bar.append("&7");
        for (int i = 0; i < empty; i++) bar.append("|");
        return bar.toString();
    }}