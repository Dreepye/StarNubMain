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

package starbounddata.packets.connection;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import starbounddata.packets.Packet;
import starbounddata.packets.Packets;
import starbounddata.types.variants.VLQ;

/**
 * Represents the HeartbeatPacket and methods to generate a packet data for StarNub and Plugins
 * <p>
 * Notes: This packet SHOULD NOT be edited freely. This packet is generated ever step by the
 * Starbound Server and sent to the Client, The starnubclient responds after 3 steps
 * <p>
 * Packet Direction: Server -> Client / Client -> Server
 * <p>
 * Starbound 1.0 Compliant (Versions 622, Update 1)
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class HeartbeatPacket extends Packet {

    private long currentStep;

    /**
     * Recommended: For internal use with StarNub Player Sessions
     * <p>
     * Uses: This is used to pre-construct packets for a specific side of a connection
     * <p>
     * @param DIRECTION       Direction representing the direction the packet is heading
     * @param SENDER_CTX      ChannelHandlerContext which represents the sender of this packets context (Context can be written to)
     * @param DESTINATION_CTX ChannelHandlerContext which represents the destination of this packets context (Context can be written to)
     */
    public HeartbeatPacket(Direction DIRECTION, ChannelHandlerContext SENDER_CTX, ChannelHandlerContext DESTINATION_CTX) {
        super(DIRECTION, Packets.HEARTBEAT.getPacketId(), SENDER_CTX, DESTINATION_CTX);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This is used to construct a packet for with no destination. This CAN ONLY be routed by using (routeToGroup, routeToGroupNoFlush) methods
     * <p>
     * @param currentStep
     */
    public HeartbeatPacket(long currentStep) {
        super(Packets.HEARTBEAT.getDirection(), Packets.HEARTBEAT.getPacketId());
        this.currentStep = currentStep;
    }

    /**
     * Recommended: For internal StarNub use with copying
     * <p>
     * Uses: This will construct a new packet from a packet
     *
     * @param packet HeartbeatPacket representing the packet to construct from
     */
    public HeartbeatPacket(HeartbeatPacket packet) {
        super(packet);
        this.currentStep = packet.getCurrentStep();
    }

    public long getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(long currentStep) {
        this.currentStep = currentStep;
    }

    /**
     * This will provide a new object while copying all of the internal data as well into this
     * new Object
     *
     * @return HeartbeatPacket the new copied object
     */
    @Override
    public HeartbeatPacket copy() {
        return new HeartbeatPacket(this);
    }

    /**
     * Uses: This method will read in a {@link io.netty.buffer.ByteBuf} into this packets fields
     * <p>
     *
     * @param in ByteBuf representing the reason to be read into the packet
     */
    @Override
    public void read(ByteBuf in) {
        this.currentStep = VLQ.readUnsignedFromBufferNoObject(in);
    }

    /**
     * Uses: This method will write to a {@link io.netty.buffer.ByteBuf} using this packets fields
     * <p>
     *
     * @param out ByteBuf representing the space to write out the packet reason
     */
    @Override
    public void write(ByteBuf out) {
        out.writeBytes(VLQ.writeSignedVLQNoObject(this.currentStep));
    }

    @Override
    public String toString() {
        return "HeartbeatPacket{" +
                "currentStep=" + currentStep +
                "} " + super.toString();
    }
}
