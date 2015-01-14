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
import org.starnub.starnubserver.pluggable.exceptions.PluginDirectoryCreationFailed;
import org.starnub.starnubserver.pluggable.resources.PluginConfiguration;
import org.starnub.starnubserver.pluggable.resources.PluginYAMLWrapper;
import org.starnub.starnubserver.pluggable.resources.YAMLFiles;
import org.starnub.utilities.dircectories.DirectoryCheckCreate;
import org.starnub.utilities.file.utility.JarFromDisk;
import org.starnub.utilities.file.yaml.YAMLWrapper;
import org.starnub.utilities.file.yaml.YamlUtilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.jar.JarEntry;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public abstract class Plugin extends Pluggable {

    protected PluginConfiguration configuration;
    protected YAMLFiles files;
    protected HashSet<String> additionalPermissions;
    private boolean enabled;

    public Plugin() {
    }

    public PluginConfiguration getConfiguration() {
        return configuration;
    }

    public YAMLFiles getFiles() {
        return files;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void loadData(YAMLWrapper pluggableInfo) throws IOException, PluginDirectoryCreationFailed {
        type = PluggableType.PLUGIN;
        List<String> additionalPermissionList = (List<String>) pluggableInfo.getValue("additional_permissions");
        if (additionalPermissionList.size() > 0) {
            additionalPermissions = new HashSet<>();
            additionalPermissions.addAll(additionalPermissionList.stream().collect(Collectors.toList()));
        }
        boolean hasConfiguration = (boolean) pluggableInfo.getValue("has_configuration");
        if (hasConfiguration) {
            if(fileType == PluggableFileType.JAVA) {
                try (InputStream defaultPluginConfiguration = this.getClass().getClassLoader().getResourceAsStream("default_configuration.yml")) {
                    configuration = new PluginConfiguration(details.getNAME(), defaultPluginConfiguration);
                }
            } else if (fileType == PluggableFileType.PYTHON){
                PluggablePythonInterpreter pluggablePythonInterpreter = PluggablePythonInterpreter.getInstance();
                pluggablePythonInterpreter.loadPythonScript(file);
                PyObject defaultConfiguration = pluggablePythonInterpreter.getPyObject("default_configuration", false);
                Map<String, Object> defaultConfigurationMap = (Map<String, Object>) defaultConfiguration.__tojava__(ConcurrentMap.class);
                configuration = new PluginConfiguration(details.getNAME(), defaultConfigurationMap);
            }
        }
        if(fileType == PluggableFileType.JAVA){
            String pluginDir = PluggableManager.getInstance().getPLUGIN_DIRECTORY_STRING();
            String absolutePath = file.getAbsolutePath();
            JarFromDisk jarFromDisk = new JarFromDisk(absolutePath);
            List<JarEntry> otherJarFiles = jarFromDisk.getJarEntries("other_files/", null);
            List<JarEntry> yamlJarEntries = jarFromDisk.getJarEntries("yaml_files/", null);
            ArrayList<String> directories = new ArrayList<>();
            directories.addAll(otherJarFiles.stream().filter(ZipEntry::isDirectory).map(JarEntry::toString).collect(Collectors.toList()));
            directories.addAll(yamlJarEntries.stream().filter(ZipEntry::isDirectory).map(JarEntry::toString).collect(Collectors.toList()));
            createDirectories(pluginDir, details.getNAME(), directories);
            otherFilesExtractor(pluginDir, jarFromDisk, otherJarFiles);
            YAMLFiles yamlFiles = yamlFiles(details.getNAME(), pluginDir, this.getClass().getClassLoader(), yamlJarEntries, jarFromDisk);
            if (yamlFiles.getFiles().size() > 0){
                files = yamlFiles;
            }
        }
    }

    private void otherFilesExtractor(String PLUGIN_DIR, JarFromDisk jarFromDisk, List<JarEntry> otherJarFiles) throws PluginDirectoryCreationFailed, IOException {
        for (JarEntry jarEntry : otherJarFiles) {
            if (!jarEntry.isDirectory()) {
                jarFromDisk.extractEntry(jarEntry,  PLUGIN_DIR + jarEntry.toString());
            }
        }
    }

    private YAMLFiles yamlFiles(String PLUGIN_NAME, String PLUGIN_DIR, ClassLoader CLASS_LOADER, List<JarEntry> yamlJarEntries, JarFromDisk jarFromDisk) throws IOException, PluginDirectoryCreationFailed {
        HashSet<PluginYAMLWrapper> pluginYAMLWrapperHashSet = new HashSet<>();
        for (JarEntry jarEntry :yamlJarEntries) {
            if (!jarEntry.isDirectory()) {
                String fileName = jarFromDisk.getJarEntryFileName(jarEntry);
                try (InputStream resourceAsStream = CLASS_LOADER.getResourceAsStream(jarEntry.toString())) {
                    final PluginYAMLWrapper pluginYAMLWrapper = new PluginYAMLWrapper(PLUGIN_NAME, fileName, resourceAsStream, PLUGIN_DIR + jarEntry.toString());
                    pluginYAMLWrapperHashSet.add(pluginYAMLWrapper);
                }
            }
        }
        return new YAMLFiles(pluginYAMLWrapperHashSet);
    }

    public void enable(){
        onPluginEnable();
        this.enabled = true;
    }

    public void disable(){
        onPluginDisable();
        this.enabled = false;
    }

    protected void createDirectories(String PLUGIN_DIR, String PLUGIN_NAME, ArrayList<String> directories) throws PluginDirectoryCreationFailed {
        LinkedHashMap<String, Boolean> linkedHashMap = DirectoryCheckCreate.dirCheck(PLUGIN_DIR, directories);
        for (Map.Entry<String, Boolean> dirEntry : linkedHashMap.entrySet()){
            String dir = dirEntry.getKey();
            boolean success = dirEntry.getValue();
            if (success){
                System.out.println("StarNub directory " + dir + " exist or was successfully created.");
            } else {
                throw new PluginDirectoryCreationFailed("ERROR CREATING DIRECTORY \"" + dir + "\" FOR PLUGIN \"" + PLUGIN_NAME + "\" PLEASE CHECK FILE PERMISSIONS. " +
                        "CONSULT THE PLUGIN DEVELOPER FOR FURTHER HELP.");
            }
        }
    }

    @Override
    public void dumpDetails() throws IOException {
        String getPluginDirectory = PluggableManager.getInstance().getPLUGIN_DIRECTORY_STRING();
        YamlUtilities.toFileYamlDump(getPluginDetailsMap(), getPluginDirectory + details.getOWNER() + "/Information/");
    }

    public LinkedHashMap<String, Object> getPluginDetailsMap(){
        LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();
        String additionalPermissionsString = "";
        if(additionalPermissions == null){
            additionalPermissionsString = "None";
        } else {
            additionalPermissionsString = additionalPermissions.toString();
        }
        linkedHashMap.put("Additional Permissions", additionalPermissionsString);
        return linkedHashMap;
    }

    public abstract void onPluginEnable();
    public abstract void onPluginDisable();

    @Override
    public String toString() {
        return "Plugin{" +
                ", additionalPermissions=" + additionalPermissions +
                ", enabled=" + enabled +
                "} " + super.toString();
    }
}