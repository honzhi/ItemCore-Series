Compiled from "StatManager.java"
public interface io.lumine.mythic.api.skills.stats.StatManager {
  public abstract java.util.Optional<io.lumine.mythic.core.skills.stats.StatType> getStat(java.lang.String);
  public abstract java.util.Optional<? extends io.lumine.mythic.api.skills.stats.StatSnapshot> getStatRegistry(io.lumine.mythic.api.adapters.AbstractEntity);
  public abstract java.util.Collection<io.lumine.mythic.core.skills.stats.StatType> getNonZeroBaseStats();
  public static java.lang.String translateLegacyStatAliases(java.lang.String);
}
