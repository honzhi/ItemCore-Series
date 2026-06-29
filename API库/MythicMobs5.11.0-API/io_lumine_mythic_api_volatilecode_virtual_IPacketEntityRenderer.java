Compiled from "IPacketEntityRenderer.java"
public interface io.lumine.mythic.api.volatilecode.virtual.IPacketEntityRenderer<T extends io.lumine.mythic.api.volatilecode.virtual.PacketEntity> {
  public abstract void spawnWithMount(io.lumine.mythic.api.adapters.AbstractPlayer, io.lumine.mythic.api.volatilecode.virtual.PacketEntityRenderer);
  public abstract void spawn();
  public abstract void spawn(java.util.function.Supplier<java.util.Collection<io.lumine.mythic.api.adapters.AbstractPlayer>>);
  public abstract void spawn(io.lumine.mythic.api.adapters.AbstractWorld);
  public abstract void spawn(java.util.Collection<io.lumine.mythic.api.adapters.AbstractPlayer>);
  public abstract void spawn(io.lumine.mythic.api.adapters.AbstractPlayer);
  public abstract void spawnWithMount(java.util.function.Supplier<java.util.Collection<io.lumine.mythic.api.adapters.AbstractPlayer>>, io.lumine.mythic.api.volatilecode.virtual.PacketEntityRenderer);
  public abstract void spawnWithMount(java.util.Collection<io.lumine.mythic.api.adapters.AbstractPlayer>, io.lumine.mythic.api.volatilecode.virtual.PacketEntityRenderer);
  public abstract void spawnWithMount(java.util.function.Supplier<java.util.Collection<io.lumine.mythic.api.adapters.AbstractPlayer>>, io.lumine.mythic.api.adapters.AbstractPlayer);
  public abstract void spawnWithMount(io.lumine.mythic.api.adapters.AbstractPlayer);
  public abstract void setCullingDistance(int);
  public abstract void update();
  public abstract void destroy(io.lumine.mythic.api.adapters.AbstractPlayer);
  public abstract void destroy();
  public abstract void mountEntity(io.lumine.mythic.api.adapters.AbstractEntity);
  public abstract void addPassenger(io.lumine.mythic.api.volatilecode.virtual.PacketEntityRenderer);
  public abstract void setMount(int);
  public abstract int getEntityId();
  public abstract java.util.UUID getUniqueId();
  public abstract T getWrapper();
  public abstract java.util.Map<io.lumine.mythic.api.adapters.AbstractPlayer, java.lang.Boolean> getTrackedPlayers();
  public default void updateRenderedPlayers();
  public abstract void updateRenderedPlayers(boolean);
}
