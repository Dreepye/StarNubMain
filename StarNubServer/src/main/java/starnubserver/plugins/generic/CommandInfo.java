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

package starnubserver.plugins.generic;

import starnubserver.plugins.CommandPackage;

import java.util.HashSet;

public class CommandInfo {

    public final String COMMANDS_NAME;
    public final String COMMANDS_ALIAS;
    private final HashSet<CommandPackage> COMMAND_PACKAGES;

    /**
     * Used to construct information
     *
     * @param COMMANDS_NAME    String representing the commands name
     * @param COMMANDS_ALIAS   String representing the commands alias
     * @param COMMAND_PACKAGES HashSet containing command packages
     */
    public CommandInfo(String COMMANDS_NAME, String COMMANDS_ALIAS, HashSet<CommandPackage> COMMAND_PACKAGES) {
        this.COMMANDS_NAME = COMMANDS_NAME;
        this.COMMANDS_ALIAS = COMMANDS_ALIAS;
        this.COMMAND_PACKAGES = COMMAND_PACKAGES;
    }

    public String getCOMMANDS_NAME() {
        return COMMANDS_NAME;
    }

    public String getCOMMANDS_ALIAS() {
        return COMMANDS_ALIAS;
    }

    public HashSet<CommandPackage> getCOMMAND_PACKAGES() {
        return COMMAND_PACKAGES;
    }


}
