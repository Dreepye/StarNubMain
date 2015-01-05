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
import starbounddata.types.entity.EntityVLQId;

import java.util.Arrays;

/**
 * Represents the EntityUpdate and methods to generate a packet data for StarNub and Plugins
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
public class EntityUpdatePacket extends Packet {

    private EntityVLQId entityId = new EntityVLQId();
    private byte[] delta;

    /**
     * Recommended: For internal use with StarNub Player Sessions
     * <p>
     * Uses: This is used to pre-construct packets for a specific side of a connection
     * <p>
     * @param DIRECTION       Direction representing the direction the packet is heading
     * @param SENDER_CTX      ChannelHandlerContext which represents the sender of this packets context (Context can be written to)
     * @param DESTINATION_CTX ChannelHandlerContext which represents the destination of this packets context (Context can be written to)
     */
    public EntityUpdatePacket(Direction DIRECTION, ChannelHandlerContext SENDER_CTX, ChannelHandlerContext DESTINATION_CTX) {
        super(DIRECTION, Packets.ENTITYUPDATE.getPacketId(), SENDER_CTX, DESTINATION_CTX);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This is used to construct a packet for a specific destination
     * <p>
     * @param DESTINATION_CTX ChannelHandlerContext which represents the destination of this packets context (Context can be written to)
     * @param entityId
     * @param delta
     */
    public EntityUpdatePacket(ChannelHandlerContext DESTINATION_CTX, EntityVLQId entityId, byte[] delta){
        super(Packets.ENTITYUPDATE.getDirection(), Packets.ENTITYUPDATE.getPacketId(), DESTINATION_CTX);
        this.entityId = entityId;
        this.delta = delta;
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This is used to construct a packet for with no destination. This CAN ONLY be routed by using (routeToGroup, routeToGroupNoFlush) methods
     * <p>
     * @param entityId
     * @param delta
     */
    public EntityUpdatePacket(EntityVLQId entityId, byte[] delta){
        super(Packets.ENTITYUPDATE.getDirection(), Packets.ENTITYUPDATE.getPacketId());
        this.entityId = entityId;
        this.delta = delta;
    }


    /**
     * Recommended: For internal StarNub use with copying
     * <p>
     * Uses: This will construct a new packet from a packet
     *
     * @param packet EntityUpdatePacket representing the packet to construct from
     */
    public EntityUpdatePacket(EntityUpdatePacket packet) {
        super(packet);
        this.entityId = packet.getEntityId();
        this.delta = packet.getDelta().clone();
    }

    public EntityVLQId getEntityId() {
        return entityId;
    }

    public void setEntityId(EntityVLQId entityId) {
        this.entityId = entityId;
    }

    public byte[] getDelta() {
        return delta;
    }

    public void setDelta(byte[] delta) {
        this.delta = delta;
    }

    /**
     * This will provide a new object while copying all of the internal data as well into this
     * new Object
     *
     * @return EntityUpdatePacket the new copied object
     */
    @Override
    public EntityUpdatePacket copy() {
        return new EntityUpdatePacket(this);
    }

    /**
     * Uses: This method will read in a {@link io.netty.buffer.ByteBuf} into this packets fields
     * <p>
     *
     * @param in ByteBuf representing the reason to be read into the packet
     */
    @Override
    public void read(ByteBuf in) {
        ByteBufferUtilities.print(in, true);
//        ByteBuf duplicate = in.duplicate();
//        VLQ.readUnsignedFromBufferNoObject(duplicate);
//        VLQ.readUnsignedFromBufferNoObject(duplicate);
//        long longnum = VLQ.readUnsignedFromBufferNoObject(duplicate);
//        System.out.println(longnum);
//        if (longnum  == 8){
//            VLQ.readUnsignedFromBufferNoObject(duplicate);
//            System.out.println(VLQ.readUnsignedFromBufferNoObject(duplicate));
//            VLQ.readUnsignedFromBufferNoObject(duplicate);
//            System.out.println(VLQ.readUnsignedFromBufferNoObject(duplicate));
//        }
        this.entityId.read(in);
        this.delta = in.readBytes(in.readableBytes()).array();
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
        this.entityId.write(out);
        out.writeBytes(delta);
    }

    @Override
    public String toString() {
        return "EntityUpdatePacket{" +
                "entityId=" + entityId +
                ", delta=" + Arrays.toString(delta) +
                "} " + super.toString();
    }
}
