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

package server.server.chat;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import lombok.Getter;
import org.joda.time.DateTime;
import server.StarNub;
import server.connectedentities.player.account.Account;
import server.connectedentities.player.session.Player;
import server.eventsrouter.events.PlayerEvent;
import starbounddata.chat.ChatReceiveChannel;
import starbounddata.color.GameColors;
import server.server.packets.chat.ChatReceivePacket;

import java.util.ArrayList;

@DatabaseTable(tableName = "CHAT_ROOMS")
public class ChatRoom {

    @Getter
    @DatabaseField(id = true, dataType = DataType.STRING, unique = true, columnName = "CHAT_ROOM_NAME")
    private volatile String chatRoomName;

    @Getter
    @DatabaseField(dataType = DataType.STRING, columnName = "ALIAS")
    private volatile String alias;

    @Getter
    @DatabaseField(foreign = true, columnName = "ROOM_CREATOR_STARNUB_ID")
    private Account roomCreatorStarnubId;

    @Getter
    @DatabaseField(dataType = DataType.DATE_TIME, columnName = "LAST_USED")
    private volatile DateTime lastUsed;

    @Getter
    @DatabaseField(dataType = DataType.STRING, columnName = "NAME_COLOR")
    private volatile String nameColor;

    @Getter
    @DatabaseField(dataType = DataType.STRING, columnName = "CHAT_COLOR")
    private volatile String chatColor;

    @Getter
    @DatabaseField(dataType = DataType.STRING, columnName = "PASSWORD")
    private volatile String password;

    @Getter
    @DatabaseField(dataType = DataType.STRING, columnName = "INFO")
    private volatile String info;

    @Getter
    private ChannelGroup channelGroup;

    public ChatRoom(){}

    public ChatRoom(String chatRoomName, String alias, Account roomCreatorStarnubId, ChannelGroup channelGroup) {
        this.chatRoomName = chatRoomName;
        this.alias = alias;
        this.roomCreatorStarnubId = roomCreatorStarnubId;
        this.channelGroup = channelGroup;
        //TODO BAD METHOD
    }

    public ChatRoom(String chatRoomName, String alias, Account roomCreatorStarnubId, ChannelGroup channelGroup, String NameColor, String ChatColor) {
        this.chatRoomName = chatRoomName;
        this.alias = alias;
        this.roomCreatorStarnubId = roomCreatorStarnubId;
        this.channelGroup = channelGroup;
        this.nameColor = NameColor;
        this.chatColor = ChatColor;
    }

    public ChatRoom(String chatRoomName, String alias, Account roomCreatorStarnubId, ChannelGroup channelGroup, String NameColor, String ChatColor, String Password) {
        this.chatRoomName = chatRoomName;
        this.alias = alias;
        this.roomCreatorStarnubId = roomCreatorStarnubId;
        this.channelGroup = channelGroup;
        this.nameColor = NameColor;
        this.chatColor = ChatColor;
        this.password = Password;
    }

    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public void setRoomCreatorStarnubId(Account roomCreatorStarnubId) {
        this.roomCreatorStarnubId = roomCreatorStarnubId;
    }

    public void setLastUsed(DateTime lastUsed) {
        this.lastUsed = lastUsed;
    }

    public void setNameColor(String nameColor) {
        this.nameColor = nameColor;
    }

    public void setChatColor(String chatColor) {
        this.chatColor = chatColor;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setChannelGroup(ChannelGroup channelGroup) {
        this.channelGroup = channelGroup;
    }

    public void chatSend(Object senderObject, String message){
        GameColors gc = StarNub.getMessageSender().getGameColors();
        int senderID = 0;
        String senderName = null;
        ArrayList<Channel> doNotSendList = null;
        if (senderObject instanceof Player){
            Player player = (Player) senderObject;
            senderID = (int) player.getStarboundClientId();
            senderName = StarNub.getMessageSender().msgUnknownNameBuilder(player, true, false);
            doNotSendList = player.getDoNotSendMessageList();
            if (password == null) {
                PlayerEvent.eventSend_Player_Chat_Success_Public_(player, this.chatRoomName, message);
            } else {
                PlayerEvent.eventSend_Player_Chat_Success_Private_(player, this.chatRoomName, message);
            }
        }

        String cChatMessageString = this.chatRoomName;

        String gameColor = gc.getDefaultNameColor();
        String ob = gc.getDefaultNameColor() + "(";
        String cb = gc.getDefaultNameColor() + ")";
        String chatRoomNameString =  senderName + gameColor + " -> " + ob + nameColor + alias + cb;
        if (password != null) {
            cChatMessageString = chatRoomNameString + "(Private)";
            chatRoomNameString = chatRoomNameString + ob + nameColor + "P" + cb;
        }
        ChatReceivePacket chatReceivePacket = new ChatReceivePacket(
                ChatReceiveChannel.UNIVERSE,
                "",
                senderID,
                chatRoomNameString,
                this.chatColor +message);
        StarNub.getPacketSender().channelGroupPacketSenderIgnoreList(this.channelGroup, doNotSendList, chatReceivePacket);
        StarNub.getLogger().cChatPrint(senderObject, message, cChatMessageString);
    }

}
