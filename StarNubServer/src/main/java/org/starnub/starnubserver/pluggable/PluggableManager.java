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

package org.starnub.starnubserver.pluggable;

import org.apache.commons.io.FileUtils;
import org.starnub.starnubserver.StarNub;
import org.starnub.starnubserver.events.events.StarNubEvent;
import org.starnub.starnubserver.pluggable.exceptions.DirectoryCreationFailed;
import org.starnub.starnubserver.pluggable.exceptions.MissingData;
import org.starnub.starnubserver.pluggable.generic.LoadSuccess;
import org.starnub.starnubserver.pluggable.generic.PluggableDetails;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
* Represents the PluginManager Enum singleton
* <p>
* This enum singleton holds and runs all things important
* to Plugins and Commands
* <p>
* @author Daniel (Underbalanced) (www.StarNub.org)
* @since 1.0
*/
public class PluggableManager<T extends Pluggable> {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final PluggableManager instance = new PluggableManager();

    /**
     * This constructor is private - Singleton Pattern
     */
    private PluggableManager() {
    }

    public static PluggableManager getInstance() {
        return instance;
    }

    private final String PLUGIN_DIRECTORY_STRING = "StarNub/Plugins/";
    private final String COMMAND_DIRECTORY_STRING = "StarNub/Commands/";
    private final ConcurrentHashMap<String, Plugin> PLUGINS = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Command> COMMANDS = new ConcurrentHashMap<>();

    public String getPLUGIN_DIRECTORY_STRING() {
        return PLUGIN_DIRECTORY_STRING;
    }

    public String getCOMMAND_DIRECTORY_STRING() {
        return COMMAND_DIRECTORY_STRING;
    }

    public ConcurrentHashMap<String, Plugin> getPLUGINS() {
        return PLUGINS;
    }

    public ConcurrentHashMap<String, Command> getCOMMANDS() {
        return COMMANDS;
    }

    @SuppressWarnings("unchecked")
    public LinkedHashMap<String, UnloadedPluggable> pluginScan(boolean updating) {
        ConcurrentHashMap<String, Pluggable> pluggablePluggins = new ConcurrentHashMap<>((Map)PLUGINS);
        return pluggableScan(PLUGIN_DIRECTORY_STRING, pluggablePluggins, updating);
    }

    @SuppressWarnings("unchecked")
    public LinkedHashMap<String, UnloadedPluggable> commandScan(boolean updating) {
        ConcurrentHashMap<String, Pluggable> pluggableCommands = new ConcurrentHashMap<>((Map)COMMANDS);
        return pluggableScan(COMMAND_DIRECTORY_STRING, pluggableCommands, updating);
    }

