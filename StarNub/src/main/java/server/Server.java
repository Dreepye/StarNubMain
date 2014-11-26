package server;

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

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.codehome.utilities.simplejson.JSONObject;
import org.codehome.utilities.simplejson.JSONPrettyPrint;
import org.codehome.utilities.simplejson.parser.JSONParser;
import org.codehome.utilities.simplejson.parser.ParseException;
import org.codehome.utilities.timers.ThreadSleep;
import server.NamedThreadFactory;
import server.StarNub;
import server.eventsrouter.events.StarboundServerStatusEvent;
import server.eventsrouter.internaldebugging.PacketDebugger;
import chatmanager.chat.ServerChat;
//import org.starnub.starbounddata.packets.starbounddata.packets.server.starbounddata.packets.KnownPackets;
import server.server.Connectionss;
import server.server.TCPProxyServer;
import server.server.UDPProxyServer;
import server.server.packets.server.ProtocolVersionPacket;
import server.server.starbound.OLDStarboundManager;
import starbounddata.color.GameColors;

import java.io.*;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Represents the Starbound Server core.
 * <p>
 * This enum singleton holds and runs all things important
 * to Starbound and connect players.
 * <p>
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public enum Server {
    INSTANCE;


    private TCPProxyServer tcpProxyServer;

    private Thread udpProxyServer;

    private OLDStarboundManager OLDStarboundManager;

    private PacketDebugger packetDebugger;

    private ProtocolVersionPacket protocolVersionPacket;

    private NioEventLoopGroup starboundQueryGroup;

    private ServerChat serverChat;

    private static final GameColors gameColors = GameColors.getInstance();

    public TCPProxyServer getTcpProxyServer() {
        return tcpProxyServer;
    }

    public Thread getUdpProxyServer() {
        return udpProxyServer;
    }

    public OLDStarboundManager getOLDStarboundManager() {
        return OLDStarboundManager;
    }

    public PacketDebugger getPacketDebugger() {
        return packetDebugger;
    }

    public ProtocolVersionPacket getProtocolVersionPacket() {
        return protocolVersionPacket;
    }

    public NioEventLoopGroup getStarboundQueryGroup() {
        return starboundQueryGroup;
    }

    public Connectionss getConnectionss() {
        return connectionss;
    }

    public ServerChat getServerChat() {
        return serverChat;
    }

    /**
     * Attempts to set the {@link TCPProxyServer} singleton.
     * <p>
     * This cannot be done if the Thread is already set.
     */
    private void setTCPProxyServer() {
        if (tcpProxyServer != null) {
            throw new UnsupportedOperationException("Cannot redefine singleton Server");
        }
        tcpProxyServer = new TCPProxyServer();
    }

    /**
     * Attempts to set the {@link server.server.UDPProxyServer} singleton.
     * <p>
     * This cannot be done if the Thread is already set.
     */
    private void setUDPProxyServerThread() {
        if (udpProxyServer != null) {
            throw new UnsupportedOperationException("Cannot redefine singleton Server");
        }
        udpProxyServer = new Thread(new UDPProxyServer(),"StarNub - UDP Proxy : Connection_Worker Thread");
    }

    /**
     * References the starboundManager variable to the {@link server.server.starbound.OLDStarboundManager} Enum singleton.
     */
    private void setStarboundManager() {
        OLDStarboundManager = OLDStarboundManager.INSTANCE;
    }

    /**
     * References the starboundManager variable to the {@link server.server.starbound.OLDStarboundManager} Enum singleton.
     */
    private void setPacketDebugger() {
        packetDebugger = PacketDebugger.INSTANCE;
    }

//    /**
//     * References the starboundManager variable to the {@link org.StarboundManager} Enum singleton.
//     */
//    private void setPacketDebugging() {
//        knownPackets = KnownPackets.INSTANCE;
////    }
    /**
     * @param protocolVersion StarboundStream represents the collected data from the apcket
     */
    public void updateProtocolVersionPacket(int protocolVersion) {
        protocolVersionPacket.setProtocolVersion(protocolVersion);
    }

    /**
     * This cannot be done if the ProtocolVersionPacket is already set
     */
    private void setProtocolVersionPacket() {
        if (protocolVersionPacket != null) {
            throw new UnsupportedOperationException("Cannot redefine ProtocolVersionPacket");
        }
        protocolVersionPacket = new ProtocolVersionPacket();
    }

    public void setNetworkThreading(){
        if (starboundQueryGroup == null) {
            starboundQueryGroup =
                    new NioEventLoopGroup(
                            1,
                            Executors.newCachedThreadPool(new NamedThreadFactory("StarNub - TCP Query : Worker Thread")));
        }
    }

    /**
     * References the starboundManager variable to the {@link Connectionss} Enum singleton.
     */
    private void setConnections() {
        connectionss = Connectionss.INSTANCE;
    }

    /**
     * References the serverChat variable to the {@link chatmanager.chat.ServerChat} Enum singleton.
     */
    private void setServerChat() {
        serverChat = ServerChat.INSTANCE;
    }

    public void start() {
        setStarboundManager();
        OLDStarboundManager.setStarboundStatus();
        OLDStarboundManager.setStarboundQueryTask();

        configStarboundConfiguration();

        setTCPProxyServer();
        startUDPServer();
//
//        setPacketDebugging();
//        knownPackets.setPacketDebugging();

        setProtocolVersionPacket();

        setPacketDebugger();
        packetDebugger.setPacketDebugging();

        setConnections();
        connectionss.setConnectionsData();

        setNetworkThreading();

        setServerChat();
        serverChat.setChatChannels();
    }

    /**
     * This method will open and change the starboundmanager configuration
     * to match the StarNub configuration.
     */
    protected void configStarboundConfiguration() {
        String starboundConfig = "starboundmanager.config";
        String starboundConfigBackup = "starboundmanager.config.bak";
        File sbConfig = new File(starboundConfig);
        File sbConfigBackup = new File(starboundConfigBackup);

        boolean firstLoop = true;
        boolean configExist = true;
        while (!sbConfig.exists()) {
            configExist =  false;
            if(firstLoop) {
                OLDStarboundManager.configGeneratorStart();
                StarNub.getLogger().cFatPrint("StarNub", "COULD NOT FIND STARBOUND.CONFIG. " +
                        "PLEASE WAIT WHILE WE GENERATE A STARBOUND CONFIGURATION. STARTING A STARBOUND INSTANCE.");
                StarNub.getLogger().cInfoPrint("StarNub", "Please wait while we generate a starboundmanager.config");
                firstLoop = false;
            } else {
                StarNub.getLogger().cInfoPrint("StarNub", "Still starting Starbound_Server.exe to generate" +
                        " a configuration.");
            }
            new ThreadSleep().timerSeconds(5);
        }

        if (!configExist) {
            StarNub.getLogger().cInfoPrint("StarNub", "Starbound configuration has been generated, shutting starbounddata.packets.starbounddata.packets.server down.");
            OLDStarboundManager.configGeneratorStop();
        }

        if (!sbConfigBackup.exists()) {
            StarNub.getLogger().cInfoPrint("StarNub", "Backing up starboundmanager.config for in case we " +
                    "have a configuration oops.");
            try {
                FileUtils.copyFile(sbConfig, sbConfigBackup);
            } catch (IOException e) {
                StarNub.getLogger().cErrPrint("StarNub", "Could not back up starboundmanager.config.");
            }
        }

            JSONParser parser = new JSONParser();

            FileReader fileRead = null;
            try {
                fileRead = new FileReader(starboundConfig);
            } catch (FileNotFoundException e){
                StarNub.getLogger().cFatPrint("StarNub", "COULD NOT FIND STARBOUND.CONFIG. EXITING STARNUB...");
                return;
            }

            Object obj = null;
            try {
                obj = parser.parse(fileRead);
            } catch (IOException | ParseException e) {
                StarNub.getLogger().cFatPrint("StarNub", "STARNUB COULD NOT PARSE STARBOUNDS CONFIGURATION OR" +
                        "COULD NOT READ IT FROM DISK. EXITING STARNUB... " +
                ExceptionUtils.getStackTrace(e));
                return;
            }

            JSONObject jsonObject = (JSONObject) obj;
            jsonObject.put("gamePort", ((Map)StarNub.getConfiguration().getConfiguration().get("starnub settings")).get("starbound_port"));
            jsonObject.put("serverName", ((Map)StarNub.getConfiguration().getConfiguration().get("starbounddata.packets.starbounddata.packets.server info")).get("server_name"));
            jsonObject.put("maxPlayers", (int) ((Map) StarNub.getConfiguration().getConfiguration().get("resources")).get("player_limit") +
                    (int) ((Map) StarNub.getConfiguration().getConfiguration().get("resources")).get("player_limit_reserved") +
                    2);

            FileWriter fileWrite = null;
            try {
                fileWrite = new FileWriter(starboundConfig);
                fileWrite.write(JSONPrettyPrint.toJSONString(jsonObject));
                fileWrite.flush();
                fileWrite.close();
            } catch (Exception e) {
                StarNub.getLogger().cErrPrint("StarNub", "Could not changing or save starboundmanager.config.");
                StarNub.getLogger().cInfoPrint("StarNub", "Copying your backup Starbound configuration to " +
                        "starboundmanager.config");
                try {
                    FileUtils.copyFile(sbConfigBackup, sbConfig);
                } catch (IOException ex) {
                    StarNub.getLogger().cErrPrint("StarNub", "COULD NOT LOAD YOUR STARBOUND BACKUP CONFIGURATION. " +
                            "STARNUB CANNOT PROCEED. PLEASE DELETE YOUR STARBOUND.CONFIG AND STARBOUND.CONFIG.BAK AND " +
                            "START A STARBOUND INSTANCE TO GENERATE A NEW CONFIGURATION AND THEN RESTART STARNUB. EXITING STARNUB...");
                    System.exit(0);
                }
                if (fileWrite != null) {
                    try {
                        fileWrite.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        try {
            FileUtils.copyFile(sbConfig, sbConfigBackup);
        } catch (IOException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));//TODO
        }
    }

    public void startUDPServer(){
        setUDPProxyServerThread();
        udpProxyServer.start();
    }

    public void restartStarbound(boolean reconfigureStarbound){
        StarboundServerStatusEvent.eventSend_Starbound_Server_Status_Restarting();
        connectionss.disconnectAllPlayers();
        new ThreadSleep().timerSeconds(60);
        OLDStarboundManager.shutdown(true);
        if (reconfigureStarbound) {
            configStarboundConfiguration();
        }
        OLDStarboundManager.startUp();
    }
}





