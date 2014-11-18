package org.starnub.server.server.packets.updates;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.starnub.server.server.packets.Packet;
import org.starnub.server.server.packets.StarboundBufferReader;

import static org.starnub.server.server.packets.StarboundBufferWriter.writeByteArray;

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
     * @param out ByteBuf to be written to for outbound packets
     */
    @Override
    public void write(ByteBuf out) {
        writeByteArray(out, clientContextUpdate);
    }
}