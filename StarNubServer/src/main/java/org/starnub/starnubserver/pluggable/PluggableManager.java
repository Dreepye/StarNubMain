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
import org.starnub.starnubserver.pluggable.generic.IPluggable;
import org.starnub.starnubserver.pluggable.generic.PluggableDetails;
import org.starnub.starnubserver.pluggable.generic.PluggableReturn;
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
    public NavigableSet<UnloadedPluggable> pluginScan(boolean updating) {
        ConcurrentHashMap<String, Pluggable> pluggablePluggins = new ConcurrentHashMap<>((Map)PLUGINS);
        return pluggableScan(PLUGIN_DIRECTORY_STRING, pluggablePluggins, updating);
    }

    @SuppressWarnings("unchecked")
    public NavigableSet<UnloadedPluggable> commandScan(boolean updating) {
        ConcurrentHashMap<String, Pluggable> pluggableCommands = new ConcurrentHashMap<>((Map)COMMANDS);
        return pluggableScan(COMMAND_DIRECTORY_STRING, pluggableCommands, updating);
    }

    public NavigableSet<UnloadedPluggable> pluggableScan(String directory, ConcurrentHashMap<String, Pluggable> loadedPluggables, boolean updating){
        TreeSet<UnloadedPluggable> unloadedPluggables = getFiles(directory, "jar", "py");
        /* Remove unloaded pluggables that are older then currently loaded*/
        Iterator<UnloadedPluggable> iterator = unloadedPluggables.iterator();
        while (iterator.hasNext()) {
            UnloadedPluggable up = iterator.next();
            String upName = up.getDetails().getNAME().toLowerCase();
            Pluggable p = loadedPluggables.get(upName);
            if (p != null){
                boolean canUpdate = canUpdate(up, p, updating);
                if (!canUpdate){
                    iterator.remove();
                }
            }
        }
        return unloadedPluggables.descendingSet();
    }

    private TreeSet<UnloadedPluggable> getFiles(String directoryString, String... extensions) {
        File directoryFile = new File(directoryString);
        Collection<File> fileCollection = FileUtils.listFiles(directoryFile, extensions, false);
        ArrayList<File> files = new ArrayList<>(fileCollection);
        TreeSet<UnloadedPluggable> unloadedPluggables = new TreeSet<>();
        for (File file : files) {
            try {
                UnloadedPluggable futureUnloadedPluggable = new UnloadedPluggable(file);
                if (futureUnloadedPluggable.getDetails() != null) {
                    String futureUnloadedPluggableName = futureUnloadedPluggable.getDetails().getNAME();
                    double futureUnloadedPluggableVersion = futureUnloadedPluggable.getDetails().getVERSION();
                    Iterator<UnloadedPluggable> iterator = unloadedPluggables.iterator();
                    boolean removed = false;
                    boolean exist = false;
                    while (iterator.hasNext()) {
                        UnloadedPluggable alreadyUnloadedPluggable = iterator.next();
                        String alreadyUnloadedPluggableName = alreadyUnloadedPluggable.getDetails().getNAME();
                        if (futureUnloadedPluggableName.equalsIgnoreCase(alreadyUnloadedPluggableName)) {
                            exist = true;
                            double versionTest = alreadyUnloadedPluggable.getDetails().getVERSION();
                            if (futureUnloadedPluggableVersion > versionTest) {
                                iterator.remove();
                                removed = true;
                            }
                        }
                    }
                    if (removed || !exist) {
                        unloadedPluggables.add(futureUnloadedPluggable);
                    }
                }
            } catch (DirectoryCreationFailed | MissingData | IOException e) {
                e.printStackTrace();
            }
        }
        for(UnloadedPluggable unloadedPluggable : unloadedPluggables){
            new StarNubEvent("Unloaded_Pluggable_File_Loaded", unloadedPluggable);
        }
        return unloadedPluggables;
    }

    private boolean canUpdate(UnloadedPluggable unloadedPluggable, Pluggable pluggable, boolean updating) {
        if(!updating){
            return false;
        } else {
            double upVersion = unloadedPluggable.getDetails().getVERSION();
            double pVersion = pluggable.getDetails().getVERSION();
            if (upVersion > pVersion) {
                unloadedPluggable.setUpdating();
                return true;
            } else {
                return false;
            }
        }
    }

    public ReturnableList<PluggableReturn> loadAllCommands(boolean enable) {
        NavigableSet<UnloadedPluggable> upSet = commandScan(true);
        ReturnableList<PluggableReturn> cSuccess = new ReturnableList<>();
        upSet.stream().forEach(up -> {
            String upName = up.getDetails().getNAME();
            PluggableReturn pls = loadPluggable(upName, up, enable);
            cSuccess.add(pls);
        });
        return cSuccess;
    }

    public ReturnableList<PluggableReturn> loadAllPlugins(boolean enable) {
        NavigableSet<UnloadedPluggable> upSet = pluginScan(true);
        ReturnableList<PluggableReturn> pSuccess = new ReturnableList<>();
        upSet.stream().forEach(up -> {
            String upName = up.getDetails().getNAME();
            PluggableReturn pls = loadPluggable(upName, up, enable);
            pSuccess.add(pls);
        });
        return pSuccess;
    }

    public PluggableReturn loadSpecificPlugin(String pName, boolean enable, boolean updating){
        NavigableSet<UnloadedPluggable> pScan = pluginScan(updating);
        for(UnloadedPluggable up : pScan){
            String upName = up.getDetails().getNAME();
            if(upName.equalsIgnoreCase(pName)) {
                return loadPluggable(upName, up, enable);
            }
        }
        return null;
    }

    public PluggableReturn loadSpecificCommand(String cName, boolean enable, boolean updating){
        NavigableSet<UnloadedPluggable> cScan = commandScan(updating);
        for(UnloadedPluggable up : cScan){
            String upName = up.getDetails().getNAME();
            if(upName.equalsIgnoreCase(cName)) {
                return loadPluggable(upName, up, enable);
            }
        }
        return null;
    }

    private PluggableReturn loadPluggable(String unloadedPluggableName, UnloadedPluggable unloadedPluggable, boolean enable) {
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
        PluggableReturn pluggableReturn;
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
            pluggableReturn = pluggableUpdated(type, pluggable);
        } else {
            pluggableReturn = pluggableLoaded(type, pluggable);
        }
        if (pluggableType == PluggableType.PLUGIN){
            PLUGINS.put(lowerCaseName, (Plugin) pluggable);
        } else {
            COMMANDS.put(lowerCaseName, (Command) pluggable);
        }
        return pluggableReturn;
    }

    public ReturnableList<PluggableReturn> enableSpecificPlugin(String pluginName){
        ReturnableList<PluggableReturn> rl = getSpecificLoadedPluginOrNearMatchs(pluginName);
        if (rl.size() > 1){
            return rl;
        } else {
            PluggableReturn pr = rl.get(0);
            IPluggable pluggable = pr.getPLUGGABLE();
            if(pluggable instanceof Pluggable){
                try {
                    ((Pluggable) pluggable).enable();
                } catch (DependencyError de) {
                    pr = dependencyError("Plugin", de, pluggable);
                }
                rl.clear();
                rl.add(pr);
            }
        }
        return rl;
    }

    public ReturnableList<PluggableReturn> enableSpecificCommand(String commandName){
        ReturnableList<PluggableReturn> rl = getSpecificLoadedCommandOrNearMatchs(commandName);
        if (rl.size() > 1){
            return rl;
        } else {
            PluggableReturn pr = rl.get(0);
            IPluggable pluggable = pr.getPLUGGABLE();
            if(pluggable instanceof Pluggable){
                try {
                    ((Pluggable) pluggable).enable();
                } catch (DependencyError de) {
                    pr = dependencyError("Command", de, pluggable);
                }
                rl.clear();
                rl.add(pr);
            }
        }
        return rl;
    }

    public ReturnableList<PluggableReturn> enableAllPlugins(){
        ReturnableList<PluggableReturn> enableSuccess = new ReturnableList<>();
        PLUGINS.values().stream().forEach(p -> {
                if(!p.isEnabled()){
                    try {
                        p.enable();
                    } catch (DependencyError de) {
                        PluggableReturn pLoadSuccess = dependencyError("Plugin", de, p);
                        enableSuccess.add(pLoadSuccess);
                    }
                }
        });
        return enableSuccess;
    }

    public ReturnableList<PluggableReturn> enableAllCommands(){
        ReturnableList<PluggableReturn> enableSuccess = new ReturnableList<>();
        COMMANDS.values().stream().forEach(c -> {
            if(!c.isEnabled()){
                try {
                    c.enable();
                } catch (DependencyError de) {
                    PluggableReturn pLoadSuccess = dependencyError("Commands", de, c);
                    enableSuccess.add(pLoadSuccess);
                }
            }
        });
        return enableSuccess;
    }

    public ReturnableList<Pluggable> disableUnloadAllPlugins(boolean unload){
        ReturnableList<Pluggable> rl = new ReturnableList<>();
        PLUGINS.values().forEach(p -> {
            ReturnableList<Pluggable> rl2 = disableUnloadPluggable(p, unload);
            rl.addAll(rl2.stream().collect(Collectors.toList()));
        });
        return rl;
    }

    public ReturnableList<Pluggable> disableUnloadAllCommands(boolean unload){
        ReturnableList<Pluggable> rl = new ReturnableList<>();
        COMMANDS.values().forEach(p -> {
            ReturnableList<Pluggable> rl2 = disableUnloadPluggable(p, unload);
            rl.addAll(rl2.stream().collect(Collectors.toList()));
        });
        return rl;
    }

    public ReturnableList<Pluggable> disableUnloadSpecificPlugin(String pluginName, boolean unload){
        Plugin plugin = getSpecificLoadedPlugin(pluginName);
        return disableUnloadPluggable(plugin, unload);
    }

    public ReturnableList<Pluggable> disableUnloadSpecificCommand(String commandName, boolean unload){
        Command command = getSpecificLoadedCommand(commandName);
        return disableUnloadPluggable(command, unload);
    }

    public ReturnableList<Pluggable> disableUnloadPluggable(Pluggable p, boolean unload){
        ReturnableList<Pluggable> rl = new ReturnableList<>();
        rl.add(p);
        String lowerCaseName= p.getDetails().getNAME().toLowerCase();
        if(p.getDetails().isUNLOADABLE()){
            for (Plugin plugin : PLUGINS.values()){
                PluggableDetails details = plugin.getDetails();
                String pluginName = details.getNAME();
                if(details.hasDependency(lowerCaseName)){
                    ReturnableList<Pluggable> rl2 = disableUnloadSpecificPlugin(pluginName, false);
                    rl.addAll(rl2.stream().collect(Collectors.toList()));
                }
            }
            for (Command command : COMMANDS.values()){
                PluggableDetails details = command.getDetails();
                String pluginName = details.getNAME();
                if(details.hasDependency(lowerCaseName)){
                    ReturnableList<Pluggable> rl3 = disableUnloadSpecificCommand(pluginName, false);
                    rl.addAll(rl3.stream().collect(Collectors.toList()));
                }
            }
            if(rl.size() == 0){
                rl.setExactMatch();
            }
            p.disable();
            if(unload) {
                if(p.getDetails().getTYPE() == PluggableType.PLUGIN) {
                    PLUGINS.remove(lowerCaseName);
                } else {
                    COMMANDS.remove(lowerCaseName);
                }
            }
        }
        return rl;
    }

    public Plugin getSpecificLoadedPlugin(String pluginName){
        return PLUGINS.get(pluginName.toLowerCase());
    }

    public Command getSpecificLoadedCommand(String pluginName){
        return COMMANDS.get(pluginName.toLowerCase());
    }

    public ReturnableList<PluggableReturn> getSpecificLoadedPluginOrNearMatchs(String pluginName){
        Plugin slp = getSpecificLoadedPlugin(pluginName);
        ReturnableList<PluggableReturn> sp = new ReturnableList<>();
        if (slp == null){
            for(Map.Entry<String, Plugin> entrySet : PLUGINS.entrySet()){
                String name = entrySet.getKey();
                Plugin plugin = entrySet.getValue();
                double distance = StringUtilities.similarityCalculationCaseInsensitive(name, pluginName);
                if (distance > 90){
                    PluggableReturn pr = new PluggableReturn(plugin, false, "Similar Matches");
                    sp.add(pr);
                }
            }
        } else {
            sp.setExactMatch();
            PluggableReturn pr = new PluggableReturn(slp, true, "Exact Match");
            sp.add(pr);
        }
        return sp;
    }

    public ReturnableList<PluggableReturn> getSpecificLoadedCommandOrNearMatchs(String commandName){
        Command slc = getSpecificLoadedCommand(commandName);
        ReturnableList<PluggableReturn> sc = new ReturnableList<>();
        if (slc == null){
            for(Map.Entry<String, Command> entrySet : COMMANDS.entrySet()){
                String name = entrySet.getKey();
                Command command = entrySet.getValue();
                double distance = StringUtilities.similarityCalculationCaseInsensitive(name, commandName);
                if (distance > 90){
                    PluggableReturn pr = new PluggableReturn(command, false, "Similar Matches");
                    sc.add(pr);
                }
            }
        } else {
            sc.setExactMatch();
            PluggableReturn pr = new PluggableReturn(slc, true, "Exact Match");
            sc.add(pr);
        }
        return sc;
    }

    private PluggableReturn classCastFailure(String type, ClassCastException e, IPluggable iPluggable) {
        return failureMethod(e, type, iPluggable, "_Class_Cast_Exception", "Class Cast Failure " + type);
    }

    private PluggableReturn pluggableIOError(String type, IOException e, IPluggable iPluggable) {
        return failureMethod(e, type, iPluggable, "_IO_Error", "IO Error");
    }

    private PluggableReturn classNotFoundError(String type, ClassNotFoundException e, IPluggable iPluggable) {
        return failureMethod(e, type, iPluggable, "_Class_Not_Found", "Class Not Found");
    }

    private PluggableReturn directoryCreationError(String type, DirectoryCreationFailed e, IPluggable iPluggable) {
        return failureMethod(e, type, iPluggable, "_Directory_Creation_Failure", "Directory Creation Error");
    }

    private PluggableReturn instantiateError(String type, InstantiationException e, IPluggable iPluggable) {
        return failureMethod(e, type, iPluggable, "_Instantiation_Failure", "Instantiation Failure");
    }

    private PluggableReturn missingDataError(String type, MissingData e, IPluggable iPluggable) {
        return failureMethod(e, type, iPluggable, "_Manifest_Missing_Data", e.getMessage());
    }

    private PluggableReturn illegalAccessError(String type, IllegalAccessException e, IPluggable iPluggable) {
        return failureMethod(e, type, iPluggable, "_Class_Access_Error", "Class Access Error");
    }

    private PluggableReturn dependencyError(String type, DependencyError e, IPluggable iPluggable) {
        return failureMethod(null, type, iPluggable, "_Missing_Dependancies", e.getMessage());
    }

    private PluggableReturn pluggableEnableError(String type, IPluggable iPluggable){
        return failureMethod(null, type, iPluggable, "_Enable_Method_Exception", "Enable Method Exception");
    }

    private PluggableReturn failNewerVersion(String type, IPluggable iPluggable){
        return failureMethod(null, type, iPluggable, "_Newer_Version_Loaded", "Newer Version Already Loaded");
    }

    private PluggableReturn failureMethod(Exception e, String type, IPluggable iPluggable,  String event, String error){
        if(e != null){
            e.printStackTrace();
        }
        String nameVersion = iPluggable.getDetails().getNameVersion();
        String failedString = nameVersion + " could not successfully loadData. Reason: " + error + ".";
        StarNub.getLogger().cErrPrint("StarNub", failedString);
        new StarNubEvent(type + event, iPluggable);
        return new PluggableReturn(iPluggable, false, failedString);
    }

    private PluggableReturn pluggableLoaded(String type, Pluggable pluggable){
        String nameVersion = pluggable.getDetails().getNameVersion();
        String success = nameVersion + " was successfully loaded as a " + type;
        return successMethod(type, pluggable, success, "_Loaded");
    }

    private PluggableReturn pluggableUpdated(String type, Pluggable pluggable){
        String nameVersion = pluggable.getDetails().getNameVersion();
        String success = type + " " + nameVersion + " updated.";
        return successMethod(type, pluggable, success, "_Updated");
    }

    private PluggableReturn successMethod(String type, Pluggable pluggable, String success, String event) {
        StarNub.getLogger().cInfoPrint("StarNub", success);
        new StarNubEvent(type + event, pluggable);
        return new PluggableReturn(pluggable, true, success);
    }
}