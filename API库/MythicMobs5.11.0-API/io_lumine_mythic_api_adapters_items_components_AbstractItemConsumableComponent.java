Compiled from "AbstractItemConsumableComponent.java"
public class io.lumine.mythic.api.adapters.items.components.AbstractItemConsumableComponent implements io.lumine.mythic.api.adapters.AbstractItemComponent {
  public io.lumine.mythic.api.adapters.items.components.AbstractItemConsumableComponent(io.lumine.mythic.core.items.MythicItem, io.lumine.mythic.api.config.MythicConfig);
  public void apply(io.lumine.mythic.core.items.MythicItem, io.lumine.mythic.api.drops.DropMetadata, io.lumine.mythic.api.adapters.AbstractItemStack);
  public java.lang.Float getConsumeSeconds();
  public java.lang.Boolean getHasConsumeParticles();
  public java.lang.String getAnimation();
  public java.lang.String getSound();
  public void setConsumeSeconds(java.lang.Float);
  public void setHasConsumeParticles(java.lang.Boolean);
  public void setAnimation(java.lang.String);
  public void setSound(java.lang.String);
  public boolean equals(java.lang.Object);
  public int hashCode();
  public java.lang.String toString();
}
