Compiled from "VolatileItemHandler.java"
public interface io.lumine.mythic.api.volatilecode.handlers.VolatileItemHandler {
  public abstract org.bukkit.inventory.ItemStack addNBTData(org.bukkit.inventory.ItemStack, java.lang.String, io.lumine.mythic.core.utils.jnbt.Tag);
  public abstract io.lumine.mythic.core.utils.jnbt.CompoundTag getNBTData(org.bukkit.inventory.ItemStack);
  public abstract org.bukkit.inventory.ItemStack setNBTData(org.bukkit.inventory.ItemStack, io.lumine.mythic.core.utils.jnbt.CompoundTag);
  public abstract void destroyItem(org.bukkit.inventory.ItemStack);
  public abstract org.bukkit.inventory.ItemStack setNBTData(org.bukkit.inventory.ItemStack, io.lumine.mythic.core.utils.jnbt.CompoundTag, io.lumine.mythic.core.skills.placeholders.PlaceholderMeta);
  public abstract int spawnFakeItem(org.bukkit.entity.Player, org.bukkit.inventory.ItemStack, io.lumine.mythic.api.adapters.AbstractLocation);
  public abstract void collectFakeItem(org.bukkit.entity.Player, int);
  public abstract void destroyFakeItem(org.bukkit.entity.Player, int);
  public abstract float getItemRecharge(org.bukkit.entity.Player);
  public abstract boolean getItemRecharging(org.bukkit.entity.Player);
  public abstract void resetItemRecharge(org.bukkit.entity.Player);
  public default void setTridentItem(org.bukkit.entity.Trident, org.bukkit.inventory.ItemStack);
  public default java.lang.String serializeItem(org.bukkit.inventory.ItemStack);
  public default java.util.Map<java.lang.String, java.lang.Object> getComponentData(org.bukkit.inventory.ItemStack);
  public default java.lang.String dumpComponentData(org.bukkit.inventory.ItemStack);
  public default java.lang.String dumpNBTData(org.bukkit.inventory.ItemStack);
  public default org.bukkit.inventory.ItemStack setDisplayName(org.bukkit.inventory.ItemStack, net.kyori.adventure.text.Component);
  public default org.bukkit.inventory.ItemStack setLore(org.bukkit.inventory.ItemStack, java.util.Collection<net.kyori.adventure.text.Component>);
  public default org.bukkit.inventory.ItemStack setSkinData(org.bukkit.inventory.ItemStack, java.lang.String, java.util.UUID, java.lang.String, java.lang.String);
  public default org.bukkit.inventory.ItemStack setCanPlaceOn(org.bukkit.inventory.ItemStack, java.util.Collection<org.bukkit.Material>);
  public default org.bukkit.inventory.ItemStack setCanBreak(org.bukkit.inventory.ItemStack, java.util.Collection<org.bukkit.Material>);
  public default java.util.Optional<java.lang.String> getItemBlockStateValue(org.bukkit.inventory.ItemStack, java.lang.String);
  public default void setItemBlockStateValue(org.bukkit.inventory.ItemStack, java.lang.String, java.lang.String);
  public default void setItemBlockStateComponent(org.bukkit.inventory.ItemStack, java.util.Map<java.lang.String, java.lang.String>);
}
