Compiled from "PlaceholderManager.java"
public interface io.lumine.mythic.api.skills.placeholders.PlaceholderManager {
  public abstract void register(java.lang.String[], io.lumine.mythic.core.skills.placeholders.Placeholder);
  public abstract void register(java.lang.String, io.lumine.mythic.core.skills.placeholders.Placeholder);
  public abstract io.lumine.mythic.core.skills.placeholders.PlaceholderExecutor$PlaceholderEntry getPlaceholder(java.lang.String);
  public abstract void registerParser(io.lumine.mythic.core.skills.placeholders.PlaceholderParser);
  public abstract void recheckForPlaceholders();
}
