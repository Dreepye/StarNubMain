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

package starnubserver.connections.player.session;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import generic.BanType;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import starbounddata.chat.ChatReceiveChannel;
import starbounddata.chat.ChatSendChannel;
import starbounddata.packets.Packet;
import starbounddata.packets.chat.ChatReceivePacket;
import starbounddata.packets.chat.ChatSendPacket;
import starbounddata.packets.connection.ClientDisconnectRequestPacket;
import starbounddata.packets.connection.ServerDisconnectPacket;
import starnubserver.StarNub;
import starnubserver.connections.player.StarNubConnection;
import starnubserver.connections.player.StarNubProxyConnection;
import starnubserver.connections.player.account.Account;
import starnubserver.connections.player.account.AccountPermission;
import starnubserver.connections.player.character.CharacterIP;
import starnubserver.connections.player.character.PlayerCharacter;
import starnubserver.connections.player.generic.Ban;
import starnubserver.connections.player.generic.StaffEntry;
import starnubserver.connections.player.groups.Group;
import starnubserver.database.tables.Characters;
import starnubserver.database.tables.PlayerSessionLog;
import starnubserver.events.events.StarNubEvent;
import starnubserver.resources.NameBuilder;
import starnubserver.resources.files.GroupsManagement;
import utilities.connectivity.ConnectionType;
import utilities.connectivity.connection.Connection;
import utilities.events.types.StringEvent;
import utilities.exceptions.CollectionDoesNotExistException;
import utilities.strings.StringUtilities;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * StarNub's Player class that represents a sender. This class
 * purpose of this class is to represent a Players Session.
 * <p>
 * The data in this class will always change between log-ins. The
 * Account and Character classes will save the permanent data to
 * a database.
 * <p>
 * All data is based on a "Session" with StarNub.
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
@DatabaseTable(tableName = "PLAYER_SESSION_LOG")
public class PlayerSession {

    private final static PlayerSessionLog PLAYER_SESSION_LOG_DB = PlayerSessionLog.getInstance();
    private final static NameBuilder NAME_BUILDER = NameBuilder.getInstance();

    //TODO DB METHODS

    /* COLUMN NAMES */
    private final static String SESSION_ID_COLUMN = "SESSION_ID";
    private final static String IP_COLUMN = "IP";
    private final static String START_TIME_COLUMN = "START_TIME";
    private final static String END_TIME_COLUMN = "END_TIME";
    private final static String CHARACTER_ID_COLUMN = "CHARACTER_ID";

    @DatabaseField(generatedId = true, columnName = SESSION_ID_COLUMN)
    private volatile int sessionID;

    @DatabaseField(dataType = DataType.STRING, columnName = IP_COLUMN)
    private volatile String sessionIpString;

    @DatabaseField(dataType = DataType.DATE_TIME, columnName = START_TIME_COLUMN)
    private volatile DateTime startTimeUtc;

    @DatabaseField(dataType = DataType.DATE_TIME, columnName = END_TIME_COLUMN)
    private volatile DateTime endTimeUtc;

    @DatabaseField(foreign = true, canBeNull = false, columnName = CHARACTER_ID_COLUMN)
    private volatile PlayerCharacter playerCharacter;

    private volatile long starboundClientId;
    private volatile String gameName;
    private volatile String nickName;
    private volatile String cleanNickName;
    private volatile boolean isOp;
    private volatile boolean afk;

    private final ConnectionType CONNECTION_TYPE;
    private final Connection CONNECTION;

    private final ConcurrentHashMap<String, ConcurrentHashMap<String, ArrayList<String>>> PERMISSIONS = new ConcurrentHashMap<>();

    /**
     * Constructor for database purposes
     */
    public PlayerSession(){
        CONNECTION_TYPE = null;
        CONNECTION = null;
    }