    public LinkedHashMap<String, UnloadedPluggable> pluggableScan(String directory, ConcurrentHashMap<String, Pluggable> loadedPluggables, boolean updating){
        LinkedHashMap<String, UnloadedPluggable> orderedPluggables = new LinkedHashMap<>();
        HashMap<String, UnloadedPluggable> unloadedPluggables = getFiles(directory, "jar", "py");
        /* Remove plugins that are already loaded from this list */
        for (Pluggable pluggable : loadedPluggables.values()) {
            PluggableDetails loadedDetails = pluggable.getDetails();
            String loadedName = loadedDetails.getNAME();
            double loadedVersion = loadedDetails.getVERSION();
            UnloadedPluggable unloadedPluggable = unloadedPluggables.get(loadedName);
            if (unloadedPluggable != null) {
                PluggableDetails unloadedDetails = unloadedPluggable.getDetails();
                String unloadedName = unloadedDetails.getNAME();
                double unloadedVersion = unloadedDetails.getVERSION();
                boolean canUpdate = canUpdate(unloadedPluggable, updating, loadedName, loadedVersion, unloadedName, unloadedVersion);
                if (!canUpdate){
                    unloadedPluggables.remove(unloadedName);
                }
            }
        }
        /* Order the Unloaded Pluggables by dependency */
        /* If not dependancies load first */
        Iterator<Map.Entry<String, UnloadedPluggable>> iterator = unloadedPluggables.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String, UnloadedPluggable> unloadedPluggableEntry = iterator.next();
            String unloadedPluggableString = unloadedPluggableEntry.getKey();
            UnloadedPluggable unloadedPluggable = unloadedPluggableEntry.getValue();
            HashSet<String> dependencies = unloadedPluggable.getDetails().getDEPENDENCIES();
            if (dependencies == null || dependencies.size() == 0) {
                orderedPluggables.put(unloadedPluggableString, unloadedPluggable);
                iterator.remove();
            }
        }
        for (Map.Entry<String, UnloadedPluggable> unloadedPluggableEntry : unloadedPluggables.entrySet()) {
            String unloadedPluggableName = unloadedPluggableEntry.getKey();
            UnloadedPluggable unloadedPluggable = unloadedPluggableEntry.getValue();
            HashSet<String> dependencies = unloadedPluggable.getDetails().getDEPENDENCIES();
            boolean canLoad = canLoad(dependencies, unloadedPluggables, PLUGINS, orderedPluggables);
            if (canLoad) {
                orderedPluggables.put(unloadedPluggableName, unloadedPluggable);
            } else {
                StarNub.getLogger().cErrPrint("StarNub", "Could not load dependencies for " + unloadedPluggableName + " .");
                new StarNubEvent("Pluggable_Cannot_Load_Dependency", unloadedPluggable);
            }
        }
        return orderedPluggables;
    }

    private boolean canUpdate(UnloadedPluggable unloadedPluggable, boolean updating, String loadedName, double loadedVersion, String unloadedName, double unloadedVersion){
        if (loadedName.equals(unloadedName)) {
            if (unloadedVersion > loadedVersion && updating) {
                unloadedPluggable.setUpdating();
                return true;
            } else {
                String removeFileRecommend = unloadedPluggable.getFile().toString();
                StarNub.getLogger().cErrPrint("StarNub", "You have multiple " + loadedName + " plugins, some are older, we recommend you remove the older file named " + removeFileRecommend + ".");
                new StarNubEvent("Pluggable_Extra_File", removeFileRecommend);
                return false;
            }
        } else {
            return true;
        }
    }

    private boolean canLoad(HashSet<String> dependencies, HashMap<String, UnloadedPluggable> unloadedPluggables, ConcurrentHashMap<String, Plugin> loadedPluggables, LinkedHashMap<String, UnloadedPluggable> orderedPluggables){
        if (dependencies != null) {
            for (String dependency : dependencies) {
                if (!dependency.isEmpty()) {
                    if (!loadedPluggables.containsKey(dependency) || !orderedPluggables.containsKey(dependency)) {
                        if (unloadedPluggables.containsKey(dependency)) {
                            UnloadedPluggable unloadedPluggable = unloadedPluggables.remove(dependency);
                            String unloadedPluggableString = unloadedPluggable.getDetails().getNAME();
                            orderedPluggables.put(unloadedPluggableString, unloadedPluggable);
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private HashMap<String, UnloadedPluggable> getFiles(String directoryString, String... extensions) {
        File directoryFile = new File(directoryString);
        Collection<File> fileCollection = FileUtils.listFiles(directoryFile, extensions, false);
        ArrayList<File> files = new ArrayList<>(fileCollection);
        if (directoryString.contains("Plugin")){
            for (Plugin plugin : PLUGINS.values()){
                files.remove(plugin.getFile());
            }
        } else if (directoryString.contains("Command")) {
            for (Command command : COMMANDS.values()){
                files.remove(command.getFile());
            }
        }
        HashMap<String, UnloadedPluggable> tempPluggables = new HashMap<>();
        for (File file : files) {
            try {
                UnloadedPluggable unloadedPluggable = new UnloadedPluggable(file);
                if (unloadedPluggable.getDetails() != null){
                    String name = unloadedPluggable.getDetails().getNAME();
                    tempPluggables.put(name, unloadedPluggable);
                }
            } catch (DirectoryCreationFailed | MissingData | IOException e) {
                e.printStackTrace();
            }
        }
        return tempPluggables;
    }

    public HashMap<String, LoadSuccess> loadAllCommands() {
        LinkedHashMap<String, UnloadedPluggable> unloadedPluggableHashMap = commandScan(true);
        HashMap<String, LoadSuccess> commandSuccess = new HashMap<>();
        for (Map.Entry<String, UnloadedPluggable> entrySet : unloadedPluggableHashMap.entrySet()) {
            String unloadedCommandName = entrySet.getKey().toLowerCase();
            UnloadedPluggable unloadedPluggable = entrySet.getValue();
            LoadSuccess loadSuccess = loadCommand(unloadedCommandName, unloadedPluggable);
            commandSuccess.put(unloadedCommandName, loadSuccess);
        }
        return commandSuccess;
    }

    public HashMap<String, LoadSuccess> loadAllPlugins(boolean enable) {
        LinkedHashMap<String, UnloadedPluggable> unloadedPluggableHashMap = pluginScan(true);
        HashMap<String, LoadSuccess> pluginSuccess = new HashMap<>();
        for (Map.Entry<String, UnloadedPluggable> entrySet : unloadedPluggableHashMap.entrySet()) {
            String unloadedPluginName = entrySet.getKey();
            UnloadedPluggable unloadedPluggable = entrySet.getValue();
            LoadSuccess loadSuccess = loadPlugin(unloadedPluginName, unloadedPluggable, enable);
            pluginSuccess.put(unloadedPluginName, loadSuccess);
        }
        return pluginSuccess;
    }

    private LoadSuccess loadCommand(String unloadedPluggableName, UnloadedPluggable unloadedPluggable){
        Command pluggable;
        String type = "Command";
        try {
            pluggable = (Command) unloadedPluggable.instantiatePluggable(PluggableType.COMMAND);
        } catch (IOException e) {
            return pluggableIOError(type, e, unloadedPluggable);
        } catch (ClassNotFoundException e) {
            return classNotFoundError(type, e, unloadedPluggable);
        } catch (DirectoryCreationFailed e) {
            return directoryCreationError(type, e, unloadedPluggable);
        } catch (InstantiationException e) {
            return instantiateError(type, e, unloadedPluggable);
        } catch (MissingData e) {
            return missingDataError(type, e, unloadedPluggable);
        } catch (IllegalAccessException e) {
            return illegalAccessError(type, e, unloadedPluggable);
        }
        boolean dependenciesLoaded = dependenciesLoaded(unloadedPluggable);
        if (!dependenciesLoaded){
            return dependencyError(type, unloadedPluggable);
        }
        LoadSuccess loadSuccess;
        if (unloadedPluggable.isUpdating()) {
            COMMANDS.remove(unloadedPluggableName);
            loadSuccess = pluggableUpdated(type, pluggable);
        } else {
            loadSuccess = pluggableLoaded(type, pluggable);
        }
        pluggable.register();
        COMMANDS.put(unloadedPluggableName, pluggable);
        return loadSuccess;
    }

    private LoadSuccess loadPlugin(String unloadedPluggableName, UnloadedPluggable unloadedPluggable, boolean enable) {
        Plugin pluggable;
        String type = "Plugin";
        try {
            pluggable = (Plugin) unloadedPluggable.instantiatePluggable(PluggableType.PLUGIN);
        } catch (IOException e) {
            return pluggableIOError(type, e, unloadedPluggable);
        } catch (ClassNotFoundException e) {
            return classNotFoundError(type, e, unloadedPluggable);
        } catch (DirectoryCreationFailed e) {
            return directoryCreationError(type, e, unloadedPluggable);
        } catch (InstantiationException e) {
            return instantiateError(type, e, unloadedPluggable);
        } catch (MissingData e) {
            return missingDataError(type, e, unloadedPluggable);
        } catch (IllegalAccessException e) {
            return illegalAccessError(type, e, unloadedPluggable);
        }
        boolean dependenciesLoaded = dependenciesLoaded(unloadedPluggable);
        if (!dependenciesLoaded){
            return dependencyError(type, unloadedPluggable);
        }
        LoadSuccess loadSuccess;
        if (unloadedPluggable.isUpdating()) {
            PLUGINS.remove(unloadedPluggableName);
            loadSuccess = pluggableUpdated(type, pluggable);
        } else {
            loadSuccess = pluggableLoaded(type, pluggable);
        }
        PLUGINS.put(unloadedPluggableName, pluggable);
        if (enable) {
            pluggable.register();
            pluggable.enable();
        }
        return loadSuccess;
    }

    private boolean dependenciesLoaded(UnloadedPluggable unloadedPluggable){
        HashSet<String> dependencies = unloadedPluggable.getDetails().getDEPENDENCIES();
        if (dependencies != null) {
            for (String dependency : dependencies) {
                boolean containsKey = PLUGINS.containsKey(dependency);
                if (!containsKey) {
                    return false;
                }
            }
        }
        return true;
    }

    private LoadSuccess pluggableIOError(String type, IOException e, UnloadedPluggable unloadedPluggable) {
        String nameVersion = unloadedPluggable.getDetails().getNameVersion();
        StarNub.getLogger().cErrPrint("StarNub", "A Pluggable could not load due to an IO Error, Pluggable: " + nameVersion + ".");
        new StarNubEvent(type + "_IO_Error", unloadedPluggable);
        e.printStackTrace();
        return new LoadSuccess(false, "IO Error for Pluggable: " + nameVersion + ".");
    }

    private LoadSuccess classNotFoundError(String type, ClassNotFoundException e, UnloadedPluggable unloadedPluggable) {
        String nameVersion = unloadedPluggable.getDetails().getNameVersion();
        StarNub.getLogger().cErrPrint("StarNub", "A Pluggable could not load due to an IO Error, Pluggable: " + nameVersion + ".");
        new StarNubEvent(type + "_Class_Not_Found", unloadedPluggable);
        e.printStackTrace();
        return new LoadSuccess(false, "Class not found for Pluggable: " + nameVersion + ".");
    }

    private LoadSuccess directoryCreationError(String type, DirectoryCreationFailed e, UnloadedPluggable unloadedPluggable) {
        String nameVersion = unloadedPluggable.getDetails().getNameVersion();
        StarNub.getLogger().cErrPrint("StarNub", "A Pluggable directory could not be created, Pluggable: " + nameVersion + ".");
        new StarNubEvent(type + "_Directory_Creation_Failed", unloadedPluggable);
        e.printStackTrace();
        return new LoadSuccess(false, "Could not create directory for Pluggable: " + nameVersion + ".");
    }

    private LoadSuccess instantiateError(String type, InstantiationException e, UnloadedPluggable unloadedPluggable) {
        String nameVersion = unloadedPluggable.getDetails().getNameVersion();
        StarNub.getLogger().cErrPrint("StarNub", "Could not instantiate, Pluggable: " + nameVersion + ".");
        new StarNubEvent(type + "_Instantiation_Error", unloadedPluggable);
        e.printStackTrace();
        return new LoadSuccess(false, "Could not instantiate Pluggable: " + nameVersion + ".");
    }

    private LoadSuccess missingDataError(String type, MissingData e, UnloadedPluggable unloadedPluggable) {
        String nameVersion = unloadedPluggable.getDetails().getNameVersion();
        StarNub.getLogger().cErrPrint("StarNub", "Could not find all of the manifest data, Pluggable: " + nameVersion + ".");
        new StarNubEvent(type + "_Manifest_Missing_Data", unloadedPluggable);
        e.printStackTrace();
        return new LoadSuccess(false, "Missing manifest data, Pluggable: " + nameVersion + ".");
    }

    private LoadSuccess illegalAccessError(String type, IllegalAccessException e, UnloadedPluggable unloadedPluggable) {
        String nameVersion = unloadedPluggable.getDetails().getNameVersion();
        StarNub.getLogger().cErrPrint("StarNub", "Illegal access exception for, Pluggable: " + nameVersion + ".");
        new StarNubEvent(type + "_Class_Access_Error", unloadedPluggable);
        e.printStackTrace();
        return new LoadSuccess(false, "Illegal access exception, Pluggable: " + nameVersion + ".");
    }

    private LoadSuccess dependencyError(String type, UnloadedPluggable unloadedPluggable) {
        String nameVersion = unloadedPluggable.getDetails().getNameVersion();
        StarNub.getLogger().cErrPrint("StarNub", "Missing dependancies for, Pluggable: " + nameVersion + ".");
        new StarNubEvent(type + "_Missing_Dependancies", unloadedPluggable);
        return new LoadSuccess(false, "Missing dependancies, Pluggable: " + nameVersion + ".");
    }

    private LoadSuccess pluggableLoaded(String type, Pluggable pluggable){
        String nameVersion = pluggable.getDetails().getNameVersion();
        String successString = nameVersion + " was successfully loaded as a " + type;
        StarNub.getLogger().cInfoPrint("StarNub", successString);
        new StarNubEvent(type + "_Loaded", pluggable);
        return new LoadSuccess(true, successString);
    }

    private LoadSuccess pluggableUpdated(String type, Pluggable pluggable){
        String nameVersion = pluggable.getDetails().getNameVersion();
        String successString = type + " " + nameVersion + " updated.";
        StarNub.getLogger().cInfoPrint("StarNub", successString);
        new StarNubEvent(type + "_Updated", pluggable);
        return new LoadSuccess(true, successString);
    }

    public void unloadAllCommands(){
        COMMANDS.values().forEach(Pluggable::unregister);
        COMMANDS.clear();
    }

    public void unloadAllPlugins(){
        PLUGINS.values().forEach(Pluggable::unregister);
        PLUGINS.clear();
    }
}