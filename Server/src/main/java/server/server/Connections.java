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

package server.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.codehome.utilities.files.AppendToFile;
import org.codehome.utilities.files.FileToList;
import org.codehome.utilities.files.RemoveLineFromFile;
import org.joda.time.DateTime;
import server.Configuration;
import server.DateAndTimes;
import server.StarNub;
import server.cache.objects.TimeCache;
import server.cache.wrappers.PlayerUUIDCacheWrapper;
import server.connectedentities.player.account.Account;
import server.connectedentities.player.character.Character;
import server.connectedentities.player.character.CharacterIP;
import server.connectedentities.player.groups.GroupSync;
import server.connectedentities.player.session.PendingPlayer;
import server.connectedentities.player.session.Player;
import server.connectedentities.player.session.Restrictions;
import server.database.DatabaseTables;
import server.eventsrouter.events.ObjectEvent;
import server.eventsrouter.events.PlayerEvent;
import server.eventsrouter.events.StarNubEvent;
import server.eventsrouter.handlers.PacketEventHandler;
import server.eventsrouter.handlers.StarNubEventHandler;
import server.logger.MultiOutputLogger;
import server.senders.MessageSender;
import server.server.chat.ChatFilter;
import starbounddata.chat.ChatSendChannel;
import server.server.packets.Packet;
import server.server.packets.connection.ClientConnectPacket;
import server.server.packets.connection.ClientDisconnectRequestPacket;
import server.server.packets.connection.ConnectResponsePacket;
import server.server.packets.connection.ServerDisconnectPacket;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Represents StarNub's Connections enum singleton.
 * <p>
 * The data in this represents a pending player starbounddata.packets.connection, connected player, whitelist and
 * restricted UUID's and IP's.
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public enum Connections {
    INSTANCE;

    @Getter
    private final static Object WHITE_LIST_LOCK = new Object();
    @Getter
    private final static Object OPS_LIST_LOCK = new Object();
    @Getter
    private final static Object CACHED_DATA_LOCK = new Object();
    @Getter
    private static ConcurrentHashMap<ChannelHandlerContext, Long> openSockets;
    @Getter
    private static ConcurrentHashMap<ChannelHandlerContext, PendingPlayer> pendingConnections;
    @Getter
    private static ConcurrentHashMap<ChannelHandlerContext, Player> connectedPlayers;
    @Getter
    private static Set<Object> whitelist;
    @Getter
    private static Set<UUID> opsList;
    @Getter
    private static ConcurrentHashMap<Object, Restrictions> restrictedIPsUUIDs;
    @Getter
//    private static Set<CachedData> cachedData;
    private volatile static GroupSync groupSync;
    @Getter
    private static PlayerUUIDCacheWrapper alreadyLoggedIn;


    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This is the core method that sets this enum singleton. Running this method sets
     * the values for pending connections, connected players, restricted ips, restricted uuids
     * and the starbounddata.packets.starbounddata.packets.server whitelist if they are null. Else they cannot be reset. After this we
     * will load the whitelist as well as the restricted ip/uuid lists.
     * <p>
     */
    @SuppressWarnings("unchecked")
    public void setConnectionsData() {
        if (openSockets == null) {
            openSockets = new ConcurrentHashMap<ChannelHandlerContext, Long>(10);
        }
        if (pendingConnections == null) {
            pendingConnections = new ConcurrentHashMap<ChannelHandlerContext, PendingPlayer>(10);
        }
        if (connectedPlayers == null) {
            connectedPlayers = new ConcurrentHashMap<ChannelHandlerContext, Player>(
                    (int) ((Map) StarNub.getConfiguration().getConfiguration().get("resources")).get("player_limit")
                            + (int) ((Map) StarNub.getConfiguration().getConfiguration().get("resources")).get("player_limit_reserved")
                            + 2
            );
        }
        if (restrictedIPsUUIDs == null) {
            restrictedIPsUUIDs = new ConcurrentHashMap<Object, Restrictions>();
        }
        if (whitelist == null ) {
            HashSet hashSet = new HashSet();
            whitelist = Collections.synchronizedSet(hashSet);
        }
        if (opsList == null ) {
            HashSet hashSet = new HashSet();
            opsList = Collections.synchronizedSet(hashSet);
        }
//        if (cachedData == null){
//            HashSet hashSet = new HashSet();
//            cachedData = Collections.synchronizedSet(hashSet);
//        }
        if (alreadyLoggedIn == null){
            alreadyLoggedIn = new PlayerUUIDCacheWrapper("StarNub", "StarNub - Character Already Online", true, 10, false);
        }
        if (groupSync == null){
            groupSync = GroupSync.INSTANCE;
            groupSync.groupSynchronizeFileToDB("StarNub");
            groupSync.setNoAccountGroup();
        }
        whitelistLoad();
        opsListLoad();
        restrictionLoad();
        eventListenerPacketEvent();
        eventListenerPlayerConnectionAttempt();
        eventListenerPlayerConnectionFinal();
        eventListenerPlayerDisconnect();
        eventListenerPlayerConnectionCleanUp();

    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method will determine what type of identifier the calling means to use. This method will
     * then cast it to that object and return it as an object to be used else where in reflection methods. If
     * the String cannot be cast to a specific object then a string will be return which indicates what
     * cast failed and or a critical failure overall. This method will only return a UUID, InetAddress, StarNubId or
     * a String which would represent a player name or something else.
     * <p>
     */
    public Object identifierCast(String identifierString) {
        if (StringUtils.countMatches(identifierString, "-") >= 4) {
            try {
                return UUID.fromString(identifierString);
            } catch (IllegalArgumentException e) {
                return "uuid";
            }
        } else if (StringUtils.countMatches(identifierString, ".") == 3) {
            try {
                return InetAddress.getByName(identifierString);
            } catch (UnknownHostException e) {
                return "ip";
            }
        } else if (isStarNubId(identifierString)){
            try {
                return playerByStarNubClientID(Integer.parseInt(identifierString.replaceAll("[sS]","")));
            } catch (NumberFormatException e) {
                return "starnubid";
            }
        } else {
            return identifierString;
        }
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method will print an error based on the object casting issues in the method identifierCastRestrictedVersion()
     * <p>
     */
    private void identifierCastError(String castedIdentifierString, String failureList, String brokenIdentifier){
        if (castedIdentifierString.equalsIgnoreCase("uuid")) {
            StarNub.getLogger().cErrPrint("StarNub", "Could not add a UUID \"" + brokenIdentifier + "\" to StarNub's internal "+failureList+"." +
                    " Please check your file. If the issue persist, please put a issue in at " +
                    "www.StarNub.org under StarNub.");
        } else if (castedIdentifierString.equalsIgnoreCase("ip")) {
            StarNub.getLogger().cErrPrint("StarNub", "Could not add a IP \"" + brokenIdentifier + "\" to StarNub's internal "+failureList+"." +
                    " Please check your file. If the issue persist, please put a issue in at " +
                    "www.StarNub.org under StarNub.");
        } else if (castedIdentifierString.equalsIgnoreCase("starnubip")) {
            StarNub.getLogger().cErrPrint("StarNub", "\"" + brokenIdentifier + "\" does not represent a StarNub ID cannot at its UUID or IPs to the StarNub's internal "+failureList+"." +
                    " Please check your file. If the issue persist, please put a issue in at www.StarNub.org under StarNub.");
        }
    }

    /**
     *  This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will take a set of StarNub ID's and return all the UUIDs and IPs
     * that are associated to that StarNub ID's. This method is slightly costly.
     *
     * @param starnubIdOrIds Object representing a HashSet of Integers or Integer reprenting
     *                       StarNubIds
     * @return               HashSet of the combined UUIDs and IPs.
     */
    @SuppressWarnings("unchecked")
    public HashSet<Object> getStarnubIdUUIDsIPs(Object starnubIdOrIds) {
        HashSet<Object> combinedList = new HashSet<Object>();
        if (starnubIdOrIds instanceof HashMap) {
            combinedList.addAll(getAllIPsByStarNubIds((HashSet<Integer>) starnubIdOrIds));
            combinedList.addAll(getAllUUIDsByStarNubIds((HashSet<Integer>) starnubIdOrIds));
        } else if (starnubIdOrIds instanceof Integer) {
            combinedList.addAll(getAllUUIDsByStarNubId((Integer) starnubIdOrIds));
            combinedList.addAll(getAllIPsByStarNubId((Integer) starnubIdOrIds));
        }
        return combinedList;
    }

    /**
     *  This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will take a StarNub Id and return all of the character UUIDs.
     *
     * @param starnubId Integer representing the StarNub Id
     * @return HashSet of character UUIDs
     */
    public HashSet<UUID> getAllUUIDsByStarNubId(Integer starnubId){
        HashSet<Integer> starnubdIds = new HashSet<Integer>();
        starnubdIds.add(starnubId);
        return getAllUUIDsByStarNubIds(starnubdIds);
    }

    /**
     *  This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will take a set of StarNub Ids and return all of the characters UUIDs.
     *
     * @param starnubIds HashSet representing the StarNub Ids
     * @return HashSet of character UUIDs
     */
    public HashSet<UUID> getAllUUIDsByStarNubIds(HashSet<Integer> starnubIds){
        HashSet<UUID> noDuplicateList = new HashSet<UUID>();
        if (!starnubIds.isEmpty()) {
            for (Integer starnubId : starnubIds) {
                noDuplicateList.addAll(StarNub.getDatabaseTables().getCharacters().getCharactersUUIDListFromStarnubId(starnubId));
            }
        } else {
            return null;
        }
        return noDuplicateList;
    }

    /**
     *  This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will take a StarNub Id and return all of the IPs the StarNub account has used.
     *
     * @param starnubId Integer representing the StarNub Id
     * @return HashSet of character IPs
     */
    public HashSet<InetAddress> getAllIPsByStarNubId(Integer starnubId){
        HashSet<Integer> starnubdIds = new HashSet<Integer>();
        starnubdIds.add(starnubId);
        return getAllIPsByStarNubIds(starnubdIds);
    }

    /**
     *  This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will take a set of StarNub Ids and return all of IPs the StarNub accounts have used.
     *
     * @param starnubIds HashSet representing the StarNub Ids
     * @return HashSet of character IPs
     */
    public HashSet<InetAddress> getAllIPsByStarNubIds(HashSet<Integer> starnubIds){
        ArrayList<Integer> playerCharactersList;
        HashSet<InetAddress> noDuplicateList = new HashSet<InetAddress>();
        if (!starnubIds.isEmpty()) {
            for (Integer starnubId : starnubIds) {
                playerCharactersList = StarNub.getDatabaseTables().getCharacters().getCharacterIDsListFromStarnubId(starnubId);
                if (!playerCharactersList.isEmpty()) {
                    noDuplicateList.addAll(StarNub.getDatabaseTables().getCharacterIPLog().getCharactersAssociatedIPListFromCharacterIds(playerCharactersList));
                }
            }
        } else {
            return null;
        }
        return noDuplicateList;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This whitelistLoad method will try and load "StarNub/whitelist.txt" and parse each line into
     * an ip, uuid or StarNub id. If you are using a StarNub Id they player must have already have an account
     * that has been used.
     * <p>
     */
    public void whitelistLoad() {
        //PLACE_HOLDER - Event
        HashSet<Object> noDuplicateList = new HashSet<Object>();
        List<String> diskWhitelist = null;
        try {
            diskWhitelist = new FileToList().readFileLinesString("StarNub/whitelist.txt");
        } catch (Exception e) {
            StarNub.getLogger().cErrPrint("StarNub", "Unable to load \"StarNub/whitelist.txt\". Please check your file. If the " +
                    "issue persist, please put a issue in at www.StarNub.org under StarNub.");
            return;
        }
        HashSet<Integer> starnubIds = new HashSet<Integer>();
        if (diskWhitelist.size() != 0) {
            for (String whiteListString : diskWhitelist) {
                Object castedIdentifier = identifierCast(whiteListString);
                if (castedIdentifier instanceof UUID || castedIdentifier instanceof InetAddress) {
                    noDuplicateList.add(castedIdentifier);
                } else if (castedIdentifier instanceof Integer) {
                    starnubIds.add((Integer) castedIdentifier);
                } else if (castedIdentifier instanceof String) {
                    identifierCastError((String) castedIdentifier, "whitelist", whiteListString);
                }
            }
            noDuplicateList.addAll(getAllUUIDsByStarNubIds(starnubIds));
            noDuplicateList.addAll(getAllIPsByStarNubIds(starnubIds));
            synchronized (WHITE_LIST_LOCK) {
                whitelist.addAll(noDuplicateList);
            }
        }
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This whitelistLoad will try to reload the whitelist from disk "StarNub/whitelist.txt".
     * <p>
     */
    public void whiteListReload() {
        //PLACE_HOLDER - Event
        synchronized (WHITE_LIST_LOCK) {
            whitelist.clear();
        }
        whitelistLoad();
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method will check to see if an identifier is on the whitelist.
     * The identifiers that can be checked are UUID, IP. For StarNub IDs it will
     * check to see if all the UUIDs or IPs are listed, and if not will return
     * false.
     * <p>
     */
    public boolean onWhitelist(String identifier) {
        Object castedIdentifier = identifierCast(identifier);
        if (castedIdentifier instanceof Integer) {
            HashSet<Integer> starnubdIds = new HashSet<Integer>();
            starnubdIds.add((Integer) castedIdentifier);
            HashSet<Object> starnubIdUUIDsIPs = getStarnubIdUUIDsIPs(starnubdIds);
            int countOfUUIDsIPs = starnubIdUUIDsIPs.size();
            int areWhitelisted = 0;
            for (Object object : starnubIdUUIDsIPs) {
                if (whitelist.contains(object)) {
                    areWhitelisted++;
                }
            }
            return countOfUUIDsIPs == areWhitelisted;
        }
        return whitelist.contains(identifier);
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method will check to see if an InetAddress is
     * on the whitelist.
     * <p>
     */
    public boolean onWhitelist(InetAddress inetAddress) {
        return whitelist.contains(inetAddress);
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method will check to see if an uuid is
     * on the whitelist.
     * <p>
     */
    public boolean onWhitelist(UUID uuid) {
        return whitelist.contains(uuid);
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will append a identifier to "StarNub/whitelist.txt" and the reload the whitelist.
     * <p>
     */
    public boolean addToWhitelist(String identifier){
        //PLACE_HOLDER - Event
        Object castedIdentifier = identifierCast(identifier);
        new AppendToFile().appendStringToFile("StarNub/whitelist.txt", identifier);
        whiteListReload();
        return whitelist.contains(castedIdentifier);
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will remove a identifier from the "StarNub/whitelist.txt" and then reload the whitelist.
     * <p>
     */
    public boolean removeFromWhitelist(String identifier){
        //PLACE_HOLDER - Event
        Object castedIdentifier = identifierCast(identifier);
        new RemoveLineFromFile().removeLineFromFile("StarNub/whitelist.txt", castedIdentifier.toString().trim());
        whiteListReload();
        return !whitelist.contains(castedIdentifier);
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This will return the ops list for use in direct operations.
     * Caution this method is not synchronized and may cause thread contention.
     * This method is private due to the possible issues, please use the other
     * helper methods or request ones to be added.
     * <p>
     */
    private Set<UUID> getOpsList() {
        return opsList;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This opsListLoad method will try and load "StarNub/ops.txt" and parse each line into
     * an UUID.
     * <p>
     */
    public void opsListLoad() {
        //PLACE_HOLDER - Event
        List<UUID> diskOpslist = null;
        try {
            diskOpslist = new FileToList().readFileLinesUUID("StarNub/ops.txt");
        } catch (Exception e) {
            StarNub.getLogger().cErrPrint("StarNub", "Unable to load \"StarNub/ops.txt\". Please check your file. If the " +
                    "issue persist, please put a issue in at www.StarNub.org under StarNub.");
            return;
        }
        synchronized (OPS_LIST_LOCK) {
            opsList.addAll(diskOpslist);
        }
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This opsListReload will try to reload the ops list from disk "StarNub/ops.txt".
     * <p>
     */
    public void opsListReload() {
        //PLACE_HOLDER - Event
        synchronized (OPS_LIST_LOCK) {
            opsList.clear();
        }
        opsListLoad();
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method will try and parse a string to uuid to be added
     * to the ops list.
     * false.
     * <p>
     */
    public boolean onOpsList(String identifier) {
        UUID uuid = null;
        try {
            uuid = UUID.fromString(identifier);
        } catch (IllegalArgumentException e) {
            //TODO message to console or persons
        }
        return uuid != null && opsList.contains(uuid);
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method will check to see if an UUID is on the ops list.
     * <p>
     */
    public boolean onOpsList(UUID uuid) {
        return opsList.contains(uuid);
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will append a identifier to "StarNub/ops.txt" and the reload the ops list.
     * <p>
     */
    public boolean addToOpsList(String identifier){
        //PLACE_HOLDER - Event
        UUID uuid = null;
        try {
            uuid = UUID.fromString(identifier);
        } catch (IllegalArgumentException e) {
            //TODO message to console or persons
        }
        if (uuid != null) {
            new AppendToFile().appendStringToFile("StarNub/whitelist.txt", uuid.toString());
        }
        whiteListReload();
        return whitelist.contains(uuid);
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will remove a identifier from the "StarNub/ops.txt" and then reload the ops.
     * <p>
     */
    public boolean removeFromOpsList(String identifier){
        //PLACE_HOLDER - Event
        UUID uuid = null;
        try {
            uuid = UUID.fromString(identifier);
        } catch (IllegalArgumentException e) {
            //TODO message to console or persons
        }
        if (uuid != null) {
            new RemoveLineFromFile().removeLineFromFile("StarNub/ops.txt", uuid.toString().trim());
        }
        whiteListReload();
        return !whitelist.contains(uuid);
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This will return the restriction list for use in direct operations.
     * Please use the other helper methods or request ones to be added in order
     * to access restricted list functions.
     * <p>
     */
    private ConcurrentHashMap<Object, Restrictions> getRestrictedIPsUUIDsList() {
        return restrictedIPsUUIDs;
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This is used to load restrictions from the database into memory
     * <p>
     */
    private void restrictionLoad() {
        //PLACE_HOLDER - Event
        try {
            List<Restrictions> restrictedAccounts = StarNub.getDatabaseTables().getPlayerSessionRestrictions().getTableDao().queryForAll();
            for (Restrictions restriction : restrictedAccounts) {
                restrictionPurger(restriction);
                Object castedIdentifier = identifierCast(restriction.getRestrictedIdentifier());
                if (castedIdentifier instanceof UUID || castedIdentifier instanceof InetAddress) {
                    restrictedIPsUUIDs.putIfAbsent(castedIdentifier, restriction);
                } else if (castedIdentifier instanceof String) {
                    identifierCastError((String) castedIdentifier, "restriction list", castedIdentifier.toString());
                }
            }
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
    }


    public void restrictionRemove(){

    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This is used to automaticly remove any restrictions that have expired
     * <p>
     */
    public void restrictionsPurge(){
        restrictedIPsUUIDs.values().forEach(this::restrictionPurger);
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This is the actual restriction purging method
     * <p>
     * @param restriction Restriction to be check for purge
     */
    private void restrictionPurger(Restrictions restriction) {
        //PLACE_HOLDER - Event
        if (restriction.getDateRestrictionExpires() == null) {
            return;
        }
        if (restriction.getDateRestrictionExpires().isBeforeNow()) {
            StarNub.getDatabaseTables().getPlayerSessionRestrictions().delete(restriction);
            Object castedIdentifier = identifierCast(restriction.getRestrictedIdentifier());
            StarNub.getLogger().cInfoPrint("StarNub", "A temporary restriction has expired and was removed. ");//event and info
            restrictedIPsUUIDs.remove(castedIdentifier);
        }
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This is used to check when a player logs in, if they are restricted or not
     * <p>
     */
    public Restrictions getPlayerRestrictionLogOn(InetAddress ip, UUID uuid){
        if (restrictedIPsUUIDs.containsKey(ip)) {
            return restrictedIPsUUIDs.get(ip);
        }
        if (restrictedIPsUUIDs.containsKey(uuid)) {
            return restrictedIPsUUIDs.get(uuid);
        } else {
            return null;
        }
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method is used for muting a UUID or InetAddress
     * <p>
     * @param identifier Object representing a UUID or IP
     * @param expirationDate DateTime the restriction will expire, NULL for never
     */
    public void playerRestrictMute(Object identifier, String imposerName, Account imposerAccount, String reason,  DateTime expirationDate){
        //PLACE_HOLDER - Event
        addRestrictions(identifier, imposerName, imposerAccount, reason,  expirationDate, true, false, false);
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method is used for command blocking a UUID or InetAddress
     * <p>
     * @param identifier Object representing a UUID or IP
     * @param imposerName String represneting who imposed the restriction
     * @param imposerAccount Account representing who imposed the restriction
     * @param expirationDate DateTime the restriction will expire, NULL for never
     */
    public void playerRestrictCommandBlock(Object identifier, String imposerName, Account imposerAccount, String reason, DateTime expirationDate){
        //PLACE_HOLDER - Event
        addRestrictions(identifier, imposerName, imposerAccount, reason,  expirationDate, false, true, false);
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method is used for banning a UUID or InetAddress
     * <p>
     * @param identifier Object representing a UUID or IP
     * @param imposerName String represneting who imposed the restriction
     * @param imposerAccount Account representing who imposed the restriction
     * @param expirationDate DateTime the restriction will expire, NULL for never
     */
    public void playerRestrictBan(Object identifier, String imposerName, Account imposerAccount, String reason, DateTime expirationDate){
        //PLACE_HOLDER - Event
        addRestrictions(identifier, imposerName, imposerAccount, reason,  expirationDate, false, false, true);
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method is used for adding a UUID or InetAddress to the restriction list
     * <p>
     * @param identifier Object representing a UUID or IP
     * @param imposerName String represneting who imposed the restriction
     * @param imposerAccount Account representing who imposed the restriction
     * @param expirationDate DateTime the restriction will expire, NULL for never
     * @param mute boolean is muted
     * @param commandBlock boolean is command blocked
     * @param ban boolean is banned
     */
    private void addRestrictions(Object identifier, String imposerName, Account imposerAccount, String reason, DateTime expirationDate, boolean mute, boolean commandBlock, boolean ban) {
        Player playerSession = getOnlinePlayerByAnyIdentifier(identifier);
        Restrictions restrictions = new Restrictions(identifier.toString(), mute, commandBlock, ban, imposerName, imposerAccount, reason,  expirationDate);
        restrictedIPsUUIDs.putIfAbsent(identifier, restrictions);
        if (playerSession != null){
            StarNub.getDatabaseTables().getPlayerSessionRestrictions().createOrUpdate(restrictions);
            playerSession.setRestrictions(restrictions);
            if (ban) {
                StarNub.getMessageSender().playerMessage("StarNub", playerSession, "You have been banned.");
                playerDisconnectPurposely(playerSession, "Banned");
            } else if (mute) {
                StarNub.getMessageSender().playerMessage("StarNub", playerSession, "You have been muted.");
            } else if (commandBlock) {
                StarNub.getMessageSender().playerMessage("StarNub", playerSession, "You have been command blocked.");
            }
        }
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method adds an event handler to listen for player starbounddata.packets.connection starbounddata.packets. It will then read the data
     * and check StarNubs internal configurations and databases/tables to determine how to handle the pending starbounddata.packets.connection.
     * Starnub will create a {@link server.connectedentities.player.session.PendingPlayer} which represents a pending
     * player session and add it to pendingConnections. This is Step 1 of 2. Step two follows this method.
     * <p>
     */
    private void eventListenerPlayerConnectionAttempt() {
        /* This will insure channels are closed if no data is received in a period of time */
        StarNub.getStarNubEventRouter().registerEventSubscription("StarNub", "StarNub_Socket_Connection_Success_Client", new StarNubEventHandler() {
            @Override
            public void onEvent(StarNubEvent eventData) {
                openSockets.putIfAbsent((ChannelHandlerContext) ((ObjectEvent) eventData).getOBJECT(), DateTime.now().getMillis());
            }
        });
        StarNub.getStarNubEventRouter().registerEventSubscription("StarNub", "StarNub_Socket_Connection_Success_Server", new StarNubEventHandler() {
            @Override
            public void onEvent(StarNubEvent eventData) {
                openSockets.putIfAbsent((ChannelHandlerContext) ((ObjectEvent) eventData).getOBJECT(), DateTime.now().getMillis());
            }
        });

        StarNub.getPacketEventRouter().registerEventSubscription("StarNub", ClientConnectPacket.class, new PacketEventHandler() {
            @Override
            public Packet onEvent(Packet packet) {
                ClientConnectPacket cliConPacket = (ClientConnectPacket) packet;


            /* This will ensure that the Client and Server sockets are removed from the
            * open sockets list. The open sockets list is to ensure no resources are leaked by attacks */
                openSockets.remove(cliConPacket.getSenderCTX());
                openSockets.remove(cliConPacket.getDestinationCTX());


                Server cServer = StarNub.getServer();
                MessageSender cMsgSen = StarNub.getMessageSender();
                Configuration conf = StarNub.getConfiguration();
                Map confSI = (Map) conf.getConfiguration().get("starbounddata.packets.starbounddata.packets.server info");
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
                if (StarNub.getServer().getStarboundManager().getStarboundStatus().isRestarting()) {
                    rejectionReason =
                            "\n^#f5f5f5;Starbound is restarting, please come back in a few minutes.\n";
                    rejectClient = "restarting";
                } else if ((boolean) ((Map) conf.getConfiguration().get("starnub settings")).get("whitelisted")) {
                    if (!StarNub.getServer().getConnections().onWhitelist(connectingIp) ||
                            !StarNub.getServer().getConnections().onWhitelist(connectingUuid)) {
                        rejectionReason =
                                "\n^#f5f5f5;This starbounddata.packets.starbounddata.packets.server is whitelisted.\n";
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
                server.connectedentities.player.character.Character character = cDb.getCharacters().getCharacterFromNameUUIDCombo(cliConPacket.getPlayerName(), cliConPacket.getUUID());
                if (character == null) {
                    character = new Character(
                            cliConPacket.getPlayerName(),
                            cChatF.cleanNameComplete(cliConPacket.getPlayerName()),
                            cliConPacket.getUUID());
                    //PLACE_HOLDER - New character seen event
                } else {
                    UUID uuid = character.getUuid();
                    if (isOnline("StarNub", uuid)) {
                        if (alreadyLoggedIn.getCache(uuid) != null) {
                            playerDisconnectPurposely(uuid, "Already Logged In");
                            alreadyLoggedIn.removeCache(uuid);
                        } else {
                            alreadyLoggedIn.addCache(uuid, new TimeCache());
                            rejectionReason =
                                    "\n^#f5f5f5;You are already logged into this starbounddata.packets.starbounddata.packets.server with this character. Please try again.";
                            rejectClient = "online";
                        }
                    } else {
                        character.setLastSeen(DateTime.now());
                    }
                }

            /* Account last login time and ID set & db create/save */
                int accountId = 0;
                if (character.getAccount() != null) {
                    accountId = character.getAccount().getStarnubId();
                    character.initialLogInProcessing();
                    cDb.getAccounts().update(character.getAccount());
                    //PLACE_HOLDER - Account Log-In Event
                }
                cDb.getCharacters().createOrUpdate(character);

                Player playerSession = new Player(cliConPacket.getSenderCTX(), cliConPacket.getDestinationCTX(), connectingIp, character, null, accountId, opsList.contains(connectingUuid));

                //TODO ignore list This currently just sets it to not have a null list for the starbounddata.packets.chat rooms
                playerSession.setDoNotSendMessageList();

                cDb.getPlayerSessionLog().create(playerSession);
                //PLACE_HOLDER - Session event

            /* Unique Character & IP log check, creation & db create/save */
                CharacterIP characterIP = new CharacterIP(character, playerSession.getSessionIp());
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
                                    "^#f5f5f5;This starbounddata.packets.starbounddata.packets.server is full and no more VIP slots are available.";
                            rejectClient = "server_full";
                        }
                    } else {
                        rejectionReason =
                                "^#f5f5f5;This starbounddata.packets.starbounddata.packets.server is full and you do not have permission to enter.";
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
                StarNub.getLogger().cDebPrint("StarNub", "A Player is attempting to connect to the starbounddata.packets.starbounddata.packets.server on IP: " + connectingIp + ".");
                addPendingConnections(pendingPlayer);
                return cliConPacket;
            }
        });

    }

    private void eventListenerPacketEvent(){
//        StarNub.getPacketEventRouter().getPacketReactor().on(T(starbounddata.packets.Packet.class), ev -> {
//
//        });
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method will add an event handler to listen to the starbounddata.packets.starbounddata.packets.server starbounddata.packets.connection response to the player starbounddata.packets.connection attempt.
     * StarNub will pull out the pending session data that was already collected and check it over. StarNub will modify the packet
     * and allow or disallow the session to be completely setup. If the session is set up then StarNub will take the collected
     * pending player session data and create a new {@link server.connectedentities.player.session.Player} that will then be
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
                PendingPlayer pendingPlayer = StarNub.getServer().getConnections().removePendingConnections(conResPacket.getDestinationCTX());
                Player playerSession = pendingPlayer.getPLAYER_SESSION();

                if (pendingPlayer.getREJECT_CLIENT() != null) {
                    conResPacket.setSuccess(false);
                    conResPacket.setRejectionReason(pendingPlayer.getREJECTION_REASON());
                    return conResPacket;
                }
                addConnectedPlayer(playerSession);
            /* StarNub will spawn a thread to finish the client starbounddata.packets.connection so that the packet can continue to the client
             * without delay. Delaying this packet further then the above will cause a race condition and a client
             * disconnect from the starbounddata.packets.starbounddata.packets.server, before the starbounddata.packets.connection set up is complete */
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Character character = playerSession.getCharacter();
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
                                cMsgSen.cWarnPrint("StarNub", "A Player tried connecting while the starbounddata.packets.starbounddata.packets.server is whitelisted on IP: " + pendingPlayer.getSESSION_IP() + ".");
                                serverSideNotificationOfPlayerDisconnect(pendingPlayer.getSESSION_SERVER_CTX());
                                return;
                            } else if (rejectReason.equalsIgnoreCase("ban") || rejectReason.equalsIgnoreCase("tban")) {
                                PlayerEvent.eventSend_Player_Connection_Failure_Banned_Temporary(pendingPlayer);
                                cMsgSen.cWarnPrint("StarNub", "A banned Player tried connecting to the starbounddata.packets.starbounddata.packets.server on IP: " + pendingPlayer.getSESSION_IP() + ".");
                                serverSideNotificationOfPlayerDisconnect(pendingPlayer.getSESSION_SERVER_CTX());
                                return;
                            } else if (rejectReason.equalsIgnoreCase("online")) {
                                PlayerEvent.eventSend_Player_Connection_Failure_Character_Already_Online(pendingPlayer);
                                cMsgSen.cWarnPrint("StarNub", "A Player tried to log in with the same character multiple times.");
                                serverSideNotificationOfPlayerDisconnect(pendingPlayer.getSESSION_SERVER_CTX());
                                return;
                            } else if (rejectReason.equalsIgnoreCase("server_full")) {
                                PlayerEvent.eventSend_Player_Connection_Failure_Character_Already_Online(pendingPlayer);
                                cMsgSen.cWarnPrint("StarNub", "A player tried to log into the starbounddata.packets.starbounddata.packets.server which is full.");
                                serverSideNotificationOfPlayerDisconnect(pendingPlayer.getSESSION_SERVER_CTX());
                                return;
                            } else if (rejectReason.equalsIgnoreCase("restarting")) {
                                PlayerEvent.eventSend_Player_Connection_Failure_Character_Already_Online(pendingPlayer);
                                cMsgSen.cWarnPrint("StarNub", "A player tried to log into the starbounddata.packets.starbounddata.packets.server while its preparing to restart.");
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

    //TODO starbounddata.packets.world intercept

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method will purge open sockets for connections that never completed within 30 seconds. This is used to
     * help prevent dos, by opening sockets on StarNub. This is surly not full proof as sometime the socket hangs open
     * on the OS side, this method will eventually be accompanied by more sure proof methods, such as a starbounddata.packets.connection rate limiter
     * when a DOS or DDOS is detected, as well as a manual packet back to the connector to insure socket close.
     * <p>
     */
    public void openSocketPurge(){
        openSockets.keySet().stream().filter(ctx -> (DateTime.now().getMillis() - openSockets.get(ctx)) >= 30000).forEach(ctx -> ctx.close());
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This will return the pending player ConcurrentHashMap for use in direct operations.
     * This method is private, please use the other helper methods or request ones to be added.
     * <p>
     */
    private ConcurrentHashMap<ChannelHandlerContext, PendingPlayer> getPendingConnectionPlayer() {
        return  pendingConnections;
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This is the core method for a player initializing a starbounddata.packets.connection to StarNub and Starbound it will
     * add the players session data to a temporary ConcurrentHashMap.
     * <p>
     * @param pendingPlayer ConnectingPlayer representing a pending player sessions data
     */
    private void addPendingConnections(PendingPlayer pendingPlayer) {
        pendingConnections.put(pendingPlayer.getSENDER_CTX(), pendingPlayer);
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This is the core method for a player initializing a starbounddata.packets.connection to StarNub and Starbound it will
     * remove the players session data and use it in another method to turn the pending player session into
     * a full session.
     * <p>
     * @param clientCtx ChannelHandlerContext representing the client side of the starbounddata.packets.connection to be removed
     */
    private PendingPlayer removePendingConnections(ChannelHandlerContext clientCtx) {
        return pendingConnections.remove(clientCtx);
        //PLACE_HOLDER - Pending starbounddata.packets.connection purge event
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method well check all of the pending connections and remove any pending starbounddata.packets.connection older then 5 minutes.
     * <p>
     */
    @SuppressWarnings("SuspiciousMethodCalls")
    public void pendingConnectionPurge() {
        pendingConnections.values().stream().filter(pendingPlayer -> (System.currentTimeMillis() - pendingPlayer.getCONNECTION_PENDING_TIME()) >= 300000).forEach(pendingPlayer -> {
            StarNub.getLogger().cErrPrint("StarNub", "It appears that a pending player starbounddata.packets.connection was never " +
                    "completed or removed from StarNubs internal memory. If this message appears often, please " +
                    "put a trouble ticket in at \"www.StarNub.org\" under StarNub.");
            serverSideNotificationOfPlayerDisconnect(pendingConnections.remove(pendingPlayer));
        });
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This will return the connected players ConcurrentHashMap for use in direct operations.
     * This method is public, but please use the other helper methods or request ones to be added over using this
     * method directly.
     * <p>
     */
    public ConcurrentHashMap<ChannelHandlerContext, Player> getConnectedPlayers() {
        return connectedPlayers;
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This is the core method for adding a player who is actively connected to the StarNub and Starbound starbounddata.packets.starbounddata.packets.server.
     * <p>
     * @param playerSession Player representing player session that contains everything we know about them
     */
    private void addConnectedPlayer(Player playerSession) {
        connectedPlayers.put(playerSession.getClientCtx(), playerSession);
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This is the core method for removing a player who is actively connected to the StarNub and Starbound starbounddata.packets.starbounddata.packets.server.
     * <p>
     * @param clientCtx ChannelHandlerContext representing the client side of the starbounddata.packets.connection to be removed
     */
    private Player removeConnectedPlayer(ChannelHandlerContext clientCtx) {
        return connectedPlayers.remove(clientCtx);
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method will add an event handler to listen for client and starbounddata.packets.starbounddata.packets.server disconnect starbounddata.packets. StarNub uses this
     * to clean up player connections.
     * <p>
     */
    private void eventListenerPlayerDisconnect() {
        StarNub.getPacketEventRouter().registerEventSubscription("StarNub", ServerDisconnectPacket.class, new PacketEventHandler() {
            @Override
            public Packet onEvent(Packet packet) {
                ServerDisconnectPacket serverDisconnectPacket = (ServerDisconnectPacket) packet;
                PlayerEvent.eventSend_Player_Disconnect(connectedPlayers.remove(serverDisconnectPacket.getDestinationCTX()), "Disconnected");
                return serverDisconnectPacket;
            }
        });
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method will notify the starbounddata.packets.starbounddata.packets.server that a player is no longer connected in order to
     * ensure that Starbound does self cleanup.
     * <p>
     */
    public void disconnectAllPlayers() {
        for (Player playerSession : connectedPlayers.values()) {
            playerDisconnectPurposely(playerSession, "Server_Restart");
        }
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method will force a starbounddata.packets.connection to be closed for a players session.
     * <p>
     */
    public void playerDisconnectPurposely(Player playerSession, String reason) throws NullPointerException{
        playerSession.getClientCtx().close();
        serverSideNotificationOfPlayerDisconnect(playerSession);
        PlayerEvent.eventSend_Player_Disconnect(connectedPlayers.remove(playerSession.getClientCtx()), reason);
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method will force a starbounddata.packets.connection to be closed for a players session.
     * <p>
     */
    public void playerDisconnectPurposely(Object playerIdentifier, String reason) throws NullPointerException{
        Player playerSession = getOnlinePlayerByAnyIdentifier(playerIdentifier);
        try {
            playerSession.getClientCtx().close();
        } catch (NullPointerException e) {
            /* Silent catch if channel is already closed */
        }
        serverSideNotificationOfPlayerDisconnect(playerSession);
        PlayerEvent.eventSend_Player_Disconnect(connectedPlayers.remove(playerSession.getClientCtx()), reason);
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method will send an event if a player has lost starbounddata.packets.connection.
     * <p>
     * @param player Player that lost starbounddata.packets.connection
     */
    private void playerDisconnectConnectionLost(Player player) {
        PlayerEvent.eventSend_Player_Disconnect(connectedPlayers.remove(player.getClientCtx()), "Lost_Connection");
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method will clean up a players starbounddata.packets.connection, by saving off database stats and ensuring
     * that both sides of the StarNub starbounddata.packets.connection were closed.
     * <p>
     */
    private void eventListenerPlayerConnectionCleanUp(){
        StarNub.getStarNubEventRouter().registerEventSubscription("StarNub", "Player_Disconnected", new StarNubEventHandler() {
            @Override
            public void onEvent(StarNubEvent eventData) {
                Player playerSession = (Player) ((PlayerEvent) eventData).getPLAYER_SESSION();
                Character character = playerSession.getCharacter();
                playerSession.setEndTimeUtc(DateTime.now());
                character.updatePlayedTimeLastSeen();
                try {
                    playerSession.getServerCtx().close();
                } catch (Exception e) {
                /* Silently catch the exception, Connection might already be cleanly closed */
                }
                try {
                    playerSession.getClientCtx().close();
                } catch (Exception e) {
                /* Silently catch the exception, Connection might already be cleanly closed */
                }
            }
        });
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method will notify the starbounddata.packets.starbounddata.packets.server that a player is no longer connected in order to
     * ensure that Starbound does self cleanup.
     * <p>
     * @param playerIdentifier Object that represent a player and it can take many forms
     */
    public void serverSideNotificationOfPlayerDisconnect(Object playerIdentifier) {
        Player playerSession = getOnlinePlayerByAnyIdentifier(playerIdentifier);
        StarNub.getPacketSender().serverPacketSender(playerSession, new ClientDisconnectRequestPacket());
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method will notify the starbounddata.packets.starbounddata.packets.server that a player is no longer connected in order to
     * ensure that Starbound does self cleanup.
     * <p>
     * @param serverSideCTX ChannelHandlerContext that represents the starbounddata.packets.starbounddata.packets.server side of the client-starbounddata.packets.starbounddata.packets.server starbounddata.packets.connection
     */
    public void serverSideNotificationOfPlayerDisconnect(ChannelHandlerContext serverSideCTX) {
        StarNub.getPacketSender().serverPacketSender(serverSideCTX, new ClientDisconnectRequestPacket());
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method will go through each online player, and update the database Played time counter as well
     * as the last seen Date. This ensures that the time is accurate in case of critical crash of the starbounddata.packets.starbounddata.packets.server tool
     * or improper restart.
     * <p>
     */
    public void connectedPlayerPlayedTimeUpdate(){
        for (Player players : connectedPlayers.values()) {
            Character character = players.getCharacter();
            character.updatePlayedTimeLastSeen();
        }
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method will go through each online player, and insure they are still connected.
     * If not it will send a player lost starbounddata.packets.connection message and remove them from the database.
     * <p>
     */
    public void connectedPlayerLostConnectionCheck() {
        connectedPlayers.values().stream().filter(player -> player.getClientCtx().isRemoved() || player.getServerCtx().isRemoved()).forEach(this::playerDisconnectConnectionLost);
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method is used to pull a player record using any type of
     * parameter. (UUID, IP, NAME, clientCTX, Starbound ID, StarNub ID)
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
        } else if (playerIdentifier instanceof Channel) {
            return playerByChannel((Channel) playerIdentifier);
        } else if (playerIdentifier instanceof Integer){
            return playerByStarboundClientID((int) playerIdentifier);
        } else {
            return null;
        }
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
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
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method is used to get a Player by a UUID from getOnlinePlayerByAnyIdentifier() method.
     * <p>
     * @param uuid UUID which represents the playerIdentifier
     * @return Player which represents the player that was retrieved by the provided UUID
     */
    private Player playerByUUID(UUID uuid) {
        for (Player playerSession : connectedPlayers.values()){
            if (playerSession.getCharacter().getUuid().equals(uuid)) {
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
     * Uses: This method is used to get a Player by a InetAddress from getOnlinePlayerByAnyIdentifier() method.
     * <p>
     * @param ip InetAddress which represents the playerIdentifier
     * @return Player which represents the player that was retrieved by the provided InetAddress
     */
    private Player playerByIP(InetAddress ip) {
        for (Player playerSession : connectedPlayers.values()){
            if (playerSession.getSessionIp().equals(ip)) {
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
     * Uses: This method is used to get a Player by a name in String form which is not as reliable as another identifier might be
     * from getOnlinePlayerByAnyIdentifier() method.
     * <p>
     * @param identifierString String which represents the playerIdentifier
     * @return Player which represents the player that was retrieved by the provided String
     */
    private Player playerByName(String identifierString) {
        for (Player playerSession : connectedPlayers.values()){
            if (playerSession.getCharacter().getName().equalsIgnoreCase(identifierString) || playerSession.getCharacter().getCleanName().equalsIgnoreCase(identifierString) ||
                    playerSession.getNickName().equalsIgnoreCase(identifierString) || playerSession.getCleanNickName().equalsIgnoreCase(identifierString) ||
                    playerSession.getGameName().equalsIgnoreCase(identifierString)) {
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
     * Uses: This method is used to get a Player by a Starbound ID from getOnlinePlayerByAnyIdentifier() method, this
     * method is reliable for sessions, but keep in mind this Starbound ID changes when a player disconnects and reconnects
     * to a Starbound starbounddata.packets.starbounddata.packets.server.
     * <p>
     * @param starboundClientId Integer which represents the playerIdentifier
     * @return Player which represents the player that was retrieved by the provided Integer
     */
    private Player playerByStarboundClientID(int starboundClientId) {
        for (Player playerSession : connectedPlayers.values()){
            if (playerSession.getStarboundClientId() == starboundClientId) {
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
     * Uses: This method is used to get a Player by a StarNub ID from getOnlinePlayerByAnyIdentifier() method, this
     * StarNub ID is tied to the players account they use to log into for groups, permissions, ect.
     * <p>
     * @param starnubClientId Integer which represents the playerIdentifier
     * @return Player which represents the player that was retrieved by the provided Integer
     */
    private Player playerByStarNubClientID(int starnubClientId) {
        for (Player playerSession : connectedPlayers.values()){
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
        for (Player playerSession : connectedPlayers.values()){
            if (playerSession.getClientCtx() == ctx || playerSession.getServerCtx() == ctx) {
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
     * Uses: This method is used to get a Player by a Channel from getOnlinePlayerByAnyIdentifier() method.
     * <p>
     * @param channel Channel which represents the playerIdentifier
     * @return Player which represents the player that was retrieved by the provided Channel
     */
    private Player playerByChannel(Channel channel) {
        for (Player playerSession : connectedPlayers.values()){
            if (playerSession.getClientChannel() == channel) {
                return playerSession;
            }
        }
        return null;
    }

//    /**
//     * This represents a higher level method for StarNubs API.
//     * <p>
//     * Recommended: For Plugin Developers & Anyone else.
//     * <p>
//     * Uses: This is used to retrieve a character by name, if the character does not exist, you will receive a
//     * String with all the character ids, last seen, played time for each for you to determine which player you meant.
//     * If you receive a string have the sender resend the query with a -# at the end for the character id, and then
//     * when parsing the re used command use the getOfflinePlayerByIdCached()
//     * <p>
//     * Known issues: Players with only numeric names.
//     * <p>
//     * @param sender Sender representing who sent the query
//     * @param characterName String representing the character name that was queried
//     * @return Object representing a PlayerCharacter or a String
//     */
//    public Object getOfflinePlayerByName(Sender sender, String characterName){
//        PlayerCharacter playerCharacter = getOfflinePlayerByNameCaching(sender, characterName);
//        if (playerCharacter != null) {
//            return playerCharacter;
//        } else {
//            String moreThenOneCharacter = "Please select a Character by repeating the command with -# at the end. Characters matching" +
//                    " \"" + characterName + "\". (Character ID/Last Seen/Played Time): ";
//                for (CachedData data : cachedData) {
//                    PlayerQueryCache playerQueryCache = (PlayerQueryCache) data;
//                    if (playerQueryCache.getRequester().equals(sender.getSENDER_CTX()) && playerQueryCache.getQueryName().equalsIgnoreCase(characterName)) {
//
//                        for (PlayerCharacter playerCharacter1 : playerQueryCache.getPlayers()) {
//                            String lastSeen = StarNub.getDateAndTimes().getFormattedDate("MM-dd-yy", playerCharacter1.getLastSeen());
//                            String dateTimeCounter = StarNub.getDateAndTimes().getPeriodFormattedFromMilliseconds(playerCharacter1.getPlayedTime(), "Y, ", "M, ", "W, ", "D, ", "H, ", "M, ", "S.");
//                            moreThenOneCharacter = moreThenOneCharacter +
//                                    "(" + playerCharacter1.getCharacterId() + "/" + lastSeen + "/" + dateTimeCounter + "), ";
//                        }
//                        try {
//                            moreThenOneCharacter = moreThenOneCharacter.substring(0, moreThenOneCharacter.lastIndexOf(",")) + ".";
//                        } catch (StringIndexOutOfBoundsException e) {
//                        /* Do nothing no players are online */
//                        }
//                        return moreThenOneCharacter;
//                    }
//            }
//        }
//        return null;
//    }

//    /**
//     * This represents a higher level method for StarNubs API.
//     * <p>
//     * Recommended: For Plugin Developers & Anyone else.
//     * <p>
//     * Uses: This method returns a PlayerCharacter from a cached query by using the characterId. This method well
//     * eventually be revamped when the full caching system is up in the future.
//     * <p>
//     * @param sender Sender representing who sent the query
//     * @param characterName String representing the character name that was queried
//     * @param characterId int representing the character id from cache
//     * @return PlayerCharacter that you wanted to retrieve
//     */
//    public PlayerCharacter getOfflinePlayerByIdCached(Sender sender, String characterName, int characterId){
//            for (CachedData data : cachedData) {
//                PlayerQueryCache playerQueryCache = (PlayerQueryCache) data;
//                if (playerQueryCache.getRequester().equals(sender.getSENDER_CTX()) && playerQueryCache.getQueryName().equalsIgnoreCase(characterName)) {
//                    for (PlayerCharacter playerCharacter : playerQueryCache.getPlayers()) {
//                            if (playerCharacter.getCharacterId() == characterId) {
//                                return playerCharacter;
//                            }
//                        }
//                }
//            }
//        StarNub.getMessageSender().commandResults(sender, null, "It appears you no longer have any cached data. Please resubmit a player query.", false);
//        return null;
//    }

//    /**
//     * This represents a lower level method for StarNubs API.
//     * <p>
//     * Recommended: For internal use with StarNub.
//     * <p>
//     * Uses: This method well retrieve a character by name, if multiple names exist, we will cache the results
//     * which you must handle. You can use getOfflinePlayerByName which will return a player or a String containing
//     * the cached results in which you can reuse the getOfflinePlayerByName with an int which will then retrieve
//     * the specific player represented by the int. The cache for this specific instance is distinguishable by the
//     * character name.
//     * <p>
//     * @param sender Sender representing who sent the query
//     * @param characterName String representing the character name that was queried
//     * @return PlayerCharacter of the retrieved Character
//     */
//    private PlayerCharacter getOfflinePlayerByNameCaching(Sender sender, String characterName){
//        List<PlayerCharacter> players = StarNub.getDatabase().getCharacterByName(characterName);
//        if (players.size() == 1) {
//            return players.get(0);
//        } else if (players.size() > 1) {
//            PlayerQueryCache playerQueryCache = new PlayerQueryCache(DateTime.now(), sender.getSENDER_CTX(), characterName, players);
//            /* More then one character by this name lets cache the results so they can be displayed to the user */
//            synchronized (CACHED_DATA_LOCK) {
//                cachedData.add(playerQueryCache);
//            }
//        }
//        return null;
//    }

    public void nickNameChanger(String sender, Object playerIdentifier, String newNickName, String reason){
        Player playerSession = getOnlinePlayerByAnyIdentifier(playerIdentifier);
        nickNameChanger(sender, playerSession, newNickName, reason);
    }

    public void nickNameChanger(String sender, Player playerSession, String newNickName, String reason){
        String sColor = StarNub.getMessageSender().getGameColors().getDefaultServerChatColor();
        String pNameColor = StarNub.getMessageSender().getGameColors().getDefaultNameColor();
        String previousNickName = playerSession.getNickName();
        playerSession.setNickName(newNickName);
        playerSession.setCleanNickName(StarNub.getServer().getServerChat().getChatFilter().removeColors(newNickName));
        /* Scheduled so that if the player is logging on they will see the message  */
        StarNub.getTask().getTaskScheduler().scheduleOneTimeTask("StarNub", "StarNub - Player - " + playerSession.getCleanNickName() +" - Nick Auto Change", new Runnable() {
            @Override
            public void run() {
                String reasonString = "Reason: " + reason;
                if (reasonString.contains("null")){
                    reasonString = "";
                }
                StarNub.getMessageSender().serverBroadcast(sender, "\""+ previousNickName + sColor + "\" has changed his/her name to \"" + pNameColor + newNickName + sColor +"\"." + reasonString);
            }
        }, 2, TimeUnit.SECONDS);
        StarNub.getMessageSender().serverChatMessageToServerForPlayer(sender, playerSession, ChatSendChannel.UNIVERSE, "/nick " + newNickName);
    }

//    /**
//     * This represents a lower level method for StarNubs API.
//     * <p>
//     * Recommended: For internal use with StarNub.
//     * <p>
//     * Uses: This method well purge any cache over the age of 10 minutes
//     * <p>
//     */
//    public void playerQueryCachePurge(){
//        synchronized (CACHED_DATA_LOCK) {
//            Iterator<CachedData> iter = cachedData.iterator();
//            while (iter.hasNext()) {
//                CachedData data = iter.next();
//                if ((DateTime.now().getMillis() - data.getEntryTime().getMillis()) > 600000)
//                    iter.remove();
//            }
//        }
//    }

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
            if (hasPermission(senderSession, "starnub.bypass.appearoffline", true)) {
                 return true;
            } else if (playerSession.getCharacter().getAccount() != null) {
                return !playerSession.getCharacter().getAccount().getAccountSettings().isAppearOffline();
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public ArrayList<Boolean> canSeePlayerIsHiddenCanSee(Object senderSession, Player playerSession) {
        boolean appearOffline = false;
        if (playerSession.getCharacter().getAccount() != null) {
            appearOffline = playerSession.getCharacter().getAccount().getAccountSettings().isAppearOffline();
        }

        if (senderSession instanceof Player) {
            Player sender = getOnlinePlayerByAnyIdentifier(senderSession);
            boolean canSeePlayer = true;
            if (appearOffline) {
                canSeePlayer = hasPermission(sender, "starnub.bypass.appearoffline", true);
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
        List<String> sortedPlayers = new ArrayList<String>();//TODO broken not working fully?
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
        String playersOnline = "Players Online: "+ connectedPlayers.size() +". NickName "+combinedToken+": ";
        String token;
        String tokenShowStarboundId = "";
        String tokenShowStarNubId = "";
        for (Player playerSession : connectedPlayers.values()) {
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

    public GroupSync getGroupSync() {
        return groupSync;
    }

    public boolean hasBasePermission(Player playerSession, String basePermission){
        if (playerSession.isOp()) {
            return true;
        } else if (playerSession.getCharacter().getAccount() != null) {
            return playerSession.getCharacter().getAccount().hasBasePermission(basePermission);
        } else {
            if (groupSync.getNoAccountGroup() != null) {
                return groupSync.getNoAccountGroup().hasBasePermission(basePermission);
            } else {
                return false;
            }
        }
    }

    public boolean hasPermission(Player playerSession, String permission, boolean checkWildCards){
        String[] perms;
        String perm3 = null;
        boolean fullPermission = false;
        try {
            perms = permission.split("\\.", 3);
            perm3 = perms[2];
            fullPermission = true;
        } catch (ArrayIndexOutOfBoundsException e) {
            perms = permission.split("\\.", 2);
        }
        return hasPermission(playerSession, perms[0], perms[1], perm3, fullPermission, checkWildCards);
    }

    public boolean hasPermission(Player playerSession, String pluginCommandNamePermission, String commandPermission, boolean checkWildCards){
        return hasPermission(playerSession, pluginCommandNamePermission, commandPermission, null, false, checkWildCards);
    }

    public boolean hasPermission(Player playerSession, String pluginCommandNamePermission, String mainArgOrVariable, String commandPermission, boolean checkWildCards){
        return hasPermission(playerSession, pluginCommandNamePermission, commandPermission, mainArgOrVariable, true, checkWildCards);
    }

    @SuppressWarnings("all")
    public boolean hasPermission(Player playerSession, String pluginCommandNamePermission, String commandPermission, String mainArgOrVariable, boolean fullPermission, boolean checkWildCards){
        if (playerSession.isOp() && checkWildCards) {
            return true;
        } else if (playerSession.getCharacter().getAccount() != null) {
             return playerSession.getCharacter().getAccount().hasPermission(pluginCommandNamePermission, commandPermission, mainArgOrVariable, fullPermission, checkWildCards);
        } else {
             if (groupSync.getNoAccountGroup() != null) {
                 return groupSync.getNoAccountGroup().hasPermission(pluginCommandNamePermission, commandPermission, mainArgOrVariable, fullPermission, checkWildCards);
             } else {
                 return false;
             }
        }
    }

    public String getPermissionVariable(Player playerSession, String permission){
        String[] perms;
        try {
            perms = permission.split("\\.", 3);
        } catch (ArrayIndexOutOfBoundsException e) {
            perms = permission.split("\\.", 2);
        }
        return getPermissionVariable(playerSession, perms[0], perms[1]);
    }

    @SuppressWarnings("all")
    public String getPermissionVariable(Player playerSession, String pluginCommandNamePermission, String commandPermission){
        if (playerSession.isOp()) {
            return "OP";
        } else if (playerSession.getCharacter().getAccount() != null) {
            return playerSession.getCharacter().getAccount().getPermissionSpecific(pluginCommandNamePermission, commandPermission);
        } else {
            if (groupSync.getNoAccountGroup() != null) {
                return groupSync.getNoAccountGroup().getPermissionSpecific(pluginCommandNamePermission, commandPermission);
            } else {
                return null;
            }
        }
    }

    public int getPermissionVariableInteger(Player playerSession, String permission){
        String[] perms;
        try {
            perms = permission.split("\\.", 3);
        } catch (ArrayIndexOutOfBoundsException e) {
            perms = permission.split("\\.", 2);
        }
        return getPermissionVariableInteger(playerSession, perms[0], perms[1]);
    }

    public int getPermissionVariableInteger(Player playerSession, String pluginCommandNamePermission, String commandPermission){
        String permissionVariable = getPermissionVariable(playerSession, pluginCommandNamePermission, commandPermission);
        if (permissionVariable == null) {
            return -100001;
        } else if (permissionVariable.equals("OP")) {
            return -100000;
        } else {
            try {
                return Integer.parseInt(permissionVariable);
            } catch (NumberFormatException e) {
                return -100002;
            }
        }
    }
}
