Compiled from "SkillTrigger.java"
public final class io.lumine.mythic.api.skills.SkillTrigger<T extends io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata> extends java.lang.Record {
  public io.lumine.mythic.api.skills.SkillTrigger(java.lang.String, java.lang.Class<T>, java.util.List<java.lang.String>);
  public static io.lumine.mythic.api.skills.SkillTrigger trigger(java.lang.String);
  public static io.lumine.mythic.api.skills.SkillTrigger create(java.lang.String, java.lang.String...);
  public static io.lumine.mythic.api.skills.SkillTrigger create(java.lang.String, java.lang.Class<? extends io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata>, java.lang.String...);
  public static io.lumine.mythic.api.skills.SkillTrigger get(java.lang.String);
  public static void register(io.lumine.mythic.api.skills.SkillTrigger);
  public static java.util.Collection<io.lumine.mythic.api.skills.SkillTrigger> values();
  public void register();
  public final java.lang.String toString();
  public final int hashCode();
  public final boolean equals(java.lang.Object);
  public java.lang.String name();
  public java.lang.Class<T> metadataClass();
  public java.util.List<java.lang.String> aliases();
}
