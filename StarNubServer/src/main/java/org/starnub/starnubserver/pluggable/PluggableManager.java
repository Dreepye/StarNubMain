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

    public LinkedHashMap<String, UnloadedPluggable> pluginScan(boolean updating) {
        LinkedHashMap<String, UnloadedPluggable> orderUnloadedPluggoles = new LinkedHashMap<>();
        HashMap<String, UnloadedPluggable> unloadedPluggables = getFiles(PLUGIN_DIRECTORY_STRING, "jar", "py");
        /* Remove plugins that are already loaded */
         for (Plugin plugin : PLUGINS.values()) {
            PluggableDetails details = plugin.getDetails();
            String loadedName = details.getOWNER();
            double loadedVersion = details.getVERSION();
            UnloadedPluggable unloadedPluggable = unloadedPluggables.get(loadedName);
            if (unloadedPluggable != null) {
                PluggableDetails unloadedDetails = unloadedPluggable.getDetails();
                String unloadedName = unloadedDetails.getOWNER();
                double unloadedVersion = unloadedDetails.getVERSION();
                /* Is loaded */
                if (loadedName.equals(unloadedName)) {
                    if (unloadedVersion > loadedVersion && updating) {
                        unloadedPluggable.setUpdating();
                    } else {
                        String removeFileRecommend = unloadedPluggable.getFile().toString();
                        unloadedPluggables.remove(loadedName);
                        StarNub.getLogger().cErrPrint("StarNub", "You have multiple " + loadedName + " plugins, some are older, we recommend you remove the older file named " + removeFileRecommend + ".");
                    }
                }
            }
        }
        /* Order the UnloadedPluggables */
        while (unloadedPluggables.size() > 0) {
            Iterator<Map.Entry<String, UnloadedPluggable>> iterator = unloadedPluggables.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, UnloadedPluggable> unloadedPluggableEntry = iterator.next();
                String key = unloadedPluggableEntry.getKey();
                UnloadedPluggable value = unloadedPluggableEntry.getValue();
                HashSet<String> dependencies = value.getDetails().getDEPENDENCIES();
                boolean canLoad = true;
                if (dependencies != null) {
                    for (String dependency : dependencies) {
                        if (!dependency.isEmpty()) {
                            if (!unloadedPluggables.containsKey(dependency) && !PLUGINS.containsKey(dependency) || !orderUnloadedPluggoles.containsKey(dependency)) {
                                iterator.remove();
                                canLoad = false;
                            }
                        }
                    }
                }
                if (canLoad) {
                    orderUnloadedPluggoles.put(key, value);
                    iterator.remove();
                }
            }
        }
        return orderUnloadedPluggoles;
    }


    public HashMap<String, UnloadedPluggable> commandScan(boolean updating) {
        HashMap<String, UnloadedPluggable> unloadedPluggables = getFiles(COMMAND_DIRECTORY_STRING, "jar", "py");
        for (Command command : COMMANDS.values()){
            PluggableDetails details = command.getDetails();
            String loadedName = details.getOWNER();
            double loadedVersion = details.getVERSION();
            String loadedcommandName = command.getCommand();
            UnloadedPluggable unloadedPluggable = unloadedPluggables.get(loadedName);
            if (unloadedPluggable != null) {
                PluggableDetails unloadedDetails = unloadedPluggable.getDetails();
                String unloadedName = unloadedDetails.getOWNER();
                double unloadedVersion = unloadedDetails.getVERSION();
                String unloadedCommandName = (String) unloadedPluggable.getYamlWrapper().getValue("command");
                //INSERT - STARNUB EVENT UPDATE
                /* Is loaded */
                if (unloadedCommandName.equals(unloadedCommandName)) {
                    if (unloadedVersion > loadedVersion && updating) {
                        unloadedPluggable.setUpdating();
                        //INSERT - STARNUB EVENT UPDATE
                    } else {
                        String removeFileRecommend = unloadedPluggable.getFile().toString();
                        unloadedPluggables.remove(loadedName);
                        StarNub.getLogger().cErrPrint("StarNub", "This command " + loadedName + " is already loaded, File: " + removeFileRecommend + ".");
                        //INSERT - STARNUB EVENT UPDATE
                    }
                }
            }
        }
        return unloadedPluggables;
    }

    public HashMap<String, UnloadedPluggable> pluggableScan(String directory, ConcurrentHashMap<String, Pluggable> loadedPluggables, boolean updating){
        LinkedHashMap<String, UnloadedPluggable> orderedPluggables = new LinkedHashMap<>();
        HashMap<String, UnloadedPluggable> unloadedPluggables = getFiles(directory, "jar", "py");
        /* Remove plugins that are already loaded from this list */
        for (Pluggable pluggable : loadedPluggables.values()) {
            PluggableDetails loadedDetails = pluggable.getDetails();
            String loadedName = loadedDetails.getNAME();
            double loadedVersion = loadedDetails.getVERSION();
            boolean isCommand = pluggable instanceof Command;
            if(isCommand){
                Command command = (Command) pluggable;
                loadedName = command.getCommand();
            }
            UnloadedPluggable unloadedPluggable = unloadedPluggables.get(loadedName);
            if (unloadedPluggable != null) {
                PluggableDetails unloadedDetails = unloadedPluggable.getDetails();
                String unloadedName = unloadedDetails.getNAME();
                double unloadedVersion = unloadedDetails.getVERSION();
                if(isCommand){
                    unloadedName = (String) unloadedPluggable.getYamlWrapper().getValue("command");
                }
                boolean canUpdate = canUpdate(unloadedPluggable, updating, loadedName, loadedVersion, unloadedName, unloadedVersion);
                if (!canUpdate){
                    unloadedPluggables.remove(unloadedName);
                }
            }
        }
        /* Order the Unloaded Pluggables by dependency */


        //Load all non dependancy plugins first




        while (unloadedPluggables.size() > 0) {
            Iterator<Map.Entry<String, UnloadedPluggable>> iterator = unloadedPluggables.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, UnloadedPluggable> unloadedPluggableEntry = iterator.next();
                String unloadedPluggableName = unloadedPluggableEntry.getKey();
                UnloadedPluggable unloadedPluggable = unloadedPluggableEntry.getValue();
                HashSet<String> dependencies = unloadedPluggable.getDetails().getDEPENDENCIES();
                boolean canLoad = canLoad(dependencies, unloadedPluggables, loadedPluggables, orderedPluggables);
                if (canLoad) {
                    orderedPluggables.put(unloadedPluggableName, unloadedPluggable);
                } else {
                    StarNub.getLogger().cErrPrint("StarNub", "Could not load dependencies for " + unloadedPluggableName + " .");
                    new StarNubEvent("Pluggable_Cannot_Load_Dependency", unloadedPluggable);
                }

            }
        }
        return unloadedPluggables;
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

    private boolean canLoad(HashSet<String> dependencies, HashMap<String, UnloadedPluggable> unloadedPluggables, ConcurrentHashMap<String, Pluggable> loadedPluggables, LinkedHashMap<String, UnloadedPluggable> orderedPluggables){
        if (dependencies != null) {
            for (String dependency : dependencies) {
                if (!dependency.isEmpty()) {
                    if (!unloadedPluggables.containsKey(dependency) && !loadedPluggables.containsKey(dependency) || !orderedPluggables.containsKey(dependency)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private HashMap<String, UnloadedPluggable> getFiles(String directoryString, String... extensions) {
        File directoryFile = new File(directoryString);
        File[] files = FileUtils.convertFileCollectionToFileArray(FileUtils.listFiles(directoryFile, extensions, false));
        HashMap<String, UnloadedPluggable> tempPluggables = new HashMap<>();
        for (File file : files) {
            try {
                UnloadedPluggable unloadedPluggable = new UnloadedPluggable(file);
                if (unloadedPluggable.getDetails() != null){
                    String name = unloadedPluggable.getDetails().getOWNER();
                    tempPluggables.put(name, unloadedPluggable);
                }
            } catch (DirectoryCreationFailed | MissingData | IOException directoryCreationFailed) {
                directoryCreationFailed.printStackTrace();
            }
        }
        return tempPluggables;
    }

    public HashMap<String, LoadSuccess> loadAllCommands() {
        HashMap<String, UnloadedPluggable> unloadedPluggableHashMap = commandScan(true);
        HashMap<String, LoadSuccess> commandSuccess = new HashMap<>();
        for (Map.Entry<String, UnloadedPluggable> entrySet : unloadedPluggableHashMap.entrySet()) {
            String unloadedCommandName = entrySet.getKey();
            UnloadedPluggable unloadedPluggable = entrySet.getValue();
            LoadSuccess loadSuccess = loadCommand(unloadedCommandName, unloadedPluggable);
            commandSuccess.put(unloadedCommandName, loadSuccess);
        }
        return commandSuccess;
    }

    public HashMap<String, LoadSuccess> loadAllPlugins(boolean enable) {
        HashMap<String, UnloadedPluggable> unloadedPluggableHashMap = pluginScan(true);
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
        /* Currently no dependancies for commands */
//        boolean dependenciesLoaded = dependenciesLoaded(unloadedPluggable);
//        if (!dependenciesLoaded){
//            return dependencyError(type, unloadedPluggable);
//        }
        LoadSuccess loadSuccess;
        if (unloadedPluggable.isUpdating()) {
            COMMANDS.remove(unloadedPluggableName);
            loadSuccess = pluggableUpdated(type, pluggable);
        } else {
            loadSuccess = pluggableLoaded(type, pluggable);
        }
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
        String successString = type + " " + nameVersion + " loaded.";
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