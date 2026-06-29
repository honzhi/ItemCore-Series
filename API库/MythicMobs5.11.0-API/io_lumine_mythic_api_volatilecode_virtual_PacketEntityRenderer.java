Compiled from "PacketEntityRenderer.java"
public abstract class io.lumine.mythic.api.volatilecode.virtual.PacketEntityRenderer<T extends io.lumine.mythic.api.volatilecode.virtual.PacketEntity<?>> implements io.lumine.mythic.api.volatilecode.virtual.IPacketEntityRenderer<T> {
  public io.lumine.mythic.api.volatilecode.virtual.PacketEntityRenderer(T);
  public void spawn(io.lumine.mythic.api.adapters.AbstractPlayer);
  public void spawn();
  public void spawn(java.util.function.Supplier<java.util.Collection<io.lumine.mythic.api.adapters.AbstractPlayer>>);
  public void spawn(io.lumine.mythic.api.adapters.AbstractWorld);
  public void spawn(java.util.Collection<io.lumine.mythic.api.adapters.AbstractPlayer>);
  public void spawnWithMount(io.lumine.mythic.api.adapters.AbstractPlayer, io.lumine.mythic.api.volatilecode.virtual.PacketEntityRenderer);
  public void spawnWithMount(java.util.function.Supplier<java.util.Collection<io.lumine.mythic.api.adapters.AbstractPlayer>>, io.lumine.mythic.api.volatilecode.virtual.PacketEntityRenderer);
  public void spawnWithMount(java.util.Collection<io.lumine.mythic.api.adapters.AbstractPlayer>, io.lumine.mythic.api.volatilecode.virtual.PacketEntityRenderer);
  public void spawnWithMount(java.util.function.Supplier<java.util.Collection<io.lumine.mythic.api.adapters.AbstractPlayer>>, io.lumine.mythic.api.adapters.AbstractPlayer);
  public void spawnWithMount(io.lumine.mythic.api.adapters.AbstractPlayer);
  public void setCullingDistance(int);
  public void updateRenderedPlayers(boolean);
  public void sendPacket(java.util.Collection<io.lumine.mythic.api.adapters.AbstractPlayer>, java.util.Collection<java.lang.Object>);
  public void sendPacket(java.lang.Object...);
  public void sendPacket(io.lumine.mythic.api.adapters.AbstractPlayer, java.util.Collection<java.lang.Object>);
  public abstract void sendPacket(io.lumine.mythic.api.adapters.AbstractPlayer, java.lang.Object);
  public void update();
  public void destroy();
  public void destroy(io.lumine.mythic.api.adapters.AbstractPlayer);
  public void mountEntity(io.lumine.mythic.api.adapters.AbstractEntity);
  public void addPassenger(io.lumine.mythic.api.volatilecode.virtual.PacketEntityRenderer);
  public int getEntityId();
  public java.util.UUID getUniqueId();
  public T getWrapper();
  public void setViewers(java.util.function.Supplier<java.util.Collection<io.lumine.mythic.api.adapters.AbstractPlayer>>);
  public it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<io.lumine.mythic.api.adapters.AbstractPlayer, java.lang.Boolean> getTrackedPlayers();
  public void setTrackedPlayers(it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<io.lumine.mythic.api.adapters.AbstractPlayer, java.lang.Boolean>);
  public java.lang.Boolean getRenderDataPerPlayer();
  public void setRenderDataPerPlayer(java.lang.Boolean);
  public void setMount(int);
  public java.util.Map getTrackedPlayers();
}
