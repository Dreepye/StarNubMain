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
import starbounddata.types.SbData;
import starbounddata.types.exceptions.VariantUnknownType;

import static starbounddata.packets.Packet.readVLQString;
import static starbounddata.packets.Packet.writeStringVLQ;

/**
 * Represents a Variant  which can be a byte, string, boolean, double, variant array, variant map.
 * <p>
 * This is a complex data type.
 *
 * Starbound 1.0 Compliant (Versions 622, Update 1)
 */
public class Variant extends SbData<Variant>{

    private VariantType variantType;
    private Object value;

    public Variant() {
    }

    public Variant(ByteBuf in) {
        read(in);
    }

    public Variant(Object value) throws VariantUnknownType {
        if (value == null){
            this.variantType = VariantType.NIL;
        } else if (value instanceof Float) {
            this.variantType = VariantType.DOUBLE;
        } else if (value instanceof Boolean) {
            this.variantType = VariantType.BOOLEAN;
        } else if (value instanceof Integer) {
            this.variantType = VariantType.INTEGER;
        } else if (value instanceof String) {
            this.variantType = VariantType.STRING;
        } else if (value instanceof VariantList) {
            this.variantType = VariantType.LIST;
        } else if (value instanceof VariantMap) {
            this.variantType = VariantType.MAP;
        } else {
            throw new VariantUnknownType("Unknown Variant Type: " + value);
        }
        this.value = value;
    }

    public Variant(Variant variant) {
        VariantType type = variant.variantType;
        Object variantValue = variant.getValue();
        switch (type){
            case LIST: {
                this.value = ((VariantList) variantValue).copy();
                break;
            }
            case MAP: {
                this.value = ((VariantMap) variantValue).copy();
                break;
            }
            default: {
                this.value = variantValue;
                break;
            }
        }
    }

    public VariantType getVariantType() {
        return variantType;
    }

    public void setVariantType(VariantType variantType) {
        this.variantType = variantType;
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
            case NIL: {
                value = null;
                break;
            }
            case DOUBLE: {
                value = in.readDouble();
                break;
            }
            case BOOLEAN: {
                value = in.readBoolean();
                break;
            }
            case INTEGER: {
                value = VLQ.readUnsignedFromBufferNoObject(in);
                break;
            }
            case STRING: {
                value = readVLQString(in);
                break;
            }
            case LIST: {
                value = new VariantList(in);
                break;
            }
            case MAP:
                value = new VariantMap(in);
                break;
            default:
                System.err.println("Unknown Variant Type, Variant Byte: " + variantType);
        }
    }

    @Override
    public void write(ByteBuf out) {
        out.writeByte(variantType.ordinal());
        switch (variantType) {
            case NIL: {
                break;
            }
            case DOUBLE: {
                out.writeDouble((Double) value);
                break;
            }
            case BOOLEAN: {
                out.writeBoolean((Boolean) value);
                break;
            }
            case INTEGER: {
                byte[] bytes = VLQ.writeUnsignedVLQNoObject((Long) value);
                out.writeBytes(bytes);
                break;
            }
            case STRING: {
                writeStringVLQ(out, (String) value);
                break;
            }
            case LIST: {
                ((VariantList) value).write(out);
                break;
            }
            case MAP:
                ((VariantMap) value).write(out);
                break;
            default:
                System.err.println("Unknown Variant Type: " + variantType);
        }
    }

    @Override
    public String toString() {
        return "Variant{" +
                "variantType=" + variantType +
                ", value=" + value +
                "} " + super.toString();
    }
}
