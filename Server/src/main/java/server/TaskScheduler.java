package server;

import lombok.Getter;
import server.eventsrouter.events.ThreadEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public enum TaskScheduler {
    INSTANCE;

    @Getter
    private ScheduledThreadPoolExecutor threadScheduler;
    @Getter
    private ConcurrentHashMap<String, ConcurrentHashMap<String, ScheduledTask>> taskList;

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This is use to set the ScheduledThreadPoolExecutor and ConcurrentHashMap for scheduling task and tracking them.
     * <p>
     *
     */
    void setScheduledTask() {
        Integer taskThreads = (int) ((Map)StarNub.getConfiguration().getConfiguration().get("resources")).get("scheduled_task_thread_count");
        this.threadScheduler = new ScheduledThreadPoolExecutor(taskThreads, new NamedThreadFactory("StarNub - Thread Scheduler : Thread Task"));
        this.taskList = new ConcurrentHashMap<String, ConcurrentHashMap<String, ScheduledTask>>();
    }

    public ScheduledThreadPoolExecutor getThreadScheduler() {
        return threadScheduler;
    }

    public ConcurrentHashMap<String, ConcurrentHashMap<String, ScheduledTask>> getTaskList() {
        return taskList;
    }

    public void oneTimeTaskPurge(){
        for (ConcurrentHashMap<String, ScheduledTask> scheduledTaskOwner : taskList.values()){
            //Clean up empty task owner keys
            scheduledTaskOwner.values().stream().filter(scheduledTask -> scheduledTask.getScheduledFuture().isDone()).forEach(scheduledTask -> {
                StarNub.getLogger().cDebPrint(
                        "StarNub",
                        "Scheduled Task Complete, purging from Task List: Owner: " + scheduledTask.getTaskOwner() + ", Task Name: " + scheduledTask.getTaskName() + ".");
                scheduledTaskOwner.remove(scheduledTask.getTaskName());
                //Clean up empty task owner keys
            });
        }
    }

    public void purgeBasedOnTaskNameSpecific(String taskOwner, String taskNameExact){
        ConcurrentHashMap<String, ScheduledTask> specificTask= taskList.get(taskOwner);
        specificTask.keySet().stream().filter(taskNameKey -> taskNameKey.equalsIgnoreCase(taskNameExact)).forEach(taskNameKey -> {
            specificTask.get(taskNameKey).getScheduledFuture().cancel(true);
            specificTask.remove(taskNameKey);
        });
    }

    public void purgeBasedOnTaskNameContains(String taskOwner, String taskNameContains){
        ConcurrentHashMap<String, ScheduledTask> specificTask= taskList.get(taskOwner);
        specificTask.keySet().stream().filter(taskNameKey -> taskNameKey.contains(taskNameContains)).forEach(taskNameKey -> {
            specificTask.get(taskNameKey).getScheduledFuture().cancel(true);
            specificTask.remove(taskNameKey);
        });
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
    public String scheduleOneTimeTask(String taskOwner, String taskName, Runnable runnable, long timeDelay, TimeUnit timeUnit) {
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
    public String scheduleAtFixedRateRepeatingTask(String taskOwner, String taskName, Runnable runnable, long initialDelay, long timeDelay, TimeUnit timeUnit) {
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
    public String scheduleWithFixedDelayRepeatingTask(String taskOwner, String taskName, Runnable runnable, long initialDelay, long timeDelay, TimeUnit timeUnit) {
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
        if (!taskOwner.equalsIgnoreCase("StarNub")) {
            taskOwner = StarNub.getPluginManager().resolvePlugin(taskOwner);
            if (taskOwner == null) {
                return "scheduler-not-scheduled-no-plugin";
            }
        }
        ScheduledFuture<?> scheduledFuture = null;
        if (taskType.equalsIgnoreCase("one")) {
            scheduledFuture = threadScheduler.schedule(runnable, timeDelay, timeUnit);
            ThreadEvent.eventSend_Thread_Task_Scheduled_One_Time(taskOwner, scheduledFuture);
        } else if (taskType.equalsIgnoreCase("fixed-rate")) {
            scheduledFuture = threadScheduler.scheduleAtFixedRate(runnable, timeDelay, initialDelay, timeUnit);
            ThreadEvent.eventSend_Thread_Task_Scheduled_Repeating(taskOwner, scheduledFuture);
        } else if (taskType.equalsIgnoreCase("fixed-delay")) {
            scheduledFuture = threadScheduler.scheduleWithFixedDelay(runnable, timeDelay, initialDelay, timeUnit);
            ThreadEvent.eventSend_Thread_Task_Scheduled_Repeating(taskOwner, scheduledFuture);
        }
        if (!taskList.containsKey(taskOwner)) {
            taskList.put(taskOwner, new ConcurrentHashMap<>());
        }
        ConcurrentHashMap<String, ScheduledTask> stg = taskList.get(taskOwner);
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
        if ((taskList.get(taskOwner).get(taskName).getScheduledFuture() != null)) {
            scheduled = "scheduler-scheduled";
        }
        return scheduled;
    }
}
