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

package starbounddata.packets.server;

import starbounddata.packets.Packet;
import starbounddata.packets.StarboundBufferReader;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Universe time Update starbounddata.packets.Packet class.
 * <p>
 * NOTE: This packet just increments up at what appears to be
 * the same rate of the starbounddata.packets.starbounddata.packets.server heartbeat (cycle) but has a
 * what appears to be an accumulative time from the starbounddata.packets.starbounddata.packets.server up time.
 * This packet has not been seen often except in latency or disconnects.
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
public class UniverseTimeUpdatePacket extends Packet {

    /**
     * A time that increments up when sent by the starbounddata.packets.starbounddata.packets.server
     */
    @Getter @Setter
    private long time;

    /**
     *
     * @return byte representing the packetId
     */
    @Override
    public byte getPacketId() {
        return 5;
    }

    /**
     * @param in ByteBuf of the readable bytes of a received payload
     */
    @Override
    public void read(ByteBuf in) {
        this.time = StarboundBufferReader.readSignedVLQ(in).getValue();
    }

    /**
     * @param out ByteBuf to be written to for outbound starbounddata.packets
     */
    @Override
    public void write(ByteBuf out) {
        writeVLQObject(out, this.time);
    }
}
