Compiled from "HologramManager.java"
public interface io.lumine.mythic.api.holograms.HologramManager {
  public abstract io.lumine.mythic.api.holograms.IHologram createHologram(java.lang.String, io.lumine.mythic.api.adapters.AbstractLocation);
  public abstract io.lumine.mythic.api.holograms.IHologram createHologram(java.lang.String, io.lumine.mythic.api.adapters.AbstractLocation, java.lang.String);
  public abstract io.lumine.mythic.api.holograms.IHologram createHologram(io.lumine.mythic.api.adapters.AbstractEntity, io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay$PacketTextDisplayBuilder);
  public abstract io.lumine.mythic.api.holograms.IHologram createHologram(java.util.function.Supplier<java.util.Collection<io.lumine.mythic.api.adapters.AbstractPlayer>>, io.lumine.mythic.api.adapters.AbstractEntity, java.lang.String, io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay$PacketTextDisplayBuilder);
  public abstract io.lumine.mythic.api.holograms.IHologram createHologram(java.util.function.Supplier<java.util.Collection<io.lumine.mythic.api.adapters.AbstractPlayer>>, io.lumine.mythic.api.adapters.AbstractLocation, java.lang.String, io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay$PacketTextDisplayBuilder);
  public abstract io.lumine.mythic.api.holograms.IHologram createHologram(io.lumine.mythic.api.adapters.AbstractEntity, java.lang.String, io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay$PacketTextDisplayBuilder);
  public abstract io.lumine.mythic.api.holograms.IHologram createHologram(io.lumine.mythic.api.adapters.AbstractLocation, java.lang.String, io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay$PacketTextDisplayBuilder);
  public abstract io.lumine.mythic.core.holograms.types.Nameplate createNameplate(io.lumine.mythic.core.mobs.ActiveMob);
  public abstract io.lumine.mythic.core.holograms.types.HealthBar createHealthBar(io.lumine.mythic.core.mobs.ActiveMob);
  public abstract io.lumine.mythic.core.holograms.types.SpeechBubble createSpeechBubble(io.lumine.mythic.api.skills.SkillCaster, java.util.function.Supplier<java.util.Collection<io.lumine.mythic.api.adapters.AbstractPlayer>>, io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay$PacketTextDisplayBuilder);
  public abstract io.lumine.mythic.core.holograms.types.CastBar createCastBar(io.lumine.mythic.core.skills.mechanics.CastMechanic$CastTracker, java.lang.String);
  public abstract void registerAttachedHologram(int, int);
  public abstract void unregisterAttachedHologram(int, int);
  public abstract java.util.Collection<java.lang.Integer> getAttachedHolograms(int);
}
