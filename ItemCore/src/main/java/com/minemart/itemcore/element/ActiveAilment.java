package com.minemart.itemcore.element;

import java.util.UUID;

public class ActiveAilment {

    private final String ailmentId;
    private final UUID targetId;
    private final UUID sourceId;
    private final int totalTicks;
    private int remainingTicks;
    private final long appliedAt;

    public ActiveAilment(String ailmentId, UUID targetId, UUID sourceId, int durationTicks) {
        this.ailmentId = ailmentId;
        this.targetId = targetId;
        this.sourceId = sourceId;
        this.totalTicks = durationTicks;
        this.remainingTicks = durationTicks;
        this.appliedAt = System.currentTimeMillis();
    }

    public void tick() {
        remainingTicks--;
    }

    public void resetDuration(int newDuration) {
        this.remainingTicks = newDuration;
    }

    public boolean isExpired() {
        return remainingTicks <= 0;
    }

    public String getAilmentId() { return ailmentId; }
    public UUID getTargetId() { return targetId; }
    public UUID getSourceId() { return sourceId; }
    public int getTotalTicks() { return totalTicks; }
    public int getRemainingTicks() { return remainingTicks; }
    public long getAppliedAt() { return appliedAt; }
}
