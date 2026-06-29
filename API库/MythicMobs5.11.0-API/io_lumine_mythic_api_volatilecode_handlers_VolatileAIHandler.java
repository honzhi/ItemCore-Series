Compiled from "VolatileAIHandler.java"
public interface io.lumine.mythic.api.volatilecode.handlers.VolatileAIHandler {
  public default io.lumine.mythic.api.mobs.MobEars createEars(io.lumine.mythic.core.mobs.ActiveMob);
  public abstract void addPathfinderGoals(org.bukkit.entity.LivingEntity, java.util.List<java.lang.String>);
  public abstract void addTargetGoals(org.bukkit.entity.LivingEntity, java.util.List<java.lang.String>);
  public default void navigateToLocation(io.lumine.mythic.api.adapters.AbstractEntity, io.lumine.mythic.api.adapters.AbstractLocation, double);
  public default void setPathfindingMalus(io.lumine.mythic.api.adapters.AbstractEntity, java.lang.String, float);
  public default void setNavigationController(io.lumine.mythic.api.adapters.AbstractEntity, java.lang.String);
  public default void resetAI(io.lumine.mythic.api.adapters.AbstractEntity);
}
