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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

/**
 * Represents a CommandPackage that contains command details
 * <p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class CommandPackage {

    private final String PLUGIN_NAME;
    private final HashSet<String> COMMANDS;
    private final HashSet<String> MAIN_ARGS;
    private final HashSet<String> PERMISSIONS;
    private final HashMap<String, Integer> CUSTOM_SPLIT;
    private final CanUse CAN_USE;
    private final String DESCRIPTION;
    private final String COMMAND_CLASS;
    private Plugin PLUGIN;

    /**
     * @param PLUGIN_NAME         String representing the plugin that owns this command
     * @param COMMANDS            String representing the command name
     * @param MAIN_ARGS           String the commands main args
     * @param COMMAND_CLASS       String the location of the class in the plugin
     * @param COMMAND_NAME        String used to build plugin permissions
     * @param CAN_USE             int 0 = Player, 1 = Remote Player, 2 = Both can use
     * @param DESCRIPTION         String description of what the command does
     */
    public CommandPackage(String PLUGIN_NAME, HashSet<String> COMMANDS, HashSet<String> MAIN_ARGS, HashMap<String, Integer> CUSTOM_SPLIT,  String COMMAND_CLASS, String COMMAND_NAME, int CAN_USE, String DESCRIPTION) {
        this.PLUGIN_NAME = PLUGIN_NAME;
        this.COMMANDS = COMMANDS;
        this.MAIN_ARGS = MAIN_ARGS;
        this.COMMAND_CLASS = COMMAND_CLASS;
        this.PERMISSIONS = new HashSet<>();
        for (String command : COMMANDS) {
            if (MAIN_ARGS.size() == 0){
                String permission = COMMAND_NAME + "." + command;
                PERMISSIONS.add(permission.toLowerCase());
            } else {
                for (String mainArg : MAIN_ARGS) {
                    String permission = COMMAND_NAME + "." + command + "." + mainArg;
                    PERMISSIONS.add(permission.toLowerCase());
                }
            }
        }
        this.CUSTOM_SPLIT = CUSTOM_SPLIT;
        this.CAN_USE = CanUse.values()[CAN_USE];
        this.DESCRIPTION = DESCRIPTION;
    }

    public String getPLUGIN_NAME() {
        return PLUGIN_NAME;
    }

    public void setPLUGIN(Plugin PLUGIN) {
        this.PLUGIN = PLUGIN;
    }

    public Plugin getPLUGIN() {
        return PLUGIN;
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

    public HashMap<String, Integer> getCUSTOM_SPLIT() {
        return CUSTOM_SPLIT;
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
        ArrayList<String> commandsList = new ArrayList<>(COMMANDS);
        linkedHashMapFinal.put("Commands", commandsList);
        ArrayList<String> mainArgsList = new ArrayList<>(MAIN_ARGS);
        linkedHashMapFinal.put("Main Args", mainArgsList);
        String canUse = "";
        switch (CAN_USE){
            case PLAYER: canUse = "Players"; break;
            case REMOTE_PLAYER: canUse = "Remote Player"; break;
            case BOTH: canUse = "Players and Remote Players"; break;
        }
        linkedHashMapFinal.put("Can Use", canUse);
        linkedHashMapFinal.put("Custom Split", CUSTOM_SPLIT);
        ArrayList<String> permissionsList = new ArrayList<>(PERMISSIONS);
        linkedHashMapFinal.put("Permissions", permissionsList);
        return linkedHashMapFinal;
    }
}
