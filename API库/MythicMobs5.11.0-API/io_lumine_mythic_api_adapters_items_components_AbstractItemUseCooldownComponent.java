Compiled from "AbstractItemUseCooldownComponent.java"
public class io.lumine.mythic.api.adapters.items.components.AbstractItemUseCooldownComponent implements io.lumine.mythic.api.adapters.AbstractItemComponent {
  public io.lumine.mythic.api.adapters.items.components.AbstractItemUseCooldownComponent(io.lumine.mythic.core.items.MythicItem, io.lumine.mythic.api.config.MythicConfig);
  public void apply(io.lumine.mythic.core.items.MythicItem, io.lumine.mythic.api.drops.DropMetadata, io.lumine.mythic.api.adapters.AbstractItemStack);
  public org.bukkit.NamespacedKey getCooldownGroup();
  public java.lang.Integer getCooldownSeconds();
  public void setCooldownGroup(org.bukkit.NamespacedKey);
  public void setCooldownSeconds(java.lang.Integer);
  public boolean equals(java.lang.Object);
  public int hashCode();
  public java.lang.String toString();
}
