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

package starbounddata.packets.wires;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import starbounddata.packets.Packet;
import starbounddata.packets.Packets;
import starbounddata.types.vectors.Vec2I;

/**
 * Represents the WorldStopPacket and methods to generate a packet data for StarNub and Plugins
 * <p>
 * Notes: This packet cannot be edited freely or sent to a client out of stream
 * <p>
 * Packet Direction: Server -> Client
 * <p>
 * Starbound 1.0 Compliant (Versions 622, Update 1)
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class DisconnectAllWiresPacket extends Packet {

    private Vec2I objectLocation = new Vec2I();
    private Vec2I connectorLocation = new Vec2I();

    /**
     * Recommended: For internal use with StarNub Player Sessions
     * <p>
     * Uses: This is used to pre-construct packets for a specific side of a connection
     * <p>
     * @param DIRECTION       Direction representing the direction the packet is heading
     * @param SENDER_CTX      ChannelHandlerContext which represents the sender of this packets context (Context can be written to)
     * @param DESTINATION_CTX ChannelHandlerContext which represents the destination of this packets context (Context can be written to)
     */
    public DisconnectAllWiresPacket(Direction DIRECTION, ChannelHandlerContext SENDER_CTX, ChannelHandlerContext DESTINATION_CTX) {
        super(DIRECTION, Packets.DISCONNECTALLWIRES.getPacketId(), SENDER_CTX, DESTINATION_CTX);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This is used to construct a packet for a specific destination
     * <p>
     * @param DESTINATION_CTX ChannelHandlerContext which represents the destination of this packets context (Context can be written to)
     * @param objectLocation
     * @param connectorLocation
     */
    public DisconnectAllWiresPacket(ChannelHandlerContext DESTINATION_CTX, Vec2I objectLocation, Vec2I connectorLocation) {
        super(Packets.DISCONNECTALLWIRES.getDirection(), Packets.DISCONNECTALLWIRES.getPacketId(), DESTINATION_CTX);
        this.objectLocation = objectLocation;
        this.connectorLocation = connectorLocation;
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This is used to construct a packet for with no destination. This CAN ONLY be routed by using (routeToGroup, routeToGroupNoFlush) methods
     * <p>
     * @param objectLocation
     * @param connectorLocation
     */
    public DisconnectAllWiresPacket(Vec2I objectLocation, Vec2I connectorLocation) {
        super(Packets.DISCONNECTALLWIRES.getDirection(), Packets.DISCONNECTALLWIRES.getPacketId());
        this.objectLocation = objectLocation;
        this.connectorLocation = connectorLocation;
    }


    /**
     * Recommended: For internal StarNub use with copying
     * <p>
     * Uses: This will construct a new packet from a packet
     *
     * @param packet WorldStopPacket representing the packet to construct from
     */
    public DisconnectAllWiresPacket(DisconnectAllWiresPacket packet) {
        super(packet);
        this.objectLocation = packet.getObjectLocation().copy();
        this.connectorLocation = packet.getConnectorLocation().copy();
    }

    public Vec2I getObjectLocation() {
        return objectLocation;
    }

    public void setObjectLocation(Vec2I objectLocation) {
        this.objectLocation = objectLocation;
    }

    public Vec2I getConnectorLocation() {
        return connectorLocation;
    }

    public void setConnectorLocation(Vec2I connectorLocation) {
        this.connectorLocation = connectorLocation;
    }

    /**
     * This will provide a new object while copying all of the internal data as well into this
     * new Object
     *
     * @return WorldStopPacket the new copied object
     */
    @Override
    public DisconnectAllWiresPacket copy() {
        return new DisconnectAllWiresPacket(this);
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
        this.objectLocation.read(in);
        this.connectorLocation.read(in);
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
        this.objectLocation.write(out);
        this.connectorLocation.write(out);
    }

    @Override
    public String toString() {
        return "DisconnectAllWiresPacket{" +
                "objectLocation=" + objectLocation +
                ", connectorLocation=" + connectorLocation +
                "} " + super.toString();
    }
}