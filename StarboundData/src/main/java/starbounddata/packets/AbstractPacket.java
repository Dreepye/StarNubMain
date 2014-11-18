package starbounddata.packets;/*
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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a basic packet.
 * <p>
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public abstract class AbstractPacket {
    /**
     * starbounddata.packets.Packet ID that links the packet to a type
     */
    @Getter @Setter
    private byte PacketId;

    /**
     * This is where the packet ordinated from which could be
     * the starbounddata.packets.starbounddata.packets.server or client socket
     */
    @Getter @Setter
    private ChannelHandlerContext senderCTX;

    /**
     * This is where the packet is destined to which could be
     * the starbounddata.packets.starbounddata.packets.server or client socket
     */
    @Getter @Setter
    private ChannelHandlerContext destinationCTX;

    /**
     * Should this packet be recycled when handled by an event
     */
    @Getter @Setter
    private boolean recycle;

    /**
     *
     * @param destinationCTX ChannelHandlerContext that the communication is destine for
     * @param senderCTX ChannelHandlerContext of the sender
     */
    public void setDestinationSenderCTX(ChannelHandlerContext destinationCTX, ChannelHandlerContext senderCTX) {
        this.destinationCTX = destinationCTX;
        this.senderCTX = senderCTX;
    }

    /**
     *
     * @param in ByteBuf of the readable bytes of a received payload
     */
    public abstract void read(ByteBuf in);

    /**
     *
     * @param out ByteBuf to be written to for outbound starbounddata.packets
     */
    public abstract void write(ByteBuf out);
}