    public PlayerSession(Connection connection, String playerName, UUID playerUUID) {
        if (connection instanceof StarNubProxyConnection){
            CONNECTION_TYPE = ConnectionType.PROXY_IN_GAME;
        } else if (connection instanceof StarNubConnection) {
            CONNECTION_TYPE = ConnectionType.REMOTE;
        } else {
            CONNECTION_TYPE = null;
        }
        CONNECTION = connection;
        InetAddress playerIP = connection.getClientIP();
        StarNub.getConnections().getINTERNAL_IP_WATCHLIST().removeCache(playerIP);
        this.startTimeUtc = DateTime.now();
        this.sessionIpString = StringUtils.remove(playerIP.toString(), "/");
        this.playerCharacter = PlayerCharacter.getPlayerCharacter(playerName, playerUUID);
        this.gameName = playerCharacter.getName();
        this.nickName = playerCharacter.getName();
        this.cleanNickName = playerCharacter.getCleanName();
        CharacterIP characterIP = new CharacterIP(playerCharacter, playerIP, false);
        characterIP.logCharacterIp();
        try {
            this.isOp = StarNub.getConnections().getCONNECTED_PLAYERS().getOPERATORS().collectionContains("uuids");
        } catch (Exception e) {
            this.isOp = false;
        }
        loadPermissions();
        PlayerSessionLog.getInstance().create(this);
        new StarNubEvent("Player_Character_New_Session", playerCharacter);
    }

    public int getSessionID() {
        return sessionID;
    }

    public static PlayerSession getSession(Object playerIdentifier){
        return StarNub.getConnections().getCONNECTED_PLAYERS().getOnlinePlayerByAnyIdentifier(playerIdentifier);
    }

    public String getSessionIpString() {
        return sessionIpString;
    }

    public DateTime getStartTimeUtc() {
        return startTimeUtc;
    }

    public DateTime getEndTimeUtc() {
        return endTimeUtc;
    }

    public void setEndTimeUtc() {
        this.endTimeUtc = DateTime.now();
        PlayerSessionLog.getInstance().update(this);
    }

    public PlayerCharacter getPlayerCharacter() {
        return playerCharacter;
    }

    public long getStarboundClientId() {
        return starboundClientId;
    }

    /**
     * WARNING: Do not use this method, not for public consumption.
     *
     * @param starboundClientId int that set the Starbound Client ID
     */
    public void setStarboundClientId(long starboundClientId) {
        this.starboundClientId = starboundClientId;
    }

    public String getGameName() {
        return gameName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
        this.cleanNickName = StringUtilities.completeClean(nickName);
    }

    public String getCleanNickName() {
        return cleanNickName;
    }

    public boolean isOp() {
        return isOp;
    }

    public void makeOp() throws IOException, CollectionDoesNotExistException {
        this.isOp = true;
        StarNub.getConnections().getCONNECTED_PLAYERS().getOPERATORS().addToOperators(playerCharacter.getUuid());
    }

    public void removeOp() throws IOException {
        this.isOp = false;
        StarNub.getConnections().getCONNECTED_PLAYERS().getOPERATORS().removeFromOperators(playerCharacter.getUuid());
    }

    public boolean isAfk() {
        return afk;
    }

    public void makeAfk() {
        this.afk = true;
    }

    public void removeAfk(){
        this.afk = false;
    }

    public void addBan(Account staffAccount, String staffDescription, BanType banType, DateTime dateExpires){
        addBan(this, staffAccount, staffDescription, banType, dateExpires);
    }

