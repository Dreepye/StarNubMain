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

package starbounddata.packets.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import starbounddata.packets.Packet;
import starbounddata.packets.Packets;

/**
 * Represents the ProtocolVersionPacket and methods to generate a packet data for StarNub and Plugins
 * <p>
 * Notes: This packet SHOULD NOT be edited freely. If the wrong version is sent to the starnubclient they will not
 * be able to connect to the starnubserver and received a wrong starnubclient version message. This is the first packet sent after a starnubclient completes a 3 way TCP handshake
 * <p>
 * Packet Direction: Server -> Client
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class ProtocolVersionPacket extends Packet {

    /**
     * Some int set by the starnubserver based on the servers Starbound version
     */
    private int protocolVersion;

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
    public ProtocolVersionPacket(Direction DIRECTION, ChannelHandlerContext SENDER_CTX, ChannelHandlerContext DESTINATION_CTX) {
        super(DIRECTION, Packets.PROTOCOLVERSION.getPacketId(), SENDER_CTX, DESTINATION_CTX);
    }

    /**
     * Recommended: For connections StarNub usage.
     * <p>
     * Uses: This method will be used to send a packet to the client with the server version. You only need the destination in order t
     * router this packet
     * <p>
     *
     * @param DESTINATION_CTX ChannelHandlerContext which represents the destination of this packets context (Context can be written to)
     * @param protocolVersion int representing the Starbounds protocol version
     */
    public ProtocolVersionPacket(ChannelHandlerContext DESTINATION_CTX, int protocolVersion) {
        super(Packets.PROTOCOLVERSION.getDirection(), Packets.PROTOCOLVERSION.getPacketId(), null, DESTINATION_CTX);
        this.protocolVersion = protocolVersion;
    }

    /**
     * Recommended: For internal StarNub use with copying
     * <p>
     * Uses: This will construct a new packet from a packet
     *
     * @param packet ProtocolVersionPacket representing the packet to construct from
     */
    public ProtocolVersionPacket(ProtocolVersionPacket packet) {
        super(packet);
        this.protocolVersion = packet.getProtocolVersion();
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    /**
     * This will provide a new object while copying all of the internal data as well into this
     * new Object
     *
     * @return ProtocolVersionPacket the new copied object
     */
    @Override
    public ProtocolVersionPacket copy() {
        return new ProtocolVersionPacket(this);
    }

    /**
     * Uses: This method will read in a {@link io.netty.buffer.ByteBuf} into this packets fields
     * <p>
     *
     * @param in ByteBuf representing the reason to be read into the packet
     */
    @Override
    public void read(ByteBuf in) {
        this.protocolVersion = in.readInt();
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
        out.writeInt(this.protocolVersion);
    }

    @Override
    public String toString() {
        return "ProtocolVersionPacket{" +
                "protocolVersion=" + protocolVersion +
                "} " + super.toString();
    }
}
