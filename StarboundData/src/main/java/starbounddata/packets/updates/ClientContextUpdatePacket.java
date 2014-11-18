package starbounddata.packets.updates;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import starbounddata.packets.Packet;
import starbounddata.packets.StarboundBufferReader;

import static starbounddata.packets.StarboundBufferWriter.writeByteArray;


@NoArgsConstructor
public class ClientContextUpdatePacket extends Packet {

    @Getter @Setter
    private byte[] clientContextUpdate;

    @Override
    public byte getPacketId() {
        return 14;
    }

    /**
     * @param in ByteBuf of the readable bytes of a received payload
     */
    @Override
    public void read(ByteBuf in) {
        this.clientContextUpdate = StarboundBufferReader.readAllBytes(in);
    }

    /**
     * @param out ByteBuf to be written to for outbound starbounddata.packets
     */
    @Override
    public void write(ByteBuf out) {
        writeByteArray(out, clientContextUpdate);
    }
}