package starnub;

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

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.codehome.utilities.simplejson.JSONObject;
import org.codehome.utilities.simplejson.JSONPrettyPrint;
import org.codehome.utilities.simplejson.parser.JSONParser;
import org.codehome.utilities.simplejson.parser.ParseException;
import org.codehome.utilities.timers.ThreadSleep;
import starnub.NamedThreadFactory;
import starnub.events.events.StarboundServerStatusEvent;
import starnub.events.internaldebugging.PacketDebugger;
import chatmanager.chat.ServerChat;
//import org.starnub.starbounddata.packets.starbounddata.packets.starnub.starbounddata.packets.KnownPackets;
import starnub.events.packet.PacketEventRouter;
import starnub.server.Connectionss;
import starnub.server.TCPProxyServer;
import starnub.server.UDPProxyServer;
import starnub.server.packets.server.ProtocolVersionPacket;
import starnub.server.starbound.OLDStarboundManager;
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

    private static final GameColors gameColors = GameColors.getInstance();
    private static final PacketEventRouter packetEventRouter = new PacketEventRouter();



    private TCPProxyServer tcpProxyServer;
    private Thread udpProxyServer;



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


    public static PacketEventRouter getPacketEventRouter() {
        return packetEventRouter;
    }

    /**
     * Attempts to set the {@link starnub.server.UDPProxyServer} singleton.
     * <p>
     * This cannot be done if the Thread is already set.
     */
    private void setUDPProxyServerThread() {
        if (udpProxyServer != null) {
            throw new UnsupportedOperationException("Cannot redefine singleton Server");
        }
        udpProxyServer = new Thread(new UDPProxyServer(),"StarNub - UDP Proxy : Connection_Worker Thread");
    }

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

    public void startUDPServer(){
        setUDPProxyServerThread();
        udpProxyServer.start();
    }

}





