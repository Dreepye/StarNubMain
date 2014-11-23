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

package server.plugins;

/**
 * Represents the a StarNub Command.
 * The plugin maker must @Override the onCommand method.
 * <p>
 * This Command class is abstract and must be extended. This is what
 * all Command classes should look like by default.
 * <p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public abstract class Command {

     /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method is required when a plugin is making a command they will make a command class that extends Command and
      * this method is what StarNub will invoke when running a command for a user.
     *
     * @param senderClass  the client utilities.file of the sender, 0 will be console (remote or local) management. You can use this item to look up
     *                          the Connected Player records use  variableName  = StarNub.getServer().getConnectedPlayers().get(senderClientID);
     * @param command           String the command that was used
     * @param args              String[] the actual arguments accessible by args[0], args[1],... etc
     */
    public abstract void onCommand(Object senderClass, String command, String[] args);

}
