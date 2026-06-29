Compiled from "VolatileBlockHandler.java"
public interface io.lumine.mythic.api.volatilecode.handlers.VolatileBlockHandler {
  public abstract void sendBlockChange(java.util.Collection<io.lumine.mythic.api.adapters.AbstractPlayer>, io.lumine.mythic.api.adapters.AbstractLocation, io.lumine.mythic.api.adapters.AbstractBlock);
  public abstract void sendMultiBlockChange(java.util.Collection<io.lumine.mythic.api.adapters.AbstractPlayer>, java.util.Map<io.lumine.mythic.api.adapters.AbstractLocation, io.lumine.mythic.api.adapters.AbstractBlock>);
  public abstract void applyPhysics(org.bukkit.block.Block);
  public abstract void togglePowerable(io.lumine.mythic.api.adapters.AbstractLocation);
  public abstract void togglePowerable(io.lumine.mythic.api.adapters.AbstractLocation, long);
  public default void togglePiston(io.lumine.mythic.api.adapters.AbstractLocation);
  public abstract void throwBlock(io.lumine.mythic.api.adapters.AbstractLocation, org.bukkit.Material, io.lumine.mythic.api.adapters.AbstractVector, int, boolean);
  public default boolean pushBlock(io.lumine.mythic.api.adapters.AbstractLocation, io.lumine.mythic.api.adapters.AbstractDirection);
  public default void sendBlockDamaged(java.util.Collection<io.lumine.mythic.api.adapters.AbstractPlayer>, io.lumine.mythic.api.adapters.AbstractLocation, int);
}
