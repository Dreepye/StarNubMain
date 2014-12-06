package starnubserver;/*
 * Copyright (C) 2014 www.StarNub.org - Underbalanced
 *
 * This file is part of org.starnubserver a Java Wrapper for Starbound.
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

import io.netty.channel.ChannelHandlerContext;
import starnubserver.cache.wrappers.IPCacheWrapper;
import starnubserver.connections.player.StarNubProxyConnection;
import starnubserver.resources.BansList;
import starnubserver.resources.files.Whitelist;
import starnubserver.resources.connections.OpenConnections;
import starnubserver.resources.connections.OpenSockets;
import starnubserver.resources.connections.Players;
import starnubserver.resources.connections.ProxyConnections;

import java.util.concurrent.TimeUnit;

public class Connections {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final Connections instance = new Connections();

    /**
     * This constructor is private - Singleton Pattern
     */
    private Connections(){
        if ((boolean) StarNub.getConfiguration().getNestedValue("advanced_settings", "packet_decoding")) {
            CONNECTED_PLAYERS = new Players(this, getExpectedPlayers(), 1.0f, getExpectedConnectsPercentage());//Elements, expected Threads
        } else {
            CONNECTED_PLAYERS = null;
        }
    }

    /**
     * Recommended: For connections use with StarNub.
     * <p>
     * Uses: This will set and get Connections Singleton instance
     ** @return Connections Singleton Instance
     */
    @SuppressWarnings("unchecked")
    public static Connections getInstance() {
        return instance;
    }

    private final IPCacheWrapper INTERNAL_IP_WATCHLIST = new IPCacheWrapper("StarNub", "StarNub - Internal IP Watch List", true, StarNub.getTaskManager(), 5000, 500, TimeUnit.MINUTES, 60, 120);
    private final Whitelist WHITELIST = new Whitelist(StarNub.getResourceManager().getStarnubResources());
    private final BansList BANSList = new BansList();
    private final OpenSockets OPEN_SOCKETS = new OpenSockets(this, 20, 1.0f, getExpectedConnectsPercentage());//Elements, expected Threads
    private final OpenConnections OPEN_CONNECTIONS = new OpenConnections(this, 20, 1.0f, getExpectedConnectsPercentage());//Elements, expected Threads
    private final ProxyConnections PROXY_CONNECTION = new ProxyConnections(this, getExpectedPlayers(), 1.0f, getExpectedConnectsPercentage() );//Elements, expected Threads
    private final Players CONNECTED_PLAYERS;

    public Whitelist getWHITELIST() {
        return WHITELIST;
    }

    public BansList getBANSList() {
        return BANSList;
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

    public IPCacheWrapper getINTERNAL_IP_WATCHLIST() {
        return INTERNAL_IP_WATCHLIST;
    }

    public int getExpectedPlayers(){
        return (int) StarNub.getConfiguration().getNestedValue("starnub_settings", "player_limit")
                + (int) StarNub.getConfiguration().getNestedValue("starnub_settings", "player_limit_reserved") + 2;
    }

    public int getExpectedThreadsGeneric(){
        return 5;
    }

    public int getExpectedConnectsPercentage(){
        return ((Double) (getExpectedPlayers() * 0.10)).intValue();
    }

    public StarNubProxyConnection getStarNubProxyConnection(ChannelHandlerContext ctx){
        StarNubProxyConnection connnection = (StarNubProxyConnection) PROXY_CONNECTION.get(ctx);
        if (connnection != null){
            return connnection;
        } else {
            return CONNECTED_PLAYERS.get(ctx);
        }
    }
}


