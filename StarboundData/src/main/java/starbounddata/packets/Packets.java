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

package starbounddata.packets;

import io.netty.channel.ChannelHandlerContext;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Set;

/**
 * Represents all of the packets and methods to generate a packet data for StarNub and Plugins
 * <p/>
 * Notes:
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public enum Packets {
    PROTOCOLVERSION("PassThroughPacket.class", Packet.Direction.STARBOUND_CLIENT),
    CONNECTIONRESPONSE("PassThroughPacket.class", Packet.Direction.STARBOUND_SERVER),
    DISCONNECTRESPONSE("PassThroughPacket.class", Packet.Direction.STARBOUND_CLIENT),
    HANDSHAKECHALLENGE("PassThroughPacket.class", Packet.Direction.NOT_USED),
    CHATRECEIVED("PassThroughPacket.class", Packet.Direction.STARBOUND_CLIENT),
    UNIVERSETIMEUPDATE("PassThroughPacket.class", Packet.Direction.STARBOUND_CLIENT),
    CELESTIALRESPONSE("PassThroughPacket.class", Packet.Direction.NOT_USED),
    CLIENTCONNECT("PassThroughPacket.class", Packet.Direction.STARBOUND_SERVER),
    CLIENTDISCONNECT("PassThroughPacket.class", Packet.Direction.STARBOUND_SERVER),
    HANDSHAKERESPONSE("PassThroughPacket.class", Packet.Direction.NOT_USED),
    WARPCOMMAND("PassThroughPacket.class", Packet.Direction.NOT_USED),
    CHATSENT("PassThroughPacket.class", Packet.Direction.STARBOUND_SERVER),
    CELESTIALREQUEST("PassThroughPacket.class", Packet.Direction.NOT_USED),
    CLIENTCONTEXTUPDATE("PassThroughPacket.class", Packet.Direction.NOT_USED),
    WORLDSTART("PassThroughPacket.class", Packet.Direction.NOT_USED),
    WORLDSTOP("PassThroughPacket.class", Packet.Direction.NOT_USED),
    TILEARRAYUPDATE("PassThroughPacket.class", Packet.Direction.NOT_USED),
    TILEUPDATE("PassThroughPacket.class", Packet.Direction.NOT_USED),
    TILELIQUIDUPDATE("PassThroughPacket.class", Packet.Direction.NOT_USED),
    TILEDAMAGEUPDATE("PassThroughPacket.class", Packet.Direction.NOT_USED),
    TILEMODIFICATIONFAILURE("PassThroughPacket.class", Packet.Direction.NOT_USED),
    GIVEITEM("PassThroughPacket.class", Packet.Direction.NOT_USED),
    SWAPCONTAINERRESULT("PassThroughPacket.class", Packet.Direction.NOT_USED),
    ENVIRONMENTUPDATE("PassThroughPacket.class", Packet.Direction.NOT_USED),
    ENTITYINTERACTRESULT("PassThroughPacket.class", Packet.Direction.NOT_USED),
    MODIFYTILELIST("PassThroughPacket.class", Packet.Direction.NOT_USED),
    DAMAGETILE("PassThroughPacket.class", Packet.Direction.NOT_USED),
    DAMAGETILEGROUP("PassThroughPacket.class", Packet.Direction.STARBOUND_SERVER),
    REQUESTDROP("PassThroughPacket.class", Packet.Direction.NOT_USED),
    SPAWNENTITY("PassThroughPacket.class", Packet.Direction.NOT_USED),
    ENTITYINTERACT("PassThroughPacket.class", Packet.Direction.NOT_USED),
    CONNECTWIRE("PassThroughPacket.class", Packet.Direction.NOT_USED),
    DISCONNECTALLWIRES("PassThroughPacket.class", Packet.Direction.NOT_USED),
    OPENCONTAINER("PassThroughPacket.class", Packet.Direction.NOT_USED),
    CLOSECONTAINER("PassThroughPacket.class", Packet.Direction.NOT_USED),
    SWAPCONTAINER("PassThroughPacket.class", Packet.Direction.NOT_USED),
    ITEMAPPLYCONTAINER("PassThroughPacket.class", Packet.Direction.NOT_USED),
    STARTCRAFTINGCONTAINER("PassThroughPacket.class", Packet.Direction.NOT_USED),
    STOPCRAFTINGCONTAINER("PassThroughPacket.class", Packet.Direction.NOT_USED),
    BURNCONTAINER("PassThroughPacket.class", Packet.Direction.NOT_USED),
    CLEARCONTAINER("PassThroughPacket.class", Packet.Direction.NOT_USED),
    WORLDCLIENTSTATEUPDATE("PassThroughPacket.class", Packet.Direction.NOT_USED),
    ENTITYCREATE("PassThroughPacket.class", Packet.Direction.NOT_USED),
    ENTITYUPDATE("PassThroughPacket.class", Packet.Direction.NOT_USED),
    ENTITYDESTROY("PassThroughPacket.class", Packet.Direction.NOT_USED),
    DAMAGENOTIFICATION("PassThroughPacket.class", Packet.Direction.NOT_USED),
    STATUSEFFECTREQUEST("PassThroughPacket.class", Packet.Direction.NOT_USED),
    UPDATEWORLDPROPERTIES("PassThroughPacket.class", Packet.Direction.NOT_USED),
    HEARTBEAT("PassThroughPacket.class", Packet.Direction.BIDIRECTIONAL);

    private String classString;
    private Packet.Direction direction;
    private static HashMap<Byte, Class> packetClasses = setPrePacketCache();

    Packets(String classString, Packet.Direction direction) {
        this.classString = classString;
        this.direction = direction;
    }

    public byte getPacketId() {
        return (byte) this.ordinal();
    }

    public String getClassString() {
        return classString;
    }

    public void setClassString(String classString) {
        this.classString = classString;
    }

    public Packet.Direction getDirection() {
        return direction;
    }

    public void setDirection(Packet.Direction direction) {
        this.direction = direction;
    }

    /**
     * Recommended: For internal StarNub usage.
     * <p/>
     * Uses: This method will scan the class path starbounddata.packets and this enum and match class paths with packet types
     * to be pre loaded for incoming connections
     *
     */
    public static HashMap<Byte, Class> setPrePacketCache(){
        Reflections reflections = new Reflections("starbounddata.packets");
        Set<Class<? extends Packet>> allClasses = reflections.getSubTypesOf(Packet.class);
        HashMap<String, Class> packetPaths = new HashMap<String, Class>();
        for (Class c : allClasses) {
            String className = c.getName();
            packetPaths.put(className.substring(className.lastIndexOf(".") + 1), c);
        }
        HashMap<Byte, Class> packetCacheToSet = new HashMap<>();
        for (Packets packet : Packets.values()) {
            String packetName = packet.classString.substring(0, packet.classString.lastIndexOf("."));
            Class packetPath = packetPaths.get(packetName);
            packetCacheToSet.put((byte) packet.ordinal(), packetPath);
        }
        return packetCacheToSet;
    }

    /**
     * Recommended: For internal StarNub usage.
     * <p/>
     * Uses: This will create new packets from the packet cache and insert them into a HashMap to be used in
     * player connections.
     *
     * @param SENDER_CTX ChannelHandlerContext the sender ChannelHandlerContext to add to the packet being created
     * @param DESTINATION_CTX ChannelHandlerContext the destination ChannelHandlerContext to add to the packet being created
     * @return HashMap the packet cache to be used in packet routing
     */
    public static HashMap<Byte, Packet> getPacketCache(Packet.Direction DIRECTION, ChannelHandlerContext SENDER_CTX, ChannelHandlerContext DESTINATION_CTX) {
        HashMap<Byte, Packet> packetPool = new HashMap<>();
        for (int i = 0; i < packetClasses.size(); i++) {
            Byte packetID = (byte) i;
            Class packetClass = packetClasses.get(packetID);
            Constructor constructor;
            Packet packet = null;
            try {
                constructor = packetClass.getConstructor(new Class[]{ChannelHandlerContext.class, ChannelHandlerContext.class});
                packet = (Packet) constructor.newInstance(new Object[]{SENDER_CTX, DESTINATION_CTX});
            } catch (NoSuchMethodException | NullPointerException e){
                try {
                    constructor = packetClass.getConstructor(new Class[]{Byte.class, ChannelHandlerContext.class, ChannelHandlerContext.class});
                    packet = (Packet) constructor.newInstance(new Object[]{packetID, SENDER_CTX, DESTINATION_CTX});
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e1) {
                    e1.printStackTrace();
                }
            } catch (InvocationTargetException | InstantiationException |IllegalAccessException e) {
                e.printStackTrace();
            }
            packetPool.put(packetID, packet);
        }
        return packetPool;
    }
}