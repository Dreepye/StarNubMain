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

    //        @Getter
//        private static HashMap<Integer, String> packetClassPaths = buildClassPaths();
//
    Packets(String classString, boolean debug) {
        this.classString = classString;
        this.debug = debug;
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

    //        private static HashMap<Integer, String> buildClassPaths(){
//
//
//
//        }
//
//
//TODO
//        public static HashMap<Byte, Packet> packetCache(ChannelHandlerContext SENDER_CTX, ChannelHandlerContext DESTINATION_CTX){
//            for (Packets p : packets.)
//                Reflections reflections = new Reflections("org.starnub.starbounddata.packets.starbounddata.packets.server.starbounddata.packets");
//                Set<Class<? extends Packet>> allClasses =
//                        reflections.getSubTypesOf(Packet.class);
//            for (Class c : allClasses){
//                String className = c.getName();
//                if (className.substring(className.lastIndexOf(".")+1).equals((String) list.get(0))){
//                    try {
//                                /* Add to known packet */
//                        Class<? extends Packet> packetClass = (Class.forName(className).asSubclass(Packet.class));
//
//
//                Class myClass = Class.forName("MyClass");
//
//        }
//
//        private static Packet packetInitializer(byte PACKET_ID, ChannelHandlerContext SENDER_CTX, ChannelHandlerContext DESTINATION_CTX){
//
//        }
//
    public static Packet getPacket(ChannelHandlerContext DESTINATION_CTX) {
        Packet packet = null;
        return packet;
    }

    public byte getPacketId() {
        return (byte) this.ordinal();
    }

//
//
//        //HashMap Generator
//        //Packet Creator - remove ids from packets hard coding
//        //Value returns
//        //Debugs will be passed back in a hashset
//
}