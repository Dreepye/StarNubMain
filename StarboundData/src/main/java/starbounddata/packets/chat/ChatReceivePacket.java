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
import starbounddata.chat.ChatReceiveChannel;
import starbounddata.packets.Packet;
import starbounddata.packets.Packets;

/**
 * Represents the ChatReceivedPacket and methods to generate a packet data for StarNub and Plugins
 * <p>
 * Notes: This packet can be edited freely. Please be cognisant of what values you change and how they will be interpreted by the starnubclient
 * <p>
 * Packet Direction: Server -> Client
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class ChatReceivePacket extends Packet {

    /**
     * <br>
     * 0 - Planet (String: ship_d07cdd7eb5bcba7a306edcf0fe610010 or Alpha Eta Car 0368 II a)
     * <br>
     * 1 - Universe
     * <br>
     * 2 - Whisper
     * <br>
     * 3 - Command
     */
    private ChatReceiveChannel channel;
    /**
     * ToDo: Insert expected string display
     */
    private String world;
    /**
     * ClientId of the sender which is assigned by the Starbound starnubserver
     */
    private int clientId;
    /**
     * Name of who sent the message
     */
    private String name;
    /**
     * Message sent from the starnubserver
     */
    private String message;

    /**
     * Recommended: For connections StarNub usage.
     * <p>
     * Uses: This is used to pre-construct packets for a specific side of a connection
     * <p>
     *
     * @param DIRECTION       Direction representing the direction the packet flows to
     * @param SENDER_CTX      ChannelHandlerContext which represents the sender of this packets context (Context can be written to)
     * @param DESTINATION_CTX ChannelHandlerContext which represents the destination of this packets context (Context can be written to)
     */
    public ChatReceivePacket(Direction DIRECTION, ChannelHandlerContext SENDER_CTX, ChannelHandlerContext DESTINATION_CTX) {
        super(DIRECTION, Packets.CHATRECEIVED.getPacketId(), SENDER_CTX, DESTINATION_CTX);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method will be used to send a packet to the client with the server version. You only need the destination in order t
     * router this packet
     * <p>
     *
     * @param DESTINATION_CTX ChannelHandlerContext which represents the destination of this packets context (Context can be written to)
     * @param world           String ToDo: Insert expected string display
     * @param clientId        String clientID of the sender. *STARNUB RESERVES 5000*
     * @param name            String name of the Sender
     * @param message         String the message
     */
    public ChatReceivePacket(ChannelHandlerContext DESTINATION_CTX, ChatReceiveChannel channel, String world, long clientId, String name, String message) {
        super(Packets.CHATRECEIVED.getDirection(), Packets.CHATRECEIVED.getPacketId(), null, DESTINATION_CTX);
        this.channel = channel;
        this.world = world;
        this.clientId = (int) clientId;
        this.name = name;
        this.message = message;
    }

    public ChatReceiveChannel getChannel() {
        return channel;
    }

    public void setChannel(ChatReceiveChannel channel) {
        this.channel = channel;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Recommended: For connections StarNub usage.
     * <p>
     * Uses: This method will read in a {@link io.netty.buffer.ByteBuf} into this packets fields
     * <p>
     *
     * @param in ByteBuf representing the reason to be read into the packet
     */
    @Override
    public void read(ByteBuf in) {
        this.channel = ChatReceiveChannel.values()[in.readUnsignedByte()];
        this.world = readStringVLQ(in);
        this.clientId = in.readInt();
        this.name = readStringVLQ(in);
        this.message = readStringVLQ(in);
    }

    /**
     * Recommended: For connections StarNub usage.
     * <p>
     * Uses: This method will write to a {@link io.netty.buffer.ByteBuf} using this packets fields
     * <p>
     *
     * @param out ByteBuf representing the space to write out the packet reason
     */
    @Override
    public void write(ByteBuf out) {
        out.writeByte(this.channel.ordinal());
        writeStringVLQ(out, this.world);
        out.writeInt(this.clientId);
        writeStringVLQ(out, this.name);
        writeStringVLQ(out, this.message);
    }

    @Override
    public String toString() {
        return "ChatReceivePacket{" +
                "channel=" + channel +
                ", world='" + world + '\'' +
                ", clientId=" + clientId +
                ", name='" + name + '\'' +
                ", message='" + message + '\'' +
                "} " + super.toString();
    }
}
