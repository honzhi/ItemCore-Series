Compiled from "PlaceholderAngle.java"
public interface io.lumine.mythic.api.skills.placeholders.PlaceholderAngle extends io.lumine.mythic.api.skills.placeholders.IPlaceholder {
  public static io.lumine.mythic.api.skills.placeholders.PlaceholderAngle of(java.lang.String, io.lumine.mythic.bukkit.utils.numbers.AngleUnit);
  public abstract io.lumine.mythic.bukkit.utils.numbers.Angle get();
  public abstract io.lumine.mythic.bukkit.utils.numbers.Angle get(io.lumine.mythic.core.skills.placeholders.PlaceholderMeta);
  public abstract io.lumine.mythic.bukkit.utils.numbers.Angle get(io.lumine.mythic.api.adapters.AbstractEntity);
  public abstract io.lumine.mythic.bukkit.utils.numbers.Angle get(io.lumine.mythic.core.skills.placeholders.PlaceholderMeta, io.lumine.mythic.api.adapters.AbstractEntity);
  public abstract io.lumine.mythic.bukkit.utils.numbers.Angle get(io.lumine.mythic.api.skills.SkillCaster);
  public abstract boolean isStaticallyEqualTo(io.lumine.mythic.bukkit.utils.numbers.Angle);
}
