

package org.starnub.server.server.packets;

import io.netty.buffer.ByteBuf;
import org.starnub.server.server.datatypes.variants.VLQ;
import org.starnub.server.server.datatypes.variants.Variant;
import org.starnub.server.server.datatypes.vectors.Vec2F;
import org.starnub.server.server.datatypes.vectors.Vec2I;

import java.nio.charset.Charset;
import java.util.List;
import java.util.UUID;

public class StarboundBufferWriter {

    public static void writeByte(ByteBuf out, Byte value) {
        out.writeByte(value);
    }

    public static void writeByteArray(ByteBuf out, byte[] bytes) {
        out.writeBytes(bytes);
    }

    public static void writeShort(ByteBuf out, short value) {
        out.writeShort(value);
    }

    public static void writeMedium(ByteBuf out, int value) {
        out.writeMedium(value);
    }

    public static void writeInt(ByteBuf out, int value) {
        out.writeInt(value);
    }

    public static void writeLong(ByteBuf out, long value) {
        out.writeLong(value);
    }

    public static void writeFloatInt32(ByteBuf out, float value) {
        out.writeFloat(value);
    }

    public static void writeDoubleInt64(ByteBuf out, double value) {
        out.writeDouble(value);
    }

    public static void writeBoolean(ByteBuf out, boolean value) {
        out.writeByte(value ? (byte) 1 : (byte) 0);
    }

    public static void writeStringVLQ(ByteBuf out, String value) {
        writeVLQIntArray(out, value.getBytes(Charset.forName("UTF-8")));
    }

    public static void writeSignedVLQObject(ByteBuf out, long value) {
        out.writeBytes(VLQ.createSignedVLQ(value));
    }

    public static void writeVLQObject(ByteBuf out, long value) {
        out.writeBytes(VLQ.createVLQ(value));
    }

    public static void writeSignedVLQNoObject(ByteBuf out, long value) {
        if (value < 0) {
            value = ((-(value+1)) << 1) | 1;
        } else {
            value = value << 1;
        }
        writeVLQNoObject(out, value);
    }

    public static void writeVLQNoObject(ByteBuf out, long value){
        int numBytes = ((64 - Long.numberOfLeadingZeros(value)) + 6) / 7;
        if (numBytes == 0){
            numBytes = 1;
        }
        out.writerIndex(numBytes + 1); /* Sets the write index at the number of bytes + 1 byte for packet id */
        for (int i = numBytes - 1; i >= 0; i--){
            int curByte = (int)(value & 0x7F);
            if (i != (numBytes - 1)){
                curByte |= 0x80;
            }
            out.setByte(i + 1, curByte); /* Sets the byte at index + 1 byte for packet id */
            value >>>= 7;
        }
    }

    public static void writeVLQIntArray(ByteBuf out, byte[] bytes) {
        out.writeBytes(VLQ.createVLQ(bytes.length));
        out.writeBytes(bytes);
    }

    public static void writeVariant(ByteBuf out, Variant value) throws Exception {
        value.writeToByteBuffer(out);
    }

    public static void writeUUID(ByteBuf out, UUID uuid){
       out.writeLong(uuid.getMostSignificantBits());
       out.writeLong(uuid.getLeastSignificantBits());
    }

    public static void writeVector2Integer (ByteBuf out, Vec2I value) {
        out.writeInt(value.getX());
        out.writeInt(value.getY());
    }

    public static void writeVector2Float (ByteBuf out, Vec2F value) {
        out.writeFloat(value.getX());
        out.writeFloat(value.getY());
    }

    public static void writeVector2IntegerArray(ByteBuf out, List<Vec2I> value){
        writeVLQObject(out, (long) value.size());
        for (Vec2I vec2I : value) {
           writeVector2Integer(out, vec2I);
        }
    }

}
