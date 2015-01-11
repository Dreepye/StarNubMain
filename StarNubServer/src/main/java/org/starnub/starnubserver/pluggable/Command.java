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
import org.starnub.starnubserver.pluggable.exceptions.MissingData;
import org.starnub.starnubserver.pluggable.exceptions.PluginDirectoryCreationFailed;
import org.starnub.utilities.file.yaml.YAMLWrapper;
import org.starnub.utilities.file.yaml.YamlUtilities;

import java.io.IOException;
import java.net.URI;
import java.util.*;

public abstract class Command<T> extends Pluggable<T> {

    private String command;
    private HashSet<String> mainArgs;
    private HashSet<String> permissions;
    private HashMap<String, Integer> customSplit;
    private CanUse canUse;

    /**
     * Creates a new <code>File</code> instance by converting the given
     * pathname string into an abstract pathname.  If the given string is
     * the empty string, then the result is the empty abstract pathname.
     *
     * @param pathname A pathname string
     * @throws NullPointerException If the <code>pathname</code> argument is <code>null</code>
     */
    public Command(String pathname) throws MissingData, IOException, PluginDirectoryCreationFailed {
        super(pathname);
    }

    /**
     * Creates a new <tt>File</tt> instance by converting the given
     * <tt>file:</tt> URI into an abstract pathname.
     * <p>
     * <p> The exact form of a <tt>file:</tt> URI is system-dependent, hence
     * the transformation performed by this constructor is also
     * system-dependent.
     * <p>
     * <p> For a given abstract pathname <i>f</i> it is guaranteed that
     * <p>
     * <blockquote><tt>
     * new File(</tt><i>&nbsp;f</i><tt>.{@link #toURI() toURI}()).equals(</tt><i>&nbsp;f</i><tt>.{@link #getAbsoluteFile() getAbsoluteFile}())
     * </tt></blockquote>
     * <p>
     * so long as the original abstract pathname, the URI, and the new abstract
     * pathname are all created in (possibly different invocations of) the same
     * Java virtual machine.  This relationship typically does not hold,
     * however, when a <tt>file:</tt> URI that is created in a virtual machine
     * on one operating system is converted into an abstract pathname in a
     * virtual machine on a different operating system.
     *
     * @param uri An absolute, hierarchical URI with a scheme equal to
     *            <tt>"file"</tt>, a non-empty path component, and undefined
     *            authority, query, and fragment components
     * @throws NullPointerException     If <tt>uri</tt> is <tt>null</tt>
     * @throws IllegalArgumentException If the preconditions on the parameter do not hold
     * @see #toURI()
     * @see java.net.URI
     * @since 1.4
     */
    public Command(URI uri) throws MissingData, IOException, PluginDirectoryCreationFailed {
        super(uri);
    }

    public String getCommand() {
        return command;
    }

    public HashSet<String> getMainArgs() {
        return mainArgs;
    }

    public HashSet<String> getPermissions() {
        return permissions;
    }

    public HashMap<String, Integer> getCustomSplit() {
        return customSplit;
    }

    public CanUse getCanUse() {
        return canUse;
    }

    @Override
    public void loadData(YAMLWrapper pluggableInfo) throws MissingData, IOException, PluginDirectoryCreationFailed {
        super.loadData(pluggableInfo);
        this.command = (String) pluggableInfo.getValue("command");
        this.mainArgs = new HashSet<>();
        List<String> mainArgsList = (List<String>) pluggableInfo.getValue("main_args");
        this.mainArgs.addAll(mainArgsList);
        this.permissions = new HashSet<>();
        if (mainArgs.size() == 0){
            String permission = pluggableDetails.getNAME() + "." + command;
            permissions.add(permission.toLowerCase());
        } else {
            for (String mainArg : mainArgs) {
                String permission = pluggableDetails.getNAME() + "." + command + "." + mainArg;
                permissions.add(permission.toLowerCase());
            }
        }
        this.customSplit = new HashMap<>();
        Map<String, Integer> customSplitMap = (Map<String, Integer>) pluggableInfo.getValue("custom_split");
        this.customSplit.putAll(customSplitMap);
        Integer canUseInteger = (Integer) pluggableInfo.getValue("can_use");
        this.canUse = CanUse.values()[canUseInteger];
    }

    @Override
    public void dumpDetails() throws IOException {
        LinkedHashMap pluginDetailDump = new LinkedHashMap();
        LinkedHashMap<String, Object> pluggableDetailsMap = pluggableDetails.getPluginDetailsMap();
        LinkedHashMap<String, Object> commandDetailsMap = getCommandDetailsMap();
        pluginDetailDump.put("Pluggable Details", pluggableDetailsMap);
        pluginDetailDump.put("Command Details", commandDetailsMap);
        YamlUtilities.toFileYamlDump(pluginDetailDump, "StarNub/Commands/Information" + pluggableDetails.getNAME() + "/Information/", "Plugin_Details.yml");
    }

    public LinkedHashMap<String,Object> getCommandDetailsMap() {
        LinkedHashMap<String, Object> linkedHashMapFinal = new LinkedHashMap<>();
        linkedHashMapFinal.put("Commands", command);
        ArrayList<String> mainArgsList = new ArrayList<>(mainArgs);
        linkedHashMapFinal.put("Main Args", mainArgsList);
        String canUseString = "";
        switch (canUse){
            case PLAYER: canUseString = "Players"; break;
            case REMOTE_PLAYER: canUseString = "Remote Player"; break;
            case BOTH: canUseString = "Players and Remote Players"; break;
        }
        linkedHashMapFinal.put("Can Use", canUse);
        linkedHashMapFinal.put("Custom Split", customSplit);
        ArrayList<String> permissionsList = new ArrayList<>(permissions);
        linkedHashMapFinal.put("Permissions", permissionsList);
        return linkedHashMapFinal;
    }

    public abstract void onCommand(PlayerSession playerSession, String command, int argsCount, String[] args);
}
