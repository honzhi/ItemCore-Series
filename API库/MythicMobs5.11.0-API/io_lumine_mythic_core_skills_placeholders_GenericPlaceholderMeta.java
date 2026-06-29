Compiled from "GenericPlaceholderMeta.java"
public class io.lumine.mythic.core.skills.placeholders.GenericPlaceholderMeta implements io.lumine.mythic.core.skills.placeholders.PlaceholderMeta {
  public io.lumine.mythic.core.skills.placeholders.GenericPlaceholderMeta(io.lumine.mythic.api.skills.SkillCaster);
  public io.lumine.mythic.core.skills.placeholders.GenericPlaceholderMeta(io.lumine.mythic.api.adapters.AbstractEntity);
  public io.lumine.mythic.core.skills.placeholders.GenericPlaceholderMeta(io.lumine.mythic.api.skills.SkillCaster, io.lumine.mythic.api.adapters.AbstractEntity);
  public io.lumine.mythic.core.skills.placeholders.GenericPlaceholderMeta(io.lumine.mythic.api.adapters.AbstractEntity, io.lumine.mythic.api.adapters.AbstractEntity);
  public io.lumine.mythic.core.skills.placeholders.PlaceholderMeta setMetadata(java.lang.String, java.lang.Object);
  public java.util.Optional<java.lang.Object> getMetadata(java.lang.String);
  public <T> java.util.Optional<T> getMetadata(java.lang.Class<T>, java.lang.String);
  public java.util.Optional<java.lang.Object> removeMetadata(java.lang.String);
  public <T> java.util.Optional<T> removeMetadata(java.lang.Class<T>, java.lang.String);
  public void clearMetadata();
  public io.lumine.mythic.api.skills.SkillCaster getCaster();
  public io.lumine.mythic.api.adapters.AbstractEntity getTrigger();
  public java.util.Map<java.lang.String, java.lang.Object> getMetadata();
  public void setMetadata(java.util.Map<java.lang.String, java.lang.Object>);
  public boolean equals(java.lang.Object);
  public int hashCode();
  public java.lang.String toString();
}
