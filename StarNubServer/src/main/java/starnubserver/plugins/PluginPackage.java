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

package starnubserver.plugins;

import starnubserver.StarNub;
import starnubserver.events.events.ThreadEvent;
import starnubserver.plugins.runnable.StarNubRunnable;
import starnubserver.resources.files.PluginConfiguration;
import utilities.file.utility.GetFileSize;

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

    public final String PLUGIN_NAME;
    public final double VERSION;
    public final double SIZE_KBS;
    public final String DEPENDENCIES;
    public final boolean HAS_CONFIGURATION_FILE;
    public final PluginConfiguration CONFIGURATION;
    public final String PLUGIN_LANGUAGE;
    public final String PLUGIN_AUTHOR;
    public final String PLUGIN_URL;
    public final String PLUGIN_DESCRIPTION;
    private final Plugin PLUGIN;
    public final boolean HAS_COMMANDS;
    public final String COMMANDS_NAME;
    public final String COMMANDS_ALIAS;
    private final ConcurrentHashMap<ArrayList<String>, CommandPackage> COMMAND_PACKAGES;
    private boolean isEnabled;
    private boolean hasThreads;

    private ConcurrentHashMap<Thread, StarNubRunnable> threads;

    /**
     * This is one long constructor, but it is to make plugin information available and
     * simple to grab.
     * <p>
     * @param pluginName              String the plugin name
     * @param version                 double the plugin version
     * @param pluginLocation          File where on the hard disk is the plugin
     * @param dependencies            String a comma separated list of dependencies
     * @param pluginConfigurationFile boolean does this Plugin have a configuration
     * @param pluginConfiguration     Configuration the configuration, null if none
     * @param pluginLanguage          String what languages does the plugin support
     * @param pluginAuthor            String the authors name
     * @param pluginURL               String the url for the plugins page, download, help, etc
     * @param pluginDescription       String a short description of the plugin
     * @param plugin                  Plugin the actual plugin utilities.file
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
                         PluginConfiguration pluginConfiguration,
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
        this.SIZE_KBS = GetFileSize.getFileSize(pluginLocation, "kilobytes");
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

    public String getPLUGIN_NAME() {
        return PLUGIN_NAME;
    }

    public double getVERSION() {
        return VERSION;
    }

    public double getSIZE_KBS() {
        return SIZE_KBS;
    }

    public String getDEPENDENCIES() {
        return DEPENDENCIES;
    }

    public boolean isHAS_CONFIGURATION_FILE() {
        return HAS_CONFIGURATION_FILE;
    }

    public PluginConfiguration getCONFIGURATION() {
        return CONFIGURATION;
    }

    public String getPLUGIN_LANGUAGE() {
        return PLUGIN_LANGUAGE;
    }

    public String getPLUGIN_AUTHOR() {
        return PLUGIN_AUTHOR;
    }

    public String getPLUGIN_URL() {
        return PLUGIN_URL;
    }

    public String getPLUGIN_DESCRIPTION() {
        return PLUGIN_DESCRIPTION;
    }

    public Plugin getPLUGIN() {
        return PLUGIN;
    }

    public boolean isHAS_COMMANDS() {
        return HAS_COMMANDS;
    }

    public String getCOMMANDS_NAME() {
        return COMMANDS_NAME;
    }

    public String getCOMMANDS_ALIAS() {
        return COMMANDS_ALIAS;
    }

    public ConcurrentHashMap<ArrayList<String>, CommandPackage> getCOMMAND_PACKAGES() {
        return COMMAND_PACKAGES;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean isHasThreads() {
        return hasThreads;
    }

    public void setHasThreads(boolean hasThreads) {
        this.hasThreads = hasThreads;
    }

    public ConcurrentHashMap<Thread, StarNubRunnable> getThreads() {
        return threads;
    }

    public void setThreads(ConcurrentHashMap<Thread, StarNubRunnable> threads) {
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
