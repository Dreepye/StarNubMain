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

package org.starnub.starnubserver.connections.player.session;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.starnub.starbounddata.packets.Packet;
import org.starnub.starbounddata.packets.chat.ChatReceivePacket;
import org.starnub.starbounddata.packets.chat.ChatSendPacket;
import org.starnub.starbounddata.packets.connection.ClientDisconnectRequestPacket;
import org.starnub.starbounddata.packets.connection.ServerDisconnectPacket;
import org.starnub.starbounddata.types.chat.ChatSendMode;
import org.starnub.starbounddata.types.chat.Mode;
import org.starnub.starbounddata.types.color.GameColors;
import org.starnub.starnubdata.generic.BanType;
import org.starnub.starnubdata.generic.DisconnectReason;
import org.starnub.starnubserver.Connections;
import org.starnub.starnubserver.StarNub;
import org.starnub.starnubserver.connections.player.StarNubConnection;
import org.starnub.starnubserver.connections.player.StarNubProxyConnection;
import org.starnub.starnubserver.connections.player.account.Account;
import org.starnub.starnubserver.connections.player.account.AccountPermission;
import org.starnub.starnubserver.connections.player.character.CharacterIP;
import org.starnub.starnubserver.connections.player.character.PlayerCharacter;
import org.starnub.starnubserver.connections.player.generic.Ban;
import org.starnub.starnubserver.connections.player.generic.StaffEntry;
import org.starnub.starnubserver.connections.player.groups.Group;
import org.starnub.starnubserver.connections.player.session.location.ExactLocation;
import org.starnub.starnubserver.connections.player.session.location.ShipsLocation;
import org.starnub.starnubserver.database.tables.Characters;
import org.starnub.starnubserver.database.tables.PlayerSessionLog;
import org.starnub.starnubserver.events.events.DisconnectEvent;
import org.starnub.starnubserver.events.events.StarNubEvent;
import org.starnub.starnubserver.resources.NameBuilder;
import org.starnub.starnubserver.resources.connections.Players;
import org.starnub.starnubserver.resources.files.GroupsManagement;
import org.starnub.utilities.cache.exceptions.CollectionDoesNotExistException;
import org.starnub.utilities.connectivity.ConnectionType;
import org.starnub.utilities.connectivity.connection.Connection;
import org.starnub.utilities.strings.StringUtilities;

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
    private final static String TYPE_COLUMN = "TYPE";
    private final static String IP_COLUMN = "IP";
    private final static String START_TIME_COLUMN = "START_TIME";
    private final static String END_TIME_COLUMN = "END_TIME";
    private final static String CHARACTER_ID_COLUMN = "CHARACTER_ID";

    @DatabaseField(generatedId = true, columnName = SESSION_ID_COLUMN)
    private volatile int sessionID;

    @DatabaseField(dataType = DataType.ENUM_STRING, columnName = TYPE_COLUMN)
    private final ConnectionType CONNECTION_TYPE;

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
    private volatile boolean op;
    private volatile boolean afk;
    private volatile long idleTime;
    private volatile ExactLocation location;
    private volatile ShipsLocation ship;

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
        this.idleTime = 0L;
        try {
            this.op = StarNub.getConnections().getCONNECTED_PLAYERS().getOPERATORS().collectionContains("uuids");
        } catch (Exception e) {
            this.op = false;
        }
        loadPermissions();
        PlayerSessionLog.getInstance().create(this);
        new StarNubEvent("Player_New_Session", playerCharacter);
    }

    public int getSessionID() {
        return sessionID;
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
        return op;
    }

    public void makeOp() throws IOException, CollectionDoesNotExistException {
        this.op = true;
        StarNub.getConnections().getCONNECTED_PLAYERS().getOPERATORS().addToOperators(playerCharacter.getUuid());
    }

    public void removeOp() throws IOException {
        this.op = false;
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

    public ConnectionType getCONNECTION_TYPE() {
        return CONNECTION_TYPE;
    }

    public Connection getCONNECTION() {
        return CONNECTION;
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

    public void sendBroadcastMessageToClient(Object sender, String message){
        sendClientMessage(sender, this, Mode.BROADCAST, null, message);
    }

//    public void channelMessageToClient(String message){}

    public void sendWhisperMessageToClient(PlayerSession sender, PlayerSession receiver, String message){
        String nameOfSender = NAME_BUILDER.msgUnknownNameBuilder(sender, true, false);
        String nameOfReceiver = NAME_BUILDER.msgUnknownNameBuilder(receiver, true, false);
        String defaultNameColor = GameColors.getInstance().getDefaultNameColor();
        String newName = nameOfSender + defaultNameColor + " -> " + nameOfReceiver + defaultNameColor;
        sendClientMessage(sender, this, Mode.WHISPER, newName, message);
        sendClientMessage(sender, receiver, Mode.WHISPER, newName, message);
    }

    public void sendCommandMessageToClient(Object sender, String message){
        sendClientMessage(sender, this, Mode.COMMAND_RESULT, null, message);
    }

    private static void sendClientMessage(Object sender, PlayerSession playerSession, Mode mode, String senderName, String message){
        if(senderName == null){
            senderName = NAME_BUILDER.msgUnknownNameBuilder(sender, true, false);
        }
        ConnectionType connectionType = playerSession.getCONNECTION_TYPE();
        ChatReceivePacket chatReceivePacket = new ChatReceivePacket(mode, "Server", 1, senderName, message);
        if (connectionType == ConnectionType.PROXY_IN_GAME) {
            sendPacketToPlayerNoFlush(playerSession, chatReceivePacket);
        } else if (connectionType == ConnectionType.REMOTE) {
            sendRemote(playerSession, chatReceivePacket);
        }
        StarNub.getLogger().cChatPrint(sender, playerSession, mode, message);
    }


    public static void sendChatBroadcastToClientsAll(Object sender, String message){
        HashSet<PlayerSession> allPlayers = Connections.getInstance().getCONNECTED_PLAYERS().getOnlinePlayersSessions();
        broadcastToClients(sender, message, Mode.BROADCAST, allPlayers, null);
    }

    public static void sendChatBroadcastToClientsGroup(Object sender, HashSet<PlayerSession> sendList, String message){
        broadcastToClients(sender, message, Mode.BROADCAST, sendList, null);
    }

    public static void sendChatBroadcastToClientsAll(Object sender, HashSet<PlayerSession> ignoredList, String message){
        HashSet<PlayerSession> allPlayers = Connections.getInstance().getCONNECTED_PLAYERS().getOnlinePlayersSessions();
        broadcastToClients(sender, message, Mode.BROADCAST, allPlayers, ignoredList);
    }

    public static void sendChatBroadcastToClientsGroup(Object sender, HashSet<PlayerSession> sendList, HashSet<PlayerSession> ignoredList, String message){
        broadcastToClients(sender, message, Mode.BROADCAST, sendList, ignoredList);
    }

//    public static void channelBroadcastToClientsAll(String message){}
//    public static void channelBroadcastToClientsGroup(HashSet<PlayerSession> sendList, String message){}
//    public static void channelBroadcastToClientsAll(HashSet<PlayerSession> ignoredList, String message){}
//    public static void channelBroadcastToClientsGroup(HashSet<PlayerSession> sendList, HashSet<PlayerSession> ignoredList, String message){}

    public static void sendWhisperBroadcastToClientsAll(Object sender, String message){
        HashSet<PlayerSession> allPlayers = Connections.getInstance().getCONNECTED_PLAYERS().getOnlinePlayersSessions();
        broadcastToClients(sender, message, Mode.WHISPER, allPlayers, null);
    }

    public static void sendWhisperBroadcastToClientsGroup(Object sender, HashSet<PlayerSession> sendList, String message){
        broadcastToClients(sender, message, Mode.WHISPER, sendList, null);
    }

    public static void sendWhisperBroadcastToClientsAll(Object sender,  HashSet<PlayerSession> ignoredList, String message){
        HashSet<PlayerSession> allPlayers = Connections.getInstance().getCONNECTED_PLAYERS().getOnlinePlayersSessions();
        broadcastToClients(sender, message, Mode.WHISPER, allPlayers, ignoredList);
    }

    public static void sendWhisperBroadcastToClientsGroup(Object sender,  HashSet<PlayerSession> sendList, HashSet<PlayerSession> ignoredList, String message){
        broadcastToClients(sender, message, Mode.WHISPER, sendList, ignoredList);
    }

    public static void sendCommandBroadcastToClientsAll(Object sender, String message){
        HashSet<PlayerSession> allPlayers = Connections.getInstance().getCONNECTED_PLAYERS().getOnlinePlayersSessions();
        broadcastToClients(sender, message, Mode.COMMAND_RESULT, allPlayers, null);
    }

    public static void sendCommandBroadcastToClientsGroup(Object sender, HashSet<PlayerSession> sendList, String message){
        broadcastToClients(sender, message, Mode.COMMAND_RESULT, sendList, null);
    }

    public static void sendCommandBroadcastToClientsAll(Object sender, HashSet<PlayerSession> ignoredList, String message){
        HashSet<PlayerSession> allPlayers = Connections.getInstance().getCONNECTED_PLAYERS().getOnlinePlayersSessions();
        broadcastToClients(sender, message, Mode.COMMAND_RESULT, allPlayers, ignoredList);
    }

    public static void sendCommandBroadcastToClientsGroup(Object sender, HashSet<PlayerSession> sendList, HashSet<PlayerSession> ignoredList, String message){
        broadcastToClients(sender, message, Mode.COMMAND_RESULT, sendList, ignoredList);
    }

    private static void broadcastToClients(Object sender, String message, Mode mode, HashSet<PlayerSession> sendList, HashSet<PlayerSession> ignoredList){
        String senderName = NAME_BUILDER.msgUnknownNameBuilder(sender, true, false);
        String sendListFinal = getSentenceNameList(sendList);
        ChatReceivePacket chatReceivePacket = new ChatReceivePacket(mode, "Server", 1, senderName, message);
        sendPacketToGroupNoFlush(chatReceivePacket, sendList, ignoredList);
        StarNub.getLogger().cChatPrint(sender, sendListFinal, mode, message);
    }

    public void sendBroadcastMessageToServer(Object sender, String message){
        sendServerMessage(sender, this, ChatSendMode.BROADCAST, message);
    }

    public void sendLocalMessageToServer(Object sender, String message){
        sendServerMessage(sender, this, ChatSendMode.LOCAL, message);
    }

    public void sendPartyMessageToServer(Object sender, String message){
        sendServerMessage(sender, this, ChatSendMode.PARTY, message);
    }

    private static void sendServerMessage(Object sender, PlayerSession playerSession, ChatSendMode chatSendMode, String message){
        ConnectionType connectionType = playerSession.CONNECTION_TYPE;
        if (connectionType == ConnectionType.PROXY_IN_GAME) {
            ChatSendPacket chatSendPacket = new ChatSendPacket(chatSendMode, message);
            sendPacketToServer(playerSession, chatSendPacket);
            String senderString = NAME_BUILDER.cUnknownNameBuilder(sender, true, false);
            StarNub.getLogger().cChatPrint(playerSession, "Server (Sender: " + senderString + ")", chatSendMode, message);
        }
    }

    public static void sendChatBroadcastToServerAll(Object sender, HashSet<PlayerSession> sendList, String message){
        HashSet<PlayerSession> allPlayers = Connections.getInstance().getCONNECTED_PLAYERS().getOnlinePlayersSessions();
        broadcastServerMessage(sender, message, ChatSendMode.BROADCAST, allPlayers, null);
    }

    public static void sendChatBroadcastToServerGroup(Object sender, HashSet<PlayerSession> sendList, String message){
        broadcastServerMessage(sender, message, ChatSendMode.BROADCAST, sendList, null);
    }

    public static void sendChatBroadcastToServerAll(Object sender, HashSet<PlayerSession> sendList, HashSet<PlayerSession> ignoredList, String message){
        HashSet<PlayerSession> allPlayers = Connections.getInstance().getCONNECTED_PLAYERS().getOnlinePlayersSessions();
        broadcastServerMessage(sender, message, ChatSendMode.BROADCAST, allPlayers, ignoredList);
    }

    public static void sendChatBroadcastToServerGroup(Object sender, HashSet<PlayerSession> sendList, HashSet<PlayerSession> ignoredList, String message){
        broadcastServerMessage(sender, message, ChatSendMode.BROADCAST, sendList, ignoredList);
    }

    public static void sendLocalBroadcastToServerAll(Object sender, HashSet<PlayerSession> sendList, String message){
        HashSet<PlayerSession> allPlayers = Connections.getInstance().getCONNECTED_PLAYERS().getOnlinePlayersSessions();
        broadcastServerMessage(sender, message, ChatSendMode.LOCAL, allPlayers, null);
    }

    public static void sendLocalBroadcastToServerGroup(Object sender, HashSet<PlayerSession> sendList, String message){
        broadcastServerMessage(sender, message, ChatSendMode.LOCAL, sendList, null);
    }

    public static void sendLocalBroadcastToServerAll(Object sender, HashSet<PlayerSession> sendList, HashSet<PlayerSession> ignoredList, String message){
        HashSet<PlayerSession> allPlayers = Connections.getInstance().getCONNECTED_PLAYERS().getOnlinePlayersSessions();
        broadcastServerMessage(sender, message, ChatSendMode.LOCAL, allPlayers, ignoredList);
    }

    public static void sendLocalBroadcastToServerGroup(Object sender, HashSet<PlayerSession> sendList, HashSet<PlayerSession> ignoredList, String message){
        broadcastServerMessage(sender, message, ChatSendMode.LOCAL, sendList, ignoredList);
    }

    public static void sendPartyBroadcastToServerAll(Object sender, HashSet<PlayerSession> sendList, PlayerSession receiver, String message){
        HashSet<PlayerSession> allPlayers = Connections.getInstance().getCONNECTED_PLAYERS().getOnlinePlayersSessions();
        broadcastServerMessage(sender, message, ChatSendMode.PARTY, allPlayers, null);
    }

    public static void sendPartyBroadcastToServerGroup(Object sender, HashSet<PlayerSession> sendList, PlayerSession receiver, String message){
        broadcastServerMessage(sender, message, ChatSendMode.PARTY, sendList, null);
    }

    public static void sendPartyBroadcastToServerAll(Object sender, HashSet<PlayerSession> sendList, PlayerSession receiver, HashSet<PlayerSession> ignoredList, String message){
        HashSet<PlayerSession> allPlayers = Connections.getInstance().getCONNECTED_PLAYERS().getOnlinePlayersSessions();
        broadcastServerMessage(sender, message, ChatSendMode.PARTY, allPlayers, ignoredList);
    }

    public static void sendPartyBroadcastToServerGroup(Object sender, HashSet<PlayerSession> sendList, PlayerSession receiver, HashSet<PlayerSession> ignoredList, String message){
        broadcastServerMessage(sender, message, ChatSendMode.PARTY, sendList, ignoredList);
    }

    private static void broadcastServerMessage(Object sender, String message, ChatSendMode chatSendMode, HashSet<PlayerSession> sendList, HashSet<PlayerSession> ignoredList){
        String senderString = NAME_BUILDER.cUnknownNameBuilder(sender, true, false);
        String sendListFinal = getSentenceNameList(sendList);
        ChatSendPacket chatSendPacket = new ChatSendPacket(chatSendMode, message);
        sendPacketToGroupNoFlush(chatSendPacket, sendList, ignoredList);
        StarNub.getLogger().cChatPrint(sendListFinal, "Server (Sender: " + senderString + ")", chatSendMode, message);
    }

    public void sendPacketToPlayer(Packet packet){
        sendPacketToPlayer(this, packet);
    }

    public void sendPacketToPlayerNoFlush(Packet packet){
        sendPacketToPlayerNoFlush(this, packet);
    }

    public void sendPacketToServer(Packet packet){
        sendPacketToServer(this, packet);
    }

    public void sendPacketToServerNoFlush(Packet packet){
        sendPacketToServerNoFlush(this, packet);
    }

    private static void sendPacketToPlayer(PlayerSession playerSession, Packet packet) {
        ConnectionType connectionType = playerSession.CONNECTION_TYPE;
        if (connectionType == ConnectionType.PROXY_IN_GAME) {
            ChannelHandlerContext clientCtx = playerSession.getCONNECTION().getCLIENT_CTX();
            packet.routeToDestination(clientCtx);
        } else if (connectionType == ConnectionType.REMOTE) {
            sendRemote(playerSession, packet);
        }
    }

    private static void sendPacketToPlayerNoFlush(PlayerSession playerSession, Packet packet) {
        ConnectionType connectionType = playerSession.CONNECTION_TYPE;
        if (connectionType == ConnectionType.PROXY_IN_GAME) {
            ChannelHandlerContext clientCtx = playerSession.getCONNECTION().getCLIENT_CTX();
            packet.routeToDestinationNoFlush(clientCtx);
        } else if (connectionType == ConnectionType.REMOTE) {
            sendRemote(playerSession, packet);
        }
    }

    private static void sendPacketToServer(PlayerSession playerSession, Packet packet) {
        ConnectionType connectionType = playerSession.CONNECTION_TYPE;
        if (connectionType == ConnectionType.PROXY_IN_GAME) {
            Connection connection = playerSession.getCONNECTION();
            ChannelHandlerContext serverCtx = ((StarNubProxyConnection) connection).getSERVER_CTX();
            packet.routeToDestination(serverCtx);
        }
    }

    private static void sendPacketToServerNoFlush(PlayerSession playerSession, Packet packet) {
        ConnectionType connectionType = playerSession.CONNECTION_TYPE;
        if (connectionType == ConnectionType.PROXY_IN_GAME) {
            Connection connection = playerSession.getCONNECTION();
            ChannelHandlerContext serverCtx = ((StarNubProxyConnection) connection).getSERVER_CTX();
            packet.routeToDestinationNoFlush(serverCtx);
        }
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Try to use NO FLUSH as this causes extra system calls. This will send this packet to multiple people. If they are a remote connection it may be converted into StarNub protocol for that session.
     *
     * @param sendList    HashSet of ChannelHandlerContext to this packet to
     */
    public static void sendPacketToGroup(Packet packet, HashSet<PlayerSession> sendList) {
        sendPacketToGroup(packet, sendList, null, true);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Try to use NO FLUSH as this causes extra system calls. This will send this packet to multiple people. If they are a remote connection it may be converted into StarNub protocol for that session.
     *
     * @param sendList    HashSet of ChannelHandlerContext to this packet to
     * @param ignoredList HashSet of ChannelHandlerContext to not send the message too
     */
    public static void sendPacketToGroup(Packet packet, HashSet<PlayerSession> sendList, HashSet<PlayerSession> ignoredList) {
        sendPacketToGroup(packet, sendList, ignoredList, true);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will send this packet to multiple people. If they are a remote connection it may be converted into StarNub protocol for that session.
     *
     * @param sendList    HashSet of ChannelHandlerContext to this packet to
     */
    public static void sendPacketToGroupNoFlush(Packet packet, HashSet<PlayerSession> sendList) {
        sendPacketToGroup(packet, sendList, null, false);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will send this packet to multiple people. If they are a remote connection it may be converted into StarNub protocol for that session.
     *
     * @param packet      Packet representing the packet to be routed
     * @param sendList    HashSet of ChannelHandlerContext to this packet to
     * @param ignoredList HashSet of ChannelHandlerContext to not send the message too
     */
    public static void sendPacketToGroupNoFlush(Packet packet, HashSet<PlayerSession> sendList, HashSet<PlayerSession> ignoredList) {
        sendPacketToGroup(packet, sendList, ignoredList, false);
    }

    private static void sendPacketToGroup(Packet packet, HashSet<PlayerSession> sendList, HashSet<PlayerSession> ignoredList, boolean flush){
        ByteBuf byteBufPacket = packet.packetToMessageEncoder();
        int refCnt;
        int ignoreListSize = 0;
        if (ignoredList != null) {
            sendList.removeAll(ignoredList);
            ignoreListSize = ignoredList.size(); /* Subtract this count */
        }
        int sendListSize = sendList.size();
        refCnt = sendListSize - ignoreListSize; /* Message ref count */
        if (sendListSize > 1){
            refCnt = refCnt - 1; /* This subtracts the one ref count of Netty.io */
            byteBufPacket.retain(refCnt);
        }
        if (!sendList.isEmpty()) {
            for (PlayerSession playerSession : sendList) {
                ChannelHandlerContext ctx = playerSession.getCONNECTION().getCLIENT_CTX();
                ConnectionType connectionType = playerSession.getCONNECTION_TYPE();
                if (connectionType == ConnectionType.PROXY_IN_GAME) {
                    if (flush) {
                        ctx.writeAndFlush(byteBufPacket, ctx.voidPromise());
                    } else {
                        ctx.write(byteBufPacket, ctx.voidPromise());
                    }
                } else if (connectionType == ConnectionType.REMOTE) {
                    sendRemote(playerSession, packet);
                }
            }
        }
    }

    private static void sendRemote(PlayerSession packet, Packet ctx){
        //REQUIRES STARNUB PROTOCOL - Needs to strip colors on REMOTE clients
    }

    private static String getSentenceNameList(HashSet<PlayerSession> playerSessions){
        String sendListNames = "";
        for (PlayerSession playerSession : playerSessions){
            String cleanName = playerSession.getPlayerCharacter().getCleanName();
            sendListNames = sendListNames + cleanName + ", ";
        }
        return StringUtilities.trimCommaForPeriod(sendListNames);
    }

    public void removeConnection() {
        ChannelHandlerContext CLIENT_CTX = CONNECTION.getCLIENT_CTX();
        if (CONNECTION_TYPE == ConnectionType.PROXY_IN_GAME) {
            StarNub.getConnections().getCONNECTED_PLAYERS().remove(CLIENT_CTX);
        }
    }

    public boolean disconnect() {
        boolean disconnect = CONNECTION.disconnect();
        disconnectCleanUp(DisconnectReason.OTHER);
        return disconnect;
    }

    public boolean disconnectReason(DisconnectReason reason) {
        ChannelHandlerContext CLIENT_CTX = CONNECTION.getCLIENT_CTX();
        boolean isConnectedCheck = CONNECTION.disconnect();
        if (!isConnectedCheck) {
            PlayerSession playerSession = StarNub.getConnections().getCONNECTED_PLAYERS().remove(CLIENT_CTX);
            new DisconnectEvent(playerSession, reason);
            disconnectCleanUp(reason);
        }
        return isConnectedCheck;
    }

    private void disconnectCleanUp(DisconnectReason reason) {
        /* Each type of connection*/
        setEndTimeUtc();
        playerCharacter.updatePlayedTimeLastSeen();
        /* In-game connections only */
        if (CONNECTION_TYPE == ConnectionType.PROXY_IN_GAME) {
            if (reason != DisconnectReason.QUIT) {
                ClientDisconnectRequestPacket clientDisconnectRequestPacket = new ClientDisconnectRequestPacket();
                ServerDisconnectPacket serverDisconnectPacket = new ServerDisconnectPacket("");
                sendPacketToServer(clientDisconnectRequestPacket);
                sendPacketToPlayer(serverDisconnectPacket);
            }
        }
    }

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
        return op || PERMISSIONS.containsKey("*") || PERMISSIONS.containsKey(basePermission);
    }

    private boolean hasSubPermission(String basePermission, String subPermission, boolean checkWildCards){
        if (PERMISSIONS.containsKey("*") && checkWildCards){
            return true;
        }
        ConcurrentHashMap<String, ArrayList<String>> concurrentHashMap = PERMISSIONS.get(basePermission);
        if (concurrentHashMap == null){
            return false;
        }
        if (checkWildCards){
            return concurrentHashMap.containsKey("*") || concurrentHashMap.containsKey(subPermission);
        } else {
            return false;
        }
    }

    private boolean hasFullPermission(String basePermission, String subPermission, String fullPermission, boolean checkWildCards){
        if (PERMISSIONS.containsKey("*") && checkWildCards){
            return true;
        }
        ConcurrentHashMap<String, ArrayList<String>> concurrentHashMap = PERMISSIONS.get(basePermission);
        if (concurrentHashMap == null) {
            return false;
        }
        if (concurrentHashMap.containsKey("*") && checkWildCards){
            return true;
        }
        ArrayList<String> strings = concurrentHashMap.get(subPermission);
        if (strings == null){
            return false;
        }
        if (checkWildCards){
            return strings.contains("*") || strings.contains(fullPermission);
        } else {
            return false;
        }
    }

    public boolean hasPermission(String permission, boolean checkWildCards) {
        if (op && checkWildCards){
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
     *          starnub.server.startUDPServer
     *          starnub.server.*
     *          starnub.*
     * <br>
     * The first line would give me shutdown. The second line would be startUDPServer. The third line give me anything under
     * starnub.server, The fourth line gives me all PERMISSIONS for the starnub related commands that fall under starnub. .
     *
     * <p>
     * @param basePermission String representing the pluggableOLD command name to check
     * @param subPermission String representing the plugins command specific sub permission
     * @param fullPermission String representing the plugins sub permission specific Command
     * @return boolean if the account has the permission
     */
    public boolean hasPermission(String basePermission, String subPermission, String fullPermission, boolean checkWildCards) {
        return op && checkWildCards || hasFullPermission(basePermission, subPermission, fullPermission, checkWildCards);
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
     *          starnub.server.startUDPServer
     *          starnub.server.*
     *          starnub.*
     * <br>
     * The first line would give me shutdown. The second line would be startUDPServer. The third line give me anything under
     * starnub.server, The fourth line gives me all PERMISSIONS for the starnub related commands that fall under starnub. .
     *
     * <p>
     * @param basePermission String representing the pluggableOLD command name to check
     * @param subPermission String representing the plugins command specific sub permission
     * @return boolean if the account has the permission
     */
    public boolean hasPermission(String basePermission, String subPermission, boolean checkWildCards) {
        return op && checkWildCards || hasSubPermission(basePermission, subPermission, checkWildCards);
    }

    public String getSpecificPermission(String basePermission, String subPermission) {
        ConcurrentHashMap<String, ArrayList<String>> concurrentHashMap = PERMISSIONS.get(basePermission);
        if (concurrentHashMap == null){
            return null;
        }
        ArrayList<String> arrayList = concurrentHashMap.get(subPermission);
        if (arrayList == null || arrayList.size() == 0){
            return null;
        }
        return arrayList.get(0);
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
        if (op){
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

    public static PlayerSession getSession(Object playerIdentifier){
        return StarNub.getConnections().getCONNECTED_PLAYERS().getOnlinePlayerByAnyIdentifier(playerIdentifier);
    }

    public static List<PlayerSession> getRecentSessions(DateTime dateTime){
        return PLAYER_SESSION_LOG_DB.getAllFromDateRangeToNow(END_TIME_COLUMN, dateTime);
    }

    public static HashSet<PlayerSession> getRecentSessionsByIdentifier(String searchId, DateTime dateTime){
        List<PlayerSession> allFromDateRangeToNow = PLAYER_SESSION_LOG_DB.getAllFromDateRangeToNow(END_TIME_COLUMN, dateTime);
        HashSet<PlayerSession> newList = new HashSet<>();
        if (Players.isStarNubId(searchId)) {
            int starnubId = Integer.parseInt(searchId.replaceAll("[sS]", ""));
            for (PlayerSession playerSession : allFromDateRangeToNow){
                Account account = playerSession.getPlayerCharacter().getAccount();
                if (account != null && starnubId == account.getStarnubId()){
                    newList.add(playerSession);
                }
            }
        } else if (StringUtils.countMatches(searchId, "-") >= 4) {
            UUID uuid = UUID.fromString(searchId);
            allFromDateRangeToNow.forEach(playerSession -> {
                UUID characterUuid = playerSession.getPlayerCharacter().getUuid();
                if (uuid.equals(characterUuid)) {
                    newList.add(playerSession);
                }
            });
        } else if (StringUtils.countMatches(searchId, ".") == 3) {
            allFromDateRangeToNow.forEach(playerSession -> {
                String ipString = playerSession.getSessionIpString();
                if (ipString.equals(searchId)) {
                    newList.add(playerSession);
                }
            });
        } else {
            for (PlayerSession playerSession : allFromDateRangeToNow){
                PlayerCharacter character = playerSession.getPlayerCharacter();
                String searchName = searchId.toLowerCase();
                String cleanName = character.getCleanName().toLowerCase();
                String characterName = character.getName().toLowerCase();
                if (cleanName.contains(searchName) || characterName.contains(searchName)) {
                    newList.add(playerSession);
                }
            }
        }
        return newList;
    }

    public static PlayerSession getSpecificSession(int playerSessionId){
        return PLAYER_SESSION_LOG_DB.getById(playerSessionId);
    }

    /* DB Methods */

    public static void retreiveSession(PlayerCharacter playerCharacter){

    }

    @Override
    public String toString() {
        return "PlayerSession{" +
                "sessionID=" + sessionID +
                ", sessionIpString='" + sessionIpString + '\'' +
                ", startTimeUtc=" + startTimeUtc +
                ", endTimeUtc=" + endTimeUtc +
                ", playerCharacter=" + playerCharacter +
                ", starboundClientId=" + starboundClientId +
                ", gameName='" + gameName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", cleanNickName='" + cleanNickName + '\'' +
                ", op=" + op +
                ", afk=" + afk +
                ", CONNECTION_TYPE=" + CONNECTION_TYPE +
                ", CONNECTION=" + CONNECTION +
                '}';
    }
}