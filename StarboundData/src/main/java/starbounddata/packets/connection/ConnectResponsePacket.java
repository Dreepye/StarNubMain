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
import starbounddata.types.planet.CelestialBaseInformation;
import starbounddata.types.variants.VLQ;


/**
 * Represents the ClientConnectPacket and methods to generate a packet data for StarNub and Plugins
 * <p>
 * Notes: This packet can be edited freely. Please be cognisant of what values you change and how they will be
 * interpreted by the starnubclient. This packet is sent to the starnubclient to notify them of connection success with all
 * of the loading data
 * <p>
 * Packet Direction: Server -> Client
 * <p>
 * Starbound 1.0 Compliant (Versions 622, Update 1)
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class ConnectResponsePacket extends Packet {

    private boolean success;
    private long clientId;
    private String rejectionReason;
    private CelestialBaseInformation celestialBaseInformation = new CelestialBaseInformation();

    /**
     * Recommended: For internal use with StarNub Player Sessions
     * <p>
     * Uses: This is used to pre-construct packets for a specific side of a connection
     * <p>
     * @param DIRECTION       Direction representing the direction the packet is heading
     * @param SENDER_CTX      ChannelHandlerContext which represents the sender of this packets context (Context can be written to)
     * @param DESTINATION_CTX ChannelHandlerContext which represents the destination of this packets context (Context can be written to)
     */
    public ConnectResponsePacket(Direction DIRECTION, ChannelHandlerContext SENDER_CTX, ChannelHandlerContext DESTINATION_CTX) {
        super(DIRECTION, Packets.CONNECTRESPONSE.getPacketId(), SENDER_CTX, DESTINATION_CTX);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This is used to construct a packet for with no destination. This CAN ONLY be routed by using (routeToGroup, routeToGroupNoFlush) methods
     * <p>
     * @param success
     * @param clientId
     * @param rejectionReason
     * @param celestialBaseInformation
     */
    public ConnectResponsePacket(boolean success, int clientId, String rejectionReason, CelestialBaseInformation celestialBaseInformation) {
        super(Packets.CLIENTCONNECT.getDirection(), Packets.CLIENTCONNECT.getPacketId());
        this.success = success;
        this.clientId = clientId;
        this.rejectionReason = rejectionReason;
        this.celestialBaseInformation = celestialBaseInformation;
    }

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
        this.celestialBaseInformation = packet.getCelestialBaseInformation().copy();
    }

    public boolean isSuccess() {
        return success;
    }

    public void makeSuccessful() {
        this.success = true;
    }

    public void makeUnsuccessful() {
        this.success = false;
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

    public CelestialBaseInformation getCelestialBaseInformation() {
        return celestialBaseInformation;
    }

    public void setCelestialBaseInformation(CelestialBaseInformation celestialBaseInformation) {
        this.celestialBaseInformation = celestialBaseInformation;
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
     * Recommended: For internal use with StarNub Player Sessions
     * <p>
     * Uses: This method will read in a {@link io.netty.buffer.ByteBuf} into this packets fields
     * <p>
     *
     * @param in ByteBuf representing the reason to be read into the packet
     */
    @Override
    public void read(ByteBuf in) {
        this.success = in.readBoolean();
        this.clientId = VLQ.readUnsignedFromBufferNoObject(in);
        this.rejectionReason = readVLQString(in);
        boolean hasCelestialBaseInformation =  in.readBoolean();
        if(hasCelestialBaseInformation){
            this.celestialBaseInformation.read(in);
        }
    }

    /**
     * Recommended: For internal use with StarNub Player Sessions
     * <p>
     * Uses: This method will write to a {@link io.netty.buffer.ByteBuf} using this packets fields
     * <p>
     *
     * @param out ByteBuf representing the space to write out the packet reason
     */
    @Override
    public void write(ByteBuf out) {
        out.writeBoolean(this.success);
        out.writeBytes(VLQ.writeSignedVLQNoObject(this.clientId));
        writeStringVLQ(out, this.rejectionReason);
        if (celestialBaseInformation == null){
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            celestialBaseInformation.write(out);
        }
    }

    @Override
    public String toString() {
        return "ConnectResponsePacket{" +
                "success=" + success +
                ", clientId=" + clientId +
                ", rejectionReason='" + rejectionReason + '\'' +
                ", celestialBaseInformation=" + celestialBaseInformation +
                "} " + super.toString();
    }
}
