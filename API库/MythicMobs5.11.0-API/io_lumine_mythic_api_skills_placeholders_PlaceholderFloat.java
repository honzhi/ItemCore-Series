Compiled from "PlaceholderFloat.java"
public interface io.lumine.mythic.api.skills.placeholders.PlaceholderFloat extends io.lumine.mythic.api.skills.placeholders.IPlaceholder {
  public static io.lumine.mythic.api.skills.placeholders.PlaceholderFloat of(java.lang.String);
  public abstract float get();
  public abstract float get(io.lumine.mythic.core.skills.placeholders.PlaceholderMeta);
  public abstract float get(io.lumine.mythic.api.adapters.AbstractEntity);
  public abstract float get(io.lumine.mythic.core.skills.placeholders.PlaceholderMeta, io.lumine.mythic.api.adapters.AbstractEntity);
  public abstract float get(io.lumine.mythic.api.skills.SkillCaster);
  public abstract float get(io.lumine.mythic.core.skills.placeholders.PlaceholderContext);
  public abstract boolean isStaticallyEqualTo(float);
}
