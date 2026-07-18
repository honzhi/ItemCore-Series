package com.minemart.itemcore.loader;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.item.CustomItem;
import com.minemart.itemcore.item.EnchantmentInfo;
import com.minemart.itemcore.item.ItemCategory;
import com.minemart.itemcore.item.ItemSlot;
import com.minemart.itemcore.item.PotionEffectInfo;
import com.minemart.itemcore.item.attribute.AttributeContainer;
import com.minemart.itemcore.item.attribute.CustomAttribute;
import com.minemart.itemcore.item.attribute.ElementType;
import com.minemart.itemcore.item.skill.ItemSkill;
import com.minemart.itemcore.item.skill.SkillTrigger;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ItemLoader {

    private final ItemCore plugin;
    private final Map<String, CustomItem> items;
    private final Map<String, ItemCategory> itemCategoryMap;

    public ItemLoader(ItemCore plugin) {
        this.plugin = plugin;
        this.items = new HashMap<>();
        this.itemCategoryMap = new HashMap<>();
    }

    public Map<String, CustomItem> load(File itemsDir, Map<String, ItemCategory> categories) {
        items.clear();
        itemCategoryMap.clear();

        if (!itemsDir.exists()) {
            itemsDir.mkdirs();
            plugin.getLogger().info("物品目录不存在，已创建");
            return items;
        }

        if (!itemsDir.isDirectory()) {
            plugin.getLogger().warning("items 路径不是目录");
            return items;
        }

        if (categories == null || categories.isEmpty()) {
            plugin.getLogger().warning("没有可用的分类，跳过物品加载");
            return items;
        }

        int loadedFiles = 0;
        int loadedItems = 0;

        for (Map.Entry<String, ItemCategory> entry : categories.entrySet()) {
            String categoryId = entry.getKey();
            ItemCategory category = entry.getValue();
            String itemsFile = category.getItemsFile();

            if (itemsFile == null || itemsFile.isEmpty()) {
                plugin.getLogger().fine("分类 " + categoryId + " 没有指定物品文件，跳过");
                continue;
            }

            if (!itemsFile.toLowerCase().endsWith(".yml")) {
                itemsFile = itemsFile + ".yml";
            }

            File file = new File(itemsDir, itemsFile);
            if (!file.exists()) {
                plugin.getLogger().warning("分类 " + categoryId + " 的物品文件不存在: " + itemsFile);
                continue;
            }

            try {
                int count = loadFile(file, categoryId);
                if (count > 0) {
                    loadedFiles++;
                    loadedItems += count;
                    plugin.getLogger().info("加载分类 " + categoryId + " 的物品: " + count + " 个 (文件: " + itemsFile + ")");
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "加载物品文件失败: " + file.getName(), e);
            }
        }

        plugin.getLogger().info("成功加载 " + loadedFiles + " 个文件, " + loadedItems + " 个物品");
        return items;
    }

    private int loadFile(File file, String categoryId) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        int count = 0;

        for (String itemId : config.getKeys(false)) {
            ConfigurationSection itemSection = config.getConfigurationSection(itemId);
            if (itemSection == null) {
                continue;
            }

            CustomItem item = parseItem(itemId, itemSection, categoryId);
            if (item != null) {
                if (items.containsKey(itemId)) {
                    plugin.getLogger().warning("重复的物品 ID: " + itemId + " (文件: " + file.getName() + ")");
                }
                items.put(itemId, item);
                itemCategoryMap.put(itemId, itemCategoryMap.getOrDefault(itemId, null));
                count++;
                plugin.getLogger().fine("加载物品: " + itemId + " (分类: " + categoryId + ")");
            }
        }

        return count;
    }

    private CustomItem parseItem(String itemId, ConfigurationSection section, String categoryId) {
        String materialStr = section.getString("material");
        if (materialStr == null || materialStr.isEmpty()) {
            plugin.getLogger().warning("物品 " + itemId + " 缺少 material 配置");
            return null;
        }

        Material material = YamlParserUtil.parseMaterial(materialStr);
        if (material == null) {
            plugin.getLogger().warning("物品 " + itemId + " 的材质无效: " + materialStr);
            return null;
        }

        CustomItem.Builder builder = CustomItem.builder(itemId)
            .material(material)
            .type(categoryId);

        String displayName = section.getString("display_name");
        if (displayName != null) {
            builder.displayName(displayName);
        }

        if (section.contains("color")) {
            Color color = parseColor(itemId, section.get("color"));
            if (color != null) {
                builder.color(color);
            }
        }

        List<String> lore = section.getStringList("lore");
        if (lore != null && !lore.isEmpty()) {
            builder.lore(lore);
        }

        ConfigurationSection enchantsSection = section.getConfigurationSection("enchantments");
        if (enchantsSection != null) {
            Map<String, Integer> enchantMap = YamlParserUtil.parseEnchantmentMap(enchantsSection);
            for (Map.Entry<String, Integer> entry : enchantMap.entrySet()) {
                builder.enchantment(new EnchantmentInfo(entry.getKey(), entry.getValue()));
            }
        }

        List<?> flagsList = section.getList("item_flags");
        if (flagsList != null) {
            List<ItemFlag> flags = YamlParserUtil.parseItemFlagList(flagsList);
            for (ItemFlag flag : flags) {
                builder.itemFlag(flag);
            }
        }

        if (section.contains("custom_model_data")) {
            builder.customModelData(section.getInt("custom_model_data"));
        }

        if (section.contains("unbreakable")) {
            builder.unbreakable(section.getBoolean("unbreakable"));
        }

        if (section.contains("durability")) {
            builder.durability(section.getInt("durability"));
        }

        if (section.contains("durability_break")) {
            builder.durabilityBreak(section.getBoolean("durability_break"));
        }

        if (section.contains("disable_anvil_repair")) {
            builder.disableAnvilRepair(section.getBoolean("disable_anvil_repair"));
        }

        if (section.contains("disable_enchanting")) {
            builder.disableEnchanting(section.getBoolean("disable_enchanting"));
        }

        if (section.contains("max_stack")) {
            builder.maxStack(section.getInt("max_stack"));
        }

        ConfigurationSection effectsSection = section.getConfigurationSection("effects");
        if (effectsSection != null) {
            Map<String, Object> effectsMap = YamlParserUtil.parseEffectsMap(effectsSection);
            for (Map.Entry<String, Object> entry : effectsMap.entrySet()) {
                int[] values = (int[]) entry.getValue();
                builder.effect(new PotionEffectInfo(entry.getKey(), values[0], values[1]));
            }
        }

        String permission = section.getString("permission");
        if (permission != null) {
            builder.permission(permission);
        }

        ConfigurationSection attributesSection = section.getConfigurationSection("attributes");
        if (attributesSection != null) {
            AttributeContainer container = parseAttributes(attributesSection);
            builder.attributes(container);
        }

        ConfigurationSection skillsSection = section.getConfigurationSection("skills");
        if (skillsSection != null) {
            List<ItemSkill> skills = parseSkills(skillsSection);
            builder.skills(skills);
        }

        List<?> slotsList = section.getList("active_slots");
        if (slotsList != null) {
            for (Object obj : slotsList) {
                if (obj instanceof String) {
                    ItemSlot slot = ItemSlot.fromConfigKey((String) obj);
                    builder.activeSlot(slot);
                }
            }
        }

        if (section.contains("right_clickable")) {
            builder.rightClickable(section.getBoolean("right_clickable"));
        }
        if (section.contains("left_clickable")) {
            builder.leftClickable(section.getBoolean("left_clickable"));
        }
        if (section.contains("droppable")) {
            builder.droppable(section.getBoolean("droppable"));
        }
        if (section.contains("clickable")) {
            builder.clickable(section.getBoolean("clickable"));
        }
        if (section.contains("keep_on_death")) {
            builder.keepOnDeath(section.getBoolean("keep_on_death"));
        }

        try {
            return builder.build();
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "构建物品失败: " + itemId, e);
            return null;
        }
    }

    private List<ItemSkill> parseSkills(ConfigurationSection skillsSection) {
        List<ItemSkill> skills = new ArrayList<>();
        boolean debugMode = plugin.getConfigManager().isDebugMode();
        
        if (debugMode) {
            plugin.getLogger().info("[DEBUG] 开始解析技能配置，找到 " + skillsSection.getKeys(false).size() + " 个触发类型");
        }
        
        for (String key : skillsSection.getKeys(false)) {
            if (debugMode) {
                plugin.getLogger().info("[DEBUG] 解析触发类型: " + key);
            }
            
            SkillTrigger trigger = SkillTrigger.fromConfigKey(key);
            if (trigger == null) {
                if (debugMode) {
                    plugin.getLogger().warning("[DEBUG] 无法识别的触发类型: " + key);
                }
                continue;
            }

            Object value = skillsSection.get(key);
            if (value instanceof ConfigurationSection) {
                ConfigurationSection skillConfig = (ConfigurationSection) value;
                
                String skillName = skillConfig.getString("skill");
                String provider = skillConfig.getString("provider", "mythicmobs");
                
                if (skillName == null || skillName.isEmpty()) {
                    skillName = skillConfig.getString("type");
                }
                
                if (debugMode) {
                    plugin.getLogger().info("[DEBUG] 技能类型: " + skillName);
                    plugin.getLogger().info("[DEBUG] 技能提供者: " + provider);
                }
                
                if (skillName != null && !skillName.isEmpty()) {
                    if (trigger == SkillTrigger.TIMER) {
                        int duration = skillConfig.getInt("duration", 20);
                        skills.add(new ItemSkill(trigger, provider, skillName, duration));
                    } else {
                        skills.add(new ItemSkill(trigger, provider, skillName, 20));
                    }
                    if (debugMode) {
                        plugin.getLogger().info("[DEBUG] 成功添加技能: 触发=" + trigger + ", 提供者=" + provider + ", 技能名=" + skillName);
                    }
                }
            } else {
                if (debugMode) {
                    plugin.getLogger().warning("[DEBUG] 技能配置格式错误，不是 ConfigurationSection");
                }
            }
        }
        
        if (debugMode) {
            plugin.getLogger().info("[DEBUG] 技能解析完成，共添加 " + skills.size() + " 个技能");
        }
        
        return skills;
    }

    private AttributeContainer parseAttributes(ConfigurationSection section) {
        AttributeContainer container = new AttributeContainer();

        for (String key : section.getKeys(false)) {
            String upperKey = key.toUpperCase();

            if (upperKey.equals("ELEMENT_MASTERY")) {
                ConfigurationSection masterySection = section.getConfigurationSection(key);
                if (masterySection != null) {
                    for (String elementId : masterySection.getKeys(false)) {
                        ElementType elementType = resolveElement(elementId);
                        if (elementType != null) {
                            double parsed = YamlParserUtil.parsePercentage(masterySection.get(elementId));
                            container.setElementMastery(elementType, parsed);
                        }
                    }
                }
                continue;
            }

            if (upperKey.equals("ELEMENT_RESIST")) {
                ConfigurationSection resistSection = section.getConfigurationSection(key);
                if (resistSection != null) {
                    for (String elementId : resistSection.getKeys(false)) {
                        ElementType elementType = resolveElement(elementId);
                        if (elementType != null) {
                            double parsed = YamlParserUtil.parsePercentage(resistSection.get(elementId));
                            container.setElementResistance(elementType, parsed);
                        }
                    }
                }
                continue;
            }

            CustomAttribute attribute = CustomAttribute.fromConfigKey(upperKey);
            if (attribute != null) {
                Object value = section.get(key);
                if (value instanceof org.bukkit.configuration.ConfigurationSection) {
                    org.bukkit.configuration.ConfigurationSection rangeSec = (org.bukkit.configuration.ConfigurationSection) value;
                    double min = rangeSec.getDouble("min", 0);
                    double max = rangeSec.getDouble("max", 0);
                    container.setAttributeRange(attribute, min, max);
                } else {
                    double parsedValue = YamlParserUtil.parsePercentage(value);
                    container.setAttribute(attribute, parsedValue);
                }
            }
        }

        return container;
    }

    private Color parseColor(String itemId, Object value) {
        if (value == null) {
            return null;
        }

        String[] components;
        if (value instanceof List<?>) {
            List<?> values = (List<?>) value;
            if (values.size() != 3) {
                plugin.getLogger().warning("物品 " + itemId + " 的 color 必须包含三个 RGB 数值");
                return null;
            }
            components = new String[]{
                String.valueOf(values.get(0)),
                String.valueOf(values.get(1)),
                String.valueOf(values.get(2))
            };
        } else {
            components = String.valueOf(value).split(",");
        }

        if (components.length != 3) {
            plugin.getLogger().warning("物品 " + itemId + " 的 color 格式无效，应为: 255, 0, 0");
            return null;
        }

        try {
            int red = Integer.parseInt(components[0].trim());
            int green = Integer.parseInt(components[1].trim());
            int blue = Integer.parseInt(components[2].trim());
            if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255) {
                plugin.getLogger().warning("物品 " + itemId + " 的 color 数值必须在 0 到 255 之间");
                return null;
            }
            return Color.fromRGB(red, green, blue);
        } catch (NumberFormatException exception) {
            plugin.getLogger().warning("物品 " + itemId + " 的 color 格式无效，应为: 255, 0, 0");
            return null;
        }
    }

    private ElementType resolveElement(String id) {
        for (ElementType element : ItemCore.getElementRegistry().getAll()) {
            if (element.getId().equalsIgnoreCase(id)) {
                return element;
            }
        }
        return null;
    }

    public Map<String, CustomItem> getItems() {
        return items;
    }

    public CustomItem getItem(String id) {
        return items.get(id);
    }
}
