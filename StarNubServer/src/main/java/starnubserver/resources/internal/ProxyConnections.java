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

package starnubserver.resources.internal;

import io.netty.channel.ChannelHandlerContext;
import starnubserver.Connections;
import utilities.connectivity.connection.ProxyConnection;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents StarNubTask instance
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class ProxyConnections extends ConcurrentHashMap<ChannelHandlerContext, ProxyConnection> {

    private final Connections CONNECTIONS;

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
    public ProxyConnections(Connections CONNECTIONS, int initialCapacity, float loadFactor, int concurrencyLevel) {
        super(initialCapacity, loadFactor, concurrencyLevel);
        this.CONNECTIONS = CONNECTIONS;
    }
}
