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

import starbounddata.packets.server.StarNub;
import server.server.packets.Packet;
import starbounddata.variants.Variant;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.UUID;

/**
 * starbounddata.packets.Packet Class.
 * <p>
 * Credit goes to: <br>
 * SirCmpwn - (https://github.com/SirCmpwn/StarNet) <br>
 * Mitch528 - (https://github.com/Mitch528/SharpStar) <br>
 * Starbound-Dev - (http://starbound-dev.org/)
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @version 1.0, 24 May 2014
 */
@NoArgsConstructor
public class ClientConnectPacket extends Packet {

    @Getter @Setter
    private String assetDigest;
    @Deprecated
    @Getter @Setter
    private Variant Claim;
    @Getter @Setter
    private java.util.UUID UUID;
    @Getter @Setter
    private String playerName;
    @Getter @Setter
    private String species;
    @Getter @Setter
    private byte[] shipWorld;
    @Getter @Setter
    private String account;

    public ClientConnectPacket(String playerName) {
        assetDigest = null;
        Claim = null;
        UUID = null;
        this.playerName = playerName;
        species = null;
        shipWorld = null;
        account = null;
    }

    public ClientConnectPacket(String assetDigest, UUID uuid, String playerName, String species, byte[] shipWorld, String account) {
        this.assetDigest = assetDigest;
//        Claim = claim;
        UUID = uuid;
        this.playerName = playerName;
        this.species = species;
        this.shipWorld = shipWorld;
        this.account = account;
    }

    @Override
    public byte getPacketId() {
        return 7;
    }

    /**
     * @param in ByteBuf of the readable bytes of a received payload
     */
    @Override
    public void read(ByteBuf in) {
        this.assetDigest = readStringVLQ(in);
        try {
            this.Claim = readVariant(in);
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        boolean uuid = readBoolean(in);
        if (uuid) {
            this.UUID = readUUID(in);
        }
        this.playerName = readStringVLQ(in);
        this.species = readStringVLQ(in);
        this.shipWorld = readVLQIntArray(in);
        this.account = readStringVLQ(in);
    }

    /**
     * @param out ByteBuf to be written to for outbound starbounddata.packets
     */
    @Override
    public void write(ByteBuf out) {
        writeStringVLQ(out, this.assetDigest);
        try {
            writeVariant(out, this.Claim);
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        writeBoolean(out, this.UUID != null);
        if (this.UUID != null) {
            writeUUID(out, this.UUID);
        }
        writeStringVLQ(out, this.playerName);
        writeStringVLQ(out, this.species);
        writeVLQIntArray(out, this.shipWorld);
        writeStringVLQ(out, this.account);
    }
}
