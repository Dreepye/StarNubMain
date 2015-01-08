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

package org.starnub.starnubserver.connections.player;

import io.netty.channel.ChannelHandlerContext;
import org.starnub.starnubdata.generic.DisconnectReason;
import org.starnub.starnubserver.StarNub;
import org.starnub.starnubserver.connections.player.session.PlayerSession;
import org.starnub.starnubserver.events.events.DisconnectEvent;
import org.starnub.starnubserver.events.events.StarNubEvent;
import org.starnub.utilities.connectivity.connection.Connection;
import org.starnub.utilities.events.EventRouter;

import java.io.IOException;

public class StarNubConnection extends Connection {

    public enum ConnectionProcessingType {
        PLAYER,
        PLAYER_NO_DECODING
    }

    private final ConnectionProcessingType CONNECTION_TYPE;

    //TODO CLEAN UP
    public StarNubConnection(EventRouter EVENT_ROUTER, ConnectionProcessingType CONNECTION_TYPE, ChannelHandlerContext CLIENT_CTX, ChannelHandlerContext SERVER_CTX) {
        super(EVENT_ROUTER, CLIENT_CTX);
        this.CONNECTION_TYPE = CONNECTION_TYPE;
        StarNub.getConnections().getOPEN_SOCKETS().remove(SERVER_CTX);
        StarNub.getConnections().getOPEN_SOCKETS().remove(CLIENT_CTX);
        boolean limitedWrapper =
                !(boolean) StarNub.getConfiguration().getNestedValue("advanced_settings", "packet_decoding") &&
                CONNECTION_TYPE == ConnectionProcessingType.PLAYER_NO_DECODING;
        if (limitedWrapper) {
            try {
                boolean uuidIp =
                        (boolean) StarNub.getConfiguration().getNestedValue("starnub_settings", "whitelisted") &&
                        StarNub.getConnections().getWHITELIST().collectionContains(getClientIP(), "uuid_ip");
                if (!uuidIp) {

                    new StarNubEvent("Player_Connection_Success_No_Decoding", this);
                } else {
                    new StarNubEvent("Player_Connection_Failure_Whitelist_No_Decoding", this);
                    this.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (CONNECTION_TYPE == ConnectionProcessingType.PLAYER) {
            StarNub.getConnections().getOPEN_CONNECTIONS().remove(CLIENT_CTX);
        } else {
            StarNub.getConnections().getOPEN_CONNECTIONS().put(CLIENT_CTX, this);
        }
    }

    public ConnectionProcessingType getCONNECTION_TYPE() {
        return CONNECTION_TYPE;
    }

    public boolean disconnectReason(PlayerSession playerSession, DisconnectReason reason) {
        boolean isConnectedCheck = super.disconnect();
        new DisconnectEvent(playerSession, reason);
        return isConnectedCheck;
    }

    /**
     * Recommended: For connections use with StarNub.
     * <p>
     * Uses: This will automatically schedule a one time task and it can be canceled by calling removeTask()
     */
    @Override
    public void removeConnection() {
        StarNub.getConnections().getOPEN_CONNECTIONS().remove(CLIENT_CTX);
        StarNub.getConnections().getPROXY_CONNECTION().remove(CLIENT_CTX);
    }



    @Override
    public String toString() {
        return "StarNubProxyConnection{} " + super.toString();
    }
}
