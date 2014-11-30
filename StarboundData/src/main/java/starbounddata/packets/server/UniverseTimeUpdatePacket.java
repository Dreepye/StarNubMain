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

package starbounddata.packets.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import starbounddata.packets.Packet;
import starbounddata.packets.Packets;
import starbounddata.variants.VLQ;

/**
 * Represents the UniverseTimeUpdate and methods to generate a packet data for StarNub and Plugins
 * <p/>
 * Notes: This packet SHOULD NOT be edited freely.
 * <p/>
 * Packet Direction: Server -> Client
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class UniverseTimeUpdatePacket extends Packet {

    /**
     * A time that increments up when sent by the starnubserver
     */
    private long time;

    public UniverseTimeUpdatePacket(ChannelHandlerContext DESTINATION_CTX) {
        super(Packets.UNIVERSETIMEUPDATE.getPacketId(), null, DESTINATION_CTX);
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
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
        this.time = VLQ.readSignedFromBufferNoObject(in);
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
        VLQ.writeVLQNoObject(out, this.time);
    }
}
