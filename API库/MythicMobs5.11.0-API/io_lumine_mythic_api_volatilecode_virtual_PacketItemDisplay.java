Compiled from "PacketItemDisplay.java"
public class io.lumine.mythic.api.volatilecode.virtual.PacketItemDisplay<T extends io.lumine.mythic.api.volatilecode.virtual.PacketEntityRenderer & io.lumine.mythic.api.volatilecode.virtual.PacketItemDisplay$PacketItemDisplayEntityRenderer> extends io.lumine.mythic.api.volatilecode.virtual.PacketEntity {
  public static io.lumine.mythic.api.volatilecode.virtual.PacketItemDisplay$PacketItemDisplayBuilder create();
  public io.lumine.mythic.api.volatilecode.virtual.PacketItemDisplay(io.lumine.mythic.api.adapters.AbstractLocation);
  public io.lumine.mythic.api.volatilecode.virtual.PacketItemDisplay(io.lumine.mythic.api.adapters.AbstractLocation, io.lumine.mythic.api.volatilecode.virtual.PacketItemDisplay$PacketItemDisplayBuilder);
  public io.lumine.mythic.api.volatilecode.virtual.PacketItemDisplay(io.lumine.mythic.api.MythicPlugin, io.lumine.mythic.api.adapters.AbstractLocation);
  public void setItem(org.bukkit.inventory.ItemStack);
  public void setRotation(float, float);
  public void setRotationTransformation(float, float, float);
  public void setTranslationTransformation(float, float, float);
  public void setScale(io.lumine.mythic.api.adapters.AbstractVector);
  public void setInterpolationDelay(int);
  public void setInterpolationDelayForced(int);
  public void setInterpolationDuration(int);
  public void setBrightness(org.bukkit.entity.Display$Brightness);
  public io.lumine.mythic.bukkit.utils.cache.DataTracker<java.lang.Integer> getInterpolationDelay();
  public io.lumine.mythic.bukkit.utils.cache.DataTracker<java.lang.Integer> getInterpolationDuration();
  public io.lumine.mythic.bukkit.utils.cache.DataTracker<java.lang.Float> getViewRange();
  public io.lumine.mythic.bukkit.utils.cache.DataTracker<java.lang.Float> getHeight();
  public io.lumine.mythic.bukkit.utils.cache.DataTracker<java.lang.Float> getWidth();
  public io.lumine.mythic.bukkit.utils.cache.DataTracker<org.bukkit.entity.Display$Billboard> getBillboard();
  public io.lumine.mythic.bukkit.utils.cache.DataTracker<java.lang.Integer> getBrightness();
  public io.lumine.mythic.bukkit.utils.cache.DataTracker<org.joml.Vector3f> getScale();
  public io.lumine.mythic.bukkit.utils.cache.DataTracker<org.joml.Vector3f> getTranslation();
  public io.lumine.mythic.bukkit.utils.cache.DataTracker<org.joml.Quaternionf> getRotationLeft();
  public io.lumine.mythic.bukkit.utils.cache.DataTracker<org.joml.Quaternionf> getRotationRight();
  public io.lumine.mythic.bukkit.utils.cache.DataTracker<org.bukkit.inventory.ItemStack> getItem();
  public io.lumine.mythic.bukkit.utils.cache.DataTracker<org.bukkit.entity.ItemDisplay$ItemDisplayTransform> getDisplayTransform();
}
