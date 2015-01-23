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
import org.starnub.starnubserver.pluggable.exceptions.DirectoryCreationFailed;
import org.starnub.starnubserver.pluggable.exceptions.MissingData;
import org.starnub.starnubserver.pluggable.generic.PluggableDetails;
import org.starnub.starnubserver.pluggable.resources.PluggableConfiguration;
import org.starnub.utilities.file.utility.FileSizeMeasure;
import org.starnub.utilities.file.utility.GetFileSize;
import org.starnub.utilities.file.yaml.YamlWrapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class UnloadedPluggable {

    private File file;
    private PluggableFileType fileType;
    private PluggableDetails details;
    private PluggableConfiguration configuration;

    private YamlWrapper yamlWrapper;
    private boolean updating;

    public UnloadedPluggable(File file) throws DirectoryCreationFailed, MissingData, IOException {
        this.file = file;
        load();
    }

    public Pluggable instantiatePluggable(PluggableType type) throws DirectoryCreationFailed, MissingData, IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, ClassCastException {
        String classString = details.getCLASS();
        Pluggable newClass = null;
        String baseDir = null;
        if (type == PluggableType.PLUGIN) {
            baseDir = PluggableManager.getInstance().getPLUGIN_DIRECTORY_STRING();
        } else if (type == PluggableType.COMMAND) {
            baseDir = PluggableManager.getInstance().getCOMMAND_DIRECTORY_STRING();
        }
        PluggableConfiguration localConfig = null;
        if (fileType == PluggableFileType.JAVA){
            URL pluginUrl = file.toURI().toURL();
            URLClassLoader classLoader = new URLClassLoader(new URL[]{pluginUrl}, StarNub.class.getClassLoader());
            Class<?> clazz = classLoader.loadClass(classString);
            if (type == PluggableType.PLUGIN){
                Class<? extends Pluggable> pluggableClass = clazz.asSubclass(Plugin.class);
                newClass = pluggableClass.newInstance();
            } else if (type == PluggableType.COMMAND){
                Class<? extends Pluggable> pluggableClass = clazz.asSubclass(Command.class);
                newClass = pluggableClass.newInstance();
            }
            try (InputStream defaultPluginConfiguration = classLoader.getResourceAsStream("default_configuration.yml")) {
                if (defaultPluginConfiguration != null) {
                    localConfig = new PluggableConfiguration(baseDir, details.getNAME(), defaultPluginConfiguration);
                }
            }
        } else if (fileType == PluggableFileType.PYTHON){
            PluggablePythonInterpreter pluggablePythonInterpreter = PluggablePythonInterpreter.getInstance();
            pluggablePythonInterpreter.loadPythonScript(file);
            PyObject pyObject = pluggablePythonInterpreter.getPyObject(classString, false);
            PyObject pluggableObject = pyObject.__call__();
            if (type == PluggableType.PLUGIN){
                newClass = (Pluggable) pluggableObject.__tojava__(Plugin.class);
            } else if (type == PluggableType.COMMAND){
                newClass = (Pluggable) pluggableObject.__tojava__(Command.class);
            }
            PyObject defaultConfiguration = pluggablePythonInterpreter.getPyObject("default_configuration", false);
            if(defaultConfiguration != null) {
                Map<String, Object> defaultConfigurationMap = (Map<String, Object>) defaultConfiguration.__tojava__(ConcurrentMap.class);
                if (defaultConfigurationMap != null) {
                    localConfig = new PluggableConfiguration(baseDir, details.getNAME(), defaultConfigurationMap);
                }
            }
        }
        if(localConfig != null && !localConfig.getDATA().isEmpty()){
            configuration = localConfig;
        }


        if (newClass != null){
            newClass.setPluggable(this);
        }
        return newClass;
    }

    public File getFile() {
        return file;
    }

    public PluggableFileType getFileType() {
        return fileType;
    }

    public PluggableDetails getDetails() {
        return details;
    }

    public PluggableConfiguration getConfiguration() {
        return configuration;
    }

    public YamlWrapper getYamlWrapper() {
        return yamlWrapper;
    }

    public boolean isUpdating() {
        return updating;
    }

    public void setUpdating() {
        this.updating = true;
    }

    @SuppressWarnings("unchecked")
    public void load() throws MissingData, IOException, DirectoryCreationFailed {
        String absolutePath = file.getAbsolutePath();
        if (absolutePath.endsWith(".py")){
            fileType = PluggableFileType.PYTHON;

        } else if (absolutePath.endsWith(".jar")){
            fileType = PluggableFileType.JAVA;
        }
        Object pluggableInfoObject = findPluggableInfo();
        if (pluggableInfoObject == null){
            return;
        }
        yamlWrapper = new YamlWrapper(null, "StarNub", "StarNub - Plugin Loader", pluggableInfoObject, "");
        String pluggableName;
        PluggableType type;
        if (yamlWrapper.hasKey("name")){
            pluggableName = (String) yamlWrapper.getValue("name");
            type = PluggableType.PLUGIN;
            yamlWrapperCheck(pluggableName, new String[]{"additional_permissions"});
        } else if (yamlWrapper.hasKey("command")){
            pluggableName = (String) yamlWrapper.getValue("command");
            type = PluggableType.COMMAND;
            yamlWrapperCheck(pluggableName, new String[]{"main_args", "custom_split", "can_use"});
        } else {
            throw new MissingData("Cannot find 'name' or 'command' key for Pluggable: " + file);
        }
        String ownerString = "owner";
        String classString = "class";
        String versionString = "version";
        String authorString = "author";
        String urlString = "url";
        String descriptionString = "description";
        String dependencies = "dependencies";
        String unloadableString = "unloadable";
        yamlWrapperCheck(pluggableName, new String[]{ownerString, classString, versionString, authorString, urlString, descriptionString, dependencies, unloadableString});
        String pluggableOwner = (String) yamlWrapper.getValue(ownerString);
        String classPath = (String) yamlWrapper.getValue(classString);
        double version = (double) yamlWrapper.getNestedValue(versionString);
        double fileSize = GetFileSize.getFileSize(file, FileSizeMeasure.KILOBYTES);
        String author = (String) yamlWrapper.getNestedValue(authorString);
        String url = (String) yamlWrapper.getNestedValue(urlString);
        String description = (String) yamlWrapper.getNestedValue(descriptionString);
        List<String> dependenciesList = (List<String>) yamlWrapper.getValue(dependencies);
        boolean unloadable = (boolean) yamlWrapper.getValue(unloadableString);
        details = new PluggableDetails(pluggableOwner, pluggableName, classPath, type, version, fileSize, author, url, description, dependenciesList, unloadable);
    }

    private void yamlWrapperCheck(String pluggableName, String[] keys) throws MissingData {
        for(String key : keys){
            if(!yamlWrapper.hasKey(key)){
                throw new MissingData("Pluggable: \"" + pluggableName + "\" Manifest Error. Missing Key: \"" + key+"\".");
            }
        }
    }

    private Object findPluggableInfo() throws MissingData, MalformedURLException {
        if (fileType == PluggableFileType.JAVA){
            URL pluginUrl = file.toURI().toURL();
            URLClassLoader classLoader = new URLClassLoader(new URL[]{pluginUrl}, StarNub.class.getClassLoader());
            return classLoader.getResourceAsStream("pluggable_info.yml");
        } else if (fileType == PluggableFileType.PYTHON) {
            PluggablePythonInterpreter interpreter = PluggablePythonInterpreter.getInstance();
            interpreter.loadPythonScript(file);
            PyObject pluggableInfo = interpreter.getPyObject("pluggable_info", false);
            if (pluggableInfo == null){
                return null;
            }
            return pluggableInfo.__tojava__(ConcurrentMap.class);
        } else {
            throw new MissingData("Unknown error loading pluggable info.");
        }
    }

    @Override
    public String toString() {
        return "UnloadedPluggable{" +
                "details=" + details +
                ", file=" + file +
                ", fileType=" + fileType +
                '}';
    }
}
