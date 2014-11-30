///*
//* Copyright (C) 2014 www.StarNub.org - Underbalanced
//*
//* This utilities.file is part of org.starnub a Java Wrapper for Starbound.
//*
//* This above mentioned StarNub software is free software:
//* you can redistribute it and/or modify it under the terms
//* of the GNU General Public License as published by the Free
//* Software Foundation, either version  3 of the License, or
//* any later version. This above mentioned CodeHome software
//* is distributed in the hope that it will be useful, but
//* WITHOUT ANY WARRANTY; without even the implied warranty of
//* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
//* the GNU General Public License for more details. You should
//* have received a copy of the GNU General Public License in
//* this StarNub Software.  If not, see <http://www.gnu.org/licenses/>.
//*/
//
//package starnub.connections.player.account;
//
//import com.j256.ormlite.field.DatabaseField;
//import com.j256.ormlite.table.DatabaseTable;
//import chatmanager.chat.ChatRoom;
//
//@DatabaseTable(tableName = "CHAT_ROOM_SUBSCRIPTIONS")
//public class ChatRoomSubscription {
//
//    /**
//     * Represent a chatRoomSubscriptionId
//     */
//    @DatabaseField(generatedId = true, columnName = "CHAT_ROOM_SUBSCRIPTION_ID")
//    private int chatRoomSubscriptionId;
//
//    /**
//     * Represents the starnubId that has ignored players
//     */
//
//    @DatabaseField(foreign = true, columnName = "ACCOUNT_SETTING")
//    private Settings accountSetting;
//
//    /**
//     * Represents a starbounddata.packets.chat room id
//     */
//
//    @DatabaseField(foreign = true, columnName = "CHAT_ROOM")
//    private ChatRoom chatRoom;
//
//    /**
//     * Constructor for database purposes
//     */
//    public ChatRoomSubscription(){}
//
//    public int getChatRoomSubscriptionId() {
//        return chatRoomSubscriptionId;
//    }
//
//    public Settings getAccountSetting() {
//        return accountSetting;
//    }
//
//    public ChatRoom getChatRoom() {
//        return chatRoom;
//    }
//
//    /**
//     * Constructor used in adding, removing or updating a starbounddata.packets.chat room subscription record
//     * @param accountSetting int representing the account id
//     * @param chatRoom int representing the starbounddata.packets.chat room
//     */
//    public ChatRoomSubscription(Settings accountSetting, ChatRoom chatRoom) {
//        this.accountSetting = accountSetting;
//        this.chatRoom = chatRoom;
//    }
//}
