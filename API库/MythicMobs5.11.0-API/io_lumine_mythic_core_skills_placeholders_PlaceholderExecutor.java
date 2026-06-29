Compiled from "PlaceholderExecutor.java"
public class io.lumine.mythic.core.skills.placeholders.PlaceholderExecutor extends io.lumine.mythic.bukkit.utils.plugin.ReloadableModule<io.lumine.mythic.bukkit.MythicBukkit> implements io.lumine.mythic.api.skills.placeholders.PlaceholderManager {
  public io.lumine.mythic.core.skills.placeholders.PlaceholderExecutor(io.lumine.mythic.bukkit.MythicBukkit);
  public void unload();
  public void load(io.lumine.mythic.bukkit.MythicBukkit);
  public void register(java.lang.String[], io.lumine.mythic.core.skills.placeholders.Placeholder);
  public void register(java.lang.String, io.lumine.mythic.core.skills.placeholders.Placeholder);
  public void register(java.lang.String[], java.lang.Class<io.lumine.mythic.core.skills.placeholders.Placeholder>);
  public void register(java.lang.String, java.lang.Class<io.lumine.mythic.core.skills.placeholders.Placeholder>);
  public io.lumine.mythic.core.skills.placeholders.PlaceholderExecutor$PlaceholderEntry getPlaceholder(java.lang.String);
  public void registerParser(io.lumine.mythic.core.skills.placeholders.PlaceholderParser);
  public void recheckForPlaceholders();
  public static java.util.List<java.lang.String> parsePlaceholders(java.lang.String);
  public io.lumine.mythic.core.skills.placeholders.StaticPlaceholderRegistry getStaticPlaceholderRegistry();
  public java.util.List<io.lumine.mythic.core.skills.placeholders.PlaceholderParser> getParsers();
  public boolean isAcceptingRegisteredParsers();
  public void setAcceptingRegisteredParsers(boolean);
  public void load(io.lumine.mythic.bukkit.utils.plugin.LuminePlugin);
}
