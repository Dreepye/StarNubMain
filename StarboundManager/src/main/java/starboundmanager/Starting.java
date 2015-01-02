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


import java.io.IOException;

/**
 * Represents StarNubs PendingConnection Status
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class Starting extends StarboundStatus {

    public Starting(StarboundManager STARBOUND_MANAGEMENT) {
        super(STARBOUND_MANAGEMENT);
    }

    /**
     * Recommended: For connections use.
     * <p>
     * Uses: This will not work unless the starnubdata.network is stopped and is called from that method, if starting this will return true
     * this will transition the state to running or stopped
     *
     * @param ipAddress String representing the address to TCP Query
     * @param port int representing the port to query
     * @param STREAM_EVENT_MESSAGE boolean representing if you are going to send the Starbound stream through an events router
     * @param STREAM_CONSOLE_PRINT boolean representing if you are going to print out the Starbound stream through the console
     * @return boolean representing if the starnubdata.network started
     */
    @Override
    public boolean start(String ipAddress, int port, boolean STREAM_EVENT_MESSAGE, boolean STREAM_CONSOLE_PRINT) {
        STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Starting", STARBOUND_MANAGEMENT);
        if (!isAlive()){
            try {
                STARBOUND_MANAGEMENT.setStarboundProcess(STREAM_EVENT_MESSAGE, STREAM_CONSOLE_PRINT);
            } catch (IOException e) {
                STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Cannot_Start_Starbound_Server_Exe", STARBOUND_MANAGEMENT);
                e.printStackTrace();
            }
            boolean running = startUpListener(ipAddress, port);
            if (running) {
                STARBOUND_MANAGEMENT.setStatus(STARBOUND_MANAGEMENT.getRUNNING());
                STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Online", STARBOUND_MANAGEMENT);
                STARBOUND_MANAGEMENT.setStartTime(System.currentTimeMillis());
                STARBOUND_MANAGEMENT.setRestarting(false);
                return true;
            } else {
                STARBOUND_MANAGEMENT.setStatus(STARBOUND_MANAGEMENT.getSTOPPED());
                STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Could_Not_Start", STARBOUND_MANAGEMENT);
                return false;
            }
        }
        return true;
    }

    /**
     * Recommended: For connections use.
     * <p>
     * Uses: This will attempt to see if the Starbound process is alive, but depending on the current status may or may not work
     *
     * @return boolean representing if the Starbound process is alive
     */
    @Override
    public boolean isAlive() {
        return super.isAlive();
    }

    /**
     * Recommended: For connections use.
     * <p>
     * Uses: This cannot be used while the starnubdata.network is starting
     *
     * @param ipAddress String representing the address to TCP Query
     * @param port int representing the port to query
     * @param queryAttempts int representing the number of queries to attempt
     * @return boolean representing if the starbound starnubdata.network is responsive
     */
    @Override
    public boolean isResponsive(String ipAddress, int port, int queryAttempts) {
        STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Starting", STARBOUND_MANAGEMENT);
        return false;
    }

    /**
     * Recommended: For connections use.
     * <p>
     * Uses: This cannot be used when the starnubdata.network is stopped
     *
     * @return boolean representing if the starnubdata.network is stopped
     */
    @Override
    public boolean stop() {
        STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Starting", STARBOUND_MANAGEMENT);
        return false;
    }
}
