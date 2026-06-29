Compiled from "SkillManager.java"
public interface io.lumine.mythic.api.skills.SkillManager {
  public abstract io.lumine.mythic.api.MythicPlugin getPlugin();
  public abstract io.lumine.mythic.api.skills.auras.AuraManager getAuraManager();
  public abstract io.lumine.mythic.api.skills.SkillEventBus getEventBus();
  public abstract void queueSecondPass(java.lang.Runnable);
  public abstract void queueAfterLoad(java.lang.Runnable);
  public abstract io.lumine.mythic.api.skills.SkillCaster getCaster(io.lumine.mythic.api.adapters.AbstractEntity);
  public abstract void registerSkill(java.lang.String, io.lumine.mythic.api.skills.Skill);
  public abstract java.util.Optional<io.lumine.mythic.api.skills.Skill> getSkill(java.lang.String);
  public abstract java.util.Optional<io.lumine.mythic.api.skills.Skill> getSkill(java.io.File, java.lang.String);
  public abstract java.util.Optional<io.lumine.mythic.api.skills.Skill> getSkill(java.io.File, io.lumine.mythic.api.skills.SkillHolder, java.lang.String);
  public abstract java.util.Collection<java.lang.String> getSkillNames();
  public abstract java.util.Collection<io.lumine.mythic.api.skills.Skill> getSkills();
  public abstract void setSkillCooldown(io.lumine.mythic.api.skills.SkillCaster, io.lumine.mythic.core.skills.AbstractSkill, double);
  public abstract boolean isSkillOnCooldown(io.lumine.mythic.api.skills.SkillCaster, io.lumine.mythic.core.skills.AbstractSkill);
  public abstract void clearSkillCooldowns(io.lumine.mythic.api.skills.SkillCaster);
  public abstract long getSkillCooldownRemainingMillis(io.lumine.mythic.api.skills.SkillCaster, io.lumine.mythic.core.skills.AbstractSkill);
  public abstract void removeSkillCooldown(io.lumine.mythic.api.skills.SkillCaster, io.lumine.mythic.core.skills.AbstractSkill);
  public abstract void purgeExpiredCooldowns();
  public abstract io.lumine.mythic.core.skills.SkillMechanic getMechanic(java.lang.String);
  public abstract io.lumine.mythic.core.skills.SkillTargeter getTargeter(java.lang.String, io.lumine.mythic.api.config.MythicLineConfig);
  public abstract io.lumine.mythic.core.skills.SkillTargeter getTargeter(java.lang.String);
  public abstract io.lumine.mythic.api.skills.SkillMetadata processTargets(io.lumine.mythic.api.skills.SkillMetadata, io.lumine.mythic.core.skills.SkillTargeter);
  public abstract io.lumine.mythic.api.skills.SkillMetadata processTargets(io.lumine.mythic.api.skills.SkillMetadata, io.lumine.mythic.core.skills.SkillTargeter, boolean, boolean);
  public abstract io.lumine.mythic.api.adapters.AbstractLocation getLocationTarget(io.lumine.mythic.core.skills.SkillTargeter, io.lumine.mythic.api.skills.SkillMetadata);
  public abstract io.lumine.mythic.core.skills.SkillCondition getCondition(java.lang.String);
  public abstract java.util.List<io.lumine.mythic.core.skills.SkillCondition> getConditions(java.util.List<java.lang.String>);
  public abstract java.util.List<io.lumine.mythic.core.skills.SkillCondition> getConditions(java.lang.String);
  public abstract io.lumine.mythic.core.skills.SkillAudience getAudience(java.lang.String);
}
