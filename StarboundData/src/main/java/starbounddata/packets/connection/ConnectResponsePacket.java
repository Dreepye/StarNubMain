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
* this StarNub Software.  If not, see <http://www.gnu.org/licenses/>.
*/

package starbounddata.packets.connection;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import starbounddata.packets.Packet;
import starbounddata.packets.Packets;
import starbounddata.variants.VLQ;
import starbounddata.variants.Variant;

import java.util.Arrays;


/**
 * Represents the ClientConnectPacket and methods to generate a packet data for StarNub and Plugins
 * <p>
 * Notes: This packet can be edited freely. Please be cognisant of what values you change and how they will be
 * interpreted by the starnubclient. This packet is sent to the starnubclient to notify them of connection success with all
 * of the loading data
 * <p>
 * Packet Direction: Server -> Client
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class ConnectResponsePacket extends Packet {

    private boolean success;
    private long clientId;
    private String rejectionReason;
    private boolean celestialInformation;
    private int orbitalLevels;
    private int ChunkSize;
    private int XYCoordinateMin;
    private int XYCoordinateMax;
    private int ZCoordinateMin;
    private int ZCoordinateMax;
    private long NumberofSectors;
    private String SectorId;
    private String SectorName;
    private long SectorSeed;
    private String SectorPrefix;
    private Variant Parameters;
    private Variant SectorConfig;
    private byte[] tempByteArray;


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
    public ConnectResponsePacket(Direction DIRECTION, ChannelHandlerContext SENDER_CTX, ChannelHandlerContext DESTINATION_CTX) {
        super(DIRECTION, Packets.CONNECTRESPONSE.getPacketId(), SENDER_CTX, DESTINATION_CTX);
    }

//    /**
//     * Recommended: For connections StarNub usage.
//     * <p/>
//     * Uses: This method will be used to send a packet to the client with the server version. You only need the destination in order t
//     * router this packet
//     * <p/>
//     *
//     * @param DESTINATION_CTX ChannelHandlerContext which represents the destination of this packets context (Context can be written to)
//     */
//    public ConnectResponsePacket(ChannelHandlerContext DESTINATION_CTX) {
//        super(Packets.DISCONNECTRESPONSE.getDirection(), Packets.DISCONNECTRESPONSE.getPacketId(), null, DESTINATION_CTX);
//
//    }

    /**
     * Recommended: For internal StarNub use with copying
     * <p>
     * Uses: This will construct a new packet from a packet
     *
     * @param packet ConnectResponsePacket representing the packet to construct from
     */
    public ConnectResponsePacket(ConnectResponsePacket packet) {
        super(packet);
        this.success = packet.isSuccess();
        this.clientId = packet.getClientId();
        this.rejectionReason = packet.getRejectionReason();
        this.tempByteArray = packet.getTempByteArray().clone();
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public boolean isCelestialInformation() {
        return celestialInformation;
    }

    public void setCelestialInformation(boolean celestialInformation) {
        this.celestialInformation = celestialInformation;
    }

    public int getOrbitalLevels() {
        return orbitalLevels;
    }

    public void setOrbitalLevels(int orbitalLevels) {
        this.orbitalLevels = orbitalLevels;
    }

    public int getChunkSize() {
        return ChunkSize;
    }

    public void setChunkSize(int chunkSize) {
        ChunkSize = chunkSize;
    }

    public int getXYCoordinateMin() {
        return XYCoordinateMin;
    }

    public void setXYCoordinateMin(int XYCoordinateMin) {
        this.XYCoordinateMin = XYCoordinateMin;
    }

    public int getXYCoordinateMax() {
        return XYCoordinateMax;
    }

    public void setXYCoordinateMax(int XYCoordinateMax) {
        this.XYCoordinateMax = XYCoordinateMax;
    }

    public int getZCoordinateMin() {
        return ZCoordinateMin;
    }

    public void setZCoordinateMin(int ZCoordinateMin) {
        this.ZCoordinateMin = ZCoordinateMin;
    }

    public int getZCoordinateMax() {
        return ZCoordinateMax;
    }

    public void setZCoordinateMax(int ZCoordinateMax) {
        this.ZCoordinateMax = ZCoordinateMax;
    }

    public long getNumberofSectors() {
        return NumberofSectors;
    }

    public void setNumberofSectors(long numberofSectors) {
        NumberofSectors = numberofSectors;
    }

    public String getSectorId() {
        return SectorId;
    }

    public void setSectorId(String sectorId) {
        SectorId = sectorId;
    }

    public String getSectorName() {
        return SectorName;
    }

    public void setSectorName(String sectorName) {
        SectorName = sectorName;
    }

    public long getSectorSeed() {
        return SectorSeed;
    }

    public void setSectorSeed(long sectorSeed) {
        SectorSeed = sectorSeed;
    }

    public String getSectorPrefix() {
        return SectorPrefix;
    }

    public void setSectorPrefix(String sectorPrefix) {
        SectorPrefix = sectorPrefix;
    }

    public Variant getParameters() {
        return Parameters;
    }

    public void setParameters(Variant parameters) {
        Parameters = parameters;
    }

    public Variant getSectorConfig() {
        return SectorConfig;
    }

    public void setSectorConfig(Variant sectorConfig) {
        SectorConfig = sectorConfig;
    }

    public byte[] getTempByteArray() {
        return tempByteArray;
    }

    public void setTempByteArray(byte[] tempByteArray) {
        this.tempByteArray = tempByteArray;
    }

    /**
     * This will provide a new object while copying all of the internal data as well into this
     * new Object
     *
     * @return ConnectResponsePacket the new copied object
     */
    @Override
    public ConnectResponsePacket copy() {
        return new ConnectResponsePacket(this);
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
        this.success = readBoolean(in);
        this.clientId = VLQ.readUnsignedFromBufferNoObject(in);
        this.rejectionReason = readStringVLQ(in);
        this.tempByteArray = in.readBytes(in.readableBytes()).array();
//        celestialInformation = stream.readBoolean();
//        if (celestialInformation) {
//            orbitalLevels = stream.readUnsignedInt();
//            ChunkSize = stream.readUnsignedInt();
//            XYCoordinateMin = stream.readUnsignedInt();
//            XYCoordinateMax = stream.readUnsignedInt();
//            ZCoordinateMin = stream.readUnsignedInt();
//            ZCoordinateMax = stream.readUnsignedInt();
//            NumberofSectors = stream.readVLQ().getValue();
//            SectorId = stream.readStringVLQ();
//            SectorName = stream.readStringVLQ();
//            SectorSeed = stream.readLong();
//            SectorPrefix = stream.readStringVLQ();
//            try { Parameters = stream.readVariant(); } catch (Exception e) {StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e)); }
//            try { SectorConfig = stream.readVariant(); } catch (Exception e) {StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e)); }
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
        writeBoolean(out, this.success);
        VLQ.writeSignedVLQNoObjectPacketEncoder(out, this.clientId);
        writeStringVLQ(out, this.rejectionReason);
        out.writeBytes(this.tempByteArray);
    }

    @Override
    public String toString() {
        return "ConnectResponsePacket{" +
                "success=" + success +
                ", clientId=" + clientId +
                ", rejectionReason='" + rejectionReason + '\'' +
                ", celestialInformation=" + celestialInformation +
                ", orbitalLevels=" + orbitalLevels +
                ", ChunkSize=" + ChunkSize +
                ", XYCoordinateMin=" + XYCoordinateMin +
                ", XYCoordinateMax=" + XYCoordinateMax +
                ", ZCoordinateMin=" + ZCoordinateMin +
                ", ZCoordinateMax=" + ZCoordinateMax +
                ", NumberofSectors=" + NumberofSectors +
                ", SectorId='" + SectorId + '\'' +
                ", SectorName='" + SectorName + '\'' +
                ", SectorSeed=" + SectorSeed +
                ", SectorPrefix='" + SectorPrefix + '\'' +
                ", Parameters=" + Parameters +
                ", SectorConfig=" + SectorConfig +
                ", tempByteArray=" + Arrays.toString(tempByteArray) +
                "} " + super.toString();
    }
}
