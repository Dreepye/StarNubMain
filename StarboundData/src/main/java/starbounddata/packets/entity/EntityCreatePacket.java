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

package starbounddata.packets.entity;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import starbounddata.ByteBufferUtilities;
import starbounddata.packets.Packet;
import starbounddata.packets.Packets;
import starbounddata.types.entity.EntityType;
import starbounddata.types.variants.VLQ;

import java.util.Arrays;

/**
 * Represents the EntityCreate and methods to generate a packet data for StarNub and Plugins
 * <p>
 * Notes: This packet can be edited freely. Please be cognisant of what values you change and how they will be interpreted by the starnubclient.
 * <p>
 * Packet Direction: Client -> Server//DEBUG UNKNOWN
 * <p>
 * Starbound 1.0 Compliant (Versions 622, Update 1)  //DEBUG - NOT COMPLIANT - NOT WORKING
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class EntityCreatePacket extends Packet {

    private EntityType entityType;
    private byte[] storeData;
    private long entityId;

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
    public EntityCreatePacket(Packet.Direction DIRECTION, ChannelHandlerContext SENDER_CTX, ChannelHandlerContext DESTINATION_CTX) {
        super(DIRECTION, Packets.ENTITYCREATE.getPacketId(), SENDER_CTX, DESTINATION_CTX);
    }

    /**
     * Recommended: For connections StarNub usage.
     * <p>
     * Uses: This method will be used to send a packet to the client with the server version. You only need the destination in order t
     * router this packet
     * <p>
     *
     * @param DESTINATION_CTX ChannelHandlerContext which represents the destination of this packets context (Context can be written to)
     * @param
     */
    public EntityCreatePacket(ChannelHandlerContext DESTINATION_CTX, EntityType entityType, byte[] storeData, long entityId) {
        super(Packets.ENTITYCREATE.getDirection(), Packets.ENTITYCREATE.getPacketId(), null, DESTINATION_CTX);


    }

    /**
     * Recommended: For internal StarNub use with copying
     * <p>
     * Uses: This will construct a new packet from a packet
     *
     * @param packet DamageRequestPacket representing the packet to construct from
     */
    public EntityCreatePacket(EntityCreatePacket packet) {
        super(packet);
        this.entityType = packet.getEntityType();
        this.storeData = packet.getStoreData().clone();
        this.entityId = packet.getEntityId();
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public byte[] getStoreData() {
        return storeData;
    }

    public void setStoreData(byte[] storeData) {
        this.storeData = storeData;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    /**
     * This will provide a new object while copying all of the internal data as well into this
     * new Object
     *
     * @return DamageRequestPacket the new copied object
     */
    @Override
    public EntityCreatePacket copy() {
        return new EntityCreatePacket(this);
    }

    /**
     * Uses: This method will read in a {@link io.netty.buffer.ByteBuf} into this packets fields
     * <p>
     *
     * @param in ByteBuf representing the reason to be read into the packet
     */
    @Override
    public void read(ByteBuf in) {
        ByteBufferUtilities.print(in);
        this.entityType =  EntityType.values()[in.readUnsignedByte()];
        this.storeData = readVLQArray(in);
        this.entityId = VLQ.readSignedFromBufferNoObject(in);
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
        out.writeByte(entityType.ordinal());
        out.writeBytes(storeData);
        out.writeBytes(VLQ.writeSignedVLQNoObject(entityId));
    }

    @Override
    public String toString() {
        return "EntityCreatePacket{" +
                "entityType=" + entityType +
                ", storeData=" + Arrays.toString(storeData) +
                ", entityId=" + entityId +
                "} " + super.toString();
    }


}
