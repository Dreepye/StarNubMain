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
import starbounddata.types.ship.ShipUpgrades;

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
 * <p>
 * Starbound 1.0 Compliant (Versions 622, Update 1)
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class ClientConnectPacket extends Packet {

    private byte[] assetDigest;
    private UUID playerUuid;
    private String playerName;
    private String playerSpecies;
    private byte[] shipData;
    private ShipUpgrades shipUpgrades = new ShipUpgrades();
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
    public ClientConnectPacket(ChannelHandlerContext DESTINATION_CTX, byte[] assetDigest, UUID playerUuid, String playerName, String playerSpecies, byte[] shipData, String account) {
        super(Packets.CLIENTCONNECT.getDirection(), Packets.CLIENTCONNECT.getPacketId(), null, DESTINATION_CTX);
        this.assetDigest = assetDigest;
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.playerSpecies = playerSpecies;
        this.shipData = shipData;
        this.account = account;
    }

    /**
     * Recommended: For internal StarNub use with copying
     * <p>
     * Uses: This will construct a new packet from a packet
     *
     * @param packet ClientConnectPacket representing the packet to construct from
     */
    public ClientConnectPacket(ClientConnectPacket packet) {
        super(packet);
        this.assetDigest = packet.getAssetDigest();
        this.playerUuid = UUID.fromString(packet.getPlayerUuid().toString());
        this.playerName = packet.getPlayerName();
        this.playerSpecies = packet.getPlayerSpecies();
        this.shipData = packet.getShipData().clone();
        this.setShipUpgrades(packet.getShipUpgrades());
        this.account = packet.getAccount();
    }

    public byte[] getAssetDigest() {
        return assetDigest;
    }

    public void setAssetDigest(byte[] assetDigest) {
        this.assetDigest = assetDigest;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public void setPlayerUuid(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerSpecies() {
        return playerSpecies;
    }

    public void setPlayerSpecies(String playerSpecies) {
        this.playerSpecies = playerSpecies;
    }

    public byte[] getShipData() {
        return shipData;
    }

    public void setShipData(byte[] shipData) {
        this.shipData = shipData;
    }

    public ShipUpgrades getShipUpgrades() {
        return shipUpgrades;
    }

    public void setShipUpgrades(ShipUpgrades shipUpgrades) {
        this.shipUpgrades = shipUpgrades;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * This will provide a new object while copying all of the internal data as well into this
     * new Object
     *
     * @return ClientConnectPacket the new copied object
     */
    @Override
    public ClientConnectPacket copy() {
        return new ClientConnectPacket(this);
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
        this.assetDigest = readVLQArray(in);
        boolean hasUUID = in.readBoolean();
        if (hasUUID) {
            this.playerUuid = readUUID(in);
        }
        this.playerName = readVLQString(in);
        this.playerSpecies = readVLQString(in);
        this.shipData = readVLQArray(in);
        this.shipUpgrades.read(in);
        this.account = readVLQString(in);
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
        writeVLQArray(out, this.assetDigest);
        if (this.playerUuid == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            writeUUID(out, this.playerUuid);
        }
        writeStringVLQ(out, this.playerName);
        writeStringVLQ(out, this.playerSpecies);
        writeVLQArray(out, this.shipData);
        this.shipUpgrades.write(out);
        writeStringVLQ(out, this.account);
    }

    @Override
    public String toString() {
        return "ClientConnectPacket{" +
                "assetDigest=" + Arrays.toString(assetDigest) +
                ", playerUuid=" + playerUuid +
                ", playerName='" + playerName + '\'' +
                ", playerSpecies='" + playerSpecies + '\'' +
                ", shipData=To Large To Display" + // Arrays.toString(shipData) +
                ", shipUpgrades=To Large To Display=" + // shipUpgrades +
                ", account='" + account + '\'' +
                "} " + super.toString();
    }
}
