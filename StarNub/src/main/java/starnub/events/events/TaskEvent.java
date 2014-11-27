/*
* Copyright (C) 2014 www.StarNub.org - Underbalanced
*
* This utilities.file is part of org.starnub a Java Wrapper for Starbound.
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

package starnub.events.events;

import utilities.events.types.Event;

public abstract class TaskEvent extends Event<String> {

    /**
     * This indicates that we are starting a task
     */

    private volatile boolean starting;

    /**
     * This indicates the task is running
     */

    private volatile boolean running;

    /**
     * This indicates the task has completed
     */

    private volatile boolean complete;

    public boolean isStarting() {
        return starting;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setStarting(boolean starting) {
        this.starting = starting;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    protected TaskEvent(String EVENT_KEY, boolean starting, boolean running, boolean complete) {
        super(EVENT_KEY, null);
        this.starting = starting;
        this.running = running;
        this.complete = complete;
    }

    public String getExtraEventData(){
        return "Starting: " + starting + ", Running: " + running + ", Complete: " + complete;
    }
}
