package com.minemart.itemcoretrinkets.api;

import org.bukkit.entity.Player;

public class TrinketSlot {

    private final String id;
    private final String type;
    private final String requiredPermission;
    private final int requiredLevel;

    public TrinketSlot(String id, String type) {
        this(id, type, null, 0);
    }

    public TrinketSlot(String id, String type, String requiredPermission, int requiredLevel) {
        this.id = id;
        this.type = type;
        this.requiredPermission = requiredPermission;
        this.requiredLevel = requiredLevel;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getRequiredPermission() {
        return requiredPermission;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    /**
     * 检查玩家是否满足此槽位的使用条件
     */
    public boolean canUse(Player player) {
        if (requiredPermission != null && !requiredPermission.isEmpty()) {
            if (!player.hasPermission(requiredPermission)) {
                return false;
            }
        }
        if (requiredLevel > 0 && player.getLevel() < requiredLevel) {
            return false;
        }
        return true;
    }
}