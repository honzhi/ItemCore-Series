Compiled from "VolatileWorldHandler.java"
public interface io.lumine.mythic.api.volatilecode.handlers.VolatileWorldHandler {
  public default io.lumine.mythic.api.adapters.AbstractBiome getBiome(io.lumine.mythic.api.adapters.AbstractLocation);
  public default java.util.Optional<io.lumine.mythic.api.adapters.AbstractBiome> getBiome(java.lang.String);
  public default java.util.Collection<io.lumine.mythic.api.adapters.AbstractBiome> getBiomes();
  public default java.util.Optional<io.lumine.mythic.api.adapters.AbstractStructure> getStructure(java.lang.String);
  public default java.util.Collection<io.lumine.mythic.api.adapters.AbstractStructure> getStructures();
  public default java.util.Collection<io.lumine.mythic.api.adapters.AbstractStructure> getStructures(io.lumine.mythic.api.adapters.AbstractLocation);
  public abstract void playSoundAtLocation(io.lumine.mythic.api.adapters.AbstractLocation, java.lang.String, float, float, double);
  public default boolean isChunkLoaded(io.lumine.mythic.api.adapters.AbstractWorld, int, int);
  public abstract java.util.Collection<io.lumine.mythic.api.adapters.AbstractEntity> getEntities(io.lumine.mythic.api.adapters.AbstractWorld);
  public abstract java.util.Collection<io.lumine.mythic.api.adapters.AbstractEntity> getLivingEntities(io.lumine.mythic.api.adapters.AbstractWorld);
  public abstract java.util.Collection<io.lumine.mythic.api.adapters.AbstractPlayer> getPlayers(io.lumine.mythic.api.adapters.AbstractWorld);
  public default int getEntitiesInChunk(io.lumine.mythic.api.adapters.AbstractWorld, int, int);
  public abstract float getDifficultyScale(io.lumine.mythic.api.adapters.AbstractLocation);
  public default java.util.Collection<io.lumine.mythic.api.adapters.AbstractEntity> getEntitiesNearLocation(io.lumine.mythic.api.adapters.AbstractLocation, double);
  public default java.util.Collection<io.lumine.mythic.api.adapters.AbstractEntity> getEntitiesNearLocation(io.lumine.mythic.api.adapters.AbstractLocation, double, java.util.function.Predicate<io.lumine.mythic.api.adapters.AbstractEntity>);
  public default org.bukkit.util.RayTraceResult rayTraceBlock(io.lumine.mythic.api.adapters.AbstractLocation, io.lumine.mythic.api.adapters.AbstractVector, double, org.bukkit.FluidCollisionMode, boolean);
  public default org.bukkit.util.RayTraceResult rayTraceBlock(io.lumine.mythic.api.adapters.AbstractLocation, io.lumine.mythic.api.adapters.AbstractLocation, org.bukkit.FluidCollisionMode, boolean);
  public default org.bukkit.util.RayTraceResult rayTrace(io.lumine.mythic.api.adapters.AbstractLocation, io.lumine.mythic.api.adapters.AbstractVector, double, org.bukkit.FluidCollisionMode, double, java.util.function.Predicate<org.bukkit.entity.Entity>, java.util.function.Predicate<org.bukkit.Material>);
  public default org.bukkit.util.RayTraceResult rayTrace(org.bukkit.Location, org.bukkit.util.Vector, double, org.bukkit.FluidCollisionMode, double, java.util.function.Predicate<org.bukkit.entity.Entity>, java.util.function.Predicate<org.bukkit.Material>);
  public default org.bukkit.util.RayTraceResult rayTrace(org.bukkit.Location, org.bukkit.util.Vector, double, double, java.util.function.Predicate<org.bukkit.entity.Entity>, java.util.function.Predicate<org.bukkit.Material>);
  public default org.bukkit.util.RayTraceResult rayTrace(org.bukkit.Location, org.bukkit.Location, org.bukkit.FluidCollisionMode, double, java.util.function.Predicate<org.bukkit.entity.Entity>, java.util.function.Predicate<org.bukkit.Material>);
  public abstract org.bukkit.entity.Entity spawnInvisibleArmorStand(org.bukkit.Location);
  public abstract io.lumine.mythic.api.volatilecode.virtual.PacketArmorStand$PacketArmorStandEntityRenderer createVirtualArmorStandRenderer(io.lumine.mythic.api.volatilecode.virtual.PacketArmorStand);
  public abstract io.lumine.mythic.api.volatilecode.virtual.PacketFallingBlock$PacketFallingBlockEntityRenderer createVirtualBlockRenderer(io.lumine.mythic.api.volatilecode.virtual.PacketFallingBlock);
  public default io.lumine.mythic.api.volatilecode.virtual.PacketBlockDisplay$PacketBlockDisplayEntityRenderer createVirtualDisplayBlockEntityRenderer(io.lumine.mythic.api.volatilecode.virtual.PacketBlockDisplay);
  public default io.lumine.mythic.api.volatilecode.virtual.PacketItemDisplay$PacketItemDisplayEntityRenderer createVirtualDisplayItemEntityRenderer(io.lumine.mythic.api.volatilecode.virtual.PacketItemDisplay);
  public default io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay$PacketTextDisplayEntityRenderer createVirtualTextDisplayEntityRenderer(io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay);
  public abstract io.lumine.mythic.api.volatilecode.virtual.PacketItem$PacketItemEntityRenderer createVirtualItemRenderer(io.lumine.mythic.api.volatilecode.virtual.PacketItem);
}
