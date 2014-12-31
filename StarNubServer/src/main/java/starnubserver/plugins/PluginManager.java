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

import org.apache.commons.io.FileUtils;
import starnubserver.StarNub;
import starnubserver.events.events.StarNubEvent;
import starnubserver.plugins.exceptions.*;
import starnubserver.plugins.generic.CommandInfo;
import starnubserver.resources.StarNubYamlWrapper;
import utilities.strings.StringUtilities;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
* Represents the PluginManager Enum singleton
* <p>
* This enum singleton holds and runs all things important
* to Plugins and Commands
* <p>
* @author Daniel (Underbalanced) (www.StarNub.org)
* @since 1.0
*/
public class PluginManager {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final PluginManager instance = new PluginManager();

    /**
     * This constructor is private - Singleton Pattern
     */
    private PluginManager() {
        try {
            pluginScan(false);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static PluginManager getInstance() {
        return instance;
    }

    private final String pluginDirString = "StarNub/Plugins/";
    private final JavaPluginLoader JAVA_PLUGIN_LOADER = JavaPluginLoader.getInstance();
    private final ConcurrentHashMap<String, UnloadedPlugin> UNLOADED_PLUGINS = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Plugin> LOADED_PLUGINS = new ConcurrentHashMap<>();

    public String getPluginDirString() {
        return pluginDirString;
    }

    public ConcurrentHashMap<String, UnloadedPlugin> getUNLOADED_PLUGINS() {
        return UNLOADED_PLUGINS;
    }

    public ConcurrentHashMap<String, Plugin> getLOADED_PLUGINS() {
        return LOADED_PLUGINS;
    }

    public HashSet<String> getAllUnloadedPluginNames(){
        return UNLOADED_PLUGINS.keySet().stream().collect(Collectors.toCollection(HashSet::new));
    }

    public HashSet<UnloadedPlugin> getAllUnloadedPlugins(){
        return UNLOADED_PLUGINS.values().stream().collect(Collectors.toCollection(HashSet::new));
    }

    public HashSet<String> getAllLoadedPluginNames(){
        return LOADED_PLUGINS.keySet().stream().collect(Collectors.toCollection(HashSet::new));
    }

    public HashSet<Plugin> getAllLoadedPlugins(){
        return LOADED_PLUGINS.values().stream().collect(Collectors.toCollection(HashSet::new));
    }

    public boolean isPluginUnloaded(String searchTerm, boolean nameOnly){
        return resolveUnloadedPlugin(searchTerm, nameOnly) != null;
    }

    public String getResolvedUnloadedPluginName(String searchTerm, boolean nameOnly){
        return resolveUnloadedPlugin(searchTerm, nameOnly).getPLUGIN_NAME();
    }

    public UnloadedPlugin resolveUnloadedPlugin(String searchTerm, boolean nameOnly){
        for (UnloadedPlugin unloadedPlugin : UNLOADED_PLUGINS.values()) {
            String pluginName = unloadedPlugin.getPLUGIN_NAME();
            boolean match = pluginName.equalsIgnoreCase(searchTerm);
            if (match) {
                return unloadedPlugin;
            } else if (!nameOnly) {
                String commandsName = (String) unloadedPlugin.getPLUGIN_PLUGIN_YML().getNestedValue("commands", "name");
                String commandsAlias = (String) unloadedPlugin.getPLUGIN_PLUGIN_YML().getNestedValue("commands", "alias");
                boolean matched = commandsName.equalsIgnoreCase(searchTerm) || commandsAlias.equalsIgnoreCase(searchTerm);
                if(matched){
                    return unloadedPlugin;
                }
            }
        }
        return null;
    }

    public boolean isPluginLoaded(String searchTerm, boolean nameOnly){
        return resolveLoadedPlugin(searchTerm, nameOnly) != null;
    }

    public String getResolvedLoadedPluginName(String searchTerm, boolean nameOnly){
        return resolveLoadedPlugin(searchTerm, nameOnly).getNAME();
    }

    public Plugin resolveLoadedPlugin(String searchTerm, boolean nameOnly){
        for (Plugin plugin : LOADED_PLUGINS.values()) {
            String pluginName = plugin.getNAME();
            boolean match = pluginName.equalsIgnoreCase(searchTerm);
            if (match) {
                return plugin;
            } else if (!nameOnly) {
                if(plugin.getCOMMAND_INFO() !=  null) {
                    String commandsName = plugin.getCOMMAND_INFO().getCOMMANDS_NAME();
                    String commandsAlias = plugin.getCOMMAND_INFO().getCOMMANDS_ALIAS();
                    boolean matched = commandsName.equalsIgnoreCase(searchTerm) || commandsAlias.equalsIgnoreCase(searchTerm);
                    if (matched) {
                        return plugin;
                    }
                }
            }
        }
        return null;
    }

    public Plugin resolveLoadedPluginByCommand(String searchTerm){
        for (Plugin plugin : LOADED_PLUGINS.values()) {
            CommandInfo commandInfo = plugin.getCOMMAND_INFO();
            if (commandInfo != null) {
                String commandsName = commandInfo.getCOMMANDS_NAME();
                String commandsAlias = commandInfo.getCOMMANDS_ALIAS();
                boolean matched = commandsName.equalsIgnoreCase(searchTerm) || commandsAlias.equalsIgnoreCase(searchTerm);
                if (matched) {
                    return plugin;
                }
            }
        }
        return null;
    }

    private void pluginScan(boolean isUpdating) throws MalformedURLException {
        UNLOADED_PLUGINS.clear();
        File[] pluginFiles = FileUtils.convertFileCollectionToFileArray(FileUtils.listFiles(new File(pluginDirString), new String[]{"jar"}, false));
        for (File pluginFile : pluginFiles) {
            URL pluginUrl = pluginFile.toURI().toURL();
            URLClassLoader classLoader = new URLClassLoader(new URL[]{pluginUrl}, StarNub.class.getClassLoader());
            StarNubYamlWrapper data = new StarNubYamlWrapper("StarNub", "StarNub - PluginLoader", classLoader.getResourceAsStream("plugin.yml"), "");
            String pluginName = (String) data.getValue("name");
            double version = (double) data.getNestedValue("details", "version");
            if (UNLOADED_PLUGINS.containsKey(pluginName)) {
                UnloadedPlugin unloadedPlugin = UNLOADED_PLUGINS.get(pluginName);
                double storedVersion = unloadedPlugin.getPLUGIN_VERSION();
                if (storedVersion < version) {
                    String removeFileRecommend = unloadedPlugin.getPLUGIN_FILE().toString();
                    UNLOADED_PLUGINS.remove(pluginName);
                    if (!isUpdating) {
                        StarNub.getLogger().cErrPrint("StarNub", "You have multiple " + pluginName + " plugins, some are older, we recommend you remove the older file named " + removeFileRecommend + ".");
                    }
                }
            }
            UNLOADED_PLUGINS.putIfAbsent(pluginName, new UnloadedPlugin(pluginName, version, pluginUrl, classLoader, data));
        }
    }

    public HashMap<String, String> loadAllPlugins(boolean upgrade, boolean enable)  {
        HashMap<String, String> pluginSuccess = new HashMap<>();
        for (String unloadedPluginName : UNLOADED_PLUGINS.keySet()) {
            String specificPlugin = null;
            try {
                specificPlugin = loadSpecificPlugin(unloadedPluginName, upgrade, enable);
            } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | PluginAlreadyLoaded | InvocationTargetException | ClassNotFoundException | IOException | PluginDependencyLoadFailed | PluginDirectoryCreationFailed | PluginDependencyNotFound | CommandYamlLoadFailed | CommandClassLoadFail e) {
                e.printStackTrace();
                pluginSuccess.put(unloadedPluginName, e.getMessage());
            }
            pluginSuccess.put(unloadedPluginName, specificPlugin);
        }
        return pluginSuccess;
    }

    public String loadSpecificPlugin(String pluginName, boolean upgrade, boolean enable) throws NoSuchMethodException, IllegalAccessException, InstantiationException, PluginAlreadyLoaded, InvocationTargetException, ClassNotFoundException, IOException, PluginDependencyLoadFailed, PluginDirectoryCreationFailed, PluginDependencyNotFound, CommandClassLoadFail, CommandYamlLoadFailed {
        UnloadedPlugin unloadedPlugin = UNLOADED_PLUGINS.remove(pluginName);
        boolean isLoaded = isPluginLoaded(pluginName, true);
        if (!upgrade && isLoaded) {
            throw new PluginAlreadyLoaded(pluginName);
        }
        JavaPlugin javaPlugin = JAVA_PLUGIN_LOADER.pluginLoader(unloadedPlugin);
        if (upgrade) {
           unloadSpecificPlugin(pluginName);
           javaPlugin.enable();
        } else if (enable){
           javaPlugin.enable();
        }
        LOADED_PLUGINS.put(javaPlugin.getNAME(), javaPlugin);
        double version = javaPlugin.getDETAILS().getVERSION();
        StarNub.getLogger().cInfoPrint("StarNub", pluginName + " (" + version + ") " + "was successfully loaded.");
        new StarNubEvent("StarNub_Plugin_Loaded", javaPlugin);
        return pluginName + " was successfully loaded.";
    }

    public String unloadAllPlugins() {
        LOADED_PLUGINS.values().forEach(starnubserver.plugins.Plugin::disable);
        LOADED_PLUGINS.clear();
        if (LOADED_PLUGINS.size() > 0) {
            return "All plugins were successfully unloaded.";
        } else {
            String loadedPlugins = stringCreateLoadedPlugins();
            return "Not all plugins were successfully unloaded." + loadedPlugins;
        }
    }


    public String unloadSpecificPlugin(String pluginName) {
        Plugin plugin = LOADED_PLUGINS.remove(pluginName);
        plugin.disable();
        if (!LOADED_PLUGINS.containsKey(pluginName)) {
            new StarNubEvent("StarNub_Plugin_Unloaded", plugin);
            return pluginName + " was successfully unloaded.";
        } else {
            return pluginName +" was not successfully unloaded.";
        }
    }
//
//    public void enableAllPlugins(boolean loadIfNotLoaded) {
//        for (PluginPackage pluginPackage : LOADED_PLUGINS.values()) {
//            //TODO Print enabled method
//            StarNub.getLogger().cInfoPrint("StarNub", "Plugin Manager: Enabling Plugins: " + pluginPackage.getPLUGIN_NAME());
//            pluginPackage.getPLUGIN().onPluginEnable();
//
//        }
//    }
//
//    public void enableSpecificPlugins(String pluginName, boolean loadIfNotLoaded) throws IOException, InstantiationException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, PluginDependencyNotFound, PluginAlreadyLoaded, PluginDependencyLoadFailed, PluginDirectoryCreationFailed, ClassNotFoundException {
//        Plugin plugin = LOADED_PLUGINS.get(pluginName);
//        if (plugin == null){
//            if(loadIfNotLoaded){
//                loadSpecificPlugin(pluginName, false, true);
//            }
//        }
//    }
//
//    public void disableAllPlugins() {
//        LOADED_PLUGINS.values().stream().filter(PluginPackage::isEnabled).forEach(pluginPackage -> {
//            System.out.println("PLUGIN_MANAGER: Disabling Plugin: " + pluginPackage.getPLUGIN_NAME());
//            pluginPackage.getPLUGIN().onPluginDisable();
//            pluginPackage.setEnabled(false);
//        });
//    }
//
//    public void disableSpecificPlugin(String pluginName) {
//        Plugin plugin = LOADED_PLUGINS.get(pluginName);
//        if (plugin == null){
//            return;
//        }
//    }
//
    private String stringCreateLoadedPlugins() {
        String loadedPluginString = "Loaded Plugins: ";
        for (Plugin plugin : LOADED_PLUGINS.values()) {
            loadedPluginString = loadedPluginString + plugin.getNAME() + " (v" + Double.toString(plugin.getDETAILS().getVERSION()) + "), ";
        }
        return StringUtilities.trimCommaForPeriod(loadedPluginString);
    }
//
//
//
//
//
//
//
//    public void initialStartup() {
//        loadAllPlugins(false);
//        enableAllPlugins();
//        yamlPluginAndCommandDumper();
//    }
//
//
//
//
//
//    public void updatePlugin(Object sender, String pluginName){
//        System.out.println(pluginName);
//        updateScan();
//        pluginName = pluginName.toLowerCase();
//        if (UNLOADED_PLUGINS.size() == 0) {
////            StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender,  "StarNub could not locate a Plugin with named \"" + pluginName + "\". StarNub also " +
////                    "was unable to locate any new versions of any plugins in StarNub/Plugins.");
//        }
//        for (String unloadedPluginString : UNLOADED_PLUGINS.keySet()) {
//            if (unloadedPluginString.equalsIgnoreCase(pluginName)) {
//                UnloadedPlugin unloadedPlugin = UNLOADED_PLUGINS.get(unloadedPluginString);
//                if ((boolean) unloadedPlugin.getPLUGIN_PLUGIN_YML().getValue("live_update")) {
//                    double upgradingVersion = unloadedPlugin.getPLUGIN_VERSION();
//                    loadSpecificPlugin(sender, unloadedPluginString, true, false);
//                    if (LOADED_PLUGINS.get(unloadedPluginString).getVERSION() == upgradingVersion) {
////                        StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, unloadedPluginString + " has successfully been updated to version \"" + Double.toString(upgradingVersion) + "\".");
//                    }
//                } else {
////                    StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, unloadedPluginString + " Does not allow live updating. You must Shutdown StarNub for this plugin.");
//                }
//            } else {
////                StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender,  "StarNub could not locate a named \"" + unloadedPluginString + "\". Here are the names of the plugins we detected that are not loaded: ");
//                String unloadedPluginList = "Unloaded Plugins: ";
//                for (UnloadedPlugin unloadedPlugin : UNLOADED_PLUGINS.values()) {
//                    unloadedPluginList = unloadedPluginList + unloadedPlugin.getPLUGIN_NAME() + "(" + unloadedPlugin.getPLUGIN_VERSION() + "), ";
//                }
//                unloadedPluginList = unloadedPluginList.substring(0, unloadedPluginList.lastIndexOf(", ")) + ".";
////                StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, unloadedPluginList);
//            }
//        }
//    }
//
//
//
//
//
//
//
//
//
//
//
//
//
//    public boolean isPluginLoadedAndEnabled(String pluginNameOrAlias) {
//        String pluginName = resolvePlugin(pluginNameOrAlias, fullName);
//        return pluginName != null && LOADED_PLUGINS.get(pluginName).isEnabled();
//    }
//
//
//
//    public void loadAndEnableAllPlugin(Object sender) {
//        loadAllPlugins(sender);
//        enableAllPlugins();
//    }
//
////    public void loadAndEnableSpecificPlugin(String pluginName) {
////        loadSpecificPlugin(pluginName);
//////    }
//
//
//
//
//
//
//    public void disableAndUnloadAllPlugin() {
//        disableAllPlugins();
////        unloadAllPlugins();
//    }
//
//    public void disableAndUnloadSpecificPlugin(String pluginName) {
//
//    }
//
//
//    private void yamlPluginAndCommandDumper () {
//        DumperOptions options = new DumperOptions();
//        options.setPrettyFlow(true);
//        options.setAllowUnicode(true);
//        for (Plugin plugin : LOADED_PLUGINS.values()) {
//            try {
//                Yaml yaml = new Yaml(options);
//                Writer writer = new FileWriter("StarNub/Plugins/" + pluginPackage.getPLUGIN_NAME() + "/" + pluginPackage.getPLUGIN_NAME().toLowerCase() + "_info.yml");
//                yaml.dump(pluginPackage, writer);
//                writer.close();
//
//                ConcurrentHashMap<ArrayList<String>, CommandPackage> commandPackages = pluginPackage.getCOMMANDS();
//                for (CommandPackage commandPackage : commandPackages.values()) {
//                    Yaml yamlCom = new Yaml(options);
//                    Writer writerCom = new FileWriter("StarNub/Plugins/" + pluginPackage.getPLUGIN_NAME() + "/CommandsInfo/" + pluginPackage.getPLUGIN_NAME().toLowerCase() +"_command_"+commandPackage.COMMAND_MAIN_ARGS +".yml");
//                    yamlCom.dump(commandPackage, writerCom);
//                    writerCom.close();
//                }
//            } catch(IOException e){
//                StarNub.getLogger().cErrPrint("StarNub", "Could not save all Plugin.yml or Command_*.yml data in StarNub/Plugins");
//            }
//        }
//    }
//
//    /**
//     * This represents a higher level method for StarNubs API.
//     * <p>
//     * Recommended: For Plugin Developers & Anyone else.
//     * <p>
//     * Uses: This will send back to the sender all loaded plugins
//     * <p>
//     *
//     */
//    public void getLoadedPluginsPrint(Object sender) {
////        StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender,  stringCreateLoadedPlugins());
//    }
//
//
//
//    /**
//     * This will return a plugin package based on the plugin class name
//     * <p>
//     * @param pluginClassString Class of the plugin that is being searched for
//     * @return PluginPackage the whole package for the plugin if a match was found
//     */
//    public PluginPackage getPluginPackageClassNameString(String pluginClassString) {
//        for (PluginPackage pluginPackage : LOADED_PLUGINS.values()) {
//            if (pluginPackage.getPLUGIN().getClass().getName().equalsIgnoreCase(pluginClassString)) {
//                return pluginPackage;
//            }
//        }
//        return null;
//    }
//
//    public CommandPackage getPluginCommandPackage(String pluginNameOrAlias, String command){
//        String pluginName = resolvePlugin(pluginNameOrAlias, fullName);
//        if (pluginName != null) {
//            for (ArrayList<String> commands : LOADED_PLUGINS.get(pluginName).getCOMMANDS().keySet()) {
//                if (commands.contains(command.toLowerCase())){
//                    return LOADED_PLUGINS.get(pluginName).getCOMMANDS().get(commands);
//                }
//            }
//        }
//        return null;
//    }
//
//
//    /**
//     * This will return a plugin package based on the alias or name provided
//     * <p>
//     * @param pluginNameOrAlias String name or alias of the plugin that is being searched for
//     * @return PluginPackage the whole package for the plugin if a match was found
//     */
//    public PluginPackage getPluginPackageNameAlias(String pluginNameOrAlias) {
//        String pluginName = resolvePlugin(pluginNameOrAlias, fullName);
//        if (pluginName == null) {
//            return null;
//        }
//        return LOADED_PLUGINS.get(pluginName);
//    }
//
//
//
//
//
//    public boolean hasPlugin(String pluginString) {
//        return resolvePlugin(pluginString, fullName) != null;
//    }
//
//    public String hasCommand(String pluginNameAlias, String command) {
//        String pluginName = resolvePlugin(pluginNameAlias, fullName);
//        if (pluginName != null) {
//            for (ArrayList<String> commands : LOADED_PLUGINS.get(pluginName).getCOMMANDS().keySet()) {
//                if (commands.contains(command)){
//                    return "hascommand";
//                }
//            }
//        } else {
//            return "noplugin";
//        }
//        return "nocommand";
//    }
//
//
//
//
//    public void getAllPluginsInfo() {
//        System.out.println("PLUGIN_MANAGER: All loaded plugin info:");
//        for (PluginPackage pluginPackage : LOADED_PLUGINS.values()) {
//            System.out.println(
//                    "Plugin Name: " + pluginPackage.getPLUGIN_NAME() +
//                            ", Plugin Version: " + pluginPackage.getVERSION() +
//                            ", Plugin Language: " + pluginPackage.getPLUGIN_LANGUAGE() +
//                            ", Plugin Author: " + pluginPackage.getPLUGIN_AUTHOR() +
//                            ", Plugin URL: " + pluginPackage.getPLUGIN_URL() +
//                            ", Plugin Dependencies: " + pluginPackage.getDEPENDENCIES());
//        }
//    }
//
//    public void getSpecificPluginsInfo(String pluginName) {
//        System.out.println("PLUGIN_MANAGER: " + pluginName + " plugin info:");
//        if (LOADED_PLUGINS.containsKey(pluginName)) {
//            PluginPackage pluginPackage = LOADED_PLUGINS.get("pluginName");
//            System.out.println(
//                    "Plugin Name: " + pluginPackage.getPLUGIN_NAME() +
//                            ", Plugin Version: " + pluginPackage.getVERSION() +
//                            ", Plugin Language: " + pluginPackage.getPLUGIN_LANGUAGE() +
//                            ", Plugin Author: " + pluginPackage.getPLUGIN_AUTHOR() +
//                            ", Plugin URL: " + pluginPackage.getPLUGIN_URL() +
//                            ", Plugin Dependencies: " + pluginPackage.getDEPENDENCIES());
//        } else {
//            System.out.println("Plugin cannot be found.");
//        }
//    }
//
//
//    //TODO remove defaults
//
//    public void listRunningPlugins() {
//        //TODO print method
//        System.out.println("Plugin Manager: Enabled Plugins: ");
//        LOADED_PLUGINS.values().stream().filter(PluginPackage::isEnabled).forEach(pluginPackage -> System.out.println(pluginPackage.getPLUGIN_NAME()));
//    }
//
//    public void listSpecificPlugins(String pluginName) {
//
//    }
//
//    public void reloadAllPlugins() {
//        disableAndUnloadAllPlugin();
////        loadAndEnableAllPlugin();
//    }
//
//    public void reloadSpecificPlugin(String pluginName) {
//
//    }
//
//    public void addRunnableToPlugin(String pluginName, StarNubRunnable runnable){
//        String classNameString = runnable.getClass().getName();
//        PluginPackage pluginPackage = getPluginPackageNameAlias(pluginName);
//        int threadCount = 0;
//        if (pluginPackage.isHasThreads()){
//            threadCount = pluginPackage.getThreads().size() + 1;
//        } else {
//            pluginPackage.setThreads(new ConcurrentHashMap<Thread, StarNubRunnable>());
//            pluginPackage.setHasThreads(true);
//        }
//        String name = "Plugin - " + pluginName +" : Class - " +classNameString.substring(classNameString.lastIndexOf(".")+1)+ " : Thread - "+threadCount;
//        Thread threadToStart = new Thread(runnable, name);
//        pluginPackage.getThreads().putIfAbsent(threadToStart, runnable);
//        threadToStart.startUDPServer();
////        ThreadEvent.eventSend_Permanent_Thread_Started(pluginPackage.getPLUGIN_NAME(), threadToStart);
//    }
}