    public static void addBan(PlayerSession playerSession, Account staffAccount, String staffDescription, BanType banType, DateTime dateExpires){
        PlayerCharacter playerCharacter = playerSession.playerCharacter;
        String sessionIpString = playerSession.sessionIpString;
        String identifier = sessionIpString;
        String uuidString = playerCharacter.getUuid().toString();
        StaffEntry staffEntry = new StaffEntry(staffAccount, staffDescription, true);
        switch (banType){
            case IP: {
                break;
            }
            case UUID: {
                identifier = uuidString;
                break;
            }
            case BOTH: {
                new Ban(playerCharacter, identifier, DateTime.now(), dateExpires, staffEntry, true);
                identifier = uuidString;
                break;
            }
            case IP_ALL_UUIDS: {
                uuidBanAll(sessionIpString, staffEntry, dateExpires);
                break;
            }
            case ALL_IPS: {
                ipBanAll(playerCharacter, staffEntry, dateExpires);
                break;
            }
            case ALL_UUIDS: {
                identifier = uuidString;
                uuidBanAll(sessionIpString, staffEntry, dateExpires);
                break;
            }
            case ALL: {
                ipBanAll(playerCharacter, staffEntry, dateExpires);
                uuidBanAll(sessionIpString, staffEntry, dateExpires);
                break;
            }
        }
        new Ban(playerCharacter, identifier, DateTime.now(), dateExpires, staffEntry, true);
    }

    private static void ipBanAll(PlayerCharacter playerCharacter, StaffEntry staffEntry, DateTime dateExpires){
        List<CharacterIP> characterIPs = getIpsByCharacters(playerCharacter);
        for (CharacterIP characterIP : characterIPs){
            new Ban(characterIP.getPlayerCharacter(), characterIP.getSessionIpString(), DateTime.now(), dateExpires, staffEntry, true);
        }
    }

    private static void uuidBanAll(String sessionIpString, StaffEntry staffEntry, DateTime dateExpires){
        List<CharacterIP> characterIPs = getCharactersByIP(sessionIpString);
        for (CharacterIP characterIP : characterIPs){
            Characters.getInstance().refresh(characterIP.getPlayerCharacter());
            PlayerCharacter playerCharacter2 = characterIP.getPlayerCharacter();
            new Ban(playerCharacter2, playerCharacter2.getUuid().toString(), DateTime.now(), dateExpires, staffEntry, true);
        }
    }

    public void removeBan(BanType banType){
        removeBan(this, banType);
    }

    public static void removeBan(PlayerSession playerSession, BanType banType){
        PlayerCharacter playerCharacter = playerSession.playerCharacter;
        String sessionIpString = playerSession.sessionIpString;
        String identifier = sessionIpString;
        String uuidString = playerCharacter.getUuid().toString();
        switch (banType){
            case IP: {
                break;
            }
            case UUID: {
                identifier = uuidString;
                break;
            }
            case ALL_IPS: {
                ipBanRemoveAll(playerCharacter);
                break;
            }
            case ALL_UUIDS: {
                identifier = uuidString;
                uuidBanRemoveAll(sessionIpString);
                break;
            }
            case IP_ALL_UUIDS: {
                uuidBanRemoveAll(sessionIpString);
                break;
            }
            case ALL: {
                ipBanRemoveAll(playerCharacter);
                uuidBanRemoveAll(sessionIpString);
                break;
            }
        }
        StarNub.getConnections().getBANSList().banRemoval(identifier);
    }

    private static void ipBanRemoveAll(PlayerCharacter playerCharacter){
        List<CharacterIP> characterIPs = getIpsByCharacters(playerCharacter);
        for (CharacterIP characterIP : characterIPs){
            String sessionIpString = characterIP.getSessionIpString();
            StarNub.getConnections().getBANSList().banRemoval(sessionIpString);
        }
    }

    private static void uuidBanRemoveAll(String sessionIpString){
        List<CharacterIP> characterIPs = getCharactersByIP(sessionIpString);
        for (CharacterIP characterIP : characterIPs){
            Characters.getInstance().refresh(characterIP.getPlayerCharacter());
            PlayerCharacter playerCharacter2 = characterIP.getPlayerCharacter();
            String uuid = playerCharacter2.getUuid().toString();
            StarNub.getConnections().getBANSList().banRemoval(uuid);
        }
    }

    public static List<CharacterIP> getCharactersByIP(String sessionIpString) {
        return CharacterIP.getCharacterIpLogsByIP(sessionIpString);
    }

    public static List<CharacterIP> getIpsByCharacters(PlayerCharacter playerCharacter){
        return CharacterIP.getCharacterIpLogsByCharacter(playerCharacter);
    }

