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

import starnubserver.plugins.generic.CommandInfo;
import starnubserver.plugins.generic.PluginDetails;
import starnubserver.plugins.resources.PluginRunnables;
import starnubserver.plugins.resources.YAMLFiles;
import starnubserver.resources.files.PluginConfiguration;

import java.io.File;

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
public abstract class JavaPlugin extends Plugin {

    private final String MAIN_CLASS;

    /**
     * This is for plugins to not have to construct anything but allows for StarNub to construct this
     */
    public JavaPlugin() {
        this.MAIN_CLASS = null;
    }

    /**
     * Used in building a plugin
     *
     * @param NAME             String name of the plugin
     * @param FILE             File file path of the plugin
     * @param PLUGIN_DETAILS   PluginDetails containing plugin information
     * @param CONFIGURATION    PluginConfiguration contains the plugin configuration from disk
     * @param FILES            YAMLFiles containing the files used in this plugin
     * @param COMMAND_INFO     CommandInfo information on the command and the command packages
     * @param PLUGIN_RUNNABLES PluginRunnables containing the plugin runnables
     */
    public JavaPlugin(String NAME, File FILE, String MAIN_CLASS, PluginDetails PLUGIN_DETAILS, PluginConfiguration CONFIGURATION, YAMLFiles FILES, CommandInfo COMMAND_INFO, PluginRunnables PLUGIN_RUNNABLES) {
        super(NAME, FILE, PLUGIN_DETAILS, CONFIGURATION, FILES, COMMAND_INFO, PLUGIN_RUNNABLES);
        this.MAIN_CLASS = MAIN_CLASS;
    }

    public String getMAIN_CLASS() {
        return MAIN_CLASS;
    }
}
