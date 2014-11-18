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

package server.plugins;

import lombok.Getter;

import java.util.ArrayList;

/**
 * Represents the StarNubs CommandPackage. This information
 * is used internally when the plugin is loaded, we build this
 * from the commandname.yml so when a command is used we have
 * a reference and how to use the command inputted.
 * <p>
 * NOTE: These fields are public for the sake of information dumping via YAML.
 * <p>
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public class CommandPackage {

    /**
     * This needs to be set by the command creator.
     * <p>
     * When creating this field just separate the commands
     * that you want to trigger this plugin.
     * Example "menu"
     * <p>
     * /{pluginname or alias} menu
     * /{pluginname or alias} command
     */
    @Getter
    public final ArrayList<String> COMMANDS;

    /**
     * This needs to be set by the command creator.
     * <p>
     * When creating this field just separate the commands
     * that you want to trigger this plugin.
     * Example "menu"
     * <p>
     * /{pluginname or alias} menu
     * /{pluginname or alias} command
     */
    @Getter
    public final ArrayList<String> COMMAND_MAIN_ARGS;

    /**
     * Where this command is located in the plugin
     */
    @Getter
    public final String COMMAND_CLASS;

//    /**
//     * Do all players have this permission by default
//     */
//    public final boolean PERMISSION_DEFAULT_ALL;

    /**
     * This needs to be set by the command creator. What specific permission is
     * required by the user.
     * <p>
     * Example: {pluginname or alias}.permission
     * <p>
     * Users will have permission if they have the wildcard permission
     * <br>
     * {pluginname or alias}.*
     * <p>
     */
    @Getter
    public final String PERMISSIONS;

    /**
     * This needs to be set by the command creator. True means the console
     * can use this command. False will block the console from using or seeing
     * the command. 1 = Player, 2 = Remote Player, 3 = Both
     */
    @Getter
    public final int CAN_USE;

    /**
     * A short description about your command.
     */
    @Getter
    public final String DESCRIPTION;

    /**
     * The actual command code for the this command
     */
    @Getter
    private final Command COMMAND;

    /**
     * @param COMMANDS String representing the command name
     * @param COMMAND_MAIN_ARGS  String the commands main args
     * @param COMMAND_CLASS String the location of the class in the plugin
     * @param PERMISSIONS   String the permission that StarNub will check on command use
     * @param CAN_USE       int 1 = Player, 2 = Remote Player, 3 = Both can use
     * @param DESCRIPTION   String description of what the command does
     * @param COMMAND       Command the actual command that holds the code
     */
    public CommandPackage(ArrayList<String> COMMANDS, ArrayList<String> COMMAND_MAIN_ARGS, String COMMAND_CLASS, String PERMISSIONS, int CAN_USE,  String DESCRIPTION, Command COMMAND) {
        this.COMMANDS = COMMANDS;
        this.COMMAND_MAIN_ARGS = COMMAND_MAIN_ARGS;
        this.COMMAND_CLASS = COMMAND_CLASS;
        this.PERMISSIONS = PERMISSIONS;
        this.CAN_USE = CAN_USE;
        this.DESCRIPTION = DESCRIPTION;
        this.COMMAND = COMMAND;
    }
}
