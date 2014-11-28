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

package starnub.resources.internalmaps;

import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import starbounddata.packets.Packet;
import starbounddata.packets.connection.ClientConnectPacket;
import starnub.Connections;
import starnub.Server;
import starnub.StarNub;
import starnub.cache.wrappers.PlayerCtxCacheWrapper;
import starnub.connections.player.StarNubProxyConnection;
import starnub.connections.player.character.CharacterIP;
import starnub.connections.player.session.Player;
import starnub.connections.player.session.Restrictions;
import starnub.database.DatabaseTables;
import starnub.events.events.PlayerEvent;
import starnub.events.packet.PacketEventHandler;
import starnub.events.packet.PacketEventSubscription;
import starnub.resources.Operators;
import starnub.senders.NameBuilder;
import utilities.time.DateAndTimes;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents StarNubTask instance
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class Players extends ConcurrentHashMap<ChannelHandlerContext, Player> {

    private final Connections CONNECTIONS;
    private final PlayerCtxCacheWrapper ACCEPT_REJECT = new PlayerCtxCacheWrapper("StarNub", "StarNub - Player Connection - Accept or Reject", true, StarNub.getTaskManager(),)//Elements, expected Threads
    private final Operators OPERATORS = new Operators(StarNub.getResourceManager().getStarnubResources());

    /**
     * Creates a new, empty map with an initial table size based on
     * the given number of elements ({@code initialCapacity}), table
     * density ({@code loadFactor}), and number of concurrently
     * updating threads ({@code concurrencyLevel}).
     *
     * @param initialCapacity  the initial capacity. The implementation
     *                         performs internal sizing to accommodate this many elements,
     *                         given the specified load factor.
     * @param loadFactor       the load factor (table density) for
     *                         establishing the initial table size
     * @param concurrencyLevel the estimated number of concurrently
     *                         updating threads. The implementation may use this value as
     *                         a sizing hint.
     * @throws IllegalArgumentException if the initial capacity is
     *                                            negative or the load factor or concurrencyLevel are
     *                                            nonpositive
     */
    public Players(Connections CONNECTIONS, int initialCapacity, float loadFactor, int concurrencyLevel) {
        super(initialCapacity, loadFactor, concurrencyLevel);
        this.CONNECTIONS = CONNECTIONS;
    }
    private void playerConnect(){
        new PacketEventSubscription("StarNub", ClientConnectPacket.class, true, new PacketEventHandler() {
            @Override
            public Packet onEvent(Packet eventData) {
                ClientConnectPacket cliConPacket = (ClientConnectPacket) eventData;

                CONNECTIONS.getOPEN_SOCKETS().remove(cliConPacket.getSENDER_CTX());
                CONNECTIONS.getOPEN_SOCKETS().remove(cliConPacket.getDESTINATION_CTX());
                StarNubProxyConnection starNubProxyConnection = (StarNubProxyConnection) CONNECTIONS.getOPEN_CONNECTIONS().get(cliConPacket.getDESTINATION_CTX());

                String playerName = cliConPacket.getPlayerName();
                UUID playerUUID = cliConPacket.getUuid();
                String playerIP = starNubProxyConnection.getClientIP().toString();

                Player player = new Player(starNubProxyConnection, playerName, playerUUID, playerIP);








                return cliConPacket;
            }
        });


        Server cServer = StarNub.getServer();
        NameBuilder cMsgSen = StarNub.getMessageSender();
        Configuration conf = StarNub.getConfiguration();
        Map confSI = (Map) conf.getConfiguration().get("starbounddata.packets.starbounddata.packets.starnub info");
        DatabaseTables cDb = StarNub.getDatabaseTables();
        DateAndTimes cDt = StarNub.getDateAndTimes();
        ChatFilter cChatF = cServer.getServerChat().getChatFilter();

        InetAddress connectingIp = ((InetSocketAddress) cliConPacket.getSenderCTX().channel().remoteAddress()).getAddress();
        UUID connectingUuid = cliConPacket.getUUID();
        Restrictions restrictions = getPlayerRestrictionLogOn(connectingIp, connectingUuid);

        String header = "^#f5f5f5;-== " + confSI.get("server_name") + " ==-\n" +
                "^#f5f5f5;-= Powered by StarNub.org =- \n\n";
        String footer = "\n\n^#f5f5f5;For information visit: ^#990000;" + StringUtils.remove((String) confSI.get("server_url"), "http://");
        String rejectionReason = "";

        String rejectClient = null;
        if (StarNub.getServer().getOLDStarboundManager().getStarboundStatus().isRestarting()) {
            rejectionReason =
                    "\n^#f5f5f5;Starbound is restarting, please come back in a few minutes.\n";
            rejectClient = "restarting";
        } else if ((boolean) ((Map) conf.getConfiguration().get("starnub settings")).get("whitelisted")) {
            if (!StarNub.getServer().getConnectionss().onWhitelist(connectingIp) ||
                    !StarNub.getServer().getConnectionss().onWhitelist(connectingUuid)) {
                rejectionReason =
                        "\n^#f5f5f5;This starbounddata.packets.starbounddata.packets.starnub is whitelisted.\n";
                rejectClient = "whitelist";
            }
        } else if (restrictions != null) {
            if (restrictions.isBanned()) {
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
            }
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

                /* Account last login time and ID set & db create/save */
        int accountId = 0;
        if (playerCharacter.getAccount() != null) {
            accountId = playerCharacter.getAccount().getStarnubId();
            playerCharacter.initialLogInProcessing();
            cDb.getAccounts().update(playerCharacter.getAccount());
            //PLACE_HOLDER - Account Log-In Event
        }
        cDb.getCharacters().createOrUpdate(playerCharacter);

        Player playerSession = new Player(cliConPacket.getSenderCTX(), cliConPacket.getDestinationCTX(), connectingIp, playerCharacter, null, accountId, opsList.contains(connectingUuid));

        //TODO ignore list This currently just sets it to not have a null list for the starbounddata.packets.chat rooms
        playerSession.setDoNotSendMessageList();

        cDb.getPlayerSessionLog().create(playerSession);
        //PLACE_HOLDER - Session event

            /* Unique Character & IP log check, creation & db create/save */
        CharacterIP characterIP = new CharacterIP(playerCharacter, playerSession.getSessionIp());
        if (!cDb.getCharacterIPLog().isCharacterIDAndIPComboRecorded(characterIP)) {
            cDb.getCharacterIPLog().create(characterIP);
        }

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

        if (rejectClient != null) {
            rejectionReason = header + rejectionReason + footer;
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
