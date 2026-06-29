Compiled from "PlaceholderBoolean.java"
public interface io.lumine.mythic.api.skills.placeholders.PlaceholderBoolean extends io.lumine.mythic.api.skills.placeholders.IPlaceholder {
  public static io.lumine.mythic.api.skills.placeholders.PlaceholderBoolean of(java.lang.String);
  public abstract boolean get();
  public abstract boolean get(io.lumine.mythic.core.skills.placeholders.PlaceholderMeta);
  public abstract boolean get(io.lumine.mythic.api.adapters.AbstractEntity);
  public abstract boolean get(io.lumine.mythic.core.skills.placeholders.PlaceholderMeta, io.lumine.mythic.api.adapters.AbstractEntity);
  public abstract boolean get(io.lumine.mythic.api.skills.SkillCaster);
  public abstract boolean get(io.lumine.mythic.core.spawning.spawners.MythicSpawner);
  public abstract boolean get(io.lumine.mythic.core.skills.placeholders.PlaceholderContext);
  public abstract boolean isStaticallyEqualTo(java.lang.Boolean);
}
