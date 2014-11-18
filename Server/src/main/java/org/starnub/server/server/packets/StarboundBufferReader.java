

package org.starnub.server.server.packets;

import io.netty.buffer.ByteBuf;
import org.starnub.server.server.datatypes.variants.VLQ;
import org.starnub.server.server.datatypes.variants.Variant;
import org.starnub.server.server.datatypes.vectors.Vec2F;
import org.starnub.server.server.datatypes.vectors.Vec2I;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StarboundBufferReader {

    public static byte readUnsignedByte(ByteBuf in) {
        return (byte) in.readUnsignedByte();
    }

    public static byte[] readInt8Array(ByteBuf in) {
        return in.readBytes(1).array();
    }

    public static byte[] readInt16Array(ByteBuf in) {
        return in.readBytes(2).array();
    }

    public static byte[] readInt24Array(ByteBuf in) {
        return in.readBytes(3).array();
    }

    public static byte[] readInt32Array(ByteBuf in) {
        return in.readBytes(4).array();
    }

    public static byte[] readInt64Array(ByteBuf in) {
        return in.readBytes(8).array();
    }

    public static byte[] readInt128Array(ByteBuf in) {
        return in.readBytes(16).array();
    }

    public static byte[] readVLQIntArray(ByteBuf in) {
        VLQ vlq = readVLQ(in);
        int len = (int) vlq.getValue();
        return in.readBytes(len).array();
    }

    public static byte[] readAllBytes(ByteBuf in) {
        return in.readBytes(in.readableBytes()).array();
    }

    public static short readUnsignedShort(ByteBuf in) {
        return (short) in.readUnsignedShort();
    }

    public static int readUnsignedMedium(ByteBuf in) {
        return in.readUnsignedMedium();
    }

    public static int readUnsignedInt(ByteBuf in) {
        return (int) in.readUnsignedInt();
    }

    public static int readInt(ByteBuf in) {
        return in.readInt();
    }

    public static Long readLong(ByteBuf in) {
        return in.readLong();
    }

    public static float readFloatInt32(ByteBuf in) {
        return in.readFloat();
    }

    public static double readDoubleInt64(ByteBuf in) {
        return in.readDouble();
    }

    public static boolean readBoolean(ByteBuf in) {
        return readUnsignedByte(in) != 0;
    }

    public static String readStringVLQ(ByteBuf in) {
        try {
            return new String(readVLQIntArray(in), Charset.forName("UTF-8"));
        } catch (Exception e) {
            return null;
        }
    }

    public static VLQ readSignedVLQ(ByteBuf in) {
        return VLQ.signedFromBuffer(in);
    }


    public static VLQ readVLQ(ByteBuf in) {
        return VLQ.unsignedFromBuffer(in);
    }

    public static Variant readVariant(ByteBuf in) throws Exception {
        return new Variant().readFromByteBuffer(in);//This class will have to have method rewrites to use ByteBuf instead of wrapper class
    }

    public static UUID readUUID(ByteBuf in) {
        return UUID.nameUUIDFromBytes(readInt128Array(in));
    }

    public static Vec2I readVector2Integer (ByteBuf in) {
       return new Vec2I(in.readInt(), in.readInt());
    }

    public static Vec2F readVector2Float (ByteBuf in) {
        return new Vec2F(in.readFloat(), in.readFloat());
    }

    public static List<Vec2I> readVector2IntegerArray(ByteBuf in){
        VLQ vlq = readVLQ(in);
        List<Vec2I> position = new ArrayList<Vec2I>();
        for (int i = 0; i < (int)vlq.getValue(); i++) {
            position.add(readVector2Integer(in));
        }
        return position;
    }



}
