/*
* Copyright (C) 2014 www.StarNub.org - Underbalanced
*
* This file is part of org.starnub a Java Wrapper for Starbound.
*
* This above mentioned StarNub software is free software:
* you can redistribute it and/or modify it under the terms
* of the GNU General Public License as published by the Free
* Software Foundation, either version  3 of the License, or
* any later version. This above mentioned CodeHome software
* is distributed in the hope that it will be useful, but
* WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
* the GNU General Public License for more details. You should
* have received a copy of the GNU General Public License in
* this StarNub Software.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.starnub.server.server.packets.tile;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.starnub.server.server.datatypes.tiles.TileDamage;
import org.starnub.server.server.datatypes.tiles.TileDamageType;
import org.starnub.server.server.datatypes.tiles.TileLayer;
import org.starnub.server.server.datatypes.vectors.Vec2F;
import org.starnub.server.server.datatypes.vectors.Vec2I;
import org.starnub.server.server.packets.Packet;
import org.starnub.server.server.packets.StarboundBufferReader;

import java.util.List;

/**
 * Heart Beat Packet class. The protocol version
 * changes with each major update.
 * <p>
 * NOTE: This packet eventually will have a Starbound configuration
 * option that will be adjustable as per the Server Owner. Currently
 * the server sends 1 packet per second to the client incremented (1,2,3,4...)
 * which is the Starbound internal cycle rate. The Client response every 3
 * Heart Beats with a increment of the receive heartbeats(3,6,9,19...)
 * <p>
 * Credit goes to: <br>
 * SirCmpwn - (https://github.com/SirCmpwn/StarNet) <br>
 * Mitch528 - (https://github.com/Mitch528/SharpStar) <br>
 * Starbound-Dev - (http://starbound-dev.org/)
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
@NoArgsConstructor
public class DamageTileGroupPacket extends Packet {

    /**
     * The Server sends steps incremented by one, the client responds every 3, increments of 3.
     */
    @Getter @Setter
    private List<Vec2I> tilePositions;
    @Getter @Setter
    private TileLayer layer;
    @Getter @Setter
    private Vec2F sourcePosition;
    @Getter @Setter
    TileDamage tileDamage;

    @Override
    public byte getPacketId() {
        return 27;
    }
    public DamageTileGroupPacket(List<Vec2I> tilePositions, TileLayer layer, Vec2F sourcePosition, TileDamage tileDamage) {
        this.tilePositions = tilePositions;
        this.layer = layer;
        this.sourcePosition = sourcePosition;
        this.tileDamage = tileDamage;
    }
    /**
     * @param in ByteBuf of the readable bytes of a received payload
     */
    @Override
    public void read(ByteBuf in) {
        this.tilePositions = StarboundBufferReader.readVector2IntegerArray(in);
        this.layer = TileLayer.values()[StarboundBufferReader.readUnsignedByte(in)];
        this.sourcePosition = StarboundBufferReader.readVector2Float(in);
        this.tileDamage = new TileDamage(TileDamageType.values()[StarboundBufferReader.readUnsignedByte(in)], StarboundBufferReader.readFloatInt32(in));
//        , readUnsignedInt(in)
    }

    /**
     * @param out ByteBuf to be written to for outbound packets
     */
    @Override
    public void write(ByteBuf out) {
        writeVector2IntegerArray(out, this.tilePositions);
        writeByte(out, (byte) this.getLayer().ordinal());
        writeVector2Float(out, this.sourcePosition);
        writeByte(out, (byte) tileDamage.getTileDamageType().ordinal());
        writeFloatInt32(out, tileDamage.getAmount());
//        writeInt(out, tileDamage.getHarvestLevel());
    }
}
