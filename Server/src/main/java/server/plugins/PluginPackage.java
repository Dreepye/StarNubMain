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

package server.plugins;

import lombok.Getter;
import lombok.Setter;
import org.codehome.utilities.files.GetFileSize;
import server.Configuration;
import server.StarNub;
import server.eventsrouter.events.ThreadEvent;
import server.plugins.runnable.StarNubRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents the StarNubs PluginPackage. This information
 * is used internally when the plugin is loaded, we build this
 * from the plugin.yml. This helps Server owners as well as plugin
 * makers to get information about plugins, get configuration variables,
 * as well as the commands that are associated with the plugin.
 * <p>
 * NOTE: These fields are public for the sake of information dumping via YAML.
 * <p>
 * This class is abstract and how a generic PluginPackage should look.
 * <p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public abstract class PluginPackage {

    @Getter
    public final String PLUGIN_NAME;
    @Getter
    public final double VERSION;
    @Getter
    public final double SIZE_KBS;
    @Getter
    public final String DEPENDENCIES;
    @Getter
    public final boolean HAS_CONFIGURATION_FILE;
    @Getter
    public final Configuration CONFIGURATION;
    @Getter
    public final String PLUGIN_LANGUAGE;
    @Getter
    public final String PLUGIN_AUTHOR;
    @Getter
    public final String PLUGIN_URL;
    @Getter
    public final String PLUGIN_DESCRIPTION;
    @Getter
    private final Plugin PLUGIN;
    @Getter
    public final boolean HAS_COMMANDS;
    @Getter
    public final String COMMANDS_NAME;
    @Getter
    public final String COMMANDS_ALIAS;
    @Getter
    private final ConcurrentHashMap<ArrayList<String>, CommandPackage> COMMAND_PACKAGES;
    @Getter @Setter
    private boolean isEnabled;
    @Getter @Setter
    private boolean hasThreads;
    @Getter @Setter
    private ConcurrentHashMap<Thread, StarNubRunnable> threads;

    /**
     * This is one long constructor, but it is to make plugin information available and
     * simple to grab.
     * <p>
     *  @param pluginName              String the plugin name
     * @param version                 double the plugin version
     * @param pluginLocation          File where on the hard disk is the plugin
     * @param dependencies            String a comma separated list of dependencies
     * @param pluginConfigurationFile boolean does this Plugin have a configuration
     * @param pluginConfiguration     Configuration the configuration, null if none
     * @param pluginLanguage          String what languages does the plugin support
     * @param pluginAuthor            String the authors name
     * @param pluginURL               String the url for the plugins page, download, help, etc
     * @param pluginDescription       String a short description of the plugin
     * @param plugin                  Plugin the actual plugin file
     * @param pluginCommands          boolean does the plugin have commands
     * @param pluginCommandsName      String representing the commands name
     * @param pluginCommandsAlias     String representing the commands alias
     * @param pluginCommandPackages   ConcurrentHashMap the plugin commands
*                                <String, CommandPackage> String = name of command, CommandPackage is like
     * @param hasThreads
     * @param threads
     */
    public PluginPackage(String pluginName,
                         double version,
                         File pluginLocation,
                         String dependencies,
                         boolean pluginConfigurationFile,
                         Configuration pluginConfiguration,
                         String pluginLanguage,
                         String pluginAuthor,
                         String pluginURL,
                         String pluginDescription,
                         Plugin plugin,
                         boolean pluginCommands,
                         String pluginCommandsName,
                         String pluginCommandsAlias,
                         ConcurrentHashMap<ArrayList<String>, CommandPackage> pluginCommandPackages,
                         boolean hasThreads,
                         ConcurrentHashMap<Thread, StarNubRunnable> threads) {

        this.PLUGIN_NAME = pluginName;
        this.VERSION = version;
        this.SIZE_KBS = new GetFileSize().getFileSize(pluginLocation, "kilobytes");
        this.DEPENDENCIES = dependencies;
        this.HAS_CONFIGURATION_FILE = pluginConfigurationFile;
        this.CONFIGURATION = pluginConfiguration;
        this.PLUGIN_LANGUAGE = pluginLanguage;
        this.PLUGIN_AUTHOR = pluginAuthor;
        this.PLUGIN_URL = pluginURL;
        this.PLUGIN_DESCRIPTION = pluginDescription;
        this.PLUGIN = plugin;
        this.HAS_COMMANDS = pluginCommands;
        this.COMMANDS_NAME = pluginCommandsName;
        this.COMMANDS_ALIAS = pluginCommandsAlias;
        this.COMMAND_PACKAGES = pluginCommandPackages;
        this.hasThreads = hasThreads;
        this.threads = threads;
    }

    public void startThreads(){
        for (Thread thread : threads.keySet()){
            StarNub.getLogger().cInfoPrint("StarNub", "Starting thread: "+thread.getName()+".");
            thread.start();
            ThreadEvent.eventSend_Permanent_Thread_Started(PLUGIN_NAME, thread);
        }
    }

    public void shutdownThreads(){
        for (Thread thread : threads.keySet()){
            StarNub.getLogger().cInfoPrint("StarNub", "Stopping thread: "+thread.getName()+".");
            threads.get(thread).setShuttingDown(true);
            ThreadEvent.eventSend_Permanent_Thread_Stopped(PLUGIN_NAME, thread);
        }
    }
}
