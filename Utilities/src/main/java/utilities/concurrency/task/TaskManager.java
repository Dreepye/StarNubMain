package utilities.concurrency.task;

import utilities.concurrency.thread.NamedThreadFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Represents a TaskManager which will allow scheduling of one time or reoccurring task
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class TaskManager extends ScheduledThreadPoolExecutor{

    private final ConcurrentHashMap<String, ConcurrentHashMap<String, ScheduledTask>> TASK_LIST = new ConcurrentHashMap<>();

    public TaskManager(int TASK_THREAD_COUNT, String THREAD_NAMING){
        super(TASK_THREAD_COUNT, new NamedThreadFactory(THREAD_NAMING));
        this.scheduleWithFixedDelayTask("Utilities", "Utilities - One Time Task Purger", this::oneTimeTaskPurge, 30, 30, TimeUnit.SECONDS);
    }

    public ConcurrentHashMap<String, ConcurrentHashMap<String, ScheduledTask>> getTASK_LIST() {
        return TASK_LIST;
    }

    /**
     * This will purge all one time task that have been completed
     */
    public void oneTimeTaskPurge(){
        for (String taskOwner : TASK_LIST.keySet()){
            ConcurrentHashMap<String, ScheduledTask> scheduledTaskOwner = TASK_LIST.get(taskOwner);
            scheduledTaskOwner.values().stream().filter(scheduledTask -> scheduledTask.getScheduledFuture().isDone()).forEach(scheduledTask -> {
                scheduledTaskOwner.remove(scheduledTask.getTaskName());
                if(scheduledTaskOwner.isEmpty()){
                    TASK_LIST.remove(taskOwner);
                }
            });
        }
    }

    /**
     * This will purge task purge task based on the Task Owner and Task Name, exact or contains. This method is case - insensitive
     *
     * @param taskOwner String representing the Task Owner
     * @param taskName String representing the partial or full Task Name to purge, this will purge any task what contains this or matches
     * @param exactMatch boolean representing if we want to purge on exact match (true) or contains (false)
     */
    public void purgeBasedOnTaskName(String taskOwner, String taskName, boolean exactMatch){
        ConcurrentHashMap<String, ScheduledTask> scheduledTaskOwner = TASK_LIST.get(taskOwner);
        HashSet<String> keysToRemove = new HashSet<>();
        if (scheduledTaskOwner == null || scheduledTaskOwner.isEmpty()){
            return;
        }
        taskName = taskName.toLowerCase();
        for (Map.Entry<String, ScheduledTask> scheduledTaskEntry : scheduledTaskOwner.entrySet()){
            String s = scheduledTaskEntry.getKey().toLowerCase();
            ScheduledTask st = scheduledTaskEntry.getValue();
            if (exactMatch){
                if (s.equals(taskName)){
                    keysToRemove.add(taskName);
                    st.getScheduledFuture().cancel(true);
                }
            } else {
                if (s.contains(taskName)){
                    keysToRemove.add(taskName);
                    st.getScheduledFuture().cancel(true);
                }
            }
        }
        keysToRemove.forEach(scheduledTaskOwner::remove);
        if(scheduledTaskOwner.isEmpty()){
            TASK_LIST.remove(taskOwner);
        }
    }


    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method will allow you to schedule one time task
     * <p>
     * Java-API: Creates and executes a one-shot action that becomes enabled after the given delay.
     * <P>
     * @param taskOwner String representing the task owner. This must be a plugin name that is currently loaded.
     * @param taskName String representing the task name that can be referenced so it can be retrieved if needed
     * @param runnable Runnable to be scheduled
     * @param timeDelay long representing how far out to schedule the task
     * @param timeUnit TimeUnit representing the units of time the time delay is for, see Java API for specifics
     * @return String representing the success message
     */
    public String scheduleTask(String taskOwner, String taskName, Runnable runnable, long timeDelay, TimeUnit timeUnit) {
        return taskScheduler(taskOwner, taskName, runnable, 0, timeDelay, timeUnit, "one");
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method will allow you to schedule one time task
     * <p>
     * Java-API: Creates and executes a periodic action that becomes enabled first after the given initial delay, and subsequently with the given period; that is executions will commence after initialDelay then initialDelay+period, then initialDelay + 2 * period, and so on.
     * <p>
     * @param taskOwner String representing the task owner. This must be a plugin name that is currently loaded.
     * @param taskName String representing the task name that can be referenced so it can be retrieved if needed
     * @param runnable Runnable to be scheduled
     * @param initialDelay long representing the initial delay before starting the task
     * @param timeDelay long representing how far out to schedule the task
     * @param timeUnit TimeUnit representing the units of time the time delay is for, see Java API for specifics
     * @return String representing the success message
     */
    public String scheduleAtFixedRateTask(String taskOwner, String taskName, Runnable runnable, long initialDelay, long timeDelay, TimeUnit timeUnit) {
        return taskScheduler(taskOwner, taskName, runnable, initialDelay, timeDelay, timeUnit, "fixed-rate");
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method will allow you to schedule one time task
     * <p>
     * Java-API: Creates and executes a periodic action that becomes enabled first after the given initial delay, and subsequently with the given delay between the termination of one execution and the commencement of the next.
     * <p>
     * @param taskOwner String representing the task owner. This must be a plugin name that is currently loaded.
     * @param taskName String representing the task name that can be referenced so it can be retrieved if needed
     * @param runnable Runnable to be scheduled
     * @param initialDelay long representing the initial delay before starting the task
     * @param timeDelay long representing how far out to schedule the task
     * @param timeUnit TimeUnit representing the units of time the time delay is for, see Java API for specifics
     * @return String representing the success message
     */
    public String scheduleWithFixedDelayTask(String taskOwner, String taskName, Runnable runnable, long initialDelay, long timeDelay, TimeUnit timeUnit) {
        return taskScheduler(taskOwner, taskName, runnable, initialDelay, timeDelay, timeUnit, "fixed-delay");
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This is used internally for StarNub to schedule task on the behalf of plugins and the program it's self
     * <p>
     * @param taskOwner String representing the task owner. This must be a plugin name that is currently loaded.
     * @param taskName String representing the task name that can be referenced so it can be retrieved if needed
     * @param runnable Runnable to be scheduled
     * @param initialDelay long representing the initial delay before starting the task
     * @param timeDelay long representing how far out to schedule the task
     * @param timeUnit TimeUnit representing the units of time the time delay is for, see Java API for specifics
     * @return String representing the success message
     */
    private String taskScheduler(String taskOwner, String taskName, Runnable runnable, long initialDelay, long timeDelay, TimeUnit timeUnit, String taskType) {
        ScheduledFuture<?> scheduledFuture = null;
        if (taskType.equalsIgnoreCase("one")) {
            scheduledFuture = this.schedule(runnable, timeDelay, timeUnit);
        } else if (taskType.equalsIgnoreCase("fixed-rate")) {
            scheduledFuture = this.scheduleAtFixedRate(runnable, timeDelay, initialDelay, timeUnit);
        } else if (taskType.equalsIgnoreCase("fixed-delay")) {
            scheduledFuture = this.scheduleWithFixedDelay(runnable, timeDelay, initialDelay, timeUnit);
        }
        insertTaskOwner(taskOwner);
        ConcurrentHashMap<String, ScheduledTask> stg = TASK_LIST.get(taskOwner);
        int inc = 0;
        String taskNameOriginal = taskName + " - ";
        do  {
            taskName = taskNameOriginal + inc;
            if (!stg.containsKey(taskName)) {
                stg.putIfAbsent(taskName, new ScheduledTask(taskOwner, taskName, runnable, scheduledFuture));
                break;
            }
            inc++;
        } while (stg.containsKey(taskName));
        String scheduled = "scheduler-not-scheduled-unknown";
        if ((TASK_LIST.get(taskOwner).get(taskName).getScheduledFuture() != null)) {
            scheduled = "scheduler-scheduled";
        }
        return scheduled;
    }

    /**
     * This method will check to see if the Task Owner exist, and if not insert them into the Task List
     *
     * @param taskOwner String the Task Owner to check or insert
     */
    private void insertTaskOwner(String taskOwner){
        taskOwner = taskOwner.toLowerCase();
        if (!TASK_LIST.containsKey(taskOwner)){
            TASK_LIST.put(taskOwner, new ConcurrentHashMap<>());
        }
    }
}
