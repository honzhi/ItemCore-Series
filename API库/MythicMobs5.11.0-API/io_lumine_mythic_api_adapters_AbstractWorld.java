Compiled from "AbstractWorld.java"
public interface io.lumine.mythic.api.adapters.AbstractWorld {
  public abstract java.util.Collection<io.lumine.mythic.api.adapters.AbstractEntity> getLivingEntities();
  public abstract boolean equals(java.lang.Object);
  public abstract java.lang.String getName();
  public abstract boolean isLoaded();
  public abstract void createExplosion(io.lumine.mythic.api.adapters.AbstractLocation, float);
  public abstract void createExplosion(io.lumine.mythic.api.adapters.AbstractLocation, float, boolean, boolean);
  public abstract java.util.List<io.lumine.mythic.api.adapters.AbstractPlayer> getPlayers();
  public abstract int getPlayerCount();
  public abstract java.util.List<io.lumine.mythic.api.adapters.AbstractPlayer> getPlayersNearLocation(io.lumine.mythic.api.adapters.AbstractLocation, int);
  public abstract java.util.Collection<io.lumine.mythic.api.adapters.AbstractEntity> getEntities();
  public abstract void setStorm(boolean);
  public abstract void setThundering(boolean);
  public abstract void setWeatherDuration(int);
  public abstract boolean playEffect(io.lumine.mythic.api.adapters.AbstractLocation, int);
  public abstract boolean playEffect(io.lumine.mythic.api.adapters.AbstractLocation, int, int);
  public abstract io.lumine.mythic.api.adapters.AbstractLocation getSpawnLocation();
  public abstract io.lumine.mythic.api.adapters.AbstractLocation getHighestBlock(io.lumine.mythic.api.adapters.AbstractLocation);
  public abstract boolean isLocationLoaded(io.lumine.mythic.api.adapters.AbstractLocation);
  public abstract io.lumine.mythic.api.adapters.AbstractBiome getLocationBiome(io.lumine.mythic.api.adapters.AbstractLocation);
  public abstract long getFullTime();
  public abstract boolean isChunkLoaded(int, int);
  public abstract java.util.UUID getUniqueId();
  public abstract int getEntitiesInChunk(int, int);
  public abstract byte getLightLevel(io.lumine.mythic.api.adapters.AbstractLocation);
  public abstract byte getLightLevelFromBlocks(io.lumine.mythic.api.adapters.AbstractLocation);
  public abstract byte getLightLevelFromSky(io.lumine.mythic.api.adapters.AbstractLocation);
  public abstract java.util.Collection<io.lumine.mythic.api.adapters.AbstractPlayer> getPlayersInRadius(io.lumine.mythic.api.adapters.AbstractLocation, double);
  public abstract java.util.Collection<io.lumine.mythic.api.adapters.AbstractEntity> getEntitiesNearLocation(io.lumine.mythic.api.adapters.AbstractLocation, double);
  public abstract java.util.Collection<io.lumine.mythic.api.adapters.AbstractEntity> getEntitiesNearLocation(io.lumine.mythic.api.adapters.AbstractLocation, double, java.util.function.Predicate<io.lumine.mythic.api.adapters.AbstractEntity>);
  public abstract java.util.Optional<io.lumine.mythic.api.adapters.AbstractLocation> getSurfaceLocation(io.lumine.mythic.api.adapters.AbstractLocation, int);
}
