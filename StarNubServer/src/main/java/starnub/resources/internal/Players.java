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

package starnub.resources.internal;

import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import starbounddata.packets.connection.ClientConnectPacket;
import starbounddata.packets.connection.ConnectResponsePacket;
import starbounddata.packets.connection.ServerDisconnectPacket;
import starnub.Connections;
import starnub.StarNub;
import starnub.StarNubTask;
import starnub.cache.wrappers.PlayerCtxCacheWrapper;
import starnub.connections.player.character.PlayerCharacter;
import starnub.connections.player.session.Player;
import starnub.events.packet.PacketEventSubscription;
import starnub.resources.Operators;
import starnub.resources.internal.handlers.ClientConnectHandler;
import starnub.resources.internal.handlers.ConnectionResponseHandler;
import starnub.resources.internal.handlers.ServerDisconnectHandler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Represents StarNubTask instance
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class Players extends ConcurrentHashMap<ChannelHandlerContext, Player> {

    private final Connections CONNECTIONS;
    private final PlayerCtxCacheWrapper ACCEPT_REJECT;
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
        this.ACCEPT_REJECT  = new PlayerCtxCacheWrapper("StarNub", "StarNub - Player Connection - Accept or Reject", true, StarNub.getTaskManager(), 20, concurrencyLevel, TimeUnit.MINUTES, 10, 60);
        new PacketEventSubscription("StarNub", ClientConnectPacket.class, true, new ClientConnectHandler(CONNECTIONS, concurrencyLevel));
        new PacketEventSubscription("StarNub", ConnectResponsePacket.class, true, new ConnectionResponseHandler(CONNECTIONS));
        new PacketEventSubscription("StarNub", ServerDisconnectPacket.class, true, new ServerDisconnectHandler(CONNECTIONS));
        new StarNubTask("StarNub", "StarNub - Connection Lost Purge", true, 30, 30, TimeUnit.SECONDS, this::connectedPlayerLostConnectionCheck);
        new StarNubTask("StarNub", "StarNub - Player Time Update", true, 30, 30, TimeUnit.SECONDS, this::connectedPlayerPlayedTimeUpdate);
        new StarNubTask("StarNub", "StarNub - Players Online - Debug Print", true, 30, 30, TimeUnit.SECONDS, this::getOnlinePlayerListTask);

    }

    public PlayerCtxCacheWrapper getACCEPT_REJECT() {
        return ACCEPT_REJECT;
    }

    public Operators getOPERATORS() {
        return OPERATORS;
    }

    /**
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method will go through each online player, and update the database Played time counter as well
     * as the last seen Date. This ensures that the time is accurate in case of critical crash of the starbounddata.packets.starbounddata.packets.starnub tool
     * or improper restart.
     * <p>
     */
    public void connectedPlayerPlayedTimeUpdate(){
        for (Player players : this.values()) {
            PlayerCharacter playerCharacter = players.getPlayerCharacter();
            playerCharacter.updatePlayedTimeLastSeen();
        }
    }

    /**
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method will go through each online player, and insure they are still connect.
     * If not it will send a player lost starbounddata.packets.connection message and remove them from the database.
     * <p>
     */
    public void connectedPlayerLostConnectionCheck() {
        for (Map.Entry<ChannelHandlerContext, Player> playerEntry : this.entrySet()){
            Player player = playerEntry.getValue();
            if (!player.isConnected()){
                player.disconnectReason("Connection_Lost");
            }
        }
    }

    /**
     * Recommended: For internal use with StarNub.
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
     * parameter. (uuid, IP, NAME, clientCTX, Starbound ID, StarNub ID)
     * <p>
     * @param playerIdentifier Object that represent a player and it can take many forms
     * @return Player which represents the player that was retrieved by the provided playerIdentifier
     */
    public Player getOnlinePlayerByAnyIdentifier(Object playerIdentifier) {
        if (playerIdentifier instanceof Player) {
            return (Player) playerIdentifier;
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

    /**
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method is used to check is a string represents a starnub id.
     * <p>
     * @param s String representing the StarNub Id
     * @return boolean if is a starnub id or not
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
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method is used to get a Player by a uuid from getOnlinePlayerByAnyIdentifier() method.
     * <p>
     * @param uuid uuid which represents the playerIdentifier
     * @return Player which represents the player that was retrieved by the provided uuid
     */
    private Player playerByUUID(UUID uuid) {
        for (Player playerSession : this.values()){
            if (playerSession.getPlayerCharacter().getUuid().equals(uuid)) {
                return playerSession;
            }
        }
        return null;
    }

    /**
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method is used to get a Player by a InetAddress from getOnlinePlayerByAnyIdentifier() method.
     * <p>
     * @param ip InetAddress which represents the playerIdentifier
     * @return Player which represents the player that was retrieved by the provided InetAddress
     */
    private Player playerByIP(InetAddress ip) {
        for (Player playerSession : this.values()){
            if (playerSession.getClientIP().equals(ip)) {
                return playerSession;
            }
        }
        return null;
    }

    /**
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method is used to get a Player by a name in String form which is not as reliable as another identifier might be
     * from getOnlinePlayerByAnyIdentifier() method.
     * <p>
     * @param identifierString String which represents the playerIdentifier
     * @return Player which represents the player that was retrieved by the provided String
     */
    private Player playerByName(String identifierString) {
        for (Player playerSession : this.values()){
            if (playerSession.getPlayerCharacter().getName().equalsIgnoreCase(identifierString) || playerSession.getPlayerCharacter().getCleanName().equalsIgnoreCase(identifierString) ||
                    playerSession.getNickName().equalsIgnoreCase(identifierString) || playerSession.getCleanNickName().equalsIgnoreCase(identifierString) ||
                    playerSession.getGameName().equalsIgnoreCase(identifierString)) {
                return playerSession;
            }
        }
        return null;
    }

    /**
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method is used to get a Player by a Starbound ID from getOnlinePlayerByAnyIdentifier() method, this
     * method is reliable for sessions, but keep in mind this Starbound ID changes when a player disconnects and reconnects
     * to a Starbound starbounddata.packets.starbounddata.packets.starnub.
     * <p>
     * @param starboundClientId Integer which represents the playerIdentifier
     * @return Player which represents the player that was retrieved by the provided Integer
     */
    private Player playerByStarboundClientID(int starboundClientId) {
        for (Player playerSession : this.values()){
            if (playerSession.getStarboundClientId() == starboundClientId) {
                return playerSession;
            }
        }
        return null;
    }

    /**
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method is used to get a Player by a StarNub ID from getOnlinePlayerByAnyIdentifier() method, this
     * StarNub ID is tied to the players account they use to log into for groups, permissions, ect.
     * <p>
     * @param starnubClientId Integer which represents the playerIdentifier
     * @return Player which represents the player that was retrieved by the provided Integer
     */
    private Player playerByStarNubClientID(int starnubClientId) {
        for (Player playerSession : this.values()){
            if (playerSession.getAccount() == starnubClientId) {
                return playerSession;
            }
        }
        return null;
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method is used to get a Player by a ChannelHandlerContext from getOnlinePlayerByAnyIdentifier() method.
     * <p>
     * @param ctx ChannelHandlerContext which represents the playerIdentifier
     * @return Player which represents the player that was retrieved by the provided ChannelHandlerContext
     */
    private Player playerByCTX(ChannelHandlerContext ctx) {
        for (Player playerSession : this.values()){
            if (playerSession.getCLIENT_CTX() == ctx || playerSession.getSERVER_CTX() == ctx) {
                return playerSession;
            }
        }
        return null;
    }

    public boolean isOnline(Object sender, Object playerIdentifier) {
        Player playerSession = getOnlinePlayerByAnyIdentifier(playerIdentifier);
        return playerSession != null && canSeePlayer(sender, playerSession);
    }

    public boolean canSeePlayerAnyIdentifier(Object sender, Object playerIdentifier){
        Player playerSession = getOnlinePlayerByAnyIdentifier(playerIdentifier);
        return playerSession != null && canSeePlayer(sender, playerSession);
    }

    public ArrayList<Boolean> canSeePlayerIsHiddenCanSeeAnyIdentifier(Object sender, Object playerIdentifier){
        Player playerSession = getOnlinePlayerByAnyIdentifier(playerIdentifier);
        if (playerSession == null) {
            return canSeeHashSetBuilder(false, false);
        }
        return canSeePlayerIsHiddenCanSee(sender, playerSession);
    }

    public boolean canSeePlayer(Object sender, Player playerSession){
        if (sender instanceof Player) {
            Player senderSession = getOnlinePlayerByAnyIdentifier(sender);
            if (senderSession.hasPermission("starnub.bypass.appearoffline", true)) {
                return true;
            } else if (playerSession.getPlayerCharacter().getAccount() != null) {
                return !playerSession.getPlayerCharacter().getAccount().getAccountSettings().isAppearOffline();
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public ArrayList<Boolean> canSeePlayerIsHiddenCanSee(Object senderSession, Player playerSession) {
        boolean appearOffline = false;
        if (playerSession.getPlayerCharacter().getAccount() != null) {
            appearOffline = playerSession.getPlayerCharacter().getAccount().getAccountSettings().isAppearOffline();
        }

        if (senderSession instanceof Player) {
            Player sender = getOnlinePlayerByAnyIdentifier(senderSession);
            boolean canSeePlayer = true;
            if (appearOffline) {
                canSeePlayer = sender.hasPermission("starnub.bypass.appearoffline", true);
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
        List<String> sortedPlayers = new ArrayList<String>();
        String tokenBaseShowStarboundId = "";
        String tokenBaseShowStarNubId = "";
        if (showStarboundId) {
            tokenBaseShowStarboundId = "StarboundID";
        }
        if (showStarNubId) {
            tokenBaseShowStarNubId = "StarNubID";
        }
        String combinedToken;
        if (!tokenBaseShowStarboundId.isEmpty() && !tokenBaseShowStarNubId.isEmpty()) {
            combinedToken = tokenBaseShowStarboundId+"/"+tokenBaseShowStarNubId;
        } else {
            combinedToken = tokenBaseShowStarboundId+tokenBaseShowStarNubId;
        }
        combinedToken = "("+combinedToken+")";
        String playersOnline = "Players Online: "+ this.size() +". NickName "+combinedToken+": ";
        String token;
        String tokenShowStarboundId = "";
        String tokenShowStarNubId = "";
        for (Player playerSession : this.values()) {
            ArrayList<Boolean> appearance = canSeePlayerIsHiddenCanSee(sender, playerSession);
            String hiddenToken = "";
            if (appearance.get(0)) {
                hiddenToken = " (H)";
            }

            if (appearance.get(1)) {
                if (showStarboundId) {
                    tokenShowStarboundId = Long.toString(playerSession.getStarboundClientId());
                }
                if (showStarNubId) {
                    if (playerSession.getAccount() > 0) {
                        tokenShowStarNubId = Integer.toString(playerSession.getAccount()) + "S";
                    } else {
                        tokenShowStarNubId = "NA";
                    }
                }

                if (!tokenShowStarboundId.isEmpty() && !tokenShowStarNubId.isEmpty()) {
                    token = tokenShowStarboundId + "/" + tokenShowStarNubId;
                } else {
                    token = tokenShowStarboundId + tokenShowStarNubId;
                }
                token = "(" + token + ")";
                sortedPlayers.add(playerSession.getCleanNickName() + hiddenToken + " " + token);
            }
        }
        Collections.sort(sortedPlayers);
        for (String player : sortedPlayers) {
            playersOnline = playersOnline + player+", ";
        }
        try {
            playersOnline = playersOnline.substring(0, playersOnline.lastIndexOf(",")) + ".";
        } catch (StringIndexOutOfBoundsException e) {
            /* Do nothing no players are online */
        }
        return playersOnline;
    }

}
