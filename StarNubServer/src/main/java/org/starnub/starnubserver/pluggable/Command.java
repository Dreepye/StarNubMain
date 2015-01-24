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
import org.starnub.utilities.arrays.ArrayUtilities;
import org.starnub.utilities.file.yaml.YamlWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class Command extends Pluggable {

    private String[] mainArgs;
    private String[] permissions;
    private int customSplit;
    private CanUse canUse;

    public Command() {
    }

    public String[] getMainArgs() {
        return mainArgs;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public int getCustomSplit() {
        return customSplit;
    }

    public CanUse getCanUse() {
        return canUse;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void loadData(YamlWrapper pluggableInfo){
        List<String> mainArgsList = (List<String>) pluggableInfo.getValue("main_args");
        mainArgs = ArrayUtilities.arrayBuilder(mainArgsList);
        String lowerCaseOrganization = getDetails().getORGANIZATION().toLowerCase();
        String lowerCaseName = getDetails().getNAME().toLowerCase();
        ArrayList<String> linkedList = new ArrayList<>();
        linkedList.add(lowerCaseOrganization + ".*");
        String permission = lowerCaseOrganization + "." + lowerCaseName;
        linkedList.add(permission);
        if (mainArgs != null && mainArgs.length > 0){
            linkedList.add(permission + ".*");
            for (String mainArg : mainArgs) {
                String permissionFull = lowerCaseOrganization + "." + lowerCaseName + "." + mainArg;
                linkedList.add(permissionFull);
            }
        }
        permissions = ArrayUtilities.arrayBuilder(linkedList);
        this.customSplit = (int) pluggableInfo.getValue("custom_split");
        Integer canUseInteger = (Integer) pluggableInfo.getValue("can_use");
        this.canUse = CanUse.values()[canUseInteger];
    }

    @Override
    public LinkedHashMap<String, Object> getDetailsMap() throws IOException { //TODO FIX
        LinkedHashMap<String, Object> linkedHashMapFinal = new LinkedHashMap<>();
        linkedHashMapFinal.put("Main Args", mainArgs);
        String canUseString = "";
        switch (canUse){
            case PLAYER: canUseString = "Players"; break;
            case REMOTE_PLAYER: canUseString = "Remote Player"; break;
            case BOTH: canUseString = "Players and Remote Players"; break;
        }
        linkedHashMapFinal.put("Can Use", canUseString);
        linkedHashMapFinal.put("Custom Split", customSplit);
        linkedHashMapFinal.put("Permissions", permissions);
        return linkedHashMapFinal;
    }

    public abstract void onRegister();
    public abstract void onCommand(PlayerSession playerSession, String command, int argsCount, String[] args);

    @Override
    public String toString() {
        return "Command{" +
                ", mainArgs=" + Arrays.toString(mainArgs) +
                ", permissions=" + Arrays.toString(permissions) +
                ", customSplit=" + customSplit +
                ", canUse=" + canUse +
                "} " + super.toString();
    }
}
