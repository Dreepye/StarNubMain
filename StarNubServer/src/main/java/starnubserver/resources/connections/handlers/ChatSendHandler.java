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

package starnubserver.resources.connections.handlers;

import starbounddata.chat.ChatReceiveChannel;
import starbounddata.packets.Packet;
import starbounddata.packets.chat.ChatSendPacket;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.events.packet.PacketEventHandler;

public class ChatSendHandler extends PacketEventHandler {

    /**
     * This is for internal use - permission checks.
     *
     * @param eventData Packet representing the packet being routed
     */
    @Override
    public void onEvent(Packet eventData) {
        ChatSendPacket chatSendPacket = (ChatSendPacket) eventData;
        PlayerSession playerSession = PlayerSession.getPlayerSession(chatSendPacket);

        /* Check Chat Permission */
        if (!playerSession.hasPermission("starnub.chat", true)){
            chatSendPacket.recycle();
            playerSession.sendChatMessage("StarNub", ChatReceiveChannel.UNIVERSE, "You do not have permission to chat. Permission required: \"starnub.chat\".");
            return;
        }

        /* Check Command Permission */
        String chatMessage = chatSendPacket.getMessage();
        boolean command = chatMessage.startsWith("/") || chatMessage.startsWith("\\");
        if(command && !playerSession.hasPermission("starnub.command", true)){
            chatSendPacket.recycle();
            playerSession.sendChatMessage("StarNub", ChatReceiveChannel.UNIVERSE, "You do not have permission to use commands. Permission required: \"starnub.command\".");
            return;
        }
    }
}
