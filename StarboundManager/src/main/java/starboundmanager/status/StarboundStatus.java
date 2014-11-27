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

package starboundmanager.status;

import starboundmanager.StarboundManagement;
import starboundmanager.StarboundQuery;
import utilities.concurrency.thread.ThreadSleep;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

public abstract class StarboundStatus {

    public final StarboundManagement STARBOUND_MANAGEMENT;

    public StarboundStatus(StarboundManagement STARBOUND_MANAGEMENT) {
        this.STARBOUND_MANAGEMENT = STARBOUND_MANAGEMENT;
    }

    public abstract boolean start();
    public abstract boolean isAlive();
    public abstract boolean isResponsive(String ipAddress, int port, int queryAttempts);
    public abstract boolean stop();

    public boolean startingUpListener(String ipAddress, int port){
        boolean online = query(ipAddress, port, 5, 48);
        STARBOUND_MANAGEMENT.printOrEvent("Starbound_Server_Status_Starting_Up", STARBOUND_MANAGEMENT);
        return online;
    }

    public boolean unresponsiveListener(String ipAddress, int port, int timeout, int queryAttempts){
        STARBOUND_MANAGEMENT.printOrEvent("Starbound_Server_Status_Responsive", STARBOUND_MANAGEMENT);
        boolean online = query(ipAddress, port, timeout, queryAttempts);
        if (online){
            STARBOUND_MANAGEMENT.printOrEvent("Starbound_Server_Status_Responsive", STARBOUND_MANAGEMENT);
        } else {
            STARBOUND_MANAGEMENT.printOrEvent("Starbound_Server_Status_Unresponsive", STARBOUND_MANAGEMENT);
        }
        return false;
    }

    private boolean query(String ipAddress, int port, int timeout, int queryAttempts) {
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
                if (e instanceof ConnectException) {
                    System.out.println("Connection Refused");
                    ThreadSleep.timerMiliseconds(timeout);
                } else if (e instanceof SocketTimeoutException) {
                    System.out.println("Socket Timed Out");
                } else {
                    e.printStackTrace();
                    ThreadSleep.timerMiliseconds(timeout);
                }
                STARBOUND_MANAGEMENT.printOrEvent("Starbound_Server_Status_TCP_Query_Failed", unresponsiveCount);
                unresponsiveCount++;
            }
        }
        return false;
    }
}
