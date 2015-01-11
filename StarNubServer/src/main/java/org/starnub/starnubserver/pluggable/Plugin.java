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

import org.starnub.starnubserver.StarNub;
import org.starnub.starnubserver.StarNubTaskManager;
import org.starnub.starnubserver.events.packet.PacketEventRouter;
import org.starnub.starnubserver.events.starnub.StarNubEventRouter;
import org.starnub.starnubserver.pluggable.exceptions.MissingData;
import org.starnub.starnubserver.pluggable.exceptions.PluginDirectoryCreationFailed;
import org.starnub.starnubserver.pluggable.plugins.resources.PluginConfiguration;
import org.starnub.utilities.dircectories.DirectoryCheckCreate;
import org.starnub.utilities.file.yaml.YAMLWrapper;
import org.starnub.utilities.file.yaml.YamlUtilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

public abstract class Plugin<T> extends Pluggable<T> {

    protected PluginConfiguration configuration;
    protected HashSet<Map<String, Object>> dependencies;
    protected HashSet<String> additionalPermissions;
    private boolean enabled;

    /**
     * Creates a new <code>File</code> instance by converting the given
     * pathname string into an abstract pathname.  If the given string is
     * the empty string, then the result is the empty abstract pathname.
     *
     * @param pathname A pathname string
     * @throws NullPointerException If the <code>pathname</code> argument is <code>null</code>
     */
    public Plugin(String pathname) throws MissingData, IOException, PluginDirectoryCreationFailed {
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
    public Plugin(URI uri) throws MissingData, IOException, PluginDirectoryCreationFailed {
        super(uri);
    }

    public HashSet<Map<String, Object>> getDependencies() {
        return dependencies;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void loadData(YAMLWrapper pluggableInfo) throws MissingData, IOException, PluginDirectoryCreationFailed {
        super.loadData(pluggableInfo);
        additionalPermissions = new HashSet<>();
        List<String> additionalPermissionList = (List<String>) pluggableInfo.getValue("additional_permissions");
        additionalPermissionList.addAll(additionalPermissionList);
        dependencies = new HashSet<>();
        ArrayList<Map<String, Object>> dependantsList = (ArrayList<Map<String, Object>>) pluggableInfo.getValue("dependencies");
        dependantsList.addAll(dependantsList);
        boolean hasConfiguration = (boolean) pluggableInfo.getValue("has_configuration");
        if (hasConfiguration) {
            if(pluginType == PluggableType.JAVA) {
                try (InputStream defaultPluginConfiguration = StarNub.class.getClassLoader().getResourceAsStream("default_configuration.yml")) {
                    configuration = new PluginConfiguration(pluggableDetails.getNAME(), defaultPluginConfiguration);
                }
            } else if (pluginType == PluggableType.PYTHON){
                PythonInterpreter pythonInterpreter = PythonInterpreter.getInstance();
                pythonInterpreter.loadPythonScript(this);
                pythonInterpreter.getPyObject("default_configuration", false);
            }
        }
    }

    public void enable(){
        onPluginEnable();
        this.enabled = true;
    }

    public void disable(){
        onPluginDisable();
        String name = pluggableDetails.getNAME();
        StarNubTaskManager.getInstance().purgeByOwnerName(name);
        PacketEventRouter.getInstance().removeEventSubscription(name);
        StarNubEventRouter.getInstance().removeEventSubscription(name);
        this.enabled = false;
    }

    protected void createDirectories(String PLUGIN_DIR, String PLUGIN_NAME, ArrayList<String> directories) throws PluginDirectoryCreationFailed {
        LinkedHashMap<String, Boolean> linkedHashMap = DirectoryCheckCreate.dirCheck(PLUGIN_DIR, directories);
        for (Map.Entry<String, Boolean> dirEntry : linkedHashMap.entrySet()){
            String dir = dirEntry.getKey();
            boolean success = dirEntry.getValue();
            if (success){
                System.out.println("StarNub directory " + dir + " exist or was successfully created.");
            } else {
                throw new PluginDirectoryCreationFailed("ERROR CREATING DIRECTORY \"" + dir + "\" FOR PLUGIN \"" + PLUGIN_NAME + "\" PLEASE CHECK FILE PERMISSIONS. " +
                        "CONSULT THE PLUGIN DEVELOPER FOR FURTHER HELP.");
            }
        }
    }

    @Override
    public void dumpDetails() throws IOException {
        LinkedHashMap pluginDetailDump = new LinkedHashMap();
        LinkedHashMap<String, Object> pluggableDetailsMap = pluggableDetails.getPluginDetailsMap();
        LinkedHashMap<String, Object> pluginDetailsMap = getPluginDetailsMap();
        pluginDetailDump.put("Pluggable Details", pluggableDetailsMap);
        pluginDetailDump.put("Plugin Details", pluginDetailsMap);
        YamlUtilities.toFileYamlDump(pluginDetailDump, "StarNub/Plugins/" + pluggableDetails.getNAME() + "/Information/", "Plugin_Details.yml");
    }

    public LinkedHashMap<String, Object> getPluginDetailsMap(){
        LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("Dependencies", dependencies);
        linkedHashMap.put("Additional Permissions", additionalPermissions);
        return linkedHashMap;
    }

    public abstract void onPluginEnable();
    public abstract void onPluginDisable();
}
