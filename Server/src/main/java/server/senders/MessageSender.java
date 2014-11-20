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

package server.senders;

import server.StarNub;
import server.connectedentities.player.account.Settings;
import server.connectedentities.player.session.Player;
import server.plugins.PluginPackage;
import server.server.Connections;
import starbounddata.chat.ChatReceiveChannel;
import starbounddata.chat.ChatSendChannel;
import starbounddata.color.GameColors;
import server.server.packets.chat.ChatReceivePacket;
import server.server.packets.chat.ChatSendPacket;

import java.util.*;

/**
 * StarNub's Message Sender.
 * <p>
 * Notes: - Methods with a cBeforeIt like that mean that it represents a console sending method, the rest can
 * be assumed to be player methods. The deference is the color tags being build into the strings or not.
 *
 *        - "CleanName" references are in regards to the StarNub internal name which is stripped of spaces, colors, special characters
 *        to allow for easier referencing
 *
 *        - "GameName" references are in regards to names that have colors in them and are meant for in-game use.
 *
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public enum MessageSender {
    INSTANCE;

    private final static Object NO_COMMAND_DELIVERY_LOCK = new Object();
    private volatile static Set<String> noCommandDeliveryRegister;
    private volatile static GameColors gameColors;

    {
        setNoCommandDeliveryRegister();
        setGameColors();
    }

    /**
     * @return GameColors used in various areas of the game and starbounddata.packets.chat
     */
    public GameColors getGameColors() {
        return gameColors;
    }

    /**
     * This cannot be done if the GameColors is already set. This sets
     * {@link starbounddata.color.GameColors} singleton enum.
     */
    public void setGameColors() {
        if (MessageSender.gameColors != null) {
            throw new UnsupportedOperationException("Cannot redefine GameColors");
        }
        MessageSender.gameColors = new GameColors();
        gameColors.setColors();
    }

    @SuppressWarnings("unchecked")
    public void setNoCommandDeliveryRegister() {
        HashSet hashSet = new HashSet();
        noCommandDeliveryRegister = Collections.synchronizedSet(hashSet);
    }


    public void registerNoCommandDelivery(String pluginName, String command){
        PluginPackage pluginPackage = StarNub.getPluginManager().getPluginPackageNameAlias(pluginName);
        synchronized (NO_COMMAND_DELIVERY_LOCK) {
            noCommandDeliveryRegister.add(pluginPackage.getCOMMANDS_NAME().toLowerCase() + " " + command.toLowerCase());
            noCommandDeliveryRegister.add(pluginPackage.getCOMMANDS_ALIAS().toLowerCase() + " " + command.toLowerCase());
        }
    }

    public void registerNoCommandDelivery(String pluginName, String[] commands){
        PluginPackage pluginPackage = StarNub.getPluginManager().getPluginPackageNameAlias(pluginName);
        for (String command : commands) {
            synchronized (NO_COMMAND_DELIVERY_LOCK) {
                noCommandDeliveryRegister.add(pluginPackage.getCOMMANDS_NAME().toLowerCase() + " " + command.toLowerCase());
                noCommandDeliveryRegister.add(pluginPackage.getCOMMANDS_ALIAS().toLowerCase() + " " + command.toLowerCase());
            }
        }
    }


    public void unregisterNoCommandDelivery(String pluginName, String command){
        PluginPackage pluginPackage = StarNub.getPluginManager().getPluginPackageNameAlias(pluginName);
        synchronized (NO_COMMAND_DELIVERY_LOCK) {
            noCommandDeliveryRegister.remove(pluginPackage.getCOMMANDS_NAME().toLowerCase() + " " + command.toLowerCase());
            noCommandDeliveryRegister.remove(pluginPackage.getCOMMANDS_ALIAS().toLowerCase() + " " + command.toLowerCase());
        }
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method will send a message to a player if the playerIdentifier is null
     * then this will send the message to the console via "Info".
     *
     * @param sender Object which represents the message sender can be a string
     * @param playerIdentifier Object which represents the player identifier can be any type listed
     *                         at {@link server.server.Connections} under method PlayerByAnyIdentifier.
     * @param message String the message to be sent to player or console
     */
    public void playerOrConsoleMessage(Object sender, Object playerIdentifier, String message){
        if (playerIdentifier == null) {
            StarNub.getLogger().cInfoPrint(sender, message);
        } else {
            playerMessage(sender, playerIdentifier, ChatReceiveChannel.UNIVERSE, message);
        }
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method is used to send a message to a player.
     * <p>
     *
     * @param sender           Object which represents the message sender can be a string, class, player, management client
     * @param playerIdentifier Object which represents the player identifier can be a Player name, Player class,
     *                         Player IP, Player uuid, Client ID or ChannelHandlerContext (CTX) (Server or Client Side)
     * @param channel          ChatChannel the type of reply (universe, planet, whisper, command)
     * @param message          String the message to be sent to the player
     */
    public void playerMessage(Object sender, Object playerIdentifier, ChatReceiveChannel channel, String message) {
        if (channel == null) {
            channel = ChatReceiveChannel.UNIVERSE;
        }
        if (channel.equals(ChatReceiveChannel.WHISPER)) {
            if (playerMessageWhisper(sender, playerIdentifier, message)) {
                String cSenderReceiver =  cPlayerNameBuild(sender, true, true) + " -> " + cPlayerNameBuild(sender, true, true);
                StarNub.getLogger().cWhisperPrint(cSenderReceiver, message);
            }
        } else {
            playerMessageSender(sender, playerIdentifier, channel, message);
            StarNub.getLogger().cChatPrint(sender, message, playerIdentifier);
        }
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method is used to send a message to a player using universe channel.
     * <p>
     *
     * @param sender           Object which represents the message sender can be a string, class, player, management client
     * @param playerIdentifier Object which represents the player identifier can be a Player name, Player class,
     *                         Player IP, Player uuid, Client ID or ChannelHandlerContext (CTX) (Server or Client Side)
     * @param message          String the message to be sent to the player
     */
    public void playerMessage(Object sender, Object playerIdentifier, String message) {
        playerMessageSender(sender, playerIdentifier, ChatReceiveChannel.UNIVERSE, message);
        StarNub.getLogger().cChatPrint(sender, message, playerIdentifier);
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method is used to send a message to a player
     * <p>
     *
     * @param sender           Object which represents the message sender can be a string, class, player, management client
     * @param playerIdentifier Object which represents the player identifier can be a Player name, Player class,
     *                         Player IP, Player uuid, Client ID or ChannelHandlerContext (CTX) (Server or Client Side)
     * @param channel          ChatChannel the type of reply (universe, planet, whisper, command)
     * @param message          String the message to be sent to the player
     */
    private void playerMessageSender(Object sender, Object playerIdentifier, ChatReceiveChannel channel, String message) {
        Player playerSession = StarNub.getServer().getConnections().getOnlinePlayerByAnyIdentifier(playerIdentifier);
        if (playerSession == null) {
            playerNotFoundMsg(sender);
            return;
        }
        StarNub.getPacketSender().playerPacketSender(playerSession, playerPacketCraft(sender, channel, true, message));
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This is used to send a whisper to a player. Some checks will be conducted by the private method
     * canWhisperPlayer to insure that the sender can whisper the receiver.
     * <p>
     * @param sender           Object which represents the message sender can be a string, class, player, management client
     * @param playerReceiverIdentifier Object which represents the player identifier can be a Player name, Player class,
     *                         Player IP, Player uuid, Client ID or ChannelHandlerContext (CTX) (Server or Client Side)
     * @param message          String the message to be sent to the player
     * @return boolean         If the whisper was successful
     */
    public boolean playerMessageWhisper(Object sender, Object playerReceiverIdentifier, String message) {
        Player playerReceiver = StarNub.getServer().getConnections().getOnlinePlayerByAnyIdentifier(playerReceiverIdentifier);
        if (playerReceiver == null) {
            playerNotFoundMsg(sender);
            return false;
        }
        Player playerSender = StarNub.getServer().getConnections().getOnlinePlayerByAnyIdentifier(sender);
        if (!canWhisperPlayer(playerSender, playerReceiver)) {
            return false;
        }

        String msgSenderReceiver =  msgPlayerNameBuilderFinal(playerSender, true, false) + gameColors.getDefaultNameColor() + " -> " + msgPlayerNameBuilderFinal(playerReceiver, true, false);
        StarNub.getPacketSender().playerPacketSender(playerSender, new ChatReceivePacket(
                ChatReceiveChannel.WHISPER,
                "",
                0,
                msgSenderReceiver,
                message));
        StarNub.getPacketSender().playerPacketSender(playerReceiver, new ChatReceivePacket(
                ChatReceiveChannel.WHISPER,
                "",
                0,
                msgSenderReceiver,
                message));
        return true;
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method will check to see if a user can be whispered by
     * the sender. This is pretty much a permissions checker.
     * <p>
     *
     * @param playerSender Player representing the sender session
     * @param playerReceiver PLayer representing the receiver session
     * @return boolean if the player can be whispered by this player
     */
    private boolean canWhisperPlayer(Player playerSender, Player playerReceiver){
        Connections con = StarNub.getServer().getConnections();
        if (!con.hasPermission(playerSender, "starnubinternals.starbounddata.packets.starbounddata.packets.server.whisper", true)) {
            playerMessage("StarNub", playerSender, ChatReceiveChannel.UNIVERSE, "You do not have the permission \"starnub.starbounddata.packets.starbounddata.packets.server.whisper\" you cannot whisper.");
            return false;
        }
        if (playerReceiver.getCharacter().getAccount() != null) {
            if (playerReceiver.getCharacter().getAccount().getAccountSettings().isAppearOffline()
                    && !con.hasPermission(playerSender, "starnubinternals.bypass.appearoffline", true)) {
                playerNotFoundMsg(playerSender);
                return false;
            } else if (playerReceiver.getCharacter().getAccount().getAccountSettings().isWhisperBlocking()
                    && !con.hasPermission(playerSender, "starnubinternals.bypass.whisperblock", true)) {
               playerMessage("StarNub", playerSender, ChatReceiveChannel.UNIVERSE, "This character is not receiving whispers.");
                return false;
            }
        }
        if (playerSender.getDoNotSendMessageList().contains(playerReceiver.getClientChannel())
                && !con.hasPermission(playerSender, "starnubinternals.bypass.ignores", true)) {
            playerMessage("StarNub", playerSender, ChatReceiveChannel.UNIVERSE, "This character does not wish to received messages from you.");
            return false;
        }
        return true;
    }

    private void playerNotFoundMsg(Object sender){
        if (sender instanceof Player) {
            playerMessage("StarNub", sender, "This player is not online or StarNub could not find them.");
        } else {
            StarNub.getLogger().cInfoPrint("StarNub",  "This player is not online or StarNub could not find them.");
        }
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method will notify the command commandSender of the results.
     *
     * @param commandSender Object representing the commandSender, can be a player or some other connected object
     * @param command String command name that should be checked if delivery reply is turned on or not, null value will deliver
     *                results
     * @param message String message to be sent to the Object that sent th command
     * @param success boolean was the command successful
     */
    public void commandResults(Object commandSender, String command, String message, boolean success) {
        if (commandSender instanceof Player) {
            playerCommandResult((Player) commandSender, command, message, success);
        } else {
            nonPlayerCommandResult(commandSender, message, success);
        }
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This is used internally to send responses to Players. Success full command execution reply
     * can be turned off, while failed commands will not be able to be disabled by starbounddata.packets.starbounddata.packets.server owners.
     * <p>
     *
     * @param playerSession  Player Who sent the command
     * @param message String the message to be sent to the player
     */
    private void playerCommandResult(Player playerSession, String command, String message, boolean success) {
        if (success) {
            StarNub.getLogger().cCommandSuccessPrint(playerSession, message);
            if (command == null || !noCommandDeliveryRegister.contains(command)) {
                playerMessageSender("StarNub", playerSession, ChatReceiveChannel.COMMAND, message);
            }
        } else {
            StarNub.getLogger().cCommandFailurePrint(playerSession, message);
            playerMessageSender("StarNub", playerSession, ChatReceiveChannel.COMMAND, message);
        }
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This is used to send a message to the console or a management client
     * <p>
     *
     * @param sender  Object which represents the message sender can be a string, class, player, management client
     * @param message String the message to be sent to the management client
     */
    private void nonPlayerCommandResult(Object sender, String message, boolean success) {
         if (sender instanceof String) {
            if (((String) sender).equalsIgnoreCase("sn") || ((String) sender).equalsIgnoreCase("starnub")) {
                if (success) {
                    StarNub.getLogger().cCommandSuccessPrint("StarNub", message);
                } else {
                    StarNub.getLogger().cCommandFailurePrint("StarNub", message);
                }
            } else {
                if (success) {
                    StarNub.getLogger().cCommandSuccessPrint(StarNub.getPluginManager().getPluginPackageClassNameString((String) sender).getPLUGIN_NAME(), message);
                } else {
                    StarNub.getLogger().cCommandFailurePrint(StarNub.getPluginManager().getPluginPackageClassNameString((String) sender).getPLUGIN_NAME(), message);
                }
            }
        }
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This is used to send a message to the starbounddata.packets.starbounddata.packets.server on a specified player channel. This method is useful
     * for sending commands on behalf of the player, or any type of starbounddata.packets.chat message on behalf of the player.
     * <p>
     *
     * @param sender           Object which represents the message sender can be a string, class, player, management client
     * @param playerIdentifier Object which represents the player identifier can be a Player name, Player class,
     *                         Player IP, Player uuid, Client ID or ChannelHandlerContext (CTX) (Server or Client Side)
     * @param channel          String for starbound starbounddata.packets.chat channel, acceptable is (planet, universe)
     * @param message          String the message to be sent to the player
     */
    public void serverChatMessageToServerForPlayer(Object sender, Object playerIdentifier, ChatSendChannel channel, String message) {
        Player playerSession = StarNub.getServer().getConnections().getOnlinePlayerByAnyIdentifier(playerIdentifier);
        if (playerSession == null) {
            playerNotFoundMsg(sender);
        } else {
            StarNub.getPacketSender().serverPacketSender(playerSession, new ChatSendPacket(channel, message));
            StarNub.getLogger().cChatPrint(playerIdentifier, message, "Server");
        }
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This is used to send a message to all players. But will not exclude anyone.
     * <p>
     *
     * @param sender  Object which represents the message sender can be a string, class, player, management client
     * @param message message to be sent to the starbounddata.packets.starbounddata.packets.server
     */
    public void serverBroadcast(Object sender, String message) {
        finalServerBroadcast(sender, message, null);
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This is used to send a message to all players except the player that it was set to exclude
     * this is useful when a player is joining we do not want to hit him with a starbounddata.packets.chat message. This method
     * will bypass starbounddata.packets.chat blocking.
     * <p>
     *
     * @param sender         Object which represents the message sender can be a string, class, player, management client
     * @param excludedPlayerSession Player who should be excluded from receiving this message
     * @param message        message to be sent to the starbounddata.packets.starbounddata.packets.server
     */
    public void serverBroadcast(Object sender, Player excludedPlayerSession, String message) {
        ArrayList<Player> excludedPlayerSessions = new ArrayList<Player>();
        excludedPlayerSessions.add(excludedPlayerSession);
        finalServerBroadcast(sender, message, excludedPlayerSessions);
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This is used to send a message to all players except the players are in the ArrayList
     * this is useful when a player is joining we do not want to hit him with a starbounddata.packets.chat message. This method
     * will bypass starbounddata.packets.chat blocking.
     * <p>
     *
     * @param sender          Object which represents the message sender can be a string, class, player, management client
     * @param excludedPlayerSessions ArrayList of Players who should be excluded from receiving this message
     * @param message         message to be sent to the starbounddata.packets.starbounddata.packets.server
     */
    public void serverBroadcast(Object sender, ArrayList<Player> excludedPlayerSessions, String message) {
        finalServerBroadcast(sender, message, excludedPlayerSessions);
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This is a detailed method used in preparing and using the {@link PacketSender}
     * to send a message to everyone online less the players in the ArrayList.
     * <p>
     *
     * @param sender          Object which represents the message sender can be a string, class, player, management client
     * @param message         String the message to be sent to the player
     * @param excludedPlayerSessions ArrayList of Players who should be excluded from receiving this message
     */
    private void finalServerBroadcast(Object sender, String message, ArrayList<Player> excludedPlayerSessions) {
        StarNub.getPacketSender().playerPacketBroadcast(
                StarNub.getServer().getConnections().getConnectedPlayers(),
                playerPacketCraft(sender, ChatReceiveChannel.UNIVERSE, true, message),
                excludedPlayerSessions);
        StarNub.getLogger().cChatPrint(sender, message, "All Players");
    }


    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This will return a build name regardless if it is a player or not. Set the
     * tags and originalName booleans if you want those to be displayed should it return a player.
     * This is meant to be used for console printing and thus will not return a color hex tag.
     * <p>
     *
     * @param nameToBuild Object representing the name to be built
     * @param tags boolean are we including player tags
     * @param originalName boolean do we need to include the original name
     * @return String representing the built name
     */
    public String cUnknownNameBuilder(Object nameToBuild, boolean tags, boolean originalName){
        return nameBuilder(nameToBuild, true, false, tags, originalName);
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This will return a build name regardless if it is a player or not. Set the
     * tags and originalName booleans if you want those to be displayed should it return a player.
     * This is meant to be used for player messaging and it will print a color hex tag.
     * <p>
     *
     * @param nameToBuild Object representing the name to be built
     * @param tags boolean are we including player tags
     * @param originalName boolean do we need to include the original name
     * @return String representing the built name
     */
    public String msgUnknownNameBuilder(Object nameToBuild, boolean tags, boolean originalName){
        return nameBuilder(nameToBuild, false, true, tags, originalName);
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This will build a string to represent the NON PLAYER name to print into the console.
     * <p>
     *
     * @param nameToBuild Object representing the name to be built
     * @return String representing the name to print
     */
    public String cNonPlayerNameBuild(Object nameToBuild){
        return nameBuilder(nameToBuild, true, false, false, false);
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This will build a string to represent the PLAYER name to print into the console. Names are built using Nick Names, you can
     * opt to include the original Player Name.
     * <p>
     *
     * @param nameToBuild Object representing the name to be built
     * @param tags boolean are we including player tags
     * @param originalName boolean include the original name
     * @return String representing the final built name
     */
    private String cPlayerNameBuild(Object nameToBuild, boolean tags, boolean originalName){
        return nameBuilder(nameToBuild, true, false, tags, originalName);
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This will build a string to represent the NON PLAYER name to be used in a starbounddata.packets.chat packet.
     * opt to include the original Player Name.
     * <p>
     *
     * @param nameToBuild Object representing the name to be built
     * @return String representing the name to print
     */
    private String msgNonPlayerNameBuild(Object nameToBuild){
        return nameBuilder(nameToBuild, false, true, false, false);
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This will build a string to represent the PLAYER name to be used in a starbounddata.packets.chat packet. It will
     * include the players name color or the starbounddata.packets.starbounddata.packets.server default. Names are built using Nick Names, you can
     * opt to include the original Player Name.
     * <p>
     *
     * @param nameToBuild Object representing the name to be built
     * @param tags boolean are we including player tags
     * @param originalName boolean include the original name
     * @return String representing the final built name
     */
    private String msgPlayerNameBuild(Object nameToBuild, boolean tags, boolean originalName){
        return nameBuilder(nameToBuild, false, true, tags, originalName);
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This is the mid point method in name building a name which takes higher level API
     * and then formulates many possibilities.
     * <p>
     *
     * @param nameToBuild Object representing the name to be built
     * @param console boolean is this name for console
     * @param color boolean do we need to include  hex color
     * @param playerTags boolean do we need to include player tags
     * @param originalName boolean do we need to include the ordinal name
     * @return String representing the built name
     */
    private String nameBuilder (Object nameToBuild, boolean console, boolean color, boolean playerTags, boolean originalName){
        String nameString = "";
        if (nameToBuild instanceof Player) {
            Player player = (Player) nameToBuild;
            if (console) {
                nameString = cPlayerNameBuilderFinal(player, playerTags, originalName);
            } else {
                nameString = msgPlayerNameBuilderFinal(player, playerTags, originalName);
            }
        } else if (nameToBuild instanceof String){
            String string = (String) nameToBuild;
            if (string.equalsIgnoreCase("starbound")){
                nameString = "Starbound";
            } else if (string.equalsIgnoreCase("starnub")){
                nameString = "StarNub";
//            } else if (StarNub.getPluginManager().hasPlugin(string)) {
//                nameString = StarNub.getPluginManager().getPluginPackageClassNameString(string).getPLUGIN_NAME();
            } else {
                nameString = string;
            }
            if (color){
                nameString = gameColors.getDefaultServerNameColor() + nameString;
            }
        }
        return nameString;
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This is the mid point method in name building a name which takes higher level API
     * and then formulates many possibilities.
     * <p>
     *
     * @param player Player representing the name we are building
     * @param playerTags boolean do we need to include player tags
     * @param originalName boolean do we need to include the ordinal name
     * @return String built player name
     */
    public String msgPlayerNameBuilderFinal(Player player, boolean playerTags, boolean originalName){
        String playerName = gameColors.getDefaultNameColor() +  player.getNickName();
        if (playerTags) {
            String prefix = "";
            String suffix = "";
            if (player.getCharacter().getAccount() != null) {
                Settings sett = player.getCharacter().getAccount().getAccountSettings();
                if (sett.getChatPrefix1() != null) {
                    prefix = sett.getChatPrefix1().getColorTag();
                }
                if (sett.getChatPrefix2() != null) {
                    prefix = prefix + sett.getChatPrefix2().getColorTag();
                }
                if (sett.getChatSuffix1() != null) {
                    suffix = sett.getChatSuffix1().getColorTag();
                }
                if (sett.getChatSuffix2() != null) {
                    suffix = suffix + sett.getChatSuffix2().getColorTag();
                }
                if (prefix != null && prefix.length() > 0) {
                    playerName = prefix + " " + playerName;
                }
                if (suffix != null && suffix.length() > 0) {
                    playerName = playerName + " " + suffix;
                }
            } else {
                playerName = StarNub.getServer().getConnections().getGroupSync().getNoAccountGroup().getTag().getColorTag() + " " + playerName;
            }
        }
        if (originalName){
            playerName = player.getGameName()+ " (Nick: " + playerName + ")";
        }
        return playerName;
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This is the mid point method in name building a name which takes higher level API
     * and then formulates many possibilities.
     * <p>
     *
     * @param player Player representing the name we are building
     * @param playerTags boolean do we need to include player tags
     * @param originalName boolean do we need to include the ordinal name
     * @return String built player name
     */
    public String cPlayerNameBuilderFinal(Player player, boolean playerTags, boolean originalName){
        String playerName = player.getCleanNickName();
        if (playerTags) {
            String prefix = "";
            String suffix = "";
            if (player.getCharacter().getAccount() != null) {
                Settings sett = player.getCharacter().getAccount().getAccountSettings();
                if (sett.getChatPrefix1() != null) {
                    prefix = sett.getChatPrefix1().getCleanTag();
                }
                if (sett.getChatPrefix2() != null) {
                    prefix = prefix + sett.getChatPrefix2().getCleanTag();
                }
                if (sett.getChatSuffix1() != null) {
                    suffix = sett.getChatSuffix1().getCleanTag();
                }
                if (sett.getChatSuffix2() != null) {
                    suffix = suffix + sett.getChatSuffix2().getCleanTag();
                }
                if (prefix != null && prefix.length() > 0) {
                    playerName = prefix + " " + playerName;
                }
                if (suffix != null && suffix.length() > 0) {
                    playerName = playerName + " " + suffix;
                }
            } else {
                playerName = StarNub.getServer().getConnections().getGroupSync().getNoAccountGroup().getTag().getCleanTag() + " " + playerName;
            }
        }
        if (originalName){
            playerName = player.getCharacter().getCleanName() + " (Nick: " + playerName + ")";
        }
        return playerName;
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This is the core method for building starbounddata.packets destined for players.
     * <p>
     *
     * @param sender Object which represents the message sender can be a string or a Player
     * @param channel ChatChannel the type of reply (universe, planet, whisper, command)
     * @param message String the message to be sent to the player
     * @return ChatReceivePacket the starbounddata.packets.chat packet to be sent to player
     */
    private ChatReceivePacket playerPacketCraft(Object sender, ChatReceiveChannel channel, boolean tags, String message){
        if (!(sender instanceof Player) && !channel.equals(ChatReceiveChannel.COMMAND)) {
            message = gameColors.getDefaultServerChatColor() + message;
        }
        return new ChatReceivePacket(channel, "", 0, msgUnknownNameBuilder(sender, tags, false), message);
    }
}