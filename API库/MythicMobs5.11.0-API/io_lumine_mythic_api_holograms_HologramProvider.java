Compiled from "HologramProvider.java"
public interface io.lumine.mythic.api.holograms.HologramProvider {
  public abstract io.lumine.mythic.api.holograms.IHologram createHologram(java.lang.String, io.lumine.mythic.api.adapters.AbstractLocation, java.lang.String);
  public default io.lumine.mythic.api.holograms.IHologram createHologram(java.lang.String, io.lumine.mythic.api.adapters.AbstractEntity, java.lang.String, float);
  public default io.lumine.mythic.api.holograms.IHologram createHologram(java.util.function.Supplier<java.util.Collection<io.lumine.mythic.api.adapters.AbstractPlayer>>, io.lumine.mythic.api.adapters.AbstractEntity, java.lang.String, io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay$PacketTextDisplayBuilder);
  public default io.lumine.mythic.api.holograms.IHologram createHologram(java.util.function.Supplier<java.util.Collection<io.lumine.mythic.api.adapters.AbstractPlayer>>, io.lumine.mythic.api.adapters.AbstractLocation, java.lang.String, io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay$PacketTextDisplayBuilder);
  public abstract io.lumine.mythic.api.holograms.IHologram createHologram(io.lumine.mythic.api.adapters.AbstractEntity, java.lang.String, io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay$PacketTextDisplayBuilder);
  public default io.lumine.mythic.api.holograms.IHologram createHologram(io.lumine.mythic.api.adapters.AbstractLocation, java.lang.String, io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay$PacketTextDisplayBuilder);
  public abstract void cleanup();
}
