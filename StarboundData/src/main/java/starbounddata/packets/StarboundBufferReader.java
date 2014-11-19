package starbounddata.packets;


import io.netty.buffer.ByteBuf;
import starbounddata.variants.VLQ;

import java.nio.charset.Charset;
import java.util.UUID;

/**
 * Represents static methods to help read data into values from a {@link io.netty.buffer.ByteBuf}
 * <p/>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class StarboundBufferReader {

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will read 1 byte from a {@link io.netty.buffer.ByteBuf} and advanced the buffer reader index by 1 byte
     * <p/>
     *
     * @param in ByteBuf representing the data to be read
     * @return byte the byte that was read
     */
    public static byte readUnsignedByte(ByteBuf in) {
        return (byte) in.readUnsignedByte();
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will read 1 byte from a {@link io.netty.buffer.ByteBuf} and advanced the buffer reader index by 1 byte
     * <p/>
     *
     * @param in ByteBuf representing the data to be read
     * @return byte the byte that was read
     */
    public static byte[] read1ByteArray(ByteBuf in) {
        return in.readBytes(1).array();
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will read 2 bytes from a {@link io.netty.buffer.ByteBuf} and advanced the buffer reader index by 2 bytes
     * <p/>
     *
     * @param in ByteBuf representing the data to be read
     * @return byte[] the byte[] that was read
     */
    public static byte[] read2ByteArray(ByteBuf in) {
        return in.readBytes(2).array();
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will read 3 bytes from a {@link io.netty.buffer.ByteBuf} and advanced the buffer reader index by 3 bytes
     * <p/>
     *
     * @param in ByteBuf representing the data to be read
     * @return byte[] the byte[] that was read
     */
    public static byte[] read3ByteArray(ByteBuf in) {
        return in.readBytes(3).array();
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will read 4 bytes from a {@link io.netty.buffer.ByteBuf} and advanced the buffer reader index by 4 bytes
     * <p/>
     *
     * @param in ByteBuf representing the data to be read
     * @return byte[] the byte[] that was read
     */
    public static byte[] read4ByteArray(ByteBuf in) {
        return in.readBytes(4).array();
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will read 8 bytes from a {@link io.netty.buffer.ByteBuf} and advanced the buffer reader index by 8 bytes
     * <p/>
     *
     * @param in ByteBuf representing the data to be read
     * @return byte[] the byte[] that was read
     */
    public static byte[] read8ByteArray(ByteBuf in) {
        return in.readBytes(8).array();
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will read 16 bytes from a {@link io.netty.buffer.ByteBuf} and advanced the buffer reader index by 16 bytes
     * <p/>
     *
     * @param in ByteBuf representing the data to be read
     * @return byte[] the byte[] that was read
     */
    public static byte[] read16ByteArray(ByteBuf in) {
        return in.readBytes(16).array();
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will read a VLQ and then a variant length of bytes from a {@link io.netty.buffer.ByteBuf} and advanced the buffer reader index by the length of the VLQ and read bytes
     * <p/>
     *
     * @param in ByteBuf representing the data to be read
     * @return byte[] the byte[] that was read
     */
    public static byte[] readVLQArray(ByteBuf in) {
        int len = VLQ.readUnsignedFromBufferNoObject(in);
        return in.readBytes(len).array();
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will read a all bytes from a {@link io.netty.buffer.ByteBuf} and advanced the buffer reader index to the writer index
     * <p/>
     *
     * @param in ByteBuf representing the data to be read
     * @return byte[] the byte[] that was read
     */
    public static byte[] readAllBytes(ByteBuf in) {
        return in.readBytes(in.readableBytes()).array();
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will read a unsigned short value from a {@link io.netty.buffer.ByteBuf} and advanced the buffer reader index 2 bytes
     * <p/>
     *
     * @param in ByteBuf representing the data to be read
     * @return short the short that was read
     */
    public static short readUnsignedShort(ByteBuf in) {
        return (short) in.readUnsignedShort();
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will read a unsigned medium value from a {@link io.netty.buffer.ByteBuf} and advanced the buffer reader index 3 bytes
     * <p/>
     *
     * @param in ByteBuf representing the data to be read
     * @return medium the medium that was read
     */
    public static int readUnsignedMedium(ByteBuf in) {
        return in.readUnsignedMedium();
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will read a unsigned int value from a {@link io.netty.buffer.ByteBuf} and advanced the buffer reader index 4 bytes
     * <p/>
     *
     * @param in ByteBuf representing the data to be read
     * @return int the int that was read
     */
    public static int readUnsignedInt(ByteBuf in) {
        return (int) in.readUnsignedInt();
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will read a int value from a {@link io.netty.buffer.ByteBuf} and advanced the buffer reader index 4 bytes
     * <p/>
     *
     * @param in ByteBuf representing the data to be read
     * @return int the int that was read
     */
    public static int readInt(ByteBuf in) {
        return in.readInt();
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will read a long value from a {@link io.netty.buffer.ByteBuf} and advanced the buffer reader index 8 bytes
     * <p/>
     *
     * @param in ByteBuf representing the data to be read
     * @return long the long that was read
     */
    public static Long readLong(ByteBuf in) {
        return in.readLong();
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will read a float value from a {@link io.netty.buffer.ByteBuf} and advanced the buffer reader index 4 bytes
     * <p/>
     *
     * @param in ByteBuf representing the data to be read
     * @return float the float that was read
     */
    public static float readFloat(ByteBuf in) {
        return in.readFloat();
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will read a double value from a {@link io.netty.buffer.ByteBuf} and advanced the buffer reader index 8 bytes
     * <p/>
     *
     * @param in ByteBuf representing the data to be read
     * @return double the double that was read
     */
    public static double readDouble(ByteBuf in) {
        return in.readDouble();
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will read 1 byte and form a boolean value from a {@link io.netty.buffer.ByteBuf} and advanced the buffer reader index by 1 byte
     * <p/>
     *
     * @param in ByteBuf representing the data to be read
     * @return boolean the boolean that was read
     */
    public static boolean readBoolean(ByteBuf in) {
        return readUnsignedByte(in) != 0;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will read a VLQ and then a byte array to form a String value from a {@link io.netty.buffer.ByteBuf} and advanced the buffer reader index by the length of the VLQ and read bytes
     * <p/>
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
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will read a uuid value from a {@link io.netty.buffer.ByteBuf} and advanced the buffer reader index 16 bytes
     * <p/>
     *
     * @param in ByteBuf representing the data to be read
     * @return uuid the uuid that was read
     */
    public static UUID readUUID(ByteBuf in) {
        return UUID.nameUUIDFromBytes(read16ByteArray(in));
    }
}
