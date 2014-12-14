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

package starnubserver.plugins.resources;

import java.io.File;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class YAMLFiles {

    private final ConcurrentHashMap<String, PluginYAMLWrapper> YAML_FILES = new ConcurrentHashMap<>();

    public ConcurrentHashMap<String, PluginYAMLWrapper> getYAML_FILES() {
        return YAML_FILES;
    }

    public HashSet<String> getFileNames(){
        return YAML_FILES.keySet().stream().collect(Collectors.toCollection(HashSet::new));
    }

    public HashSet<File> getFiles(){
        return YAML_FILES.values().stream().map(PluginYAMLWrapper::getDISK_FILE).collect(Collectors.toCollection(HashSet::new));
    }

    public PluginYAMLWrapper getYamlWrapper(String filename){
        return YAML_FILES.get(filename.toLowerCase());
    }

    public File getSpecificFile(String filename){
        return YAML_FILES.get(filename.toLowerCase()).getDISK_FILE();
    }

    public void addPluginYamlWrapper(PluginYAMLWrapper pluginYAMLWrapper){
        YAML_FILES.put(pluginYAMLWrapper.getFILE_NAME(), pluginYAMLWrapper);
    }

    public void addPluginYamlWrappersBulk(HashSet<PluginYAMLWrapper> pluginYAMLWrapperHashSet){
        for (PluginYAMLWrapper pluginYAMLWrapper : pluginYAMLWrapperHashSet){
            YAML_FILES.put(pluginYAMLWrapper.getFILE_NAME(), pluginYAMLWrapper);
        }
    }

    public void removePluginYamlWrapper(String filename){
        YAML_FILES.remove(filename);
    }
}
