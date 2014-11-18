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

package starbounddata.packets.chat;


import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import starbounddata.packets.Packet;
import starbounddata.packets.StarboundBufferReader;

import static starbounddata.packets.StarboundBufferWriter.writeByte;
import static starbounddata.packets.StarboundBufferWriter.writeInt;
import static starbounddata.packets.StarboundBufferWriter.writeStringVLQ;

/**
 * Chat Receive starbounddata.packets.Packet Class.
 * <p>
 * NOTE: This packet can be edited freely. Please be cognisant
 * of what values you change and how they will be interpreted by the client
 * <p>
 * DIRECTION: Server -> Client
 * <p>
 * Credit goes to: <br>
 * SirCmpwn - (https://github.com/SirCmpwn/StarNet) <br>
 * Mitch528 - (https://github.com/Mitch528/SharpStar) <br>
 * Starbound-Dev - (http://starbound-dev.org/)
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
@NoArgsConstructor
public class ChatReceivePacket extends Packet {

    public enum ChatReceiveChannel {
        PLANET,
        UNIVERSE,
        WHISPER,
        COMMAND
    }

    /**
     * <br>
     * 0 - Local Chat (Object Sent: ship_d07cdd7eb5bcba7a306edcf0fe610010 or Alpha Eta Car 0368 II a)
     * <br>
     * 1 - Entire Server (Galactic)
     * <br>
     * 2 - Whisper
     * <br>
     * 3 - Command Results
     */
    @Getter @Setter
    private ChatReceiveChannel channel;

    /**
     * Sent blank, unless used in local starbounddata.packets.chat
     */
    @Getter @Setter
    private String world;

    /**
     * ClientId of the sender
     */
    @Getter @Setter
    private int clientId;

    /**
     * name of the sender
     */
    @Getter @Setter
    private String name;

    /**
     * The message
     */
    @Getter @Setter
    private String message;

    /**
     * @param channel  String one of the following:
     *                 <br>
     *                 0 - Local Chat (Planet) (Object Sent: ship_d07cdd7eb5bcba7a306edcf0fe610010 or Alpha Eta Car 0368 II a)
     *                 <br>
     *                 1 - Entire Server (Universe)
     *                 <br>
     *                 2 - Whisper
     *                 <br>
     *                 3 - Command Results
     * @param world    String representing the starbounddata.packets.world the starbounddata.packets.chat was sent from
     * @param clientId String clientID of the sender. *STARNUB RESERVES 5000*
     * @param name     String name of the Sender
     * @param message  String the message
     */
    public ChatReceivePacket(ChatReceiveChannel channel, String world, long clientId, String name, String message) {
        this.channel = channel;
        this.world = world;
        this.clientId = (int) clientId;
        this.name = name;
        this.message = message;
    }

    /**
     * @return byte representing the packet id
     */
    @Override
    public byte getPacketId() {
        return 4;
    }

    /**
     * @param in ByteBuf of the readable bytes of a received payload
     */
    @Override
    public void read(ByteBuf in) {
        this.channel = ChatReceiveChannel.values()[StarboundBufferReader.readUnsignedByte(in)];
        this.world = StarboundBufferReader.readStringVLQ(in);
        this.clientId = StarboundBufferReader.readUnsignedInt(in);
        this.name = StarboundBufferReader.readStringVLQ(in);
        this.message = StarboundBufferReader.readStringVLQ(in);
    }

    /**
     * @param out ByteBuf to be written to for outbound starbounddata.packets
     */
    @Override
    public void write(ByteBuf out) {
        writeByte(out, (byte) this.channel.ordinal());
        writeStringVLQ(out, this.world);
        writeInt(out, this.clientId);
        writeStringVLQ(out, this.name);
        writeStringVLQ(out, this.message);
    }
}
