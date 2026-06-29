Compiled from "PacketItem.java"
public class io.lumine.mythic.api.volatilecode.virtual.PacketItem<T extends io.lumine.mythic.api.volatilecode.virtual.PacketEntityRenderer & io.lumine.mythic.api.volatilecode.virtual.PacketItem$PacketItemEntityRenderer> extends io.lumine.mythic.api.volatilecode.virtual.PacketEntity {
  public io.lumine.mythic.bukkit.utils.cache.DataTracker<org.bukkit.inventory.ItemStack> item;
  public io.lumine.mythic.api.volatilecode.virtual.PacketItem(io.lumine.mythic.api.adapters.AbstractLocation);
  public io.lumine.mythic.api.volatilecode.virtual.PacketItem(io.lumine.mythic.api.MythicPlugin, io.lumine.mythic.api.adapters.AbstractLocation);
  public void setItem(org.bukkit.inventory.ItemStack);
  public T getRenderer();
  public void collect(io.lumine.mythic.api.adapters.AbstractPlayer);
  public io.lumine.mythic.bukkit.utils.cache.DataTracker<org.bukkit.inventory.ItemStack> getItem();
  public io.lumine.mythic.api.volatilecode.virtual.IPacketEntityRenderer getRenderer();
}
