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

import lombok.Getter;
import org.joda.time.DateTime;
import server.StarNub;

/**
 * Represents the StarboundServerStatus. When this
 * class is sent the methods can be accessed in order
 * to perform functions.
 * <p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public class StarboundServerStatusEvent extends StatusEvent {

    /**
     * boolean representing that the Starbound starbounddata.packets.starbounddata.packets.server process connectionstatus
     */

    private volatile boolean crashed;

    /**
     * boolean representing that the Starbound starbounddata.packets.starbounddata.packets.server is responsive
     */

    private volatile boolean unresponsive;

    /**
     * boolean represents if the starbounddata.packets.starbounddata.packets.server is restarting
     */

    private volatile boolean restarting;

    public boolean isCrashed() {
        return crashed;
    }

    public void setCrashed(boolean crashed) {
        this.crashed = crashed;
    }

    public boolean isUnresponsive() {
        return unresponsive;
    }

    public void setUnresponsive(boolean unresponsive) {
        this.unresponsive = unresponsive;
    }

    public boolean isRestarting() {
        return restarting;
    }

    public void setRestarting(boolean restarting) {
        this.restarting = restarting;
    }

    /**
     * StarboundServerStatus Constructor
     *
     * @param unresponsive boolean setting the responsiveness
     * @param online       boolean setting if the starbounddata.packets.starbounddata.packets.server is online
     */
    public StarboundServerStatusEvent(String EVENT_KEY, boolean crashed, boolean unresponsive, boolean startingUp, boolean online, boolean restarting) {
        super(EVENT_KEY, startingUp, online);
        this.crashed = crashed;
        this.unresponsive = unresponsive;
        this.restarting = restarting;
    }

    /**
     * @param crashed      boolean setting isCrashed
     * @param unresponsive boolean setting the Unresponsive
     * @param startingUp   boolean setting startingUp
     * @param online       boolean setting if the starbounddata.packets.starbounddata.packets.server is online
     */
    public void updateStatus(boolean crashed, boolean unresponsive, boolean startingUp, boolean online, boolean restarting) {
        this.crashed = crashed;
        this.unresponsive = unresponsive;
        this.restarting = restarting;
        setStartingUp(startingUp);
        setOnline(online);
    }

    /**
     * Sent when the starbounddata.packets.starbounddata.packets.server check detects a crashed process
     */
    public static void eventSend_Starbound_Server_Status_Crashed() {
        StarNub.getStarNubEventRouter().notify(new StarboundServerStatusEvent("Starbound_Server_Status_Crashed", true, false, false, false, false));
        StarNub.getServer().getOLDStarboundManager().getStarboundStatus().updateStatus(true, false, false, false, false);
    }

    /**
     * Sent when the starbounddata.packets.starbounddata.packets.server tcp query returns unresponsive after 12 query attempts over 120 seconds
     */
    public static void eventSend_Starbound_Server_Status_Unresponsive() {
        StarNub.getStarNubEventRouter().notify(new StarboundServerStatusEvent("Starbound_Server_Status_Unresponsive", false, true, false, false, false));
        StarNub.getServer().getOLDStarboundManager().getStarboundStatus().updateStatus(false, true, false, false, false);
    }

    /**
     * Sent when the starbounddata.packets.starbounddata.packets.server is just starting up
     */
    public static void eventSend_Starbound_Server_Status_StartingUp() {
        StarNub.getStarNubEventRouter().notify(new StarboundServerStatusEvent("Starbound_Server_Status_Starting_Up", false, false, true, false, false));
        StarNub.getServer().getOLDStarboundManager().getStarboundStatus().updateStatus(false, false, true, false, false);
    }

    /**
     * Sent periodically while the starbounddata.packets.starbounddata.packets.server is still starting up
     */
    public static void eventSend_Starbound_Server_Status_Starting_Up_Still() {
        StarNub.getStarNubEventRouter().notify(new StarboundServerStatusEvent("Starbound_Server_Status_Starting_Up_Still", false, false, true, false, false));
    }

    /**
     * Sent once the starbounddata.packets.starbounddata.packets.server is online
     */
    public static void eventSend_Starbound_Server_Status_Online() {
        StarNub.getStarNubEventRouter().notify(new StarboundServerStatusEvent("Starbound_Server_Status_Online", false, false, false, true, false));
        StarNub.getServer().getOLDStarboundManager().getStarboundStatus().updateStatus(false, false, false, true, false);
        StarNub.getServerStats().setSbOnlineTime(DateTime.now());
    }

    /**
     * Sent when the starbounddata.packets.starbounddata.packets.server has responded to a process check and TCP query
     */
    public static void eventSend_Starbound_Server_Status_Responsive() {
        StarNub.getStarNubEventRouter().notify(new TimeEvent("Starbound_Server_Status_Responsive", StarNub.getServerStats().getSbUptime()));
    }

    /**
     * Sent when the starbounddata.packets.starbounddata.packets.server has responded to a process check and TCP query
     */
    public static void eventSend_Starbound_Server_Status_Restarting() {
        StarNub.getStarNubEventRouter().notify(new StarboundServerStatusEvent("Starbound_Server_Status_Restarting", false, false, false, true, true));
        StarNub.getServer().getOLDStarboundManager().getStarboundStatus().updateStatus(false, false, false, true, true);
    }

    /**
     * Sent when the starbounddata.packets.starbounddata.packets.server was signaled to shutdown
     */
    public static void eventSend_Starbound_Server_Status_ShuttingDown() {
        StarNub.getStarNubEventRouter().notify(new StarboundServerStatusEvent("Starbound_Server_Status_Shutting_Down", false, false, false, true, StarNub.getServer().getOLDStarboundManager().getStarboundStatus().restarting));
        StarNub.getServer().getOLDStarboundManager().getStarboundStatus().updateStatus(false, false, false, true, false);
    }

    /**
     * Sent when the starbounddata.packets.starbounddata.packets.server has shutdown
     */
    public static void eventSend_Starbound_Server_Status_Shutdown() {
        StarNub.getStarNubEventRouter().notify(new StarboundServerStatusEvent("Starbound_Server_Status_Shutdown", false, false, false, false, StarNub.getServer().getOLDStarboundManager().getStarboundStatus().restarting));
        StarNub.getServer().getOLDStarboundManager().getStarboundStatus().updateStatus(false, false, false, false, false);
    }

    /**
     * Sent every time the starbounddata.packets.starbounddata.packets.server query attempt has failed.
     *
     * @param queryAttempts int indicated the number of TCP query tries
     */
    public static void eventSend_Starbound_Server_Status_TCPQuery_Failed(int queryAttempts) {
        StarNub.getStarNubEventRouter().notify(new IntegerEvent("Starbound_Server_Status_TCP_Query_Failed", queryAttempts));
    }


}
