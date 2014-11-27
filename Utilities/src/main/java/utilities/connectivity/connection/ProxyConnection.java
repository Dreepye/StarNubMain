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

package utilities.connectivity.connection;


import io.netty.channel.ChannelHandlerContext;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Represents Netty Abstract ProxyConnection Class
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public class ProxyConnection extends Connection {

    private final ChannelHandlerContext SERVER_CTX;

    public ProxyConnection(ChannelHandlerContext CLIENT_CTX, ChannelHandlerContext SERVER_CTX) {
        super(CLIENT_CTX);
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
}
