/*
* Copyright (C) 2014 www.StarNub.org - Underbalanced
*
* This utilities.file is part of org.starnub a Java Wrapper for Starbound.
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
import io.netty.channel.ChannelHandlerContext;
import starbounddata.packets.Packet;
import starbounddata.packets.Packets;

import static starbounddata.packets.StarboundBufferReader.readStringVLQ;
import static starbounddata.packets.StarboundBufferReader.readUnsignedByte;
import static starbounddata.packets.StarboundBufferWriter.writeByte;
import static starbounddata.packets.StarboundBufferWriter.writeStringVLQ;

/**
 * Represents the ChatSentPacket and methods to generate a packet data for StarNub and Plugins
 * <p/>
 * Notes: This packet can be edited freely. Please be cognisant of what values you change and how they will be interpreted by the client
 * <p/>
 * Packet Direction: Client -> Server
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class ChatSendPacket extends Packet {

    public enum ChatSendChannel {
        UNIVERSE,
        PLANET
    }

    /**
     * <br>
     * 0 - Universe
     * <br>
     * 1 - Planet  (String: ship_d07cdd7eb5bcba7a306edcf0fe610010  or Alpha Eta Car 0368 II a))
     * <br>
     */
    private ChatSendChannel channel;

    /**
     * Message sent from the client
     */
    private String message;

    public ChatSendChannel getChannel() {
        return channel;
    }

    public void setChannel(ChatSendChannel channel) {
        this.channel = channel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @param channel String one of the following:
     *                <br>
     *                0 - Entire Server (Universe)
     *                <br>
     *                1 - World Chat (Object Sent: ship_d07cdd7eb5bcba7a306edcf0fe610010  or Alpha Eta Car 0368 II a))
     *                <br>
     * @param message
     */
    public ChatSendPacket(ChannelHandlerContext DESTINATION_CTX, ChatSendChannel channel, String message) {
        super(Packets.CHATSENT.getPacketId(), null, DESTINATION_CTX);
        this.channel = channel;
        this.message = message;
    }

    /**
     * Recommended: For internal StarNub usage.
     * <p/>
     * Uses: This method will read in a {@link io.netty.buffer.ByteBuf} into this packets fields
     * <p/>
     *
     * @param in ByteBuf representing the reason to be read into the packet
     */
    @Override
    public void read(ByteBuf in) {
        this.message = readStringVLQ(in);
        this.channel = ChatSendChannel.values()[readUnsignedByte(in)];
    }

    /**
     * Recommended: For internal StarNub usage.
     * <p/>
     * Uses: This method will write to a {@link io.netty.buffer.ByteBuf} using this packets fields
     * <p/>
     *
     * @param out ByteBuf representing the space to write out the packet reason
     */
    @Override
    public void write(ByteBuf out) {
        writeStringVLQ(out, this.message);
        writeByte(out, (byte) this.channel.ordinal());
    }
}
