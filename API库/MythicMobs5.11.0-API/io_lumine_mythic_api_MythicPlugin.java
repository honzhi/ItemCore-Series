Compiled from "MythicPlugin.java"
public interface io.lumine.mythic.api.MythicPlugin {
  public abstract io.lumine.mythic.api.adapters.ServerInterface getBootstrap();
  public abstract io.lumine.mythic.api.config.ConfigManager getConfiguration();
  public abstract io.lumine.mythic.api.packs.PackManager getPackManager();
  public abstract io.lumine.mythic.api.skills.pins.PinManager getPinManager();
  public abstract io.lumine.mythic.api.mobs.MobManager getMobManager();
  public abstract io.lumine.mythic.api.skills.SkillManager getSkillManager();
  public abstract io.lumine.mythic.api.items.ItemManager getItemManager();
  public abstract io.lumine.mythic.api.drops.DropManager getDropManager();
  public abstract io.lumine.mythic.api.skills.stats.StatManager getStatManager();
  public abstract io.lumine.mythic.core.skills.variables.VariableManager getVariableManager();
  public abstract io.lumine.mythic.api.skills.placeholders.PlaceholderManager getPlaceholderManager();
  public abstract io.lumine.mythic.api.volatilecode.VolatileCodeHandler getVolatileCodeHandler();
  public abstract boolean isLoading();
  public abstract boolean isShuttingDown();
}
