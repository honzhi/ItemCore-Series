Compiled from "IGenericPlaceholder.java"
public interface io.lumine.mythic.api.skills.placeholders.IGenericPlaceholder<T> extends io.lumine.mythic.api.skills.placeholders.IPlaceholder {
  public abstract T get();
  public abstract T get(io.lumine.mythic.core.skills.placeholders.PlaceholderMeta);
  public abstract T get(io.lumine.mythic.api.adapters.AbstractEntity);
  public abstract T get(io.lumine.mythic.core.skills.placeholders.PlaceholderMeta, io.lumine.mythic.api.adapters.AbstractEntity);
  public abstract T get(io.lumine.mythic.core.skills.placeholders.PlaceholderMeta, io.lumine.mythic.api.adapters.AbstractLocation);
  public abstract T get(io.lumine.mythic.api.skills.SkillCaster);
  public abstract T get(io.lumine.mythic.core.skills.placeholders.PlaceholderContext);
  public abstract boolean isStaticallyEqualTo(T);
}
