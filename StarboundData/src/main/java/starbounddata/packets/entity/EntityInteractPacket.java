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
import starbounddata.types.entity.EntityId;
import starbounddata.types.vectors.Vec2F;

/**
 * Represents the EntityInteract and methods to generate a packet data for StarNub and Plugins
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
public class EntityInteractPacket extends Packet {

    private EntityId sourceEntityId = new EntityId();
    private Vec2F position = new Vec2F();
    private EntityId targetEntityId = new EntityId();


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
    public EntityInteractPacket(Direction DIRECTION, ChannelHandlerContext SENDER_CTX, ChannelHandlerContext DESTINATION_CTX) {
        super(DIRECTION, Packets.ENTITYINTERACT.getPacketId(), SENDER_CTX, DESTINATION_CTX);
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
    public EntityInteractPacket(ChannelHandlerContext DESTINATION_CTX, EntityId sourceEntityId, Vec2F position , EntityId targetEntityId) {
        super(Packets.ENTITYINTERACT.getDirection(), Packets.ENTITYINTERACT.getPacketId(), null, DESTINATION_CTX);
        this.sourceEntityId = sourceEntityId;
        this.position = position;
        this.targetEntityId = targetEntityId;
    }

    /**
     * Recommended: For internal StarNub use with copying
     * <p>
     * Uses: This will construct a new packet from a packet
     *
     * @param packet DamageRequestPacket representing the packet to construct from
     */
    public EntityInteractPacket(EntityInteractPacket packet) {
        super(packet);
        this.sourceEntityId = packet.getSourceEntityId();
        this.position = packet.getPosition();
        this.targetEntityId = packet.getTargetEntityId();
    }

    public EntityId getSourceEntityId() {
        return sourceEntityId;
    }

    public void setSourceEntityId(EntityId sourceEntityId) {
        this.sourceEntityId = sourceEntityId;
    }

    public Vec2F getPosition() {
        return position;
    }

    public void setPosition(Vec2F position) {
        this.position = position;
    }

    public EntityId getTargetEntityId() {
        return targetEntityId;
    }

    public void setTargetEntityId(EntityId targetEntityId) {
        this.targetEntityId = targetEntityId;
    }

    /**
     * This will provide a new object while copying all of the internal data as well into this
     * new Object
     *
     * @return DamageRequestPacket the new copied object
     */
    @Override
    public EntityInteractPacket copy() {
        return new EntityInteractPacket(this);
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
        this.sourceEntityId.readEntityId(in);
        this.position.readVec2F(in);
        this.targetEntityId.readEntityId(in);
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
        this.sourceEntityId.writeEntityId(out);
        this.position.writeVec2F(out);
        this.targetEntityId.writeEntityId(out);
    }

    @Override
    public String toString() {
        return "EntityInteractPacket{" +
                "sourceEntityId=" + sourceEntityId +
                ", position=" + position +
                ", targetEntityId=" + targetEntityId +
                "} " + super.toString();
    }
}
