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

package org.starnub.starbounddata.packets;

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
    FLYSHIP("FlyShipPacket.class", Packet.Direction.BIDIRECTIONAL), //DEBUG - Direction and test warping to planets that do not exist
    CHATSEND("ChatSendPacket.class", Packet.Direction.TO_STARBOUND_SERVER),
    CELESTIALREQUEST("CelestialRequestPacket.class", Packet.Direction.TO_STARBOUND_SERVER),
    CLIENTCONTEXTUPDATE("ClientContextUpdatePacket.class", Packet.Direction.BIDIRECTIONAL), //DEBUG - All data needs debugged
    WORLDSTART("WorldStartPacket.class", Packet.Direction.BIDIRECTIONAL), //DEBUG - ALL DATA
    WORLDSTOP("WorldStopPacket.class", Packet.Direction.TO_STARBOUND_CLIENT),
    CENTRALSTRUCTUREUPDATE("CentralStructureUpdatePacket.class", Packet.Direction.TO_STARBOUND_CLIENT),
    TILEARRAYUPDATE("", Packet.Direction.NOT_USED), // NetTile - Will be done once documents are corrected - STARBOUND DOCUMENTS NOT CORRECT
    TILEUPDATE("", Packet.Direction.NOT_USED), // NetTile - Will be done once documents are corrected - STARBOUND DOCUMENTS NOT CORRECT
    TILELIQUIDUPDATE("TileLiquidUpdatePacket.class", Packet.Direction.BIDIRECTIONAL), //DEBUG - STARBOUND DOCUMENTS NOT CORRECT
    TILEDAMAGEUPDATE("", Packet.Direction.NOT_USED),
    TILEMODIFICATIONFAILURE("", Packet.Direction.NOT_USED),
    GIVEITEM("GiveItemPacket.class", Packet.Direction.BIDIRECTIONAL), //DEBUG - item descriptor
    SWAPINCONTAINERRESULT("", Packet.Direction.NOT_USED), //Item Descriptor issues
    ENVIRONMENTUPDATE("", Packet.Direction.NOT_USED),
    ENTITYINTERACTRESULT("EntityInteractResultPacket.class", Packet.Direction.TO_STARBOUND_CLIENT), //DEBUG - STARBOUND DOCUMENTS NOT CORRECT
    UPDATETILEPROTECTION("UpdateTileProtectionPacket.class", Packet.Direction.BIDIRECTIONAL),//DEBUG DIRECTION AND DATA
    MODIFYTILELIST("", Packet.Direction.NOT_USED),
    DAMAGETILEGROUP("DamageTileGroupPacket.class", Packet.Direction.TO_STARBOUND_SERVER),
    COLLECTLIQUID("CollectLiquidPacket.class", Packet.Direction.TO_STARBOUND_SERVER),
    REQUESTDROP("RequestDropPacket.class", Packet.Direction.TO_STARBOUND_SERVER),
    SPAWNENTITY("SpawnEntityPacket.class", Packet.Direction.BIDIRECTIONAL), //DEBUG DIRECTION AND DATA
    ENTITYINTERACT("EntityInteractPacket.class", Packet.Direction.BIDIRECTIONAL), //DEBUG DIRECTION
    CONNECTWIRE("ConnectWirePacket.class", Packet.Direction.BIDIRECTIONAL), //DEBUG Direction and Data
    DISCONNECTALLWIRES("DisconnectAllWiresPacket.class", Packet.Direction.BIDIRECTIONAL), //DEBUG Direction and Data
    OPENCONTAINER("OpenContainerPacket.class", Packet.Direction.TO_STARBOUND_SERVER),
    CLOSECONTAINER("CloseContainerPacket.class", Packet.Direction.TO_STARBOUND_SERVER),
    SWAPINCONTAINER("", Packet.Direction.NOT_USED), //Item Descriptor issues
    ITEMAPPLYINCONTAINER("", Packet.Direction.NOT_USED), //Item Descriptor issues
    STARTCRAFTINGINCONTAINER("StartCraftingInContainerPacket.class", Packet.Direction.TO_STARBOUND_SERVER),
    STOPCRAFTINGINCONTAINER("StopCraftingInContainerPacket.class", Packet.Direction.BIDIRECTIONAL), //DEBUG Direction and Data
    BURNCONTAINER("BurnContainerPacket.class", Packet.Direction.BIDIRECTIONAL), //DEBUG Direction and Data
    CLEARCONTAINER("ClearContainerPacket.class", Packet.Direction.TO_STARBOUND_SERVER),
    WORLDCLIENTSTATEUPDATE("", Packet.Direction.NOT_USED),
    ENTITYCREATE("EntityCreatePacket.class", Packet.Direction.BIDIRECTIONAL), //DEBUG Data Issues // BROKEN BECAUSE SOME ARE NOT BYTE ARRAYS
    ENTITYUPDATE("EntityUpdatePacket.class", Packet.Direction.BIDIRECTIONAL), // DEBUG Delta and direction
    ENTITYDESTROY("EntityDestroyPacket.class", Packet.Direction.BIDIRECTIONAL),
    HITREQUEST("HitRequestPacket.class", Packet.Direction.TO_STARBOUND_SERVER),
    DAMAGEREQUEST("DamageRequestPacket.class", Packet.Direction.NOT_USED), //DEBUG - Data Issues PROBABLE DOUBLE
    DAMAGENOTIFICATION("DamageNotificationPacket.class", Packet.Direction.BIDIRECTIONAL), //DEBUG - Data Issues PROBABLE DOUBLE
    CALLSCRIPTEDENTITY("CallScriptedEntityPacket.class", Packet.Direction.BIDIRECTIONAL), //DEBUG - ALL DATA
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
     * Recommended: For internal use with StarNub Player Sessions
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
            if (!packet.classString.isEmpty() && !(packet.classString.length() < 6) && packet.direction != Packet.Direction.NOT_USED) {
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