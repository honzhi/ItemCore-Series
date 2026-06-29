Compiled from "AbstractSkillAdapter.java"
public interface io.lumine.mythic.api.adapters.AbstractSkillAdapter {
  public abstract void strikeLightning(io.lumine.mythic.api.adapters.AbstractLocation);
  public abstract void doDamage(io.lumine.mythic.api.skills.damage.DamageMetadata, io.lumine.mythic.api.adapters.AbstractEntity);
  public abstract void throwSkill(io.lumine.mythic.api.adapters.AbstractLocation, io.lumine.mythic.api.adapters.AbstractEntity, float, float);
  public abstract void itemSprayEffect(io.lumine.mythic.api.adapters.AbstractLocation, org.bukkit.inventory.ItemStack, int, int, double, double, double, double, boolean, boolean);
  public abstract void strikeLightningEffect(io.lumine.mythic.api.adapters.AbstractLocation);
  public abstract void playSmokeEffect(io.lumine.mythic.api.adapters.AbstractLocation, int);
  public abstract void shootFireball(io.lumine.mythic.api.skills.SkillCaster, io.lumine.mythic.core.skills.mechanics.ShootFireballMechanic$FireballType, io.lumine.mythic.api.adapters.AbstractLocation, io.lumine.mythic.api.adapters.AbstractLocation, float, float, boolean, int, boolean, boolean, io.lumine.mythic.api.adapters.AbstractItemStack);
  public abstract void pushButton(io.lumine.mythic.api.skills.SkillCaster, io.lumine.mythic.api.adapters.AbstractLocation);
  public abstract void toggleLever(io.lumine.mythic.api.skills.SkillCaster, io.lumine.mythic.api.adapters.AbstractLocation, int);
  public abstract io.lumine.mythic.api.adapters.AbstractEntity shootProjectile(io.lumine.mythic.api.skills.SkillCaster, io.lumine.mythic.api.adapters.AbstractLocation, io.lumine.mythic.api.adapters.AbstractLocation, java.lang.Class<? extends org.bukkit.entity.Projectile>, io.lumine.mythic.api.adapters.AbstractVector, io.lumine.mythic.core.skills.mechanics.ShootMechanic);
  public abstract io.lumine.mythic.api.adapters.AbstractEntity shootArcProjectile(io.lumine.mythic.api.skills.SkillCaster, io.lumine.mythic.api.adapters.AbstractLocation, io.lumine.mythic.api.adapters.AbstractLocation, java.lang.Class<? extends org.bukkit.entity.Projectile>, float, boolean);
  public abstract void executeVolley(io.lumine.mythic.api.skills.SkillCaster, io.lumine.mythic.api.adapters.AbstractLocation, int, float, float, int, int, boolean);
  public abstract io.lumine.mythic.api.adapters.AbstractEntity rainProjectile(io.lumine.mythic.api.skills.SkillCaster, io.lumine.mythic.api.adapters.AbstractLocation, java.lang.Class<? extends org.bukkit.entity.Projectile>, float);
  public abstract io.lumine.mythic.api.adapters.AbstractEntity shootShulkerBullet(io.lumine.mythic.api.skills.SkillCaster, io.lumine.mythic.api.adapters.AbstractEntity, io.lumine.mythic.api.adapters.AbstractLocation, io.lumine.mythic.core.skills.mechanics.ShootShulkerMechanic);
}
