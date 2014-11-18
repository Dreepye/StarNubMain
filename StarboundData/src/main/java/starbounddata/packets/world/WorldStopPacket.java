package starbounddata.packets.world;


import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import starbounddata.packets.Packet;
import starbounddata.packets.StarboundBufferReader;

import static starbounddata.packets.StarboundBufferWriter.writeStringVLQ;

@NoArgsConstructor
public class WorldStopPacket extends Packet {

    @Getter @Setter
    private String stopReason;

    @Override
    public byte getPacketId() {
        return 16;
    }

    /**
     * @param in ByteBuf of the readable bytes of a received payload
     */
    @Override
    public void read(ByteBuf in) {
        this.stopReason = StarboundBufferReader.readStringVLQ(in);
    }

    /**
     * @param out ByteBuf to be written to for outbound starbounddata.packets
     */
    @Override
    public void write(ByteBuf out) {
        writeStringVLQ(out, stopReason);
    }
}