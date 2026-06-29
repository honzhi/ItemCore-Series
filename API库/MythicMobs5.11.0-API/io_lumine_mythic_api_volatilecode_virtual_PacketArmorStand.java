Compiled from "PacketArmorStand.java"
public class io.lumine.mythic.api.volatilecode.virtual.PacketArmorStand<T extends io.lumine.mythic.api.volatilecode.virtual.PacketEntityRenderer & io.lumine.mythic.api.volatilecode.virtual.PacketArmorStand$PacketArmorStandEntityRenderer> extends io.lumine.mythic.api.volatilecode.virtual.PacketEntity {
  public static io.lumine.mythic.api.volatilecode.virtual.PacketArmorStand$PacketArmorStandBuilder create();
  public io.lumine.mythic.api.volatilecode.virtual.PacketArmorStand(io.lumine.mythic.api.adapters.AbstractLocation);
  public io.lumine.mythic.api.volatilecode.virtual.PacketArmorStand(io.lumine.mythic.api.adapters.AbstractLocation, io.lumine.mythic.api.volatilecode.virtual.PacketArmorStand$PacketArmorStandBuilder);
  public io.lumine.mythic.api.volatilecode.virtual.PacketArmorStand(io.lumine.mythic.api.MythicPlugin, io.lumine.mythic.api.adapters.AbstractLocation);
  public void setHeadPose(org.bukkit.util.EulerAngle);
  public void setItem(org.bukkit.inventory.ItemStack);
  public io.lumine.mythic.bukkit.utils.cache.DataTracker<org.bukkit.inventory.ItemStack> getHeadItem();
  public io.lumine.mythic.bukkit.utils.cache.DataTracker<org.bukkit.util.EulerAngle> getRotation();
  public boolean isSmall();
  public boolean isUpsideDown();
}
