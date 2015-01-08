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

package org.starnub.starnubserver.resources.connections.handlers;

import org.starnub.starnubdata.generic.DisconnectReason;
import org.starnub.starbounddata.packets.Packet;
import org.starnub.starbounddata.packets.connection.ServerDisconnectPacket;
import org.starnub.starnubserver.Connections;
import org.starnub.starnubserver.connections.player.session.PlayerSession;
import org.starnub.starnubserver.events.packet.PacketEventHandler;

public class ServerDisconnectHandler extends PacketEventHandler {

    private final Connections CONNECTIONS;

    public ServerDisconnectHandler(Connections CONNECTIONS) {
        this.CONNECTIONS = CONNECTIONS;
    }

    /**
     * Recommended: For connections use with StarNub
     * <p>
     * Uses: This is used to clean up player connections
     *
     * @param eventData Packet representing the packet being routed
     */
    @Override
    public void onEvent(Packet eventData) {
        ServerDisconnectPacket serverDisconnectPacket = (ServerDisconnectPacket) eventData;
        PlayerSession playerSession = CONNECTIONS.getCONNECTED_PLAYERS().get(serverDisconnectPacket.getDESTINATION_CTX());
        if (playerSession != null){
            playerSession.disconnectReason(DisconnectReason.QUIT);
        }
    }
}
