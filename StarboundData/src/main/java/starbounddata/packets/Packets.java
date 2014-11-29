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
    PROTOCOLVERSION("PassThroughPacket.class", false),
    CONNECTIONRESPONSE("PassThroughPacket.class", false),
    DISCONNECTRESPONSE("PassThroughPacket.class", false),
    HANDSHAKECHALLENGE("PassThroughPacket.class", false),
    CHATRECEIVED("PassThroughPacket.class", false),
    UNIVERSETIMEUPDATE("PassThroughPacket.class", false),
    CELESTIALRESPONSE("PassThroughPacket.class", false),
    CLIENTCONNECT("PassThroughPacket.class", false),
    CLIENTDISCONNECT("PassThroughPacket.class", false),
    HANDSHAKERESPONSE("PassThroughPacket.class", false),
    WARPCOMMAND("PassThroughPacket.class", false),
    CHATSENT("PassThroughPacket.class", false),
    CELESTIALREQUEST("PassThroughPacket.class", false),
    CLIENTCONTEXTUPDATE("PassThroughPacket.class", false),
    WORLDSTART("PassThroughPacket.class", false),
    WORLDSTOP("PassThroughPacket.class", false),
    TILEARRAYUPDATE("PassThroughPacket.class", false),
    TILEUPDATE("PassThroughPacket.class", false),
    TILELIQUIDUPDATE("PassThroughPacket.class", false),
    TILEDAMAGEUPDATE("PassThroughPacket.class", false),
    TILEMODIFICATIONFAILURE("PassThroughPacket.class", false),
    GIVEITEM("PassThroughPacket.class", false),
    SWAPCONTAINERRESULT("PassThroughPacket.class", false),
    ENVIRONMENTUPDATE("PassThroughPacket.class", false),
    ENTITYINTERACTRESULT("PassThroughPacket.class", false),
    MODIFYTILELIST("PassThroughPacket.class", false),
    DAMAGETILE("PassThroughPacket.class", false),
    DAMAGETILEGROUP("PassThroughPacket.class", false),
    REQUESTDROP("PassThroughPacket.class", false),
    SPAWNENTITY("PassThroughPacket.class", false),
    ENTITYINTERACT("PassThroughPacket.class", false),
    CONNECTWIRE("PassThroughPacket.class", false),
    DISCONNECTALLWIRES("PassThroughPacket.class", false),
    OPENCONTAINER("PassThroughPacket.class", false),
    CLOSECONTAINER("PassThroughPacket.class", false),
    SWAPCONTAINER("PassThroughPacket.class", false),
    ITEMAPPLYCONTAINER("PassThroughPacket.class", false),
    STARTCRAFTINGCONTAINER("PassThroughPacket.class", false),
    STOPCRAFTINGCONTAINER("PassThroughPacket.class", false),
    BURNCONTAINER("PassThroughPacket.class", false),
    CLEARCONTAINER("PassThroughPacket.class", false),
    WORLDCLIENTSTATEUPDATE("PassThroughPacket.class", false),
    ENTITYCREATE("PassThroughPacket.class", false),
    ENTITYUPDATE("PassThroughPacket.class", false),
    ENTITYDESTROY("PassThroughPacket.class", false),
    DAMAGENOTIFICATION("PassThroughPacket.class", false),
    STATUSEFFECTREQUEST("PassThroughPacket.class", false),
    UPDATEWORLDPROPERTIES("PassThroughPacket.class", false),
    HEARTBEAT("PassThroughPacket.class", false);

    private String classString;
    private boolean debug;
    private static HashMap<Byte, Class> packetClasses = setPrePacketCache();

    Packets(String classString, boolean debug) {
        this.classString = classString;
        this.debug = debug;
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

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
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
    public static HashMap<Byte, Packet> getPacketCache(ChannelHandlerContext SENDER_CTX, ChannelHandlerContext DESTINATION_CTX) {
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