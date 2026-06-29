Compiled from "AuraManager.java"
public interface io.lumine.mythic.api.skills.auras.AuraManager {
  public abstract io.lumine.mythic.core.skills.auras.AuraRegistry getAuraRegistry(io.lumine.mythic.api.adapters.AbstractEntity);
  public abstract io.lumine.mythic.core.skills.auras.AuraRegistry getAuraRegistry(java.util.UUID);
  public abstract boolean getHasAura(io.lumine.mythic.api.adapters.AbstractEntity, java.lang.String);
  public abstract int getAuraStacks(io.lumine.mythic.api.adapters.AbstractEntity, java.lang.String);
  public abstract void removeAuraStacks(io.lumine.mythic.api.adapters.AbstractEntity, java.lang.String, int);
}
