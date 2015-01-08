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

package org.starnub.starnubserver.resources;

import org.starnub.starbounddata.packets.Packet;
import org.starnub.starbounddata.types.color.GameColors;
import org.starnub.starnubserver.StarNub;
import org.starnub.starnubserver.connections.player.account.Settings;
import org.starnub.starnubserver.connections.player.session.PlayerSession;
import org.starnub.starnubserver.StarNub;
import org.starnub.starnubserver.connections.player.account.Settings;
import org.starnub.starnubserver.connections.player.session.PlayerSession;

/**
 * Represents StarNubs Name Builder
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class NameBuilder {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final NameBuilder instance = new NameBuilder();

    /**
     * This constructor is private - Singleton Pattern
     */
    public NameBuilder(){}

    /**
     *
     * @return MessageSender Singleton Instance
     */
    public static NameBuilder getInstance() {
        return instance;
    }

    /**
     * Recommended: For connections use with StarNub.
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
     * Recommended: For connections use with StarNub.
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
     * Recommended: For connections use with StarNub.
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
     * Recommended: For connections use with StarNub.
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
     * Recommended: For connections use with StarNub.
     * <p>
     * Uses: This will build a string to represent the NON PLAYER name to be used in a chat packet.
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
     * Recommended: For connections use with StarNub.
     * <p>
     * Uses: This will build a string to represent the PLAYER name to be used in a chat packet. It will
     * include the players name color or the default color default. Names are built using Nick Names, you can
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
     * Recommended: For connections use with StarNub.
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
        if (nameToBuild instanceof Packet){
            nameToBuild = PlayerSession.getPlayerSession((Packet) nameToBuild);
        }
        if (nameToBuild instanceof PlayerSession) {
            PlayerSession playerSession = (PlayerSession) nameToBuild;
            if (console) {
                nameString = cPlayerNameBuilderFinal(playerSession, playerTags, originalName);
            } else {
                nameString = msgPlayerNameBuilderFinal(playerSession, playerTags, originalName);
            }
        } else if (nameToBuild instanceof String){
            String string = (String) nameToBuild;
            if (string.equalsIgnoreCase("starbound")){
                nameString = "Starbound";
            } else if (string.equalsIgnoreCase("starnub")){
                nameString = "StarNub";
            } else if (string.equalsIgnoreCase("servername")) {
                nameString = (String) StarNub.getConfiguration().getNestedValue("starnub_info", "server_name");
            } else {
                nameString = string;
            }
            if (color){
                nameString = GameColors.getInstance().getDefaultServerNameColor() + nameString;
            }
        }
        return nameString;
    }

    /**
     * Recommended: For connections use with StarNub.
     * <p>
     * Uses: This is the mid point method in name building a name which takes higher level API
     * and then formulates many possibilities.
     * <p>
     *
     * @param playerSession Player representing the name we are building
     * @param playerTags boolean do we need to include player tags
     * @param originalName boolean do we need to include the ordinal name
     * @return String built player name
     */
    public String msgPlayerNameBuilderFinal(PlayerSession playerSession, boolean playerTags, boolean originalName){
        String playerName = GameColors.getInstance().getDefaultNameColor() +  playerSession.getNickName();
        if (playerTags) {
            String prefix = "";
            String suffix = "";
            if (playerSession.getPlayerCharacter().getAccount() != null) {
                Settings settings = playerSession.getPlayerCharacter().getAccount().getAccountSettings();
                if (settings.getChatPrefix1() != null) {
                    prefix = settings.getChatPrefix1().getBuiltGameTag();
                }
                if (settings.getChatPrefix2() != null) {
                    prefix = prefix + settings.getChatPrefix2().getBuiltGameTag();
                }
                if (settings.getChatSuffix1() != null) {
                    suffix = settings.getChatSuffix1().getBuiltGameTag();
                }
                if (settings.getChatSuffix2() != null) {
                    suffix = suffix + settings.getChatSuffix2().getBuiltGameTag();
                }
                if (prefix != null && prefix.length() > 0) {
                    playerName = prefix + " " + playerName;
                }
                if (suffix != null && suffix.length() > 0) {
                    playerName = playerName + " " + suffix;
                }
            } else {
//                playerName = StarNub.getServer().getConnectionss().getGroupSync().getNoAccountGroup().getTag().getColorTag() + " " + playerName;
            }
        }
        if (originalName){
            playerName = playerSession.getGameName() + " (Nick: " + playerName + ")";
        }
        return playerName;
    }

    /**
     * Recommended: For connections use with StarNub.
     * <p>
     * Uses: This is the mid point method in name building a name which takes higher level API
     * and then formulates many possibilities.
     * <p>
     *
     * @param playerSession Player representing the name we are building
     * @param playerTags boolean do we need to include player tags
     * @param originalName boolean do we need to include the ordinal name
     * @return String built player name
     */
    public String cPlayerNameBuilderFinal(PlayerSession playerSession, boolean playerTags, boolean originalName){
        String playerName = playerSession.getCleanNickName();
        if (playerTags) {
            String prefix = "";
            String suffix = "";
            if (playerSession.getPlayerCharacter().getAccount() != null) {
                Settings sett = playerSession.getPlayerCharacter().getAccount().getAccountSettings();
                if (sett.getChatPrefix1() != null) {
                    prefix = sett.getChatPrefix1().getBuiltConsoleTag();
                }
                if (sett.getChatPrefix2() != null) {
                    prefix = prefix + sett.getChatPrefix2().getBuiltConsoleTag();
                }
                if (sett.getChatSuffix1() != null) {
                    suffix = sett.getChatSuffix1().getBuiltConsoleTag();
                }
                if (sett.getChatSuffix2() != null) {
                    suffix = suffix + sett.getChatSuffix2().getBuiltConsoleTag();
                }
                if (prefix != null && prefix.length() > 0) {
                    playerName = prefix + " " + playerName;
                }
                if (suffix != null && suffix.length() > 0) {
                    playerName = playerName + " " + suffix;
                }
            } else {
//                playerName = StarNub.getServer().getConnectionss().getGroupSync().getNoAccountGroup().getTag().getCleanTag() + " " + playerName;
            }
        }
        if (originalName){
            playerName = playerSession.getPlayerCharacter().getCleanName() + " (Nick: " + playerName + ")";
        }
        return playerName;
    }
}