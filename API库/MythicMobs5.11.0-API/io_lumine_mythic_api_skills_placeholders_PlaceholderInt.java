Compiled from "PlaceholderInt.java"
public interface io.lumine.mythic.api.skills.placeholders.PlaceholderInt extends io.lumine.mythic.api.skills.placeholders.IPlaceholder {
  public static io.lumine.mythic.api.skills.placeholders.PlaceholderInt of(java.lang.String);
  public abstract int get();
  public abstract int get(io.lumine.mythic.core.skills.placeholders.PlaceholderMeta);
  public abstract int get(io.lumine.mythic.api.adapters.AbstractEntity);
  public abstract int get(io.lumine.mythic.core.skills.placeholders.PlaceholderMeta, io.lumine.mythic.api.adapters.AbstractEntity);
  public abstract int get(io.lumine.mythic.api.skills.SkillCaster);
  public abstract int get(io.lumine.mythic.core.spawning.spawners.MythicSpawner);
  public abstract int get(io.lumine.mythic.core.skills.placeholders.PlaceholderContext);
  public abstract boolean isStaticallyEqualTo(int);
}
