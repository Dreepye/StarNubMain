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

package starnubserver.plugins;

import starnubdata.generic.CanUse;

import java.util.*;

/**
 * Represents a CommandPackage that contains command details
 * <p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class CommandPackage {

    private HashSet<String> COMMANDS;
    private HashSet<String> MAIN_ARGS;
    private HashSet<String> PERMISSIONS;
    private CanUse CAN_USE;
    private String DESCRIPTION;
    private String COMMAND_CLASS;

    /**
     * This is for plugins to not have to construct anything but allows for StarNub to construct this
     */
    public CommandPackage() {
    }

    /**
     * @param COMMANDS            String representing the command name
     * @param MAIN_ARGS           String the commands main args
     * @param COMMAND_CLASS       String the location of the class in the plugin
     * @param COMMAND_NAME        String used to build plugin permissions
     * @param CAN_USE             int 0 = Player, 1 = Remote Player, 2 = Both can use
     * @param DESCRIPTION         String description of what the command does
     */
    public CommandPackage(HashSet<String> COMMANDS, HashSet<String> MAIN_ARGS, String COMMAND_CLASS, String COMMAND_NAME, int CAN_USE, String DESCRIPTION) {
        this.COMMANDS = COMMANDS;
        this.MAIN_ARGS = MAIN_ARGS;
        this.COMMAND_CLASS = COMMAND_CLASS;
        this.PERMISSIONS = new HashSet<>();
        for (String command : COMMANDS) {
            for (String mainArg : MAIN_ARGS) {
                String permission = COMMAND_NAME + "." + command + "." + mainArg;
                PERMISSIONS.add(permission);
            }
        }
        this.CAN_USE = CanUse.values()[CAN_USE];
        this.DESCRIPTION = DESCRIPTION;
    }

    public HashSet<String> getCOMMANDS() {
        return COMMANDS;
    }

    public HashSet<String> getMAIN_ARGS() {
        return MAIN_ARGS;
    }

    public CanUse getCAN_USE() {
        return CAN_USE;
    }

    public HashSet<String> getPERMISSIONS() {
        return PERMISSIONS;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public String getCOMMAND_CLASS() {
        return COMMAND_CLASS;
    }

    public LinkedHashMap<String, Object> getCommandDetailsMap(String pluginName, String pluginCommandName, String pluginNameAlias){
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("Plugin Name", pluginName);
        linkedHashMap.put("Command Name", pluginCommandName);
        linkedHashMap.put("Command Alias", pluginNameAlias);
        linkedHashMap.put("Class", COMMAND_CLASS);
        return getCommandDetailsMap(linkedHashMap);
    }

    public LinkedHashMap<String,Object> getCommandDetailsMap(LinkedHashMap<String, String> linkedHashMap) {
        LinkedHashMap<String, Object> linkedHashMapFinal = new LinkedHashMap<>();
        linkedHashMapFinal.put("Plugin Command Details", linkedHashMap);
        linkedHashMapFinal.put("Commands", COMMANDS);
        linkedHashMapFinal.put("Main Args", MAIN_ARGS);
        linkedHashMapFinal.put("Can Use", CAN_USE.toString());
        linkedHashMapFinal.put("Permissions", PERMISSIONS);
        return linkedHashMapFinal;
    }
}
