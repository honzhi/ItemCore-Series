package com.minemart.itemcore.loader;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.item.attribute.AttributeContainer;
import com.minemart.itemcore.item.attribute.CustomAttribute;
import com.minemart.itemcore.item.attribute.ElementType;
import com.minemart.itemcore.item.set.ItemSet;
import com.minemart.itemcore.item.set.SetBonus;
import com.minemart.itemcore.item.skill.ItemSkill;
import com.minemart.itemcore.item.skill.SkillTrigger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SetLoader {

    private final ItemCore plugin;
    private final Map<String, ItemSet> itemSets = new LinkedHashMap<>();

    public SetLoader(ItemCore plugin) {
        this.plugin = plugin;
    }

    public Map<String, ItemSet> load(File file) {
        itemSets.clear();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection root = config.getConfigurationSection("sets");
        if (root == null) {
            root = config;
        }

        for (String setId : root.getKeys(false)) {
            ConfigurationSection setSection = root.getConfigurationSection(setId);
            if (setSection == null) {
                continue;
            }

            ItemSet itemSet = parseSet(setId, setSection);
            if (itemSet != null) {
                itemSets.put(setId.toLowerCase(Locale.ROOT), itemSet);
            }
        }

        plugin.getLogger().info("成功加载 " + itemSets.size() + " 个套装");
        return itemSets;
    }

    private ItemSet parseSet(String setId, ConfigurationSection section) {
        String displayName = section.getString("display_name", setId);
        List<String> lore = section.getStringList("lore");
        ItemSet.ActivationMode activationMode = ItemSet.ActivationMode.fromConfig(
                section.getString("activation_mode", "cumulative"));
        ConfigurationSection bonusesSection = section.getConfigurationSection("bonuses");
        Map<Integer, SetBonus> bonuses = new LinkedHashMap<>();

        if (bonusesSection == null) {
            plugin.getLogger().warning("套装 " + setId + " 缺少 bonuses 配置");
        } else {
            for (String piecesKey : bonusesSection.getKeys(false)) {
                int requiredPieces;
                try {
                    requiredPieces = Integer.parseInt(piecesKey);
                } catch (NumberFormatException exception) {
                    plugin.getLogger().warning("套装 " + setId + " 的档位不是有效件数: " + piecesKey);
                    continue;
                }

                if (requiredPieces <= 0) {
                    plugin.getLogger().warning("套装 " + setId + " 的激活件数必须大于 0: " + piecesKey);
                    continue;
                }

                ConfigurationSection bonusSection = bonusesSection.getConfigurationSection(piecesKey);
                if (bonusSection == null) {
                    continue;
                }

                AttributeContainer attributes = parseAttributes(
                        setId, requiredPieces, bonusSection.getConfigurationSection("attributes"));
                List<ItemSkill> skills = parseSkills(
                        setId, requiredPieces, bonusSection.getConfigurationSection("skills"));
                bonuses.put(requiredPieces, new SetBonus(requiredPieces, attributes, skills));
            }
        }

        return new ItemSet(setId, displayName, lore, activationMode, bonuses);
    }

    private AttributeContainer parseAttributes(String setId, int pieces, ConfigurationSection section) {
        AttributeContainer attributes = new AttributeContainer();
        if (section == null) {
            return attributes;
        }

        for (String key : section.getKeys(false)) {
            if (key.equalsIgnoreCase("element_mastery") || key.equalsIgnoreCase("element_resist")) {
                ConfigurationSection elementSection = section.getConfigurationSection(key);
                if (elementSection == null) {
                    continue;
                }
                for (String elementId : elementSection.getKeys(false)) {
                    ElementType element = ItemCore.getElementRegistry().get(elementId);
                    if (element == null) {
                        plugin.getLogger().warning("套装 " + setId + " 的 " + pieces + " 件效果包含未知元素: " + elementId);
                        continue;
                    }
                    double value = YamlParserUtil.parsePercentage(elementSection.get(elementId));
                    if (key.equalsIgnoreCase("element_mastery")) {
                        attributes.setElementMastery(element, value);
                    } else {
                        attributes.setElementResistance(element, value);
                    }
                }
                continue;
            }

            CustomAttribute attribute = CustomAttribute.fromConfigKey(key);
            if (attribute == null) {
                plugin.getLogger().warning("套装 " + setId + " 的 " + pieces + " 件效果包含未知属性: " + key);
                continue;
            }

            Object value = section.get(key);
            if (value instanceof ConfigurationSection) {
                plugin.getLogger().warning("套装属性不支持随机范围: " + setId + ".bonuses." + pieces + ".attributes." + key);
                continue;
            }
            attributes.setAttribute(attribute, YamlParserUtil.parsePercentage(value));
        }

        return attributes;
    }

    private List<ItemSkill> parseSkills(String setId, int pieces, ConfigurationSection section) {
        List<ItemSkill> skills = new ArrayList<>();
        if (section == null) {
            return skills;
        }

        for (String triggerKey : section.getKeys(false)) {
            SkillTrigger trigger = SkillTrigger.fromConfigKey(triggerKey);
            if (trigger == null) {
                plugin.getLogger().warning("套装 " + setId + " 的 " + pieces + " 件效果包含未知技能触发器: " + triggerKey);
                continue;
            }

            ConfigurationSection skillSection = section.getConfigurationSection(triggerKey);
            if (skillSection == null) {
                plugin.getLogger().warning("套装 " + setId + " 的技能配置格式无效: " + triggerKey);
                continue;
            }

            String skillName = skillSection.getString("skill");
            if (skillName == null || skillName.isEmpty()) {
                plugin.getLogger().warning("套装 " + setId + " 的技能缺少 skill: " + triggerKey);
                continue;
            }

            String provider = skillSection.getString("provider", "mythicmobs");
            int duration = skillSection.getInt("duration", 20);
            skills.add(new ItemSkill(trigger, provider, skillName, duration));
        }

        return skills;
    }

    public Map<String, ItemSet> getItemSets() {
        return itemSets;
    }
}
