package org.starnub.server.server.packets.world;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.starnub.server.server.packets.Packet;
import org.starnub.server.server.packets.StarboundBufferReader;

import static org.starnub.server.server.packets.StarboundBufferWriter.writeByteArray;

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
     * @param out ByteBuf to be written to for outbound packets
     */
    @Override
    public void write(ByteBuf out) {
        writeByteArray(out, startReason);
    }
}