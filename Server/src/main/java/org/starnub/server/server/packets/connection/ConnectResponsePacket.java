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

package org.starnub.server.server.packets.connection;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.starnub.server.server.datatypes.variants.Variant;
import org.starnub.server.server.packets.Packet;
import org.starnub.server.server.packets.StarboundBufferReader;

/**
 * Connection Response Packet class. This packet tells the client
 * whether their connection attempt is successful or if they have
 * been rejected. It is the final packet sent in the handshake process.
 * <p>
 * NOTE:
 * <P>
 * Credit goes to: <br>
 * SirCmpwn - (https://github.com/SirCmpwn/StarNet) <br>
 * Mitch528 - (https://github.com/Mitch528/SharpStar) <br>
 * Starbound-Dev - (http://starbound-dev.org/)
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @version 1.0, 24 May 2014
 */
@NoArgsConstructor
public class ConnectResponsePacket extends Packet {

    /**
     * Whether or not the connection was successful
     */
    @Getter @Setter
    private boolean success;

    /**
     * The identifier for the connecting client.
     */
    @Getter @Setter
    private long clientId;

    /**
     * A string signifying why the user was rejected.
     * This will be present even if the connection was
     * successful, but will simply have a length prefix of 0.
     */
    @Getter @Setter
    private String rejectionReason;

    /**
     * Determines whether the following celestial information exists.
     */
    @Getter @Setter
    private boolean celestialInformation;

    /**
     * The maximum number of orbital levels.
     */
    @Getter @Setter
    private int orbitalLevels;

    /**
     *
     */
    @Getter @Setter
    private int ChunkSize;

    /**
     *
     */
    @Getter @Setter
    private int XYCoordinateMin;

    /**
     *
     */
    @Getter @Setter
    private int XYCoordinateMax;

    /**
     *
     */
    @Getter @Setter
    private int ZCoordinateMin;

    /**
     *
     */
    @Getter @Setter
    private int ZCoordinateMax;

    /**
     *
     */
    @Getter @Setter
    private long NumberofSectors;

    /**
     *
     */
    @Getter @Setter
    private String SectorId;

    /**
     *
     */
    @Getter @Setter
    private String SectorName;

    /**
     *
     */
    @Getter @Setter
    private long SectorSeed;

    /**
     *
     */
    @Getter @Setter
    private String SectorPrefix;

    /**
     *
     */
    @Getter @Setter
    private Variant Parameters;

    /**
     *
     */
    @Getter @Setter
    private Variant SectorConfig;

    @Getter @Setter
    private byte[] tempByteArray;

    /**
     * @return byte representing the packet id for this class
     */
    @Override
    public byte getPacketId() {
        return 1;
    }

    /**
     * @param in ByteBuf of the readable bytes of a received payload
     */
    @Override
    public void read(ByteBuf in) {
        this.success = StarboundBufferReader.readBoolean(in);
        this.clientId = StarboundBufferReader.readVLQ(in).getValue();
        this.rejectionReason = StarboundBufferReader.readStringVLQ(in);
        this.tempByteArray = StarboundBufferReader.readAllBytes(in);
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
     * @param out ByteBuf to be written to for outbound packets
     */
    @Override
    public void write(ByteBuf out) {
        writeBoolean(out, this.success);
        writeVLQObject(out, this.clientId);
        writeStringVLQ(out, this.rejectionReason);
        writeByteArray(out, this.tempByteArray);
    }
}
