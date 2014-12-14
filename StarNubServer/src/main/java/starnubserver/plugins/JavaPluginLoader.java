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
import starnubserver.plugins.resources.PluginYAMLWrapper;
import starnubserver.plugins.resources.YAMLFiles;
import starnubserver.resources.files.PluginConfiguration;
import utilities.dircectories.DirectoryCheckCreate;
import utilities.exceptions.PluginAlreadyLoaded;
import utilities.exceptions.PluginDependencyLoadFailed;
import utilities.exceptions.PluginDependencyNotFound;
import utilities.exceptions.PluginDirectoryCreationFailed;
import utilities.file.utility.JarFromDisk;
import utilities.file.yaml.YAMLWrapper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

/**
* Represents Java Plugin Loader.
* <p>
* This enum singleton holds and runs all things important
* to loading Java Plugins
* <p>
* @author Daniel (Underbalanced) (www.StarNub.org)
* @since 1.0
*/
public class JavaPluginLoader {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final JavaPluginLoader instance = new JavaPluginLoader();
    private static final PluginManager PLUGIN_MANAGER = PluginManager.getInstance();
    private static final CommandLoader COMMAND_LOADER = CommandLoader.getInstance();

    /**
     * This constructor is private - Singleton Pattern
     */
    private JavaPluginLoader() {
    }

    public static JavaPluginLoader getInstance() {
        return instance;
    }

    public JavaPlugin pluginLoader(UnloadedPlugin unloadedPlugin) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, PluginAlreadyLoaded, PluginDependencyNotFound, PluginDependencyLoadFailed, PluginDirectoryCreationFailed, IOException {
        final String PLUGIN_NAME = unloadedPlugin.getPLUGIN_NAME();
        final Map DATA = unloadedPlugin.getPLUGIN_PLUGIN_YML().getDATA();
        final URLClassLoader CLASS_LOADER = unloadedPlugin.getPLUGIN_URL_CLASS_LOADER();
        final File PLUGIN_FILE_PATH = unloadedPlugin.getPLUGIN_FILE();
        final String PLUGIN_TO_LOAD_NAME = (String) DATA.get("name");

        final YAMLWrapper PLUGIN_MANIFEST = unloadedPlugin.getPLUGIN_PLUGIN_YML();
        final String MAIN_CLASS = (String) PLUGIN_MANIFEST.getValue("class");
        final boolean LIVE_UPDATE = (boolean) PLUGIN_MANIFEST.getNestedValue("details", "live_update");
        final List<String> LANGUAGE = (List<String>) PLUGIN_MANIFEST.getNestedValue("details", "languages");
        final double VERSION = (double) PLUGIN_MANIFEST.getNestedValue("details", "versions");
        final String DESCRIPTION = (String) PLUGIN_MANIFEST.getNestedValue("details", "description");
        final List<String> DEPENDENCIES = (List<String>) PLUGIN_MANIFEST.getNestedValue("details", "dependencies");
        final String AUTHOR = (String) PLUGIN_MANIFEST.getNestedValue("details", "author");
        final String URL = (String) PLUGIN_MANIFEST.getNestedValue("details", "url");
        final boolean HAS_CONFIGURATION = (boolean) PLUGIN_MANIFEST.getValue("configuration");
        final boolean HAS_COMMANDS = (boolean) PLUGIN_MANIFEST.getNestedValue("commands", "has");
        final String COMMAND_NAME = (String) PLUGIN_MANIFEST.getNestedValue("commands", "name");
        final String COMMAND_ALIAS = (String) PLUGIN_MANIFEST.getNestedValue("commands", "alias");
        final boolean HAS_RUNNABLES = (boolean) PLUGIN_MANIFEST.getNestedValue("runnables", "has");
        final List<String> RUNNABLE_CLASSES = (List<String>) PLUGIN_MANIFEST.getNestedValue("runnables", "runnables");
        final List<String> ADDITIONAL_PERMISSIONS = (List<String>) PLUGIN_MANIFEST.getValue("class");

        dependencyLoader(DEPENDENCIES);

        final String PLUGIN_DIR = "StarNub/Plugins/" + PLUGIN_NAME + "/";

        JarFromDisk jarFromDisk = new JarFromDisk(PLUGIN_FILE_PATH);
        List<JarEntry> otherJarFiles = jarFromDisk.getJarEntries("other_files/", null);

        otherFilesExtractor(PLUGIN_NAME, PLUGIN_DIR, jarFromDisk, otherJarFiles);

        List<JarEntry> yamlJarEntries = jarFromDisk.getJarEntries("yaml_files/", null);
        HashSet<PluginYAMLWrapper> pluginYAMLWrapperHashSet = new HashSet<>();
        for (JarEntry jarEntry : yamlJarEntries) {
            String fileName = jarFromDisk.getJarEntryFileName(jarEntry);
            final PluginYAMLWrapper pluginYAMLWrapper = new PluginYAMLWrapper(PLUGIN_NAME, fileName, CLASS_LOADER.getResourceAsStream(fileName), PLUGIN_DIR + jarEntry.toString());
            pluginYAMLWrapperHashSet.add(pluginYAMLWrapper);
        }

        List<JarEntry> commandsJarEntries = jarFromDisk.getJarEntries("commands/", null);

        final PluginDetails DETAILS = new PluginDetails();
        final PluginConfiguration CONFIGURATION = new PluginConfiguration();
        final YAMLFiles YAML_FILES = yamlFiles(PLUGIN_NAME, PLUGIN_DIR, CLASS_LOADER, jarFromDisk);
        final CommandInfo COMMAND_INFO = new CommandInfo();
        final PluginRunnables RUNNABLES = new PluginRunnables();


        final Class<?> PLUGIN_CLASS = CLASS_LOADER.loadClass(MAIN_CLASS);
        final Constructor CONSTRUCTOR = PLUGIN_CLASS.getConstructor(new Class[]{String.class, String.class, String.class, PluginDetails.class, PluginConfiguration.class, YAMLFiles.class, CommandInfo.class, PluginRunnables.class});


        return (JavaPlugin) CONSTRUCTOR.newInstance(new Object[]{});
    }



