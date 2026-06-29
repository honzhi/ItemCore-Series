Compiled from "PlaceholderVector.java"
public interface io.lumine.mythic.api.skills.placeholders.PlaceholderVector extends io.lumine.mythic.api.skills.placeholders.IPlaceholder {
  public static io.lumine.mythic.api.skills.placeholders.PlaceholderVector of(java.lang.String);
  public abstract io.lumine.mythic.api.adapters.AbstractVector get();
  public abstract io.lumine.mythic.api.adapters.AbstractVector get(io.lumine.mythic.core.skills.placeholders.PlaceholderMeta);
  public abstract io.lumine.mythic.api.adapters.AbstractVector get(io.lumine.mythic.api.adapters.AbstractEntity);
  public abstract io.lumine.mythic.api.adapters.AbstractVector get(io.lumine.mythic.core.skills.placeholders.PlaceholderMeta, io.lumine.mythic.api.adapters.AbstractEntity);
  public abstract io.lumine.mythic.api.adapters.AbstractVector get(io.lumine.mythic.api.skills.SkillCaster);
  public abstract io.lumine.mythic.api.adapters.AbstractVector get(io.lumine.mythic.core.skills.placeholders.PlaceholderContext);
  public abstract boolean isStaticallyEqualTo(io.lumine.mythic.api.adapters.AbstractVector);
}
