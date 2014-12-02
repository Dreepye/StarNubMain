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
import java.util.Map;
import java.util.Set;

/**
 * Represents all of the packets and methods to generate a packet data for StarNub and Plugins
 * <p>
 * Notes:
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public enum Packets {
    PROTOCOLVERSION("ProtocolVersionPacket.class", Packet.Direction.TO_STARBOUND_CLIENT),
    CONNECTIONRESPONSE("ConnectResponsePacket.class", Packet.Direction.TO_STARBOUND_CLIENT),
    DISCONNECTRESPONSE("ServerDisconnectPacket.class", Packet.Direction.TO_STARBOUND_CLIENT),
    HANDSHAKECHALLENGE("", Packet.Direction.NOT_USED),
    CHATRECEIVED("ChatReceivePacket.class", Packet.Direction.TO_STARBOUND_CLIENT),
    UNIVERSETIMEUPDATE("UniverseTimeUpdatePacket.class", Packet.Direction.TO_STARBOUND_CLIENT),
    CELESTIALRESPONSE("", Packet.Direction.NOT_USED),
    CLIENTCONNECT("ClientConnectPacket.class", Packet.Direction.TO_STARBOUND_SERVER),
    CLIENTDISCONNECT("ClientDisconnectRequestPacket.class", Packet.Direction.TO_STARBOUND_SERVER),
    HANDSHAKERESPONSE("", Packet.Direction.NOT_USED),
    WARPCOMMAND("", Packet.Direction.NOT_USED),
    CHATSENT("ChatSendPacket.class", Packet.Direction.TO_STARBOUND_SERVER),
    CELESTIALREQUEST("", Packet.Direction.NOT_USED),
    CLIENTCONTEXTUPDATE("", Packet.Direction.NOT_USED),
    WORLDSTART("", Packet.Direction.NOT_USED),
    WORLDSTOP("", Packet.Direction.NOT_USED),
    TILEARRAYUPDATE("", Packet.Direction.NOT_USED),
    TILEUPDATE("", Packet.Direction.NOT_USED),
    TILELIQUIDUPDATE("", Packet.Direction.NOT_USED),
    TILEDAMAGEUPDATE("", Packet.Direction.NOT_USED),
    TILEMODIFICATIONFAILURE("", Packet.Direction.NOT_USED),
    GIVEITEM("", Packet.Direction.NOT_USED),
    SWAPCONTAINERRESULT("", Packet.Direction.NOT_USED),
    ENVIRONMENTUPDATE("", Packet.Direction.NOT_USED),
    ENTITYINTERACTRESULT("", Packet.Direction.NOT_USED),
    MODIFYTILELIST("", Packet.Direction.NOT_USED),
    DAMAGETILE("", Packet.Direction.NOT_USED),
    DAMAGETILEGROUP("DamageTileGroupPacket.class", Packet.Direction.TO_STARBOUND_SERVER),
    REQUESTDROP("", Packet.Direction.NOT_USED),
    SPAWNENTITY("", Packet.Direction.NOT_USED),
    ENTITYINTERACT("", Packet.Direction.NOT_USED),
    CONNECTWIRE("", Packet.Direction.NOT_USED),
    DISCONNECTALLWIRES("", Packet.Direction.NOT_USED),
    OPENCONTAINER("", Packet.Direction.NOT_USED),
    CLOSECONTAINER("", Packet.Direction.NOT_USED),
    SWAPCONTAINER("", Packet.Direction.NOT_USED),
    ITEMAPPLYCONTAINER("", Packet.Direction.NOT_USED),
    STARTCRAFTINGCONTAINER("", Packet.Direction.NOT_USED),
    STOPCRAFTINGCONTAINER("", Packet.Direction.NOT_USED),
    BURNCONTAINER("", Packet.Direction.NOT_USED),
    CLEARCONTAINER("", Packet.Direction.NOT_USED),
    WORLDCLIENTSTATEUPDATE("", Packet.Direction.NOT_USED),
    ENTITYCREATE("", Packet.Direction.NOT_USED),
    ENTITYUPDATE("", Packet.Direction.NOT_USED),
    ENTITYDESTROY("", Packet.Direction.NOT_USED),
    DAMAGENOTIFICATION("", Packet.Direction.NOT_USED),
    STATUSEFFECTREQUEST("", Packet.Direction.NOT_USED),
    UPDATEWORLDPROPERTIES("", Packet.Direction.NOT_USED),
    HEARTBEAT("HeartbeatPacket.class", Packet.Direction.BIDIRECTIONAL);

    private static HashMap<Packets, Class> packetClasses = setPrePacketCache();
    private String classString;
    private Packet.Direction direction;

    Packets(String classString, Packet.Direction direction) {
        this.classString = classString;
        this.direction = direction;
    }

    /**
     * Recommended: For internal StarNub usage.
     * <p>
     * Uses: This method will scan the class path starbounddata.packets and this enum and match class paths with packet types
     * to be pre loaded for incoming connections
     */
    public static HashMap<Packets, Class> setPrePacketCache() {
        Reflections reflections = new Reflections("starbounddata.packets");
        Set<Class<? extends Packet>> allClasses = reflections.getSubTypesOf(Packet.class);
        HashMap<String, Class> packetPaths = new HashMap<String, Class>();
        for (Class c : allClasses) {
            String className = c.getName();
            packetPaths.put(className.substring(className.lastIndexOf(".") + 1), c);
        }
        HashMap<Packets, Class> packetCacheToSet = new HashMap<>();
        for (Packets packet : Packets.values()) {
            if (!packet.classString.isEmpty() || !(packet.classString.length() < 4) || packet.direction != Packet.Direction.NOT_USED) {
                String packetName = packet.classString.substring(0, packet.classString.lastIndexOf("."));
                Class packetPath = packetPaths.get(packetName);
                packetCacheToSet.put(packet, packetPath);
            }
        }
        return packetCacheToSet;
    }

    /**
     * Recommended: For internal StarNub usage.
     * <p>
     * Uses: This will create new packets from the packet cache and insert them into a HashMap to be used in
     * player connections.
     *
     *
     * @param DIRECTION       Direction representing where the packet flows
     * @param SENDER_CTX      ChannelHandlerContext the sender ChannelHandlerContext to add to the packet being created
     * @param DESTINATION_CTX ChannelHandlerContext the destination ChannelHandlerContext to add to the packet being created
     * @return HashMap the packet cache to be used in packet routing
     */
    public static HashMap<Byte, Packet> getPacketCache(Packet.Direction DIRECTION, ChannelHandlerContext SENDER_CTX, ChannelHandlerContext DESTINATION_CTX) {
        HashMap<Byte, Packet> packetPool = new HashMap<>();
        for (Map.Entry<Packets, Class> packetsClassEntry : packetClasses.entrySet()) {
            Class packetClass = packetsClassEntry.getValue();
            Packets packet = packetsClassEntry.getKey();
            Constructor constructor;
            Packet packetToConstruct = null;
            if (packet.direction != DIRECTION) {
                DIRECTION = DIRECTION == Packet.Direction.TO_STARBOUND_CLIENT ? Packet.Direction.TO_STARBOUND_SERVER : Packet.Direction.TO_STARBOUND_CLIENT;
                try {
                    constructor = packetClass.getConstructor(new Class[]{Packet.Direction.class, ChannelHandlerContext.class, ChannelHandlerContext.class});
                    packetToConstruct = (Packet) constructor.newInstance(new Object[]{DIRECTION, SENDER_CTX, DESTINATION_CTX});
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
                packetPool.put(packet.getPacketId(), packetToConstruct);
            }
        }
        return packetPool;
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

    @Override
    public String toString() {
        return "Packets{" +
                "classString='" + classString + '\'' +
                ", direction=" + direction +
                "} " + super.toString();
    }
}