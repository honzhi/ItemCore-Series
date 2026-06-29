Compiled from "PacketTextDisplay.java"
public class io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay<T extends io.lumine.mythic.api.volatilecode.virtual.PacketEntityRenderer & io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay$PacketTextDisplayEntityRenderer> extends io.lumine.mythic.api.volatilecode.virtual.PacketEntity {
  public static io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay$PacketTextDisplayBuilder create();
  public io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay(io.lumine.mythic.api.adapters.AbstractLocation);
  public io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay(io.lumine.mythic.api.MythicPlugin, io.lumine.mythic.api.adapters.AbstractLocation);
  public io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay(io.lumine.mythic.api.adapters.AbstractLocation, io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay$PacketTextDisplayBuilder);
  public T getRenderer();
  public void setText(java.lang.String);
  public void setText(java.lang.String[]);
  public void setText(java.util.Collection<java.lang.String>);
  public void setTextComponent(java.util.Collection<net.kyori.adventure.text.Component>);
  public void setTextComponent(net.kyori.adventure.text.Component);
  public void setTextComponent(java.util.function.Function<io.lumine.mythic.api.adapters.AbstractPlayer, net.kyori.adventure.text.Component>);
  public void setScale(io.lumine.mythic.api.adapters.AbstractVector);
  public void setRotation(float, float);
  public void setRotation(float, float, float);
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
  public io.lumine.mythic.bukkit.utils.cache.DataTracker<java.util.function.Function<io.lumine.mythic.api.adapters.AbstractPlayer, net.kyori.adventure.text.Component>> getText();
  public io.lumine.mythic.bukkit.utils.cache.DataTracker<java.lang.Integer> getLineWidth();
  public io.lumine.mythic.bukkit.utils.cache.DataTracker<org.bukkit.Color> getBackgroundColor();
  public io.lumine.mythic.api.volatilecode.virtual.IPacketEntityRenderer getRenderer();
}
