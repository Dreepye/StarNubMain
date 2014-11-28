/*
 * Copyright (C) 2014 www.StarNub.org - Underbalanced
 *
 * This file is part of org.starnub a Java Wrapper for Starbound.
 *
 * This above mentioned StarNub software is free software:
 * you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free
 * Software Foundation, either version  3 of the License, or
 * any later version. This above mentioned CodeHome software
 * is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
 * the GNU General Public License for more details. You should
 * have received a copy of the GNU General Public License in
 * this StarNub Software.  If not, see <http://www.gnu.org/licenses/>.
 */

package starnub;

import utilities.concurrency.task.ScheduledTask;
import utilities.concurrency.task.TaskManager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Represents StarNubTask instance
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class StarNubTask extends ScheduledTask {

    TaskManager taskManager = StarNub.getTaskManager();

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will automatically schedule a one time task and it can be canceled by calling removeTask()
     *
     * @param OWNER String representing the owner of this task. HIGHLY RECOMMENDED: Please use your exact plugin name from your plugin.yml
     * @param NAME String representing some name that you want to use that can be used when searching for specific task
     * @param timeDelay long representing the interval that this task should wait until it executes
     * @param timeUnit TimeUnit representing what your timeDelay is measured as
     * @param RUNNABLE Runnable representing the Runnable that extends run() with the code that you want to execute
     */
    public StarNubTask(String OWNER, String NAME, long timeDelay, TimeUnit timeUnit, Runnable RUNNABLE) {
        super(OWNER, NAME, timeDelay, timeUnit, RUNNABLE);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will automatically schedule a repeating with or without a fixed delay task and it can be canceled by calling removeTask()
     *
     * @param OWNER String representing the owner of this task. HIGHLY RECOMMENDED: Please use your exact plugin name from your plugin.yml
     * @param NAME String representing some name that you want to use that can be used when searching for specific task
     * @param fixedDelay boolean representing if this should wait a fixed amount before starting
     * @param initialDelay long representing the fixed delay value, if false, use 0
     * @param timeDelay long representing the interval that this task should repeat itself
     * @param timeUnit TimeUnit representing what your timeDelay is measured as
     * @param RUNNABLE Runnable representing the Runnable that extends run() with the code that you want to execute
     */
    public StarNubTask(String OWNER, String NAME, boolean fixedDelay, long initialDelay, long timeDelay, TimeUnit timeUnit, Runnable RUNNABLE) {
        super(OWNER, NAME, fixedDelay, initialDelay, timeDelay, timeUnit, RUNNABLE);
    }

    /**
     * Recommended: For internal use with StarNub.
     *
     * Uses: This will schedule a one time task then insert this task into the task list
     *
     * @param timeDelay long representing the interval that this task should repeat itself
     * @param timeUnit TimeUnit representing what your timeDelay is measured as
     */
    @Override
    public void scheduleTask(long timeDelay, TimeUnit timeUnit) {
        super.scheduledFuture = taskManager.schedule(super.RUNNABLE, timeDelay, timeUnit);
        insertTaskList();
    }

    /**
     * Recommended: For internal use with StarNub.
     *
     * Uses: This will schedule a repeating task then insert this task into the task list
     *
     * @param initialDelay long representing the fixed delay value, if false, use 0
     * @param timeDelay long representing the interval that this task should repeat itself
     * @param timeUnit TimeUnit representing what your timeDelay is measured as
     */
    @Override
    public void scheduleRepeatingTask(long initialDelay, long timeDelay, TimeUnit timeUnit) {
        super.scheduledFuture = taskManager.scheduleAtFixedRate(super.RUNNABLE, initialDelay, timeDelay, timeUnit);
        insertTaskList();
    }

    /**
     * Recommended: For internal use with StarNub.
     *
     * Uses: This will schedule a repeating task with a fixed delay then insert this task into the task list
     *
     * @param initialDelay long representing the fixed delay value, if false, use 0
     * @param timeDelay long representing the interval that this task should repeat itself
     * @param timeUnit TimeUnit representing what your timeDelay is measured as
     */
    @Override
    public void scheduleRepeatingFixedDelayTask(long initialDelay, long timeDelay, TimeUnit timeUnit) {
        super.scheduledFuture = taskManager.scheduleWithFixedDelay(super.RUNNABLE, initialDelay, timeDelay, timeUnit);
        insertTaskList();
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     *
     * Uses: This will remove the task from the task list
     */
    @Override
    public void removeTask() {
        try {
            taskManager.getTASK_LIST().get(super.OWNER).remove(super.NAME).getScheduledFuture().cancel(true);
        }catch (NullPointerException e){
            /* Silent Catch */
        }
    }

    /**
     * Recommended: For internal use with StarNub.
     *
     * Uses: This will insert this task into the task list
     */
    private void insertTaskList() {
        ConcurrentHashMap<String, ConcurrentHashMap<String, ScheduledTask>> taskList = taskManager.getTASK_LIST();
        if (!taskList.containsKey(super.OWNER)){
            taskList.put(super.OWNER, new ConcurrentHashMap<>());
        }
        ConcurrentHashMap<String, ScheduledTask> stg = taskList.get(super.OWNER);
        int inc = 0;
        do {
            String taskNameOriginal = super.NAME + " - " + inc;
            if (!stg.containsKey(taskNameOriginal)) {
                stg.putIfAbsent(taskNameOriginal, this);
                break;
            }
            inc++;
        } while (true);
    }
}
