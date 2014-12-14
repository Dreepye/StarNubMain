package utilities.concurrent.task;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Represents a abstract ScheduledTask which is to be used with the {@link TaskManager}
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public abstract class ScheduledTask {

    protected final String OWNER;
    protected final String NAME;
    protected final Runnable RUNNABLE;
    protected ScheduledFuture<?> scheduledFuture;

    public ScheduledTask(String OWNER, String NAME, Runnable RUNNABLE, ScheduledFuture<?> scheduledFuture) {
        this.OWNER = OWNER;
        this.NAME = NAME;
        this.RUNNABLE = RUNNABLE;
        this.scheduledFuture = scheduledFuture;
    }

    public ScheduledTask(String OWNER, String NAME, long timeDelay, TimeUnit timeUnit, Runnable RUNNABLE) {
        this.OWNER = OWNER;
        this.NAME = NAME;
        this.RUNNABLE = RUNNABLE;
        scheduleTask(timeDelay, timeUnit);
    }

    public ScheduledTask(String OWNER, String NAME, boolean fixedDelay, long initialDelay, long timeDelay, TimeUnit timeUnit, Runnable RUNNABLE) {
        this.OWNER = OWNER;
        this.NAME = NAME;
        this.RUNNABLE = RUNNABLE;
        if (!fixedDelay) {
            scheduleRepeatingTask(initialDelay, timeDelay, timeUnit);
        } else {
            scheduleRepeatingFixedDelayTask(initialDelay, timeDelay, timeUnit);
        }
    }

    public String getOWNER() {
        return OWNER;
    }

    public String getNAME() {
        return NAME;
    }

    public Runnable getRUNNABLE() {
        return RUNNABLE;
    }

    public ScheduledFuture<?> getScheduledFuture() {
        return scheduledFuture;
    }

    public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    public abstract void scheduleTask(long timeDelay, TimeUnit timeUnit);

    public abstract void scheduleRepeatingTask(long initialDelay, long timeDelay, TimeUnit timeUnit);

    public abstract void scheduleRepeatingFixedDelayTask(long initialDelay, long timeDelay, TimeUnit timeUnit);

    public void removeDeactivate(){
        scheduledFuture.cancel(true);
        removeTask();
    }

    public abstract void removeTask();

}
