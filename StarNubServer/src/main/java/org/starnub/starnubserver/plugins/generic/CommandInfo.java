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

package org.starnub.starnubserver.plugins.generic;

import org.starnub.starnubserver.plugins.Command;

import java.util.HashSet;

public class CommandInfo {

    private final String COMMANDS_NAME;
    private final String COMMANDS_ALIAS;
    private final HashSet<Command> COMMANDS = new HashSet<>();

    /**
     * Used to construct information
     *
     * @param COMMANDS_NAME    String representing the commands name
     * @param COMMANDS_ALIAS   String representing the commands alias
     * @param COMMANDS HashSet containing command packages
     */
    public CommandInfo(String COMMANDS_NAME, String COMMANDS_ALIAS, HashSet<Command> COMMANDS) {
        this.COMMANDS_NAME = COMMANDS_NAME;
        this.COMMANDS_ALIAS = COMMANDS_ALIAS;
        this.COMMANDS.addAll(COMMANDS);
    }

    public String getCOMMANDS_NAME() {
        return COMMANDS_NAME;
    }

    public String getCOMMANDS_ALIAS() {
        return COMMANDS_ALIAS;
    }

    public HashSet<Command> getCOMMANDS() {
        return COMMANDS;
    }

    public Command getCommandByName(String commandString){
        commandString = commandString.toLowerCase();
        for (Command command : COMMANDS){
            HashSet<String> commandNameStrings = command.getCOMMANDS();
            if (commandNameStrings.contains(commandString)){
                return command;
            }
        }
        return null;
    }
}