    //TODO Get game tags
    //TODO Get console tags

    public void sendChatMessage(Object sender, ChatReceiveChannel channel, String message) {
        ChannelHandlerContext CLIENT_CTX = CONNECTION.getCLIENT_CTX();
        if (CONNECTION_TYPE == ConnectionType.PROXY_IN_GAME) {
            String nameOfSender = NAME_BUILDER.msgUnknownNameBuilder(sender, true, false);
            ChatReceivePacket chatReceivePacket = new ChatReceivePacket(CLIENT_CTX, channel, "Server", 1, nameOfSender, message);
            chatReceivePacket.routeToDestination();
            StarNub.getLogger().cChatPrint("StarNub", message, chatReceivePacket);
        } else if (CONNECTION_TYPE == ConnectionType.REMOTE) {
            //TODO - REMOTE
        }
    }

    public void sendServerChatMessage(ChatSendChannel channel, String message) {
        ChannelHandlerContext SERVER_CTX = ((StarNubProxyConnection) CONNECTION).getSERVER_CTX();
        if (CONNECTION_TYPE == ConnectionType.PROXY_IN_GAME) {
            ChatSendPacket chatSendPacket = new ChatSendPacket(SERVER_CTX, channel, message);
            chatSendPacket.routeToDestination();
        }  else if (CONNECTION_TYPE == ConnectionType.REMOTE) {
            //TODO - REMOTE
        }
    }

    public void removeConnection() {
        ChannelHandlerContext CLIENT_CTX = CONNECTION.getCLIENT_CTX();
        if (CONNECTION_TYPE == ConnectionType.PROXY_IN_GAME) {
            StarNub.getConnections().getCONNECTED_PLAYERS().remove(CLIENT_CTX);
        }
    }

    public boolean disconnect() {
        boolean disconnect = CONNECTION.disconnect();
        disconnectCleanUp("");
        return disconnect;
    }

    public boolean disconnectReason(String reason) {
        ChannelHandlerContext CLIENT_CTX = CONNECTION.getCLIENT_CTX();
        boolean isConnectedCheck = CONNECTION.disconnect();
        if (!isConnectedCheck) {
            PlayerSession playerSession = StarNub.getConnections().getCONNECTED_PLAYERS().remove(CLIENT_CTX);
            new StringEvent("Player_Disconnect_" + reason, playerSession);
            disconnectCleanUp(reason);
        }
        return isConnectedCheck;
    }

    private void disconnectCleanUp(String reason) {
        /* Each type of connection*/
        setEndTimeUtc();
        playerCharacter.updatePlayedTimeLastSeen();

        /* In-game connections only */
        if (CONNECTION_TYPE == ConnectionType.PROXY_IN_GAME) {
            ChannelHandlerContext CLIENT_CTX = CONNECTION.getCLIENT_CTX();
            ChannelHandlerContext SERVER_CTX = ((StarNubProxyConnection) CONNECTION).getSERVER_CTX();
            if (!reason.equalsIgnoreCase("quit")) {
                new ClientDisconnectRequestPacket(SERVER_CTX);
                new ServerDisconnectPacket(CLIENT_CTX, "");
            }
        }
    }

    //TODO remove from op

    /* Permission Methods*/

    public void reloadPermissions() {
        PERMISSIONS.clear();
        loadPermissions();
    }

    public void loadPermissions(){
        Account account = playerCharacter.getAccount();
        LinkedHashSet<String> permissions = new LinkedHashSet<>();
        if(account == null){
            GroupsManagement.getInstance().getGROUPS().values().stream().filter(group -> group.getType().equalsIgnoreCase("noaccount")).forEach(group -> {
                permissions.addAll(group.getGROUP_PERMISSIONS());
            });
        } else {
            List<AccountPermission> accountPermissions = AccountPermission.getAccountPermissionsByAccount(account);
            permissions.addAll(accountPermissions.stream().map(AccountPermission::getPermission).collect(Collectors.toList()));
            TreeMap<Integer, Group> allGroups = account.getAllGroupsOrderedByRank();
            NavigableMap<Integer, Group> navigableMap = allGroups.descendingMap();
            for (Group group : navigableMap.values()){
                permissions.addAll(group.getGROUP_PERMISSIONS());
            }
        }
        permissions.forEach(this::addPermissionToMap);
        System.out.println(PERMISSIONS);
    }

