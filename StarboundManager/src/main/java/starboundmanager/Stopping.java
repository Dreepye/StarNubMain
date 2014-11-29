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


import utilities.concurrency.thread.ThreadSleep;

/**
 * Represents StarNubs Disconnected Status
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class Stopping extends StarboundStatus {

    public Stopping(StarboundManager STARBOUND_MANAGEMENT) {
        super(STARBOUND_MANAGEMENT);
    }

    /**
     * Recommended: For internal use.
     * <p>
     * Uses: This will not work while the network is stopping
     *
     * @param ipAddress String representing the address to TCP Query
     * @param port int representing the port to query
     * @param STREAM_EVENT_MESSAGE boolean representing if you are going to send the Starbound stream through an event router
     * @param STREAM_CONSOLE_PRINT boolean representing if you are going to print out the Starbound stream through the console
     * @return boolean representing if the network started
     */
    @Override
    public boolean start(String ipAddress, int port, boolean STREAM_EVENT_MESSAGE, boolean STREAM_CONSOLE_PRINT) {
        STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Stopping", STARBOUND_MANAGEMENT);
        return false;
    }

    /**
     * Recommended: For internal use.
     * <p>
     * Uses: This will attempt to see if the Starbound process is alive, but depending on the current status may or may not work
     *
     * @return boolean representing if the Starbound process is alive
     */
    @Override
    public boolean isAlive() {
        STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Stopping", STARBOUND_MANAGEMENT);
        return false;
    }

    /**
     * Recommended: For internal use.
     * <p>
     * Uses: This cannot be used while the network is stopping
     *
     * @param ipAddress String representing the address to TCP Query
     * @param port int representing the port to query
     * @param queryAttempts int representing the number of queries to attempt
     * @return boolean representing if the starbound network is responsive
     */
    @Override
    public boolean isResponsive(String ipAddress, int port, int queryAttempts) {
        STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Stopping", STARBOUND_MANAGEMENT);
        return false;
    }

    /**
     * Recommended: For internal use.
     * <p>
     * Uses: This will attempt to stop the Starbound process and transition it to unresponsive if we were unable to stop it
     * or stopped if the process was stopped
     *
     * @return boolean representing if the network is stopped
     */
    @Override
    public boolean stop() {
        STARBOUND_MANAGEMENT.getStarboundProcess().getProcess().destroy();
        ThreadSleep.timerSeconds(2);
        if (isAlive()){
            STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Shutdown_Error_Process", STARBOUND_MANAGEMENT);
            STARBOUND_MANAGEMENT.getStarboundProcess().getProcess().destroyForcibly();
            ThreadSleep.timerSeconds(2);
        }
        if (isAlive()){
            STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Shutdown_Fatal_Process", STARBOUND_MANAGEMENT);
            STARBOUND_MANAGEMENT.setStatus(STARBOUND_MANAGEMENT.getUNRESPONSIVE());
            return false;
        } else {
            STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Shutdown", STARBOUND_MANAGEMENT);
            STARBOUND_MANAGEMENT.setStatus(STARBOUND_MANAGEMENT.getSTOPPED());
            return true;
        }
    }
}
