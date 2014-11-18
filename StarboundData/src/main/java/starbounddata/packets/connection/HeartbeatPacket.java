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

package starbounddata.packets.connection;

import server.server.packets.Packet;
import server.server.packets.StarboundBufferReader;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static server.server.packets.StarboundBufferWriter.writeVLQObject;

/**
 * Heart Beat starbounddata.packets.Packet class. The protocol version
 * changes with each major update.
 * <p>
 * NOTE: This packet eventually will have a Starbound configuration
 * option that will be adjustable as per the Server Owner. Currently
 * the starbounddata.packets.starbounddata.packets.server sends 1 packet per second to the client incremented (1,2,3,4...)
 * which is the Starbound internal cycle rate. The Client response every 3
 * Heart Beats with a increment of the receive heartbeats(3,6,9,19...)
 * <p>
 * Credit goes to: <br>
 * SirCmpwn - (https://github.com/SirCmpwn/StarNet) <br>
 * Mitch528 - (https://github.com/Mitch528/SharpStar) <br>
 * Starbound-Dev - (http://starbound-dev.org/)
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
@NoArgsConstructor
public class HeartbeatPacket extends Packet {

    /**
     * The Server sends steps incremented by one, the client responds every 3, increments of 3.
     */
    @Getter @Setter
    private long currentStep;

    @Override
    public byte getPacketId() {
        return 48;
    }

    /**
     * @param in ByteBuf of the readable bytes of a received payload
     */
    @Override
    public void read(ByteBuf in) {
        this.currentStep = StarboundBufferReader.readVLQ(in).getValue();
    }

    /**
     * @param out ByteBuf to be written to for outbound starbounddata.packets
     */
    @Override
    public void write(ByteBuf out) {
        writeVLQObject(out, this.currentStep);
    }
}
