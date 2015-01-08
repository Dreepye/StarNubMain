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

package org.starnub.utilities.connectivity.connection;

import io.netty.channel.ChannelHandlerContext;
import org.starnub.utilities.events.EventRouter;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Represents Netty Abstract ProxyConnection Class
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public abstract class ProxyConnection extends Connection {

    protected final ChannelHandlerContext SERVER_CTX;

    public ProxyConnection(EventRouter EVENT_ROUTER, ChannelHandlerContext CLIENT_CTX, ChannelHandlerContext SERVER_CTX) {
        super(EVENT_ROUTER, CLIENT_CTX);
        this.SERVER_CTX = SERVER_CTX;
    }

    public ChannelHandlerContext getSERVER_CTX() {
        return SERVER_CTX;
    }

    public InetAddress getServerIP(){
        return ((InetSocketAddress) SERVER_CTX.channel().remoteAddress()).getAddress();
    }

    public int getServerSocket() {
        return ((InetSocketAddress) SERVER_CTX.channel().remoteAddress()).getPort();
    }

    public String getServerHostName() {
        return ((InetSocketAddress) SERVER_CTX.channel().remoteAddress()).getHostName();
    }

    public String getServerHostString(){
        return ((InetSocketAddress) SERVER_CTX.channel().remoteAddress()).getHostString();
    }

    public abstract void removeConnection();

    @Override
    public String toString() {
        return "ProxyConnection{" +
                "SERVER_CTX=" + SERVER_CTX +
                "} " + super.toString();
    }
}
