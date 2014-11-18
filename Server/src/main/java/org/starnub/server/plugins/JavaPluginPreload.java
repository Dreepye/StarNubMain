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

package org.starnub.server.plugins;


import org.codehome.utilities.directories.DirectoryCheckCreate;
import org.starnub.server.Configuration;
import org.starnub.server.StarNub;
import org.starnub.server.plugins.runnable.StarNubRunnable;

import java.io.File;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents Java Plugin Loader.
 * <p>
 * This enum singleton holds and runs all things important
 * to loading Java Plugins
 * <p>
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
enum JavaPluginPreload {
    INSTANCE;

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method is where all the magic happens for loading a plugin.
     * This method loads and insure the plugin is within standards and will not have issues
     * being run in StarNub. If the plugin load fails it will be added to a failed plugins
     * ConcurrentHashMap and printed out after the process.
     * <p>
     * @param sender
     * @param unloadedPlugin    UnloadedPlugin which holds the data for the plugin StarNub is trying to load
     * @param isUpgrading       boolean if we are upgrading a running plugin or not,
     */
    @SuppressWarnings("unchecked")
    public void pluginPackageLoader(Object sender, UnloadedPlugin unloadedPlugin, boolean isUpgrading) {
        String pluginName = unloadedPlugin.getPLUGIN_NAME();
        Map data = unloadedPlugin.getPLUGIN_PLUGIN_YML();
        URLClassLoader classLoader = unloadedPlugin.getPLUGIN_URL_CLASS_LOADER();
        String URLString = unloadedPlugin.getPLUGIN_URL().toString();
        File pluginFilePath = unloadedPlugin.getPLUGIN_FILE();

        /* Check plugin for duplicate plugins */
        if (!isUpgrading) {
            for (PluginPackage pluginPackage : PluginManager.loadedPlugins.values()) {
                if (pluginPackage.getPLUGIN_NAME().equals(data.get("name"))) {
                    StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName+" is already loaded.");
                    return;
                }
            }
        }

