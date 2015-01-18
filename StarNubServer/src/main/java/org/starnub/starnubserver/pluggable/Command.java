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

package org.starnub.starnubserver.pluggable;

import org.starnub.starnubdata.generic.CanUse;
import org.starnub.starnubserver.connections.player.session.PlayerSession;
import org.starnub.utilities.file.yaml.YamlWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class Command extends Pluggable {

    private HashSet<String> mainArgs;
    private HashSet<String> permissions;
    private int customSplit;
    private CanUse canUse;

    public Command() {
    }

    public HashSet<String> getMainArgs() {
        return mainArgs;
    }

    public HashSet<String> getPermissions() {
        return permissions;
    }

    public int getCustomSplit() {
        return customSplit;
    }

    public CanUse getCanUse() {
        return canUse;
    }

    @Override
    public void loadData(YamlWrapper pluggableInfo){
        type = PluggableType.COMMAND;
        this.mainArgs = new HashSet<>();
        List<String> mainArgsList = (List<String>) pluggableInfo.getValue("main_args");
        this.mainArgs.addAll(mainArgsList);
        this.permissions = new HashSet<>();
        if (mainArgs.size() == 0){
            String permission = details.getOWNER() + "." + details.getNAME();
            permissions.add(permission.toLowerCase());
        } else {
            for (String mainArg : mainArgs) {
                String permission = details.getOWNER() + "." + details.getNAME() + "." + mainArg;
                permissions.add(permission.toLowerCase());
            }
        }
        this.customSplit = (int) pluggableInfo.getValue("custom_split");
        Integer canUseInteger = (Integer) pluggableInfo.getValue("can_use");
        this.canUse = CanUse.values()[canUseInteger];
    }

    @Override
    public LinkedHashMap<String, Object> getDetailsMap() throws IOException { //TODO FIX
        LinkedHashMap<String, Object> linkedHashMapFinal = new LinkedHashMap<>();
        ArrayList<String> mainArgsList = new ArrayList<>(mainArgs);
        linkedHashMapFinal.put("Main Args", mainArgsList);
        String canUseString = "";
        switch (canUse){
            case PLAYER: canUseString = "Players"; break;
            case REMOTE_PLAYER: canUseString = "Remote Player"; break;
            case BOTH: canUseString = "Players and Remote Players"; break;
        }
        linkedHashMapFinal.put("Can Use", canUseString);
        linkedHashMapFinal.put("Custom Split", customSplit);
        ArrayList<String> permissionsList = new ArrayList<>(permissions);
        linkedHashMapFinal.put("Permissions", permissionsList);
        return linkedHashMapFinal;
    }

    public abstract void onCommand(PlayerSession playerSession, String command, int argsCount, String[] args);
    public abstract void onRegister();

    @Override
    public String toString() {
        return "Command{" +
                ", mainArgs=" + mainArgs +
                ", permissions=" + permissions +
                ", customSplit=" + customSplit +
                ", canUse=" + canUse +
                "} " + super.toString();
    }
}
