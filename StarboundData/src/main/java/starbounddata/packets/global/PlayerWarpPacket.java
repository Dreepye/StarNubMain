package starbounddata.packets.global;

import server.server.packets.Packet;
import server.server.packets.StarboundBufferReader;
import starbounddata.warp.WarpTarget;
import starbounddata.warp.WarpType;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static server.server.packets.StarboundBufferWriter.writeByte;
import static server.server.packets.StarboundBufferWriter.writeByteArray;

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
     * @param out ByteBuf to be written to for outbound starbounddata.packets
     */
    @Override
    public void write(ByteBuf out) {
        writeByte(out, (byte) this.warpType.ordinal());
//        writeVLQObject(out, warpType.ordinal());
        writeByteArray(out, payload);
    }

}
