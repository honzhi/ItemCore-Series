Compiled from "ItemManager.java"
public interface io.lumine.mythic.api.items.ItemManager {
  public abstract void registerItemSupplier(io.lumine.mythic.api.items.ItemSupplier);
  public abstract java.util.Collection<io.lumine.mythic.core.items.MythicItem> getItems();
  public abstract java.util.Collection<java.lang.String> getItemNames();
  public abstract java.util.Optional<io.lumine.mythic.core.items.MythicItem> getItem(java.lang.String);
  public abstract boolean isMythicItem(org.bukkit.inventory.ItemStack);
  public abstract java.lang.String getMythicTypeFromItem(org.bukkit.inventory.ItemStack);
  public abstract java.util.Collection<io.lumine.mythic.core.items.MythicItem> filterItems(java.util.Collection<io.lumine.mythic.core.items.MythicItem>, java.lang.String);
}
