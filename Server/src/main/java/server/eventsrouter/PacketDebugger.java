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

package server.eventsrouter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.codehome.utilities.files.YamlLoader;
import org.reflections.Reflections;
import server.StarNub;
import server.eventsrouter.handlers.PacketEventHandler;
import server.server.packets.Packet;
import server.server.packets.StarboundBufferReader;
import server.server.packets.chat.ChatReceivePacket;
import server.server.packets.chat.ChatSendPacket;
import server.server.packets.global.PlayerWarpPacket;
import server.server.packets.server.ProtocolVersionPacket;
import server.server.packets.server.UniverseTimeUpdatePacket;
import server.server.packets.tile.DamageTileGroupPacket;
import server.server.packets.world.WorldStartPacket;
import server.server.packets.world.WorldStopPacket;
import starbounddata.vectors.Vec2I;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
* Represents the Known Starbound Packets.
* <p>
* This enum holds the set and get methods for known starbounddata.packets.
* <p>
* @author Daniel (Underbalanced) (www.StarNub.org)
* @since 1.0
*/
public enum PacketDebugger {
    INSTANCE;

    /**
     * This method sets the LinkedList and only works when the Starbound starbounddata.packets.starbounddata.packets.server is not online
     * due to the volatility of playing with this while needing them for player connections. This
     * method will also set up starbounddata.packets.Packet Debugging by invoking eventsrouter located at
     * this debugging variables are in the
     * resource file "starbound_packet_types.yml"
     */
    @SuppressWarnings("unchecked")
    public synchronized void setPacketDebugging() {
        Map<String, Object> implementedPackets = new YamlLoader().resourceYamlLoader("starbound/starbound_packet_types.yml");
        Reflections reflections = new Reflections("org.starnub.starbounddata.packets.starbounddata.packets.server.starbounddata.packets");
        Set<Class<? extends Packet>> allClasses = reflections.getSubTypesOf(Packet.class);
        for (Object value : implementedPackets.values()) {
            ArrayList<Object> list = (ArrayList<Object>) value;
            for (Class c : allClasses) {
                /* Debugging */
                Class<?> packetDebuggingClass = null;
                try {
                    packetDebuggingClass = Class.forName("org.PacketDebugger");
                } catch (ClassNotFoundException e) {
                    StarNub.getLogger().cErrPrint("StarNub", "Could not load starbounddata.packets.Packet Event Debugging Class.");
                }
                try {
                    if (((String) list.get(2)).equalsIgnoreCase("debug") && packetDebuggingClass != null) {
                        try {
                            Method method = packetDebuggingClass.getDeclaredMethod((String) list.get(0));
                            method.invoke(PacketDebugger.INSTANCE);
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            StarNub.getLogger().cErrPrint("StarNub", "Could not load starbounddata.packets.Packet Event Debugging Method.");
                            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    /* Do nothing if no variable to debug is set */
                }
            }
        }
    }


    /**
     * Used in debugging {@link server.server.packets.chat.ChatReceivePacket}
     */
    public void ChatReceivePacket() {
        StarNub.getPacketEventRouter().registerEventSubscription("StarNub", ChatReceivePacket.class, new PacketEventHandler() {
            @Override
            public Packet onEvent(Packet eventData) {
                ChatReceivePacket chaRecPacket = (ChatReceivePacket) eventData;
                StarNub.getLogger().cDebPrint("StarNub", "PACKET ChatReceivePacket Data: \n" +
                        chaRecPacket.getChannel() + "\n" +
                        chaRecPacket.getWorld() + "\n" +
                        chaRecPacket.getClientId() + "\n" +
                        chaRecPacket.getName() + "\n" +
                        chaRecPacket.getMessage());
                return eventData;
            }
        });
    }

    /**
     * Used in debugging {@link server.server.packets.chat.ChatSendPacket}
     */
    public void ChatSendPacket() {
        StarNub.getPacketEventRouter().registerEventSubscription("StarNub", ChatSendPacket.class, new PacketEventHandler() {
            @Override
            public Packet onEvent(Packet eventData) {
                ChatSendPacket chaSenPacket = (ChatSendPacket) eventData;
                StarNub.getLogger().cDebPrint("StarNub", "PACKET ChatSendPacket Data: \n" +
                        chaSenPacket.getChannel() + "\n" +
                        chaSenPacket.getMessage());
                return eventData;
            }
        });
    }

    /**
     * Used in debugging {@link server.server.packets.connection.ClientConnectPacket}
     */
    public void ClientConnectPacket() {
        StarNub.getPacketEventRouter().registerEventSubscription("StarNub", ClientConnectPacket.class, new PacketEventHandler() {
            @Override
            public Packet onEvent(Packet eventData) {
                ClientConnectPacket clieConnPacket = (ClientConnectPacket) eventData;
                StarNub.getLogger().cDebPrint("StarNub", "PACKET ClientConnectPacket Data: \n"
                        //                          + clieConnPacket.getAssetDigest() + "\n"
                        + clieConnPacket.getClaim() + "\n"
                        + clieConnPacket.getUUID() + "\n"
                        + clieConnPacket.getPlayerName() + "\n"
                        + clieConnPacket.getSpecies() + "\n"
                        //                          + new String(clieConnPacket.getShipWorld()) + "\n"
                        + clieConnPacket.getAccount());
                return eventData;
            }
        });
    }

    /**
     * Used in debugging {@link server.server.packets.connection.ClientDisconnectRequestPacket}
     */
    public void ClientDisconnectRequestPacket() {
        StarNub.getPacketEventRouter().registerEventSubscription("StarNub", ClientDisconnectRequestPacket.class, new PacketEventHandler() {
            @Override
            public Packet onEvent(Packet eventData) {
                ClientDisconnectRequestPacket disConPacket = (ClientDisconnectRequestPacket) eventData;
                StarNub.getLogger().cDebPrint("StarNub", "PACKET ClientDisconnectRequestPacket Data: \n" +
                        disConPacket.getEmptyByte());
                return eventData;
            }
        });
    }

    /**
     * Used in debugging {@link server.server.packets.connection.ConnectResponsePacket}
     */
    public void ConnectResponsePacket() {
        StarNub.getPacketEventRouter().registerEventSubscription("StarNub", ConnectResponsePacket.class, new PacketEventHandler() {
            @Override
            public Packet onEvent(Packet eventData) {
                ConnectResponsePacket connResPacket = (ConnectResponsePacket) eventData;
                StarNub.getLogger().cDebPrint("StarNub", "PACKET ConnectionResponse Data: \n"
                        + connResPacket.isSuccess() + "\n"
                        + connResPacket.getClientId() + "\n"
                        + connResPacket.getRejectionReason() + "\n"
                        + connResPacket.isCelestialInformation() + "\n"
                        + connResPacket.getOrbitalLevels() + "\n"
                        + connResPacket.getChunkSize() + "\n"
                        + connResPacket.getXYCoordinateMin() + "\n"
                        + connResPacket.getXYCoordinateMax() + "\n"
                        + connResPacket.getZCoordinateMin() + "\n"
                        + connResPacket.getZCoordinateMax() + "\n"
                        + connResPacket.getNumberofSectors() + "\n"
                        + connResPacket.getSectorId() + "\n"
                        + connResPacket.getSectorName() + "\n"
                        + connResPacket.getSectorSeed() + "\n"
                        + connResPacket.getSectorPrefix() + "\n");
        /*              + connResPacket.getParameters() + "\n"    Needs to be formatted - TODO API    */
        /*              + connResPacket.getSectorConfig()    Needs to be formatted - TODO API    */
                return eventData;
            }
        });
    }


    /**
     * Used in debugging {@link server.server.packets.tile.DamageTileGroupPacket}
     */
    public void DamageTileGroupPacket() {
        StarNub.getPacketEventRouter().registerEventSubscription("StarNub", DamageTileGroupPacket.class, new PacketEventHandler() {
            @Override
            public Packet onEvent(Packet eventData) {
                DamageTileGroupPacket damTileGroPacket = (DamageTileGroupPacket) eventData;
                String tiles = "";
                int count = 0;
                for (Vec2I vec2I : damTileGroPacket.getTilePositions()) {
                    count++;
                    tiles = tiles + "Tile# " + count + ": X-" + vec2I.getX() + ", Y-" + vec2I.getY() + ", ";
                }
                try {
                    tiles = tiles.substring(0, tiles.lastIndexOf(",")) + ".";
                } catch (StringIndexOutOfBoundsException e) {
                                /* Do nothing no players are online */
                }
                StarNub.getLogger().cDebPrint("StarNub", "PACKET DamageTileGroupPacket Data: " +
                                "\nDamaged Tiles Count: " + damTileGroPacket.getTilePositions().size() +
                                "\nTiles Damaged: " + damTileGroPacket.getLayer() +
                                "\nSource of Damage: X-" + damTileGroPacket.getSourcePosition().getX() + ", Y-" + damTileGroPacket.getSourcePosition().getY() +
                                "\nTile Damage: Type: " + damTileGroPacket.getTileDamage().getTileDamageType() + ", Amount: " + damTileGroPacket.getTileDamage().getAmount() + ", Harvest Level: " + damTileGroPacket.getTileDamage().getHarvestLevel() +
                                "\nTiles Damaged: " + tiles
                );
                return eventData;
            }
        });
    }

    /**
     * Used in debugging {@link server.server.packets.connection.HeartbeatPacket}
     */
    public void HeartbeatPacket() {
        StarNub.getPacketEventRouter().registerEventSubscription("StarNub", HeartbeatPacket.class, new PacketEventHandler() {
            @Override
            public Packet onEvent(Packet eventData) {
                HeartbeatPacket herBeaPacket = (HeartbeatPacket) eventData;
                StarNub.getLogger().cDebPrint("StarNub", "PACKET HeartbeatPacket Data: \n" +
                        "starbounddata.packets.Packet ID: " + herBeaPacket.getPacketId() +
                        "\nstarbounddata.packets.Packet Payload: " + herBeaPacket.getCurrentStep());
                return eventData;
            }
        });
    }

    /**
     * Used in debugging {@link server.server.packets.chat.ChatSendPacket}
     */
    public void PlayerWarpPacket() {
        StarNub.getPacketEventRouter().registerEventSubscription("StarNub", PlayerWarpPacket.class, new PacketEventHandler() {
            @Override
            public Packet onEvent(Packet eventData) {
                PlayerWarpPacket playerWarpPacket = (PlayerWarpPacket) eventData;
                StarNub.getLogger().cDebPrint("StarNub", "PACKET PlayerWarpPacket Data: \n" +
                                playerWarpPacket.getWarpType() + "\n"
                        //                    playerWarpPacket.getWarpTarget()+"\n"+
                        //                    Arrays.toString(playerWarpPacket.getPayload())
                );
                ByteBuf byteBuf = Unpooled.copiedBuffer(playerWarpPacket.getPayload());
                System.out.println(StarboundBufferReader.readStringVLQ(byteBuf));
                return eventData;
            }
        });
    }

    /**
     * Used in debugging {@link server.server.packets.server.ProtocolVersionPacket}
     */
    public void ProtocolVersionPacket() {
        StarNub.getPacketEventRouter().registerEventSubscription("StarNub", ProtocolVersionPacket.class, new PacketEventHandler() {
            @Override
            public Packet onEvent(Packet eventData) {
                ProtocolVersionPacket protVerPacket = (ProtocolVersionPacket) eventData;
                StarNub.getLogger().cDebPrint("StarNub", "PACKET ProtocolVersion Data: Server Version: " + protVerPacket.getProtocolVersion());
                return eventData;
            }
        });
    }

    /**
     * Used in debugging {@link server.server.packets.connection.ServerDisconnectPacket}
     */
    public void ServerDisconnectPacket() {
        StarNub.getPacketEventRouter().registerEventSubscription("StarNub", ServerDisconnectPacket.class, new PacketEventHandler() {
            @Override
            public Packet onEvent(Packet eventData) {
                ServerDisconnectPacket serDisPacket = (ServerDisconnectPacket) eventData;
                StarNub.getLogger().cDebPrint("StarNub", "PACKET ServerDisconnectPacket Data: \n" +
                        "starbounddata.packets.Packet ID: " + serDisPacket.getPacketId() +
                        "\nstarbounddata.packets.Packet Payload: " + serDisPacket.getPayload());
                return eventData;
            }
        });
    }

    /**
     * Used in debugging {@link server.server.packets.server.UniverseTimeUpdatePacket}
     */
    public void UniverseTimeUpdatePacket() {
        StarNub.getPacketEventRouter().registerEventSubscription("StarNub", UniverseTimeUpdatePacket.class, new PacketEventHandler() {
            @Override
            public Packet onEvent(Packet eventData) {
                UniverseTimeUpdatePacket univTimPacket = (UniverseTimeUpdatePacket) eventData;
                StarNub.getLogger().cDebPrint("StarNub", "PACKET UniverseTimeUpdate Data: Time: " + univTimPacket.getTime());
                return eventData;
            }
        });
    }

    /**
     * Used in debugging {@link server.server.packets.server.UniverseTimeUpdatePacket}
     */
    public void WorldStartPacket() {
        StarNub.getPacketEventRouter().registerEventSubscription("StarNub", WorldStartPacket.class, new PacketEventHandler() {
            @Override
            public Packet onEvent(Packet eventData) {
                WorldStartPacket worlStarPacket = (WorldStartPacket) eventData;
                StarNub.getLogger().cDebPrint("StarNub", "PACKET WorldStartPacket Data: Reason: " + worlStarPacket.getStartReason());
                return eventData;
            }
        });
    }

    /**
     * Used in debugging {@link server.server.packets.server.UniverseTimeUpdatePacket}
     */
    public void WorldStopPacket() {
        StarNub.getPacketEventRouter().registerEventSubscription("StarNub", WorldStopPacket.class, new PacketEventHandler() {
            @Override
            public Packet onEvent(Packet eventData) {
                WorldStopPacket worlStopPacket = (WorldStopPacket) eventData;
                StarNub.getLogger().cDebPrint("StarNub", "PACKET WorldStopPacket Data: Reason: " + worlStopPacket.getStopReason());
                return eventData;
            }
        });
    }
}



