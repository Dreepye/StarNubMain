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

package org.starnub.starnubserver.pluggable;

import org.python.core.PyObject;
import org.starnub.starnubserver.StarNub;
import org.starnub.starnubserver.pluggable.exceptions.MissingData;
import org.starnub.starnubserver.pluggable.exceptions.PluginDirectoryCreationFailed;
import org.starnub.starnubserver.pluggable.generic.PluggableDetails;
import org.starnub.utilities.file.utility.FileSizeMeasure;
import org.starnub.utilities.file.utility.GetFileSize;
import org.starnub.utilities.file.yaml.YAMLWrapper;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.ConcurrentMap;

public abstract class Pluggable<T> extends File {

    protected PluggableDetails pluggableDetails;
    protected PluggableType pluginType;

    /**
     * Creates a new <code>File</code> instance by converting the given
     * pathname string into an abstract pathname.  If the given string is
     * the empty string, then the result is the empty abstract pathname.
     *
     * @param pathname A pathname string
     * @throws NullPointerException If the <code>pathname</code> argument is <code>null</code>
     */
    public Pluggable(String pathname) throws MissingData, IOException, PluginDirectoryCreationFailed {
        super(pathname);
        load();
    }

    /**
     * Creates a new <tt>File</tt> instance by converting the given
     * <tt>file:</tt> URI into an abstract pathname.
     * <p>
     * <p> The exact form of a <tt>file:</tt> URI is system-dependent, hence
     * the transformation performed by this constructor is also
     * system-dependent.
     * <p>
     * <p> For a given abstract pathname <i>f</i> it is guaranteed that
     * <p>
     * <blockquote><tt>
     * new File(</tt><i>&nbsp;f</i><tt>.{@link #toURI() toURI}()).equals(</tt><i>&nbsp;f</i><tt>.{@link #getAbsoluteFile() getAbsoluteFile}())
     * </tt></blockquote>
     * <p>
     * so long as the original abstract pathname, the URI, and the new abstract
     * pathname are all created in (possibly different invocations of) the same
     * Java virtual machine.  This relationship typically does not hold,
     * however, when a <tt>file:</tt> URI that is created in a virtual machine
     * on one operating system is converted into an abstract pathname in a
     * virtual machine on a different operating system.
     *
     * @param uri An absolute, hierarchical URI with a scheme equal to
     *            <tt>"file"</tt>, a non-empty path component, and undefined
     *            authority, query, and fragment components
     * @throws NullPointerException     If <tt>uri</tt> is <tt>null</tt>
     * @throws IllegalArgumentException If the preconditions on the parameter do not hold
     * @see #toURI()
     * @see java.net.URI
     * @since 1.4
     */
    public Pluggable(URI uri) throws MissingData, IOException, PluginDirectoryCreationFailed {
        super(uri);
        load();
    }

    public PluggableDetails getPluggableDetails() {
        return pluggableDetails;
    }

    public PluggableType getPluginType() {
        return pluginType;
    }

    public void load() throws MissingData, IOException, PluginDirectoryCreationFailed {
        if (this.getAbsolutePath().endsWith(".py")){
            pluginType = PluggableType.PYTHON;
        } else if (this.getAbsolutePath().endsWith(".jar")){
            pluginType = PluggableType.JAVA;
        }
        Object pluggableInfoObject = findPluggableInfo();
        YAMLWrapper pluggableInfoYaml = new YAMLWrapper(null, "StarNub", "StarNub - Plugin Loader", pluggableInfoObject, "");
        loadData(pluggableInfoYaml);
    }

    public void loadData(YAMLWrapper pluggableInfo) throws MissingData, IOException, PluginDirectoryCreationFailed {
        String pluggableName = (String) pluggableInfo.getValue("name");
        String classPath = (String) pluggableInfo.getValue("class");
        double version = (double) pluggableInfo.getNestedValue("details", "version");
        double fileSize = GetFileSize.getFileSize(this, FileSizeMeasure.KILOBYTES);
        String author = (String) pluggableInfo.getNestedValue("details", "author");
        String url = (String) pluggableInfo.getNestedValue("details", "url");
        String description = (String) pluggableInfo.getNestedValue("details", "description");
        pluggableDetails = new PluggableDetails(pluggableName, classPath, version, fileSize, author, url, description);
    }

    private Object findPluggableInfo() throws MissingData, MalformedURLException {
        if (pluginType == PluggableType.JAVA){
            URL pluginUrl = this.toURI().toURL();
            URLClassLoader classLoader = new URLClassLoader(new URL[]{pluginUrl}, StarNub.class.getClassLoader());
            return classLoader.getResourceAsStream("pluggable_info.yml");
        } else if (pluginType == PluggableType.PYTHON) {
            PythonInterpreter interpreter = PythonInterpreter.getInstance();
            interpreter.loadPythonScript(this);
            PyObject pluggable_info = interpreter.getPyObject("pluggable_info", false);
            return (ConcurrentMap<String, Object>) pluggable_info.__tojava__(ConcurrentMap.class);
        } else {
            throw new MissingData("Unknown error loading pluggable info.");
        }
    }

    public void instantiate() throws MissingData, IOException, PluginDirectoryCreationFailed {
        load();
        if (pluginType == PluggableType.JAVA){




        } else if (pluginType == PluggableType.PYTHON){


        }

    }


    public abstract void dumpDetails() throws IOException;

    @Override
    public String toString() {
        return "Pluggable{" +
                "pluggableDetails=" + pluggableDetails +
                ", pluginType=" + pluginType +
                "} " + super.toString();
    }
}
