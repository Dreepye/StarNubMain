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

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.Getter;
import org.joda.time.DateTime;
import server.StarNub;
import server.connectedentities.player.session.Player;
import server.eventsrouter.handlers.PacketEventHandler;
import server.eventsrouter.events.PlayerEvent;
import server.logger.MultiOutputLogger;
import server.senders.MessageSender;
import server.server.Connections;
import server.server.Server;
import starbounddata.chat.ChatReceiveChannel;
import starbounddata.chat.ChatSendChannel;
import server.server.packets.Packet;
import server.server.packets.chat.ChatReceivePacket;
import server.server.packets.chat.ChatSendPacket;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum ServerChat {
    INSTANCE;

    @Getter
    private ChatFilter chatFilter;

    @Getter
    private static ConcurrentHashMap<String, ChatRoom> chatRooms;

    /**
     * Gets the set link to {@link ChatFilter} Enum singleton.
     */
    public ChatFilter getChatFilter() {
        return chatFilter;
    }

    /**
     * References the chatFilter variable to the {@link ChatFilter} Enum singleton.
     */
    private synchronized void setChatFilter() {
        chatFilter = ChatFilter.INSTANCE;
        chatFilter.setChatFilterData();
    }

    /**
     * @return globalClientChannels instance being ran
     */
    public ConcurrentHashMap<String, ChatRoom> getClientChannels() {
        return chatRooms;
    }

    /**
     * This cannot be done if the globalClientChannels is already set.
     */
    public void setChatChannels() {
        if (chatRooms != null) {
            throw new UnsupportedOperationException("Cannot redefine globalClientChannels");
        }
        chatRooms = new ConcurrentHashMap<String, ChatRoom>();
        ChatRoom universeChat = StarNub.getDatabaseTables().getChatRooms().getChatRoomByName("Universe");
        if (universeChat == null) {
            universeChat = new ChatRoom(
                    "Universe",
                    "Uni",
                    null,
                    new DefaultChannelGroup(GlobalEventExecutor.INSTANCE),
                    StarNub.getMessageSender().getGameColors().getDefaultNameColor(),
                    StarNub.getMessageSender().getGameColors().getDefaultChatColor());
                    StarNub.getDatabaseTables().getChatRooms().create(universeChat);
        } else {
            universeChat.setChannelGroup(new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
        }
        chatRooms.put("universe", universeChat);
        setChatFilter();
        chatMessageInterception();
    }


    public boolean joinChatRoom(String channel, Player player, String password, boolean isSaved){
        //Add methods to join or create room
        return chatRooms.get(channel.toLowerCase()).getChannelGroup().add(player.getClientCtx().channel());
    }


    public boolean leaveChatRoom(String channel, Object player){
        return chatRooms.get(channel.toLowerCase()).getChannelGroup().remove(StarNub.getServer().getConnections().getOnlinePlayerByAnyIdentifier(player).getClientCtx());
    }


    public boolean deleteChatRoom(String channel) {
        if (chatRooms.get(channel.toLowerCase()).getChannelGroup().isEmpty()) {
            chatRooms.remove(channel.toLowerCase());
        }
        return !chatRooms.containsKey(channel.toLowerCase());
    }

    public void removeEmptyChannels(){
        for (String chatChannelName : chatRooms.keySet()) {
            ChatRoom chatRoom = chatRooms.get(chatChannelName);
            if (chatRoom.getChannelGroup().isEmpty() && !chatRoom.getChatRoomName().equalsIgnoreCase("universe")) {
                chatRooms.remove(chatChannelName);
            }
        }
    }

    public ChatRoom getChatRoomByAnyIdentifier(Object chatroomIdentifier){
        if (chatroomIdentifier instanceof String) {
            return getChatRoomByName((String) chatroomIdentifier);
        //} else if (chatroomIdentifier instanceof Integer) {
           // return getChatRoomById((Integer) chatroomIdentifier);
        } else {
            return null;
        }
    }

    public ChatRoom getChatRoomByName(String channel) {
        for (String chatroom : chatRooms.keySet()) {
            if (chatroom.equalsIgnoreCase(channel)) {
                return chatRooms.get(chatroom);
            }
        }
        return null;
    }

    public ChatRoom getChatRoomById(String chatRoomId) {
        for (ChatRoom chatRoom : chatRooms.values()) {
            if (chatRoom.getChatRoomName().equalsIgnoreCase(chatRoomId)) {
                return chatRoom;
            }
        }
        return null;
    }

    public String getChatRoomPlayersListSingleRoom(Object sender, String channelName) {
        for (String roomString : chatRooms.keySet()) {
            if (roomString.equalsIgnoreCase(channelName)) {
                ChatRoom chatRoom = chatRooms.get(roomString);
                if (!chatRoom.getChannelGroup().isEmpty()) {
                    return "Chat Room: " + chatRoomPlayersListBuilder(sender, chatRooms.get(roomString));
                }
            }
        }
        return null;
    }


    public String getChatRoomPlayersListAllRooms(Object sender) {
        String chatChannelsPrint = "Chat Rooms: ";
        for (ChatRoom chatRoom : chatRooms.values()) {
            if (!chatRoom.getChannelGroup().isEmpty()) {
                chatChannelsPrint = chatChannelsPrint + chatRoomPlayersListBuilder(sender, chatRoom);
            }
        }
        return chatChannelsPrint;
    }

    private String chatRoomPlayersListBuilder(Object sender, ChatRoom chatRoom){
        int chatRoomPlayerCount = chatRoom.getChannelGroup().size();
        String chatRoomPart1 = chatRoom.getChatRoomName() //+ " (ID: " + chatRoom.getChatRoomId() + ")"
                + ". Player Count (";
        String chatRoomString = "). Players: ";
        ChannelGroup channelGroup = chatRoom.getChannelGroup();
        for (Channel channel : channelGroup) {
            Player playerSession = StarNub.getServer().getConnections().getOnlinePlayerByAnyIdentifier((channel));
            boolean appearOffline = false;
            if (playerSession.getCharacter().getAccount() != null) {
                appearOffline = playerSession.getCharacter().getAccount().getAccountSettings().isAppearOffline();
            }
            ArrayList<Boolean> appearance = StarNub.getServer().getConnections().canSeePlayerIsHiddenCanSee(sender, playerSession);
            String hiddenToken = "";
            if (appearance.get(0)) {
                hiddenToken = " (H)";
            }
            if (appearance.get(1)) {

                chatRoomString = chatRoomString + playerSession.getCleanNickName() + hiddenToken + ", ";

            } else {
                chatRoomPlayerCount--;
            }
        }
        try {
            chatRoomString = chatRoomPart1+Integer.toString(chatRoomPlayerCount)+chatRoomString;
            chatRoomString = chatRoomString.substring(0, chatRoomString.lastIndexOf(",")) + ". ";
        } catch (StringIndexOutOfBoundsException e) {
                /* Do nothing no players are in channel */
        }
        return chatRoomString;
    }

    /**
     * This method will intercept starbounddata.packets.chat messages and insure they are routed correctly.
     * <p>
     * The wrapper handles whispers to ensure that they make it to the person regardless if they have funky
     * characters.
     */
    protected void chatMessageInterception() {
        StarNub.getPacketEventRouter().registerEventSubscription("StarNub", ChatSendPacket.class, new PacketEventHandler() {
            @Override
            public Packet onEvent(Packet packet) {
                return playerChatProcessor(packet);
            }
        });
        StarNub.getPacketEventRouter().registerEventSubscription("StarNub", ChatReceivePacket.class, new PacketEventHandler() {
            @Override
            public Packet onEvent(Packet packet) {
                return serverChatProcessor(packet);
            }
        });

//        StarNub.getPacketEventRouter().getPacketReactor().on(T(ChatSendPacket.class), this::playerChatProcessor);
//
//
//        StarNub.getPacketEventRouter().getPacketReactor().on(T(ChatReceivePacket.class), this::serverChatProcessor);
    }

    private Packet playerChatProcessor(Packet packet){
        ChatSendPacket chatPacket = (ChatSendPacket) packet;

        /* This method will put the packet back into the packet pool and then recycle the event */
        packet.setRecycle(true);

        Player messageSender = StarNub.getServer().getConnections().getOnlinePlayerByAnyIdentifier(chatPacket.getSenderCTX());
        String chatMessage = chatPacket.getMessage();
        long nowTime = DateTime.now().getMillis();

        if (!chatMessage.startsWith("/")) {
            chatMessage(nowTime, messageSender, chatMessage, chatPacket, messageSender.getCharacter().getAccount() != null);
        } else if (chatPacket.getMessage().startsWith("/")) {
            commandUse(nowTime, messageSender, chatMessage);
        }
        return chatPacket;
    }

    private ChatSendPacket chatMessage(long nowTime, Player messageSender, String chatMessage, ChatSendPacket chatPacket, boolean hasAccount) {
        MultiOutputLogger cLogSen = StarNub.getLogger();
        MessageSender cMsgSen = StarNub.getMessageSender();
        Server cServer = StarNub.getServer();
        Connections cConn = cServer.getConnections();
        ChatFilter cChatF = cServer.getServerChat().getChatFilter();
        Map confSC = (Map) StarNub.getConfiguration().getConfiguration().get("starbounddata.packets.starbounddata.packets.server starbounddata.packets.chat");
        String spamReply = (String) confSC.get("global_spam_reply");

        boolean isMuted = false;
        if (messageSender.getRestrictions() != null) {
            isMuted = messageSender.getRestrictions().isMuted();
        }

        /* Can starbounddata.packets.chat */
        if (!cConn.hasPermission(messageSender, "starnubinternals.starbounddata.packets.starbounddata.packets.server.starbounddata.packets.chat", true)) {
            cMsgSen.playerMessage("StarNub", messageSender, "You do not have the permission \"starnubinternals.starbounddata.packets.starbounddata.packets.server.starbounddata.packets.chat\" you can not use starbounddata.packets.chat.");
            return chatPacket;
        }

        /* Spam watch - Chat speed */
        if (!cConn.hasPermission(messageSender, "starnubinternals.bypass.chatrate", true)) {
            int personalRateLimit = cConn.getPermissionVariableInteger(messageSender, "starnub.chatrate");
            if (personalRateLimit < -100000) {
                personalRateLimit = personalChatRateSet();
            } else if (personalRateLimit == -100000) {
                personalRateLimit = 0;
            }
            if (messageSender.getLastMessageTime() >= nowTime) {
                cMsgSen.playerMessage("StarNub", messageSender, spamReply + " You can send a message in " + Double.toString((double) ((messageSender.getLastMessageTime() - nowTime) / 1000)) + " second(s).");
                PlayerEvent.eventSend_Player_Chat_Failed_Spam(messageSender, chatMessage);
                return chatPacket;
            } else {
                messageSender.setLastMessageTime(nowTime + personalRateLimit);
            }
        }

        /* Spam watch - Muted check */
        if (isMuted && !cConn.hasPermission(messageSender, "starnubinternals.bypass.mute", true)) {
            PlayerEvent.eventSend_Player_Chat_Failed_Muted(messageSender, chatMessage);
            cMsgSen.playerMessage("StarNub", messageSender, "You are currently muted. Please visit " + ((String) ((Map) StarNub.getConfiguration().getConfiguration().get("starbounddata.packets.starbounddata.packets.server info")).get("server_url")) + " for further details.");
            return chatPacket;
        }

        /* Spam watch - Repeat messages */
        if ((boolean) confSC.get("global_message_block_repeat_messages") && !cConn.hasPermission(messageSender, "starnubinternals.starbounddata.packets.chat.repeatmessage", true)) {
            double percentThreshold = (double) confSC.get("global_message_similarity");
            double percentageAlike = 0;
            try{
                percentageAlike = cChatF.percentageAlikeCalculation(messageSender.getLastMessage(), chatMessage);
            } catch (ArithmeticException e) {
                StarNub.getLogger().cFatPrint("StarNub", "Arithmetic Exception: Percentage calculator. Variables 1: "+messageSender.getLastMessage()+", Variable 2: "+chatMessage+".");
            }
            if (percentageAlike != 0) {
                if (percentageAlike > percentThreshold) {
                    cMsgSen.playerMessage("StarNub", messageSender, spamReply);
                    PlayerEvent.eventSend_Player_Chat_Failed_Spam(messageSender, chatMessage);
                    return chatPacket;
                }
            }
        }
        messageSender.setLastMessage(chatMessage);

        /* Spam watch - Remove repeated letters based on configuration count */
        if ((boolean) confSC.get("global_message_remove_continuous_letters") && !cConn.hasPermission(messageSender, "starnubinternals.starbounddata.packets.chat.continuousletters", true)) {
            chatMessage = cChatF.removeMultipleCharacter(chatMessage);
        }

        /* Spam watch - Remove starbounddata.packets.chat colors */
        if (!cConn.hasPermission(messageSender, "starnubinternals.starbounddata.packets.chat.color", true)) {
            chatMessage = cChatF.removeColors(chatMessage);
        }

        /* Spam watch - Remove all upper case */
        if ((boolean) confSC.get("global_caps_limiter") && !cConn.hasPermission(messageSender, "starnubinternals.starbounddata.packets.chat.nocapslimit", true)) {
            if (cChatF.stringCapitalizationCheck(chatMessage)
                    >= (double) confSC.get("global_caps_percentage")) {
                chatMessage = chatMessage.toLowerCase();
            }
        }

        /* Word Filter */
        if ((boolean) confSC.get("global_word_filter") && !cConn.hasPermission(messageSender, "starnubinternals.bypass.wordfilter", true)) {
            chatMessage = cChatF.chatFilter(messageSender, chatMessage);
        }

        /* Finally message sender */
        if (chatPacket.getChannel().equals(ChatSendChannel.UNIVERSE)) {
            if (hasAccount) {
                messageSender.getCharacter().getAccount().getAccountSettings().getDefaultChatRoom().chatSend(messageSender, chatMessage);
            } else {
                StarNub.getServer().getServerChat().getChatRoomByName("Universe").chatSend(messageSender, chatMessage);
            }
        } else if (chatPacket.getChannel().equals(ChatSendChannel.PLANET)) {
            PlayerEvent.eventSend_Player_Chat_Success_Local(messageSender, chatMessage);
            cLogSen.cChatPrint(messageSender, chatMessage, "Local");
            StarNub.getPacketSender().serverPacketSender(messageSender, chatPacket);
        }
        return chatPacket;
    }

    private void commandUse(long nowTime, Player messageSender, String chatMessage) {
        MessageSender cMsgSen = StarNub.getMessageSender();
        Server cServer = StarNub.getServer();
        Connections cConn = cServer.getConnections();
        Map confSC = (Map) StarNub.getConfiguration().getConfiguration().get("starbounddata.packets.starbounddata.packets.server starbounddata.packets.chat");
        String spamReply = (String) confSC.get("global_spam_reply");

        boolean isCommandBlocked = false;
        if (messageSender.getRestrictions() != null) {
            isCommandBlocked = messageSender.getRestrictions().isCommandBlocked();
        }

        /* Can use commands */
        if (!cConn.hasPermission(messageSender, "starnubinternals.starbounddata.packets.starbounddata.packets.server.commands", true)) {
            StarNub.getMessageSender().playerMessage("StarNub", messageSender, "You do not have the permission \"starnubinternals.starbounddata.packets.starbounddata.packets.server.commands\" you can not use commands.");
            return;
        }

        /* Spam watch - Command speed */
        if (!cConn.hasPermission(messageSender, "starnubinternals.bypass.commandrate", true)) {
            int personalRateLimit = cConn.getPermissionVariableInteger(messageSender, "starnubinternals.commandrate");
            if (personalRateLimit < -100000) {
                personalRateLimit = personalCommandRateSet();
            } else if (personalRateLimit == -100000) {
                personalRateLimit = 0;
            }
            if (messageSender.getLastCommandTime() >= nowTime) {
                StarNub.getMessageSender().playerMessage("StarNub", messageSender, spamReply + " You can send a message in " + Double.toString((double) ((messageSender.getLastCommandTime() - nowTime) / 1000)) + ".");
                PlayerEvent.eventSend_Player_Command_Failed_Spam(messageSender, chatMessage);
                return;
            } else {
                messageSender.setLastCommandTime(nowTime + personalRateLimit);
            }
        }

        /* Spam watch - Command blocked check */
        if (isCommandBlocked && !cConn.hasPermission(messageSender, "starnubinternals.bypass.commandblock", true)) {
            PlayerEvent.eventSend_Player_Command_Failed_Command_Blocked(messageSender, chatMessage);
            cMsgSen.playerMessage("StarNub", messageSender, "You are currently unable to used commands. Please visit " + ((String) ((Map) StarNub.getConfiguration().getConfiguration().get("starbounddata.packets.starbounddata.packets.server info")).get("server_url")) + " for further details.");
            return;
        }

        PlayerEvent.eventSend_Player_Command_Used(messageSender, chatMessage);
        StarNub.getCommandParser().commandSend(messageSender, chatMessage);
    }

    private int personalChatRateSet(){
        return ((int) ((Map) StarNub.getConfiguration().getConfiguration().get("starbounddata.packets.starbounddata.packets.server starbounddata.packets.chat")).get("global_message_speed"));
    }

    private int personalCommandRateSet(){
        return ((int) ((Map) StarNub.getConfiguration().getConfiguration().get("starbounddata.packets.starbounddata.packets.server starbounddata.packets.chat")).get("global_command_speed"));
    }

    private Packet serverChatProcessor(Packet packet){
        ChatReceivePacket chatPacket = (ChatReceivePacket) packet;

        Player messageRecipient = StarNub.getServer().getConnections().getOnlinePlayerByAnyIdentifier(chatPacket.getDestinationCTX());
        String chatMessage = chatPacket.getMessage();
        ChatReceiveChannel chatReceiveChannel = chatPacket.getChannel();

        /* Replace starbounddata.packets.starbounddata.packets.server with Starbound */
        if (chatPacket.getName().equalsIgnoreCase("server")) {
            chatPacket.setName("Starbound");
        }

        /* This will take pvp messages and no longer print them globally, but instead only on the planet or ship
        * We will also not be using nick names */
        if (chatMessage.contains("is now PVP") || chatMessage.contains("is no longer PVP")) {
            if (chatReceiveChannel.equals(ChatReceiveChannel.UNIVERSE)) {
//                StarNub.getServer().getKnownPackets().releasePacket(chatPacket);
                chatPacket.setRecycle(true);
            }
        } else if (chatMessage.contains("has entered PVP Mode") || chatMessage.contains("has left PVP Mode")) {
            chatPacket.setName("StarNub");
        } else if (chatMessage.contains("Nick changed to")) {
            /* This method will put the packet back into the packet pool and then recycle the event */
//            StarNub.getServer().getKnownPackets().releasePacket(chatPacket);
            chatPacket.setRecycle(true);
        }
        return chatPacket;
    }
}
