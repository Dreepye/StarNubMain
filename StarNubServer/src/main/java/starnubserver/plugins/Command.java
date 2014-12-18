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

package starnubserver.plugins;

import starnubserver.connections.player.session.PlayerSession;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Represents a Command that contains command details this command must be extended by a plugin
 * to be used as a command, as well as have a command.yml pointing to this class.
 * <p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public abstract class Command extends CommandPackage{

    /**
     * @param PLUGIN_NAME         String representing the plugin that owns this command
     * @param COMMANDS            HashMap representing the command name
     * @param MAIN_ARGS           HashMap the commands main args
     * @param CUSTOM_SPLIT        HashMap containing a map of commands with a integer for when to split the command
     * @param COMMAND_CLASS       String the location of the class in the plugin
     * @param COMMAND_NAME        String used to build plugin permissions
     * @param CAN_USE             int 0 = Player, 1 = Remote Player, 2 = Both can use
     * @param DESCRIPTION         String description of what the command does
     */
    public Command(String PLUGIN_NAME ,  HashSet<String> COMMANDS, HashSet<String> MAIN_ARGS, HashMap<String, Integer> CUSTOM_SPLIT, String COMMAND_CLASS, String COMMAND_NAME, int CAN_USE, String DESCRIPTION) {
        super(PLUGIN_NAME, COMMANDS, MAIN_ARGS, CUSTOM_SPLIT, COMMAND_CLASS, COMMAND_NAME, CAN_USE, DESCRIPTION);
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method is required when a plugin is making a command they will make a command class that extends Command and
      * this method is what StarNub will invoke when running a command for a user.
     *
     * @param playerSession     PlayerSession for the person who used the command
     *                          the Connected Player records use  variableName  = StarNub.getServer().getConnectedPlayers().get(senderClientID);
     * @param command           String the command that was used
     * @param args              String[] the actual arguments accessible by args[0], args[1],... etc
     */
    public abstract void onCommand(PlayerSession playerSession, String command, String[] args);

}
