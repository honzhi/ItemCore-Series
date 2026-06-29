Compiled from "PlaceholderString.java"
public interface io.lumine.mythic.api.skills.placeholders.PlaceholderString extends io.lumine.mythic.api.skills.placeholders.IPlaceholder {
  public abstract java.lang.String get(io.lumine.mythic.core.skills.placeholders.PlaceholderMeta, io.lumine.mythic.api.adapters.AbstractItemStack);
  public static io.lumine.mythic.api.skills.placeholders.PlaceholderString of(java.lang.String);
  public abstract java.lang.String get();
  public abstract java.lang.String get(io.lumine.mythic.core.skills.placeholders.PlaceholderMeta);
  public abstract java.lang.String get(io.lumine.mythic.api.adapters.AbstractEntity);
  public abstract java.lang.String get(io.lumine.mythic.core.skills.placeholders.PlaceholderMeta, io.lumine.mythic.api.adapters.AbstractEntity);
  public abstract java.lang.String get(io.lumine.mythic.core.skills.placeholders.PlaceholderMeta, io.lumine.mythic.api.adapters.AbstractLocation);
  public abstract java.lang.String get(io.lumine.mythic.api.skills.SkillCaster);
  public abstract java.lang.String get(io.lumine.mythic.core.skills.placeholders.PlaceholderContext);
  public abstract boolean isStaticallyEqualTo(java.lang.String);
}
