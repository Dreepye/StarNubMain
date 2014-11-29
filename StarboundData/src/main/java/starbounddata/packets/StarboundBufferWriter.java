package starbounddata.packets;


import io.netty.buffer.ByteBuf;
import starbounddata.variants.VLQ;

import java.nio.charset.Charset;
import java.util.UUID;

/**
 * Represents static methods to help write data into a {@link io.netty.buffer.ByteBuf} from values
 * <p/>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class StarboundBufferWriter {

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will write 1 byte to a {@link io.netty.buffer.ByteBuf} and advanced the buffer writer index by 1 byte
     * <p/>
     *
     * @param out   ByteBuf representing the buffer to be written to
     * @param value Byte the byte to be written to the buffer
     */
    public static void writeByte(ByteBuf out, Byte value) {
        out.writeByte(value);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will write some number of byte to a {@link io.netty.buffer.ByteBuf} and advanced the buffer writer index by some number of byte
     * <p/>
     *
     * @param out   ByteBuf representing the buffer to be written to
     * @param bytes bytes[] with an arbitrary number of bytes
     */
    public static void writeByteArray(ByteBuf out, byte[] bytes) {
        out.writeBytes(bytes);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will write a variant length byte[] value to a {@link io.netty.buffer.ByteBuf} and advanced the buffer writer index by a variant length and the number of bytes
     * <p/>
     *
     * @param out   ByteBuf representing the buffer to be written to
     * @param bytes bytes[] value to be written to the buffer
     */
    public static void writeVLQArray(ByteBuf out, byte[] bytes) {
        out.writeBytes(VLQ.createVLQ(bytes.length));
        out.writeBytes(bytes);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will write a short value to a {@link io.netty.buffer.ByteBuf} and advanced the buffer writer index by 2 bytes
     * <p/>
     *
     * @param out   ByteBuf representing the buffer to be written to
     * @param value short value to be written to the buffer
     */
    public static void writeShort(ByteBuf out, short value) {
        out.writeShort(value);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will write a medium value to a {@link io.netty.buffer.ByteBuf} and advanced the buffer writer index by 3 bytes
     * <p/>
     *
     * @param out   ByteBuf representing the buffer to be written to
     * @param value medium value to be written to the buffer
     */
    public static void writeMedium(ByteBuf out, int value) {
        out.writeMedium(value);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will write a int value to a {@link io.netty.buffer.ByteBuf} and advanced the buffer writer index by 4 bytes
     * <p/>
     *
     * @param out   ByteBuf representing the buffer to be written to
     * @param value int value to be written to the buffer
     */
    public static void writeInt(ByteBuf out, int value) {
        out.writeInt(value);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will write a long value to a {@link io.netty.buffer.ByteBuf} and advanced the buffer writer index by 8 bytes
     * <p/>
     *
     * @param out   ByteBuf representing the buffer to be written to
     * @param value long value to be written to the buffer
     */
    public static void writeLong(ByteBuf out, long value) {
        out.writeLong(value);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will write a float value to a {@link io.netty.buffer.ByteBuf} and advanced the buffer writer index by 4 bytes
     * <p/>
     *
     * @param out   ByteBuf representing the buffer to be written to
     * @param value float value to be written to the buffer
     */
    public static void writeFloat(ByteBuf out, float value) {
        out.writeFloat(value);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will write a double value to a {@link io.netty.buffer.ByteBuf} and advanced the buffer writer index by 8 bytes
     * <p/>
     *
     * @param out   ByteBuf representing the buffer to be written to
     * @param value double value to be written to the buffer
     */
    public static void writeDouble(ByteBuf out, double value) {
        out.writeDouble(value);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will write a boolean value to a {@link io.netty.buffer.ByteBuf} and advanced the buffer writer index by 1 bytes
     * <p/>
     *
     * @param out   ByteBuf representing the buffer to be written to
     * @param value voolean value to be written to the buffer
     */
    public static void writeBoolean(ByteBuf out, boolean value) {
        out.writeByte(value ? (byte) 1 : (byte) 0);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will write a String value to a {@link io.netty.buffer.ByteBuf} and advanced the buffer writer index by a variant length and the number of bytes
     * <p/>
     *
     * @param out   ByteBuf representing the buffer to be written to
     * @param value String value to be written to the buffer
     */
    public static void writeStringVLQ(ByteBuf out, String value) {
        writeVLQArray(out, value.getBytes(Charset.forName("UTF-8")));
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will write a uuid value to a {@link io.netty.buffer.ByteBuf} and advanced the buffer writer index by 16 bytes
     * <p/>
     *
     * @param out  ByteBuf representing the buffer to be written to
     * @param uuid uuid value to be written to the buffer
     */
    public static void writeUUID(ByteBuf out, UUID uuid) {
        out.writeLong(uuid.getMostSignificantBits());
        out.writeLong(uuid.getLeastSignificantBits());
    }
}
