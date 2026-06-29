Compiled from "IEntityTargeter.java"
public interface io.lumine.mythic.api.skills.targeters.IEntityTargeter extends io.lumine.mythic.api.skills.targeters.ISkillTargeter {
  public abstract java.util.Collection<io.lumine.mythic.api.adapters.AbstractEntity> getEntities(io.lumine.mythic.api.skills.SkillMetadata);
  public default io.lumine.mythic.core.skills.targeters.IEntitySelector$TargetMode getFilter(io.lumine.mythic.core.skills.targeters.IEntitySelector$TargetFilter);
}
