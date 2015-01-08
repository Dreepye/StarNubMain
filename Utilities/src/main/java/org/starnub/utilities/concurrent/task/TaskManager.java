package org.starnub.utilities.concurrent.task;

import org.starnub.utilities.concurrent.thread.NamedThreadFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

    public TaskManager(int TASK_THREAD_COUNT, int MAX_THREAD_COUNT, long KEEP_ALIVE, TimeUnit TIME_UNIT, String THREAD_NAMING){
        super(TASK_THREAD_COUNT, new NamedThreadFactory(THREAD_NAMING));
        super.setMaximumPoolSize(MAX_THREAD_COUNT);
        super.setKeepAliveTime(KEEP_ALIVE, TIME_UNIT);
        Runnable runnable = TaskManager.this::oneTimeTaskPurge;
        new ScheduledTask(this, "Utilities", "Utilities - One Time Task Purger", true, 30, 30, TimeUnit.SECONDS, runnable);
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
    public void purgeByTaskName(String taskOwner, String taskName, boolean exactMatch){
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
     * This will purge task purge task based on the Task Owner and Task Name, exact or contains. This method is case - insensitive
     *
     * @param taskOwner String representing the Task Owner
     */
    public void purgeByOwnerName(String taskOwner){
        ConcurrentHashMap<String, ScheduledTask> scheduledTaskOwner = TASK_LIST.remove(taskOwner);
        if (scheduledTaskOwner == null || scheduledTaskOwner.isEmpty()){
            return;
        }
        for (Map.Entry<String, ScheduledTask> scheduledTaskEntry : scheduledTaskOwner.entrySet()){
            String s = scheduledTaskEntry.getKey().toLowerCase();
            if (s.equalsIgnoreCase(taskOwner)){
                scheduledTaskOwner.remove(s).getScheduledFuture().cancel(true);
            }
        }
    }
}
