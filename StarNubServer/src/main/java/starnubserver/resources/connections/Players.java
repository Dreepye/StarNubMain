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

package starnubserver.resources.connections;

import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import starbounddata.packets.Packet;
import starbounddata.packets.connection.ClientConnectPacket;
import starbounddata.packets.connection.ConnectResponsePacket;
import starbounddata.packets.connection.ServerDisconnectPacket;
import starnubdata.generic.DisconnectReason;
import starnubserver.Connections;
import starnubserver.StarNub;
import starnubserver.StarNubTask;
import starnubserver.cache.wrappers.PlayerCtxCacheWrapper;
import starnubserver.connections.player.StarNubProxyConnection;
import starnubserver.connections.player.character.PlayerCharacter;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.events.packet.PacketEventSubscription;
import starnubserver.resources.connections.handlers.ClientConnectHandler;
import starnubserver.resources.connections.handlers.ConnectionResponseHandler;
import starnubserver.resources.connections.handlers.ServerDisconnectHandler;
import starnubserver.resources.files.Operators;
import utilities.connectivity.ConnectionType;
import utilities.events.Priority;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Represents StarNubTask instance
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class Players extends ConcurrentHashMap<ChannelHandlerContext, PlayerSession> {

    private final Connections CONNECTIONS;
    private final PlayerCtxCacheWrapper ACCEPT_REJECT;
    private final Operators OPERATORS = Operators.getInstance();

    /**
     * Creates a new, empty map with an initial table size based on
     * the given number of elements ({@code initialCapacity}), table
     * density ({@code loadFactor}), and number of concurrently
     * updating threads ({@code concurrencyLevel}).
     *
     * @param initialCapacity  the initial capacity. The implementation
     *                         performs connections sizing to accommodate this many elements,
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
        this.ACCEPT_REJECT  = new PlayerCtxCacheWrapper("StarNub", "StarNub - Player Connection - Accept or Reject", true, 20, TimeUnit.MINUTES, 1, 60);
        registerPacketHandlers(concurrencyLevel);
        new StarNubTask("StarNub", "StarNub - Connection Lost Purge", true, 30, 30, TimeUnit.SECONDS, this::connectedPlayerLostConnectionCheck);
        new StarNubTask("StarNub", "StarNub - Player Time Update", true, 30, 30, TimeUnit.SECONDS, this::connectedPlayerPlayedTimeUpdate);
        new StarNubTask("StarNub", "StarNub - Players Online - Debug Print", true, 30, 30, TimeUnit.SECONDS, this::getOnlinePlayerListTask);
    }

    private void registerPacketHandlers(int concurrencyLevel){
        /* Connection Related */
        new PacketEventSubscription("StarNub", Priority.CRITICAL, ClientConnectPacket.class, new ClientConnectHandler(CONNECTIONS, concurrencyLevel));
        new PacketEventSubscription("StarNub", Priority.CRITICAL, ConnectResponsePacket.class, new ConnectionResponseHandler(CONNECTIONS));
        new PacketEventSubscription("StarNub", Priority.CRITICAL, ServerDisconnectPacket.class, new ServerDisconnectHandler(CONNECTIONS));
        /* Permission Related */
//        new PacketEventSubscription("StarNub", Priority.CRITICAL, DamageTileGroupPacket.class, new DamageTileGroupHandler());
    }

    public PlayerCtxCacheWrapper getACCEPT_REJECT() {
        return ACCEPT_REJECT;
    }

    public Operators getOPERATORS() {
        return OPERATORS;
    }

    /**
     * Recommended: For connections use with StarNub.
     * <p>
     * Uses: This method will go through each online player, and update the database Played time counter as well
     * as the last seen Date. This ensures that the time is accurate in case of critical crash of the starbounddata.packets.starbounddata.packets.starnubserver tool
     * or improper restart.
     * <p>
     */
    public void connectedPlayerPlayedTimeUpdate(){
        for (PlayerSession players : this.values()) {
            players.setEndTimeUtc();
            PlayerCharacter playerCharacter = players.getPlayerCharacter();
            playerCharacter.updatePlayedTimeLastSeen();
        }
    }

    /**
     * Recommended: For connections use with StarNub.
     * <p>
     * Uses: This method will go through each online player, and insure they are still connect.
     * If not it will send a player lost starbounddata.packets.connection message and remove them from the database.
     * <p>
     */
    public void connectedPlayerLostConnectionCheck() {
        for (Map.Entry<ChannelHandlerContext, PlayerSession> playerEntry : this.entrySet()){
            PlayerSession playerSession = playerEntry.getValue();
            if (!playerSession.getCONNECTION().isConnected()){
                playerSession.disconnectReason(DisconnectReason.CONNECTION_LOST);
            }
        }
    }

    /**
     * Recommended: For connections use with StarNub.
     * <p>
     * Uses: This method will print all of the online players out in a debug message if turned on
     * <p>
     */
    private void getOnlinePlayerListTask() {
        StarNub.getLogger().cDebPrint("StarNub", getOnlinePlayersNameList("StarNub", true, true));
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method is used to pull a player record using any type of
     * parameter. (Packet, Uuid, IP, NAME, clientCTX, Starbound ID, StarNub ID)
     * <p>
     * @param playerIdentifier Object that represent a player and it can take many forms
     * @return Player which represents the player that was retrieved by the provided playerIdentifier
     */
    public PlayerSession getOnlinePlayerByAnyIdentifier(Object playerIdentifier) {
        if (playerIdentifier instanceof PlayerSession) {
            return (PlayerSession) playerIdentifier;
        } else if (playerIdentifier instanceof Packet){
            return playerByPacket((Packet) playerIdentifier);
        } else if (playerIdentifier instanceof String) {
            String identifierString = (String) playerIdentifier;
            if (isStarNubId(identifierString)) {
                return playerByStarNubClientID(Integer.parseInt(identifierString.replaceAll("[sS]", "")));
            } else if (StringUtils.countMatches(identifierString, "-") >= 4) {
                return playerByUUID(UUID.fromString(identifierString));
            } else if (StringUtils.countMatches(identifierString, ".") == 3) {
                try {
                    return playerByIP(InetAddress.getByName(identifierString));
                } catch (UnknownHostException e) {
                    return null;
                }
            } else {
                try {
                    return playerByStarboundClientID(Integer.parseInt(identifierString));
                } catch (Exception e) {
                    return playerByName(identifierString);
                }
            }
        } else if (playerIdentifier instanceof UUID) {
            return playerByUUID((UUID) playerIdentifier);
        } else if (playerIdentifier instanceof InetAddress) {
            return playerByIP((InetAddress) playerIdentifier);
        } else if (playerIdentifier instanceof ChannelHandlerContext) {
            return playerByCTX((ChannelHandlerContext) playerIdentifier);
        } else if (playerIdentifier instanceof Integer){
            return playerByStarboundClientID((int) playerIdentifier);
        } else {
            return null;
        }
    }

    private PlayerSession playerByPacket(Packet packet){
        return this.values().stream()
                   .filter(p -> p.getCONNECTION().getCLIENT_CTX() == packet.getSENDER_CTX() || p.getCONNECTION().getCLIENT_CTX() == packet.getDESTINATION_CTX())
                   .findAny().orElse(null);
    }

    /**
     * Recommended: For connections use with StarNub.
     * <p>
     * Uses: This method is used to check is a string represents a starnubserver id.
     * <p>
     * @param s String representing the StarNub Id
     * @return boolean if is a starnubserver id or not
     */
    private boolean isStarNubId(String s){
        if (!s.endsWith("s") && !s.endsWith("S")) {
            return false;
        }
        s = s.replaceAll("[sS]","");
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Recommended: For connections use with StarNub.
     * <p>
     * Uses: This method is used to get a Player by a uuid from getOnlinePlayerByAnyIdentifier() method.
     * <p>
     * @param uuid uuid which represents the playerIdentifier
     * @return Player which represents the player that was retrieved by the provided uuid
     */
    private PlayerSession playerByUUID(UUID uuid) {
        for (PlayerSession playerSessionSession : this.values()){
            if (playerSessionSession.getPlayerCharacter().getUuid().equals(uuid)) {
                return playerSessionSession;
            }
        }
        return null;
    }

    /**
     * Recommended: For connections use with StarNub.
     * <p>
     * Uses: This method is used to get a Player by a InetAddress from getOnlinePlayerByAnyIdentifier() method.
     * <p>
     * @param ip InetAddress which represents the playerIdentifier
     * @return Player which represents the player that was retrieved by the provided InetAddress
     */
    private PlayerSession playerByIP(InetAddress ip) {
        for (PlayerSession playerSessionSession : this.values()){
            if (playerSessionSession.getCONNECTION().getClientIP().equals(ip)) {
                return playerSessionSession;
            }
        }
        return null;
    }

    /**
     * Recommended: For connections use with StarNub.
     * <p>
     * Uses: This method is used to get a Player by a name in String form which is not as reliable as another identifier might be
     * from getOnlinePlayerByAnyIdentifier() method.
     * <p>
     * @param identifierString String which represents the playerIdentifier
     * @return Player which represents the player that was retrieved by the provided String
     */
    private PlayerSession playerByName(String identifierString) {
        for (PlayerSession playerSessionSession : this.values()){
            if (playerSessionSession.getPlayerCharacter().getName().equalsIgnoreCase(identifierString) || playerSessionSession.getPlayerCharacter().getCleanName().equalsIgnoreCase(identifierString) ||
                    playerSessionSession.getNickName().equalsIgnoreCase(identifierString) || playerSessionSession.getCleanNickName().equalsIgnoreCase(identifierString) ||
                    playerSessionSession.getGameName().equalsIgnoreCase(identifierString)) {
                return playerSessionSession;
            }
        }
        return null;
    }

    /**
     * Recommended: For connections use with StarNub.
     * <p>
     * Uses: This method is used to get a Player by a Starbound ID from getOnlinePlayerByAnyIdentifier() method, this
     * method is reliable for sessions, but keep in mind this Starbound ID changes when a player disconnects and reconnects
     * to a Starbound starbounddata.packets.starbounddata.packets.starnubserver.
     * <p>
     * @param starboundClientId Integer which represents the playerIdentifier
     * @return Player which represents the player that was retrieved by the provided Integer
     */
    private PlayerSession playerByStarboundClientID(int starboundClientId) {
        for (PlayerSession playerSessionSession : this.values()){
            if (playerSessionSession.getStarboundClientId() == starboundClientId) {
                return playerSessionSession;
            }
        }
        return null;
    }

    /**
     * Recommended: For connections use with StarNub.
     * <p>
     * Uses: This method is used to get a Player by a StarNub ID from getOnlinePlayerByAnyIdentifier() method, this
     * StarNub ID is tied to the players account they use to log into for groups, permissions, ect.
     * <p>
     * @param starnubClientId Integer which represents the playerIdentifier
     * @return Player which represents the player that was retrieved by the provided Integer
     */
    private PlayerSession playerByStarNubClientID(int starnubClientId) {
        for (PlayerSession playerSessionSession : this.values()){
            if (playerSessionSession.getPlayerCharacter().getAccount().getStarnubId() == starnubClientId) {
                return playerSessionSession;
            }
        }
        return null;
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For connections use with StarNub.
     * <p>
     * Uses: This method is used to get a Player by a ChannelHandlerContext from getOnlinePlayerByAnyIdentifier() method.
     * <p>
     * @param ctx ChannelHandlerContext which represents the playerIdentifier
     * @return Player which represents the player that was retrieved by the provided ChannelHandlerContext
     */
    private PlayerSession playerByCTX(ChannelHandlerContext ctx) {
        for (PlayerSession playerSessionSession : this.values()){
            if (playerSessionSession.getCONNECTION().getCLIENT_CTX() == ctx
                    || (playerSessionSession.getCONNECTION_TYPE() == ConnectionType.PROXY_IN_GAME && ((StarNubProxyConnection) playerSessionSession.getCONNECTION()).getSERVER_CTX() == ctx)) {
                return playerSessionSession;
            }
        }
        return null;
    }

    public boolean isOnline(Object sender, Object playerIdentifier) {
        PlayerSession playerSessionSession = getOnlinePlayerByAnyIdentifier(playerIdentifier);
        return playerSessionSession != null && canSeePlayer(sender, playerSessionSession);
    }

    public boolean canSeePlayerAnyIdentifier(Object sender, Object playerIdentifier){
        PlayerSession playerSessionSession = getOnlinePlayerByAnyIdentifier(playerIdentifier);
        return playerSessionSession != null && canSeePlayer(sender, playerSessionSession);
    }

    public ArrayList<Boolean> canSeePlayerIsHiddenCanSeeAnyIdentifier(Object sender, Object playerIdentifier){
        PlayerSession playerSessionSession = getOnlinePlayerByAnyIdentifier(playerIdentifier);
        if (playerSessionSession == null) {
            return canSeeHashSetBuilder(false, false);
        }
        return canSeePlayerIsHiddenCanSee(sender, playerSessionSession);
    }

    public boolean canSeePlayer(Object sender, PlayerSession playerSessionSession){
        if (sender instanceof PlayerSession) {
            PlayerSession senderSession = getOnlinePlayerByAnyIdentifier(sender);
            if (senderSession.hasPermission("starnub.bypass.appear_offline", true)) {
                return true;
            } else if (playerSessionSession.getPlayerCharacter().getAccount() != null) {
                return !playerSessionSession.getPlayerCharacter().getAccount().getAccountSettings().isAppearOffline();
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public ArrayList<Boolean> canSeePlayerIsHiddenCanSee(Object senderSession, PlayerSession playerSessionSession) {
        boolean appearOffline = false;
        if (playerSessionSession.getPlayerCharacter().getAccount() != null) {
            appearOffline = playerSessionSession.getPlayerCharacter().getAccount().getAccountSettings().isAppearOffline();
        }

        if (senderSession instanceof PlayerSession) {
            PlayerSession sender = getOnlinePlayerByAnyIdentifier(senderSession);
            boolean canSeePlayer = true;
            if (appearOffline) {
                canSeePlayer = sender.hasPermission("starnubserver.bypass.appearoffline", true);
            }
            return canSeeHashSetBuilder(appearOffline, canSeePlayer);
        } else {
            return canSeeHashSetBuilder(appearOffline, true);
        }
    }

    private ArrayList<Boolean> canSeeHashSetBuilder(boolean isHidden, boolean canSeeHidden){
        ArrayList<Boolean> canSeePlayer = new ArrayList<>();
        canSeePlayer.add(isHidden);
        canSeePlayer.add(canSeeHidden);
        return canSeePlayer;
    }

    public String nameBuild(PlayerSession playerSession){

        return null;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will return a string of all the online players, optionally with Starbound and Starnub IDs.
     * This method is temporary and highly unofficial. This will later be refined.
     * <p>
     * @param showStarboundId boolean to show Starbound Ids
     * @param showStarNubId boolean to show Starnub Ids
     * @return String with all of the online players
     */
    public String getOnlinePlayersNameList(Object sender, boolean showStarboundId, boolean showStarNubId) {
        boolean canSee = false;
        if (sender instanceof PlayerSession){
            canSee = ((PlayerSession) sender).hasPermission("starnub.bypass.appear_offline", true);
        }


        String built = "";
        HashSet<String> playersOnline;

        final boolean finalCanSee = canSee;
        playersOnline = this.values().stream()
                .sorted((p1, p2) -> p1.getCleanNickName().compareTo(p2.getCleanNickName()))
                .map(
                        p1 -> p1.getNickName() + "( "
                                +
                                (showStarboundId
                                        && showStarNubId
                                        && (p1.getPlayerCharacter().getAccount() != null) ? p1.getStarboundClientId() + "/" + p1.getPlayerCharacter().getAccount().getStarnubId()
                                        : showStarboundId ? p1.getStarboundClientId()
                                        : showStarNubId
                                        && (p1.getPlayerCharacter().getAccount() != null) ? p1.getPlayerCharacter().getAccount().getStarnubId() : "")
                                +
                                ((p1.getPlayerCharacter().getAccount() != null && p1.getPlayerCharacter().getAccount().getAccountSettings().isAppearOffline()) ? " )(H)" : ")")
                )

                .collect(Collectors.toCollection(LinkedHashSet::new));

//        .filter(p -> !finalCanSee ? (p.getPlayerCharacter().getAccount() != null && !p.getPlayerCharacter().getAccount().getAccountSettings().isAppearOffline()) : null)



//        //
//        List<String> sortedPlayers = new ArrayList<String>();
//        String tokenBaseShowStarboundId = "";
//        String tokenBaseShowStarNubId = "";
//        if (showStarboundId) {
//            tokenBaseShowStarboundId = "StarboundID";
//        }
//        if (showStarNubId) {
//            tokenBaseShowStarNubId = "StarNubID";
//        }
//        String combinedToken;
//        if (!tokenBaseShowStarboundId.isEmpty() && !tokenBaseShowStarNubId.isEmpty()) {
//            combinedToken = tokenBaseShowStarboundId+"/"+tokenBaseShowStarNubId;
//        } else {
//            combinedToken = tokenBaseShowStarboundId+tokenBaseShowStarNubId;
//        }
//        combinedToken = "("+combinedToken+")";
//        String playersOnline = "Players Online: "+ this.size() +". NickName "+combinedToken+": ";
//        String token;
//        String tokenShowStarboundId = "";
//        String tokenShowStarNubId = "";
//        for (PlayerSession playerSessionSession : this.values()) {
//            ArrayList<Boolean> appearance = canSeePlayerIsHiddenCanSee(sender, playerSessionSession);
//            String hiddenToken = "";
//            if (appearance.get(0)) {
//                hiddenToken = " (H)";
//            }
//
//            if (appearance.get(1)) {
//                if (showStarboundId) {
//                    tokenShowStarboundId = Long.toString(playerSessionSession.getStarboundClientId());
//                }
//                if (showStarNubId) {
//                    if (playerSessionSession.getPlayerCharacter().getAccount().getStarnubId() > 0) {
//                        tokenShowStarNubId = Integer.toString(playerSessionSession.getPlayerCharacter().getAccount().getStarnubId()) + "S";
//                    } else {
//                        tokenShowStarNubId = "NA";
//                    }
//                }
//
//                if (!tokenShowStarboundId.isEmpty() && !tokenShowStarNubId.isEmpty()) {
//                    token = tokenShowStarboundId + "/" + tokenShowStarNubId;
//                } else {
//                    token = tokenShowStarboundId + tokenShowStarNubId;
//                }
//                token = "(" + token + ")";
//                sortedPlayers.add(playerSessionSession.getCleanNickName() + hiddenToken + " " + token);
//            }
//        }
//        Collections.sort(sortedPlayers);
//        for (String player : sortedPlayers) {
//            playersOnline = playersOnline + player+", ";
//        }
//        try {
//            playersOnline = playersOnline.substring(0, playersOnline.lastIndexOf(",")) + ".";
//        } catch (StringIndexOutOfBoundsException e) {
//            /* Do nothing no players are online */
//        }
        return String.valueOf(playersOnline);
    }

}
