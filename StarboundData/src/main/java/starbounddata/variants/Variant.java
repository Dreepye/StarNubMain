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
* this CodeHome Software.  If not, see <http://www.gnu.org/licenses/>.
*/

package starbounddata.variants;

import io.netty.buffer.ByteBuf;
import starbounddata.packets.StarboundBufferReader;

import java.util.HashMap;
import java.util.Map;

import static starbounddata.packets.StarboundBufferWriter.writeStringVLQ;

/**
 * Represents a Variant  which can be a byte, string, boolean, double, variant array, variant map.
 * <p/>
 * This is a complex data type.
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class Variant {

    private Object value;

    public Variant() {
    }

    public Variant(ByteBuf in) throws Exception {
        readFromByteBuffer(in);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Variant(Object value) throws Exception {
        if (!(value == null ||
                value instanceof String ||
                value instanceof Double ||
                value instanceof Boolean ||
                value instanceof Byte ||
                value instanceof Variant[] ||
                value instanceof Map<?, ?>)) {
            throw new Exception("Variants are unable to represent " + value.getClass().getName() + ".");
        }
        this.value = value;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will created a {@link starbounddata.variants.Variant} from a {@link io.netty.buffer.ByteBuf}
     * <p/>
     *
     * @param in ByteBuf representing the bytes to be read into a {@link starbounddata.variants.Variant}
     * @return Variant representing the {@link starbounddata.variants.Variant} object
     * @throws Exception if this ByteBuf does not represent a {@link starbounddata.variants.Variant}
     */
    public Variant readFromByteBuffer(ByteBuf in) throws Exception {
        Variant variant = new Variant();
        byte type = StarboundBufferReader.readUnsignedByte(in);
        switch (type) {
            case 1:
                variant.value = null;
                break;
            case 2:
                variant.value = StarboundBufferReader.readDouble(in);
                break;
            case 3:
                variant.value = in.readBoolean();
                break;
            case 4:
                variant.value = VLQ.readUnsignedFromBufferNoObject(in);
                break;
            case 5:
                variant.value = StarboundBufferReader.readStringVLQ(in);
                break;
            case 6:
                Variant[] array = new Variant[VLQ.readUnsignedFromBufferNoObject(in)];
                for (int i = 0; i < array.length; i++)
                    array[i] = this.readFromByteBuffer(in);
                variant.value = array;
                break;
            case 7:
                Map<String, Variant> dict = new HashMap<String, Variant>();
                int length = VLQ.readUnsignedFromBufferNoObject(in);
                while (length-- > 0)
                    dict.put(StarboundBufferReader.readStringVLQ(in), this.readFromByteBuffer(in));
                variant.value = dict;
                break;
            default:
                System.err.println("Unknown Variant Type: " + type);
                throw new Exception("Unknown Variant type");
        }
        return variant;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will write a {@link starbounddata.variants.Variant} to a {@link io.netty.buffer.ByteBuf}
     * <p/>
     *
     * @param out ByteBuf representing the buffer to write the variant to
     */
    @SuppressWarnings("unchecked")
    public void writeToByteBuffer(ByteBuf out) {
        if (value == null) {
            out.writeByte(1);
        } else if (value instanceof Double) {
            out.writeByte(2);
            out.writeDouble((Double) value);
        } else if (value instanceof Boolean) {
            out.writeByte(3);
            out.writeBoolean((Boolean) value);
        } else if (value instanceof Long) {
            out.writeByte(4);
            VLQ.writeVLQNoObject(out, (Long) value);
        } else if (value instanceof String) {
            out.writeByte(5);
            writeStringVLQ(out, (String) value);
        } else if (value instanceof Variant[]) {
            out.writeByte(6);
            Variant[] array = (Variant[]) value;
            VLQ.writeVLQNoObject(out, (long) array.length);
            for (Variant anArray : array) {
                anArray.writeToByteBuffer(out);
            }
        } else if (value instanceof Map<?, ?>) {
            out.writeByte(7);
            Map<String, Variant> dict = (Map<String, Variant>) value;
            VLQ.writeVLQNoObject(out, (long) dict.size());
            for (Map.Entry<String, Variant> kvp : dict.entrySet()) {
                writeStringVLQ(out, kvp.getKey());
                kvp.getValue().writeToByteBuffer(out);
            }
        }
    }
}
