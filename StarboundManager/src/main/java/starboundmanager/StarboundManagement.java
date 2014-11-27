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

package starboundmanager;

import starboundmanager.status.*;
import utilities.events.EventRouter;
import utilities.events.types.ObjectEvent;

/**
 * Represents ProcessManagement this will get a file path for which starbound_server.exe
 * to be using, based on the operating system.
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class StarboundManagement extends StarboundServerExe {

    private StarboundStatus stopped;
    private StarboundStatus starting;
    private StarboundStatus running;
    private StarboundStatus unresponsive;
    private StarboundStatus shuttingDown;

    protected volatile StarboundStatus status;
    protected StarboundProcess starboundProcess;
    protected int serverVersion;

    protected final EventRouter EVENT_ROUTER;
    protected final boolean EVENT_MESSAGE;

    private StarboundManagement(EventRouter EVENT_ROUTER) {
        super();
        this.stopped = new Stopped(this);
        this.starting = new Starting(this);
        this.running = new Running(this);
        this.unresponsive = new Unresponsive(this);
        this.shuttingDown = new Stopping(this);
        this.EVENT_ROUTER = EVENT_ROUTER;
        this.EVENT_MESSAGE = EVENT_ROUTER != null;
        this.status = stopped;
    }


    public StarboundStatus getStopped() {
        return stopped;
    }

    public StarboundStatus getStarting() {
        return starting;
    }

    public StarboundStatus getRunning() {
        return running;
    }

    public StarboundStatus getShuttingDown() {
        return shuttingDown;
    }

    public StarboundStatus getUnresponsive() {
        return unresponsive;
    }

    public StarboundStatus getStatus() {
        return status;
    }

    public void setStatus(StarboundStatus status) {
        this.status = status;
    }

    public StarboundProcess getStarboundProcess() {
        return starboundProcess;
    }

    public int getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(int serverVersion) {
        this.serverVersion = serverVersion;
    }

    public void start(boolean consolePrint){
        status.start();
    }

    public void isAlive(){
        status.isAlive();
    }

    public void isResponsive(String ipAddress, int port, int queryAttempts){
        status.isResponsive(ipAddress, port, queryAttempts);
    }

    public void restart(){
        status.stop();
        status.start();
    }

    public void stop(){
        status.stop();
    }

    public void configurationGenerator(){

    }

    @SuppressWarnings("unchecked")
    public void printOrEvent(String eventKey, Object eventData){
        if (EVENT_MESSAGE) {
            EVENT_ROUTER.eventNotify(new ObjectEvent(eventKey, eventData));
            //TODO Management System - Player List, Banning, Kicking Plugin
        } else {
            System.out.println(eventKey + ": " +eventData);
        }
    }
}
