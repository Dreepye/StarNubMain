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

package utilities.concurrent.task;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Represents a InternalScheduledTask which is to be used with the {@link TaskManager}
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public class InternalTask extends ScheduledTask {

    public InternalTask(String OWNER, String NAME, Runnable RUNNABLE, ScheduledFuture<?> scheduledFuture) {
        super(OWNER, NAME, RUNNABLE, scheduledFuture);
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
}
