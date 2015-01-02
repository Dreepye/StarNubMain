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

package starnubserver.resources.connections.handlers;

import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import starbounddata.packets.Packet;
import starbounddata.packets.connection.ClientConnectPacket;
import starbounddata.types.chat.Mode;
import starboundmanager.Starting;
import starnubdata.generic.DisconnectReason;
import starnubserver.Connections;
import starnubserver.StarNub;
import starnubserver.cache.objects.RejectionCache;
import starnubserver.cache.wrappers.PlayerUUIDCacheWrapper;
import starnubserver.connections.player.StarNubProxyConnection;
import starnubserver.connections.player.generic.Ban;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.events.events.StarNubEvent;
import starnubserver.events.packet.PacketEventHandler;
import starnubserver.events.starnub.StarNubEventHandler;
import starnubserver.events.starnub.StarNubEventSubscription;
import utilities.cache.objects.TimeCache;
import utilities.events.Priority;
import utilities.events.types.ObjectEvent;
import utilities.time.DateAndTimes;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Represents ClientConnectHandler this is used because creating this anonymously would be to much code
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class ClientConnectHandler extends PacketEventHandler {

    private final Connections CONNECTIONS;

    private PlayerUUIDCacheWrapper ALREADY_LOGGED_ON;
    private PlayerUUIDCacheWrapper RESERVED_KICKED;

    public ClientConnectHandler(Connections CONNECTIONS, int expectedThreads) {
        this.CONNECTIONS = CONNECTIONS;
        this.ALREADY_LOGGED_ON = new PlayerUUIDCacheWrapper("StarNub", "StarNub - Character Already Online", true, 20, TimeUnit.MINUTES, 10, 60);
        this.RESERVED_KICKED = new PlayerUUIDCacheWrapper("StarNub", "StarNub - Reserved Kick", true, 20, TimeUnit.MINUTES, 10, 60);
        /* Register and events handler to notify player if they were kicked for a reserved player */
        new StarNubEventSubscription("StarNub", Priority.CRITICAL ,"Player_Connected", new StarNubEventHandler() {
            @Override
            public void onEvent(ObjectEvent eventData) {
                PlayerSession playerSession = (PlayerSession) eventData.getEVENT_DATA();
                TimeCache timeCache = null;
                timeCache = RESERVED_KICKED.removeCache(playerSession.getPlayerCharacter().getUuid());
                if (timeCache != null) {
                    playerSession.sendChatMessage("StarNub", Mode.BROADCAST, "You were disconnected to make room for a player" +
                            " who has a Reserved Server Slot.");
                }
            }
        });
    }

    /**
     * Recommended: For connections use with StarNub
     * <p>
     * Uses: Handles Client Connection Packets for connection attempts. Step 1 of 2 (ConnectionResponsePacket is Part 2).
     *
     * @param eventData Packet representing the packet being routed
     */
    @Override
    public void onEvent(Packet eventData) {
        ClientConnectPacket clientConnectPacket = (ClientConnectPacket) eventData;
        ChannelHandlerContext clientCTX = clientConnectPacket.getSENDER_CTX();

        StarNubProxyConnection starnubProxyConnection = (StarNubProxyConnection) StarNub.getConnections().getOPEN_CONNECTIONS().remove(clientCTX);

        String playerName = clientConnectPacket.getPlayerName();
        UUID playerUUID = clientConnectPacket.getPlayerUuid();

        PlayerSession playerSession = new PlayerSession(starnubProxyConnection, playerName, playerUUID);

        String header =
                "^#f5f5f5;-== " + StarNub.getConfiguration().getNestedValue("starnub_info", "server_name") + " ==-\n" +
                        "^#f5f5f5;-= Powered by StarNub.org =- \n\n";
        String footer =
                "\n\n^#f5f5f5;For information visit: ^#990000;" + StringUtils.remove((String) StarNub.getConfiguration().getNestedValue("starnub_info", "server_url"), "http://");

        RejectionCache rejectionCache = null;

        /* Server Restarting Check */
        rejectionCache = restartingCheck(playerSession, header, footer);
        if (rejectionCache != null) {
            addToRejections(rejectionCache, clientConnectPacket);
            return;
        }

        /* Player Whitelist Check */
        rejectionCache = whitelist(playerSession, header, footer);
        if (rejectionCache != null) {
            addToRejections(rejectionCache, clientConnectPacket);
            return;
        }

        /* Player Ban Check */
        rejectionCache = bannedCheck(playerSession, header, footer);
        if (rejectionCache != null) {
            addToRejections(rejectionCache, clientConnectPacket);
            return;
        }

        /* Already Logged On */
        rejectionCache = alreadyLoggedOn(playerSession, header, footer);
        if (rejectionCache != null) {
            addToRejections(rejectionCache, clientConnectPacket);
            return;
        }

        /* Server Full Check */
        rejectionCache = serverFull(playerSession, header, footer);
        if (rejectionCache != null) {
            addToRejections(rejectionCache, clientConnectPacket);
            return;
        }

        /* Allow Connection */
        addToRejections(new RejectionCache(false, playerSession), clientConnectPacket);
    }

    /**
     * Recommended: For connections use with StarNub
     * <p>
     * Uses: This will process a rejected cache and packet
     *
     * @param rejectionCache      Rejection cache representing the the rejection cache
     * @param clientConnectPacket ClientConnectPacket representing the starnubclient connection packet
     * @return ClientConnectPacket must be returned to be sent back into the stream
     */
    private ClientConnectPacket addToRejections(RejectionCache rejectionCache, ClientConnectPacket clientConnectPacket) {
        PlayerSession playerSession = rejectionCache.getPLAYERSession();
        ChannelHandlerContext clientCTX = playerSession.getCONNECTION().getCLIENT_CTX();
        playerSession.getPlayerCharacter().updateLastSeen(DateTime.now());
        new StarNubEvent("Player_Connection_Attempt", this);
        StarNub.getLogger().cDebPrint("StarNub", "A player named "+ playerSession.getPlayerCharacter().getCleanName() +" is attempting to connect to the server on IP: " + playerSession.getSessionIpString() + ".");
        CONNECTIONS.getCONNECTED_PLAYERS().getACCEPT_REJECT().addCache(clientCTX, rejectionCache);
        return clientConnectPacket;
    }

    /**
     * Recommended: For connections use with StarNub
     * <p>
     * Uses: This checks to see if the starnubdata.network is restarting
     *
     * @param playerSession Player player session
     * @param header rejection header
     * @param footer rejection footer
     * @return RejectionCache or null depending if we need to reject this player
     */
    private RejectionCache restartingCheck(PlayerSession playerSession, String header, String footer) {
        if (StarNub.getStarboundServer().getStatus() instanceof Starting || StarNub.getStarboundServer().isRestarting()) {
            String reason = "\n^#f5f5f5;Starbound is restarting, please come back in a few minutes.\n";
            return new RejectionCache(true, RejectionCache.Reason.RESTARTING, header + reason + footer, playerSession);
        } else {
            return null;
        }
    }

    /**
     * Recommended: For connections use with StarNub
     * <p>
     * Uses: This checks to see if the starnubdata.network is whitelisted and is the player on it
     *
     * @param playerSession Player player session
     * @param header rejection header
     * @param footer rejection footer
     * @return RejectionCache or null depending if we need to reject this player
     */
    private RejectionCache whitelist(PlayerSession playerSession, String header, String footer) {
        if ((boolean) (StarNub.getConfiguration().getNestedValue("starnub_settings", "whitelisted"))) {
            String playerIP = playerSession.getSessionIpString();
            String playerUUID = playerSession.getPlayerCharacter().getUuid().toString();
            boolean ipWhitelisted = false;
            boolean uuidWhitelisted = false;
            try {
                ipWhitelisted = (StarNub.getConfiguration().collectionContains(playerIP, "uuid_ip"));
                uuidWhitelisted = (StarNub.getConfiguration().collectionContains(playerUUID, "uuid_ip"));
            } catch (Exception e) {
                /* Silent Catch */
            }
            if (ipWhitelisted) {
                return null;
            } else if (uuidWhitelisted) {
                return null;
            } else {
                String reason = "\n^#f5f5f5;This starnubdata.network is whitelisted.\n";
                return new RejectionCache(true, RejectionCache.Reason.WHITELIST, header + reason + footer, playerSession);
            }
        } else {
            return null;
        }
    }

    /**
     * Recommended: For connections use with StarNub
     * <p>
     * Uses: This checks to see if the player is banned or not by uuid and ip
     *
     * @param playerSession Player player session
     * @param header rejection header
     * @param footer rejection footer
     * @return RejectionCache or null depending if we need to reject this player
     */
    private RejectionCache bannedCheck(PlayerSession playerSession, String header, String footer) {
        Ban ban = CONNECTIONS.getBANSList().banGet(playerSession);
        if (ban != null) {
            DateTime dateRestrictionExpires = ban.getDateExpires();
            String reason;
            RejectionCache.Reason bannedReason = null;
            if (dateRestrictionExpires == null) {
                reason =
                        "\n^#f5f5f5;You have been permanently banned: \n^#990000; Since " + DateAndTimes.getFormattedDate("MMMM dd, yyyy", ban.getDate());
                bannedReason = RejectionCache.Reason.BANNED;
            } else {
                reason =
                        "^#f5f5f5;You have been temporarily banned: \n^#990000; Since " + DateAndTimes.getFormattedDate("MMMM dd, yyyy", ban.getDate()) +
                                "\n^#f5f5f5;You will be automatically unbanned on: \n^#990000;" + DateAndTimes.getFormattedDate("MMMM dd, yyyy '@' HH:mm '- Server Time'", dateRestrictionExpires);
                bannedReason = RejectionCache.Reason.TEMPORARY_BANNED;
            }
            return new RejectionCache(true, bannedReason, header + reason + footer, playerSession);
        } else {
            return null;
        }
    }

    /**
     * Recommended: For connections use with StarNub
     * <p>
     * Uses: This checks to see if the player is logged in already, the first time they cannot connect, the second they disconnect the current player
     *
     * @param playerSession Player player session
     * @param header rejection header
     * @param footer rejection footer
     * @return RejectionCache or null depending if we need to reject this player
     */
    private RejectionCache alreadyLoggedOn(PlayerSession playerSession, String header, String footer) { //DEBUG - THIS METHOD - Does not disconnect on second attempt
        UUID uuid = playerSession.getPlayerCharacter().getUuid();
        boolean isOnline = CONNECTIONS.getCONNECTED_PLAYERS().isOnline("StarNub", uuid);
        if (isOnline) {
            if (ALREADY_LOGGED_ON.getCache(uuid) != null) {
                PlayerSession playerSessionDisconnect = CONNECTIONS.getCONNECTED_PLAYERS().getOnlinePlayerByAnyIdentifier(uuid);
                playerSessionDisconnect.disconnectReason(DisconnectReason.CHARACTER_LOG_IN);
                ALREADY_LOGGED_ON.removeCache(uuid);
            } else {
                ALREADY_LOGGED_ON.addCache(uuid, new TimeCache());
                String reason = "\n^#f5f5f5;You are already logged into this Server with this character. Please try again.";
                return new RejectionCache(true, RejectionCache.Reason.ALREADY_LOGGED_IN, header + reason + footer, playerSession);
            }
        }
        return null;
    }

    /**
     * Recommended: For connections use with StarNub
     * <p>
     * Uses: This checks to see if the starnubdata.network is full. Reserved Kick can will kick regular players first, then reserved players. Reserved plays can only ick regular players
     *
     * @param playerSession Player player session
     * @param header rejection header
     * @param footer rejection footer
     * @return RejectionCache or null depending if we need to reject this player
     */
    private RejectionCache serverFull(PlayerSession playerSession, String header, String footer) {
        int currentPlayerCount = CONNECTIONS.getCONNECTED_PLAYERS().size();
        int playerLimit = (int) StarNub.getConfiguration().getNestedValue("starnub_settings", "player_limit");
        int vipLimit = (int) StarNub.getConfiguration().getNestedValue("starnub_settings", "player_limit_reserved");
        int combinedCount = playerLimit + vipLimit;
        if (currentPlayerCount >= playerLimit) {
            String reason;
            String previousKick = "";
            boolean spaceMade = false;
            TimeCache timeCache = null;
            timeCache = RESERVED_KICKED.removeCache(playerSession.getPlayerCharacter().getUuid());
            if (timeCache != null) {
                previousKick = "\n^#f5f5f5;You were previously Kicked to make room for a player with a reserved Slot.";
            }
            if (playerSession.hasPermission("starnub.reserved.kick", true)) {
                for (PlayerSession playerSessionToKick : CONNECTIONS.getCONNECTED_PLAYERS().values()) {
                    if (CONNECTIONS.getCONNECTED_PLAYERS().size() < combinedCount) {
                        spaceMade = true;
                        break;
                    } else if (!playerSessionToKick.hasPermission("starnub.reserved", true) || !playerSessionToKick.hasPermission("starnubinternals.reserved.kick", true)) {
                        playerSession.disconnectReason(DisconnectReason.RESERVED_KICK);
                        RESERVED_KICKED.addCache(playerSessionToKick.getPlayerCharacter().getUuid(), new TimeCache());
                    }
                }
                if (!spaceMade) {
                    for (PlayerSession playerSessionToKick : CONNECTIONS.getCONNECTED_PLAYERS().values()) {
                        if (CONNECTIONS.getCONNECTED_PLAYERS().size() < combinedCount) {
                            spaceMade = true;
                            break;
                        } else if (!playerSessionToKick.hasPermission("starnub.reserved.kick", true)) {
                            playerSession.disconnectReason(DisconnectReason.RESERVED_KICK);
                            RESERVED_KICKED.addCache(playerSessionToKick.getPlayerCharacter().getUuid(), new TimeCache());
                        }
                    }
                }
                if (!spaceMade) {
                    reason = "^#f5f5f5;This starnubdata.network is full and no more VIP slots are available.";
                    return new RejectionCache(true, RejectionCache.Reason.SERVER_FULL_NO_VIP_KICK, header + reason + footer, playerSession);
                }
            } else if (playerSession.hasPermission("starnub.reserved", true)) {
                for (PlayerSession playerSessionToKick : CONNECTIONS.getCONNECTED_PLAYERS().values()) {
                    if (CONNECTIONS.getCONNECTED_PLAYERS().size() < combinedCount) {
                        spaceMade = true;
                        break;
                    } else if (!playerSessionToKick.hasPermission("starnub.reserved", true) || !playerSessionToKick.hasPermission("starnubinternals.reserved.kick", true)) {
                        playerSession.disconnectReason(DisconnectReason.RESERVED_KICK);
                        RESERVED_KICKED.addCache(playerSessionToKick.getPlayerCharacter().getUuid(), new TimeCache());
                    }
                }
                if (!spaceMade) {
                    reason = "^#f5f5f5;This starnubdata.network is full and no more VIP slots are available." + previousKick;
                    return new RejectionCache(true, RejectionCache.Reason.SERVER_FULL_NO_VIP, header + reason + footer, playerSession);
                }
            } else {
                reason = "^#f5f5f5;This starnubdata.network is full and you do not have permission to enter." + previousKick;
                return new RejectionCache(true, RejectionCache.Reason.SERVER_FULL, header + reason + footer, playerSession);
            }
        }
        return null;
    }
}








