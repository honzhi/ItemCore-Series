Compiled from "Skill.java"
public interface io.lumine.mythic.api.skills.Skill {
  public abstract java.lang.String getInternalName();
  public abstract io.lumine.mythic.api.config.MythicConfig getConfig();
  public abstract java.util.Collection<io.lumine.mythic.api.skills.SkillHolder> getParents();
  public abstract void addParent(io.lumine.mythic.api.skills.SkillHolder);
  public abstract boolean isInlineSkill();
  public abstract void execute(io.lumine.mythic.api.skills.SkillTrigger, io.lumine.mythic.api.skills.SkillCaster, io.lumine.mythic.api.adapters.AbstractEntity, io.lumine.mythic.api.adapters.AbstractLocation, java.util.HashSet<io.lumine.mythic.api.adapters.AbstractEntity>, java.util.HashSet<io.lumine.mythic.api.adapters.AbstractLocation>, float);
  public abstract void execute(io.lumine.mythic.api.skills.SkillMetadata);
  public abstract boolean isUsable(io.lumine.mythic.api.skills.SkillMetadata);
  public abstract boolean isUsable(io.lumine.mythic.api.skills.SkillMetadata, io.lumine.mythic.api.skills.SkillTrigger);
  public abstract boolean onCooldown(io.lumine.mythic.api.skills.SkillCaster);
  public abstract java.util.List<io.lumine.mythic.core.skills.SkillCondition> getConditions();
  public abstract java.util.List<io.lumine.mythic.core.skills.SkillCondition> getConditionsTarget();
  public abstract java.util.List<io.lumine.mythic.core.skills.SkillCondition> getConditionsTrigger();
}
