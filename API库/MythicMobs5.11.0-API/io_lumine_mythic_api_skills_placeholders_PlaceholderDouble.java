Compiled from "PlaceholderDouble.java"
public interface io.lumine.mythic.api.skills.placeholders.PlaceholderDouble extends io.lumine.mythic.api.skills.placeholders.IPlaceholder {
  public static io.lumine.mythic.api.skills.placeholders.PlaceholderDouble of(java.lang.String);
  public abstract double get();
  public abstract double get(io.lumine.mythic.core.skills.placeholders.PlaceholderMeta);
  public abstract double get(io.lumine.mythic.api.adapters.AbstractEntity);
  public abstract double get(io.lumine.mythic.core.skills.placeholders.PlaceholderMeta, io.lumine.mythic.api.adapters.AbstractEntity);
  public abstract double get(io.lumine.mythic.api.skills.SkillCaster);
  public abstract double get(io.lumine.mythic.core.skills.placeholders.PlaceholderContext);
  public abstract boolean isStaticallyEqualTo(double);
}
