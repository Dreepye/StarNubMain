package org.starnub.server.server.packets.global;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.starnub.server.server.datatypes.warp.WarpTarget;
import org.starnub.server.server.datatypes.warp.WarpType;
import org.starnub.server.server.packets.Packet;
import org.starnub.server.server.packets.StarboundBufferReader;

import static org.starnub.server.server.packets.StarboundBufferWriter.writeByte;
import static org.starnub.server.server.packets.StarboundBufferWriter.writeByteArray;

@NoArgsConstructor
public class PlayerWarpPacket extends Packet {

    @Getter @Setter
    private WarpType warpType;
    @Getter @Setter
    private WarpTarget warpTarget;
    @Getter @Setter
    private byte[] payload;
//    UniqueWorldId is a String
//    CelestialWorldId is CelestialCoordinates
//    ClientShipWorldId is a UUID

    public PlayerWarpPacket(WarpType warpType) {
        this.warpType = warpType;
    }

    /**
     * @return byte representing the packetId
     */
    @Override
    public byte getPacketId() {
        return 10;
    }

    /**
     * @param in ByteBuf of the readable bytes of a received payload
     */
    @Override
    public void read(ByteBuf in) {
        this.warpType = WarpType.values()[StarboundBufferReader.readUnsignedByte(in)];

//        this.warpTarget = WarpTarget.values()[((int) readVLQ(in).getValue())];
        this.payload = StarboundBufferReader.readAllBytes(in);

    }

    /**
     * @param out ByteBuf to be written to for outbound packets
     */
    @Override
    public void write(ByteBuf out) {
        writeByte(out, (byte) this.warpType.ordinal());
//        writeVLQObject(out, warpType.ordinal());
        writeByteArray(out, payload);
    }

}
