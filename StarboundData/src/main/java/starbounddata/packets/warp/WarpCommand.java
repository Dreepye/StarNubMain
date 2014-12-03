package starbounddata.packets.warp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import starbounddata.packets.Packet;
import starbounddata.packets.Packets;

import static starbounddata.packets.StarboundBufferReader.readStringVLQ;
import static starbounddata.packets.StarboundBufferReader.readUnsignedByte;
import static starbounddata.packets.StarboundBufferWriter.writeStringVLQ;
import static starbounddata.packets.StarboundBufferWriter.writeByte;

/**
 * Created by r00t on 12/1/2014.
 */

public class WarpCommand extends Packet {

    /**
     * Enum for types of warp
     * */

    public enum warpType{
        MoveShip,
        WarptoMyShip,
        WarptoPlayerShip,
        WarptoPlanet,
        Warptohome
    }

    /**
     * Variables used by Warpcommand
     */
    private Byte warptype;
    private String world_coordinate;
    private String player_name;

    /**
     * set/get Methods.
     * @param warpType
     */

    public void setWarptype(Byte warpType){this.warptype=warpType;}

    public int getWarptype() {return warptype;}

    public void setPlayername(String name){this.player_name = name;}

    public String getPlayername(){return player_name;}


    public WarpCommand(ChannelHandlerContext DESTINATION_CTX, Byte warptype, String world_coordinate, String player_name){
        super(Packets.WARPCOMMAND.getDirection(),Packets.WARPCOMMAND.getPacketId(), null, DESTINATION_CTX);
        this.warptype = warptype;
        this.world_coordinate = world_coordinate;
        this.player_name = player_name;
    }


    public void read(ByteBuf in) {
        this.warptype = readUnsignedByte(in);
        this.world_coordinate = readStringVLQ(in);
        this.player_name = readStringVLQ(in);
    }

    public void write(ByteBuf out) {
        writeByte(out, this.warptype);
        writeStringVLQ(out, this.world_coordinate);
        writeStringVLQ(out, this.player_name);

    }
}
