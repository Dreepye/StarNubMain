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

package starboundmanager;

import utilities.concurrent.thread.ThreadSleep;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

public abstract class StarboundStatus {

    public final StarboundManager STARBOUND_MANAGEMENT;

    public StarboundStatus(StarboundManager STARBOUND_MANAGEMENT) {
        this.STARBOUND_MANAGEMENT = STARBOUND_MANAGEMENT;
    }

    /**
     * Recommended: For connections use.
     * <p>
     * Uses: This will attempt to start the Starbound process, but depending on the current status may or may not work
     *
     * @param ipAddress String representing the address to TCP Query
     * @param port int representing the port to query
     * @param STREAM_EVENT_MESSAGE boolean representing if you are going to send the Starbound stream through an event router
     * @param STREAM_CONSOLE_PRINT boolean representing if you are going to print out the Starbound stream through the console
     * @return boolean representing if the starnubdata.network started
     */
    public abstract boolean start(String ipAddress, int port, boolean STREAM_EVENT_MESSAGE, boolean STREAM_CONSOLE_PRINT);

    /**
     * Recommended: For connections use.
     * <p>
     * Uses: This will attempt to see if the Starbound process is alive
     *
     * @return boolean representing if the Starbound process is alive
     */
    public abstract boolean isAlive();

    /**
     * Recommended: For connections use.
     * <p>
     * Uses: This will attempt to see if the Starbound starnubdata.network is responsive, but depending on the current status may or may not work, queries are attempted
     * every 10 seconds. Setting the queryAttempts to 12 for example would be 120 seconds worth of tries, which would be 2 minutes.
     *
     * @param ipAddress String representing the address to TCP Query
     * @param port int representing the port to query
     * @param queryAttempts int representing the number of queries to attempt
     * @return boolean representing if the starbound starnubdata.network is responsive
     */
    public abstract boolean isResponsive(String ipAddress, int port, int queryAttempts);

    /**
     * Recommended: For connections use.
     * <p>
     * Uses: This will attempt to stop the Starbound process, but depending on the current status may or may not work
     *
     * @return boolean representing if the starnubdata.network is stopped
     */
    public abstract boolean stop();

    /**
     * Recommended: For connections use.
     * <p>
     * Uses: This will attempt will query the Starbound starnubdata.network every 5 seconds until it is started, It will check up to 48 times which is 4 minutes
     *
     * @param ipAddress String representing the address to TCP Query
     * @param port int representing the port to query
     * @return boolean representing that the starnubdata.network has started up or not
     */
    protected boolean startUpListener(String ipAddress, int port){
        STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Starting_Up", STARBOUND_MANAGEMENT);
        return query(ipAddress, port, 5, 48);
    }


    /**
     * Recommended: For connections use.
     * <p>
     * Uses: This will attempt to see if the Starbound starnubdata.network is responsive, but depending on the current status may or may not work, queries are attempted
     * every 10 seconds. Setting the queryAttempts to 12 for example would be 120 seconds worth of tries, which would be 2 minutes
     *
     * @param ipAddress String representing the address to TCP Query
     * @param port int representing the port to query
     * @param queryAttempts int representing the number of queries to attempt
     * @return boolean representing if the starnubdata.network is started
     */
    protected boolean responsiveListener(String ipAddress, int port, int timeout, int queryAttempts){
        STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Responsive", STARBOUND_MANAGEMENT);
        boolean online = query(ipAddress, port, timeout, queryAttempts);
        if (online){
            STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Responsive", STARBOUND_MANAGEMENT);
        } else {
            STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Unresponsive", STARBOUND_MANAGEMENT);
        }
        return online;
    }


    /**
     * Recommended: For connections use.
     * <p>
     * Uses: This will attempt to see if the Starbound starnubdata.network is responsive, but depending on the current status may or may not work, queries are attempted
     * every 10 seconds. Setting the queryAttempts to 12 for example would be 120 seconds worth of tries, which would be 2 minutes
     *
     * @param ipAddress String representing the address to TCP Query
     * @param port int representing the port to query
     * @param timeout int representing the timeout in seconds
     * @param queryAttempts int representing the number of queries to attempt
     * @return boolean representing if the starnubdata.network is responsive and was able to get the starnubdata.network version
     */
    protected boolean query(String ipAddress, int port, int timeout, int queryAttempts) {
        int unresponsiveCount = 0;
        while (unresponsiveCount < queryAttempts) {
            try {
                int version = StarboundQuery.query(ipAddress, port, timeout);
                if (version > 0) {
                    if (STARBOUND_MANAGEMENT.getServerVersion() == 0) {
                        STARBOUND_MANAGEMENT.setServerVersion(version);
                    }
                    return true;
                }
            } catch (IOException e) {
                String reason = "Unknown";
                if (e instanceof ConnectException) {
                    reason = "Connection_Refused";
                    ThreadSleep.timerMiliseconds(timeout);
                } else if (e instanceof SocketTimeoutException) {
                    System.out.println("Socket_Timeout");
                } else {
                    e.printStackTrace();
                    ThreadSleep.timerMiliseconds(timeout);
                }
                STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_TCP_Query_Failed_" + reason, unresponsiveCount);
                unresponsiveCount++;
            }
        }
        return false;
    }
}
