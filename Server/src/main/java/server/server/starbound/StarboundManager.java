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

package server.server.starbound;

import lombok.Getter;
import org.codehome.utilities.timers.ThreadSleep;
import server.eventsrouter.events.StarboundServerStatusEvent;
import server.eventsrouter.events.StarboundQueryTaskEvent;

/**
 * This class will be the interface for users to manipulate
 * the classes within this package, to prevent duplicate threads
 * and or illegal accesses.
 * <p>
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public enum StarboundManager {
    INSTANCE;

    /**
     * StarNub internal use only
     */

    private StarboundServerStatusEvent starboundStatus;

    /**
     * StarNub internal use only
     */

    private StarboundQueryTaskEvent starboundQueryTask;

    /**
     * This cannot be done if the StarboundServerStatus is already set
     */
    public void setStarboundStatus() {
        if (starboundStatus != null) {
            throw new UnsupportedOperationException("Cannot redefine StarboundServerStatus singleton");
        }
        starboundStatus = new StarboundServerStatusEvent("Starbound Status", false, false, false, false, false);
    }

    /**
     * This cannot be done if the StarboundQueryTask is already set
     */
    public void setStarboundQueryTask() {
        if (starboundQueryTask != null) {
            throw new UnsupportedOperationException("Cannot redefine StarboundQueryTask singleton");
        }
        starboundQueryTask = new StarboundQueryTaskEvent("Starbound Status", false, false, false);
    }

    /**
     * This method will startup Starbound and send an event once
     * the starbounddata.packets.starbounddata.packets.server is online. It will not work if this method was
     * already activated and has not completed a startup.
     * <p>
     * @return boolean representing success of calling this method does not mean
     * that the method executed its task through though. That is sent through an Event
     */
    public synchronized boolean startUp() {
        if(!starboundStatus.isOnline() && !starboundStatus.isStartingUp()) {
            StarboundServerStatusEvent.eventSend_Starbound_Server_Status_StartingUp();
            ProcessManagement.sb_ProcessStart();
            StarboundQueryTaskEvent.eventSend_Server_Query_Task_Starting();
            new Thread (new StatusQuery("startup"),"StarNub - Starbound - Server Status Query").start();
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method will check the Starbound Server Process and
     * responsiveness through a TCP Query. It will not work if this method was
     * already activated and has not completed a query cycle.
     * <p>
     * @return boolean representing success of calling this method does not mean
     * that the method executed its task through though. That is sent through an Event
     */
    public synchronized boolean statusQuery() {
        if (!ProcessManagement.sb_ProcessStatus()) {
            StarboundServerStatusEvent.eventSend_Starbound_Server_Status_Crashed();
            StarboundQueryTaskEvent.eventSend_Server_Query_Task_Complete();
            return true;
        }
        if(starboundStatus.isOnline() && !starboundStatus.isStartingUp() && !starboundQueryTask.isRunning() && !starboundQueryTask.isStarting()) {
            StarboundQueryTaskEvent.eventSend_Server_Query_Task_Starting();
            new Thread (new StatusQuery("responsiveness"),"StarNub - Starbound - Server Status Query").start();
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method will stop the Starbound Server Process.
     * It will not work if this method was already activated
     * and has not finished shutting down.
     * <p>
     * @return boolean representing success of calling this method does not mean
     * that the method executed its task through though. That is sent through an Event
     */
    public synchronized boolean shutdown(boolean isRestarting) {
        if (isRestarting){
            StarboundServerStatusEvent.eventSend_Starbound_Server_Status_Restarting();
        }
        if(starboundStatus.isOnline()) {
            StarboundServerStatusEvent.eventSend_Starbound_Server_Status_ShuttingDown();
            ProcessManagement.sb_ProcessKill();
            while (ProcessManagement.sb_ProcessStatus()) {
              new ThreadSleep().timerSeconds(1);
            }
            StarboundServerStatusEvent.eventSend_Starbound_Server_Status_Shutdown();
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method will restart the Starbound Server.
     * <p>
     * @return boolean representing success of calling this method does not mean
     * that the method executed its task through though. That is sent through an Events
     * in the above methods
     */
    public synchronized boolean restart() {
        return shutdown(true) && startUp();
    }

    /**
     * THIS METHOD SHOULD NOT BE USED BY ANY PLUGINS. THIS METHOD IS
     * SOLEY FOR ERROR CORRECTION AND DEBUGGING OF STARNUB.
     */
    public synchronized void configGeneratorStart() {
        ProcessManagement.sb_ProcessStart();
    }

    /**
     * THIS METHOD SHOULD NOT BE USED BY ANY PLUGINS. THIS METHOD IS
     * SOLEY FOR ERROR CORRECTION AND DEBUGGING OF STARNUB.
     */
    public synchronized void configGeneratorStop() {
        ProcessManagement.sb_ProcessKill();
    }
}
