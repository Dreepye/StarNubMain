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
import starbounddata.variants.VLQ;
import starbounddata.variants.Variant;

import static starbounddata.packets.StarboundBufferReader.*;
import static starbounddata.packets.StarboundBufferWriter.*;

/**
 * Represents the ClientConnectPacket and methods to generate a packet data for StarNub and Plugins
 * <p/>
 * Notes: This packet can be edited freely. Please be cognisant of what values you change and how they will be
 * interpreted by the client. This packet is sent to the client to notify them of connection success with all
 * of the loading data
 * <p/>
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

    public ConnectResponsePacket(byte PACKET_ID, ChannelHandlerContext SENDER_CTX, ChannelHandlerContext DESTINATION_CTX) {
        super(PACKET_ID, SENDER_CTX, DESTINATION_CTX);
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
        this.success = readBoolean(in);
        this.clientId = VLQ.readUnsignedFromBufferNoObject(in);
        this.rejectionReason = readStringVLQ(in);
        this.tempByteArray = readAllBytes(in);
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
        writeBoolean(out, this.success);
        VLQ.writeSignedVLQNoObject(out, this.clientId);
        writeStringVLQ(out, this.rejectionReason);
        writeByteArray(out, this.tempByteArray);
    }
}
