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

package utilities.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a NamedThreadFactory, this class is used to create a thread with a
 * specific NAME.
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class NamedThreadFactory implements ThreadFactory {

    private final String NAME;
    private final AtomicInteger INDEX = new AtomicInteger(1);

    public NamedThreadFactory(String NAME) {
        this.NAME = NAME + " - ";
    }

    /**
     * This will create a thread using the String NAME with a increment counter
     *
     * @param r Runnable representing the Runnable to be wrapped in a thread
     * @return Thread representing the new Thread which wraps the Runnable
     */
    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, NAME + INDEX.getAndIncrement());
        if (thread.isDaemon())
            thread.setDaemon(false);
        if (thread.getPriority() != Thread.NORM_PRIORITY)
            thread.setPriority(Thread.NORM_PRIORITY);
        return thread;
    }
}
