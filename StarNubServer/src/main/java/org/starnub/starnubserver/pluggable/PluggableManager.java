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
import org.starnub.starnubserver.pluggable.exceptions.MissingData;
import org.starnub.starnubserver.pluggable.exceptions.NotAPluggable;
import org.starnub.starnubserver.pluggable.exceptions.PluginDirectoryCreationFailed;
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
                PluggableDetails unloadedDetails = unloadedPluggable.getPluggableDetails();
                String unloadedName = unloadedDetails.getOWNER();
                double unloadedVersion = unloadedDetails.getVERSION();
                /* Is loaded */
                if (loadedName.equals(unloadedName)) {
                    if (unloadedVersion > loadedVersion && updating) {
                        unloadedPluggable.setUpdating();
                    } else {
                        String removeFileRecommend = unloadedPluggable.getPluggableFile().toString();
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
                HashSet<String> dependencies = value.getPluggableDetails().getDEPENDENCIES();
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
                PluggableDetails unloadedDetails = unloadedPluggable.getPluggableDetails();
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
                        String removeFileRecommend = unloadedPluggable.getPluggableFile().toString();
                        unloadedPluggables.remove(loadedName);
                        StarNub.getLogger().cErrPrint("StarNub", "This command " + loadedName + " is already loaded, File: " + removeFileRecommend + ".");
                        //INSERT - STARNUB EVENT UPDATE
                    }
                }
            }
        }
        return unloadedPluggables;
    }

    private HashMap<String, UnloadedPluggable> getFiles(String directoryString, String... extensions) {
        File directoryFile = new File(directoryString);
        File[] files = FileUtils.convertFileCollectionToFileArray(FileUtils.listFiles(directoryFile, extensions, false));
        HashMap<String, UnloadedPluggable> tempPluggables = new HashMap<>();
        for (File file : files) {
            try {
                UnloadedPluggable unloadedPluggable = new UnloadedPluggable(file);
                if (unloadedPluggable.getPluggableDetails() != null){
                    String name = unloadedPluggable.getPluggableDetails().getOWNER();
                    tempPluggables.put(name, unloadedPluggable);
                }
            } catch (PluginDirectoryCreationFailed | MissingData | IOException | NotAPluggable pluginDirectoryCreationFailed) {
                pluginDirectoryCreationFailed.printStackTrace();
            }
        }
        return tempPluggables;
    }

    public HashMap<String, String> loadAllCommands() {//FIX UPDATING COMMANDS
        HashMap<String, UnloadedPluggable> unloadedPluggableHashMap = commandScan(true);
        HashMap<String, String> commandSuccess = new HashMap<>();
        for (Map.Entry<String, UnloadedPluggable> entrySet : unloadedPluggableHashMap.entrySet()) {
            String unloadedCommandName = entrySet.getKey();
            UnloadedPluggable unloadedPluggable = entrySet.getValue();
            try {
                Command pluggable = (Command) unloadedPluggable.instantiatePluggable(PluggableType.COMMAND);
                commandSuccess.put(unloadedCommandName, "Success");
                if (unloadedPluggable.isUpdating()){
                    COMMANDS.remove(unloadedCommandName);
                   //INSERT - STARNUB EVENT UPDATE
                }
                COMMANDS.put(unloadedCommandName, pluggable);
                pluggable.register();
                //INSERT - STARNUB EVENT UPDATE
            } catch (IOException | PluginDirectoryCreationFailed | MissingData | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
                commandSuccess.put(unloadedCommandName, e.getMessage());
                //INSERT - STARNUB EVENT UPDATE
            }
        }
        return commandSuccess;
    }

    public HashMap<String, String> loadAllPlugins(boolean enable) {
        HashMap<String, UnloadedPluggable> unloadedPluggableHashMap = pluginScan(true);
        HashMap<String, String> pluginSuccess = new HashMap<>();
        for (Map.Entry<String, UnloadedPluggable> entrySet : unloadedPluggableHashMap.entrySet()) {
            String unloadedPluginName = entrySet.getKey();
            UnloadedPluggable unloadedPluggable = entrySet.getValue();
            HashSet<String> dependencies = unloadedPluggable.getPluggableDetails().getDEPENDENCIES();
            boolean dependenciesLoaded = true;
            if (dependencies != null) {
                for (String dependency : dependencies) {
                    boolean containsKey = PLUGINS.containsKey(dependency);
                    if (!containsKey) {
                        dependenciesLoaded = false;
                    }
                }
            }
            if (!dependenciesLoaded){
                pluginSuccess.put(unloadedPluginName, "Could not load dependency");
                //INSERT - STARNUB EVENT UPDATE
            } else {
                try {
                    Plugin pluggable = (Plugin) unloadedPluggable.instantiatePluggable(PluggableType.PLUGIN);
                    pluginSuccess.put(unloadedPluginName, "Success");
                    if (unloadedPluggable.isUpdating()) {
                        PLUGINS.remove(unloadedPluginName);
                        //INSERT - STARNUB EVENT UPDATE
                    }
                    PLUGINS.put(unloadedPluginName, pluggable);
                    if (enable) {
                        pluggable.enable();
                        pluggable.register();
                    }
                    //INSERT - STARNUB EVENT UPDATE
                } catch (PluginDirectoryCreationFailed | MissingData | ClassNotFoundException | IOException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                    pluginSuccess.put(unloadedPluginName, e.getMessage());
                }
            }
        }
        return pluginSuccess;//EMABLE
    }



