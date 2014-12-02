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

package starbounddata.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import utilities.compression.Zlib;

import java.util.HashSet;

import static starbounddata.packets.StarboundBufferWriter.writeByte;
import static starbounddata.packets.StarboundBufferWriter.writeByteArray;
import static starbounddata.variants.VLQ.writeSignedVLQNoObject;

/**
 * Represents a basic packet that all packets should inherit.
 * <p>
 * Notes:
 * - Packet ID is a single Byte
 * - {@link io.netty.channel.ChannelHandlerContext} are the decoders on both the starnubclient and starnubserver side of the socket, this is used to write network data to the session
 * - recycle represents if this packet should be recycled when handled by StarNub
 * <p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public abstract class Packet {

    private final Direction DIRECTION;
    private final byte PACKET_ID;
    private final ChannelHandlerContext SENDER_CTX;
    private final ChannelHandlerContext DESTINATION_CTX;
    private boolean recycle = false;

    /**
     * Recommended: For internal StarNub usage.
     * <p>
     * Uses: This constructor is used in construction of packets to be cached on each side of the players connection (Client, Server) sides of the proxy
     *
     * @param DIRECTION       Direction representing the direction the packet flows to
     * @param PACKET_ID       byte representing the Starbound packet id for this type of packet
     * @param SENDER_CTX      ChannelHandlerContext which represents the sender of this packets context (Context can be written to)
     * @param DESTINATION_CTX ChannelHandlerContext which represents the destination of this packets context (Context can be written to)
     */
    public Packet(Direction DIRECTION, byte PACKET_ID, ChannelHandlerContext SENDER_CTX, ChannelHandlerContext DESTINATION_CTX) {
        this.DIRECTION = DIRECTION;
        this.PACKET_ID = PACKET_ID;
        this.SENDER_CTX = SENDER_CTX;
        this.DESTINATION_CTX = DESTINATION_CTX;
    }

    public ChannelHandlerContext getSENDER_CTX() {
        return SENDER_CTX;
    }

    public ChannelHandlerContext getDESTINATION_CTX() {
        return DESTINATION_CTX;
    }

    public boolean isRecycle() {
        return recycle;
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Setting this will cause the packet to stop being routed to event handlers and placed back into the packet pool with out
     * being routed to the destination
     */
    public void recycle() {
        this.recycle = true;
    }

    /**
     * Recommended: For internal StarNub usage.
     * <p>
     * Uses: This is used for StarNub to reset the packets routing
     */
    public void resetRecycle() {
        this.recycle = false;
    }

    /**
     * Recommended: For internal StarNub usage.
     * <p>
     * Uses: This method will read in a {@link io.netty.buffer.ByteBuf} into this packets fields
     * <p>
     *
     * @param in ByteBuf representing the reason to be read into the packet
     */
    public abstract void read(ByteBuf in);

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method will write to a {@link io.netty.buffer.ByteBuf} using this packets fields
     */
    public void routeToDestination() {
        DESTINATION_CTX.writeAndFlush(packetToMessageEncoder(this), DESTINATION_CTX.voidPromise());
    }

    /**
     * Recommended: For internal StarNub usage.
     * <p>
     * Uses: This method will write to a {@link io.netty.buffer.ByteBuf} using this packets fields
     * <p>
     *
     * @param packet Packet representing the packet to encode
     * @return ByteBuf representing the ByteBuf to write to socket
     */
    private static ByteBuf packetToMessageEncoder(Packet packet) {
        ByteBuf msgOut = PooledByteBufAllocator.DEFAULT.directBuffer();
        packet.write(msgOut);
        int payloadLengthOut = msgOut.readableBytes();
        byte[] dataOut;
        if (payloadLengthOut > 100) {
            dataOut = Zlib.compress(msgOut.readBytes(payloadLengthOut).array());
            payloadLengthOut = -dataOut.length;
        } else {
            dataOut = msgOut.readBytes(payloadLengthOut).array();
        }
        msgOut.clear();
        writeByte(msgOut, packet.getPACKET_ID());
        writeSignedVLQNoObject(msgOut, payloadLengthOut);
        writeByteArray(msgOut, dataOut);
        return msgOut;
    }

    /**
     * Recommended: For internal StarNub usage.
     * <p>
     * Uses: This method will write to a {@link io.netty.buffer.ByteBuf} using this packets fields
     * <p>
     *
     * @param out ByteBuf representing the space to write out the packet reason
     */
    public abstract void write(ByteBuf out);

    public byte getPACKET_ID() {
        return PACKET_ID;
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will send this packet to multiple people
     *
     * @param sendList    HashSet of ChannelHandlerContext to this packet to
     * @param ignoredList HashSet of ChannelHandlerContext to not send the message too
     */
    public void routeToGroup(HashSet<ChannelHandlerContext> sendList, HashSet<ChannelHandlerContext> ignoredList) {
        if (ignoredList != null) {
            sendList.stream().filter(ctx -> !ignoredList.contains(ctx)).forEach(ctx -> ctx.writeAndFlush(packetToMessageEncoder(this), ctx.voidPromise()));
        } else {
            for (ChannelHandlerContext ctx : sendList) {
                ctx.writeAndFlush(packetToMessageEncoder(this), ctx.voidPromise());
            }
        }
    }

    @Override
    public String toString() {
        return "Packet{" +
                "DIRECTION=" + DIRECTION +
                ", PACKET_ID=" + PACKET_ID +
                ", SENDER_CTX=" + SENDER_CTX +
                ", DESTINATION_CTX=" + DESTINATION_CTX +
                ", recycle=" + recycle +
                '}';
    }

    /**
     * This represents the direction the packet travels to.
     */
    public enum Direction {
        STARBOUND_SERVER,
        STARBOUND_CLIENT,
        BIDIRECTIONAL,
        NOT_USED
    }
}
