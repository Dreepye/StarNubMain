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

package starnub.resources.internal;

import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import starbounddata.packets.Packet;
import starbounddata.packets.connection.ClientConnectPacket;
import starboundmanager.Starting;
import starnub.Connections;
import starnub.StarNub;
import starnub.cache.objects.RejectionCache;
import starnub.cache.wrappers.PlayerCtxCacheWrapper;
import starnub.connections.player.StarNubProxyConnection;
import starnub.connections.player.session.Player;
import starnub.connections.player.session.Restrictions;
import starnub.events.events.PlayerEvent;
import starnub.events.packet.PacketEventHandler;
import starnub.events.packet.PacketEventSubscription;
import starnub.resources.Operators;
import starnub.resources.internal.handlers.ClientConnectHandler;
import starnub.resources.internal.handlers.ConnectionResponseHandler;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents StarNubTask instance
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class Players extends ConcurrentHashMap<ChannelHandlerContext, Player> {

    private final Connections CONNECTIONS;
    private final PlayerCtxCacheWrapper ACCEPT_REJECT = new PlayerCtxCacheWrapper("StarNub", "StarNub - Player Connection - Accept or Reject", true, StarNub.getTaskManager(),)//Elements, expected Threads
    private final Operators OPERATORS = new Operators(StarNub.getResourceManager().getStarnubResources());

    /**
     * Creates a new, empty map with an initial table size based on
     * the given number of elements ({@code initialCapacity}), table
     * density ({@code loadFactor}), and number of concurrently
     * updating threads ({@code concurrencyLevel}).
     *
     * @param initialCapacity  the initial capacity. The implementation
     *                         performs internal sizing to accommodate this many elements,
     *                         given the specified load factor.
     * @param loadFactor       the load factor (table density) for
     *                         establishing the initial table size
     * @param concurrencyLevel the estimated number of concurrently
     *                         updating threads. The implementation may use this value as
     *                         a sizing hint.
     * @throws IllegalArgumentException if the initial capacity is
     *                                            negative or the load factor or concurrencyLevel are
     *                                            nonpositive
     */
    public Players(Connections CONNECTIONS, int initialCapacity, float loadFactor, int concurrencyLevel) {
        super(initialCapacity, loadFactor, concurrencyLevel);
        this.CONNECTIONS = CONNECTIONS;
        new PacketEventSubscription("StarNub", ClientConnectPacket.class, true, new ClientConnectHandler(CONNECTIONS));
        new PacketEventSubscription("StarNub", ClientConnectPacket.class, true, new ConnectionResponseHandler(CONNECTIONS));
    }

    public Operators getOPERATORS() {
        return OPERATORS;
    }
}