        /* Since the plugin is not a duplicate get main class*/
        String mainClass = null;
        try {
            mainClass = (String) data.get("class");
        } catch (NullPointerException e) {
            StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName + " Plugin Load Error: Could not find \"class\" value in: \"plugin.yml\".");
            return;
        }

        /* Load Class into a java class, then cast to a Plugin */
        Plugin plugin;
        Class<?> javaClass;
        try {
            javaClass = classLoader.loadClass(mainClass);
            try {
                Class<? extends Plugin> pluginClass = javaClass.asSubclass(Plugin.class);
                try {
                    plugin = pluginClass.newInstance();
                } catch (InstantiationException e) {
                    StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName + " Plugin Load Error: Could not instantiate plugin.");
                    return;
                } catch (IllegalAccessException e) {
                    StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName + " Plugin Load Error: Could not instantiate plugin, illegal access exception.");
                    return;
                }
            } catch (ClassCastException e) {
                StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName + " Plugin Load Error: Does not represent a plugin.");
                return;
            }
        } catch (ClassNotFoundException e) {
            StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName + " Plugin Load Error: Class not found: \"" + mainClass + "\" for \"class\" value in: \"plugin.yml\".");
            return;
        } catch (NoClassDefFoundError e) {
            StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName + " Plugin Load Error: Package not found: \"" + mainClass + "\" for \"class\" value in: \"plugin.yml\".");
            return;
        }

        /*  Dependency check */
        if (!dependencyCheck(sender, pluginName, data)) {
            return;
        }

        /*  Directory Check & configuration load */
        new DirectoryCheckCreate("StarNub/Plugins", pluginName, pluginName+"/CommandsInfo");
        boolean configurationFile = (boolean) data.get("configuration");
        Configuration configuration = null;
        if (configurationFile) {
            configuration = new Configuration(pluginName + " Plugin Configuration",classLoader.getResourceAsStream("default_plugin_configuration.yml"), "StarNub/Plugins/" + pluginName + "/" + pluginName.toLowerCase() + "_config.yml");
            configuration.loadConfiguration(false);
        }
        if (configurationFile) {
            if (configuration.getConfiguration() == null) {
                StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName + " Plugin Load Error: Unable to load Plugin Configuration for Plugin.");
                return;
            }
        }

        boolean commands = (boolean) data.get("commands");
        String commandName = (String) data.get("commands_name");
        ConcurrentHashMap<ArrayList<String>, CommandPackage> commandPackages = null;
        if (commands) {
            commandPackages = CommandPreload.INSTANCE.preloadPluginCommands(sender, pluginFilePath, pluginName, classLoader, commandName);
        }

        boolean hasRunnables = (boolean) data.get("runnables");
        Object runnableClasses = data.get("runnable_classes");
        ArrayList<String> classes = new ArrayList<String>();
        ConcurrentHashMap<Thread, StarNubRunnable> threads = null;
        if (hasRunnables) {
            if (runnableClasses instanceof String) {
                classes.add(String.valueOf(classes));
            } else if (runnableClasses instanceof List) {
                classes = (ArrayList<String>) runnableClasses;
            }
            threads = ThreadsAndRunnablesPreload.INSTANCE.preloadThreadsAndRunnables(sender, pluginFilePath, pluginName, classLoader, classes);
        }

        try {
            JavaPluginPackage javaPluginPackage = new JavaPluginPackage(
                    pluginName,
                    (double) data.get("version"),
                    pluginFilePath,
                    (String) data.get("dependencies"),
                    configurationFile,
                    configuration,
                    (String) data.get("language"),
                    (String) data.get("author"),
                    (String) data.get("url"),
                    (String) data.get("description"),
                    plugin,
                    commands,
                    commandName,
                    (String) data.get("commands_alias"),
                    commandPackages,
                    hasRunnables,
                    threads,
                    (String) data.get("class"));
            if (isUpgrading) {
                StarNub.getPluginManager().unloadSpecificPlugin(sender, pluginName);
            }
            StarNub.getPluginManager().addLoadedPlugin(pluginName , javaPluginPackage);
            StarNub.getPluginManager().removeUnloadedPluginFromList(pluginName);


        } catch (NullPointerException e) {
            StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName + " Plugin Load Error: Null value or empty: \"plugin.yml\".");
        }
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method is where StarNub checks for dependencies. If it has a dependicy we will
     * attempt to load the dependancies first
     * <p>
     * @param pluginName String representing the name to print of the plugin fails before we get its plugin.yml
     */
    @SuppressWarnings("unchecked")
    private boolean dependencyCheck(Object sender, String pluginName, Map data) {
        boolean success = false;
        try {
            try {
                List<String> dependencies = (List<String>) StarNub.getConfiguration().getConfiguration().get("dependencies");
                for (String dependency : dependencies) {
                    success = checkDependency(sender, pluginName, dependency);
                    if (!success) {
                        return  false;
                    }
                }
            } catch (ClassCastException ex) {
                 return checkDependency(sender, pluginName, (String) StarNub.getConfiguration().getConfiguration().get("dependencies"));
            }
        } catch (NullPointerException e) {
            return true;
        }
        return success;
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method is where StarNub attempts to load the dependencies that were found.
     * <p>
     * @param pluginName String representing the name to print of the plugin fails before we get its plugin.yml
     * @param dependency String representing the dependency
     * @return boolean if the dependency load was successful or not
     */
    private boolean checkDependency(Object sender, String pluginName, String dependency) {
        if (!StarNub.getPluginManager().getLoadedPlugins().contains(pluginName)) {
            StarNub.getPluginManager().loadSpecificPlugin(sender, dependency, false, false);//TODO FIX
            if (StarNub.getPluginManager().getLoadedPlugins().contains(pluginName)) {
                return true;
            } else {
                StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName + " Plugin Load Error: Failure to load dependency \"" + dependency + ".");
                return false;
            }
        }
        return false;
    }
}




