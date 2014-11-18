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

package org.starnub.server.server.packets.chat;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.starnub.server.server.datatypes.chat.ChatSendChannel;
import org.starnub.server.server.packets.Packet;
import org.starnub.server.server.packets.StarboundBufferReader;

import static org.starnub.server.server.packets.StarboundBufferWriter.writeByte;
import static org.starnub.server.server.packets.StarboundBufferWriter.writeStringVLQ;

/**
 * Chat Sent Packet Class.
 * <p>
 * NOTE: This packet can be edited freely. The server
 * will respond based on "/" or no "/"
 * <p>
 * DIRECTION: Client -> Server
 * <p>
 * Initial credit goes Mitch528 for helping originally. <br>
 * (https://github.com/Mitch528/SharpStar) <br>
 * <p>
 * Since the help the packet has been lay out has been heavily modified.
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
@NoArgsConstructor
public class ChatSendPacket extends Packet {

    /**
     * <br>
     * 0 - Entire Server (Universe)
     * <br>
     * 1 - World Chat (Object Sent: ship_d07cdd7eb5bcba7a306edcf0fe610010  or Alpha Eta Car 0368 II a))
     * <br>
     */
    @Getter @Setter
    private ChatSendChannel channel;

    /**
     * message that is sent
     */
    @Getter @Setter
    private String message;


    /**
     * @param channel  String one of the following:
     *                       <br>
     *                       0 - Entire Server (Universe)
     *                       <br>
     *                       1 - World Chat (Object Sent: ship_d07cdd7eb5bcba7a306edcf0fe610010  or Alpha Eta Car 0368 II a))
     *                       <br>
     * @param message
     */
    public ChatSendPacket(ChatSendChannel channel, String message) {
        this.channel = channel;
        this.message = message;
    }

    /**
     * @return byte representing the packetId
     */
    @Override
    public byte getPacketId() {
        return 11;
    }

    /**
     * @param in ByteBuf of the readable bytes of a received payload
     */
    @Override
    public void read(ByteBuf in) {
        this.message = StarboundBufferReader.readStringVLQ(in);
        this.channel = ChatSendChannel.values()[StarboundBufferReader.readUnsignedByte(in)];
    }

    /**
     * @param out ByteBuf to be written to for outbound packets
     */
    @Override
    public void write(ByteBuf out) {
        writeStringVLQ(out, this.message);
        writeByte(out, (byte) this.channel.ordinal());
    }
}
