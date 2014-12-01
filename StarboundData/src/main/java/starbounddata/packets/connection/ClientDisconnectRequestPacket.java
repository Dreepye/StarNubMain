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
* this CodeHome Software.  If not, see <http://www.gnu.org/licenses/>.
*/

package starbounddata.packets.connection;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import starbounddata.packets.Packet;
import starbounddata.packets.Packets;

import static starbounddata.packets.StarboundBufferReader.readUnsignedByte;
import static starbounddata.packets.StarboundBufferWriter.writeByte;

/**
 * Represents the ClientDisconnectRequestPacket and methods to generate a packet data for StarNub and Plugins
 * <p/>
 * Notes: This packet can be edited freely. This packet will send the starnubserver a disconnect request
 * <p/>
 * Packet Direction: Client -> Server
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class ClientDisconnectRequestPacket extends Packet {

    private byte emptyByte;

    public byte getEmptyByte() {
        return emptyByte;
    }

    public void setEmptyByte(byte emptyByte) {
        this.emptyByte = emptyByte;
    }

    public ClientDisconnectRequestPacket(Direction DIRECTION, ChannelHandlerContext DESTINATION_CTX) {
        super(DIRECTION, Packets.CLIENTDISCONNECT.getPacketId(), null, DESTINATION_CTX);
        emptyByte = 1;
    }

    /**
     * Recommended: For internal StarNub usage.
     * <p/>
     * Uses: This method will read in a {@link io.netty.buffer.ByteBuf} into this packets fields
     * <p/>
     *
     * @param in ByteBuf representing the reason to be read into the packet
     */
    @Override
    public void read(ByteBuf in) {
        this.emptyByte = readUnsignedByte(in);
    }

    /**
     * Recommended: For internal StarNub usage.
     * <p/>
     * Uses: This method will write to a {@link io.netty.buffer.ByteBuf} using this packets fields
     * <p/>
     *
     * @param out ByteBuf representing the space to write out the packet reason
     */
    @Override
    public void write(ByteBuf out) {
        writeByte(out, this.emptyByte);
    }

    @Override
    public String toString() {
        return "ClientDisconnectRequestPacket{" +
                "emptyByte=" + emptyByte +
                '}';
    }
}
