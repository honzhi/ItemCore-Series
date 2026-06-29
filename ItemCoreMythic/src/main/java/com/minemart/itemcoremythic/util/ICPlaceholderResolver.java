package com.minemart.itemcoremythic.util;

import com.minemart.itemcore.ItemCore;
import com.minemart.itemcore.calculator.AttributeCalculator;
import com.minemart.itemcore.item.attribute.AttributeContainer;
import com.minemart.itemcore.item.attribute.CustomAttribute;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ICPlaceholderResolver {

    private static final Pattern IC_PLACEHOLDER = Pattern.compile("<ic\\.(\\w+)>");

    public static String resolve(Player player, String input) {
        if (input == null || !input.contains("<ic.")) {
            return input;
        }

        AttributeContainer attrs = AttributeCalculator.calculatePlayerAttributes(player);

        Matcher matcher = IC_PLACEHOLDER.matcher(input);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String attrName = matcher.group(1).toLowerCase();
            double value = getAttributeValue(attrs, attrName);
            matcher.appendReplacement(result, String.valueOf(value));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    private static double getAttributeValue(AttributeContainer attrs, String name) {
        for (CustomAttribute attr : CustomAttribute.values()) {
            if (attr.getConfigKey().toLowerCase().equals(name)) {
                return attrs.getAttribute(attr);
            }
        }
        return 0;
    }
}
