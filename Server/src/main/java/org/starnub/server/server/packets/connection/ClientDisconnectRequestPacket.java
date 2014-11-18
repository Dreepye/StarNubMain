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
* this CodeHome Software.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.starnub.server.server.packets.connection;

import io.netty.buffer.ByteBuf;
import org.starnub.server.server.packets.Packet;
import org.starnub.server.server.packets.StarboundBufferReader;

import static org.starnub.server.server.packets.StarboundBufferWriter.writeByte;

/**
 * Packet Class.
 * <p>
 * Credit goes to: <br>
 * SirCmpwn - (https://github.com/SirCmpwn/StarNet) <br>
 * Mitch528 - (https://github.com/Mitch528/SharpStar) <br>
 * Starbound-Dev - (http://starbound-dev.org/)
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @version 1.0, 24 May 2014
 */
public class ClientDisconnectRequestPacket extends Packet {

    private byte emptyByte;

    public ClientDisconnectRequestPacket() {
        emptyByte = 1;
    }

    @Override
    public byte getPacketId() {
        return 8;
    }

    //TODO Reverse Engineer
    /**
     * @return the emptyByte
     */
    public byte getEmptyByte() {
        return emptyByte;
    }

    /**
     * @param emptyByte the emptyByte to set
     */
    public void setEmptyByte(byte emptyByte) {
        this.emptyByte = emptyByte;
    }

    /**
     * @param in ByteBuf of the readable bytes of a received payload
     */
    @Override
    public void read(ByteBuf in) {
        this.emptyByte = StarboundBufferReader.readUnsignedByte(in);
    }

    /**
     * @param out ByteBuf to be written to for outbound packets
     */
    @Override
    public void write(ByteBuf out) {
        writeByte(out, this.emptyByte);
    }
}
