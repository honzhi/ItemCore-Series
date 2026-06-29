package com.minemart.itemcoreforge.trigger;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundTrigger implements Trigger {

    private final Sound sound;
    private final float volume;
    private final float pitch;

    public SoundTrigger(Sound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public SoundTrigger(Sound sound) {
        this(sound, 1.0f, 1.0f);
    }

    @Override
    public void execute(Player player) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    @Override
    public String getType() {
        return "sound";
    }

    public Sound getSound() {
        return sound;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }
}
