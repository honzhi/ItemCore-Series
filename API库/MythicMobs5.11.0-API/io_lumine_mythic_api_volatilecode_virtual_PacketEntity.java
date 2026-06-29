Compiled from "PacketEntity.java"
public abstract class io.lumine.mythic.api.volatilecode.virtual.PacketEntity<T extends io.lumine.mythic.api.volatilecode.virtual.IPacketEntityRenderer> {
  public io.lumine.mythic.api.volatilecode.virtual.PacketEntity(io.lumine.mythic.api.adapters.AbstractLocation);
  public void update();
  public void destroy();
  public int getEntityId();
  public io.lumine.mythic.bukkit.utils.cache.DataTracker<org.joml.Vector3dc> getLocation();
  public void setPosition(io.lumine.mythic.api.adapters.AbstractLocation);
  public void teleport(io.lumine.mythic.api.adapters.AbstractLocation);
  public void setVelocity(io.lumine.mythic.api.adapters.AbstractVector);
  public void setHasGravity(java.lang.Boolean);
  public void setDirty();
  public void clearDirty();
  public T getRenderer();
  public boolean isDirty();
  public io.lumine.mythic.api.adapters.AbstractWorld getWorld();
  public io.lumine.mythic.bukkit.utils.cache.DataTracker<java.lang.Float> getYaw();
  public io.lumine.mythic.bukkit.utils.cache.DataTracker<java.lang.Float> getPitch();
  public io.lumine.mythic.api.adapters.AbstractLocation getPreviousLocation();
  public io.lumine.mythic.bukkit.utils.cache.DataTracker<java.lang.Boolean> getHasGravity();
}
