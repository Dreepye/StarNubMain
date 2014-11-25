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

package server.eventsrouter.events;

/**
 * Represents a base Status abstract class to be used
 * for customized connectionstatus eventsrouter.
 * <p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public abstract class StatusEvent extends StarNubEvent<String> {

    /**
     * boolean representing if the object is starting up
     */

    private volatile boolean startingUp;

    /**
     * boolean representing the connectionstatus
     */

    private volatile boolean online;

    public boolean isStartingUp() {
        return startingUp;
    }

    public void setStartingUp(boolean startingUp) {
        this.startingUp = startingUp;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    /**
     * Basic constructor
     */
    public StatusEvent(String EVENT_KEY, boolean startingUp, boolean online) {
        super(EVENT_KEY);
        this.startingUp = startingUp;
        this.online = online;
    }

    public String getExtraEventData(){
        return "Starting Up: " + startingUp + ", Online: " + online + ".";
    }
}
