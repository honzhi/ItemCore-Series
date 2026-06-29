Compiled from "PlaceholderColor.java"
public interface io.lumine.mythic.api.skills.placeholders.PlaceholderColor extends io.lumine.mythic.api.skills.placeholders.IPlaceholder {
  public static io.lumine.mythic.api.skills.placeholders.PlaceholderColor of(java.lang.String);
  public abstract io.lumine.mythic.bukkit.utils.serialize.Chroma get();
  public abstract io.lumine.mythic.bukkit.utils.serialize.Chroma get(io.lumine.mythic.core.skills.placeholders.PlaceholderMeta);
  public abstract io.lumine.mythic.bukkit.utils.serialize.Chroma get(io.lumine.mythic.api.adapters.AbstractEntity);
  public abstract io.lumine.mythic.bukkit.utils.serialize.Chroma get(io.lumine.mythic.core.skills.placeholders.PlaceholderMeta, io.lumine.mythic.api.adapters.AbstractEntity);
  public abstract io.lumine.mythic.bukkit.utils.serialize.Chroma get(io.lumine.mythic.api.skills.SkillCaster);
}
