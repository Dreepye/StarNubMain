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

package org.starnub.starnubserver;

import org.starnub.starnubserver.events.events.StarNubEvent;
import org.starnub.utilities.concurrent.task.ScheduledTask;
import org.starnub.utilities.concurrent.task.TaskManager;

import java.util.concurrent.TimeUnit;

/**
 * Represents StarNubTask instance
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class StarNubTask extends ScheduledTask {

    private final static TaskManager TASK_MANAGER = StarNubTaskManager.getInstance();

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will automatically schedule a one time task and it can be canceled by calling removeTask()
     *
     * @param OWNER String representing the owner of this task. HIGHLY RECOMMENDED: Please use your exact pluggableOLD name from your pluggableOLD.yml
     * @param NAME String representing some name that you want to use that can be used when searching for specific task
     * @param timeDelay long representing the interval that this task should wait until it executes
     * @param timeUnit TimeUnit representing what your timeDelay is measured as
     * @param RUNNABLE Runnable representing the Runnable that extends run() with the code that you want to execute
     */
    public StarNubTask(String OWNER, String NAME, long timeDelay, TimeUnit timeUnit, Runnable RUNNABLE) {
        super(TASK_MANAGER, OWNER, NAME, timeDelay, timeUnit, RUNNABLE);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will automatically schedule a repeating with or without a fixed delay task and it can be canceled by calling removeTask()
     *
     * @param OWNER String representing the owner of this task. HIGHLY RECOMMENDED: Please use your exact pluggableOLD name from your pluggableOLD.yml
     * @param NAME String representing some name that you want to use that can be used when searching for specific task
     * @param fixedDelay boolean representing if this should wait a fixed amount before starting
     * @param initialDelay long representing the fixed delay value, if false, use 0
     * @param timeDelay long representing the interval that this task should repeat itself
     * @param timeUnit TimeUnit representing what your timeDelay is measured as
     * @param RUNNABLE Runnable representing the Runnable that extends run() with the code that you want to execute
     */
    public StarNubTask(String OWNER, String NAME, boolean fixedDelay, long initialDelay, long timeDelay, TimeUnit timeUnit, Runnable RUNNABLE) {
        super(TASK_MANAGER, OWNER, NAME, fixedDelay, initialDelay, timeDelay, timeUnit, RUNNABLE);
    }

    /**
     * Recommended: For connections use with StarNub.
     *
     * Uses: This will schedule a one time task then insert this task into the task list
     *
     * @param timeDelay long representing the interval that this task should repeat itself
     * @param timeUnit TimeUnit representing what your timeDelay is measured as
     */
    @Override
    public void scheduleTask(long timeDelay, TimeUnit timeUnit) {
        super.scheduleTask(timeDelay, timeUnit);
        new StarNubEvent("StarNub_Task_Registered_One_Time", this);
    }

    /**
     * Recommended: For connections use with StarNub.
     *
     * Uses: This will schedule a repeating task then insert this task into the task list
     *
     * @param initialDelay long representing the fixed delay value, if false, use 0
     * @param timeDelay long representing the interval that this task should repeat itself
     * @param timeUnit TimeUnit representing what your timeDelay is measured as
     */
    @Override
    public void scheduleRepeatingTask(long initialDelay, long timeDelay, TimeUnit timeUnit) {
        super.scheduleRepeatingTask(initialDelay, timeDelay, timeUnit);
        new StarNubEvent("StarNub_Task_Registered_Recurring_No_Delay", this);
    }

    /**
     * Recommended: For connections use with StarNub.
     *
     * Uses: This will schedule a repeating task with a fixed delay then insert this task into the task list
     *
     * @param initialDelay long representing the fixed delay value, if false, use 0
     * @param timeDelay long representing the interval that this task should repeat itself
     * @param timeUnit TimeUnit representing what your timeDelay is measured as
     */
    @Override
    public void scheduleRepeatingFixedDelayTask(long initialDelay, long timeDelay, TimeUnit timeUnit) {
        super.scheduleRepeatingFixedDelayTask(initialDelay, timeDelay, timeUnit);
        new StarNubEvent("StarNub_Task_Registered_Recurring_Fixed_Delay", this);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     *
     * Uses: This will remove the task from the task list
     */
    @Override
    public void removeTask() {
        super.removeTask();
        new StarNubEvent("StarNub_Task_Removed", this);
    }

    @Override
    public String toString() {
        return "StarNubTask{} " + super.toString();
    }
}
