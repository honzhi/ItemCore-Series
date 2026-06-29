Compiled from "VolatileCodeHandler.java"
public interface io.lumine.mythic.api.volatilecode.VolatileCodeHandler {
  public static io.lumine.mythic.bukkit.MythicBukkit getPlugin();
  public default io.lumine.mythic.api.volatilecode.handlers.VolatileAIHandler getAIHandler();
  public default io.lumine.mythic.api.volatilecode.handlers.VolatileBlockHandler getBlockHandler();
  public default io.lumine.mythic.api.volatilecode.handlers.VolatileCompatibilityHandler getCompatibilityHandler();
  public default io.lumine.mythic.api.volatilecode.handlers.VolatileEntityHandler getEntityHandler();
  public default io.lumine.mythic.api.volatilecode.handlers.VolatileItemHandler getItemHandler();
  public default io.lumine.mythic.api.volatilecode.handlers.VolatilePacketHandler getPacketHandler();
  public default io.lumine.mythic.api.volatilecode.handlers.VolatileSpawningHandler getSpawningHandler();
  public default io.lumine.mythic.api.volatilecode.handlers.VolatileWorldHandler getWorldHandler();
  public default io.lumine.mythic.core.utils.jnbt.CompoundTag createCompoundTag(java.util.Map<java.lang.String, io.lumine.mythic.core.utils.jnbt.Tag>);
  public abstract boolean doDamage(io.lumine.mythic.core.mobs.ActiveMob, io.lumine.mythic.api.adapters.AbstractEntity, float);
  public abstract void CreateFireworksExplosion(org.bukkit.Location, boolean, boolean, int, int[], int[], int);
  public abstract void setChickenHostile(org.bukkit.entity.Chicken);
  public abstract java.util.Set<io.lumine.mythic.api.adapters.AbstractEntity> getEntitiesBySelector(io.lumine.mythic.api.skills.SkillCaster, java.lang.String);
  public abstract double getAbsorptionHearts(org.bukkit.entity.LivingEntity);
  public abstract void saveSkinData(org.bukkit.entity.Player, java.lang.String);
  public abstract float getItemRecharge(org.bukkit.entity.Player);
  public abstract void doEffectArmSwing(io.lumine.mythic.api.adapters.AbstractEntity);
  public default void lookAtLocation(io.lumine.mythic.api.adapters.AbstractEntity, io.lumine.mythic.api.adapters.AbstractLocation, boolean, boolean);
  public default void lookAtEntity(io.lumine.mythic.api.adapters.AbstractEntity, io.lumine.mythic.api.adapters.AbstractEntity, boolean, boolean);
  public default void lookAt(io.lumine.mythic.api.adapters.AbstractEntity, float, float);
  public default void setHeadYaw(io.lumine.mythic.api.adapters.AbstractEntity, float);
  public default boolean getItemRecharging(org.bukkit.entity.Player);
  public default void applyPhysics(org.bukkit.block.Block);
  public default void sendResourcePack(io.lumine.mythic.api.adapters.AbstractPlayer, java.lang.String, java.lang.String);
}
