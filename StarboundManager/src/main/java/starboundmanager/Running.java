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
 * Represents StarNubs InitializingConnection Status
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class Running extends StarboundStatus {

    public Running(StarboundManager STARBOUND_MANAGEMENT) {
        super(STARBOUND_MANAGEMENT);
    }

    /**
     * Recommended: For connections use.
     * <p>
     * Uses: This will not work because the network is already running
     *
     * @param ipAddress String representing the address to TCP Query
     * @param port int representing the port to query
     * @param STREAM_EVENT_MESSAGE boolean representing if you are going to send the Starbound stream through an event router
     * @param STREAM_CONSOLE_PRINT boolean representing if you are going to print out the Starbound stream through the console
     * @return boolean representing if the network started
     */
    @Override
    public boolean start(String ipAddress, int port, boolean STREAM_EVENT_MESSAGE, boolean STREAM_CONSOLE_PRINT) {
        STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Running", STARBOUND_MANAGEMENT);
        return true;
    }

    /**
     * Recommended: For connections use.
     * <p>
     * Uses: This will attempt to see if the Starbound process is alive
     *
     * @return boolean representing if the Starbound process is alive
     */
    @Override
    public boolean isAlive() {
        boolean isAlive = false;
        try {
            isAlive = STARBOUND_MANAGEMENT.getStarboundProcess().getProcess().isAlive();
        }catch (NullPointerException e){
            /* Silent Catch */
        }
        if (!isAlive){
            STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Crashed", STARBOUND_MANAGEMENT);
            STARBOUND_MANAGEMENT.setStatus(STARBOUND_MANAGEMENT.getSTOPPED());
        }
        return isAlive;
    }

    /**
     * Recommended: For connections use.
     * <p>
     * Uses: This will attempt to see if the Starbound network is responsive, but depending on the current status may or may not work, queries are attempted
     * every 10 seconds. Setting the queryAttempts to 12 for example would be 120 seconds worth of tries, which would be 2 minutes. This will transition the state to unresponsive
     * if unresponsive
     *
     * @param ipAddress String representing the address to TCP Query
     * @param port int representing the port to query
     * @param queryAttempts int representing the number of queries to attempt
     * @return boolean representing if the starbound network is responsive
     */
    @Override
    public boolean isResponsive(String ipAddress, int port, int queryAttempts) {
        boolean isResponsive = false;
        try {
            isResponsive = STARBOUND_MANAGEMENT.getStarboundProcess().getProcess().isAlive();
        }catch (NullPointerException e){
            /* Silent Catch */
        }
        if (!isResponsive){
            STARBOUND_MANAGEMENT.setStatus(STARBOUND_MANAGEMENT.getUNRESPONSIVE());
        }
        return isResponsive;
    }

    /**
     * Recommended: For connections use.
     * <p>
     * Uses: This will attempt to stop the Starbound process by transition the status to stopping and then calling the stop method of the stopping
     * status
     *
     * @return boolean representing if the network is stopped
     */
    @Override
    public boolean stop() {
        STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Shutting_Down", STARBOUND_MANAGEMENT);
        STARBOUND_MANAGEMENT.setStatus(STARBOUND_MANAGEMENT.getSTOPPING());
        return STARBOUND_MANAGEMENT.getStatus().stop();
    }
}
