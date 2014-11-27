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
public abstract class Plugin {

    /**
     * What to do when the plugin is enabled.
     */
    public abstract void onPluginEnable();

    /**
     * What to do when the plugin is disabled.
     */
    public abstract void onPluginDisable();

}


