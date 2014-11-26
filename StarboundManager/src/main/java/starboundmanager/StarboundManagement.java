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
import utilities.concurrency.thread.ThreadSleep;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

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

    private volatile StarboundStatus status;
    protected static StarboundProcess starboundProcess;
    private int serverVersion;

    private StarboundManagement() {
        super();
        stopped = new Stopped(this);
        starting = new Starting(this);
        running = new Running(this);
        unresponsive = new Unresponsive(this);
        shuttingDown = new Stopping(this);
        status = stopped;
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

    public StarboundStatus getStatus() {
        return status;
    }

    public static starboundmanager.StarboundProcess getStarboundProcess() {
        return starboundProcess;
    }

    public int getServerVersion() {
        return serverVersion;
    }

    public void start(){
        status.start();
    }

    public void isAlive(){
        status.isAlive();
    }

    public void isResponsive(String ipAddress, int port, int queryAttempts){
        status.isResponsive();
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

    public void startingUpListener(){
        //No increment
        //Task will check process then Query will be a task every 5 seconds
        //Send Responsive event out
    }

    public void unresponsiveListener(){
        //Increment for each responsive
        //Task will check process then Query will be a task every 30 seconds
        //Responsive will set responsive variable to 0
    }

    private boolean query(String ipAddress, int port, int timeout, int queryAttempts) {
        int unresponsiveCount = 0;
        while (unresponsiveCount < queryAttempts) {
            try {
                int version = StarboundQuery.query(ipAddress, port, timeout);
                if (version > 0) {
                    if (serverVersion == 0) {
                        serverVersion = version;
                    }
                    return true;
                }
            } catch (IOException e) {
                if (e instanceof ConnectException) {
                    System.out.println("Connection Refused");
                    ThreadSleep.timerSeconds(10);
                } else if (e instanceof SocketTimeoutException) {
                    System.out.println("Socket Timed Out");
                } else {
                    e.printStackTrace();
                    ThreadSleep.timerSeconds(10);
                }
                unresponsiveCount++;
            }
        }
        return false;
    }
}
