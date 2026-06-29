Compiled from "AbstractDirection.java"
public final class io.lumine.mythic.api.adapters.AbstractDirection extends java.lang.Enum<io.lumine.mythic.api.adapters.AbstractDirection> {
  public static final io.lumine.mythic.api.adapters.AbstractDirection DOWN;
  public static final io.lumine.mythic.api.adapters.AbstractDirection UP;
  public static final io.lumine.mythic.api.adapters.AbstractDirection NORTH;
  public static final io.lumine.mythic.api.adapters.AbstractDirection SOUTH;
  public static final io.lumine.mythic.api.adapters.AbstractDirection WEST;
  public static final io.lumine.mythic.api.adapters.AbstractDirection EAST;
  public static io.lumine.mythic.api.adapters.AbstractDirection[] values();
  public static io.lumine.mythic.api.adapters.AbstractDirection valueOf(java.lang.String);
  public double getStepX();
  public double getStepY();
  public double getStepZ();
  public static io.lumine.mythic.api.adapters.AbstractDirection getRelative(io.lumine.mythic.api.adapters.AbstractLocation, io.lumine.mythic.api.adapters.AbstractLocation);
}
