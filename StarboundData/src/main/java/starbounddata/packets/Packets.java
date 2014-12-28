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

import org.reflections.Reflections;

import java.util.HashMap;
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
    SERVERDISCONNECT("ServerDisconnectPacket.class", Packet.Direction.TO_STARBOUND_CLIENT),
    CONNECTRESPONSE("ConnectResponsePacket.class", Packet.Direction.TO_STARBOUND_CLIENT),
    HANDSHAKECHALLENGE("HandshakeChallengePacket.class", Packet.Direction.TO_STARBOUND_CLIENT),
    CHATRECEIVE("ChatReceivePacket.class", Packet.Direction.TO_STARBOUND_CLIENT),
    UNIVERSETIMEUPDATE("UniverseTimeUpdatePacket.class", Packet.Direction.TO_STARBOUND_CLIENT),
    CELESTIALRESPONSE("CelestialResponsePacket.class", Packet.Direction.TO_STARBOUND_CLIENT), //DEBUG - ALL DATA, TWO BYE ANOMALY and UNDOCUMENTED WORLD VISITABLE PTR
    CLIENTCONNECT("ClientConnectPacket.class", Packet.Direction.TO_STARBOUND_SERVER),
    CLIENTDISCONNECTREQUEST("ClientDisconnectRequestPacket.class", Packet.Direction.TO_STARBOUND_SERVER),
    HANDSHAKERESPONSE("HandshakeResponsePacket.class", Packet.Direction.TO_STARBOUND_SERVER),
    PLAYERWARP("PlayerWarp.class", Packet.Direction.BIDIRECTIONAL), //DEBUG - LocationsIds - Remaining - UNIQUE_WORLD & CELESTIAL_WORLD
    FLYSHIP("FlyShipPacket.class", Packet.Direction.BIDIRECTIONAL), //DEBUG - Direction and warping to planets that do not exist
    CHATSEND("ChatSendPacket.class", Packet.Direction.TO_STARBOUND_SERVER),
    CELESTIALREQUEST("CelestialRequestPacket.class", Packet.Direction.TO_STARBOUND_SERVER),
    CLIENTCONTEXTUPDATE("ClientContextUpdatePacket.class", Packet.Direction.BIDIRECTIONAL), //DEBUG - All data needs debugged
    WORLDSTART("WorldStartPacket.class", Packet.Direction.BIDIRECTIONAL), //DEBUG - ALL DATA
    WORLDSTOP("WorldStopPacket.class", Packet.Direction.TO_STARBOUND_CLIENT),
    CENTRALSTRUCTUREUPDATE("", Packet.Direction.NOT_USED),
    TILEARRAYUPDATE("", Packet.Direction.NOT_USED),
    TILEUPDATE("", Packet.Direction.NOT_USED),
    TILELIQUIDUPDATE("", Packet.Direction.NOT_USED),
    TILEDAMAGEUPDATE("", Packet.Direction.NOT_USED),
    TILEMODIFICATIONFAILURE("", Packet.Direction.NOT_USED),
    GIVEITEM("GiveItemPacket.class", Packet.Direction.BIDIRECTIONAL), //DEBUG - PROB ONLY CLIENT
    SWAPINCONTAINERRESULT("", Packet.Direction.NOT_USED),
    ENVIRONMENTUPDATE("", Packet.Direction.NOT_USED),
    ENTITYINTERACTRESULT("", Packet.Direction.NOT_USED),
    UPDATETILEPROTECTION("UpdateTileProtectionPacket.class", Packet.Direction.BIDIRECTIONAL),//DEBUG DIRECTION AND DATA
    MODIFYTILELIST("", Packet.Direction.NOT_USED),
    DAMAGETILEGROUP("DamageTileGroupPacket.class", Packet.Direction.TO_STARBOUND_SERVER),
    COLLECTLIQUID("", Packet.Direction.NOT_USED),
    REQUESTDROP("", Packet.Direction.NOT_USED),
    SPAWNENTITY("", Packet.Direction.NOT_USED),
    ENTITYINTERACT("EntityInteractPacket.class", Packet.Direction.BIDIRECTIONAL), //DEBUG DIRECTION
    CONNECTWIRE("", Packet.Direction.NOT_USED),
    DISCONNECTALLWIRES("", Packet.Direction.NOT_USED),
    OPENCONTAINER("OpenContainerPacket.class", Packet.Direction.TO_STARBOUND_SERVER),
    CLOSECONTAINER("", Packet.Direction.NOT_USED),
    SWAPINCONTAINER("", Packet.Direction.NOT_USED),
    ITEMAPPLYINCONTAINER("", Packet.Direction.NOT_USED),
    STARTCRAFTINGINCONTAINER("", Packet.Direction.NOT_USED),
    STOPCRAFTINGINCONTAINER("", Packet.Direction.NOT_USED),
    BURNCONTAINER("", Packet.Direction.NOT_USED),
    CLEARCONTAINER("", Packet.Direction.NOT_USED),
    WORLDCLIENTSTATEUPDATE("", Packet.Direction.NOT_USED),
    ENTITYCREATE("EntityCreatePacket.class", Packet.Direction.BIDIRECTIONAL), //DEBUG Data Issues
    ENTITYUPDATE("EntityUpdatePacket.class", Packet.Direction.BIDIRECTIONAL), // DEBUG Delta and direction
    ENTITYDESTROY("EntityDestroyPacket.class", Packet.Direction.BIDIRECTIONAL),
    HITREQUEST("HitRequestPacket.class", Packet.Direction.TO_STARBOUND_SERVER),
    DAMAGEREQUEST("DamageRequestPacket.class", Packet.Direction.NOT_USED), //DEBUG - Data Issues PROBABLE DOUBLE
    DAMAGENOTIFICATION("DamageNotificationPacket.class", Packet.Direction.BIDIRECTIONAL), //DEBUG - Data Issues PROBABLE DOUBLE
    CALLSCRIPTEDENTITY("", Packet.Direction.NOT_USED),
    UPDATEWORLDPROPERTIES("", Packet.Direction.NOT_USED),
    HEARTBEAT("HeartbeatPacket.class", Packet.Direction.BIDIRECTIONAL);

    private static HashMap<Packets, Class> packetClasses = setPrePacketCache();
    private String classString;
    private Packet.Direction direction;

    Packets(String classString, Packet.Direction direction) {
        this.classString = classString;
        this.direction = direction;
    }

    public static HashMap<Packets, Class> getPacketClasses() {
        return packetClasses;
    }

    public static void setPacketClasses(HashMap<Packets, Class> packetClasses) {
        Packets.packetClasses = packetClasses;
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

    public byte getPacketId() {
        return (byte) this.ordinal();
    }

    /**
     * Recommended: For connections StarNub usage.
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
            if (!packet.classString.isEmpty() && !(packet.classString.length() < 4) && packet.direction != Packet.Direction.NOT_USED) {
                String packetName = packet.classString.substring(0, packet.classString.lastIndexOf("."));
                Class packetPath = packetPaths.get(packetName);
                packetCacheToSet.put(packet, packetPath);
            }
        }
        return packetCacheToSet;
    }

    public static Packets fromString(String string){
        for(Packets p : Packets.values()){
            String packetClassString = p.getClassString();
            if (packetClassString.equalsIgnoreCase(string)){
                return p;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Packets{" +
                "classString='" + classString + '\'' +
                ", direction=" + direction +
                "} " + super.toString();
    }
}