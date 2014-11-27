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

import utilities.events.EventRouter;
import utilities.events.types.ObjectEvent;
import utilities.file.simplejson.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

/**
 * Represents ProcessManagement this will get a file path for which starbound_server.exe
 * to be using, based on the operating system.
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class StarboundManagement extends StarboundServerExe {

    private final StarboundStatus stopped;
    private final StarboundStatus starting;
    private final StarboundStatus running;
    private final StarboundStatus unresponsive;
    private final StarboundStatus stopping;

    protected volatile StarboundStatus status;
    protected StarboundProcess starboundProcess;
    protected StarboundConfiguration starboundConfiguration;
    protected int serverVersion;

    protected final EventRouter EVENT_ROUTER;
    protected final boolean EVENT_MESSAGE;

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will construct a StarboundManagement with an EventRouter, if non is provided by using null then
     * it will ignore not having an event router.
     *
     * @param EVENT_ROUTER EventRouter an event router from the Utilities Library
     */
    private StarboundManagement(EventRouter EVENT_ROUTER) {
        super();
        this.stopped = new Stopped(this);
        this.starting = new Starting(this);
        this.running = new Running(this);
        this.unresponsive = new Unresponsive(this);
        this.stopping = new Stopping(this);
        this.EVENT_ROUTER = EVENT_ROUTER;
        this.EVENT_MESSAGE = EVENT_ROUTER != null;
        this.status = stopped;
    }

    /**
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This will return the Stopped Status
     *
     * @return StarboundStatus
     */
    protected StarboundStatus getStopped() {
        return stopped;
    }

    /**
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This will return the Starting Status
     *
     * @return StarboundStatus
     */
    protected StarboundStatus getStarting() {
        return starting;
    }

    /**
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This will return the Running Status
     *
     * @return StarboundStatus
     */
    protected StarboundStatus getRunning() {
        return running;
    }

    /**
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This will return the Stopping Status
     *
     * @return StarboundStatus
     */
    protected StarboundStatus getStopping() {
        return stopping;
    }

    /**
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This will return the Unresponsive Status
     *
     * @return StarboundStatus
     */
    protected StarboundStatus getUnresponsive() {
        return unresponsive;
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will return the Starbound Status and can be casted into (Running, Starting, Stopped, Stopping, Unresponsive) and used
     * as a per status bases
     *
     * @return StarboundStatus any class representing StarboundStatus
     */
    public StarboundStatus getStatus() {
        return status;
    }

    /**
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This will set the Starbound Server status
     *
     * @param status StarboundStatus the status class to set
     */
    protected void setStatus(StarboundStatus status) {
        this.status = status;
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will return the StarboundProcess which contains the starbound process
     *
     * @return StarboundProcess
     */
    public StarboundProcess getStarboundProcess() {
        return starboundProcess;
    }

    /**
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This will set the Starbound Server version
     *
     * @param STREAM_EVENT_MESSAGE boolean representing if you are going to send the Starbound stream through an event router
     * @throws IOException
     */
    protected void setStarboundProcess(boolean STREAM_EVENT_MESSAGE, boolean STREAM_CONSOLE_PRINT) throws IOException {
        this.starboundProcess = new StarboundProcess(this,STREAM_EVENT_MESSAGE, STREAM_CONSOLE_PRINT);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will either print to console or send events
     *
     * @return StarboundConfiguration returns the starbound configuration class
     */
    public StarboundConfiguration getStarboundConfiguration() {
        return starboundConfiguration;
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will either print to console or send events
     *
     * @param STARBOUND_CONFIGURATION String representing the name of the starbound configuration
     * @throws FileNotFoundException throws this exception if the file does not exist
     */
    public void setStarboundConfiguration(String STARBOUND_CONFIGURATION) throws FileNotFoundException {
        this.starboundConfiguration = new StarboundConfiguration(this, STARBOUND_CONFIGURATION);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Gets the server version
     *
     * @return int representing the server version
     */
    public int getServerVersion() {
        return serverVersion;
    }

    /**
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This will set the Starbound Server version
     *
     * @param serverVersion int representing the server version
     */
    protected void setServerVersion(int serverVersion) {
        this.serverVersion = serverVersion;
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will attempt to start the Starbound Process but may not work depending on the current state
     *
     * @param ipAddress String representing the address to TCP Query
     * @param port int representing the port to query
     * @param STREAM_EVENT_MESSAGE boolean representing if you are going to send the Starbound stream through an event router
     * @param STREAM_CONSOLE_PRINT boolean representing if you are going to print out the Starbound stream through the console
     * @return boolean representing if the server started
     */
    public boolean start(String ipAddress, int port, boolean STREAM_EVENT_MESSAGE, boolean STREAM_CONSOLE_PRINT){
        return status.start(ipAddress, port, STREAM_EVENT_MESSAGE, STREAM_CONSOLE_PRINT);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will attempt to see if the Starbound process is alive, but depending on the current status may or may not work
     *
     * @return boolean representing if the Starbound process is alive
     */
    public boolean isAlive(){
        return status.isAlive();
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will attempt to see if the Starbound server is responsive, but depending on the current status may or may not work, queries are attempted
     * every 10 seconds. Setting the queryAttempts to 12 for example would be 120 seconds worth of tries, which would be 2 minutes.
     *
     * @param ipAddress String representing the address to TCP Query
     * @param port int representing the port to query
     * @param queryAttempts int representing the number of queries to attempt
     * @return boolean representing if the starbound server is responsive
     */
    public boolean isResponsive(String ipAddress, int port, int queryAttempts){
        return status.isResponsive(ipAddress, port, queryAttempts);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will attempt to restart the starbound process by using the stop() and start() methods and may or may not work depending on the state
     *
     * @param ipAddress String representing the address to TCP Query
     * @param port int representing the port to query
     * @param STREAM_EVENT_MESSAGE boolean representing if you are going to send the Starbound stream through an event router
     * @param STREAM_CONSOLE_PRINT boolean representing if you are going to print out the Starbound stream through the console
     * @return boolean representing if the server stopped and started successfully
     */
    public boolean restart(String ipAddress, int port, boolean STREAM_EVENT_MESSAGE, boolean STREAM_CONSOLE_PRINT){
        printOrEvent("Starbound_Status_Restarting", this);
        return status.stop() && status.start(ipAddress, port, STREAM_EVENT_MESSAGE, STREAM_CONSOLE_PRINT);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will attempt to stop the Starbound process, but depending on the current status may or may not work
     *
     * @return boolean representing if the server is stopped
     */
    public boolean stop(){
        return status.stop();
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will attempt to generate a Starbound Configuration if one does not exist
     *
     * @param STARBOUND_CONFIGURATION String representing the name of the starbound configuration
     * @throws IOException various exceptions can be thrown, IO or File related
     */
    public void configurationGenerator(String STARBOUND_CONFIGURATION) throws IOException {
        if (starboundConfiguration == null) {
            setStarboundConfiguration(STARBOUND_CONFIGURATION);
        }
        starboundConfiguration.generateConfiguration();
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will attempt configure starbound using a Map with String keys and Object Values you provided to
     * be updated in the starbound configuration
     *
     * @param STARBOUND_CONFIGURATION String representing the name of the starbound configuration
     * @param configurationValues Map representing the string keys and objects to place in the Starbound Configuration
     * @throws IOException various exceptions can be thrown, IO or File related
     */
    public void configureConfiguration(String STARBOUND_CONFIGURATION, Map<String, Object> configurationValues) throws IOException, ParseException {
        if (starboundConfiguration == null) {
            setStarboundConfiguration(STARBOUND_CONFIGURATION);
        }
        starboundConfiguration.generateConfiguration();
        starboundConfiguration.configure(configurationValues);
    }

    /**
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This will either print to console or send events
     *
     * @param eventKey String representing the event key or console leading name
     * @param eventData Object representing the data
     */
    @SuppressWarnings("unchecked")
    protected void printOrEvent(String eventKey, Object eventData){
        if (EVENT_MESSAGE) {
            EVENT_ROUTER.eventNotify(new ObjectEvent(eventKey, eventData));
            //TODO Management System - Player List, Banning, Kicking Plugin
        } else {
            System.out.println(eventKey + ": " +eventData);
        }
    }
}
