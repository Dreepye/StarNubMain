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
import starbounddata.types.SbData;

import java.util.HashMap;
import java.util.Map;

import static starbounddata.packets.Packet.readVLQString;
import static starbounddata.types.variants.VLQ.readUnsignedFromBufferNoObject;

/**
 * Represents a Variant  which can be a byte, string, boolean, double, variant array, variant map.
 * <p>
 * This is a complex data type.
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class Variant extends SbData<Variant>{

    private VariantType variantType;
    private Object value;

    public Variant() {
    }

    public Variant(ByteBuf in) {
        read(in);
    }

    public Variant(Object value) throws Exception {
        if (!(value == null ||
                value instanceof String ||
                value instanceof Float ||
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

    @Override
    public void read(ByteBuf in) {
        this.variantType = VariantType.values()[in.readUnsignedByte()];
        switch (variantType) {
            case NIL:
                value = null;
                break;
            case FLOAT:
                value = in.readFloat();
                break;
            case BOOLEAN:
                value = in.readBoolean();
                break;
            case INTEGER:
                value = readUnsignedFromBufferNoObject(in);
                break;
            case STRING:
                value = readVLQString(in);
                break;
            case LIST://Variant List TODO is a list of variants // Size

                Variant[] array = new Variant[(int) readUnsignedFromBufferNoObject(in)];
                for (int i = 0; i < array.length; i++)
//                    array[i] = this.readFromByteBuffer(in);
                    value = array;
                break;
            case MAP://VARIANT MAP TODO
                Map<String, Variant> dict = new HashMap<String, Variant>();
                long length = readUnsignedFromBufferNoObject(in);
                while (length-- > 0)
//                    dict.put(readVLQString(in), this.readFromByteBuffer(in));
                    value = dict;
                break;
            default:
                System.err.println("Unknown Variant Type: " + variantType);
        }
    }

    @Override
    public void write(ByteBuf out) {
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
                anArray.write(out);
            }
        } else if (value instanceof Map<?, ?>) {
            out.writeByte(7);
            Map<String, Variant> dict = (Map<String, Variant>) value;
            out.writeBytes(VLQ.writeVLQNoObject((long) dict.size()));
            for (Map.Entry<String, Variant> kvp : dict.entrySet()) {
                Packet.writeStringVLQ(out, kvp.getKey());
                kvp.getValue().write(out);
            }
        }
    }

    @Override
    public String toString() {
        return "Variant{" +
                "value=" + value +
                '}';
    }
}
