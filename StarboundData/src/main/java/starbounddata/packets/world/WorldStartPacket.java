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

package starbounddata.packets.world;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import starbounddata.ByteBufferUtilities;
import starbounddata.packets.Packet;
import starbounddata.packets.Packets;
import starbounddata.types.dungeon.ProtectedDungeonIds;
import starbounddata.types.variants.Variant;
import starbounddata.types.vectors.Vec2F;

import java.util.Arrays;

/**
 * Represents the WorldStopPacket and methods to generate a packet data for StarNub and Plugins
 * <p>
 * Notes: This packet cannot be edited freely or sent to a client out of stream
 * <p>
 * Packet Direction: Server -> Client //DEBUG ALL PACKET PARTS
 * <p>
 * Starbound 1.0 Compliant (Versions 622, Update 1)
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class WorldStartPacket extends Packet {

    private Variant templateData = new Variant();
    private byte[] skyData;
    private byte[] weatherData;
    private Vec2F playerStart = new Vec2F();
    private ProtectedDungeonIds protectedDungeonIds = new ProtectedDungeonIds();
    private Variant worldProperties = new Variant();
    private int clientId;
    private boolean localInterpolationMode;

    /**
     * Recommended: For internal use with StarNub Player Sessions
     * <p>
     * Uses: This is used to pre-construct packets for a specific side of a connection
     * <p>
     * @param DIRECTION       Direction representing the direction the packet is heading
     * @param SENDER_CTX      ChannelHandlerContext which represents the sender of this packets context (Context can be written to)
     * @param DESTINATION_CTX ChannelHandlerContext which represents the destination of this packets context (Context can be written to)
     */
    public WorldStartPacket(Direction DIRECTION, ChannelHandlerContext SENDER_CTX, ChannelHandlerContext DESTINATION_CTX) {
        super(DIRECTION, Packets.WORLDSTART.getPacketId(), SENDER_CTX, DESTINATION_CTX);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This is used to construct a packet for a specific destination
     * <p>
     * @param DESTINATION_CTX ChannelHandlerContext which represents the destination of this packets context (Context can be written to)
     * @param templateData
     * @param skyData
     * @param weatherData
     * @param playerStart
     * @param protectedDungeonIds
     * @param worldProperties
     * @param clientId
     * @param localInterpolationMode
     */
    public WorldStartPacket(ChannelHandlerContext DESTINATION_CTX, Variant templateData, byte[] skyData, byte[] weatherData, Vec2F playerStart, ProtectedDungeonIds protectedDungeonIds, Variant worldProperties, int clientId, boolean localInterpolationMode) {
        super(Packets.WORLDSTART.getDirection(), Packets.WORLDSTART.getPacketId(), DESTINATION_CTX);
        this.templateData = templateData;
        this.skyData = skyData;
        this.weatherData = weatherData;
        this.playerStart = playerStart;
        this.protectedDungeonIds = protectedDungeonIds;
        this.worldProperties = worldProperties;
        this.clientId = clientId;
        this.localInterpolationMode = localInterpolationMode;
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This is used to construct a packet for with no destination. This CAN ONLY be routed by using (routeToGroup, routeToGroupNoFlush) methods
     * <p>
     * @param templateData
     * @param skyData
     * @param weatherData
     * @param playerStart
     * @param protectedDungeonIds
     * @param worldProperties
     * @param clientId
     * @param localInterpolationMode
     */
    public WorldStartPacket(Variant templateData, byte[] skyData, byte[] weatherData, Vec2F playerStart, ProtectedDungeonIds protectedDungeonIds, Variant worldProperties, int clientId, boolean localInterpolationMode) {
        super(Packets.WORLDSTART.getDirection(), Packets.WORLDSTART.getPacketId());
        this.templateData = templateData;
        this.skyData = skyData;
        this.weatherData = weatherData;
        this.playerStart = playerStart;
        this.protectedDungeonIds = protectedDungeonIds;
        this.worldProperties = worldProperties;
        this.clientId = clientId;
        this.localInterpolationMode = localInterpolationMode;
    }


    /**
     * Recommended: For internal StarNub use with copying
     * <p>
     * Uses: This will construct a new packet from a packet
     *
     * @param packet WorldStopPacket representing the packet to construct from
     */
    public WorldStartPacket(WorldStartPacket packet) {
        super(packet);
        this.templateData = packet.getTemplateData().copy();
        this.skyData = packet.getSkyData().clone();
        this.weatherData = packet.getWeatherData().clone();
        this.playerStart = packet.getPlayerStart().copy();
        this.protectedDungeonIds = packet.getProtectedDungeonIds().copy();
        this.worldProperties = packet.getWorldProperties().copy();
        this.clientId = packet.getClientId();
        this.localInterpolationMode = packet.isLocalInterpolationMode();
    }

    public Variant getTemplateData() {
        return templateData;
    }

    public void setTemplateData(Variant templateData) {
        this.templateData = templateData;
    }

    public byte[] getSkyData() {
        return skyData;
    }

    public void setSkyData(byte[] skyData) {
        this.skyData = skyData;
    }

    public byte[] getWeatherData() {
        return weatherData;
    }

    public void setWeatherData(byte[] weatherData) {
        this.weatherData = weatherData;
    }

    public Vec2F getPlayerStart() {
        return playerStart;
    }

    public void setPlayerStart(Vec2F playerStart) {
        this.playerStart = playerStart;
    }

    public ProtectedDungeonIds getProtectedDungeonIds() {
        return protectedDungeonIds;
    }

    public void setProtectedDungeonIds(ProtectedDungeonIds protectedDungeonIds) {
        this.protectedDungeonIds = protectedDungeonIds;
    }

    public Variant getWorldProperties() {
        return worldProperties;
    }

    public void setWorldProperties(Variant worldProperties) {
        this.worldProperties = worldProperties;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public boolean isLocalInterpolationMode() {
        return localInterpolationMode;
    }

    public void setLocalInterpolationMode(boolean localInterpolationMode) {
        this.localInterpolationMode = localInterpolationMode;
    }

    /**
     * This will provide a new object while copying all of the internal data as well into this
     * new Object
     *
     * @return WorldStopPacket the new copied object
     */
    @Override
    public WorldStartPacket copy() {
        return new WorldStartPacket(this);
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
        ByteBufferUtilities.print(in, true);
        this.templateData.read(in);
        this.skyData = readVLQArray(in);
        this.weatherData = readVLQArray(in);
        this.playerStart.read(in);
//        this.protectedDungeonIds.read(in); // DEBUG
        this.worldProperties.read(in);
        this.clientId = in.readInt();
        this.localInterpolationMode = in.readBoolean();
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
        this.templateData.write(out);
        writeVLQArray(out, this.skyData);
        writeVLQArray(out, this.weatherData);
        this.playerStart.write(out);
//        this.protectedDungeonIds.write(out);
        this.worldProperties.write(out);
        out.writeInt(this.clientId);
        out.writeBoolean(this.localInterpolationMode);
    }

    @Override
    public String toString() {
        return "WorldStartPacket{" +
                "templateData=" + templateData +
                ", skyData=" + Arrays.toString(skyData) +
                ", weatherData=" + Arrays.toString(weatherData) +
                ", playerStart=" + playerStart +
                ", protectedDungeonIds=" + protectedDungeonIds +
                ", worldProperties=" + worldProperties +
                ", clientId=" + clientId +
                ", localInterpolationMode=" + localInterpolationMode +
                "} " + super.toString();
    }
}