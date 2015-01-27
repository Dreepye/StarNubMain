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
import org.starnub.starnubserver.pluggable.generic.PluggableDetails;
import org.starnub.starnubserver.pluggable.generic.PluggableLoadSuccess;
import org.starnub.utilities.classloaders.CustomURLClassLoader;
import org.starnub.utilities.collections.list.ReturnableList;
import org.starnub.utilities.exceptions.DependencyError;
import org.starnub.utilities.exceptions.DirectoryCreationFailed;
import org.starnub.utilities.exceptions.MissingData;
import org.starnub.utilities.strings.StringUtilities;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
* Represents the PluginManager Enum singleton
* <p>
* This enum singleton holds and runs all things important
* to Plugins and Commands
* <p>
* @author Daniel (Underbalanced) (www.StarNub.org)
* @since 1.0
*/
public class PluggableManager {

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
    private final CustomURLClassLoader<StarNub> PLUGGABLE_CLASS_LOADER = new CustomURLClassLoader<>(StarNub.class);

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

    public CustomURLClassLoader getPLUGGABLE_CLASS_LOADER() {
        return PLUGGABLE_CLASS_LOADER;
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
        HashMap<String, UnloadedPluggable> unloadedPluggables = getFiles(directory, "jar", "py");
        LinkedHashMap<String, UnloadedPluggable> orderedPluggables = new LinkedHashMap<>();
        /* Remove plugins that are already loaded from this list */
        for (Pluggable pluggable : loadedPluggables.values()) {
            PluggableDetails loadedDetails = pluggable.getDetails();
            String loadedName = loadedDetails.getNAME();
            double loadedVersion = loadedDetails.getVERSION();
            UnloadedPluggable unloadedPluggable = unloadedPluggables.get(loadedName);
            if (unloadedPluggable != null) {
                PluggableDetails unloadedDetails = unloadedPluggable.getDetails();
                double unloadedVersion = unloadedDetails.getVERSION();
                canUpdate(unloadedPluggable, updating, loadedVersion, unloadedVersion);
            }
        }
        /* Non dependency ordered first */
        Iterator<Map.Entry<String, UnloadedPluggable>> iterator = unloadedPluggables.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String, UnloadedPluggable> unloadedPluggableEntry = iterator.next();
            String unloadedPluggableString = unloadedPluggableEntry.getKey();
            UnloadedPluggable unloadedPluggable = unloadedPluggableEntry.getValue();
            String[] dependencies = unloadedPluggable.getDetails().getDEPENDENCIES();
            if(dependencies == null || dependencies.length == 0 || dependencies[0].isEmpty()){
                orderedPluggables.put(unloadedPluggableString, unloadedPluggable);
                iterator.remove();
            }
        }
        /* Load the rest */
        for(Map.Entry<String, UnloadedPluggable> unloadedPluggableEntry : unloadedPluggables.entrySet()) {
            String unloadedPluggableString = unloadedPluggableEntry.getKey();
            UnloadedPluggable unloadedPluggable = unloadedPluggableEntry.getValue();
            dependenciesOrganizer(unloadedPluggableString, unloadedPluggable, orderedPluggables, unloadedPluggables);
        }
        return orderedPluggables;
    }

    private void dependenciesOrganizer(String unloadedPluggableString, UnloadedPluggable unloadedPluggable, LinkedHashMap<String, UnloadedPluggable> orderedPluggables, HashMap<String, UnloadedPluggable> unloadedPluggables) {
        String[] dependencies = unloadedPluggable.getDetails().getDEPENDENCIES();
        if (!orderedPluggables.containsKey(unloadedPluggableString)){
            for (String dependency : dependencies) {
                if (!orderedPluggables.containsKey(dependency)) {
                    UnloadedPluggable unloadedPluggableDep = unloadedPluggables.get(dependency);
                    if (unloadedPluggableDep != null) {
                        String unloadedPluggableDepString = unloadedPluggableDep.getDetails().getNAME();
                        dependenciesOrganizer(unloadedPluggableDepString, unloadedPluggableDep, orderedPluggables, unloadedPluggables);
                    }
                }
            }
            orderedPluggables.put(unloadedPluggableString, unloadedPluggable);
        }
    }

    private boolean canUpdate(UnloadedPluggable unloadedPluggable, boolean updating, double loadedVersion, double unloadedVersion){
        if (unloadedVersion > loadedVersion && updating) {
            unloadedPluggable.setUpdating();
            return true;
        }
        return false;
    }

    private HashMap<String, UnloadedPluggable> getFiles(String directoryString, String... extensions) {
        File directoryFile = new File(directoryString);
        Collection<File> fileCollection = FileUtils.listFiles(directoryFile, extensions, false);
        ArrayList<File> files = new ArrayList<>(fileCollection);
        HashMap<String, UnloadedPluggable> tempPluggables = new HashMap<>();
        for (File file : files) {
            try {
                UnloadedPluggable unloadedPluggable = new UnloadedPluggable(file);
                if (unloadedPluggable.getDetails() != null){
                    String name = unloadedPluggable.getDetails().getNAME();
                    double version = unloadedPluggable.getDetails().getVERSION();
                    UnloadedPluggable unloadedPluggableTest = tempPluggables.get(name);
                    if (unloadedPluggableTest == null){
                        tempPluggables.put(name, unloadedPluggable);
                    } else {
                        double versionTest = unloadedPluggableTest.getDetails().getVERSION();
                        if (version > versionTest){
                            tempPluggables.replace(name, unloadedPluggable);
                        }
                    }
                }
            } catch (DirectoryCreationFailed | MissingData | IOException e) {
                e.printStackTrace();
            }
        }
        for(UnloadedPluggable unloadedPluggable : tempPluggables.values()){
            new StarNubEvent("Unloaded_Pluggable_File_Loaded", unloadedPluggable);
        }
        return tempPluggables;
    }

    private PluggableLoadSuccess loadPluggable(String unloadedPluggableName, UnloadedPluggable unloadedPluggable, boolean enable) {
        Pluggable pluggable;
        PluggableType pluggableType = unloadedPluggable.getDetails().getTYPE();
        String type = unloadedPluggable.getDetails().getTypeString();
        try {
            if (pluggableType == PluggableType.PLUGIN) {
                pluggable = unloadedPluggable.instantiatePluggable(PluggableType.PLUGIN);
            } else {
                pluggable = unloadedPluggable.instantiatePluggable(PluggableType.COMMAND);
            }
        } catch (ClassCastException e){
            return classCastFailure(type, e, unloadedPluggable);
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
        PluggableLoadSuccess pluggableLoadSuccess;
        String lowerCaseName = unloadedPluggableName.toLowerCase();
        if (enable) {
            try {
                pluggable.enable();
            } catch (Exception e){
                e.printStackTrace();
                return pluggableEnableError(type, unloadedPluggable);
            }
        }
        boolean containsKey;
        if (pluggableType == PluggableType.PLUGIN){
            containsKey = PLUGINS.containsKey(lowerCaseName);
        } else {
            containsKey = COMMANDS.containsKey(lowerCaseName);
        }

        if (!unloadedPluggable.isUpdating() && containsKey) {
            return failNewerVersion(type, unloadedPluggable);
        } else if (unloadedPluggable.isUpdating()) {

            Pluggable remove;
            if (pluggableType == PluggableType.PLUGIN){
                remove = PLUGINS.remove(lowerCaseName);
            } else {
                remove = COMMANDS.remove(lowerCaseName);
            }
            remove.disable();
            pluggableLoadSuccess = pluggableUpdated(type, pluggable);
        } else {
            pluggableLoadSuccess = pluggableLoaded(type, pluggable);
        }
        if (pluggableType == PluggableType.PLUGIN){
            PLUGINS.put(lowerCaseName, (Plugin) pluggable);
        } else {
             COMMANDS.put(lowerCaseName, (Command) pluggable);
        }
        return pluggableLoadSuccess;
    }



    private PluggableLoadSuccess classCastFailure(String type, ClassCastException e, UnloadedPluggable unloadedPluggable) {
        return failureMethod(e, type, unloadedPluggable, "_Class_Cast_Exception", "Class Cast Failure " + type);
    }

    private PluggableLoadSuccess pluggableIOError(String type, IOException e, UnloadedPluggable unloadedPluggable) {
        return failureMethod(e, type, unloadedPluggable, "_IO_Error", "IO Error");
    }

    private PluggableLoadSuccess classNotFoundError(String type, ClassNotFoundException e, UnloadedPluggable unloadedPluggable) {
        return failureMethod(e, type, unloadedPluggable, "_Class_Not_Found", "Class Not Found");
    }

    private PluggableLoadSuccess directoryCreationError(String type, DirectoryCreationFailed e, UnloadedPluggable unloadedPluggable) {
        return failureMethod(e, type, unloadedPluggable, "_Directory_Creation_Failure", "Directory Creation Error");
    }

    private PluggableLoadSuccess instantiateError(String type, InstantiationException e, UnloadedPluggable unloadedPluggable) {
        return failureMethod(e, type, unloadedPluggable, "_Instantiation_Failure", "Instantiation Failure");
    }

    private PluggableLoadSuccess missingDataError(String type, MissingData e, UnloadedPluggable unloadedPluggable) {
        return failureMethod(e, type, unloadedPluggable, "_Manifest_Missing_Data", e.getMessage());
    }

    private PluggableLoadSuccess illegalAccessError(String type, IllegalAccessException e, UnloadedPluggable unloadedPluggable) {
        return failureMethod(e, type, unloadedPluggable, "_Class_Access_Error", "Class Access Error");
    }

    private PluggableLoadSuccess dependencyError(String type, DependencyError e, UnloadedPluggable unloadedPluggable) {
        return failureMethod(null, type, unloadedPluggable, "_Missing_Dependancies", e.getMessage());
    }

    private PluggableLoadSuccess pluggableEnableError(String type, UnloadedPluggable unloadedPluggable){
        return failureMethod(null, type, unloadedPluggable, "_Enable_Method_Exception", "Enable Method Exception");
    }

    private PluggableLoadSuccess failNewerVersion(String type, UnloadedPluggable unloadedPluggable){
        return failureMethod(null, type, unloadedPluggable, "_Newer_Version_Loaded", "Newer Version Already Loaded");
    }

    private PluggableLoadSuccess failureMethod(Exception e, String type, UnloadedPluggable unloadedPluggable,  String event, String error){
        if(e != null){
            e.printStackTrace();
        }
        String nameVersion = unloadedPluggable.getDetails().getNameVersion();
        String failedString = nameVersion + " could not successfully load. Reason: " + error + ".";
        StarNub.getLogger().cErrPrint("StarNub", failedString);
        new StarNubEvent(type + event, unloadedPluggable);
        return new PluggableLoadSuccess(unloadedPluggable, false, failedString);
    }

    private PluggableLoadSuccess pluggableLoaded(String type, Pluggable pluggable){
        String nameVersion = pluggable.getDetails().getNameVersion();
        String success = nameVersion + " was successfully loaded as a " + type;
        return successMethod(type, pluggable, success, "_Loaded");
    }

    private PluggableLoadSuccess pluggableUpdated(String type, Pluggable pluggable){
        String nameVersion = pluggable.getDetails().getNameVersion();
        String success = type + " " + nameVersion + " updated.";
        return successMethod(type, pluggable, success, "_Updated");
    }

    private PluggableLoadSuccess successMethod(String type, Pluggable pluggable, String success, String event) {
        StarNub.getLogger().cInfoPrint("StarNub", success);
        new StarNubEvent(type + event, pluggable);
        return new PluggableLoadSuccess(pluggable, true, success);
    }

    public PluggableLoadSuccess loadSpecificPlugin(String pluginName, boolean enable, boolean updating){
        LinkedHashMap<String, UnloadedPluggable> pluginScan = pluginScan(updating);
        for (Map.Entry<String, UnloadedPluggable> entrySet : pluginScan.entrySet()){
            String name = entrySet.getKey();
            if(name.equalsIgnoreCase(pluginName)) {
                UnloadedPluggable unloadedPluggable = entrySet.getValue();
                return loadPluggable(name, unloadedPluggable, enable);
            }
        }
        return null;
    }

    public PluggableLoadSuccess loadSpecificCommand(String commandName, boolean enable, boolean updating){
        LinkedHashMap<String, UnloadedPluggable> commandScan = commandScan(updating);
        for (Map.Entry<String, UnloadedPluggable> entrySet : commandScan.entrySet()){
            String name = entrySet.getKey();
            if(name.equalsIgnoreCase(commandName)) {
                UnloadedPluggable unloadedPluggable = entrySet.getValue();
                return loadPluggable(name, unloadedPluggable, enable);
            }
        }
        return null;
    }

    public ReturnableList<Pluggable> disableUnloadSpecificPlugin(String pluginName, boolean unload){
        Plugin plugin = getSpecificLoadedPlugin(pluginName);
        return disableUnloadPluggable(plugin, unload);
    }

    public ReturnableList<Pluggable> disableUnloadSpecificCommand(String commandName, boolean unload){
        Command command = getSpecificLoadedCommand(commandName);
        return disableUnloadPluggable(command, unload);
    }

    public HashSet<PluggableLoadSuccess> loadAllCommands(boolean enable) {
        LinkedHashMap<String, UnloadedPluggable> unloadedPluggableHashMap = commandScan(true);
        HashSet<PluggableLoadSuccess> commandSuccess = new HashSet<>();
        for (Map.Entry<String, UnloadedPluggable> entrySet : unloadedPluggableHashMap.entrySet()) {
            String unloadedCommandName = entrySet.getKey().toLowerCase();
            UnloadedPluggable unloadedPluggable = entrySet.getValue();
            PluggableLoadSuccess pluggableLoadSuccess = loadPluggable(unloadedCommandName, unloadedPluggable, true);
            commandSuccess.add(pluggableLoadSuccess);
        }
        return commandSuccess;
    }

    public HashSet<PluggableLoadSuccess> loadAllPlugins(boolean enable) {
        LinkedHashMap<String, UnloadedPluggable> unloadedPluggableHashMap = pluginScan(true);
        HashSet<PluggableLoadSuccess> pluginSuccess = new HashSet<>();
        for (Map.Entry<String, UnloadedPluggable> entrySet : unloadedPluggableHashMap.entrySet()) {
            String unloadedPluginName = entrySet.getKey();
            UnloadedPluggable unloadedPluggable = entrySet.getValue();
            PluggableLoadSuccess pluggableLoadSuccess = loadPluggable(unloadedPluginName, unloadedPluggable, enable);
            pluginSuccess.add(pluggableLoadSuccess);
        }
        return pluginSuccess;
    }

    public void enableAllPlugins(){
        HashSet<PluggableLoadSuccess> enableSuccess = new HashSet<>();
        PLUGINS.values().stream().forEach(p -> {
                if(!p.isEnabled()){
                    try {
                        p.enable();
                    } catch (DependencyError de) {
                        de.printStackTrace();
                    }
                }
        });
    }

    public void enableAllCommands(){
        COMMANDS.values().stream().forEach( c-> {

        });
    }

    public void disableAllPlugins(){
        PLUGINS.values().forEach(p -> disableUnloadPluggable(p, false));
    }

    public void disableAllCommands(){
        COMMANDS.values().forEach(p -> disableUnloadPluggable(p, false));
    }

    public void unloadAllCommands(){
        COMMANDS.values().forEach(p -> disableUnloadPluggable(p, true));
    }

    public void unloadAllPlugins(){
        PLUGINS.values().forEach(p -> disableUnloadPluggable(p, true));
    }

    public ReturnableList<Pluggable> disableUnloadPluggable(Pluggable p, boolean unload){
        ReturnableList<Pluggable> disabledUnloadedPluggables = new ReturnableList<>();
        disabledUnloadedPluggables.add(p);
        String lowerCaseName= p.getDetails().getNAME().toLowerCase();
        if(p.getDetails().isUNLOADABLE()){
            for (Plugin plugin : PLUGINS.values()){
                PluggableDetails details = plugin.getDetails();
                String pluginName = details.getNAME();
                if(details.hasDependancy(lowerCaseName)){
                    ReturnableList<Pluggable> pluggables = disableUnloadSpecificPlugin(pluginName, false);
                    disabledUnloadedPluggables.addAll(pluggables.stream().collect(Collectors.toList()));
                }
            }
            for (Command command : COMMANDS.values()){
                PluggableDetails details = command.getDetails();
                String pluginName = details.getNAME();
                if(details.hasDependancy(lowerCaseName)){
                    ReturnableList<Pluggable> pluggables = disableUnloadSpecificCommand(pluginName, false);
                    disabledUnloadedPluggables.addAll(pluggables.stream().collect(Collectors.toList()));
                }
            }
            if(disabledUnloadedPluggables.size() == 0){
                disabledUnloadedPluggables.setExactMatch();
            }
            p.unregister();
            p.disable();
            if(unload) {
                if(p.getDetails().getTYPE() == PluggableType.PLUGIN) {
                    PLUGINS.remove(lowerCaseName);
                } else {
                    COMMANDS.remove(lowerCaseName);
                }
            }
        }
        return disabledUnloadedPluggables;
    }

    public Plugin getSpecificLoadedPlugin(String pluginName){
        return PLUGINS.get(pluginName.toLowerCase());
    }

    public Command getSpecificLoadedCommand(String pluginName){
        return COMMANDS.get(pluginName.toLowerCase());
    }

    public ReturnableList<Plugin> getSpecificLoadedPluginOrNearMatchs(String pluginName){
        Plugin specificLoadedPlugin = getSpecificLoadedPlugin(pluginName);
        ReturnableList<Plugin> similarPlugins = new ReturnableList<>();
        if (specificLoadedPlugin == null){
            for(Map.Entry<String, Plugin> entrySet : PLUGINS.entrySet()){
                String name = entrySet.getKey();
                Plugin plugin = entrySet.getValue();
                double distance = StringUtilities.similarityCalculationCaseInsensitive(name, pluginName);
                if (distance > 90){
                    similarPlugins.add(plugin);
                }
            }
        } else {
            similarPlugins.setExactMatch();
            similarPlugins.add(specificLoadedPlugin);
        }
        return similarPlugins;
    }

    public ReturnableList<Command> getSpecificLoadedCommandOrNearMatchs(String commandName){
        Command specificLoadedCommand = getSpecificLoadedCommand(commandName);
        ReturnableList<Command> similarCommand = new ReturnableList<>();
        if (specificLoadedCommand == null){
            for(Map.Entry<String, Command> entrySet : COMMANDS.entrySet()){
                String name = entrySet.getKey();
                Command command = entrySet.getValue();
                double distance = StringUtilities.similarityCalculationCaseInsensitive(name, commandName);
                if (distance > 90){
                    similarCommand.add(command);
                }
            }
        } else {
            similarCommand.setExactMatch();
            similarCommand.add(specificLoadedCommand);
        }
        return similarCommand;
    }
}