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
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a VLQ.
 * <p>
 * This is a complex data type. More information can be found here.
 * <p>
 * http://en.wikipedia.org/wiki/Variable-length_quantity
 * <p>
 * Credit goes to: <br>
 * SirCmpwn - (https://github.com/SirCmpwn/StarNet) <br>
 * Mitch528 - (https://github.com/Mitch528/SharpStar) <br>
 * Starbound-Dev - (http://starbound-dev.org/)
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public class VLQ {

    /**
     * This is the length of the VLQ field
     */
    @Getter @Setter
    private int length;

    /**
     * This is the actual value of the VLQ which represents how many bytes follow the VLQ
     */
    @Getter @Setter
    private long value;

    /**
     * Default constructor
     */
    public VLQ() {
    }

    /**
     * Constructor
     * @param length int length of the VLQ
     * @param value long set the VLQ which represent how many bytes will follow this VLQ
     */
    public VLQ(int length, long value) {
        this.length = length;
        this.value = value;
    }

    /**
     * This reads a Signed VLQ
     * @param buf ByteBuf a buffer from netty.io which is where we are reading the VLQ from.
     * @return VLQ which represent how many bytes will follow this VLQ
     */
    public static VLQ signedFromBuffer(ByteBuf buf) {
        VLQ value = unsignedFromBuffer(buf);
        long val = value.getValue();
        if ((value.getValue() & 1) == 0x00)
            val = (long) val >> 1;
        else
            val = -((long) (val >> 1) + 1);
        value.setValue(val);
        return value;
    }

    /**
     * This reads a Unsigned VLQ
     * @param buf ByteBuf a buffer from netty.io which is where we are reading the VLQ from.
     * @return VLQ which represent how many bytes will follow this VLQ
     * @throws IndexOutOfBoundsException if the VLQ is not found
     */
    public static VLQ unsignedFromBuffer(ByteBuf buf) throws IndexOutOfBoundsException {
        long value = 0L;
        int length = 0;
        while (length <= 10) {
            byte tmp = buf.readByte();
            value = (value << 7) | (long) (tmp & 0x7f);
            length++;
            if ((tmp & 0x80) == 0)
                break;
        }
        return new VLQ(length, value);
    }

    /**
     * Creates a Signed VLQ
     * @param value long the size of the bites that will precede this VLQ
     * @return byte[] which is the actual sVLQ field
     */
    public static byte[] createSignedVLQ(long value) {
        long result;
        if (value < 0) {
            result = ((-(value+1)) << 1) | 1;
        } else {
            result = value << 1;
        }
        return createVLQ(result);
    }

    /**
     * Creates a Unsigned VLQ
     * @param value long the size of the bites that will precede this VLQ
     * @return byte[] which is the actual VLQ field
     */
    public static byte[] createVLQ (long value) {
        int numRelevantBits = 64 - Long.numberOfLeadingZeros(value);
        int numBytes = (numRelevantBits + 6) / 7;
        if (numBytes == 0)
            numBytes = 1;
        byte[] output = new byte[numBytes];
        for (int i = numBytes - 1; i >= 0; i--)
        {
            int curByte = (int)(value & 0x7F);
            if (i != (numBytes - 1))
                curByte |= 0x80;
            output[i] = (byte)curByte;
            value >>>= 7;
        }
        return output;
    }
}
