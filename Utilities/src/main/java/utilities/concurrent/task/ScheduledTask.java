package utilities.concurrent.task;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Represents a abstract ScheduledTask which is to be used with the {@link TaskManager}
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public class ScheduledTask {

    protected final TaskManager TASK_MANAGER;

    protected final String OWNER;
    protected final String NAME;
    protected final Runnable RUNNABLE;
    protected ScheduledFuture<?> scheduledFuture;

    public ScheduledTask(TaskManager TASK_MANAGER, String OWNER, String NAME, Runnable RUNNABLE, ScheduledFuture<?> scheduledFuture) {
        this.TASK_MANAGER = TASK_MANAGER;
        this.OWNER = OWNER;
        this.NAME = NAME;
        this.RUNNABLE = RUNNABLE;
        this.scheduledFuture = scheduledFuture;
    }

    public ScheduledTask(TaskManager TASK_MANAGER, String OWNER, String NAME, long timeDelay, TimeUnit timeUnit, Runnable RUNNABLE) {
        this.TASK_MANAGER = TASK_MANAGER;
        this.OWNER = OWNER;
        this.NAME = NAME;
        this.RUNNABLE = RUNNABLE;
        scheduleTask(timeDelay, timeUnit);
    }

    public ScheduledTask(TaskManager TASK_MANAGER, String OWNER, String NAME, boolean fixedDelay, long initialDelay, long timeDelay, TimeUnit timeUnit, Runnable RUNNABLE) {
        this.TASK_MANAGER = TASK_MANAGER;
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

    public void scheduleTask(long timeDelay, TimeUnit timeUnit){
        scheduledFuture = TASK_MANAGER.schedule(RUNNABLE, timeDelay, timeUnit);
        insertTaskList();
    }

    public void scheduleRepeatingTask(long initialDelay, long timeDelay, TimeUnit timeUnit){
        scheduledFuture = TASK_MANAGER.scheduleAtFixedRate(RUNNABLE, initialDelay, timeDelay, timeUnit);
        insertTaskList();
    }

    public void scheduleRepeatingFixedDelayTask(long initialDelay, long timeDelay, TimeUnit timeUnit){
        scheduledFuture = TASK_MANAGER.scheduleWithFixedDelay(RUNNABLE, initialDelay, timeDelay, timeUnit);
        insertTaskList();
    }

    /**
     * Recommended: For connections use with StarNub.
     *
     * Uses: This will insert this task into the task list
     */
    private void insertTaskList() {
        ConcurrentHashMap<String, ConcurrentHashMap<String, ScheduledTask>> taskList = TASK_MANAGER.getTASK_LIST();
        if (!taskList.containsKey(OWNER)){
            taskList.put(OWNER, new ConcurrentHashMap<>());
        }
        ConcurrentHashMap<String, ScheduledTask> stg = taskList.get(OWNER);
        int inc = 0;
        do {
            String taskNameOriginal = NAME + " - " + inc;
            if (!stg.containsKey(taskNameOriginal)) {
                stg.putIfAbsent(taskNameOriginal, this);
                break;
            }
            inc++;
        } while (true);
    }

    public void removeTask(){
        ConcurrentHashMap<String, ConcurrentHashMap<String, ScheduledTask>> taskList = TASK_MANAGER.getTASK_LIST();
        ConcurrentHashMap<String, ScheduledTask> ownerTaskMap = taskList.get(OWNER);
        for (Map.Entry<String, ScheduledTask> scheduledTaskEntry : ownerTaskMap.entrySet()){
            String key = scheduledTaskEntry.getKey();
            ScheduledTask scheduledTask = scheduledTaskEntry.getValue();
            if (scheduledTask.equals(this)){
                ownerTaskMap.remove(key);
            }
        }
        if (ownerTaskMap.size() == 0){
            taskList.remove(OWNER);
        }
        scheduledFuture.cancel(true);
    }

    @Override
    public String toString() {
        return "ScheduledTask{" +
                "TASK_MANAGER=" + TASK_MANAGER +
                ", OWNER='" + OWNER + '\'' +
                ", NAME='" + NAME + '\'' +
                ", RUNNABLE=" + RUNNABLE +
                ", scheduledFuture_Delay=" + scheduledFuture.getDelay(TimeUnit.SECONDS) +
                ", scheduledFuture=" + scheduledFuture +
                '}';
    }
}
