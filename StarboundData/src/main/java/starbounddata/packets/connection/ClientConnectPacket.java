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
* this CodeHome Software.  If not, see <http://www.gnu.org/licenses/>.
*/

package starbounddata.packets.connection;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import starbounddata.packets.Packet;
import starbounddata.packets.Packets;
import starbounddata.variants.Variant;

import java.util.UUID;

import static starbounddata.packets.StarboundBufferReader.*;
import static starbounddata.packets.StarboundBufferWriter.*;

/**
 * Represents the ClientConnectPacket and methods to generate a packet data for StarNub and Plugins
 * <p/>
 * Notes: This packet can be edited freely. Please be cognisant of what values you change and how they will be interpreted by the client.
 * This packet is sent when a client is initially requesting a connection to the server after it received
 * the {@link starbounddata.packets.server.ProtocolVersionPacket}
 * <p/>
 * Packet Direction: Client -> Server
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
@NoArgsConstructor
public class ClientConnectPacket extends Packet {

    @Getter
    @Setter
    private String assetDigest;
    @Deprecated
    @Getter
    @Setter
    private Variant Claim;
    @Getter
    @Setter
    private java.util.UUID uuid;
    @Getter
    @Setter
    private String playerName;
    @Getter
    @Setter
    private String species;
    @Getter
    @Setter
    private byte[] shipWorld;
    @Getter
    @Setter
    private String account;

    public ClientConnectPacket(ChannelHandlerContext DESTINATION_CTX, String assetDigest, UUID uuid, String playerName, String species, byte[] shipWorld, String account) {
        super(Packets.CLIENTCONNECT.getPacketId(), null, DESTINATION_CTX);
        this.assetDigest = assetDigest;
//        Claim = claim;
        this.uuid = uuid;
        this.playerName = playerName;
        this.species = species;
        this.shipWorld = shipWorld;
        this.account = account;
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
        this.assetDigest = readStringVLQ(in);
        try {
            this.Claim = new Variant(in);
        } catch (Exception e) {
//            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e)); // ToDo some return facility for this
        }
        boolean uuid = readBoolean(in);
        if (uuid) {
            this.uuid = readUUID(in);
        }
        this.playerName = readStringVLQ(in);
        this.species = readStringVLQ(in);
        this.shipWorld = readVLQArray(in);
        this.account = readStringVLQ(in);
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
        writeStringVLQ(out, this.assetDigest);
        try {
            this.Claim.writeToByteBuffer(out);
        } catch (Exception e) {
//            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e)); // ToDo some return facility for this
        }
        writeBoolean(out, this.uuid != null);
        if (this.uuid != null) {
            writeUUID(out, this.uuid);
        }
        writeStringVLQ(out, this.playerName);
        writeStringVLQ(out, this.species);
        writeVLQArray(out, this.shipWorld);
        writeStringVLQ(out, this.account);
    }
}
