Compiled from "AbstractItemSpawnerComponent.java"
public class io.lumine.mythic.api.adapters.items.components.AbstractItemSpawnerComponent implements io.lumine.mythic.api.adapters.AbstractItemComponent {
  public io.lumine.mythic.api.adapters.items.components.AbstractItemSpawnerComponent(io.lumine.mythic.core.items.MythicItem, io.lumine.mythic.api.config.MythicConfig);
  public void apply(io.lumine.mythic.core.items.MythicItem, io.lumine.mythic.api.drops.DropMetadata, io.lumine.mythic.api.adapters.AbstractItemStack);
  public java.lang.Integer getDelay();
  public java.lang.Integer getMaxSpawnDelay();
  public java.lang.Integer getMinSpawnDelay();
  public java.lang.Integer getRequiredPlayerRange();
  public java.lang.Integer getSpawnCount();
  public java.lang.Integer getSpawnRange();
  public java.lang.Integer getMaxNearbyEntities();
  public java.util.Collection<io.lumine.mythic.api.adapters.items.components.AbstractItemSpawnerComponent$PotentialSpawns> getPotentialSpawns();
  public void setDelay(java.lang.Integer);
  public void setMaxSpawnDelay(java.lang.Integer);
  public void setMinSpawnDelay(java.lang.Integer);
  public void setRequiredPlayerRange(java.lang.Integer);
  public void setSpawnCount(java.lang.Integer);
  public void setSpawnRange(java.lang.Integer);
  public void setMaxNearbyEntities(java.lang.Integer);
  public void setPotentialSpawns(java.util.Collection<io.lumine.mythic.api.adapters.items.components.AbstractItemSpawnerComponent$PotentialSpawns>);
  public boolean equals(java.lang.Object);
  public int hashCode();
  public java.lang.String toString();
}
