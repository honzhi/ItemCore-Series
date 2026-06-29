Compiled from "AbstractItemToolComponent.java"
public class io.lumine.mythic.api.adapters.items.components.AbstractItemToolComponent implements io.lumine.mythic.api.adapters.AbstractItemComponent {
  public io.lumine.mythic.api.adapters.items.components.AbstractItemToolComponent(io.lumine.mythic.core.items.MythicItem, io.lumine.mythic.api.config.MythicConfig);
  public void apply(io.lumine.mythic.core.items.MythicItem, io.lumine.mythic.api.drops.DropMetadata, io.lumine.mythic.api.adapters.AbstractItemStack);
  public java.lang.Integer getDamagePerBlock();
  public java.lang.Float getDefaultMiningSpeed();
  public java.util.List<io.lumine.mythic.api.adapters.items.components.AbstractItemToolComponent$AbstractToolRule> getRules();
  public void setDamagePerBlock(java.lang.Integer);
  public void setDefaultMiningSpeed(java.lang.Float);
  public void setRules(java.util.List<io.lumine.mythic.api.adapters.items.components.AbstractItemToolComponent$AbstractToolRule>);
  public boolean equals(java.lang.Object);
  public int hashCode();
  public java.lang.String toString();
}
