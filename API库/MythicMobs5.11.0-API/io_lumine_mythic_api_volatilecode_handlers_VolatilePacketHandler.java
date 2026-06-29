Compiled from "VolatilePacketHandler.java"
public interface io.lumine.mythic.api.volatilecode.handlers.VolatilePacketHandler {
  public abstract void injectPlayer(org.bukkit.entity.Player);
  public abstract void ejectPlayer(org.bukkit.entity.Player);
  public default void enablePlayerActionEvent();
  public default void enablePlayerBlockDestructionEvent();
}
