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

package starbounddata.variants;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a Variant Length Quantity(VLQ).
 * <p/>
 * This is a complex data type. More information can be found here.
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @see <a href="http://en.wikipedia.org/wiki/Variable-length_quantity">VLQ's in Action</a>
 * @since 1.0 Beta
 */
@AllArgsConstructor
@NoArgsConstructor
public class VLQ {

    /**
     * This is the length of the VLQ field
     */
    @Getter
    @Setter
    private int length;

    /**
     * This is the actual value of the VLQ which represents how many bytes follow the VLQ
     */
    @Getter
    @Setter
    private long value;

    ///////////////////     REPRESENTS VLQ OBJECT CREATION METHODS     ///////////////////

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will created a s{@link starbounddata.variants.VLQ} from a {@link io.netty.buffer.ByteBuf}
     * <p/>
     * Notes: This will create a VLQ object and SHOULD NOT be used when possible
     * <p/>
     *
     * @param in ByteBuf in which is to be read
     * @return VLQ which represent how many reason bytes exist after the {@link starbounddata.variants.VLQ}
     */
    public static VLQ signedFromBuffer(ByteBuf in) {
        VLQ value = unsignedFromBuffer(in);
        long val = value.getValue();
        if ((value.getValue() & 1) == 0x00)
            val = (long) val >> 1;
        else
            val = -((long) (val >> 1) + 1);
        value.setValue(val);
        return value;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will created a u{@link starbounddata.variants.VLQ} from a {@link io.netty.buffer.ByteBuf}
     * <p/>
     * Notes: This will create a VLQ object and SHOULD NOT be used when possible
     * <p/>
     *
     * @param in ByteBuf in which is to be read
     * @return VLQ which represent how many reason bytes exist after the {@link starbounddata.variants.VLQ}
     * @throws IndexOutOfBoundsException if the {@link starbounddata.variants.VLQ} ran out of bytes while reading
     */
    public static VLQ unsignedFromBuffer(ByteBuf in) throws IndexOutOfBoundsException {
        long value = 0L;
        int length = 0;
        while (length <= 10) {
            byte tmp = in.readByte();
            value = (value << 7) | (long) (tmp & 0x7f);
            length++;
            if ((tmp & 0x80) == 0)
                break;
        }
        return new VLQ(length, value);
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will created a s{@link starbounddata.variants.VLQ} from a long
     * <p/>
     * Notes: This will create a VLQ object and SHOULD NOT be used when possible
     * <p/>
     *
     * @param value long the size of the bites that will precede this VLQ
     * @return byte[] which is the actual sVLQ field
     */
    public static byte[] createSignedVLQ(long value) {
        long result;
        if (value < 0) {
            result = ((-(value + 1)) << 1) | 1;
        } else {
            result = value << 1;
        }
        return createVLQ(result);
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will created a u{@link starbounddata.variants.VLQ} from a long
     * <p/>
     * Notes: This will create a VLQ object and SHOULD NOT be used when possible
     * <p/>
     *
     * @param value long the size of the bites that will precede this VLQ
     * @return byte[] which is the actual VLQ field
     */
    public static byte[] createVLQ(long value) {
        int numRelevantBits = 64 - Long.numberOfLeadingZeros(value);
        int numBytes = (numRelevantBits + 6) / 7;
        if (numBytes == 0)
            numBytes = 1;
        byte[] output = new byte[numBytes];
        for (int i = numBytes - 1; i >= 0; i--) {
            int curByte = (int) (value & 0x7F);
            if (i != (numBytes - 1))
                curByte |= 0x80;
            output[i] = (byte) curByte;
            value >>>= 7;
        }
        return output;
    }

    ///////////////////     REPRESENTS NO VLQ OBJECT CREATION METHODS     ///////////////////

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will read a s{@link starbounddata.variants.VLQ} from a {@link io.netty.buffer.ByteBuf}
     * <p/>
     * Notes: This will not create a VLQ object and should be used
     * <p/>
     *
     * @param in ByteBuf representing the bytes to be read for a reason length from a signed vlq
     * @return int representing the reason length
     */
    public static int readSignedFromBufferNoObject(ByteBuf in) {
        int payloadLength = readUnsignedFromBufferNoObject(in);
        if ((payloadLength & 1) == 0x00) {
            payloadLength = payloadLength >> 1;
        } else {
            payloadLength = -((payloadLength >> 1) + 1);
        }
        return payloadLength;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will read a u{@link starbounddata.variants.VLQ} from a {@link io.netty.buffer.ByteBuf}
     * <p/>
     * Notes: This will not create a VLQ object and should be used
     * <p/>
     *
     * @param in ByteBuf representing the bytes to be read for a reason length from a vlq
     * @return int representing the reason length
     */
    public static int readUnsignedFromBufferNoObject(ByteBuf in) {
        int vlqLength = 0;
        int payloadLength = 0;
        while (vlqLength <= 10) {
            int tmpByte = in.readByte();
            payloadLength = (payloadLength << 7) | (tmpByte & 0x7f);
            vlqLength++;
            if ((tmpByte & 0x80) == 0) {
                break;
            }
        }
        return payloadLength;
    }


    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will write a s{@link starbounddata.variants.VLQ} to a {@link io.netty.buffer.ByteBuf}
     * <p/>
     * Notes: This will not create a VLQ object and should be used
     * <p/>
     *
     * @param out   ByteBuf in which is to be read
     * @param value long representing the VLQ value to be written out
     */
    public static void writeSignedVLQNoObject(ByteBuf out, long value) {
        if (value < 0) {
            value = ((-(value + 1)) << 1) | 1;
        } else {
            value = value << 1;
        }
        writeVLQNoObject(out, value);
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will write a u{@link starbounddata.variants.VLQ} to a {@link io.netty.buffer.ByteBuf}
     * <p/>
     * Notes: This will not create a VLQ object and should be used
     * <p/>
     *
     * @param out   ByteBuf in which is to be read
     * @param value long representing the VLQ value to be written out
     */
    public static void writeVLQNoObject(ByteBuf out, long value) {
        int numBytes = ((64 - Long.numberOfLeadingZeros(value)) + 6) / 7;
        if (numBytes == 0) {
            numBytes = 1;
        }
        out.writerIndex(numBytes + 1); /* Sets the write index at the number of bytes + 1 byte for packet id */
        for (int i = numBytes - 1; i >= 0; i--) {
            int curByte = (int) (value & 0x7F);
            if (i != (numBytes - 1)) {
                curByte |= 0x80;
            }
            out.setByte(i + 1, curByte); /* Sets the byte at index + 1 byte for packet id */
            value >>>= 7;
        }
    }
}