Compiled from "Placeholder.java"
public interface io.lumine.mythic.core.skills.placeholders.Placeholder {
  public static io.lumine.mythic.core.skills.placeholders.types.GeneralPlaceholder general(java.util.function.Function<java.lang.String, java.lang.String>);
  public static io.lumine.mythic.core.skills.placeholders.types.MetaPlaceholder meta(java.util.function.BiFunction<io.lumine.mythic.core.skills.placeholders.PlaceholderMeta, java.lang.String, java.lang.String>);
  public static io.lumine.mythic.core.skills.placeholders.types.EntityPlaceholder entity(java.util.function.BiFunction<io.lumine.mythic.api.adapters.AbstractEntity, java.lang.String, java.lang.String>);
  public static io.lumine.mythic.core.skills.placeholders.types.LocationPlaceholder location(java.util.function.BiFunction<io.lumine.mythic.api.adapters.AbstractLocation, java.lang.String, java.lang.String>);
  public static io.lumine.mythic.core.skills.placeholders.types.ParentPlaceholder parent(java.util.function.BiFunction<io.lumine.mythic.api.adapters.AbstractEntity, java.lang.String, java.lang.String>);
  public static io.lumine.mythic.core.skills.placeholders.types.SpawnerPlaceholder spawner(java.util.function.BiFunction<io.lumine.mythic.core.spawning.spawners.MythicSpawner, java.lang.String, java.lang.String>);
  public static io.lumine.mythic.core.skills.placeholders.types.TargetPlaceholder target(io.lumine.mythic.bukkit.utils.interfaces.TriFunction<io.lumine.mythic.core.skills.placeholders.PlaceholderMeta, io.lumine.mythic.api.adapters.AbstractEntity, java.lang.String, java.lang.String>);
  public static io.lumine.mythic.core.skills.placeholders.types.WorldPlaceholder world(java.util.function.BiFunction<io.lumine.mythic.api.adapters.AbstractWorld, java.lang.String, java.lang.String>);
}
