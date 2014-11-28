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

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import starbounddata.packets.Packet;
import starbounddata.packets.connection.ClientConnectPacket;
import starboundmanager.Starting;
import starnub.Connections;
import starnub.StarNub;
import starnub.cache.objects.RejectionCache;
import starnub.connections.player.StarNubProxyConnection;
import starnub.connections.player.session.Player;
import starnub.connections.player.session.Restrictions;
import starnub.events.events.PlayerEvent;
import starnub.events.packet.PacketEventHandler;

import java.util.Map;
import java.util.UUID;

/**
 * Represents ClientConnectHandler this is used because creating this anonymously would be to much code
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class ClientConnectHandler extends PacketEventHandler{

    private final Connections CONNECTIONS;

    public ClientConnectHandler(Connections CONNECTIONS) {
        this.CONNECTIONS = CONNECTIONS;
    }
    /**
     * Recommended: For internal use with StarNub
     * <p>
     * Uses: Handles Client Connection Packets for connection attempts
     *
     * @param eventData Packet representing the packet being routed
     * @return Packet any class representing packet can be returned
     */
    @Override
    public Packet onEvent(Packet eventData) {
        ClientConnectPacket cliConPacket = (ClientConnectPacket) eventData;

        CONNECTIONS.getOPEN_SOCKETS().remove(cliConPacket.getSENDER_CTX());
        CONNECTIONS.getOPEN_SOCKETS().remove(cliConPacket.getDESTINATION_CTX());
        StarNubProxyConnection starNubProxyConnection = (StarNubProxyConnection) CONNECTIONS.getOPEN_CONNECTIONS().remove(cliConPacket.getDESTINATION_CTX());

        String playerName = cliConPacket.getPlayerName();
        UUID playerUUID = cliConPacket.getUuid();

        Player player = new Player(starNubProxyConnection, playerName, playerUUID);

        String header =
                "^#f5f5f5;-== " + StarNub.getConfiguration().getNestedValue("server_name", "starnub info") + " ==-\n" +
                        "^#f5f5f5;-= Powered by StarNub.org =- \n\n";
        String footer =
                "\n\n^#f5f5f5;For information visit: ^#990000;" + StringUtils.remove((String) StarNub.getConfiguration().getNestedValue("server_url", "starnub info"), "http://");

        RejectionCache rejectionCache = null;

        rejectionCache = restartingCheck(header, footer);
        if (rejectionCache != null) {
            return cliConPacket;
        }

        rejectionCache = whitelist(player, header, footer);
        if (rejectionCache != null) {
            return cliConPacket;
        }



        return cliConPacket;
    }

    private RejectionCache restartingCheck(String header, String footer) {
        if(StarNub.getServer().getStarboundManager().getStatus() instanceof Starting) {
            String reason = "\n^#f5f5f5;Starbound is restarting, please come back in a few minutes.\n";
            String rejectionReason = header + reason + footer;
            return new RejectionCache(true, RejectionCache.Reason.RESTARTING, rejectionReason);
        }
        return null;
    }

    private RejectionCache whitelist(Player player, String header, String footer) {
        if ((boolean) (StarNub.getConfiguration().getNestedValue("whitelisted", "starnub settings"))) {
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
                String reason = "\n^#f5f5f5;This server is whitelisted.\n";
                String rejectionReason = header + reason + footer;
                return new RejectionCache(true, RejectionCache.Reason.WHITELIST, rejectionReason);
            }
        }
        return null;
    }

    private RejectionCache bannedCheck(Player player, String header, String footer) {
        Restrictions restrictions = player.getPlayerCharacter().getRestrictions();
        if (restrictions != null) {
            if (restrictions.isUuidBanned()) {
                    /* Build the string information */
                DateTime dateRestrictionExpires = restrictions.getDateRestrictionExpires();
                if (dateRestrictionExpires == null) {
                    rejectionReason =
                            "\n^#f5f5f5;You have been permanently banned: \n^#990000; Since " + cDt.getFormattedDate("MMMM dd, yyyy", restrictions.getDateRestricted());
                    rejectClient = "ban";
                } else {
                    rejectionReason =
                            "^#f5f5f5;You have been temporarily banned: \n^#990000; Since " + cDt.getFormattedDate("MMMM dd, yyyy", restrictions.getDateRestricted()) +
                                    "\n^#f5f5f5;You will be automatically unbanned on: \n^#990000;" + cDt.getFormattedDate("MMMM dd, yyyy '@' HH:mm '- Server Time'", dateRestrictionExpires);
                    rejectClient = "tban";
                }
            } else if (restrictions.isIpBanned())
        }
        return null;
    }

            /* Character check or create & db create/save */
    else {
        UUID uuid = playerCharacter.getUuid();
        if (isOnline("StarNub", uuid)) {
            if (alreadyLoggedIn.getCache(uuid) != null) {
                playerDisconnectPurposely(uuid, "Already Logged In");
                alreadyLoggedIn.removeCache(uuid);
            } else {
                alreadyLoggedIn.addCache(uuid, new TimeCache());
                rejectionReason =
                        "\n^#f5f5f5;You are already logged into this starbounddata.packets.starbounddata.packets.starnub with this character. Please try again.";
                rejectClient = "online";
            }
        } else {
            playerCharacter.setLastSeen(DateTime.now());
        }
    }


            /* Unique Character & IP log check, creation & db create/save */


    /* TODO clean up player vip slots */
    int currentPlayerCount = connectedPlayers.size();
    int playerLimit = (int) ((Map) StarNub.getConfiguration().getConfiguration().get("resources")).get("player_limit");
    int vipLimit = (int) ((Map) StarNub.getConfiguration().getConfiguration().get("resources")).get("player_limit_reserved");
    int combinedCount = playerLimit + vipLimit;

    if (currentPlayerCount >= playerLimit) {
        if (hasPermission(playerSession, "starnubinternals.reserved.kick", true)) {
            outerloop:
            for (Player player : connectedPlayers.values()) {
                if (connectedPlayers.size() < combinedCount) {
                    break outerloop;
                } else if (!hasPermission(player, "starnubinternals.reserved", true) || !hasPermission(player, "starnubinternals.reserved.kick", true)) {
                    playerDisconnectPurposely(player, "RESERVED_KICK");
                }
            }
        } else if (hasPermission(playerSession, "starnubinternals.reserved", true)) {
            if (currentPlayerCount > combinedCount) {
                rejectionReason =
                        "^#f5f5f5;This starbounddata.packets.starbounddata.packets.starnub is full and no more VIP slots are available.";
                rejectClient = "server_full";
            }
        } else {
            rejectionReason =
                    "^#f5f5f5;This starbounddata.packets.starbounddata.packets.starnub is full and you do not have permission to enter.";
            rejectClient = "server_full";
        }
    }



    PendingPlayer pendingPlayer = new PendingPlayer(
            playerSession,
            cliConPacket.getSenderCTX(),
            connectingIp,
            cliConPacket.getDestinationCTX(),
            rejectClient,
            rejectionReason
    );
    PlayerEvent.eventSend_Player_Connection_Attempt(pendingPlayer);
    StarNub.getLogger().cDebPrint("StarNub", "A Player is attempting to connect to the starbounddata.packets.starbounddata.packets.starnub on IP: " + connectingIp + ".");
    addPendingConnections(pendingPlayer);
    return cliConPacket;
}
});
}
