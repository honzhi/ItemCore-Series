Compiled from "AbstractItemTrimComponent.java"
public class io.lumine.mythic.api.adapters.items.components.AbstractItemTrimComponent implements io.lumine.mythic.api.adapters.AbstractItemComponent {
  public io.lumine.mythic.api.adapters.items.components.AbstractItemTrimComponent(io.lumine.mythic.core.items.MythicItem, io.lumine.mythic.api.config.MythicConfig);
  public io.lumine.mythic.api.adapters.items.components.AbstractItemTrimComponent(io.lumine.mythic.core.items.MythicItem, java.lang.String, java.lang.String);
  public void apply(io.lumine.mythic.core.items.MythicItem, io.lumine.mythic.api.drops.DropMetadata, io.lumine.mythic.api.adapters.AbstractItemStack);
  public void setTrimMaterial(io.lumine.mythic.api.skills.placeholders.PlaceholderString);
  public void setTrimPattern(io.lumine.mythic.api.skills.placeholders.PlaceholderString);
  public boolean equals(java.lang.Object);
  public int hashCode();
  public java.lang.String toString();
  public io.lumine.mythic.api.skills.placeholders.PlaceholderString getTrimMaterial();
  public io.lumine.mythic.api.skills.placeholders.PlaceholderString getTrimPattern();
}
