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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class StarNubTask extends ScheduledTask {

    public StarNubTask(String OWNER, String NAME, long timeDelay, TimeUnit timeUnit, Runnable RUNNABLE) {
        super(OWNER, NAME, timeDelay, timeUnit, RUNNABLE);
    }

    public StarNubTask(String OWNER, String NAME, boolean fixedDelay, long initialDelay, long timeDelay, TimeUnit timeUnit, Runnable RUNNABLE) {
        super(OWNER, NAME, fixedDelay, initialDelay, timeDelay, timeUnit, RUNNABLE);
    }

    @Override
    public void scheduleTask(long timeDelay, TimeUnit timeUnit) {

    }

    @Override
    public void scheduleRepeatingTask(long initialDelay, long timeDelay, TimeUnit timeUnit) {

    }

    @Override
    public void scheduleRepeatingFixedDelayTask(long initialDelay, long timeDelay, TimeUnit timeUnit) {

    }

    @Override
    public void removeTask() {

    }





    ///Implenetent the below on the task
    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This is used internally for StarNub to schedule task on the behalf of plugins and the program it's self
     * <p>
     * @param taskType representing the task type
     * @param scheduledTask representing the Task Owner, Name and Runnable
     * @param initialDelay long representing the initial delay before starting the task
     * @param timeDelay long representing how far out to schedule the task
     * @param timeUnit TimeUnit representing the units of time the time delay is for, see Java API for specifics
     * @return String representing the success message
     */
    private String taskScheduler(String taskType, ScheduledTask scheduledTask, long initialDelay, long timeDelay, TimeUnit timeUnit) {
        scheduledTask.scheduleThisTask(taskType, this, initialDelay, timeDelay, timeUnit);
        String taskOwner = scheduledTask.getOWNER();
        String taskName = scheduledTask.getNAME();
        insertTaskOwner(taskOwner);
        ConcurrentHashMap<String, ScheduledTask> stg = TASK_LIST.get(taskOwner);
        int inc = 0;
        String taskNameOriginal = taskName + " - ";
        do  {
            taskName = taskNameOriginal + inc;
            if (!stg.containsKey(taskName)) {
                stg.putIfAbsent(taskName, scheduledTask);
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
