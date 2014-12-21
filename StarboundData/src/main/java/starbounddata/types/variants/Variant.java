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
* this CodeHome Software.  If not, see <http://www.gnu.org/licenses/>.
*/

package starbounddata.types.variants;

import io.netty.buffer.ByteBuf;
import starbounddata.packets.Packet;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a Variant  which can be a byte, string, boolean, double, variant array, variant map.
 * <p>
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

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will created a {@link starbounddata.types.variants.Variant} from a {@link io.netty.buffer.ByteBuf}
     * <p>
     *
     * @param in ByteBuf representing the bytes to be read into a {@link starbounddata.types.variants.Variant}
     * @return Variant representing the {@link starbounddata.types.variants.Variant} object
     * @throws Exception if this ByteBuf does not represent a {@link starbounddata.types.variants.Variant}
     */
    public Variant readFromByteBuffer(ByteBuf in) throws Exception {
        Variant variant = new Variant();
        byte type = (byte) in.readUnsignedByte();
        switch (type) {
            case 1:
                variant.value = null;
                break;
            case 2:
                variant.value = in.readDouble();
                break;
            case 3:
                variant.value = in.readBoolean();
                break;
            case 4:
                variant.value = VLQ.readUnsignedFromBufferNoObject(in);
                break;
            case 5:
                variant.value = Packet.readVLQString(in);
                break;
            case 6:
                Variant[] array = new Variant[(int) VLQ.readUnsignedFromBufferNoObject(in)];
                for (int i = 0; i < array.length; i++)
                    array[i] = this.readFromByteBuffer(in);
                variant.value = array;
                break;
            case 7:
                Map<String, Variant> dict = new HashMap<String, Variant>();
                long length = VLQ.readUnsignedFromBufferNoObject(in);
                while (length-- > 0)
                    dict.put(Packet.readVLQString(in), this.readFromByteBuffer(in));
                variant.value = dict;
                break;
            default:
                System.err.println("Unknown Variant Type: " + type);
                throw new Exception("Unknown Variant type");
        }
        return variant;
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will write a {@link starbounddata.types.variants.Variant} to a {@link io.netty.buffer.ByteBuf}
     * <p>
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
            out.writeBytes(VLQ.writeVLQNoObject((Long) value));
        } else if (value instanceof String) {
            out.writeByte(5);
            Packet.writeStringVLQ(out, (String) value);
        } else if (value instanceof Variant[]) {
            out.writeByte(6);
            Variant[] array = (Variant[]) value;
            out.writeBytes(VLQ.writeVLQNoObject((long) array.length));
            for (Variant anArray : array) {
                anArray.writeToByteBuffer(out);
            }
        } else if (value instanceof Map<?, ?>) {
            out.writeByte(7);
            Map<String, Variant> dict = (Map<String, Variant>) value;
            out.writeBytes(VLQ.writeVLQNoObject((long) dict.size()));
            for (Map.Entry<String, Variant> kvp : dict.entrySet()) {
                Packet.writeStringVLQ(out, kvp.getKey());
                kvp.getValue().writeToByteBuffer(out);
            }
        }
    }

    public Variant copy() throws Exception {
        return new Variant(this.value);
    }

    @Override
    public String toString() {
        return "Variant{" +
                "value=" + value +
                '}';
    }
}
