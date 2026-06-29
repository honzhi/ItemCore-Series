Compiled from "AbstractTaskManager.java"
public interface io.lumine.mythic.api.adapters.AbstractTaskManager {
  public abstract int scheduleAsyncTask(java.lang.Runnable, int, int);
  public abstract int scheduleTask(java.lang.Runnable, int, int);
  public abstract void cancelTask(int);
  public abstract void runAsync(java.lang.Runnable);
  public abstract void runLater(java.lang.Runnable, int);
  public abstract void runAsyncLater(java.lang.Runnable, int);
  public abstract void run(java.lang.Runnable);
}
