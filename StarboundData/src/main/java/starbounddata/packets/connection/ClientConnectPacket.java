/*
* Copyright (C) 2014 www.StarNub.org - Underbalanced
*
* This utilities.file is part of org.starnub a Java Wrapper for Starbound.
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
import starbounddata.packets.Packet;
import starbounddata.packets.Packets;
import starbounddata.variants.Variant;

import java.util.Arrays;
import java.util.UUID;


/**
 * Represents the ClientConnectPacket and methods to generate a packet data for StarNub and Plugins
 * <p>
 * Notes: This packet can be edited freely. Please be cognisant of what values you change and how they will be interpreted by the starnubclient.
 * This packet is sent when a starnubclient is initially requesting a connection to the starnubserver after it received
 * the {@link starbounddata.packets.server.ProtocolVersionPacket}
 * <p>
 * Packet Direction: Client -> Server
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class ClientConnectPacket extends Packet {

    private String assetDigest;
    @Deprecated
    private Variant Claim;
    private java.util.UUID uuid;
    private String playerName;
    private String species;
    private byte[] shipWorld;
    private String account;


    /**
     * Recommended: For connections StarNub usage.
     * <p>
     * Uses: This is used to pre-construct packets for a specific side of a connection
     * <p>
     *
     * @param DIRECTION       Direction representing the direction the packet flows to
     * @param SENDER_CTX      ChannelHandlerContext which represents the sender of this packets context (Context can be written to)
     * @param DESTINATION_CTX ChannelHandlerContext which represents the destination of this packets context (Context can be written to)
     */
    public ClientConnectPacket(Direction DIRECTION, ChannelHandlerContext SENDER_CTX, ChannelHandlerContext DESTINATION_CTX) {
        super(DIRECTION, Packets.CLIENTCONNECT.getPacketId(), SENDER_CTX, DESTINATION_CTX);
    }

    /**
     * Recommended: For connections StarNub usage.
     * <p>
     * Uses: This method will be used to send a packet to the client with the server version. You only need the destination in order t
     * router this packet
     * <p>
     *
     * @param DESTINATION_CTX ChannelHandlerContext which represents the destination of this packets context (Context can be written to)
     */
    public ClientConnectPacket(ChannelHandlerContext DESTINATION_CTX, String assetDigest, UUID uuid, String playerName, String species, byte[] shipWorld, String account) {
        super(Packets.CLIENTCONNECT.getDirection(), Packets.CLIENTCONNECT.getPacketId(), null, DESTINATION_CTX);
        this.assetDigest = assetDigest;
//        Claim = claim;
        this.uuid = uuid;
        this.playerName = playerName;
        this.species = species;
        this.shipWorld = shipWorld;
        this.account = account;
    }

    public String getAssetDigest() {
        return assetDigest;
    }

    public void setAssetDigest(String assetDigest) {
        this.assetDigest = assetDigest;
    }

    public Variant getClaim() {
        return Claim;
    }

    public void setClaim(Variant claim) {
        Claim = claim;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public byte[] getShipWorld() {
        return shipWorld;
    }

    public void setShipWorld(byte[] shipWorld) {
        this.shipWorld = shipWorld;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * Recommended: For connections StarNub usage.
     * <p>
     * Uses: This method will read in a {@link io.netty.buffer.ByteBuf} into this packets fields
     * <p>
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
     * Recommended: For connections StarNub usage.
     * <p>
     * Uses: This method will write to a {@link io.netty.buffer.ByteBuf} using this packets fields
     * <p>
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

    @Override
    public String toString() {
        return "ClientConnectPacket{" +
                "assetDigest='" + assetDigest + '\'' +
                ", Claim=" + Claim +
                ", uuid=" + uuid +
                ", playerName='" + playerName + '\'' +
                ", species='" + species + '\'' +
                ", shipWorld=" + Arrays.toString(shipWorld) +
                ", account='" + account + '\'' +
                "} " + super.toString();
    }

}
