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

package org.starnub.server.server.packets.server;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.starnub.server.server.packets.Packet;
import org.starnub.server.server.packets.StarboundBufferReader;

import static org.starnub.server.server.packets.StarboundBufferWriter.writeInt;

/**
 * Protocol Version Packet class. The protocol version
 * changes with each major update.
 * <p>
 * NOTE: This packet tells the client what server version is being run.
 * If you choose to modify this packet, the player may receive a incorrect
 * client error.
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
public class ProtocolVersionPacket extends Packet {

    /**
     * The server's supported protocol version. Changes with each release
     */
    @Getter @Setter
    private int protocolVersion;
    /**
     * @param protocolVersion int representing the protocol version
     */
    public ProtocolVersionPacket(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    /**
     * @return byte representing the packet id for this class
     */
    @Override
    public byte getPacketId() {
        return 0;
    }

    /**
     * @param in ByteBuf of the readable bytes of a received payload
     */
    @Override
    public void read(ByteBuf in) {
        this.protocolVersion = StarboundBufferReader.readInt(in);
    }

    /**
     * @param out ByteBuf to be written to for outbound packets
     */
    @Override
    public void write(ByteBuf out) {
        writeInt(out, this.protocolVersion);
    }
}
