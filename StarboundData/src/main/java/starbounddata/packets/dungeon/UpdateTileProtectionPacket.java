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

package starbounddata.packets.dungeon;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import starbounddata.packets.Packet;
import starbounddata.packets.Packets;

import java.util.Arrays;


/**
 * Represents the ClientConnectPacket and methods to generate a packet data for StarNub and Plugins
 * <p>
 * Notes: This packet can be edited freely. Please be cognisant of what values you change and how they will be interpreted by the starnubclient.
 * This packet is sent when a starnubclient is initially requesting a connection to the starnubserver after it received
 * the {@link starbounddata.packets.server.ProtocolVersionPacket}
 * <p>
 * Packet Direction: Server -> Client //DEBUG DIRECTION AND DATA
 * <p>
 * Starbound 1.0 Compliant (Versions 622, Update 1)
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class UpdateTileProtectionPacket extends Packet {

    private byte[] bytes;

    /**
     * Recommended: For internal use with StarNub Player Sessions
     * <p>
     * Uses: This is used to pre-construct packets for a specific side of a connection
     * <p>
     * @param DIRECTION       Direction representing the direction the packet is heading
     * @param SENDER_CTX      ChannelHandlerContext which represents the sender of this packets context (Context can be written to)
     * @param DESTINATION_CTX ChannelHandlerContext which represents the destination of this packets context (Context can be written to)
     */
    public UpdateTileProtectionPacket(Direction DIRECTION, ChannelHandlerContext SENDER_CTX, ChannelHandlerContext DESTINATION_CTX) {
        super(DIRECTION, Packets.UPDATETILEPROTECTION.getPacketId(), SENDER_CTX, DESTINATION_CTX);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This is used to construct a packet for with no destination. This CAN ONLY be routed by using (routeToGroup, routeToGroupNoFlush) methods
     * <p>
     * @param bytes
     */
    public UpdateTileProtectionPacket(byte[] bytes) {
        super(Packets.UPDATETILEPROTECTION.getDirection(), Packets.UPDATETILEPROTECTION.getPacketId());
        this.bytes = bytes;
    }

    /**
     * Recommended: For internal StarNub use with copying
     * <p>
     * Uses: This will construct a new packet from a packet
     *
     * @param packet ClientConnectPacket representing the packet to construct from
     */
    public UpdateTileProtectionPacket(UpdateTileProtectionPacket packet) {
        super(packet);
        this.bytes = packet.getBytes();
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }


    /**
     * This will provide a new object while copying all of the internal data as well into this
     * new Object
     *
     * @return ClientConnectPacket the new copied object
     */
    @Override
    public UpdateTileProtectionPacket copy() {
        return new UpdateTileProtectionPacket(this);
    }

    /**
     * Recommended: For internal use with StarNub Player Sessions
     * <p>
     * Uses: This method will read in a {@link io.netty.buffer.ByteBuf} into this packets fields
     * <p>
     *
     * @param in ByteBuf representing the reason to be read into the packet
     */
    @Override
    public void read(ByteBuf in) {
        this.bytes = readVLQArray(in);
    }

    /**
     * Recommended: For internal use with StarNub Player Sessions
     * <p>
     * Uses: This method will write to a {@link io.netty.buffer.ByteBuf} using this packets fields
     * <p>
     *
     * @param out ByteBuf representing the space to write out the packet reason
     */
    @Override
    public void write(ByteBuf out) {
        writeVLQArray(out, this.bytes);
    }

    @Override
    public String toString() {
        return "UpdateTileProtectionPacket{" +
                "bytes=" + Arrays.toString(bytes) +
                "} " + super.toString();
    }
}
