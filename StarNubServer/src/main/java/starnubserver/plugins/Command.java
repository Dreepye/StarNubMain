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
     * This is for plugins to not have to construct anything but allows for StarNub to construct this
     */
    public Command() {
    }

    /**
     * @param COMMANDS            String representing the command name
     * @param MAIN_ARGS           String the commands main args
     * @param COMMAND_CLASS       String the location of the class in the plugin
     * @param PLUGIN_COMMAND_NAME String used to build plugin permissions
     * @param CAN_USE             int 0 = Player, 1 = Remote Player, 2 = Both can use
     * @param DESCRIPTION         String description of what the command does
     */
    public Command(HashSet<String> COMMANDS, HashSet<String> MAIN_ARGS, String COMMAND_CLASS, String PLUGIN_COMMAND_NAME, int CAN_USE, String DESCRIPTION) {
        super(COMMANDS, MAIN_ARGS, COMMAND_CLASS, PLUGIN_COMMAND_NAME, CAN_USE, DESCRIPTION);
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method is required when a plugin is making a command they will make a command class that extends Command and
      * this method is what StarNub will invoke when running a command for a user.
     *
     * @param senderClass  the starnubclient utilities.file of the sender, 0 will be console (remote or local) management. You can use this item to look up
     *                          the Connected Player records use  variableName  = StarNub.getServer().getConnectedPlayers().get(senderClientID);
     * @param command           String the command that was used
     * @param args              String[] the actual arguments accessible by args[0], args[1],... etc
     */
    public abstract void onCommand(Object senderClass, String command, String[] args);

}
