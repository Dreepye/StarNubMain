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

import starnubserver.StarNubTaskManager;
import starnubserver.events.packet.PacketEventRouter;
import starnubserver.events.starnub.StarNubEventRouter;
import starnubserver.plugins.generic.CommandInfo;
import starnubserver.plugins.generic.PluginDetails;
import starnubserver.plugins.resources.PluginRunnables;
import starnubserver.plugins.resources.YAMLFiles;
import starnubserver.resources.files.PluginConfiguration;

/**
 * Represents the a StarNub Plugin.
 * The plugin maker must @Override the
 * onPluginEnable & onPluginDisable method.
 * <p>
 * This Plugin class is abstract and must be extended. This is what
 * all Plugin classes should look like by default.
 * <p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public abstract class Plugin extends PluginPackage{

    /**
     * This is for plugins to not have to construct anything but allows for StarNub to construct this
     */
    public Plugin() {
    }

    /**
     * Used in building a plugin
     *
     * @param NAME             String name of the plugin
     * @param PATH             String file path of the plugin
     * @param PLUGIN_DETAILS   PluginDetails containing plugin information
     * @param CONFIGURATION    PluginConfiguration contains the plugin configuration from disk
     * @param FILES            YAMLFiles containing the files used in this plugin
     * @param COMMAND_INFO     CommandInfo information on the command and the command packages
     * @param PLUGIN_RUNNABLES PluginRunnables containing the plugin runnables
     */
    public Plugin(String NAME, String PATH, PluginDetails PLUGIN_DETAILS, PluginConfiguration CONFIGURATION, YAMLFiles FILES, CommandInfo COMMAND_INFO, PluginRunnables PLUGIN_RUNNABLES) {
        super(NAME, PATH, PLUGIN_DETAILS, CONFIGURATION, FILES, COMMAND_INFO, PLUGIN_RUNNABLES);
    }

    public void enable(){
        onPluginEnable();
        PluginRunnables pluginRunnables = super.getRUNNABLES();
        pluginRunnables.startThreads();
        super.setEnabled(true);
    }

    public void disable(){
        onPluginDisable();
        String name = super.getNAME();
        PluginRunnables pluginRunnables = super.getRUNNABLES();
        pluginRunnables.shutdownThreads();
        StarNubTaskManager.getInstance().purgeByOwnerName(name);
        PacketEventRouter.getInstance().removeEventSubscription(name);
        StarNubEventRouter.getInstance().removeEventSubscription(name);
        super.setEnabled(false);
    }

    /**
     * What to do when the plugin is enabled.
     */
    public abstract void onPluginEnable();

    /**
     * What to do when the plugin is disabled.
     */
    public abstract void onPluginDisable();

}



