package starnub;/*
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

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import starbounddata.packets.Packet;
import starbounddata.packets.connection.ClientConnectPacket;
import starnub.cache.wrappers.PlayerCtxCacheWrapper;
import starnub.connections.player.StarNubProxyConnection;
import starnub.connections.player.character.*;
import starnub.connections.player.session.PendingPlayer;
import starnub.connections.player.session.Player;
import starnub.connections.player.session.Restrictions;
import starnub.database.DatabaseTables;
import starnub.events.events.PlayerEvent;
import starnub.events.packet.PacketEventHandler;
import starnub.events.packet.PacketEventSubscription;
import starnub.resources.BannedIPs;
import starnub.resources.Whitelist;
import starnub.resources.internalmaps.OpenConnections;
import starnub.resources.internalmaps.OpenSockets;
import starnub.resources.internalmaps.Players;
import starnub.resources.internalmaps.ProxyConnections;
import starnub.senders.NameBuilder;
import utilities.time.DateAndTimes;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.UUID;

public class Connections {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final Connections instance = new Connections();

    /**
     * This constructor is private - Singleton Pattern
     */
    private Connections(){
        //Construct maps
    }

    /**
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This will set and get Connections Singleton instance
     ** @return Connections Singleton Instance
     */
    @SuppressWarnings("unchecked")
    public static Connections getInstance() {
        return instance;
    }

    private final BannedIPs INTERNALLY_BLOCKED_IPS = new BannedIPs(StarNub.getResourceManager().getStarnubResources());
    private final Whitelist WHITELIST = new Whitelist(StarNub.getResourceManager().getStarnubResources());

    private final OpenSockets OPEN_SOCKETS = new OpenSockets();//Elements, expected Threads
    private final OpenConnections OPEN_CONNECTIONS = new OpenConnections();//Elements, expected Threads
    private final ProxyConnections PROXY_CONNECTION= new ProxyConnections();//Elements, expected Threads
    private final Players CONNECTED_PLAYERS = new Players();//Elements, expected Threads

    public BannedIPs getINTERNALLY_BLOCKED_IPS() {
        return INTERNALLY_BLOCKED_IPS;
    }

    public Whitelist getWHITELIST() {
        return WHITELIST;
    }

    public OpenSockets getOPEN_SOCKETS() {
        return OPEN_SOCKETS;
    }

    public OpenConnections getOPEN_CONNECTIONS() {
        return OPEN_CONNECTIONS;
    }

    public ProxyConnections getPROXY_CONNECTION() {
        return PROXY_CONNECTION;
    }

    public Players getCONNECTED_PLAYERS() {
        return CONNECTED_PLAYERS;
    }
}