//
//    public String loadSpecificPlugin(String pluginName, boolean upgrade, boolean enable) throws NoSuchMethodException, IllegalAccessException, InstantiationException, PluginAlreadyLoaded, InvocationTargetException, ClassNotFoundException, IOException, PluginDependencyLoadFailed, PluginDirectoryCreationFailed, PluginDependencyNotFound, CommandClassLoadFail, CommandYamlLoadFailed {
//        UnloadedPlugin unloadedPlugin = UNLOADED_PLUGINS.remove(pluginName);
//        boolean isLoaded = isPluginLoaded(pluginName, true);
//        if (!upgrade && isLoaded) {
//            throw new PluginAlreadyLoaded(pluginName);
//        }
//        JavaPlugin javaPlugin = JAVA_PLUGIN_LOADER.pluginLoader(unloadedPlugin);
//        if (upgrade) {
//            unloadSpecificPlugin(pluginName);
//            javaPlugin.enable();
//        } else if (enable) {
//            javaPlugin.enable();
//        }
//        LOADED_PLUGINS.put(javaPlugin.getOWNER(), javaPlugin);
//        double version = javaPlugin.getDETAILS().getVERSION();
//        StarNub.getLogger().cInfoPrint("StarNub", pluginName + " (" + version + ") " + "was successfully loaded.");
//        new StarNubEvent("StarNub_Plugin_Loaded", javaPlugin);
//        return pluginName + " was successfully loaded.";
//    }

    public void unloadAllCommands(){
        COMMANDS.values().forEach(org.starnub.starnubserver.pluggable.Command::unregisterTask);
        COMMANDS.values().forEach(org.starnub.starnubserver.pluggable.Command::unregisterSubscriptions);
        COMMANDS.clear();
    }

    public void unloadAllPlugins(){
        PLUGINS.values().forEach(org.starnub.starnubserver.pluggable.Plugin::unregisterTask);
        PLUGINS.values().forEach(org.starnub.starnubserver.pluggable.Plugin::unregisterSubscriptions);
        PLUGINS.values().forEach(org.starnub.starnubserver.pluggable.Plugin::disable);
        PLUGINS.clear();
    }



//
//    public String unloadAllPlugins() {
//        LOADED_PLUGINS.values().forEach(Plugin::disable);
//        LOADED_PLUGINS.clear();
//        if (LOADED_PLUGINS.size() > 0) {
//            return "All plugins were successfully unloaded.";
//        } else {
//            String loadedPlugins = stringCreateLoadedPlugins();
//            return "Not all plugins were successfully unloaded." + loadedPlugins;
//        }
//    }
//
//    public String unloadSpecificPlugin(String pluginName) {
//        Plugin plugin = LOADED_PLUGINS.remove(pluginName);
//        plugin.disable();
//        if (!LOADED_PLUGINS.containsKey(pluginName)) {
//            new StarNubEvent("StarNub_Plugin_Unloaded", plugin);
//            return pluginName + " was successfully unloaded.";
//        } else {
//            return pluginName + " was not successfully unloaded.";
//        }
//    }
}