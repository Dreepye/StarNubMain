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

package starnub.resources.internal.handlers;

import starbounddata.packets.Packet;
import starnub.Connections;
import starnub.StarNub;
import starnub.connections.player.session.Player;
import starnub.events.events.PlayerEvent;
import starnub.events.packet.PacketEventHandler;

public class ConnectionResponseHandler extends PacketEventHandler {

    private final Connections CONNECTIONS;

    public ConnectionResponseHandler(Connections CONNECTIONS) {
        this.CONNECTIONS = CONNECTIONS;
    }

    /**
     * Recommended: For internal use with StarNub
     * <p>
     * Uses: Handles Server Connection Response Packets which are replies to the Connection Attempts
     *
     * @param eventData Packet representing the packet being routed
     * @return Packet any class representing packet can be returned
     */
    @Override
    public Packet onEvent(Packet eventData) {





        return null;
    }
}





    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method will add an event handler to listen to the starbounddata.packets.starbounddata.packets.starnub starbounddata.packets.connection response to the player starbounddata.packets.connection attempt.
     * StarNub will pull out the pending session data that was already collected and check it over. StarNub will modify the packet
     * and allow or disallow the session to be completely setup. If the session is set up then StarNub will take the collected
     * pending player session data and create a new {@link starnub.connections.player.session.Player} that will then be
     * placed in connectedPlayers. This is Step 2 of 2.
     * <p>
     */
    private void eventListenerPlayerConnectionFinal() {
//        StarNub.getPacketEventRouter().getPacketReactor().on(T(ConnectResponsePacket.class), ev -> {
        StarNub.getPacketEventRouter().registerEventSubscription("StarNub", ConnectResponsePacket.class, new PacketEventHandler() {
            @Override
            public Packet onEvent(Packet packet) {

            /* Cast the event data (ev.data()) into a starbounddata.packets.connection response packet */
                ConnectResponsePacket conResPacket = (ConnectResponsePacket) packet;

            /* Pull the existing data from the pending connections */
                PendingPlayer pendingPlayer = StarNub.getServer().getConnectionss().removePendingConnections(conResPacket.getDestinationCTX());
                Player playerSession = pendingPlayer.getPLAYER_SESSION();

                if (pendingPlayer.getREJECT_CLIENT() != null) {
                    conResPacket.setSuccess(false);
                    conResPacket.setRejectionReason(pendingPlayer.getREJECTION_REASON());
                    return conResPacket;
                }
                addConnectedPlayer(playerSession);
            /* StarNub will spawn a thread to finish the client starbounddata.packets.connection so that the packet can continue to the client
             * without delay. Delaying this packet further then the above will cause a race condition and a client
             * disconnect from the starbounddata.packets.starbounddata.packets.starnub, before the starbounddata.packets.connection set up is complete */
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PlayerCharacter playerCharacter = playerSession.getPlayerCharacter();
                        playerSession.setStarboundClientId(conResPacket.getClientId());

                        Server cServer = StarNub.getServer();
                        MultiOutputLogger cMsgSen = StarNub.getLogger();
                        DatabaseTables cDb = StarNub.getDatabaseTables();
                        ChatFilter cChatF = cServer.getServerChat().getChatFilter();

                        if (pendingPlayer.getREJECT_CLIENT() != null) {
                            String rejectReason = pendingPlayer.getREJECT_CLIENT();
                            //TODO Potentially move this into singular thread
                            if (rejectReason.equalsIgnoreCase("whitelist")) {
                                PlayerEvent.eventSend_Player_Connection_Failure_Whitelist(pendingPlayer);
                                cMsgSen.cWarnPrint("StarNub", "A Player tried connecting while the starbounddata.packets.starbounddata.packets.starnub is whitelisted on IP: " + pendingPlayer.getSESSION_IP() + ".");
                                serverSideNotificationOfPlayerDisconnect(pendingPlayer.getSESSION_SERVER_CTX());
                                return;
                            } else if (rejectReason.equalsIgnoreCase("ban") || rejectReason.equalsIgnoreCase("tban")) {
                                PlayerEvent.eventSend_Player_Connection_Failure_Banned_Temporary(pendingPlayer);
                                cMsgSen.cWarnPrint("StarNub", "A banned Player tried connecting to the starbounddata.packets.starbounddata.packets.starnub on IP: " + pendingPlayer.getSESSION_IP() + ".");
                                serverSideNotificationOfPlayerDisconnect(pendingPlayer.getSESSION_SERVER_CTX());
                                return;
                            } else if (rejectReason.equalsIgnoreCase("online")) {
                                PlayerEvent.eventSend_Player_Connection_Failure_Character_Already_Online(pendingPlayer);
                                cMsgSen.cWarnPrint("StarNub", "A Player tried to log in with the same character multiple times.");
                                serverSideNotificationOfPlayerDisconnect(pendingPlayer.getSESSION_SERVER_CTX());
                                return;
                            } else if (rejectReason.equalsIgnoreCase("server_full")) {
                                PlayerEvent.eventSend_Player_Connection_Failure_Character_Already_Online(pendingPlayer);
                                cMsgSen.cWarnPrint("StarNub", "A player tried to log into the starbounddata.packets.starbounddata.packets.starnub which is full.");
                                serverSideNotificationOfPlayerDisconnect(pendingPlayer.getSESSION_SERVER_CTX());
                                return;
                            } else if (rejectReason.equalsIgnoreCase("restarting")) {
                                PlayerEvent.eventSend_Player_Connection_Failure_Character_Already_Online(pendingPlayer);
                                cMsgSen.cWarnPrint("StarNub", "A player tried to log into the starbounddata.packets.starbounddata.packets.starnub while its preparing to restart.");
                                serverSideNotificationOfPlayerDisconnect(pendingPlayer.getSESSION_SERVER_CTX());
                                return;
                            }
                        }
                        //PLACE_HOLDER - New character ip combo event

                    /* Nickname Normalizer */
                        String nickOriginal = playerSession.getNickName();
                        String nickClean = cChatF.cleanNameAccordingToPermissions(playerSession, nickOriginal);
                        try {
                            if (cChatF.percentageAlikeCalculation(nickClean, nickOriginal) != 100) {
                                nickNameChanger("StarNub", playerSession, nickClean, "Server Auto Name Changer. Illegal Nick Name.");
                            }
                        } catch (ArithmeticException e) {
                            StarNub.getLogger().cFatPrint("StarNub", "Arithmetic Exception: Percentage calculator. Variables 1: " + nickClean + ", Variable 2: " + nickOriginal + ".");
                        }
                    /* Sends an event on player starbounddata.packets.connection success, adds the player to universe starbounddata.packets.chat */
                        PlayerEvent.eventSend_Player_Connection_Success(playerSession);
                        //REPLACE with starbounddata.packets.chat room subscriptions
                        cServer.getServerChat().joinChatRoom("Universe", playerSession, null, false);
                    }
                }, "StarNub - Connections - Player Connection Wrap-Up").start();
//        });
                return conResPacket;
            }
        });
    }
