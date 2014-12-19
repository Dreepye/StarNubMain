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
import starbounddata.chat.MessageContext;
import starbounddata.chat.Mode;
import starbounddata.packets.Packet;
import starbounddata.packets.Packets;

/**
 * Represents the ChatReceivedPacket and methods to generate a packet data for StarNub and Plugins
 * <p>
 * Notes: This packet can be edited freely. Please be cognisant of what values you change and how they will be interpreted by the starnubclient
 * <p>
 * Packet Direction: Server -> Client
 * <p>
 * Starbound 1.0 Compliant
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class ChatReceivePacket extends Packet {


    private MessageContext messageContext = new MessageContext();
    private int clientId;
    private String fromName;
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
        super(DIRECTION, Packets.CHATRECEIVE.getPacketId(), SENDER_CTX, DESTINATION_CTX);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method will be used to send a packet to the client with the server version. You only need the destination in order t
     * router this packet
     * <p>
     *
     * @param DESTINATION_CTX ChannelHandlerContext which represents the destination of this packets context (Context can be written to)
     * @param channelName           String
     * @param clientId        String clientID of the sender. *STARNUB RESERVES 5000*
     * @param fromName            String name of the Sender
     * @param message         String the message
     */
    public ChatReceivePacket(ChannelHandlerContext DESTINATION_CTX, Mode mode, String channelName, long clientId, String fromName, String message) {
        super(Packets.CHATRECEIVE.getDirection(), Packets.CHATRECEIVE.getPacketId(), null, DESTINATION_CTX);
        messageContext.readMessageContext(mode, channelName);
        this.clientId = (int) clientId;
        this.fromName = fromName;
        this.message = message;
    }

    /**
     * Recommended: For internal StarNub use with copying
     * <p>
     * Uses: This will construct a new packet from a packet
     *
     * @param packet ChatReceivePacket
     */
    public ChatReceivePacket(ChatReceivePacket packet) {
        super(packet);
        this.messageContext = packet.getMessageContext();
        this.clientId = packet.getClientId();
        this.fromName = packet.getFromName();
        this.message = packet.getMessage();
    }

    public MessageContext getMessageContext() {
        return messageContext;
    }

    public void setMessageContext(MessageContext messageContext) {
        this.messageContext = messageContext;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * This will provide a new object while copying all of the internal data as well into this
     * new Object
     *
     * @return ChatReceivePacket the new copied object
     */
    @Override
    public ChatReceivePacket copy() {
        return new ChatReceivePacket(this);
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
        this.messageContext.readMessageContext(in);
        this.clientId = in.readInt();
        this.fromName = readStringVLQ(in);
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
        messageContext.writeMessageContext(out);
        out.writeInt(this.clientId);
        writeStringVLQ(out, this.fromName);
        writeStringVLQ(out, this.message);
    }

    @Override
    public String toString() {
        return "ChatReceivePacket{" +
                "messageContext=" + messageContext +
                ", clientId=" + clientId +
                ", fromName='" + fromName + '\'' +
                ", message='" + message + '\'' +
                "} " + super.toString();
    }
}
