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
import starbounddata.variants.VLQ;

/**
 * Represents the HeartbeatPacket and methods to generate a packet data for StarNub and Plugins
 * <p/>
 * Notes: This packet SHOULD NOT be edited freely. This packet is generated ever step by the
 * Starbound Server and sent to the Client, The client responds after 3 steps
 * <p/>
 * Packet Direction: Server -> Client / Client -> Server
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class HeartbeatPacket extends Packet {

    private long currentStep;

    public HeartbeatPacket(ChannelHandlerContext DESTINATION_CTX) {
        super(Packets.HEARTBEAT.getPacketId(), null, DESTINATION_CTX);
    }

    public long getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(long currentStep) {
        this.currentStep = currentStep;
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p/>
     * Recommended: For internal StarNub usage.
     * <p/>
     * Uses: This method will read in a {@link io.netty.buffer.ByteBuf} into this packets fields
     * <p/>
     *
     * @param in ByteBuf representing the reason to be read into the packet
     */
    @Override
    public void read(ByteBuf in) {
        this.currentStep = VLQ.readUnsignedFromBufferNoObject(in);
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p/>
     * Recommended: For internal StarNub usage.
     * <p/>
     * Uses: This method will write to a {@link io.netty.buffer.ByteBuf} using this packets fields
     * <p/>
     *
     * @param out ByteBuf representing the space to write out the packet reason
     */
    @Override
    public void write(ByteBuf out) {
        VLQ.writeSignedVLQNoObject(out, this.currentStep);
    }
}
