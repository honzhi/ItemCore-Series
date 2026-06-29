Compiled from "PlaceholderLong.java"
public interface io.lumine.mythic.api.skills.placeholders.PlaceholderLong extends io.lumine.mythic.api.skills.placeholders.IPlaceholder {
  public static io.lumine.mythic.api.skills.placeholders.PlaceholderLong of(java.lang.String);
  public abstract java.lang.Long get();
  public abstract java.lang.Long get(io.lumine.mythic.core.skills.placeholders.PlaceholderMeta);
  public abstract java.lang.Long get(io.lumine.mythic.api.adapters.AbstractEntity);
  public abstract java.lang.Long get(io.lumine.mythic.core.skills.placeholders.PlaceholderMeta, io.lumine.mythic.api.adapters.AbstractEntity);
  public abstract java.lang.Long get(io.lumine.mythic.api.skills.SkillCaster);
  public abstract java.lang.Long get(io.lumine.mythic.core.spawning.spawners.MythicSpawner);
  public abstract java.lang.Long get(io.lumine.mythic.core.skills.placeholders.PlaceholderContext);
  public abstract boolean isStaticallyEqualTo(java.lang.Long);
}
