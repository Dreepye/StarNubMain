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
import org.starnub.starnubserver.pluggable.exceptions.NotAPluggable;
import org.starnub.starnubserver.pluggable.exceptions.PluginDirectoryCreationFailed;
import org.starnub.starnubserver.pluggable.generic.PluggableDetails;
import org.starnub.utilities.file.utility.FileSizeMeasure;
import org.starnub.utilities.file.utility.GetFileSize;
import org.starnub.utilities.file.yaml.YAMLWrapper;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public class UnloadedPluggable {

    private PluggableDetails pluggableDetails;
    private File pluggableFile;
    private PluggableFileType pluggableFileType;
    private YAMLWrapper yamlWrapper;
    private boolean updating;

    public UnloadedPluggable(File pluggableFile) throws PluginDirectoryCreationFailed, MissingData, IOException, NotAPluggable {
        this.pluggableFile = pluggableFile;
        load();
    }

    public Pluggable instantiatePluggable(PluggableType type) throws PluginDirectoryCreationFailed, MissingData, IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        String classString = pluggableDetails.getCLASS();
        Pluggable newClass = null;
        if (pluggableFileType == PluggableFileType.JAVA){
            URL pluginUrl = pluggableFile.toURI().toURL();
            URLClassLoader classLoader = new URLClassLoader(new URL[]{pluginUrl}, StarNub.class.getClassLoader());
            Class<?> clazz = classLoader.loadClass(classString);
            if (type == PluggableType.PLUGIN){
                Class<? extends Pluggable> pluggableClass = clazz.asSubclass(Plugin.class);
                newClass = pluggableClass.newInstance();
            } else if (type == PluggableType.COMMAND){
                Class<? extends Pluggable> pluggableClass = clazz.asSubclass(Command.class);
                newClass = pluggableClass.newInstance();
            }
        } else if (pluggableFileType == PluggableFileType.PYTHON){
            PluggablePythonInterpreter pluggablePythonInterpreter = PluggablePythonInterpreter.getInstance();
            pluggablePythonInterpreter.loadPythonScript(pluggableFile);
            PyObject pyObject = pluggablePythonInterpreter.getPyObject(classString, false);
            PyObject pluggableObject = pyObject.__call__();
            if (type == PluggableType.PLUGIN){
                newClass = (Pluggable) pluggableObject.__tojava__(Plugin.class);
            } else if (type == PluggableType.COMMAND){
                newClass = (Pluggable) pluggableObject.__tojava__(Command.class);
            }
        }
        if (newClass != null){
            newClass.setPluggable(this);
        }
        return newClass;
    }

    public PluggableDetails getPluggableDetails() {
        return pluggableDetails;
    }

    public File getPluggableFile() {
        return pluggableFile;
    }

    public PluggableFileType getPluggableFileType() {
        return pluggableFileType;
    }

    public YAMLWrapper getYamlWrapper() {
        return yamlWrapper;
    }

    public boolean isUpdating() {
        return updating;
    }

    public void setUpdating() {
        this.updating = true;
    }

    public void load() throws MissingData, IOException, PluginDirectoryCreationFailed, NotAPluggable {
        String absolutePath = pluggableFile.getAbsolutePath();
        if (absolutePath.endsWith(".py")){
            pluggableFileType = PluggableFileType.PYTHON;
        } else if (absolutePath.endsWith(".jar")){
            pluggableFileType = PluggableFileType.JAVA;
        }
        Object pluggableInfoObject = findPluggableInfo();
        if (pluggableInfoObject == null){
            return;
        }
        yamlWrapper = new YAMLWrapper(null, "StarNub", "StarNub - Plugin Loader", pluggableInfoObject, "");
        String pluggableOwner = (String) yamlWrapper.getValue("owner");
        String pluggableName = (String) yamlWrapper.getValue("name");
        String classPath = (String) yamlWrapper.getValue("class");
        double version = (double) yamlWrapper.getNestedValue("version");
        double fileSize = GetFileSize.getFileSize(pluggableFile, FileSizeMeasure.KILOBYTES);
        String author = (String) yamlWrapper.getNestedValue("author");
        String url = (String) yamlWrapper.getNestedValue("url");
        String description = (String) yamlWrapper.getNestedValue("description");
        List<String> dependenciesList = null;
        if (yamlWrapper.hasKey("dependencies")){
            dependenciesList = (List<String>) yamlWrapper.getValue("dependencies");
        }
        pluggableDetails = new PluggableDetails(pluggableOwner, pluggableName, classPath, version, fileSize, author, url, description, dependenciesList);
    }

    private Object findPluggableInfo() throws MissingData, MalformedURLException {
        if (pluggableFileType == PluggableFileType.JAVA){
            URL pluginUrl = pluggableFile.toURI().toURL();
            URLClassLoader classLoader = new URLClassLoader(new URL[]{pluginUrl}, StarNub.class.getClassLoader());
            return classLoader.getResourceAsStream("pluggable_info.yml");
        } else if (pluggableFileType == PluggableFileType.PYTHON) {
            PluggablePythonInterpreter interpreter = PluggablePythonInterpreter.getInstance();
            interpreter.loadPythonScript(pluggableFile);
            PyObject pluggableInfo = interpreter.getPyObject("pluggable_info", false);
            if (pluggableInfo == null){
                return null;
            }
            return (ConcurrentMap<String, Object>) pluggableInfo.__tojava__(ConcurrentMap.class);
        } else {
            throw new MissingData("Unknown error loading pluggable info.");
        }
    }

    @Override
    public String toString() {
        return "UnloadedPluggable{" +
                "details=" + pluggableDetails +
                ", file=" + pluggableFile +
                ", fileType=" + pluggableFileType +
                '}';
    }
}