    public String addPermission(String permission){
        Account account = playerCharacter.getAccount();
        if (account == null){
            return "Could not add permission \"" + permission + "\" to \"" + playerCharacter.getName() + "\". No account was found.";
        }
        new AccountPermission(account, permission, true);
        reloadPermissions();
        if (hasPermission(permission, false)){
            return "Permission \"" + permission + "\" added to \"" + playerCharacter.getName() + "\"'s account.";
        }
        return "Critical Error Adding Permission";
    }

    private void addPermissionToMap(String permission){
        String[] permissionBreak = permission.split("\\.", 3);
        if (permissionBreak.length == 3) {
            PERMISSIONS.putIfAbsent(permissionBreak[0], new ConcurrentHashMap<>());
            PERMISSIONS.get(permissionBreak[0]).putIfAbsent(permissionBreak[1], new ArrayList<>());
            PERMISSIONS.get(permissionBreak[0]).get(permissionBreak[1]).add(permissionBreak[2]);
        } else if (permissionBreak.length == 2 ) {
            PERMISSIONS.putIfAbsent(permissionBreak[0], new ConcurrentHashMap<>());
            PERMISSIONS.get(permissionBreak[0]).putIfAbsent(permissionBreak[1], new ArrayList<>());
        } else if (permissionBreak.length == 1){
            PERMISSIONS.putIfAbsent(permissionBreak[0], new ConcurrentHashMap<>());
        }
    }

    public String deletePermission(String permission){
        Account account = playerCharacter.getAccount();
        if (account == null){
            return "Could not delete permission \"" + permission + "\" from \"" + playerCharacter.getName() + "\". No account was found.";
        }
        AccountPermission accountPermission = AccountPermission.getAccountPermissionByAccountFirstMatch(account, permission);
        accountPermission.deleteFromDatabase();
        deletePermissionFromMap(permission);
        if (hasPermission(permission, false)){
            return "Permission \"" + permission + "\" was deleted from \"" + playerCharacter.getName() + "\"'s account.";
        }
        return "Critical Error Adding Permission";
    }

    private void deletePermissionFromMap(String permission){
        String[] permissionBreak = permission.split("\\.", 3);
        if (permissionBreak.length == 3) {
            PERMISSIONS.get(permissionBreak[0]).get(permissionBreak[1]).remove(permissionBreak[2]);
            if (PERMISSIONS.get(permissionBreak[0]).get(permissionBreak[1]).isEmpty()) {
                PERMISSIONS.get(permissionBreak[0]).remove(permissionBreak[1]);
                if (PERMISSIONS.get(permissionBreak[0]).isEmpty()){
                    PERMISSIONS.remove(permissionBreak[0]);
                }
            }
        } else if (permissionBreak.length == 2 ) {
            PERMISSIONS.get(permissionBreak[0]).remove(permissionBreak[1]);
            if (PERMISSIONS.get(permissionBreak[0]).isEmpty()) {
                PERMISSIONS.remove(permissionBreak[0]);
            }
        } else if (permissionBreak.length == 1){
            PERMISSIONS.remove(permissionBreak[0]);
        }
    }

    public boolean hasBasePermission(String basePermission) {
        return isOp || PERMISSIONS.containsKey("*") || PERMISSIONS.containsKey(basePermission);
    }

    private boolean hasSubPermission(String basePermission, String subPermission, boolean checkWildCards){
        ConcurrentHashMap<String, ArrayList<String>> concurrentHashMap = PERMISSIONS.get(basePermission);
        if (concurrentHashMap == null){
            return false;
        }
        if (checkWildCards){
            return concurrentHashMap.containsKey("*") || concurrentHashMap.containsKey(subPermission);
        } else {
            return concurrentHashMap.containsKey(subPermission);
        }
    }

