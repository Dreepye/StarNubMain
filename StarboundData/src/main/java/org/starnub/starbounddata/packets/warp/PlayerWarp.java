package org.starnub.starbounddata.packets.warp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.starnub.starbounddata.ByteBufferUtilities;
import org.starnub.starbounddata.packets.Packet;
import org.starnub.starbounddata.packets.Packets;
import org.starnub.starbounddata.types.warp.ClientShipWorld;
import org.starnub.starbounddata.types.warp.MissionWorld;
import org.starnub.starbounddata.types.warp.WarpId;
import org.starnub.starbounddata.types.warp.WarpType;

/**
 * Represents the WarpCommand and methods to generate a packet data for StarNub and Plugins
 * <p>
 * Notes: This packet can be edited freely. Please be cognisant of what values you change and how they will be interpreted by the starnubclient.
 * <p>
 * Packet Direction: Client -> Server
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 *         Tim    (r00t-s)
 * @since 1.0 Beta
 */
public class PlayerWarp extends Packet {

    private WarpType warpType;
    private WarpId warpId;
    private Object locationId;

    /**
     * Recommended: For internal use with StarNub Player Sessions
     * <p>
     * Uses: This is used to pre-construct packets for a specific side of a connection
     * <p>
     * @param DIRECTION       Direction representing the direction the packet is heading
     * @param SENDER_CTX      ChannelHandlerContext which represents the sender of this packets context (Context can be written to)
     * @param DESTINATION_CTX ChannelHandlerContext which represents the destination of this packets context (Context can be written to)
     */
    public PlayerWarp(Direction DIRECTION, ChannelHandlerContext SENDER_CTX, ChannelHandlerContext DESTINATION_CTX) {
        super(DIRECTION, Packets.PLAYERWARP.getPacketId(), SENDER_CTX, DESTINATION_CTX);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This is used to construct a packet for with no destination. This CAN ONLY be routed by using (routeToGroup, routeToGroupNoFlush) methods
     * <p>
     * @param warpType
     */
    public PlayerWarp(WarpType warpType){
        super(Packets.PLAYERWARP.getDirection(),Packets.PLAYERWARP.getPacketId());
        this.warpType = warpType;
        //TODO
    }



    /**
     * Recommended: For internal StarNub use with copying
     * <p>
     * Uses: This will construct a new packet from a packet
     *
     * @param packet WarpCommand representing the packet to construct from
     */
    public PlayerWarp(PlayerWarp packet) {
        super(packet);
        this.warpType = packet.getWarpType();
        this.warpId = packet.getWarpId();
        this.locationId = packet.getLocationId();
    }

    public WarpType getWarpType() {
        return warpType;
    }

    public void setWarpType(WarpType warpType) {
        this.warpType = warpType;
    }

    public WarpId getWarpId() {
        return warpId;
    }

    public void setWarpId(WarpId warpId) {
        this.warpId = warpId;
    }

    public Object getLocationId() {
        return locationId;
    }

    public void setLocationId(Object locationId) {
        this.locationId = locationId;
    }

    /**
     * This will provide a new object while copying all of the internal data as well into this
     * new Object
     *
     * @return WarpCommand the new copied object
     */
    @Override
    public PlayerWarp copy() {
        return new PlayerWarp(this);
    }

    /**
     * Recommended: For internal use with StarNub Player Sessions
     * <p>
     * Uses: This method will read in a {@link io.netty.buffer.ByteBuf} into this packets fields
     * <p>
     * Note: This particular read will discard the packet if the tile radius exceed that of the {@link org.starnub.starbounddata.types.vectors.Vec2IArray} constructor
     *
     * @param in ByteBuf representing the reason to be read into the packet
     */
    @Override
    public void read(ByteBuf in) {
        ByteBufferUtilities.print(in, true);
        this.warpType = WarpType.values()[in.readUnsignedByte()];
        this.warpId =  WarpId.values()[in.readUnsignedByte()];
            switch (warpId){
                case CLIENT_SHIP_WORLD: {
                    this.locationId = new ClientShipWorld(in); break;
                }
                case MISSION_WORLD: {
                    this.locationId = new MissionWorld(in); break;
                }
                default: locationId = in.readBytes(in.readableBytes()).array(); break;
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
        out.writeByte(warpType.ordinal());
        out.writeByte(warpId.ordinal());
            switch (warpId){
                case CLIENT_SHIP_WORLD: {
                    ((ClientShipWorld) this.locationId).write(out); break;
                }
                case MISSION_WORLD: {
                    ((MissionWorld) this.locationId).write(out); break;
                }
                default: out.writeBytes((byte[]) locationId); break; //FINISH THE OTHER TWO
            }
    }

    @Override
    public String toString() {
        return "PlayerWarp{" +
                "warpType=" + warpType +
                ", warpId=" + warpId +
                ", locationId=" + locationId +
                "} " + super.toString();
    }
}
