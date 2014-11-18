package org.starnub.server.server.packets;

import io.netty.buffer.ByteBuf;
import lombok.NoArgsConstructor;

/**
 * Represents a Basic Packet.
 * <p>
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
@NoArgsConstructor
public class Packet extends AbstractPacket {

        /**
         * @param in ByteBuf of the readable bytes of a received payload
         */
        @Override
        public void read(ByteBuf in) {

        }

        /**
         * @param out ByteBuf to be written to for outbound packets
         */
        @Override
        public void write(ByteBuf out) {

        }
}

