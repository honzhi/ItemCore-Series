Compiled from "AbstractItemPotionComponent.java"
public class io.lumine.mythic.api.adapters.items.components.AbstractItemPotionComponent implements io.lumine.mythic.api.adapters.AbstractItemComponent {
  public io.lumine.mythic.api.adapters.items.components.AbstractItemPotionComponent(io.lumine.mythic.core.items.MythicItem, io.lumine.mythic.api.config.MythicConfig);
  public void apply(io.lumine.mythic.core.items.MythicItem, io.lumine.mythic.api.drops.DropMetadata, io.lumine.mythic.api.adapters.AbstractItemStack);
  public io.lumine.mythic.api.skills.placeholders.PlaceholderString getCustomName();
  public io.lumine.mythic.api.skills.placeholders.PlaceholderColor getPotionColor();
  public java.util.Collection<io.lumine.mythic.bukkit.adapters.BukkitPotionEffect> getEffects();
  public void setCustomName(io.lumine.mythic.api.skills.placeholders.PlaceholderString);
  public void setPotionColor(io.lumine.mythic.api.skills.placeholders.PlaceholderColor);
  public void setEffects(java.util.Collection<io.lumine.mythic.bukkit.adapters.BukkitPotionEffect>);
  public boolean equals(java.lang.Object);
  public int hashCode();
  public java.lang.String toString();
}
