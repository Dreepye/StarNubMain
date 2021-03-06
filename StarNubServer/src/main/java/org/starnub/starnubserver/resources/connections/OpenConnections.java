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
import org.starnub.utilities.connectivity.connection.Connection;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Represents OpenConnections instance. These connections have no purpose yet and they will be purged after a set time.
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class OpenConnections extends ConcurrentHashMap<ChannelHandlerContext, Connection> {

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
    public OpenConnections(Connections CONNECTIONS, int initialCapacity, float loadFactor, int concurrencyLevel) {
        super(initialCapacity, loadFactor, concurrencyLevel);
        this.CONNECTIONS = CONNECTIONS;
        new StarNubTask("StarNub", "StarNub - OpenConnections - Open Connection Purge", true , 30, 30, TimeUnit.SECONDS, this::connectionPurge);
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For connections use with StarNub.
     * <p>
     * Uses: This method will purge open connections for connections that never been associated with any task or service within 30 seconds.
     */
    private void connectionPurge(){
        for (Entry<ChannelHandlerContext, Connection> connectionEntry : this.entrySet()){
            ChannelHandlerContext ctx = connectionEntry.getKey();
            Connection connection = connectionEntry.getValue();
            if ((System.currentTimeMillis() - connection.getCONNECTION_START_TIME()) >= 30000){
                connection.disconnect();
                new StarNubEvent("StarNub_Open_Connection_Purged", connection);
                this.remove(ctx);
            }
        }
    }
}
