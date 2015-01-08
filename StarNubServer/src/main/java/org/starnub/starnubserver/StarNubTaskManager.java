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

import org.starnub.utilities.concurrent.task.TaskManager;

import java.util.concurrent.TimeUnit;

/**
 * Represents StarNubTask instance
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class StarNubTaskManager extends TaskManager {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final StarNubTaskManager instance = new StarNubTaskManager();

    /**
     * This constructor is private - Singleton Pattern
     */
    private StarNubTaskManager() {
        super(
                (int) StarNub.getConfiguration().getNestedValue("advanced_settings", "resources", "task_threads", "core_count"),
                (int) StarNub.getConfiguration().getNestedValue("advanced_settings", "resources", "task_threads", "max_count"),
                (int) StarNub.getConfiguration().getNestedValue("advanced_settings", "resources", "task_threads", "keep_alive_minutes"),
                TimeUnit.MINUTES,
                "StarNub - Events & Scheduled Task - Worker");
    }

    public static StarNubTaskManager getInstance() {
        return instance;
    }

}
