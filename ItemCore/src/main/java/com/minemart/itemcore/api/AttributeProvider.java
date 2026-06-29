package com.minemart.itemcore.api;

import com.minemart.itemcore.item.attribute.AttributeContainer;
import org.bukkit.entity.Player;

/**
 * 属性提供者接口 - 其他插件通过此接口向 ItemCore 贡献属性
 */
public interface AttributeProvider {

    /**
     * 返回该玩家应获得的额外属性
     * @param player 目标玩家
     * @return 属性容器（空容器表示无贡献）
     */
    AttributeContainer getAttributes(Player player);
}