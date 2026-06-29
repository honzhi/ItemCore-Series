Compiled from "PolymorphicPlaceholder.java"
public interface io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder {
  public static io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder of(java.lang.String);
  public abstract java.lang.String originalValue();
  public abstract java.lang.String getString(io.lumine.mythic.core.skills.placeholders.PlaceholderContext);
  public abstract int getInt(io.lumine.mythic.core.skills.placeholders.PlaceholderContext);
  public abstract float getFloat(io.lumine.mythic.core.skills.placeholders.PlaceholderContext);
  public abstract double getDouble(io.lumine.mythic.core.skills.placeholders.PlaceholderContext);
  public abstract java.lang.Long getLong(io.lumine.mythic.core.skills.placeholders.PlaceholderContext);
  public abstract boolean getBoolean(io.lumine.mythic.core.skills.placeholders.PlaceholderContext);
  public abstract java.util.List<java.lang.String> getList(io.lumine.mythic.core.skills.placeholders.PlaceholderContext);
  public abstract java.util.Map<java.lang.String, java.lang.String> getMap(io.lumine.mythic.core.skills.placeholders.PlaceholderContext);
  public abstract io.lumine.mythic.api.adapters.AbstractVector getVector(io.lumine.mythic.core.skills.placeholders.PlaceholderContext);
  public abstract java.time.Instant getTime(io.lumine.mythic.core.skills.placeholders.PlaceholderContext);
  public abstract io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder cache(io.lumine.mythic.core.skills.variables.VariableType);
}
