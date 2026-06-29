Compiled from "Pack.java"
public interface io.lumine.mythic.api.packs.Pack extends io.lumine.mythic.bukkit.utils.config.properties.PropertyHolder {
  public abstract java.io.File getFolder();
  public abstract java.lang.String getKey();
  public abstract java.lang.String getName();
  public abstract java.lang.String getAuthor();
  public abstract java.lang.String getUrl();
  public abstract java.lang.String getVersion();
  public abstract java.io.File getSchematicDirectory();
  public abstract java.lang.Integer getItemCount();
  public abstract void addItem();
  public default java.io.File getPackFolder(java.lang.String);
  public abstract java.io.File getPackFolder(java.lang.String, boolean);
  public default java.util.Collection<java.io.File> getPackFolders(java.lang.String);
  public abstract java.util.Collection<java.io.File> getPackFolders(java.lang.String, boolean, boolean);
  public abstract java.io.File getPackFile(java.lang.String);
  public abstract <T> io.lumine.mythic.bukkit.utils.menu.Icon<T> createMenuIcon(java.util.function.BiConsumer<T, org.bukkit.entity.Player>);
}
