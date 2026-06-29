Compiled from "AbstractBlock.java"
public abstract class io.lumine.mythic.api.adapters.AbstractBlock {
  public io.lumine.mythic.api.adapters.AbstractBlock();
  public abstract boolean matches(io.lumine.mythic.api.adapters.AbstractLocation);
  public void set(io.lumine.mythic.api.adapters.AbstractLocation);
  public abstract void set(io.lumine.mythic.api.adapters.AbstractLocation, boolean);
  public boolean isAir();
  public boolean isOccluding();
  public boolean isLiquid();
  public boolean isLava();
  public boolean isWater();
  public abstract io.lumine.mythic.api.volatilecode.virtual.PacketFallingBlock createFakeEntity(io.lumine.mythic.api.adapters.AbstractLocation);
  public abstract org.bukkit.block.data.BlockData getBlockData();
}
