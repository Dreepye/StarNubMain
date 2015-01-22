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

import org.starnub.starnubserver.StarNub;
import org.starnub.starnubserver.events.events.StarNubEvent;
import org.starnub.starnubserver.pluggable.exceptions.DirectoryCreationFailed;
import org.starnub.starnubserver.pluggable.resources.PluginYamlWrapper;
import org.starnub.starnubserver.pluggable.resources.YamlFiles;
import org.starnub.utilities.arrays.ArrayUtilities;
import org.starnub.utilities.dircectories.DirectoryCheckCreate;
import org.starnub.utilities.file.utility.JarFromDisk;
import org.starnub.utilities.file.yaml.YamlWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;

public abstract class Plugin extends Pluggable {

    protected YamlFiles files;
    protected String[] additionalPermissions;
    private boolean enabled;

    public Plugin() {
    }

    public YamlFiles getFiles() {
        return files;
    }

    public String[] getAdditionalPermissions() {
        return additionalPermissions;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void enable(){
        onEnable();
        new StarNubEvent("Plugin_Enabled", this);
        StarNub.getLogger().cInfoPrint("StarNub", details.getNameVersion() + " enabled.");
        this.enabled = true;
    }

    public void disable(){
        onDisable();
        new StarNubEvent("Plugin_Disabled", this);
        StarNub.getLogger().cInfoPrint("StarNub", details.getNameVersion() + " disabled.");
        this.enabled = false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void loadData(YamlWrapper pluggableInfo) throws IOException, DirectoryCreationFailed {
        List<String> additionalPermissionList = (List<String>) pluggableInfo.getValue("additional_permissions");
        additionalPermissions = ArrayUtilities.arrayBuilder(additionalPermissionList);
        if(fileType == PluggableFileType.JAVA){
            String pluginDir = PluggableManager.getInstance().getPLUGIN_DIRECTORY_STRING();
            String absolutePath = file.getAbsolutePath();
            JarFromDisk jarFromDisk = new JarFromDisk(absolutePath);
            List<JarEntry> otherJarFiles = jarFromDisk.getJarEntries("other_files/", null);
            List<JarEntry> yamlJarEntries = jarFromDisk.getJarEntries("yaml_files/", null);
            ArrayList<String> directories = new ArrayList<>();
//            directories.addAll(otherJarFiles.stream().filter(ZipEntry::isDirectory).map(JarEntry::toString).collect(Collectors.toList()));
//            directories.addAll(yamlJarEntries.stream().filter(ZipEntry::isDirectory).map(JarEntry::toString).collect(Collectors.toList()));
            createDirectories(details.getNAME(), pluginDir, directories);
            otherFilesExtractor(details.getNAME(), pluginDir, jarFromDisk, otherJarFiles);
            YamlFiles yamlFiles = yamlFiles(details.getNAME(), pluginDir, this.getClass().getClassLoader(), yamlJarEntries, jarFromDisk);
            if (yamlFiles.getFiles().size() > 0){
                files = yamlFiles;
            }
        }
    }

    private void otherFilesExtractor(String PLUGIN_NAME, String PLUGIN_DIR, JarFromDisk jarFromDisk, List<JarEntry> otherJarFiles) throws DirectoryCreationFailed, IOException {
        for (JarEntry jarEntry : otherJarFiles) {
            if (!jarEntry.isDirectory()) {
                jarFromDisk.extractEntry(jarEntry,  PLUGIN_DIR + PLUGIN_NAME);
            }
        }
    }

    private YamlFiles yamlFiles(String PLUGIN_NAME, String PLUGIN_DIR, ClassLoader CLASS_LOADER, List<JarEntry> yamlJarEntries, JarFromDisk jarFromDisk) throws IOException, DirectoryCreationFailed {
        HashSet<PluginYamlWrapper> pluginYAMLWrapperHashSet = new HashSet<>();
        for (JarEntry jarEntry :yamlJarEntries) {
            if (!jarEntry.isDirectory()) {
                String fileName = jarFromDisk.getJarEntryFileName(jarEntry);
                try (InputStream resourceAsStream = CLASS_LOADER.getResourceAsStream(jarEntry.toString())) {
                    final PluginYamlWrapper pluginYAMLWrapper = new PluginYamlWrapper(PLUGIN_NAME, fileName, resourceAsStream, PLUGIN_DIR + PLUGIN_NAME);
                    pluginYAMLWrapperHashSet.add(pluginYAMLWrapper);
                }
            }
        }
        return new YamlFiles(pluginYAMLWrapperHashSet);
    }

    protected void createDirectories(String PLUGIN_NAME, String PLUGIN_DIR, ArrayList<String> directories) throws DirectoryCreationFailed {
        LinkedHashMap<String, Boolean> linkedHashMap = DirectoryCheckCreate.dirCheck(PLUGIN_DIR + PLUGIN_NAME, directories);
        for (Map.Entry<String, Boolean> dirEntry : linkedHashMap.entrySet()){
            String dir = dirEntry.getKey();
            boolean success = dirEntry.getValue();
            if (success){
                StarNub.getLogger().cInfoPrint("StarNub", "StarNub directory " + dir + " exist or was successfully created.");
            } else {
                throw new DirectoryCreationFailed("ERROR CREATING DIRECTORY \"" + dir + "\" FOR PLUGIN \"" + PLUGIN_NAME + "\" PLEASE CHECK FILE PERMISSIONS. " +
                        "CONSULT THE PLUGIN DEVELOPER FOR FURTHER HELP.");
            }
        }
    }

    @Override
    public LinkedHashMap<String, Object> getDetailsMap() throws IOException {
        LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("Additional Permissions", additionalPermissions);
        return linkedHashMap;
    }

    public abstract void onEnable();
    public abstract void onDisable();
    public abstract void onRegister();

    @Override
    public String toString() {
        return "Plugin{" +
                ", additionalPermissions=" + Arrays.toString(additionalPermissions) +
                ", enabled=" + enabled +
                "} " + super.toString();
    }
}
