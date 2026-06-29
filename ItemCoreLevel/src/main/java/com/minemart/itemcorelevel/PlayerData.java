package com.minemart.itemcorelevel;

import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private String playerName;
    private int level;
    private int exp;
    private long lastOnline;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.level = 1;
        this.exp = 0;
        this.lastOnline = System.currentTimeMillis();
    }

    public PlayerData(UUID uuid, String playerName, int level, int exp, long lastOnline) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.level = level;
        this.exp = exp;
        this.lastOnline = lastOnline;
    }

    public UUID getUuid() { return uuid; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = Math.max(1, level); }
    public int getExp() { return exp; }
    public void setExp(int exp) { this.exp = Math.max(0, exp); }
    public long getLastOnline() { return lastOnline; }
    public void setLastOnline(long lastOnline) { this.lastOnline = lastOnline; }
    public void touch() { this.lastOnline = System.currentTimeMillis(); }
}