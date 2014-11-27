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

package starnub.plugins;

import starnub.Configuration;
import starnub.plugins.runnable.StarNubRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents the StarNubs JavaPluginPackage. This information
 * is used internally when the plugin is loaded, we build this
 * from the plugin.yml. This helps Server owners as well as plugin
 * makers to get information about plugins, get configuration variables,
 * as well as the commands that are associated with the plugin.
 * <p>
 * This JavaPluginPackage extends a PluginPackage, so we can include the
 * main JavaClass that contains the onPluginEnable & onPluginDisable.
 * <p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public class JavaPluginPackage extends PluginPackage {

    /**
     * Where we find the main class for the plugin
     */

    public final String MAIN_CLASS;

    /**
     * This is one long constructor, but it is to make plugin information available and
     * simple to grab.
     * <p>
     *
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
     *                                similar to a PluginPackage
     * @param MAIN_CLASS               String which is the class location for the plugin
     */
    public JavaPluginPackage(String pluginName, double version, File pluginLocation, String dependencies, boolean pluginConfigurationFile, Configuration pluginConfiguration, String pluginLanguage, String pluginAuthor, String pluginURL, String pluginDescription, Plugin plugin, boolean pluginCommands, String pluginCommandsName, String pluginCommandsAlias, ConcurrentHashMap<ArrayList<String>, CommandPackage> pluginCommandPackages, boolean HAS_THREADS, ConcurrentHashMap<Thread, StarNubRunnable> THREADS, String MAIN_CLASS) {
        super(pluginName, version, pluginLocation, dependencies, pluginConfigurationFile, pluginConfiguration, pluginLanguage, pluginAuthor, pluginURL, pluginDescription, plugin, pluginCommands, pluginCommandsName, pluginCommandsAlias, pluginCommandPackages, HAS_THREADS, THREADS);
        this.MAIN_CLASS = MAIN_CLASS;
    }

    public String getMAIN_CLASS() {
        return MAIN_CLASS;
    }
}
