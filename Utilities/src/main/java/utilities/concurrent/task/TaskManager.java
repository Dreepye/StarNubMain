package utilities.concurrent.task;

import utilities.concurrent.thread.NamedThreadFactory;

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
 * @since 1.0
 */
public class TaskManager extends ScheduledThreadPoolExecutor{

    private final ConcurrentHashMap<String, ConcurrentHashMap<String, ScheduledTask>> TASK_LIST = new ConcurrentHashMap<>();

    public TaskManager(int TASK_THREAD_COUNT, String THREAD_NAMING){
        super(TASK_THREAD_COUNT, new NamedThreadFactory(THREAD_NAMING));
        Runnable runnable = TaskManager.this::oneTimeTaskPurge;
        ScheduledFuture scheduledFuture =  this.scheduleWithFixedDelay(runnable, 30, 30, TimeUnit.SECONDS);
        ScheduledTask scheduledTask = new InternalTask("Utilities", "Utilities - One Time Task Purger", runnable,  scheduledFuture);
        TASK_LIST.put("Utilities", new ConcurrentHashMap<>());
        TASK_LIST.get("Utilities").put("Utilities - One Time Task Purger", scheduledTask);
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
                scheduledTaskOwner.remove(scheduledTask.getNAME());
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
}
