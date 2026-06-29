Compiled from "ILocationTargeter.java"
public interface io.lumine.mythic.api.skills.targeters.ILocationTargeter extends io.lumine.mythic.api.skills.targeters.ISkillTargeter {
  public abstract java.util.Collection<io.lumine.mythic.api.adapters.AbstractLocation> getLocations(io.lumine.mythic.api.skills.SkillMetadata);
  public default boolean useLocationMutators();
}
