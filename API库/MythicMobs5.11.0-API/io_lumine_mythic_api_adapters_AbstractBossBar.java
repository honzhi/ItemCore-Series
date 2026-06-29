Compiled from "AbstractBossBar.java"
public interface io.lumine.mythic.api.adapters.AbstractBossBar extends io.lumine.mythic.bukkit.utils.terminable.Terminable {
  public abstract java.lang.String getTitle();
  public abstract void setTitle(java.lang.String);
  public abstract io.lumine.mythic.api.adapters.AbstractBossBar$BarColor getColor();
  public abstract void setColor(java.lang.String);
  public abstract io.lumine.mythic.api.adapters.AbstractBossBar$BarStyle getStyle();
  public abstract void setStyle(java.lang.String);
  public abstract void removeFlag(java.lang.String);
  public abstract void addFlag(java.lang.String);
  public abstract boolean hasFlag(java.lang.String);
  public abstract void setProgress(double);
  public abstract double getProgress();
  public abstract void addPlayer(io.lumine.mythic.api.adapters.AbstractPlayer);
  public abstract void removePlayer(io.lumine.mythic.api.adapters.AbstractPlayer);
  public abstract void removeAll();
  public abstract java.util.Collection<io.lumine.mythic.api.adapters.AbstractPlayer> getPlayers();
  public abstract void setVisible(boolean);
  public abstract boolean isVisible();
  public abstract void setCreateFog(boolean);
  public abstract void setDarkenSky(boolean);
  public abstract void setPlayBossMusic(boolean);
  public abstract java.util.Collection<java.util.UUID> getPlayerIds();
  public abstract void removePlayer(java.util.UUID);
  public abstract boolean isViewing(io.lumine.mythic.api.adapters.AbstractPlayer);
}
