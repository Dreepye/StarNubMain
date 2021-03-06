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

package org.starnub.starnubserver.resources.connections;

import io.netty.channel.ChannelHandlerContext;
import org.starnub.starnubserver.Connections;
import org.starnub.starnubserver.StarNubTask;
import org.starnub.starnubserver.events.events.StarNubEvent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Represents OpenSockets instance are OpenSockets who have no purpose yet and will be purged after a set time
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class OpenSockets extends ConcurrentHashMap<ChannelHandlerContext, Long> {

    private final Connections CONNECTIONS;

    /**
     * Creates a new, empty map with an initial table size based on
     * the given number of elements ({@code initialCapacity}), table
     * density ({@code loadFactor}), and number of concurrently
     * updating threads ({@code concurrencyLevel}).
     *
     * @param initialCapacity  the initial capacity. The implementation
     *                         performs connections sizing to accommodate this many elements,
     *                         given the specified loadData factor.
     * @param loadFactor       the loadData factor (table density) for
     *                         establishing the initial table size
     * @param concurrencyLevel the estimated number of concurrently
     *                         updating threads. The implementation may use this value as
     *                         a sizing hint.
     * @throws IllegalArgumentException if the initial capacity is
     *                                            negative or the loadData factor or concurrencyLevel are
     *                                            nonpositive
     */
    public OpenSockets(Connections CONNECTIONS, int initialCapacity, float loadFactor, int concurrencyLevel) {
        super(initialCapacity, loadFactor, concurrencyLevel);
        this.CONNECTIONS = CONNECTIONS;
        new StarNubTask("StarNub", "StarNub - OpenSockets - Socket Purge", true , 5, 5, TimeUnit.SECONDS, this::purgeSockets);
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For connections use with StarNub.
     * <p>
     * Uses: This method will purge open sockets for connections that never completed within 30 seconds.
     */
    private void purgeSockets(){
        for (Entry<ChannelHandlerContext, Long> openSocket : this.entrySet()){
            ChannelHandlerContext ctx = openSocket.getKey();
            long socketAge = openSocket.getValue();
            if ((System.currentTimeMillis() - socketAge) >= 30000){
                ctx.close();
                new StarNubEvent("StarNub_Open_Socket_Purged", ctx);
                this.remove(ctx);
            }
        }

    }
}
