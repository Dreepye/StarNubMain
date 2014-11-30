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

package starnubserver.resources.internal.handlers;

import starbounddata.packets.Packet;
import starbounddata.packets.connection.ServerDisconnectPacket;
import starnubserver.Connections;
import starnubserver.connections.player.session.Player;
import starnubserver.events.packet.PacketEventHandler;

public class ServerDisconnectHandler extends PacketEventHandler {

    private final Connections CONNECTIONS;

    public ServerDisconnectHandler(Connections CONNECTIONS) {
        this.CONNECTIONS = CONNECTIONS;
    }

    /**
     * Recommended: For internal use with StarNub
     * <p>
     * Uses: This is used to clean up player connections
     *
     * @param eventData Packet representing the packet being routed
     * @return Packet any class representing packet can be returned
     */
    @Override
    public Packet onEvent(Packet eventData) {
        ServerDisconnectPacket serverDisconnectPacket = (ServerDisconnectPacket) eventData;
        Player player = CONNECTIONS.getCONNECTED_PLAYERS().get(serverDisconnectPacket.getDESTINATION_CTX());
        player.disconnectReason("Quit");
        return serverDisconnectPacket;
    }
}
