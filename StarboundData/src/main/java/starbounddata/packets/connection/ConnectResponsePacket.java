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

package starbounddata.packets.connection;


import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@NoArgsConstructor
public class ConnectResponsePacket extends Packet {

    @Getter
    @Setter
    private boolean success;
    @Getter
    @Setter
    private long clientId;
    @Getter
    @Setter
    private String rejectionReason;
    @Getter
    @Setter
    private boolean celestialInformation;
    @Getter
    @Setter
    private int orbitalLevels;
    @Getter
    @Setter
    private int ChunkSize;
    @Getter
    @Setter
    private int XYCoordinateMin;
    @Getter
    @Setter
    private int XYCoordinateMax;
    @Getter
    @Setter
    private int ZCoordinateMin;
    @Getter
    @Setter
    private int ZCoordinateMax;
    @Getter
    @Setter
    private long NumberofSectors;
    @Getter
    @Setter
    private String SectorId;
    @Getter
    @Setter
    private String SectorName;
    @Getter
    @Setter
    private long SectorSeed;
    @Getter
    @Setter
    private String SectorPrefix;
    @Getter
    @Setter
    private Variant Parameters;
    @Getter
    @Setter
    private Variant SectorConfig;
    @Getter
    @Setter
    private byte[] tempByteArray;

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
