package utilities.concurrency.task;

import java.util.concurrent.ScheduledFuture;

/**
 * Represents a ScheduledTask which is to be used with the {@link TaskManager}
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class ScheduledTask {

    private String taskOwner;
    private String taskName;
    private Runnable runnable;
    private ScheduledFuture<?> scheduledFuture;

    public ScheduledTask(String taskOwner, String taskName, Runnable runnable, ScheduledFuture<?> scheduledFuture) {
        this.taskOwner = taskOwner;
        this.taskName = taskName;
        this.runnable = runnable;
        this.scheduledFuture = scheduledFuture;
    }

    public String getTaskOwner() {
        return taskOwner;
    }

    public String getTaskName() {
        return taskName;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public ScheduledFuture<?> getScheduledFuture() {
        return scheduledFuture;
    }
}
