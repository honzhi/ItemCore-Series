Compiled from "MythicSpawnState.java"
public interface io.lumine.mythic.api.spawning.MythicSpawnState {
  public abstract io.lumine.mythic.api.adapters.AbstractWorld getWorld();
  public abstract int getSpawnableChunkCount();
  public abstract int getMaxMobCount();
  public abstract int getMaxMobsPerPlayer();
  public abstract int getCurrentMobCount(io.lumine.mythic.api.mobs.MobSpawnCategory);
  public abstract int getMonsterCount();
  public abstract java.util.Collection<io.lumine.mythic.api.mobs.MobSpawnCategory> getCategories();
  public abstract java.util.Map<io.lumine.mythic.api.mobs.MobSpawnCategory, java.lang.Integer> getCurrentMobCounts();
}
