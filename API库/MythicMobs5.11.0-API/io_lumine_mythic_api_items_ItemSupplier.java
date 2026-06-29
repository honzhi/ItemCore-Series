Compiled from "ItemSupplier.java"
public interface io.lumine.mythic.api.items.ItemSupplier {
  public abstract java.lang.String getNamespace();
  public abstract org.bukkit.inventory.ItemStack getItem(java.lang.String);
  public abstract boolean isSimilar(java.lang.String, org.bukkit.inventory.ItemStack);
  public default io.lumine.mythic.api.adapters.AbstractItemStack getMythicItem(java.lang.String);
  public abstract java.util.Collection<java.lang.String> getAvailableItemNames();
}
