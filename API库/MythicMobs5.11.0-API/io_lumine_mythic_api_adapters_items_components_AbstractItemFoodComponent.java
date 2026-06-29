Compiled from "AbstractItemFoodComponent.java"
public class io.lumine.mythic.api.adapters.items.components.AbstractItemFoodComponent implements io.lumine.mythic.api.adapters.AbstractItemComponent {
  public io.lumine.mythic.api.adapters.items.components.AbstractItemFoodComponent(io.lumine.mythic.core.items.MythicItem, io.lumine.mythic.api.config.MythicConfig);
  public void apply(io.lumine.mythic.core.items.MythicItem, io.lumine.mythic.api.drops.DropMetadata, io.lumine.mythic.api.adapters.AbstractItemStack);
  public java.lang.Boolean getCanAlwaysEat();
  public java.lang.Integer getNutrition();
  public java.lang.Float getSaturation();
  public java.lang.Float getEatSeconds();
  public void setCanAlwaysEat(java.lang.Boolean);
  public void setNutrition(java.lang.Integer);
  public void setSaturation(java.lang.Float);
  public void setEatSeconds(java.lang.Float);
  public boolean equals(java.lang.Object);
  public int hashCode();
  public java.lang.String toString();
}
