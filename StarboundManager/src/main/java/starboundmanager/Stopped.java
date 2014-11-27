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


/**
 * Represents StarNubs Connected Status
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class Stopped extends StarboundStatus{

    public Stopped(StarboundManagement STARBOUND_MANAGEMENT) {
        super(STARBOUND_MANAGEMENT);
    }

    /**
     * Recommended: For internal use.
     * <p>
     * Uses: This will attempt to start the Starbound process and transition the state to starting
     *
     * @param ipAddress String representing the address to TCP Query
     * @param port int representing the port to query
     * @param STREAM_EVENT_MESSAGE boolean representing if you are going to send the Starbound stream through an event router
     * @param STREAM_CONSOLE_PRINT boolean representing if you are going to print out the Starbound stream through the console
     * @return boolean representing if the server started
     */
    @Override
    public boolean start(String ipAddress, int port, boolean STREAM_EVENT_MESSAGE, boolean STREAM_CONSOLE_PRINT) {
        STARBOUND_MANAGEMENT.setStatus(STARBOUND_MANAGEMENT.getSTARTING());
        return STARBOUND_MANAGEMENT.getStatus().start(ipAddress, port, STREAM_EVENT_MESSAGE, STREAM_CONSOLE_PRINT);
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
        STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Stopped", STARBOUND_MANAGEMENT);
        return false;
    }

    /**
     * Recommended: For internal use.
     * <p>
     * Uses: This cannot be used while the server is stopped
     *
     * @param ipAddress String representing the address to TCP Query
     * @param port int representing the port to query
     * @param queryAttempts int representing the number of queries to attempt
     * @return boolean representing if the starbound server is responsive
     */
    @Override
    public boolean isResponsive(String ipAddress, int port, int queryAttempts) {
        STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Stopped", STARBOUND_MANAGEMENT);
        return false;
    }

    /**
     * Recommended: For internal use.
     * <p>
     * Uses: This cannot be used with the server is stopped
     *
     * @return boolean representing if the server is stopped
     */
    @Override
    public boolean stop() {
        STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Stopped", STARBOUND_MANAGEMENT);
        return true;
    }
}
