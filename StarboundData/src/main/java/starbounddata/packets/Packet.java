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
import starbounddata.variants.VLQ;
import utilities.compression.Zlib;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.UUID;

import static starbounddata.variants.VLQ.writeSignedVLQNoObjectPacketEncoder;

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
     * Recommended: For connections StarNub usage.
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

    public Direction getDIRECTION() {
        return DIRECTION;
    }

    public byte getPACKET_ID() {
        return PACKET_ID;
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
     * Recommended: For connections StarNub usage.
     * <p>
     * Uses: This is used for StarNub to reset the packets routing
     */
    public void resetRecycle() {
        this.recycle = false;
    }

    /**
     * Recommended: For connections StarNub usage.
     * <p>
     * Uses: This method will read in a {@link io.netty.buffer.ByteBuf} into this packets fields
     * <p>
     *
     * @param in ByteBuf representing the reason to be read into the packet
     */
    public abstract void read(ByteBuf in);

    /**
     * Recommended: For connections StarNub usage.
     * <p>
     * Uses: This method will write to a {@link io.netty.buffer.ByteBuf} using this packets fields
     * <p>
     *
     * @param out ByteBuf representing the space to write out the packet reason
     */
    public abstract void write(ByteBuf out);

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method will write to a {@link io.netty.buffer.ByteBuf} using this packets fields
     */
    public void routeToDestination() {
        DESTINATION_CTX.writeAndFlush(packetToMessageEncoder(), DESTINATION_CTX.voidPromise());
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
            sendList.stream().filter(ctx -> !ignoredList.contains(ctx)).forEach(ctx -> ctx.writeAndFlush(packetToMessageEncoder(), ctx.voidPromise()));
        } else {
            for (ChannelHandlerContext ctx : sendList) {
                ctx.writeAndFlush(packetToMessageEncoder(), ctx.voidPromise());
            }
        }
    }

    /**
     * Recommended: For connections StarNub usage.
     * <p>
     * Uses: This method will write to a {@link io.netty.buffer.ByteBuf} using this packets fields
     * <p>
     *
     * @return ByteBuf representing the ByteBuf to write to socket
     */
    private ByteBuf packetToMessageEncoder() {
        ByteBuf msgOut = PooledByteBufAllocator.DEFAULT.directBuffer();
        this.write(msgOut);
        int payloadLengthOut = msgOut.readableBytes();
        byte[] dataOut;
        if (payloadLengthOut > 100) {
            dataOut = Zlib.compress(msgOut.readBytes(payloadLengthOut).array());
            payloadLengthOut = -dataOut.length;
        } else {
            dataOut = msgOut.readBytes(payloadLengthOut).array();
        }
        msgOut.clear();
        msgOut.writeByte(PACKET_ID);
        writeSignedVLQNoObjectPacketEncoder(msgOut, payloadLengthOut);
        msgOut.writeBytes(dataOut);
        return msgOut;
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
        TO_STARBOUND_SERVER,
        TO_STARBOUND_CLIENT,
        BIDIRECTIONAL,
        NOT_USED
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will read a uuid value from a {@link io.netty.buffer.ByteBuf} and advanced the buffer reader index 16 bytes
     * <p>
     *
     * @param in ByteBuf representing the data to be read
     * @return uuid the uuid that was read
     */
    public static UUID readUUID(ByteBuf in) {
        return UUID.nameUUIDFromBytes(in.readBytes(16).array());
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will read a VLQ and then a byte array to form a String value from a {@link io.netty.buffer.ByteBuf} and advanced the buffer reader index by the length of the VLQ and read bytes
     * <p>
     *
     * @param in ByteBuf representing the data to be read
     * @return String the String that was read
     */
    public static String readStringVLQ(ByteBuf in) {
        try {
            return new String(readVLQArray(in), Charset.forName("UTF-8"));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will read a VLQ and then a variant length of bytes from a {@link io.netty.buffer.ByteBuf} and advanced the buffer reader index by the length of the VLQ and read bytes
     * <p>
     *
     * @param in ByteBuf representing the data to be read
     * @return byte[] the byte[] that was read
     */
    public static byte[] readVLQArray(ByteBuf in) {
        int len = VLQ.readUnsignedFromBufferNoObject(in);
        return in.readBytes(len).array();
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will read 1 byte and form a boolean value from a {@link io.netty.buffer.ByteBuf} and advanced the buffer reader index by 1 byte
     * <p>
     *
     * @param in ByteBuf representing the data to be read
     * @return boolean the boolean that was read
     */
    public static boolean readBoolean(ByteBuf in) {
        return in.readUnsignedByte() != 0;
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will write a uuid value to a {@link io.netty.buffer.ByteBuf} and advanced the buffer writer index by 16 bytes
     * <p>
     *
     * @param out  ByteBuf representing the buffer to be written to
     * @param uuid uuid value to be written to the buffer
     */
    public static void writeUUID(ByteBuf out, UUID uuid) {
        out.writeLong(uuid.getMostSignificantBits());
        out.writeLong(uuid.getLeastSignificantBits());
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will write a String value to a {@link io.netty.buffer.ByteBuf} and advanced the buffer writer index by a variant length and the number of bytes
     * <p>
     *
     * @param out   ByteBuf representing the buffer to be written to
     * @param value String value to be written to the buffer
     */
    public static void writeStringVLQ(ByteBuf out, String value) {
        writeVLQArray(out, value.getBytes(Charset.forName("UTF-8")));
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will write a variant length byte[] value to a {@link io.netty.buffer.ByteBuf} and advanced the buffer writer index by a variant length and the number of bytes
     * <p>
     *
     * @param out   ByteBuf representing the buffer to be written to
     * @param bytes bytes[] value to be written to the buffer
     */
    public static void writeVLQArray(ByteBuf out, byte[] bytes) {
        out.writeBytes(VLQ.createVLQNoObject(bytes.length));
        out.writeBytes(bytes);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will write a boolean value to a {@link io.netty.buffer.ByteBuf} and advanced the buffer writer index by 1 bytes
     * <p>
     *
     * @param out   ByteBuf representing the buffer to be written to
     * @param value voolean value to be written to the buffer
     */
    public static void writeBoolean(ByteBuf out, boolean value) {
        out.writeByte(value ? (byte) 1 : (byte) 0);
    }
}
