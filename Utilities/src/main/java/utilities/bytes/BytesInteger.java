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

package utilities.bytes;

/**
 * Represents BytesInteger which converts Integers to and from byte[]'s
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public class BytesInteger {

    public static void putInt(int value, byte[] array, int offset) {
        array[offset]   = (byte)(0xff & (value >> 24));
        array[offset+1] = (byte)(0xff & (value >> 16));
        array[offset+2] = (byte)(0xff & (value >> 8));
        array[offset+3] = (byte)(0xff & value);
    }

    public static int getInt(byte[] array, int offset) {
        return
                ((array[offset]   & 0xff) << 24) |
                        ((array[offset+1] & 0xff) << 16) |
                        ((array[offset+2] & 0xff) << 8) |
                        (array[offset+3] & 0xff);
    }

    public static void putLong(long value, byte[] array, int offset) {
        array[offset]   = (byte)(0xff & (value >> 56));
        array[offset+1] = (byte)(0xff & (value >> 48));
        array[offset+2] = (byte)(0xff & (value >> 40));
        array[offset+3] = (byte)(0xff & (value >> 32));
        array[offset+4] = (byte)(0xff & (value >> 24));
        array[offset+5] = (byte)(0xff & (value >> 16));
        array[offset+6] = (byte)(0xff & (value >> 8));
        array[offset+7] = (byte)(0xff & value);
    }

    public static long getLong(byte[] array, int offset) {
        return
                ((long)(array[offset]   & 0xff) << 56) |
                        ((long)(array[offset+1] & 0xff) << 48) |
                        ((long)(array[offset+2] & 0xff) << 40) |
                        ((long)(array[offset+3] & 0xff) << 32) |
                        ((long)(array[offset+4] & 0xff) << 24) |
                        ((long)(array[offset+5] & 0xff) << 16) |
                        ((long)(array[offset+6] & 0xff) << 8) |
                        ((long)(array[offset+7] & 0xff));
    }
}
