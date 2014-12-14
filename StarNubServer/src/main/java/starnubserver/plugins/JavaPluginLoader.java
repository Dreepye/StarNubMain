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
import starnubserver.plugins.resources.StarNubRunnable;
import starnubserver.plugins.resources.YAMLFiles;
import starnubserver.resources.files.PluginConfiguration;
import utilities.dircectories.DirectoryCheckCreate;
import utilities.exceptions.*;
import utilities.file.utility.FileSizeMeasure;
import utilities.file.utility.GetFileSize;
import utilities.file.utility.JarFromDisk;
import utilities.file.yaml.YAMLWrapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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

    public JavaPluginLoader() {
    }

    public static JavaPluginLoader getInstance() {
        return instance;
    }

    public JavaPlugin pluginLoader(UnloadedPlugin unloadedPlugin) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, PluginAlreadyLoaded, PluginDependencyNotFound, PluginDependencyLoadFailed, PluginDirectoryCreationFailed, IOException, CommandClassLoadFail, CommandYamlLoadFailed {
        final String PLUGIN_NAME = unloadedPlugin.getPLUGIN_NAME();
        final URLClassLoader CLASS_LOADER = unloadedPlugin.getPLUGIN_URL_CLASS_LOADER();
        final File PLUGIN_FILE_PATH = unloadedPlugin.getPLUGIN_FILE();

        final YAMLWrapper PLUGIN_MANIFEST = unloadedPlugin.getPLUGIN_PLUGIN_YML();
        final String MAIN_CLASS = (String) PLUGIN_MANIFEST.getValue("class");
        final List<String> LANGUAGE = (List<String>) PLUGIN_MANIFEST.getNestedValue("details", "languages");
        final double VERSION = (double) PLUGIN_MANIFEST.getNestedValue("details", "version");
        final String DESCRIPTION = (String) PLUGIN_MANIFEST.getNestedValue("details", "description");
        final List<String> DEPENDENCIES = (List<String>) PLUGIN_MANIFEST.getNestedValue("details", "dependencies");
        final String AUTHOR = (String) PLUGIN_MANIFEST.getNestedValue("details", "author");
        final String URL = (String) PLUGIN_MANIFEST.getNestedValue("details", "url");
        final boolean HAS_CONFIGURATION = (boolean) PLUGIN_MANIFEST.getValue("configuration");
        final String COMMAND_NAME = (String) PLUGIN_MANIFEST.getNestedValue("commands", "name");
        final String COMMAND_ALIAS = (String) PLUGIN_MANIFEST.getNestedValue("commands", "alias");
        final List<String> RUNNABLE_CLASSES = (List<String>) PLUGIN_MANIFEST.getNestedValue("runnables", "runnables");
        final List<String> ADDITIONAL_PERMISSIONS = (List<String>) PLUGIN_MANIFEST.getValue("additional_permissions");

        dependencyLoader(DEPENDENCIES);

        final String PLUGIN_DIR = "StarNub/Plugins/" + PLUGIN_NAME + "/";

        JarFromDisk jarFromDisk = new JarFromDisk(PLUGIN_FILE_PATH);
        List<JarEntry> otherJarFiles = jarFromDisk.getJarEntries("other_files/", null);

        otherFilesExtractor(PLUGIN_NAME, PLUGIN_DIR, jarFromDisk, otherJarFiles);

        final double FILE_SIZE = GetFileSize.getFileSize(PLUGIN_FILE_PATH, FileSizeMeasure.KILOBYTES);

        final HashSet<String> DEPENDENCY_HASHSET = new HashSet<>();
        DEPENDENCY_HASHSET.addAll(DEPENDENCIES);

        final HashSet<String> LANGUAGE_HASHSET = new HashSet<>();
        LANGUAGE_HASHSET.addAll(LANGUAGE);

        final HashSet<String> PERMISSIONS_HASHSET = new HashSet<>();
        PERMISSIONS_HASHSET.addAll(ADDITIONAL_PERMISSIONS);

        final PluginDetails DETAILS = new PluginDetails(VERSION, FILE_SIZE, DEPENDENCY_HASHSET, LANGUAGE_HASHSET, AUTHOR, URL, PERMISSIONS_HASHSET, DESCRIPTION);

        PluginConfiguration CONFIGURATION = null;
        if (HAS_CONFIGURATION) {
            try (InputStream defaultPluginConfiguration = CLASS_LOADER.getResourceAsStream("default_plugin_configuration.yml")) {
                CONFIGURATION = new PluginConfiguration(PLUGIN_NAME, defaultPluginConfiguration);
            }
        }

        final YAMLFiles YAML_FILES = yamlFiles(PLUGIN_NAME, PLUGIN_DIR, CLASS_LOADER, jarFromDisk);

        PluginRunnables RUNNABLES = null;
        if (RUNNABLE_CLASSES.size() > 0) {
            final ConcurrentHashMap<Thread, StarNubRunnable> runnables = runnables(RUNNABLE_CLASSES, PLUGIN_NAME, CLASS_LOADER);
            RUNNABLES = new PluginRunnables(runnables);
        }

        List<JarEntry> commandsJarEntries = jarFromDisk.getJarEntries("commands/", ".yml");
        CommandInfo COMMAND_INFO = null;
        if(commandsJarEntries.size() > 1) {
            HashSet<Command> commands = commandsPackage(PLUGIN_NAME, COMMAND_NAME, CLASS_LOADER, commandsJarEntries);
            COMMAND_INFO = new CommandInfo(COMMAND_NAME, COMMAND_ALIAS, commands);
        }

        Class<?> pluginClass = CLASS_LOADER.loadClass(MAIN_CLASS);
        final Class<? extends JavaPlugin> JAVA_PLUGIN_CLASS = pluginClass.asSubclass(JavaPlugin.class);
        final Constructor CONSTRUCTOR = JAVA_PLUGIN_CLASS.getConstructor(new Class[]{String.class, File.class, String.class, PluginDetails.class, PluginConfiguration.class, YAMLFiles.class, CommandInfo.class, PluginRunnables.class});
        return (JavaPlugin) CONSTRUCTOR.newInstance(new Object[]{PLUGIN_NAME, PLUGIN_FILE_PATH, MAIN_CLASS, DETAILS, CONFIGURATION, YAML_FILES, COMMAND_INFO, RUNNABLES});
    }

    private void dependencyLoader(List<String> dependencies) throws PluginDependencyNotFound, PluginDependencyLoadFailed, PluginDirectoryCreationFailed, CommandClassLoadFail, CommandYamlLoadFailed {
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

    private ConcurrentHashMap<Thread, StarNubRunnable> runnables(List<String> RUNNABLE_CLASSES, String PLUGIN_NAME, URLClassLoader CLASS_LOADER) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        int threadCount = 0;
        final ConcurrentHashMap<Thread, StarNubRunnable> runnables = new ConcurrentHashMap<>();
        for (String classString : RUNNABLE_CLASSES) {
            String name = "Plugin - " + PLUGIN_NAME +" : Class - " +classString.substring(classString.lastIndexOf(".") + 1) + " : Thread - " + threadCount;
            threadCount++;
            Class<?> javaClass = CLASS_LOADER.loadClass(classString);
            Class<? extends StarNubRunnable> starNubRunnableClass = javaClass.asSubclass(StarNubRunnable.class);
            StarNubRunnable starNubRunnable = starNubRunnableClass.newInstance();
            Thread starnubThread = new Thread(starNubRunnable, name);
            runnables.put(starnubThread, starNubRunnable);
        }
        return runnables;
    }

    private HashSet<Command> commandsPackage(String PLUGIN_NAME, String PLUGIN_COMMAND_NAME, URLClassLoader CLASS_LOADER, List<JarEntry> commandJarEntries) throws CommandYamlLoadFailed, CommandClassLoadFail {
        final HashSet<Command> commandHashSet = new HashSet<>();
        HashSet<String> loaded = new HashSet<>();
        for (JarEntry jarEntry : commandJarEntries) {
            String jarEntryPath = jarEntry.toString();
            final YAMLWrapper COMMAND_FILE;
            try {
                try (InputStream resourceAsStream = CLASS_LOADER.getResourceAsStream(jarEntryPath)) {
                    COMMAND_FILE = new YAMLWrapper(PLUGIN_NAME, jarEntryPath, resourceAsStream, "");
                }
            } catch (IOException e) {
                throw new CommandYamlLoadFailed(e.getMessage());
            }
            final String COMMAND_CLASS = (String) COMMAND_FILE.getValue("class");
            if (!loaded.contains(COMMAND_CLASS)) {
                List<String> commands = (List<String>) COMMAND_FILE.getValue("commands");
                HashSet<String> COMMANDS_HASHSET = new HashSet<>();
                COMMANDS_HASHSET.addAll(commands);
                List<String> main_args = (List<String>) COMMAND_FILE.getValue("main_args");
                HashSet<String> MAIN_ARGS_HASHSET = new HashSet<>();
                MAIN_ARGS_HASHSET.addAll(main_args);
                final int CAN_USE = (int) COMMAND_FILE.getValue("can_use");
                final String DESCRIPTION = (String) COMMAND_FILE.getValue("description");
                try {
                    Class<?> commandClass = CLASS_LOADER.loadClass(COMMAND_CLASS);
                    final Class<? extends JavaPlugin> JAVA_COMMAND_CLASS = commandClass.asSubclass(JavaPlugin.class);
                    final Constructor CONSTRUCTOR = JAVA_COMMAND_CLASS.getConstructor(new Class[]{HashSet.class, HashSet.class, String.class, String.class, Integer.class, String.class});
                    Command command = (Command) CONSTRUCTOR.newInstance(new Object[]{COMMANDS_HASHSET, MAIN_ARGS_HASHSET, COMMAND_CLASS, PLUGIN_COMMAND_NAME, CAN_USE, DESCRIPTION});
                    commandHashSet.add(command);
                    loaded.add(COMMAND_CLASS);
                } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                    throw new CommandClassLoadFail(e.getMessage());
                }
            }
        }
        return commandHashSet;
    }
}




