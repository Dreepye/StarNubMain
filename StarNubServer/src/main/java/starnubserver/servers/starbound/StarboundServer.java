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
* this CodeHome Software.  If not, see <http://www.gnu.org/licenses/>.
*/

package starnubserver.servers.starbound;

import starboundmanager.StarboundManager;
import starboundmanager.StarboundStatus;
import starnubserver.StarNub;
import starnubserver.StarNubTaskManager;
import starnubserver.events.starnub.StarNubEventRouter;
import utilities.file.simplejson.parser.ParseException;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents the Starbound Server core.
 * <p>
 * This enum singleton holds and runs all things important
 * to Starbound and connect players.
 * <p>
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public class StarboundServer extends StarboundManager {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final StarboundServer instance = new StarboundServer();
    private TCPProxyServer tcpProxyServer;
    private UDPProxyServer udpProxyServer;
    private boolean isLinux;

    /**
     * This constructor is private - Singleton Pattern
     */
    private StarboundServer() {
        super(StarNubEventRouter.getInstance(), StarNubTaskManager.getInstance());
        String operatingSystem = this.getOPERATING_SYSTEM();
        if (operatingSystem.equals("Linux")){
            this.isLinux = true;
        }
    }

    public static StarboundServer getInstance() {
        return instance;
    }

    public TCPProxyServer getTcpProxyServer() {
        return tcpProxyServer;
    }

    public UDPProxyServer getUdpProxyServer() {
        return udpProxyServer;
    }

    public void startServers() {
        int starnubPort = (int) StarNub.getConfiguration().getNestedValue("starnub_settings", "starnub_port");
        int starboundPort = (int) StarNub.getConfiguration().getNestedValue("starnub_settings", "starbound_port");
        String starboundAddress = "127.0.0.1";

        this.udpProxyServer = new UDPProxyServer(starnubPort, starboundAddress, starboundPort);
        new Thread(this.udpProxyServer, "StarNub - UDP Proxy : Connection_Worker Thread").start();
        startTCPServer(starnubPort, starboundAddress, starboundPort);
    }

    public void startTCPServer(int starnubPort, String starboundAdress, int starboundPort){
        this.tcpProxyServer = new TCPProxyServer(isLinux, starnubPort, starboundAdress, starboundPort);
        this.tcpProxyServer.start();
    }

    public void restartTcpServer(){
        int starnubPort = (int) StarNub.getConfiguration().getNestedValue("starnub_settings", "starnub_port");
        int starboundPort = (int) StarNub.getConfiguration().getNestedValue("starnub_settings", "starbound_port");
        String starboundAddress = "127.0.0.1";
        stopTCPServer();
        startTCPServer(starnubPort, starboundAddress, starboundPort);
    }

    public void stopTCPServer() {
        this.tcpProxyServer.shutdown();
    }

    public void stopServers(){
        stopTCPServer();
        stopUDPServer();
    }

    private void stopUDPServer() {
        udpProxyServer.setStopping(true);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will return the Starbound Status and can be casted into (Running, Starting, Stopped, Stopping, Unresponsive) and used
     * as a per status bases
     *
     * @return StarboundStatus any class representing StarboundStatus
     */
    @Override
    public StarboundStatus getStatus() {
        return super.getStatus();
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Gets the starnubdata.network version
     *
     * @return int representing the starnubdata.network version
     */
    @Override
    public int getServerVersion() {
        return super.getServerVersion();
    }

    public boolean start() throws IOException, ParseException {
        configurationGenerator();
        configureConfiguration();
        int starboundPort = (int) StarNub.getConfiguration().getNestedValue("starnub_settings", "starbound_port");
        return start("127.0.0.1", starboundPort, false, false);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will attempt to start the Starbound Process but may not work depending on the current state
     *
     * @param ipAddress            String representing the address to TCP Query
     * @param port                 int representing the port to query
     * @param STREAM_EVENT_MESSAGE boolean representing if you are going to send the Starbound stream through an event router
     * @param STREAM_CONSOLE_PRINT boolean representing if you are going to print out the Starbound stream through the console
     * @return boolean representing if the starnubdata.network started
     */
    @Override
    public boolean start(String ipAddress, int port, boolean STREAM_EVENT_MESSAGE, boolean STREAM_CONSOLE_PRINT) {
        return super.start(ipAddress, port, STREAM_EVENT_MESSAGE, STREAM_CONSOLE_PRINT);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will attempt to see if the Starbound process is alive, but depending on the current status may or may not work
     *
     * @return boolean representing if the Starbound process is alive
     */
    @Override
    public boolean isAlive() {
        return super.isAlive();
    }

    public boolean isResponsive(int queryAttempts){
        int starboundPort = (int) StarNub.getConfiguration().getNestedValue("starnub_settings", "starbound_port");
        return isResponsive("127.0.0.1", starboundPort, queryAttempts);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will attempt to see if the Starbound starnubdata.network is responsive, but depending on the current status may or may not work, queries are attempted
     * every 10 seconds. Setting the queryAttempts to 12 for example would be 120 seconds worth of tries, which would be 2 minutes.
     *
     * @param ipAddress     String representing the address to TCP Query
     * @param port          int representing the port to query
     * @param queryAttempts int representing the number of queries to attempt
     * @return boolean representing if the starbound starnubdata.network is responsive
     */
    @Override
    public boolean isResponsive(String ipAddress, int port, int queryAttempts) {
        return super.isResponsive(ipAddress, port, queryAttempts);
    }

    public boolean restart(){
        int starboundPort = (int) StarNub.getConfiguration().getNestedValue("starnub_settings", "starbound_port");
        return restart("127.0.0.1", starboundPort, false, false);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will attempt to restart the starbound process by using the stop() and startUDPServer() methods and may or may not work depending on the state
     *
     * @param ipAddress            String representing the address to TCP Query
     * @param port                 int representing the port to query
     * @param STREAM_EVENT_MESSAGE boolean representing if you are going to send the Starbound stream through an event router
     * @param STREAM_CONSOLE_PRINT boolean representing if you are going to print out the Starbound stream through the console
     * @return boolean representing if the starnubdata.network STOPPED and started successfully
     */
    @Override
    public boolean restart(String ipAddress, int port, boolean STREAM_EVENT_MESSAGE, boolean STREAM_CONSOLE_PRINT) {
        return super.restart(ipAddress, port, STREAM_EVENT_MESSAGE, STREAM_CONSOLE_PRINT);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will attempt to stop the Starbound process, but depending on the current status may or may not work
     *
     * @return boolean representing if the starnubdata.network is STOPPED
     */
    @Override
    public boolean stop() {
        return super.stop();
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will attempt to generate a Starbound Configuration if one does not exist
     *
     * @throws java.io.IOException various exceptions can be thrown, IO or File related
     */
    @Override
    public void configurationGenerator() throws IOException {
        super.configurationGenerator();
    }


    public void configureConfiguration() throws IOException, ParseException {
        Map<String, Object> configurationVariables = new LinkedHashMap<>();

        String serverName = (String) StarNub.getConfiguration().getNestedValue("starnub_info", "server_name");
        configurationVariables.put("serverName", serverName);

        int starboundPort = (int) StarNub.getConfiguration().getNestedValue("starnub_settings", "starbound_port");
        configurationVariables.put("gamePort", starboundPort);

        int playerLimit =  (int) StarNub.getConfiguration().getNestedValue("starnub_settings", "player_limit");
        int playerLimitReserved =  (int) StarNub.getConfiguration().getNestedValue("starnub_settings", "player_limit_reserved");
        int playerLimitTotal = playerLimit + playerLimitReserved + 2;
        configurationVariables.put("maxPlayers", playerLimitTotal);

        configureConfiguration(configurationVariables);
    }


    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will attempt configure starbound using a Map with String keys and Object Values you provided to
     * be updated in the starbound configuration
     *
     * @param configurationValues Map representing the string keys and objects to place in the Starbound Configuration
     * @throws java.io.IOException various exceptions can be thrown, IO or File related
     */
    @Override
    public void configureConfiguration(Map<String, Object> configurationValues) throws IOException, ParseException {
        super.configureConfiguration(configurationValues);
    }
}





