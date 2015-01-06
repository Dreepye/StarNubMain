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
public class ConnectWirePacket extends Packet {

    private Vec2I outputObjectLocation = new Vec2I();
    private Vec2I outputConnectorLocation = new Vec2I();
    private Vec2I inputObjectLocation = new Vec2I();
    private Vec2I inputConnectorLocation = new Vec2I();

    /**
     * Recommended: For internal use with StarNub Player Sessions
     * <p>
     * Uses: This is used to pre-construct packets for a specific side of a connection
     * <p>
     * @param DIRECTION       Direction representing the direction the packet is heading
     * @param SENDER_CTX      ChannelHandlerContext which represents the sender of this packets context (Context can be written to)
     * @param DESTINATION_CTX ChannelHandlerContext which represents the destination of this packets context (Context can be written to)
     */
    public ConnectWirePacket(Direction DIRECTION, ChannelHandlerContext SENDER_CTX, ChannelHandlerContext DESTINATION_CTX) {
        super(DIRECTION, Packets.CONNECTWIRE.getPacketId(), SENDER_CTX, DESTINATION_CTX);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This is used to construct a packet for with no destination. This CAN ONLY be routed by using (routeToGroup, routeToGroupNoFlush) methods
     * <p>
     * @param outputObjectLocation
     * @param outputConnectorLocation
     * @param inputObjectLocation
     * @param inputConnectorLocation
     */
    public ConnectWirePacket(Vec2I outputObjectLocation, Vec2I outputConnectorLocation, Vec2I inputObjectLocation, Vec2I inputConnectorLocation) {
        super(Packets.CONNECTWIRE.getDirection(), Packets.CONNECTWIRE.getPacketId());
        this.outputObjectLocation = outputObjectLocation;
        this.outputConnectorLocation = outputConnectorLocation;
        this.inputObjectLocation = inputObjectLocation;
        this.inputConnectorLocation = inputConnectorLocation;
    }

    /**
     * Recommended: For internal StarNub use with copying
     * <p>
     * Uses: This will construct a new packet from a packet
     *
     * @param packet WorldStopPacket representing the packet to construct from
     */
    public ConnectWirePacket(ConnectWirePacket packet) {
        super(packet);
        this.outputObjectLocation = packet.getOutputObjectLocation().copy();
        this.outputConnectorLocation = packet.getOutputConnectorLocation().copy();
        this.inputObjectLocation = packet.getInputObjectLocation().copy();
        this.inputConnectorLocation = packet.getInputConnectorLocation().copy();
    }

    public Vec2I getOutputObjectLocation() {
        return outputObjectLocation;
    }

    public void setOutputObjectLocation(Vec2I outputObjectLocation) {
        this.outputObjectLocation = outputObjectLocation;
    }

    public Vec2I getOutputConnectorLocation() {
        return outputConnectorLocation;
    }

    public void setOutputConnectorLocation(Vec2I outputConnectorLocation) {
        this.outputConnectorLocation = outputConnectorLocation;
    }

    public Vec2I getInputObjectLocation() {
        return inputObjectLocation;
    }

    public void setInputObjectLocation(Vec2I inputObjectLocation) {
        this.inputObjectLocation = inputObjectLocation;
    }

    public Vec2I getInputConnectorLocation() {
        return inputConnectorLocation;
    }

    public void setInputConnectorLocation(Vec2I inputConnectorLocation) {
        this.inputConnectorLocation = inputConnectorLocation;
    }

    /**
     * This will provide a new object while copying all of the internal data as well into this
     * new Object
     *
     * @return WorldStopPacket the new copied object
     */
    @Override
    public ConnectWirePacket copy() {
        return new ConnectWirePacket(this);
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
        this.outputObjectLocation.read(in);
        this.outputConnectorLocation.read(in);
        this.inputObjectLocation.read(in);
        this.inputConnectorLocation.read(in);
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
        this.outputObjectLocation.write(out);
        this.outputConnectorLocation.write(out);
        this.inputObjectLocation.write(out);
        this.inputConnectorLocation.write(out);
    }

    @Override
    public String toString() {
        return "ConnectWirePacket{" +
                "outputObjectLocation=" + outputObjectLocation +
                ", outputConnectorLocation=" + outputConnectorLocation +
                ", inputObjectLocation=" + inputObjectLocation +
                ", inputConnectorLocation=" + inputConnectorLocation +
                "} " + super.toString();
    }
}