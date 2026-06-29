Compiled from "PlaceholderMeta.java"
public interface io.lumine.mythic.core.skills.placeholders.PlaceholderMeta {
  public default io.lumine.mythic.api.skills.SkillCaster getCaster();
  public default io.lumine.mythic.api.adapters.AbstractEntity getTrigger();
  public abstract io.lumine.mythic.core.skills.placeholders.PlaceholderMeta setMetadata(java.lang.String, java.lang.Object);
  public default io.lumine.mythic.core.skills.placeholders.PlaceholderMeta setMetadata(io.lumine.mythic.core.skills.placeholders.PlaceholderMetadataKey, java.lang.Object);
  public abstract java.util.Optional<java.lang.Object> getMetadata(java.lang.String);
  public default java.util.Optional<java.lang.Object> getMetadata(io.lumine.mythic.core.skills.placeholders.PlaceholderMetadataKey);
  public abstract <T> java.util.Optional<T> getMetadata(java.lang.Class<T>, java.lang.String);
  public abstract java.util.Optional<java.lang.Object> removeMetadata(java.lang.String);
  public abstract <T> java.util.Optional<T> removeMetadata(java.lang.Class<T>, java.lang.String);
  public abstract void clearMetadata();
}
