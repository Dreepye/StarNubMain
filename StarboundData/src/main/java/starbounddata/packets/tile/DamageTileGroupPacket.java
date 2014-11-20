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

package starbounddata.packets.tile;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import starbounddata.packets.Packet;
import starbounddata.packets.Packets;
import starbounddata.tiles.TileDamage;
import starbounddata.vectors.Vec2F;
import starbounddata.vectors.Vec2IArray;

import static starbounddata.packets.StarboundBufferReader.readFloat;
import static starbounddata.packets.StarboundBufferReader.readUnsignedByte;
import static starbounddata.packets.StarboundBufferWriter.writeByte;
import static starbounddata.packets.StarboundBufferWriter.writeFloat;

/**
 * Represents the DamageTileGroup and methods to generate a packet data for StarNub and Plugins
 * <p/>
 * Notes: This packet can be edited freely. Please be cognisant of what values you change and how they will be interpreted by the client.
 * <p/>
 * Packet Direction: Client -> Server
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class DamageTileGroupPacket extends Packet {

    @Getter
    @Setter
    TileDamage tileDamage;
    @Getter
    @Setter
    private Vec2IArray tilePositions;
    @Getter
    @Setter
    private TileLayer layer;
    @Getter
    @Setter
    private Vec2F sourcePosition;

    public DamageTileGroupPacket(ChannelHandlerContext DESTINATION_CTX, Vec2IArray tilePositions, TileLayer layer, Vec2F sourcePosition, TileDamage tileDamage) {
        super(Packets.DAMAGETILEGROUP.getPacketId(), null, DESTINATION_CTX);
        this.tilePositions = tilePositions;
        this.layer = layer;
        this.sourcePosition = sourcePosition;
        this.tileDamage = tileDamage;
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p/>
     * Recommended: For internal StarNub usage.
     * <p/>
     * Uses: This method will read in a {@link io.netty.buffer.ByteBuf} into this packets fields
     * <p/>
     *
     * @param in ByteBuf representing the reason to be read into the packet
     */
    @Override
    public void read(ByteBuf in) {
        this.tilePositions = new Vec2IArray(in);
        this.layer = TileLayer.values()[readUnsignedByte(in)];
        this.sourcePosition = new Vec2F(in);
        this.tileDamage = new TileDamage(TileDamage.TileDamageType.values()[readUnsignedByte(in)], readFloat(in));
        // Harvest Level
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p/>
     * Recommended: For internal StarNub usage.
     * <p/>
     * Uses: This method will write to a {@link io.netty.buffer.ByteBuf} using this packets fields
     * <p/>
     *
     * @param out ByteBuf representing the space to write out the packet reason
     */
    @Override
    public void write(ByteBuf out) {
        this.tilePositions.writeVec2IArray(out);
        writeByte(out, (byte) this.getLayer().ordinal());
        this.sourcePosition.writeVec2F(out);
        writeByte(out, (byte) tileDamage.getTileDamageType().ordinal());
        writeFloat(out, tileDamage.getAmount());
//        writeInt(out, tileDamage.getHarvestLevel());
    }

    public enum TileLayer {
        FOREGROUND,
        BACKGROUND
    }

    public enum TileDamageType {
        NONE,
        PLANTISH,
        BLOCKISH,
        BEAMISH,
        EXPLOSIVE,
        FIRE,
        TILLING,
        CRUSHING,
    }
}