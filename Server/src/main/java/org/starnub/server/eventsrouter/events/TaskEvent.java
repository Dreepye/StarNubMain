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

package org.starnub.server.eventsrouter.events;

import lombok.Getter;
import lombok.Setter;

public abstract class TaskEvent extends StarNubEvent<String> {

    /**
     * This indicates that we are starting a task
     */
    @Getter @Setter
    private volatile boolean starting;

    /**
     * This indicates the task is running
     */
    @Getter @Setter
    private volatile boolean running;

    /**
     * This indicates the task has completed
     */
    @Getter @Setter
    private volatile boolean complete;

    protected TaskEvent(String EVENT_KEY, boolean starting, boolean running, boolean complete) {
        super(EVENT_KEY);
        this.starting = starting;
        this.running = running;
        this.complete = complete;
    }

    public String getExtraEventData(){
        return "Starting: " + starting + ", Running: " + running + ", Complete: " + complete;
    }
}
