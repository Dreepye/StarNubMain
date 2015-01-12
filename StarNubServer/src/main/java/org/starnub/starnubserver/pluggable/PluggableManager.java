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

    public void pluginScan(boolean updating) {
        LinkedHashMap<String, UnloadedPluggable> orderUnloadedPlugholes = new LinkedHashMap<>();
        HashMap<String, UnloadedPluggable> unloadedPluggables = getFiles(PLUGIN_DIRECTORY_STRING, ".jar", ".py");
        /* Remove plugins that are already loaded */
        for (Plugin plugin : PLUGINS.values()) {
            PluggableDetails details = plugin.getPluggableDetails();
            String loadedName = details.getNAME();
            double loadedVersion = details.getVERSION();
            UnloadedPluggable unloadedPluggable = unloadedPluggables.get(loadedName);
            if (unloadedPluggable != null) {
                PluggableDetails unloadedDetails = unloadedPluggable.getPluggableDetails();
                String unloadedName = unloadedDetails.getNAME();
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
                ArrayList<String> loadFirst = value.getLoadFirst();
                HashSet<String> dependencies = value.getPluggableDetails().getDEPENDENCIES();
                boolean canLoad = true;
                if (loadFirst != null) {
                    for (String toLoad : loadFirst) {
                        if (!unloadedPluggables.containsKey(toLoad) && !PLUGINS.containsKey(toLoad)) {
                            iterator.remove();
                        } else if (!orderUnloadedPlugholes.containsKey(toLoad)) {
                            canLoad = false;
                        }
                    }
                }
                if (dependencies != null) {
                    for (String dependency : dependencies) {
                        if (!unloadedPluggables.containsKey(dependency) && !PLUGINS.containsKey(dependency)) {
                            iterator.remove();
                        } else if (!orderUnloadedPlugholes.containsKey(dependency)) {
                            canLoad = false;
                        }
                    }
                }
                if (canLoad) {
                    orderUnloadedPlugholes.put(key, value);
                }
            }
        }
    }

    public HashMap<String, UnloadedPluggable> commandScan(boolean updating) {
        HashMap<String, UnloadedPluggable> unloadedPluggables = getFiles(COMMAND_DIRECTORY_STRING, "jar", "py");
        for (Command command : COMMANDS.values()){
            PluggableDetails details = command.getPluggableDetails();
            String loadedName = details.getNAME();
            double loadedVersion = details.getVERSION();
            String loadedcommandName = command.getCommand();
            UnloadedPluggable unloadedPluggable = unloadedPluggables.get(loadedName);
            if (unloadedPluggable != null) {
                PluggableDetails unloadedDetails = unloadedPluggable.getPluggableDetails();
                String unloadedName = unloadedDetails.getNAME();
                double unloadedVersion = unloadedDetails.getVERSION();
                String unloadedCommandName = (String) unloadedPluggable.getYamlWrapper().getValue("command");
                /* Is loaded */
                if (unloadedCommandName.equals(unloadedCommandName)) {
                    if (unloadedVersion > loadedVersion && updating) {
                        unloadedPluggable.setUpdating();
                    } else {
                        String removeFileRecommend = unloadedPluggable.getPluggableFile().toString();
                        unloadedPluggables.remove(loadedName);
                        StarNub.getLogger().cErrPrint("StarNub", "This command " + loadedName + " is already loaded, File: " + removeFileRecommend + ".");
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
                String name = unloadedPluggable.getPluggableDetails().getNAME();
                tempPluggables.put(name, unloadedPluggable);
            } catch (PluginDirectoryCreationFailed | MissingData | IOException pluginDirectoryCreationFailed) {
                pluginDirectoryCreationFailed.printStackTrace();
            }
        }
        return tempPluggables;
    }

    public HashMap<String, String> loadAllCommands() {
        HashMap<String, UnloadedPluggable> unloadedPluggableHashMap = commandScan(true);
        HashMap<String, String> commandSuccess = new HashMap<>();
        for (Map.Entry<String, UnloadedPluggable> entrySet : unloadedPluggableHashMap.entrySet()) {
            String unloadedCommandName = entrySet.getKey();
            UnloadedPluggable unloadedPluggable = entrySet.getValue();
            try {
                Command pluggable = (Command) unloadedPluggable.instantiatePluggable(PluggableType.COMMAND);
                commandSuccess.put(unloadedCommandName, "Success");
                COMMANDS.put(unloadedCommandName, pluggable);
            } catch (IOException | PluginDirectoryCreationFailed | MissingData | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
                commandSuccess.put(unloadedCommandName, e.getMessage());
            }
        }
        return commandSuccess;
    }
//
//    public HashMap<String, String> loadAllPlugins(boolean upgrade, boolean enable) {
//        HashMap<String, String> pluginSuccess = new HashMap<>();
//        for (Map.Entry<String, UnloadedPlugin> entrySet : UNLOADED_PLUGINS.entrySet()) {
//            String unloadedPluginName = entrySet.getKey();
//            UnloadedPlugin unloadedPlugin = entrySet.getValue();
//            YAMLWrapper plugin_yml = unloadedPlugin.getPLUGIN_PLUGIN_YML();
//            List<String> loadFirst = (List<String>) plugin_yml.getNestedValue("details", "load_first");
//            for (String string : loadFirst) {
//                if (UNLOADED_PLUGINS.containsKey(string)) {
//                    String specificPlugin = null;
//                    try {
//                        specificPlugin = loadSpecificPlugin(string, false, enable);
//                    } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | PluginAlreadyLoaded | InvocationTargetException | ClassNotFoundException | IOException | PluginDependencyLoadFailed | PluginDirectoryCreationFailed | PluginDependencyNotFound | CommandYamlLoadFailed | CommandClassLoadFail e) {
//                        e.printStackTrace();
//                        pluginSuccess.put(unloadedPluginName, e.getMessage());
//                    }
//                    pluginSuccess.put(unloadedPluginName, specificPlugin);
//                }
//            }
//            String specificPlugin = null;
//            try {
//                specificPlugin = loadSpecificPlugin(unloadedPluginName, upgrade, enable);
//            } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | PluginAlreadyLoaded | InvocationTargetException | ClassNotFoundException | IOException | PluginDependencyLoadFailed | PluginDirectoryCreationFailed | PluginDependencyNotFound | CommandYamlLoadFailed | CommandClassLoadFail e) {
//                e.printStackTrace();
//                pluginSuccess.put(unloadedPluginName, e.getMessage());
//            }
//            pluginSuccess.put(unloadedPluginName, specificPlugin);
//        }
//        return pluginSuccess;
//    }
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
//        LOADED_PLUGINS.put(javaPlugin.getNAME(), javaPlugin);
//        double version = javaPlugin.getDETAILS().getVERSION();
//        StarNub.getLogger().cInfoPrint("StarNub", pluginName + " (" + version + ") " + "was successfully loaded.");
//        new StarNubEvent("StarNub_Plugin_Loaded", javaPlugin);
//        return pluginName + " was successfully loaded.";
//    }
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