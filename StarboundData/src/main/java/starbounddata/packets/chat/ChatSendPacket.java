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

/**
 * Represents the ChatSentPacket and methods to generate a packet data for StarNub and Plugins
 * <p>
 * Notes: This packet can be edited freely. Please be cognisant of what values you change and how they will be interpreted by the starnubclient
 * <p>
 * Packet Direction: Client -> Server
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class ChatSendPacket extends Packet {

    /**
     * <br>
     * 0 - Universe
     * <br>
     * 1 - Planet  (String: ship_d07cdd7eb5bcba7a306edcf0fe610010  or Alpha Eta Car 0368 II a))
     * <br>
     */
    private ChatSendChannel channel;
    /**
     * Message sent from the starnubclient
     */
    private String message;

    /**
     * Recommended: For internal StarNub usage.
     * <p>
     * Uses: This is used to pre-construct packets for a specific side of a connection
     * <p>
     *
     * @param DIRECTION       Direction representing the direction the packet flows to
     * @param SENDER_CTX      ChannelHandlerContext which represents the sender of this packets context (Context can be written to)
     * @param DESTINATION_CTX ChannelHandlerContext which represents the destination of this packets context (Context can be written to)
     */
    public ChatSendPacket(Direction DIRECTION, ChannelHandlerContext SENDER_CTX, ChannelHandlerContext DESTINATION_CTX) {
        super(DIRECTION, Packets.CHATSENT.getPacketId(), SENDER_CTX, DESTINATION_CTX);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method will be used to send a packet to the client with the server version. You only need the destination in order t
     * router this packet
     * <p>
     *
     * @param DESTINATION_CTX ChannelHandlerContext which represents the destination of this packets context (Context can be written to)
     * @param channel         ChatSendChannel representing the chanel to be sent on
     * @param message         String representing the message
     */
    public ChatSendPacket(ChannelHandlerContext DESTINATION_CTX, ChatSendChannel channel, String message) {
        super(Packets.CHATSENT.getDirection(), Packets.CHATSENT.getPacketId(), null, DESTINATION_CTX);
        this.channel = channel;
        this.message = message;
    }

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
     * Recommended: For internal StarNub usage.
     * <p>
     * Uses: This method will read in a {@link io.netty.buffer.ByteBuf} into this packets fields
     * <p>
     *
     * @param in ByteBuf representing the reason to be read into the packet
     */
    @Override
    public void read(ByteBuf in) {
        this.message = readStringVLQ(in);
        this.channel = ChatSendChannel.values()[in.readUnsignedByte()];
    }

    /**
     * Recommended: For internal StarNub usage.
     * <p>
     * Uses: This method will write to a {@link io.netty.buffer.ByteBuf} using this packets fields
     * <p>
     *
     * @param out ByteBuf representing the space to write out the packet reason
     */
    @Override
    public void write(ByteBuf out) {
        writeStringVLQ(out, this.message);
        out.writeByte(this.channel.ordinal());
    }

    @Override
    public String toString() {
        return "ChatSendPacket{" +
                "channel=" + channel +
                ", message='" + message + '\'' +
                "} " + super.toString();
    }

    public enum ChatSendChannel {
        UNIVERSE,
        PLANET
    }
}
