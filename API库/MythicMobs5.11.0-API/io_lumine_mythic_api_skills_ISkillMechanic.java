Compiled from "ISkillMechanic.java"
public interface io.lumine.mythic.api.skills.ISkillMechanic {
  public default io.lumine.mythic.api.MythicPlugin getPlugin();
  public default io.lumine.mythic.api.skills.ThreadSafetyLevel getThreadSafetyLevel();
  public default boolean getTargetsSpectators();
  public default boolean getTargetsCreatives();
}
