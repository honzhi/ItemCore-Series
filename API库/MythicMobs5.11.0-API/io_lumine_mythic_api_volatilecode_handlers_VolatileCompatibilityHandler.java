Compiled from "VolatileCompatibilityHandler.java"
public interface io.lumine.mythic.api.volatilecode.handlers.VolatileCompatibilityHandler {
  public default void setLegacyBannerPattern(org.bukkit.block.Banner, org.bukkit.DyeColor, java.lang.String);
  public default void setLegacyBannerPattern(org.bukkit.inventory.meta.BannerMeta, org.bukkit.DyeColor, java.lang.String);
  public default void setCatVariant(org.bukkit.entity.Cat, java.lang.String);
  public default void setWolfVariant(org.bukkit.entity.Wolf, java.lang.String);
}
