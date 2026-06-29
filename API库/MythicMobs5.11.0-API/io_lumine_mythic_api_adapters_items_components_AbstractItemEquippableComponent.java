Compiled from "AbstractItemEquippableComponent.java"
public class io.lumine.mythic.api.adapters.items.components.AbstractItemEquippableComponent implements io.lumine.mythic.api.adapters.AbstractItemComponent {
  public io.lumine.mythic.api.adapters.items.components.AbstractItemEquippableComponent(io.lumine.mythic.core.items.MythicItem);
  public io.lumine.mythic.api.adapters.items.components.AbstractItemEquippableComponent(io.lumine.mythic.core.items.MythicItem, io.lumine.mythic.api.config.MythicConfig);
  public void apply(io.lumine.mythic.core.items.MythicItem, io.lumine.mythic.api.drops.DropMetadata, io.lumine.mythic.api.adapters.AbstractItemStack);
  public org.bukkit.NamespacedKey getModel();
  public org.bukkit.inventory.EquipmentSlot getSlot();
  public java.lang.String getEquipSound();
  public org.bukkit.NamespacedKey getCameraOverlay();
  public java.util.List<java.lang.String> getEntityTypes();
  public java.lang.Boolean getDispensable();
  public java.lang.Boolean getSwappable();
  public java.lang.Boolean getDamageOnHurt();
  public void setModel(org.bukkit.NamespacedKey);
  public void setSlot(org.bukkit.inventory.EquipmentSlot);
  public void setEquipSound(java.lang.String);
  public void setCameraOverlay(org.bukkit.NamespacedKey);
  public void setEntityTypes(java.util.List<java.lang.String>);
  public void setDispensable(java.lang.Boolean);
  public void setSwappable(java.lang.Boolean);
  public void setDamageOnHurt(java.lang.Boolean);
  public boolean equals(java.lang.Object);
  public int hashCode();
  public java.lang.String toString();
}