    private void dependencyLoader(List<String> dependencies) throws PluginDependencyNotFound, PluginDependencyLoadFailed, PluginDirectoryCreationFailed {
        for (String dependency : dependencies) {
            boolean isDependencyLoaded = PLUGIN_MANAGER.isPluginLoaded(dependency, true);
            if (!isDependencyLoaded) {
                boolean isPluginUnloaded = PLUGIN_MANAGER.isPluginUnloaded(dependency, true);
                if (isPluginUnloaded) {
                    try {
                        PLUGIN_MANAGER.loadSpecificPlugin(dependency, false, true);
                    } catch (IOException| NoSuchMethodException | IllegalAccessException | PluginAlreadyLoaded | InstantiationException | ClassNotFoundException | InvocationTargetException e) {
                        throw new PluginDependencyLoadFailed(e.getMessage());
                    }
                } else {
                    throw new PluginDependencyNotFound(dependency);
                }
            }
        }
    }

    private void otherFilesExtractor(String PLUGIN_NAME, String PLUGIN_DIR, JarFromDisk jarFromDisk, List<JarEntry> otherJarFiles) throws PluginDirectoryCreationFailed, IOException {
        ArrayList<String> directories = new ArrayList<>();
        directories.addAll(otherJarFiles.stream().filter(ZipEntry::isDirectory).map(JarEntry::toString).collect(Collectors.toList()));
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
        for (JarEntry jarEntry : otherJarFiles) {
            if (!jarEntry.isDirectory()) {
                jarFromDisk.extractEntry(jarEntry,  PLUGIN_DIR + jarEntry.toString());
            }
        }
    }

    private YAMLFiles yamlFiles(String PLUGIN_NAME, String PLUGIN_DIR, URLClassLoader CLASS_LOADER, JarFromDisk jarFromDisk){
        List<JarEntry> yamlJarEntries = jarFromDisk.getJarEntries("yaml_files/", null);
        HashSet<PluginYAMLWrapper> pluginYAMLWrapperHashSet = new HashSet<>();
        for (JarEntry jarEntry : yamlJarEntries) {
            String fileName = jarFromDisk.getJarEntryFileName(jarEntry);
            final PluginYAMLWrapper pluginYAMLWrapper = new PluginYAMLWrapper(PLUGIN_NAME, fileName, CLASS_LOADER.getResourceAsStream(fileName), PLUGIN_DIR + jarEntry.toString());
            pluginYAMLWrapperHashSet.add(pluginYAMLWrapper);
        }
        return new YAMLFiles(pluginYAMLWrapperHashSet);
    }
}




