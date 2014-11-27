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

package starnub.connections.player.session;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import starnub.StarNub;
import utilities.connectivity.connection.ProxyConnection;
import starnub.connections.player.character.Character;
import starbounddata.packets.chat.ChatReceivePacket;
import starbounddata.packets.chat.ChatSendPacket;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Observable;

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
public class Player extends ProxyConnection {

    /**
     * Represents this sessions unique sessionId that was generated by the database tools
     */
    @DatabaseField(generatedId = true, columnName = "SESSION_ID")
    private volatile int sessionID;

    /**
     * Represents this Players IP ina string mainly used for the database
     */
    @DatabaseField(dataType = DataType.STRING, columnName = "IP")
    private volatile String sessionIpString;

    /**
     * Represents the start time in UTC from when the Player starbounddata.packets.connection was completely excepted
     */
    @DatabaseField(dataType = DataType.DATE_TIME, columnName = "START_TIME")
    private volatile DateTime startTimeUtc;

    /**
     * Represents the start time in UTC from when the Player starbounddata.packets.connection was completely excepted
     */
    @DatabaseField(dataType = DataType.DATE_TIME, columnName = "END_TIME")
    private volatile DateTime endTimeUtc;

    /**
     * Represents the Players StarNub ID if they have registered the Character
     */
    @DatabaseField(dataType = DataType.INTEGER, canBeNull = true, columnName = "STARNUB_ID")
    private volatile int account;

    /**
     * Represents the Character for this Player
     */
    @DatabaseField(foreign = true, canBeNull = false, columnName = "CHARACTER_ID")
    private volatile Character character;

    /**
     * Represents the Character for this Player
     */
    private volatile Restrictions restrictions;

    /**
     * Represents the Starbound Client ID assigned by the Starbound starbounddata.packets.starbounddata.packets.starnub
     */
    private volatile long starboundClientId;

    /**
     * Represents the characters full name with color tags and all
     */
    private volatile String gameName;

    /**
     * Represents the Nick name of the Player, with all of the color strings and other stuff in it
     */
    private volatile String nickName;

    /**
     * Represents the Clean version of the Nick name where we stripped everything out that inter fears with starbounddata.packets.starbounddata.packets.starnub logs and administration
     */
    private volatile String cleanNickName;


    /**
     * This indicates weather this account is an operator, which gives them complete control
     * over the starbounddata.packets.starbounddata.packets.starnub.
     * <p>
     * NOTE: Use with caution. This is really not the right way to give a person admin or mod access. You
     * should use a group with the correct permissions.
     */
    private volatile boolean isOp;

    /**
     * Represents if the Player is Away From Keyboard (AFK), this is manually set by a Player or Plugin
     */
    private volatile boolean afk;

    /**
     * Represents the ChannelHandlerContext of who not to send a message to
     */

    private volatile ArrayList<Channel> doNotSendMessageList;

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

    public int getAccount() {
        return account;
    }

    public Character getCharacter() {
        return character;
    }

    public Restrictions getRestrictions() {//INTEGRATE HERE and NOT in CONNECTIONS
        return restrictions;
    }

    public long getStarboundClientId() {
        return starboundClientId;
    }

    public String getGameName() {
        return gameName;
    }

    public String getNickName() {
        return nickName;
    }

    public String getCleanNickName() {
        return cleanNickName;
    }

    public boolean isOp() {
        return isOp;
    }

    public boolean isAfk() {
        return afk;
    }

    public ArrayList<Channel> getDoNotSendMessageList() {
        return doNotSendMessageList;
    }

    /**
     * This constructor is used to set the player session data up, otherwise the data cannot be set
     *
     * @param character PlayerCharacter the character that is being used in this player session
     * @param restrictions PlayerRestrictions if any that mark this player with (Banned, Muted, Command Blocked...See the class for details)
     * @param account Integer representing a StarNub account id
     */
    public Player(ProxyConnection proxyConnection, Character character, Restrictions restrictions, int account) {
        super(StarNub.getStarNubEventRouter(), proxyConnection.getCLIENT_CTX(), proxyConnection.getSERVER_CTX());
        this.startTimeUtc = DateTime.now();
        this.sessionIpString = StringUtils.remove(proxyConnection.getClientIP().toString(), "/");
        this.character = character;
        this.restrictions = get; //Use restriction cache from connection class

        this.account = account;
        this.gameName = character.getName(); //Internal self cleaning
        this.nickName = character.getName();
        this.cleanNickName = character.getCleanName(); //Internal self cleaning
        this.isOp = isOp; /// Set internally
    }

    @Override
    public void addConnection() {

    }

    @Override
    public void removeConnection() {

    }


    /**
     *
     * @param endTimeUtc long allows the setting of this session when it ends
     */
    public void setEndTimeUtc(DateTime endTimeUtc) {
        this.endTimeUtc = endTimeUtc;
        StarNub.getDatabaseTables().getPlayerSessionLog().update(this);
    }

    /**
     *
     * @param account Account which this session belongs too
     */
    public void setAccount(int account) {
        this.account = account;
        StarNub.getDatabaseTables().getPlayerSessionLog().update(this);
    }

    /**
     *
     * @param restrictions sets the Restrictions for this session with (Banned, Muted, Command Blocked...See the class for details)
     */
    public void setRestrictions(Restrictions restrictions) {
        this.restrictions = restrictions;
    }

    /**
     *
     * WARNING: Do not use this method, not for public consumption.
     *
     * @param starboundClientId int that set the Starbound Client ID
     */
    public void setStarboundClientId(long starboundClientId) {
        this.starboundClientId = starboundClientId;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    /**
     *
     * @param nickName String set the nick name that includes colors and other characters
     */
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    /**
     *
     * @param cleanNickName String to set the clean version of the nick name, colors and special characters removed
     */
    public void setCleanNickName(String cleanNickName) {
        this.cleanNickName = cleanNickName;
    }

    public void setOp(boolean isOp) {
        this.isOp = isOp;
    }

    /**
     *
     * @param afk boolean to set if this session/character is ask or not
     */
    public void setAfk(boolean afk) {
        this.afk = afk;
    }

    public void setDoNotSendMessageList() {
        this.doNotSendMessageList = new ArrayList<Channel>();
    }

    public void reloadIgnoreList(){

    }

    public void addToIgnoreList(){

    }

    public void removeFromIgnoreList(){

    }

    @Override
    public void sendChatMessage(Object sender, ChatReceivePacket.ChatReceiveChannel channel, String message){
        if (sender instanceof Player) {
            new ChatReceivePacket(clientCtx, channel, "", 0, msgUnknownNameBuilder(sender, tags, false), message).routeToDestination();
        }
    }
    
    public void sendServerChatMessage(ChatSendPacket.ChatSendChannel channel, String message){
        new ChatSendPacket(serverCtx, channel, message).routeToDestination();
    }


}



