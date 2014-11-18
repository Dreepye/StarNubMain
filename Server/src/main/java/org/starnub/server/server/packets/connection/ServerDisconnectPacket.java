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

package org.starnub.server.server.packets.connection;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.starnub.server.server.packets.Packet;

import static org.starnub.server.server.packets.StarboundBufferReader.readStringVLQ;
import static org.starnub.server.server.packets.StarboundBufferWriter.writeStringVLQ;

@NoArgsConstructor
public class ServerDisconnectPacket extends Packet {

    /**
     * byte[] which is the payload of the packet
     */
    @Getter @Setter
    private String payload;


    public ServerDisconnectPacket(String payload) {
        this.payload = payload;
    }

    /**
     * @return byte representing the packet id for this class
     */
    @Override
    public byte getPacketId() {
        return 2;
    }

    /**
     * @param in ByteBuf of the readable bytes of a received payload
     */
    @Override
    public void read(ByteBuf in) {
        this.payload = readStringVLQ(in);
    }

    /**
     * @param out ByteBuf to be written to for outbound packets
     */
    @Override
    public void write(ByteBuf out) {
        writeStringVLQ(out, this.payload);
    }
}