package starbounddata.packets.world;


import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import starbounddata.packets.Packet;
import starbounddata.packets.StarboundBufferReader;
import starbounddata.packets.StarboundBufferWriter;

@NoArgsConstructor
public class WorldStartPacket extends Packet {

    @Getter @Setter
    private byte[] startReason;

    @Override
    public byte getPacketId() {
        return 14;
    }

    /**
     * @param in ByteBuf of the readable bytes of a received payload
     */
    @Override
    public void read(ByteBuf in) {
        this.startReason = StarboundBufferReader.readAllBytes(in);
    }

    /**
     * @param out ByteBuf to be written to for outbound starbounddata.packets
     */
    @Override
    public void write(ByteBuf out) {
        StarboundBufferWriter.writeByteArray(out, startReason);
    }
}