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

package starbounddata.packets.structure;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import starbounddata.packets.Packet;
import starbounddata.packets.Packets;
import starbounddata.types.variants.Variant;

/**
 * Represents the WorldStopPacket and methods to generate a packet data for StarNub and Plugins
 * <p>
 * Notes: This packet cannot be edited freely or sent to a client out of stream
 * <p>
 * Packet Direction: Server -> Client //DEBUG ALL PACKET PARTS
 * <p>
 * Starbound 1.0 Compliant (Versions 622, Update 1)
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class CentralStructureUpdatePacket extends Packet {

    private Variant structureData = new Variant();

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
    public CentralStructureUpdatePacket(Direction DIRECTION, ChannelHandlerContext SENDER_CTX, ChannelHandlerContext DESTINATION_CTX) {
        super(DIRECTION, Packets.CENTRALSTRUCTUREUPDATE.getPacketId(), SENDER_CTX, DESTINATION_CTX);
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
    public CentralStructureUpdatePacket(ChannelHandlerContext DESTINATION_CTX, Variant structureData) {
        super(Packets.CENTRALSTRUCTUREUPDATE.getDirection(), Packets.CENTRALSTRUCTUREUPDATE.getPacketId(), null, DESTINATION_CTX);
        this.structureData = structureData;
    }

    /**
     * Recommended: For internal StarNub use with copying
     * <p>
     * Uses: This will construct a new packet from a packet
     *
     * @param packet WorldStopPacket representing the packet to construct from
     */
    public CentralStructureUpdatePacket(CentralStructureUpdatePacket packet) {
        super(packet);
        this.structureData = packet.getStructureData().copy();
    }

    public Variant getStructureData() {
        return structureData;
    }

    public void setStructureData(Variant structureData) {
        this.structureData = structureData;
    }


    /**
     * This will provide a new object while copying all of the internal data as well into this
     * new Object
     *
     * @return WorldStopPacket the new copied object
     */
    @Override
    public CentralStructureUpdatePacket copy() {
        return new CentralStructureUpdatePacket(this);
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
        this.structureData.read(in);
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
        this.structureData.write(out);
    }

    @Override
    public String toString() {
        return "CentralStructureUpdatePacket{" +
                "structureData=" + structureData +
                "} " + super.toString();
    }
}