    private boolean hasFullPermission(String basePermission, String subPermission, String fullPermission, boolean checkWildCards){
        ConcurrentHashMap<String, ArrayList<String>> concurrentHashMap = PERMISSIONS.get(basePermission);
        if (concurrentHashMap == null) {
            return false;
        }
        ArrayList<String> strings = concurrentHashMap.get(subPermission);
        if (strings == null){
            return false;
        }
        if (checkWildCards){
            return strings.contains("*") || strings.contains(fullPermission);
        } else {
            return strings.contains(fullPermission);
        }
    }

    public boolean hasPermission(String permission, boolean checkWildCards) {
        if (isOp && checkWildCards){
            return true;
        } else {
            String[] permissions = permission.split("\\.", 3);
            boolean fullPermission = permissions.length == 3;
            if (fullPermission){
                return hasFullPermission(permissions[0], permissions[1], permissions[2], checkWildCards);
            } else {
                return hasSubPermission(permissions[0], permissions[1], checkWildCards);
            }
        }
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will return if a player has a permission. It will check OP and Wildcards first then for partial or full permission.
     * <P>
     * Example: {plugname}.{subpermission}.{command}
     *          starnub.server.shutdown
     *          starnub.server.start
     *          starnub.server.*
     *          starnub.*
     * <br>
     * The first line would give me shutdown. The second line would be start. The third line give me anything under
     * starnub.server, The fourth line gives me all PERMISSIONS for the starnub related commands that fall under starnub. .
     *
     * <p>
     * @param basePermission String representing the plugin command name to check
     * @param subPermission String representing the plugins command specific sub permission
     * @param fullPermission String representing the plugins sub permission specific Command
     * @return boolean if the account has the permission
     */
    public boolean hasPermission(String basePermission, String subPermission, String fullPermission, boolean checkWildCards) {
        return isOp && checkWildCards || hasFullPermission(basePermission, subPermission, fullPermission, checkWildCards);
    }

    public String getSpecificPermission(String basePermission, String subPermission) {
        ConcurrentHashMap<String, ArrayList<String>> concurrentHashMap = PERMISSIONS.get(basePermission);
        if (concurrentHashMap == null){
            return null;
        }
        return concurrentHashMap.get(subPermission).get(0);
    }

    public String getSpecificPermission(String permission){
        String[] permissions = permission.split("\\.", 3);
        return getSpecificPermission(permissions[0], permissions[1]);
    }

    public int getSpecificPermissionInteger(String permission){
        String[] permissions = permission.split("\\.", 3);
        return getSpecificPermissionInteger(permissions[0], permissions[1]);
    }

    public int getSpecificPermissionInteger(String basePermission, String subPermission) {
        if (isOp){
            return -10000;
        }
        String permissionVariable = getSpecificPermission(basePermission, subPermission);
        if (permissionVariable == null) {
            return -100001;
        }
        try {
            return Integer.parseInt(permissionVariable);
        } catch (NumberFormatException e) {
            return -100002;
        }
    }

    public static PlayerSession getPlayerSession(Packet packet){
        return StarNub.getConnections().getCONNECTED_PLAYERS().getOnlinePlayerByAnyIdentifier(packet);
    }

    /* DB Methods */

    public static void retreiveSession(PlayerCharacter playerCharacter){

    }

    @Override
    public String toString() {
        return "Player{" +
                "sessionID=" + sessionID +
                ", sessionIpString='" + sessionIpString + '\'' +
                ", startTimeUtc=" + startTimeUtc +
                ", endTimeUtc=" + endTimeUtc +
                ", playerCharacter=" + playerCharacter +
                ", starboundClientId=" + starboundClientId +
                ", gameName='" + gameName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", cleanNickName='" + cleanNickName + '\'' +
                ", isOp=" + isOp +
                ", afk=" + afk +
                "} " + super.toString();
    }
}




