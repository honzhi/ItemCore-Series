Compiled from "AbstractItemBannerComponent.java"
public class io.lumine.mythic.api.adapters.items.components.AbstractItemBannerComponent implements io.lumine.mythic.api.adapters.AbstractItemComponent {
  public io.lumine.mythic.api.adapters.items.components.AbstractItemBannerComponent(io.lumine.mythic.core.items.MythicItem, io.lumine.mythic.api.config.MythicConfig);
  public void apply(io.lumine.mythic.core.items.MythicItem, io.lumine.mythic.api.drops.DropMetadata, io.lumine.mythic.api.adapters.AbstractItemStack);
  public io.lumine.mythic.api.skills.placeholders.PlaceholderColor getColor();
  public java.util.Collection<java.lang.String> getBannerLayers();
  public void setColor(io.lumine.mythic.api.skills.placeholders.PlaceholderColor);
  public void setBannerLayers(java.util.Collection<java.lang.String>);
  public boolean equals(java.lang.Object);
  public int hashCode();
  public java.lang.String toString();
}
