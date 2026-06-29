Compiled from "ConfigManager.java"
public interface io.lumine.mythic.api.config.ConfigManager {
  public abstract java.io.File getConfigFolder();
  public abstract int getDebugLevel();
  public abstract void setDebugLevel(int);
  public abstract boolean isDebugMode();
  public abstract io.lumine.mythic.api.config.MythicConfig createConfig(java.io.File);
  public abstract io.lumine.mythic.api.config.MythicConfig createConfig(org.bukkit.configuration.file.FileConfiguration);
  public abstract io.lumine.mythic.api.config.MythicConfig createConfig(java.lang.String, org.bukkit.configuration.file.FileConfiguration);
  public abstract io.lumine.mythic.api.config.MythicConfig createConfig(java.lang.String, java.io.File);
  public abstract io.lumine.mythic.api.config.MythicConfig createConfig(java.lang.String, java.io.File, org.bukkit.configuration.file.FileConfiguration);
  public abstract io.lumine.mythic.api.skills.placeholders.PlaceholderTime createPlaceholderTime(java.lang.String);
  public abstract io.lumine.mythic.api.skills.placeholders.PlaceholderVector createPlaceholderVector(java.lang.String);
  public abstract io.lumine.mythic.api.config.MythicLineConfig createLineConfig(java.lang.String);
  public abstract io.lumine.mythic.api.config.MythicLineConfig createLineConfig(java.io.File, java.lang.String);
  public abstract io.lumine.mythic.api.skills.placeholders.PlaceholderAngle createPlaceholderAngle(java.lang.String, io.lumine.mythic.bukkit.utils.numbers.AngleUnit);
  public abstract io.lumine.mythic.api.skills.placeholders.PlaceholderBoolean createPlaceholderBoolean(java.lang.String);
  public abstract io.lumine.mythic.api.skills.placeholders.PlaceholderColor createPlaceholderColor(java.lang.String);
  public abstract io.lumine.mythic.api.skills.placeholders.PlaceholderDouble createPlaceholderDouble(java.lang.String);
  public abstract io.lumine.mythic.api.skills.placeholders.PlaceholderFloat createPlaceholderFloat(java.lang.String);
  public abstract io.lumine.mythic.api.skills.placeholders.PlaceholderInt createPlaceholderInt(java.lang.String);
  public abstract io.lumine.mythic.api.skills.placeholders.PlaceholderLong createPlaceholderLong(java.lang.String);
  public abstract io.lumine.mythic.api.skills.placeholders.PlaceholderString createPlaceholderString(java.lang.String);
  public abstract java.lang.Boolean getLoadExampleConfigs();
  public abstract int getClockIntervalMain();
}
