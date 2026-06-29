Compiled from "DropMetadata.java"
public interface io.lumine.mythic.api.drops.DropMetadata extends java.lang.Cloneable,io.lumine.mythic.core.skills.placeholders.PlaceholderMeta {
  public abstract java.util.Optional<io.lumine.mythic.api.skills.SkillCaster> getDropper();
  public abstract java.util.Optional<io.lumine.mythic.api.adapters.AbstractEntity> getCause();
  public abstract float getAmount();
  public abstract void setAmount(float);
  public abstract int getGenerations();
  public abstract int tick();
  public abstract io.lumine.mythic.api.skills.SkillCaster getCaster();
  public abstract io.lumine.mythic.api.adapters.AbstractEntity getTrigger();
  public default io.lumine.mythic.core.skills.placeholders.PlaceholderMeta getExtraPlaceholderMeta();
  public abstract java.util.Optional<org.bukkit.inventory.ItemStack> getItem();
  public abstract void setItem(org.bukkit.inventory.ItemStack);
  public abstract double getLevel();
  public abstract void setLevel(double);
  public abstract io.lumine.mythic.api.drops.DropMetadata clone();
  public abstract io.lumine.mythic.api.drops.DropMetadata deeperClone();
}
