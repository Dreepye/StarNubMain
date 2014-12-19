package starbounddata.packets.warp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import starbounddata.packets.Packet;
import starbounddata.packets.Packets;

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
public class WarpCommand extends Packet {

    public enum WarpType {
        MOVE_SHIP,
        WARP_TO_MY_SHIP,
        WARP_TO_PLAYER_SHIP,
        WARP_TO_PLANET,
        WARP_TO_HOME
    }

    private WarpType warpType;
    private String worldCoordinate;
    private String playerName;

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
    public WarpCommand(Direction DIRECTION, ChannelHandlerContext SENDER_CTX, ChannelHandlerContext DESTINATION_CTX) {
        super(DIRECTION, Packets.DAMAGETILEGROUP.getPacketId(), SENDER_CTX, DESTINATION_CTX);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method will be used to send a packet to the client with the server version. You only need the destination in order t
     * router this packet
     * <p>
     *
     * @param DESTINATION_CTX ChannelHandlerContext which represents the destination of this packets context (Context can be written to)
     * @param warpType WarpType representing the warpType enumeration
     * @param worldCoordinate String representing the world coordinate
     * @param playerName String representing the layer name
     */
    public WarpCommand(ChannelHandlerContext DESTINATION_CTX, WarpType warpType, String worldCoordinate, String playerName){
        super(Packets.PLAYERWARP.getDirection(),Packets.PLAYERWARP.getPacketId(), null, DESTINATION_CTX);
        this.warpType = warpType;
        this.worldCoordinate = worldCoordinate;
        this.playerName = playerName;
    }

    /**
     * Recommended: For internal StarNub use with copying
     * <p>
     * Uses: This will construct a new packet from a packet
     *
     * @param packet WarpCommand representing the packet to construct from
     */
    public WarpCommand(WarpCommand packet) {
        super(packet);
        this.warpType = packet.getWarpType();
        this.worldCoordinate = packet.getWorldCoordinate();
        this.playerName = packet.getPlayerName();
    }

    public WarpType getWarpType() {
        return warpType;
    }

    public void setWarpType(WarpType warpType) {
        this.warpType = warpType;
    }

    public String getWorldCoordinate() {
        return worldCoordinate;
    }

    public void setWorldCoordinate(String worldCoordinate) {
        this.worldCoordinate = worldCoordinate;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * This will provide a new object while copying all of the internal data as well into this
     * new Object
     *
     * @return WarpCommand the new copied object
     */
    @Override
    public WarpCommand copy() {
        return new WarpCommand(this);
    }

    /**
     * Recommended: For connections StarNub usage.
     * <p>
     * Uses: This method will read in a {@link io.netty.buffer.ByteBuf} into this packets fields
     * <p>
     * Note: This particular read will discard the packet if the tile radius exceed that of the {@link starbounddata.types.vectors.Vec2IArray} constructor
     *
     * @param in ByteBuf representing the reason to be read into the packet
     */
    @Override
    public void read(ByteBuf in) {
        this.warpType = WarpType.values()[in.readUnsignedByte()];
        this.worldCoordinate = readStringVLQ(in);
        this.playerName = readStringVLQ(in);
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
        out.writeByte(warpType.ordinal());
        writeStringVLQ(out, this.worldCoordinate);
        writeStringVLQ(out, this.playerName);
    }

    @Override
    public String toString() {
        return "WarpCommand{" +
                "warpType=" + warpType +
                ", worldCoordinate='" + worldCoordinate + '\'' +
                ", playerName='" + playerName + '\'' +
                "} " + super.toString();
    }
}
