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

package starnubserver.resources.internal.handlers;

import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import starbounddata.packets.Packet;
import starbounddata.packets.chat.ChatReceivePacket;
import starbounddata.packets.connection.ClientConnectPacket;
import starboundmanager.Starting;
import starnubserver.Connections;
import starnubserver.StarNub;
import starnubserver.cache.objects.RejectionCache;
import starnubserver.cache.wrappers.PlayerUUIDCacheWrapper;
import starnubserver.connections.player.StarNubProxyConnection;
import starnubserver.connections.player.session.Ban;
import starnubserver.connections.player.session.Player;
import starnubserver.events.events.StarNubEvent;
import starnubserver.events.packet.PacketEventHandler;
import starnubserver.events.starnub.StarNubEventHandler;
import starnubserver.events.starnub.StarNubEventSubscription;
import utilities.cache.objects.TimeCache;
import utilities.events.types.Event;
import utilities.exceptions.CacheWrapperOperationException;
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
        this.ALREADY_LOGGED_ON = new PlayerUUIDCacheWrapper("StarNub", "StarNub - Character Already Online", true, StarNub.getTaskManager(), 20, expectedThreads, TimeUnit.MINUTES, 10, 60);
        this.RESERVED_KICKED = new PlayerUUIDCacheWrapper("StarNub", "StarNub - Reserved Kick", true, StarNub.getTaskManager(), 20, expectedThreads, TimeUnit.MINUTES, 10, 60);
        /* Register and event handler to notify player if they were kicked for a reserved player */
        new StarNubEventSubscription("StarNub", "Player_Connected", new StarNubEventHandler<Event<String>>() {
            @Override
            public Event<String> onEvent(Event<String> eventData) {
                Player player = (Player) eventData.getEVENT_DATA();
                TimeCache timeCache = null;
                try {
                    timeCache = RESERVED_KICKED.removeCache(player.getPlayerCharacter().getUuid());
                } catch (CacheWrapperOperationException e) {
                    StarNub.getLogger().cFatPrint("StarNub", e.getMessage());
                }
                if (timeCache != null) {
                    player.sendChatMessage("StarNub", ChatReceivePacket.ChatReceiveChannel.UNIVERSE, "You were disconnected to make room for a player" +
                            "who has a Reserved Server Slot.");
                }
                return null;
            }
        });
    }

    /**
     * Recommended: For internal use with StarNub
     * <p>
     * Uses: Handles Client Connection Packets for connection attempts. Step 1 of 2 (ConnectionResponsePacket is Part 2).
     *
     * @param eventData Packet representing the packet being routed
     * @return Packet any class representing packet can be returned
     */
    @Override
    public Packet onEvent(Packet eventData) {
        ClientConnectPacket clientConnectPacket = (ClientConnectPacket) eventData;

        ChannelHandlerContext clientCTX = clientConnectPacket.getSENDER_CTX();
        CONNECTIONS.getOPEN_SOCKETS().remove(clientCTX);
        CONNECTIONS.getOPEN_SOCKETS().remove(clientConnectPacket.getDESTINATION_CTX());
        StarNubProxyConnection starNubProxyConnection = (StarNubProxyConnection) CONNECTIONS.getOPEN_CONNECTIONS().remove(clientCTX);

        String playerName = clientConnectPacket.getPlayerName();
        UUID playerUUID = clientConnectPacket.getUuid();

        Player player = new Player(starNubProxyConnection, playerName, playerUUID);

        String header =
                "^#f5f5f5;-== " + StarNub.getConfiguration().getNestedValue("starnub info", "server_name") + " ==-\n" +
                        "^#f5f5f5;-= Powered by StarNub.org =- \n\n";
        String footer =
                "\n\n^#f5f5f5;For information visit: ^#990000;" + StringUtils.remove((String) StarNub.getConfiguration().getNestedValue("starnub info", "server_url"), "http://");

        RejectionCache rejectionCache = null;

        /* Server Restarting Check */
        rejectionCache = restartingCheck(player, header, footer);
        if (rejectionCache != null) {
            return addToRejections(rejectionCache, clientConnectPacket);
        }

        /* Player Whitelist Check */
        rejectionCache = whitelist(player, header, footer);
        if (rejectionCache != null) {
            return addToRejections(rejectionCache, clientConnectPacket);
        }

        /* Player Ban Check */
        rejectionCache = bannedCheck(player, header, footer);
        if (rejectionCache != null) {
            return addToRejections(rejectionCache, clientConnectPacket);
        }

        /* Already Logged On */
        rejectionCache = alreadyLoggedOn(player, header, footer);
        if (rejectionCache != null) {
            return addToRejections(rejectionCache, clientConnectPacket);
        }

        /* Server Full Check */
        rejectionCache = serverFull(player, header, footer);
        if (rejectionCache != null) {
            return addToRejections(rejectionCache, clientConnectPacket);
        }

        /* Allow Connection */
        return addToRejections(new RejectionCache(false, player), clientConnectPacket);
    }

    /**
     * Recommended: For internal use with StarNub
     * <p>
     * Uses: This will process a rejected cache and packet
     *
     * @param rejectionCache      Rejection cache representing the the rejection cache
     * @param clientConnectPacket ClientConnectPacket representing the starnubclient connection packet
     * @return ClientConnectPacket must be returned to be sent back into the stream
     */
    private ClientConnectPacket addToRejections(RejectionCache rejectionCache, ClientConnectPacket clientConnectPacket) {
        Player player = rejectionCache.getPLAYER();
        ChannelHandlerContext clientCTX = player.getCLIENT_CTX();
        player.getPlayerCharacter().setLastSeen(DateTime.now());
        new StarNubEvent("Player_Connection_Attempt", this);
        StarNub.getLogger().cDebPrint("StarNub", "A Player is attempting to connect to the network on IP: " + player.getSessionIpString() + ".");
        try {
            CONNECTIONS.getCONNECTED_PLAYERS().getACCEPT_REJECT().addCache(clientCTX, rejectionCache);
        } catch (CacheWrapperOperationException e) {
            StarNub.getLogger().cFatPrint("StarNub", e.getMessage());
        }
        return clientConnectPacket;
    }

    /**
     * Recommended: For internal use with StarNub
     * <p>
     * Uses: This checks to see if the network is restarting
     *
     * @param player Player player session
     * @param header rejection header
     * @param footer rejection footer
     * @return RejectionCache or null depending if we need to reject this player
     */
    private RejectionCache restartingCheck(Player player, String header, String footer) {
        if (StarNub.getStarboundServer().getStarboundManager().getStatus() instanceof Starting) {
            String reason = "\n^#f5f5f5;Starbound is restarting, please come back in a few minutes.\n";
            return new RejectionCache(true, RejectionCache.Reason.RESTARTING, header + reason + footer, player);
        } else {
            return null;
        }
    }

    /**
     * Recommended: For internal use with StarNub
     * <p>
     * Uses: This checks to see if the network is whitelisted and is the player on it
     *
     * @param player Player player session
     * @param header rejection header
     * @param footer rejection footer
     * @return RejectionCache or null depending if we need to reject this player
     */
    private RejectionCache whitelist(Player player, String header, String footer) {
        if ((boolean) (StarNub.getConfiguration().getNestedValue("starnub settings", "whitelisted"))) {
            String playerIP = player.getSessionIpString();
            String playerUUID = player.getPlayerCharacter().getUuid().toString();
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
                String reason = "\n^#f5f5f5;This network is whitelisted.\n";
                return new RejectionCache(true, RejectionCache.Reason.WHITELIST, header + reason + footer, player);
            }
        } else {
            return null;
        }
    }

    /**
     * Recommended: For internal use with StarNub
     * <p>
     * Uses: This checks to see if the player is banned or not by uuid and ip
     *
     * @param player Player player session
     * @param header rejection header
     * @param footer rejection footer
     * @return RejectionCache or null depending if we need to reject this player
     */
    private RejectionCache bannedCheck(Player player, String header, String footer) {
        Ban ban = player.getPlayerCharacter().getBan();
        if (ban != null) {
            DateTime dateRestrictionExpires = ban.getDateRestrictionExpires();
            String reason;
            RejectionCache.Reason bannedReason = null;
            if (dateRestrictionExpires == null) {
                reason =
                        "\n^#f5f5f5;You have been permanently banned: \n^#990000; Since " + DateAndTimes.getFormattedDate("MMMM dd, yyyy", ban.getDateRestricted());
                bannedReason = RejectionCache.Reason.BANNED;
            } else {
                reason =
                        "^#f5f5f5;You have been temporarily banned: \n^#990000; Since " + DateAndTimes.getFormattedDate("MMMM dd, yyyy", ban.getDateRestricted()) +
                                "\n^#f5f5f5;You will be automatically unbanned on: \n^#990000;" + DateAndTimes.getFormattedDate("MMMM dd, yyyy '@' HH:mm '- Server Time'", dateRestrictionExpires);
                bannedReason = RejectionCache.Reason.TEMPORARY_BANNED;
            }
            return new RejectionCache(true, bannedReason, header + reason + footer, player);
        } else {
            return null;
        }
    }

    /**
     * Recommended: For internal use with StarNub
     * <p>
     * Uses: This checks to see if the player is logged in already, the first time they cannot connect, the second they disconnect the current player
     *
     * @param player Player player session
     * @param header rejection header
     * @param footer rejection footer
     * @return RejectionCache or null depending if we need to reject this player
     */
    private RejectionCache alreadyLoggedOn(Player player, String header, String footer) {
        UUID uuid = player.getPlayerCharacter().getUuid();
        boolean isOnline = CONNECTIONS.getCONNECTED_PLAYERS().isOnline("StarNub", uuid);
        if (isOnline) {
            try {
                if (ALREADY_LOGGED_ON.getCache(uuid) != null) {
                    Player playerDisconnect = CONNECTIONS.getCONNECTED_PLAYERS().getOnlinePlayerByAnyIdentifier(uuid);
                    playerDisconnect.disconnectReason("Same_Character_Login");
                    ALREADY_LOGGED_ON.removeCache(uuid);
                } else {
                    ALREADY_LOGGED_ON.addCache(uuid, new TimeCache());
                    String reason = "\n^#f5f5f5;You are already logged into this Server with this character. Please try again.";
                    return new RejectionCache(true, RejectionCache.Reason.ALREADY_LOGGED_IN, header + reason + footer, player);
                }
            } catch (CacheWrapperOperationException e) {
                StarNub.getLogger().cFatPrint("StarNub", e.getMessage());
            }
        }
        return null;
    }

    /**
     * Recommended: For internal use with StarNub
     * <p>
     * Uses: This checks to see if the network is full. Reserved Kick can will kick regular players first, then reserved players. Reserved plays can only ick regular players
     *
     * @param player Player player session
     * @param header rejection header
     * @param footer rejection footer
     * @return RejectionCache or null depending if we need to reject this player
     */
    private RejectionCache serverFull(Player player, String header, String footer) {
        int currentPlayerCount = CONNECTIONS.getCONNECTED_PLAYERS().size();
        int playerLimit = (int) StarNub.getConfiguration().getNestedValue("resources", "player_limit");
        int vipLimit = (int) StarNub.getConfiguration().getNestedValue("resources", "player_limit_reserved");
        int combinedCount = playerLimit + vipLimit;
        if (currentPlayerCount >= playerLimit) {
            String reason;
            String previousKick = "";
            boolean spaceMade = false;
            TimeCache timeCache = null;
            try {
                timeCache = RESERVED_KICKED.removeCache(player.getPlayerCharacter().getUuid());
            } catch (CacheWrapperOperationException e) {
                e.printStackTrace();
            }
            if (timeCache != null) {
                previousKick = "\n^#f5f5f5;You were previously Kicked to make room for a player with a reserved Slot.";
            }
            if (player.hasPermission("starnubinternals.reserved.kick", true)) {
                for (Player playerToKick : CONNECTIONS.getCONNECTED_PLAYERS().values()) {
                    if (CONNECTIONS.getCONNECTED_PLAYERS().size() < combinedCount) {
                        spaceMade = true;
                        break;
                    } else if (!playerToKick.hasPermission("starnubinternals.reserved", true) || !playerToKick.hasPermission("starnubinternals.reserved.kick", true)) {
                        player.disconnectReason("Reserved_Kick");
                        try {
                            RESERVED_KICKED.addCache(playerToKick.getPlayerCharacter().getUuid(), new TimeCache());
                        } catch (CacheWrapperOperationException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (!spaceMade) {
                    for (Player playerToKick : CONNECTIONS.getCONNECTED_PLAYERS().values()) {
                        if (CONNECTIONS.getCONNECTED_PLAYERS().size() < combinedCount) {
                            spaceMade = true;
                            break;
                        } else if (!playerToKick.hasPermission("starnubinternals.reserved.kick", true)) {
                            player.disconnectReason("Reserved_Kick");
                            try {
                                RESERVED_KICKED.addCache(playerToKick.getPlayerCharacter().getUuid(), new TimeCache());
                            } catch (CacheWrapperOperationException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if (!spaceMade) {
                    reason = "^#f5f5f5;This network is full and no more VIP slots are available.";
                    return new RejectionCache(true, RejectionCache.Reason.SERVER_FULL_NO_VIP_KICK, header + reason + footer, player);
                }
            } else if (player.hasPermission("starnubinternals.reserved", true)) {
                for (Player playerToKick : CONNECTIONS.getCONNECTED_PLAYERS().values()) {
                    if (CONNECTIONS.getCONNECTED_PLAYERS().size() < combinedCount) {
                        spaceMade = true;
                        break;
                    } else if (!playerToKick.hasPermission("starnubinternals.reserved", true) || !playerToKick.hasPermission("starnubinternals.reserved.kick", true)) {
                        player.disconnectReason("Reserved_Kick");
                        try {
                            RESERVED_KICKED.addCache(playerToKick.getPlayerCharacter().getUuid(), new TimeCache());
                        } catch (CacheWrapperOperationException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (!spaceMade) {
                    reason = "^#f5f5f5;This network is full and no more VIP slots are available." + previousKick;
                    return new RejectionCache(true, RejectionCache.Reason.SERVER_FULL_NO_VIP, header + reason + footer, player);
                }
            } else {
                reason = "^#f5f5f5;This network is full and you do not have permission to enter." + previousKick;
                return new RejectionCache(true, RejectionCache.Reason.SERVER_FULL, header + reason + footer, player);
            }
        }
        return null;
    }
}








