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
* this CodeHome Software.  If not, see <http://www.gnu.org/licenses/>.
*/

package starbounddata.packets.connection;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import starbounddata.packets.Packet;
import starbounddata.packets.Packets;

/**
 * Represents the ClientDisconnectRequestPacket and methods to generate a packet data for StarNub and Plugins
 * <p>
 * Notes: This packet can be edited freely. This packet will send the starnubserver a disconnect request
 * <p>
 * Packet Direction: Client -> Server
 * <p>
 * Starbound 1.0 Compliant (Versions 622, Update 1)
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class ClientDisconnectRequestPacket extends Packet {

    private byte emptyByte;

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
    public ClientDisconnectRequestPacket(Direction DIRECTION, ChannelHandlerContext SENDER_CTX, ChannelHandlerContext DESTINATION_CTX) {
        super(DIRECTION, Packets.CLIENTDISCONNECTREQUEST.getPacketId(), SENDER_CTX, DESTINATION_CTX);
    }

    /**
     * Recommended: For connections StarNub usage.
     * <p>
     * Uses: This method will be used to send a packet to the client with the server version. You only need the destination in order t
     * router this packet
     * <p>
     *
     * @param DESTINATION_CTX ChannelHandlerContext which represents the destination of this packets context (Context can be written to)
     */
    public ClientDisconnectRequestPacket(ChannelHandlerContext DESTINATION_CTX) {
        super(Packets.CLIENTDISCONNECTREQUEST.getDirection(), Packets.CLIENTDISCONNECTREQUEST.getPacketId(), null, DESTINATION_CTX);
        emptyByte = 1;
    }

    /**
     * Recommended: For internal StarNub use with copying
     * <p>
     * Uses: This will construct a new packet from a packet
     *
     * @param packet ClientDisconnectRequestPacket representing the packet to construct from
     */
    public ClientDisconnectRequestPacket(ClientDisconnectRequestPacket packet) {
        super(packet);
        this.emptyByte = packet.getEmptyByte();
    }

    public byte getEmptyByte() {
        return emptyByte;
    }

    public void setEmptyByte(byte emptyByte) {
        this.emptyByte = emptyByte;
    }

    /**
     * This will provide a new object while copying all of the internal data as well into this
     * new Object
     *
     * @return ClientDisconnectRequestPacket the new copied object
     */
    @Override
    public ClientDisconnectRequestPacket copy() {
        return new ClientDisconnectRequestPacket(this);
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
        this.emptyByte = (byte) in.readUnsignedByte();
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
        out.writeByte(this.emptyByte);
    }

    @Override
    public String toString() {
        return "ClientDisconnectRequestPacket{" +
                "emptyByte=" + emptyByte +
                "} " + super.toString();
    }

}
