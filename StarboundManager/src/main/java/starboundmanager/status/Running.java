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
import utilities.concurrency.thread.ThreadSleep;

/**
 * Represents StarNubs InitializingConnection Status
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class Running extends StarboundStatus {

    public Running(StarboundManagement STARBOUND_MANAGEMENT) {
        super(STARBOUND_MANAGEMENT);
    }

    @Override
    public boolean start() {
        STARBOUND_MANAGEMENT.printOrEvent("Starbound_Server_Status_Already_Started", STARBOUND_MANAGEMENT);
        return true;
    }

    @Override
    public boolean isAlive() {
        boolean isAlive = false;
        try {
            isAlive = STARBOUND_MANAGEMENT.getStarboundProcess().getProcess().isAlive();
        }catch (NullPointerException e){
            /* Silent Catch */
        }
        if (!isAlive){
            STARBOUND_MANAGEMENT.printOrEvent("Starbound_Server_Status_Crashed", STARBOUND_MANAGEMENT);
            STARBOUND_MANAGEMENT.setStatus(STARBOUND_MANAGEMENT.getStopped());
        }
        return isAlive;
    }

    @Override
    public boolean isResponsive(String ipAddress, int port, int queryAttempts) {
        boolean isResponsive = false;
        try {
            isResponsive = STARBOUND_MANAGEMENT.getStarboundProcess().getProcess().isAlive();
        }catch (NullPointerException e){
            /* Silent Catch */
        }
        if (!isResponsive){
            STARBOUND_MANAGEMENT.setStatus(STARBOUND_MANAGEMENT.getUnresponsive());
        }
        return isResponsive;
    }

    @Override
    public boolean stop() {
        STARBOUND_MANAGEMENT.printOrEvent("Starbound_Server_Status_Shutting_Down", STARBOUND_MANAGEMENT);
        STARBOUND_MANAGEMENT.getStarboundProcess().getProcess().destroy();
        ThreadSleep.timerSeconds(2);
        if (isAlive()){
            STARBOUND_MANAGEMENT.printOrEvent("Starbound_Server_Status_Shutdown_Error_Process", STARBOUND_MANAGEMENT);
            STARBOUND_MANAGEMENT.getStarboundProcess().getProcess().destroyForcibly();
            ThreadSleep.timerSeconds(2);
        }
        if (isAlive()){
            STARBOUND_MANAGEMENT.printOrEvent("Starbound_Server_Status_Shutdown_Fatal_Process", STARBOUND_MANAGEMENT);
            STARBOUND_MANAGEMENT.setStatus(STARBOUND_MANAGEMENT.getUnresponsive());
            return false;
        } else {
            STARBOUND_MANAGEMENT.printOrEvent("Starbound_Server_Status_Shutdown", STARBOUND_MANAGEMENT);
            STARBOUND_MANAGEMENT.setStatus(STARBOUND_MANAGEMENT.getStopped());
            return true;
        }
    }
}
