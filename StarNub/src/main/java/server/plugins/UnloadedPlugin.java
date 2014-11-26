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

package server.plugins;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

/**
 * Represents a plugin that has not been attempted to be loaded.
 * <p>
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
class UnloadedPlugin {


    private final String PLUGIN_NAME;

    private final double PLUGIN_VERSION;

    private final URL PLUGIN_URL;

    private final File PLUGIN_FILE;

    private final URLClassLoader PLUGIN_URL_CLASS_LOADER;

    private final Map<String, Object> PLUGIN_PLUGIN_YML;

    /**
     *
     * @param PLUGIN_NAME String representing the plugin on disk
     * @param PLUGIN_URL URL representing the plugin location on disk
     * @param PLUGIN_PLUGIN_YML Map representing the plugin.yml which
     *                          tells StarNub how to load the plugin
     */
    public UnloadedPlugin(String PLUGIN_NAME, double PLUGIN_VERSION, URL PLUGIN_URL, URLClassLoader PLUGIN_URL_CLASS_LOADER, Map<String, Object> PLUGIN_PLUGIN_YML) {
        this.PLUGIN_NAME = PLUGIN_NAME;
        this.PLUGIN_VERSION = PLUGIN_VERSION;
        this.PLUGIN_URL = PLUGIN_URL;
        String URLString = PLUGIN_URL.toString();
        this.PLUGIN_FILE = new File (URLString.substring(URLString.indexOf("StarNub")));
        this.PLUGIN_URL_CLASS_LOADER = PLUGIN_URL_CLASS_LOADER;
        this.PLUGIN_PLUGIN_YML = PLUGIN_PLUGIN_YML;
    }

    public String getPLUGIN_NAME() {
        return PLUGIN_NAME;
    }

    public double getPLUGIN_VERSION() {
        return PLUGIN_VERSION;
    }

    public URL getPLUGIN_URL() {
        return PLUGIN_URL;
    }

    public File getPLUGIN_FILE() {
        return PLUGIN_FILE;
    }

    public URLClassLoader getPLUGIN_URL_CLASS_LOADER() {
        return PLUGIN_URL_CLASS_LOADER;
    }

    public Map<String, Object> getPLUGIN_PLUGIN_YML() {
        return PLUGIN_PLUGIN_YML;
    }
}
