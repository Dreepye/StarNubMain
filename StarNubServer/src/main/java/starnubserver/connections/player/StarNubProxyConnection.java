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

package starnubserver.connections.player;

import io.netty.channel.ChannelHandlerContext;
import starnubserver.StarNub;
import utilities.connectivity.connection.ProxyConnection;
import utilities.events.EventRouter;

import java.io.IOException;

public class StarNubProxyConnection extends ProxyConnection {

    public StarNubProxyConnection(EventRouter EVENT_ROUTER, ChannelHandlerContext CLIENT_CTX, ChannelHandlerContext SERVER_CTX) {
        super(EVENT_ROUTER, CLIENT_CTX, SERVER_CTX);
        StarNub.getConnections().getOPEN_SOCKETS().remove(CLIENT_CTX);
        StarNub.getConnections().getOPEN_SOCKETS().remove(SERVER_CTX);
        if ((boolean) StarNub.getConfiguration().getNestedValue("starnub settings", "packet_events")) {
            StarNub.getConnections().getOPEN_CONNECTIONS().put(CLIENT_CTX, this);
        } else {
            boolean uuidIp = false;
            try {
                uuidIp = (boolean) StarNub.getConfiguration().getNestedValue("starnub settings", "whitelisted") &&
                          StarNub.getConnections().getWHITELIST().collectionContains(getClientIP(), "uuid_ip");
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!uuidIp) {
                StarNub.getConnections().getPROXY_CONNECTION().put(CLIENT_CTX, this);
            } else {
                this.disconnect();
            }
        }
    }

    /**
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This will automatically schedule a one time task and it can be canceled by calling removeTask()
     */
    @Override
    public void removeConnection() {
        StarNub.getConnections().getOPEN_CONNECTIONS().remove(CLIENT_CTX);
        StarNub.getConnections().getPROXY_CONNECTION().remove(CLIENT_CTX);
    }
}
