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
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import starnubserver.StarNub;
import starnubserver.events.events.ThreadEvent;
import starnubserver.plugins.runnable.StarNubRunnable;
import starnubserver.resources.TemporaryYAML;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    }

    public static PluginManager getInstance() {
        return instance;
    }

    protected final static String pluginDirString = "StarNub/Plugins/";
    protected static ConcurrentHashMap<String, UnloadedPlugin> unloadedPlugins;
    protected static ConcurrentHashMap<String, PluginPackage> loadedPlugins;
    public static String getPluginDirString() {
        return pluginDirString;
    }
    public static ConcurrentHashMap<String, UnloadedPlugin> getUnloadedPlugins() {
        return unloadedPlugins;
    }

    public static void setUnloadedPlugins(ConcurrentHashMap<String, UnloadedPlugin> unloadedPlugins) {
        PluginManager.unloadedPlugins = unloadedPlugins;
    }

    public static ConcurrentHashMap<String, PluginPackage> getLoadedPlugins() {
        return loadedPlugins;
    }

    public static void setLoadedPlugins(ConcurrentHashMap<String, PluginPackage> loadedPlugins) {
        PluginManager.loadedPlugins = loadedPlugins;
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This is used to prepare this singleton for use.
     * <p>
     *
     */
    @SuppressWarnings("unchecked")
    public void initializePluginManager() {
        unloadedPlugins = new ConcurrentHashMap<String, UnloadedPlugin>();
        loadedPlugins = new ConcurrentHashMap<String, PluginPackage>();
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This is used on initial start up to load plugins plugins, print failures
     * and then enable the plugins. It will also dump the info files for the plugins
     * and commands.
     * <p>
     *
     */
    public void initialStartup() {
        setUnloadedPlugins(false);
        loadAllPlugins(null);
        enableAllPlugins();
        yamlPluginAndCommandDumper();
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method well scan for "jars" inside of StarNub/Plugins and attempt to open them and look for a plugin.yml
     * If we cannot load the plugin.yml then it may not be a plugin or a correctly made plugin.
     * <p>
     *
     * @return ConcurrentHashMap representing plugins that are not loaded but exist in StarNub/Plugins
     * @param isUpdating
     */
    private ConcurrentHashMap<String, UnloadedPlugin> pluginScan(boolean isUpdating) {
        ConcurrentHashMap<String, UnloadedPlugin> unloadedPluginConcurrentHashMap = new ConcurrentHashMap<String, UnloadedPlugin>();
        File[] pluginFiles = FileUtils.convertFileCollectionToFileArray(FileUtils.listFiles(new File(pluginDirString), new String[]{"jar"}, false));
        for (File pluginFile : pluginFiles) {
            TemporaryYAML data;
            URLClassLoader classLoader;
            String pluginName;
            double version;
            try {
                URL pluginUrl = pluginFile.toURI().toURL();
                classLoader = new URLClassLoader(new URL[]{pluginUrl}, StarNub.class.getClassLoader());
                data = new TemporaryYAML(classLoader.getResourceAsStream("plugin.yml"));
                pluginName = (String) data.getValue("name");
                version = (double) data.getValue("version");
                if (unloadedPluginConcurrentHashMap.containsKey(pluginName)) {
                    double storedVersion = unloadedPluginConcurrentHashMap.get(pluginName).getPLUGIN_VERSION();
                    if (storedVersion < version) {
                        String removeFile = unloadedPluginConcurrentHashMap.get(pluginName).getPLUGIN_FILE().toString();
                        unloadedPluginConcurrentHashMap.remove(pluginName);
                        if (!isUpdating) {
                            StarNub.getLogger().cErrPrint("StarNub", "You have multiple " + pluginName + " plugins, some are older, we recommend you remove the older utilities.file named " + removeFile + ".");
                        }
                    }
                }
                unloadedPluginConcurrentHashMap.putIfAbsent(pluginName, new UnloadedPlugin(pluginName, version, pluginUrl, classLoader, data));

            } catch (Exception e) {
                StarNub.getLogger().cErrPrint("sn","Could not load a utilities.file in the plugins directory "+pluginFile.toString()+"unknown issue URI or URL. Maybe this is not a plugin?");
            }
        }
        return unloadedPluginConcurrentHashMap;
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method well set the unloaded plugins ConcurrentHashMap
     * <p>
     *
     * @param isUpdating
     */
    private void setUnloadedPlugins(boolean isUpdating){
        unloadedPlugins = pluginScan(isUpdating);
        for (String unloadedPluginKey : unloadedPlugins.keySet()) {
            UnloadedPlugin unloadedPlugin = unloadedPlugins.get(unloadedPluginKey);
            if (loadedPlugins.containsKey(unloadedPluginKey)) {
                PluginPackage loadedPlugin = loadedPlugins.get(unloadedPluginKey);
                if (unloadedPlugin.getPLUGIN_VERSION() <= loadedPlugin.getVERSION()) {
                    unloadedPlugins.remove(unloadedPluginKey);
                }
            }
        }
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method will reload the unloaded plugins list by clearing and rescanning the hard drive
     * <p>
     *
     */
    private void updateScan() {
        unloadedPlugins.clear();
        setUnloadedPlugins(true);
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method will attempt to load a new version of a plugin from the hard drive and replace the current running plugin. The
     * Plugin must support live starbounddata.packets.updates.
     * <p>
     *
     */
    public void updatePlugin(Object sender, String pluginName){
        System.out.println(pluginName);
        updateScan();
        pluginName = pluginName.toLowerCase();
        if (unloadedPlugins.size() == 0) {
//            StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender,  "StarNub could not locate a Plugin with named \"" + pluginName + "\". StarNub also " +
//                    "was unable to locate any new versions of any plugins in StarNub/Plugins.");
        }
        for (String unloadedPluginString : unloadedPlugins.keySet()) {
            if (unloadedPluginString.equalsIgnoreCase(pluginName)) {
                UnloadedPlugin unloadedPlugin = unloadedPlugins.get(unloadedPluginString);
                if ((boolean) unloadedPlugin.getPLUGIN_PLUGIN_YML().getValue("live_update")) {
                    double upgradingVersion = unloadedPlugin.getPLUGIN_VERSION();
                    loadSpecificPlugin(sender, unloadedPluginString, true, false);
                    if (loadedPlugins.get(unloadedPluginString).getVERSION() == upgradingVersion) {
//                        StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, unloadedPluginString + " has successfully been updated to version \"" + Double.toString(upgradingVersion) + "\".");
                    }
                } else {
//                    StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, unloadedPluginString + " Does not allow live updating. You must Shutdown StarNub for this plugin.");
                }
            } else {
//                StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender,  "StarNub could not locate a named \"" + unloadedPluginString + "\". Here are the names of the plugins we detected that are not loaded: ");
                String unloadedPluginList = "Unloaded Plugins: ";
                for (UnloadedPlugin unloadedPlugin : unloadedPlugins.values()) {
                    unloadedPluginList = unloadedPluginList + unloadedPlugin.getPLUGIN_NAME() + "(" + unloadedPlugin.getPLUGIN_VERSION() + "), ";
                }
                unloadedPluginList = unloadedPluginList.substring(0, unloadedPluginList.lastIndexOf(", ")) + ".";
//                StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, unloadedPluginList);
            }
        }
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method will remove a plugin from the unloaded list
     * <p>
     *
     */
    protected void removeUnloadedPluginFromList(String pluginName) {
        unloadedPlugins.remove(pluginName);
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will attempt to load all plugins one at a time from StarNub/Plugins. If the plugin has a dependencies it will
     * load them first.
     * <p>
     *
     */
    public void loadAllPlugins(Object sender) {
        for (String unloadedPluginName : unloadedPlugins.keySet()) {
            loadSpecificPlugin(sender, unloadedPluginName, false, true);
        }
//        StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, stringCreateLoadedPlugins());
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will attempt to load a specific plugin by its plugin name by using the utilities.file scanner.
     * <p>
     *
     */
    public void loadSpecificPlugin(Object sender, String pluginName, boolean upgrade, boolean groupLoad) {
        JavaPluginPreload.INSTANCE.pluginPackageLoader(sender, unloadedPlugins.remove(pluginName), upgrade);
        if (!groupLoad && loadedPlugins.containsKey(pluginName)) {
//            StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName+" was successfully loaded.");
        } else if (!groupLoad && !loadedPlugins.containsKey(pluginName)) {
//            StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName+" was not successfully loaded.");
        }
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will tell you if a plugin is loaded.
     * <p>
     * @param pluginName String representing the plugin name that failed
     */
    public void isPluginLoaded(Object sender, String pluginName) {
        if (loadedPlugins.contains(pluginName)) {
//            StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName+" is loaded.");
        } else {
//            StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName+" is not loaded.");
        }
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will unload all plugins
     * <p>
     */
    public void unloadAllPlugins(Object sender) {
        disableAllPlugins();
        loadedPlugins.clear();
        if (loadedPlugins.size() > 0) {
//            StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, "All plugins were successfully unloaded.");
        } else {
//            StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender,  "Not all plugins were successfully unloaded.");
//            StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender,  stringCreateLoadedPlugins());
        }
    }


    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will tell you if a plugin is loaded.
     * <p>
     * @param pluginName String representing the plugin name that failed
     *
     */
    public void unloadSpecificPlugin(Object sender, String pluginName) {
        disableSpecificPlugin(pluginName);
        loadedPlugins.remove(pluginName);
        if (!loadedPlugins.contains(pluginName)) {
//            StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName+" was successfully unloaded.");
        } else {
//            StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName+" was not successfully unloaded.");
        }
    }

















    public boolean isPluginLoadedAndEnabled(String pluginNameOrAlias) {
        String pluginName = resolvePlugin(pluginNameOrAlias);
        return pluginName != null && loadedPlugins.get(pluginName).isEnabled();
    }



    public void loadAndEnableAllPlugin(Object sender) {
        loadAllPlugins(sender);
        enableAllPlugins();
    }

//    public void loadAndEnableSpecificPlugin(String pluginName) {
//        loadSpecificPlugin(pluginName);
////    }

    public void enableAllPlugins() {
        for (PluginPackage pluginPackage : loadedPlugins.values()) {
            //TODO Print enabled method
            StarNub.getLogger().cInfoPrint("StarNub", "Plugin Manager: Enabling Plugins: " + pluginPackage.getPLUGIN_NAME());
            pluginPackage.getPLUGIN().onPluginEnable();
            if (pluginPackage.isHasThreads()) {
                pluginPackage.startThreads();
            }
            pluginPackage.setEnabled(true);
        }
    }

    public void enableSpecificPlugins(String pluginName) {

    }
    public void disableAllPlugins() {
        loadedPlugins.values().stream().filter(PluginPackage::isEnabled).forEach(pluginPackage -> {
            System.out.println("PLUGIN_MANAGER: Disabling Plugin: " + pluginPackage.getPLUGIN_NAME());
            pluginPackage.getPLUGIN().onPluginDisable();
            pluginPackage.setEnabled(false);
        });
    }

    public void disableSpecificPlugin(String pluginName) {

    }




    public void disableAndUnloadAllPlugin() {
        disableAllPlugins();
//        unloadAllPlugins();
    }

    public void disableAndUnloadSpecificPlugin(String pluginName) {

    }








    public void resourceExtract(Object sender, String pluginName, String resourcePath, String folderName) {
        String resolvedPlugin = resolvePlugin(pluginName);
        File pluginFile = new File(pluginDirString + resolvedPlugin + ".jar");
        if (!pluginFile.exists()){
//            StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, "Could not find plugin while trying to extract its" +
//                    "resources.");
            return;
        }
        Map<String, Object> data;
        URLClassLoader classLoader;

//        try {
//            URL pluginUrl = pluginFile.toURI().toURL();
//            classLoader = new URLClassLoader(
//                    new URL[]{pluginUrl}, StarNub.class.getClassLoader());
//            new (classLoader.getResourceAsStream(resourcePath));
//            if (unloadedPluginConcurrentHashMap.containsKey(pluginName)) {
//                double storedVersion = unloadedPluginConcurrentHashMap.get(pluginName).getPLUGIN_VERSION();
//                if (storedVersion < version) {
//                    String removeFile = unloadedPluginConcurrentHashMap.get(pluginName).getPLUGIN_FILE().toString();
//                    unloadedPluginConcurrentHashMap.remove(pluginName);
//                    if (!isUpdating) {
//                        StarNub.getLogger().cErrPrint("StarNub", "You have multiple " + pluginName + " plugins, some are older, we recommend you remove the older utilities.file named " + removeFile + ".");
//                    }
//                }
//            }
//            unloadedPluginConcurrentHashMap.putIfAbsent(pluginName, new UnloadedPlugin(pluginName, version, pluginUrl, classLoader, data));
//
//        } catch (Exception e) {
//            StarNub.getLogger().cErrPrint("sn","Could not load a utilities.file in the plugins directory "+pluginFile.toString()+"unknown issue URI or URL. Maybe this is not a plugin?");
//        }

    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method will save to disk each plugins plugin.yml and commands.yml so
     * starbounddata.packets.starbounddata.packets.starnubserver owners can get further information about the plugin and commands.
     * <p>
     *
     */
    private void yamlPluginAndCommandDumper () {
        DumperOptions options = new DumperOptions();
        options.setPrettyFlow(true);
        options.setAllowUnicode(true);
        for (PluginPackage pluginPackage : loadedPlugins.values()) {
            try {
                Yaml yaml = new Yaml(options);
                Writer writer = new FileWriter("StarNub/Plugins/" + pluginPackage.getPLUGIN_NAME() + "/" + pluginPackage.getPLUGIN_NAME().toLowerCase() + "_info.yml");
                yaml.dump(pluginPackage, writer);
                writer.close();

                ConcurrentHashMap<ArrayList<String>, CommandPackage> commandPackages = pluginPackage.getCOMMAND_PACKAGES();
                for (CommandPackage commandPackage : commandPackages.values()) {
                    Yaml yamlCom = new Yaml(options);
                    Writer writerCom = new FileWriter("StarNub/Plugins/" + pluginPackage.getPLUGIN_NAME() + "/CommandsInfo/" + pluginPackage.getPLUGIN_NAME().toLowerCase() +"_command_"+commandPackage.COMMAND_MAIN_ARGS +".yml");
                    yamlCom.dump(commandPackage, writerCom);
                    writerCom.close();
                }
            } catch(IOException e){
                StarNub.getLogger().cErrPrint("StarNub", "Could not save all Plugin.yml or Command_*.yml data in StarNub/Plugins");
            }
        }
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will send back to the sender all loaded plugins
     * <p>
     *
     */
    public void getLoadedPluginsPrint(Object sender) {
//        StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender,  stringCreateLoadedPlugins());
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method will return a string with all of the loaded plugins with version number.
     * <p>
     *
     */
    private String stringCreateLoadedPlugins() {
        String loadedPluginString = "Loaded Plugins: ";
        for (PluginPackage pluginPackage : loadedPlugins.values()) {
            loadedPluginString = loadedPluginString + pluginPackage.getPLUGIN_NAME() + " (v" + Double.toString(pluginPackage.getVERSION()) + "), ";
        }
        try {
            loadedPluginString = loadedPluginString.substring(0, loadedPluginString.lastIndexOf(",")) + ".";
        } catch (StringIndexOutOfBoundsException e) {
            /* Do nothing no players are online */
        }
        return loadedPluginString;
    }

















    /**
     * Upon successful plugin loading StarNub will insert it into the list of loaded plugins
     * <p>
     * @param pluginName String of the plugin name
     * @param pluginPackage PluginPackage which contains all the details about a plugin
     */
    protected void addLoadedPlugin(String pluginName, PluginPackage pluginPackage) {
        loadedPlugins.put(pluginName, pluginPackage);
    }









    /**
     * This will return a plugin package based on the plugin class name
     * <p>
     * @param pluginClassString Class of the plugin that is being searched for
     * @return PluginPackage the whole package for the plugin if a match was found
     */
    public PluginPackage getPluginPackageClassNameString(String pluginClassString) {
        for (PluginPackage pluginPackage : loadedPlugins.values()) {
            if (pluginPackage.getPLUGIN().getClass().getName().equalsIgnoreCase(pluginClassString)) {
                return pluginPackage;
            }
        }
        return null;
    }

    public CommandPackage getPluginCommandPackage(String pluginNameOrAlias, String command){
        String pluginName = resolvePlugin(pluginNameOrAlias);
        if (pluginName != null) {
            for (ArrayList<String> commands : loadedPlugins.get(pluginName).getCOMMAND_PACKAGES().keySet()) {
                if (commands.contains(command.toLowerCase())){
                    return loadedPlugins.get(pluginName).getCOMMAND_PACKAGES().get(commands);
                }
            }
        }
        return null;
    }

    public Map<String, Object> getConfiguration(String pluginNameOrAlias) {
//        return getPluginPackageNameAlias(pluginNameOrAlias).getCONFIGURATION().getConfiguration();
        return null;
    }

    /**
     * This will return a plugin package based on the alias or name provided
     * <p>
     * @param pluginNameOrAlias String name or alias of the plugin that is being searched for
     * @return PluginPackage the whole package for the plugin if a match was found
     */
    public PluginPackage getPluginPackageNameAlias(String pluginNameOrAlias) {
        String pluginName = resolvePlugin(pluginNameOrAlias);
        if (pluginName == null) {
            return null;
        }
        return loadedPlugins.get(pluginName);
    }



    //TODO unload and reload

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method will return a Plugins name using its name,
     * command name or alias to search for it.
     *
     * @param pluginString
     * @return
     */
    public String resolvePlugin(String pluginString){
        for (PluginPackage pluginPackage : loadedPlugins.values()) {
            if(pluginPackage.getPLUGIN_NAME() != null && pluginPackage.getPLUGIN_NAME().equalsIgnoreCase(pluginString)){
                return pluginPackage.getPLUGIN_NAME();
            } else if (pluginPackage.getCOMMANDS_NAME() != null && pluginPackage.getCOMMANDS_NAME().equalsIgnoreCase(pluginString)){
                return pluginPackage.getPLUGIN_NAME();
            } else if (pluginPackage.getCOMMANDS_ALIAS() != null && pluginPackage.getCOMMANDS_ALIAS().equalsIgnoreCase(pluginString)) {
                return pluginPackage.getPLUGIN_NAME();
            }
        }
        return null;
    }


    public boolean hasPlugin(String pluginString) {
        return resolvePlugin(pluginString) != null;
    }

    public String hasCommand(String pluginNameAlias, String command) {
        String pluginName = resolvePlugin(pluginNameAlias);
        if (pluginName != null) {
            for (ArrayList<String> commands : loadedPlugins.get(pluginName).getCOMMAND_PACKAGES().keySet()) {
                if (commands.contains(command)){
                    return "hascommand";
                }
            }
        } else {
            return "noplugin";
        }
        return "nocommand";
    }




    public void getAllPluginsInfo() {
        System.out.println("PLUGIN_MANAGER: All loaded plugin info:");
        for (PluginPackage pluginPackage : loadedPlugins.values()) {
            System.out.println(
                    "Plugin Name: " + pluginPackage.getPLUGIN_NAME() +
                            ", Plugin Version: " + pluginPackage.getVERSION() +
                            ", Plugin Language: " + pluginPackage.getPLUGIN_LANGUAGE() +
                            ", Plugin Author: " + pluginPackage.getPLUGIN_AUTHOR() +
                            ", Plugin URL: " + pluginPackage.getPLUGIN_URL() +
                            ", Plugin Dependencies: " + pluginPackage.getDEPENDENCIES());
        }
    }

    public void getSpecificPluginsInfo(String pluginName) {
        System.out.println("PLUGIN_MANAGER: " + pluginName + " plugin info:");
        if (loadedPlugins.containsKey(pluginName)) {
            PluginPackage pluginPackage = loadedPlugins.get("pluginName");
            System.out.println(
                    "Plugin Name: " + pluginPackage.getPLUGIN_NAME() +
                            ", Plugin Version: " + pluginPackage.getVERSION() +
                            ", Plugin Language: " + pluginPackage.getPLUGIN_LANGUAGE() +
                            ", Plugin Author: " + pluginPackage.getPLUGIN_AUTHOR() +
                            ", Plugin URL: " + pluginPackage.getPLUGIN_URL() +
                            ", Plugin Dependencies: " + pluginPackage.getDEPENDENCIES());
        } else {
            System.out.println("Plugin cannot be found.");
        }
    }


    //TODO remove defaults

    public void listRunningPlugins() {
        //TODO print method
        System.out.println("Plugin Manager: Enabled Plugins: ");
        loadedPlugins.values().stream().filter(PluginPackage::isEnabled).forEach(pluginPackage -> System.out.println(pluginPackage.getPLUGIN_NAME()));
    }

    public void listSpecificPlugins(String pluginName) {

    }

    public void reloadAllPlugins() {
        disableAndUnloadAllPlugin();
//        loadAndEnableAllPlugin();
    }

    public void reloadSpecificPlugin(String pluginName) {

    }

    public void addRunnableToPlugin(String pluginName, StarNubRunnable runnable){
        String classNameString = runnable.getClass().getName();
        PluginPackage pluginPackage = getPluginPackageNameAlias(pluginName);
        int threadCount = 0;
        if (pluginPackage.isHasThreads()){
            threadCount = pluginPackage.getThreads().size() + 1;
        } else {
            pluginPackage.setThreads(new ConcurrentHashMap<Thread, StarNubRunnable>());
            pluginPackage.setHasThreads(true);
        }
        String name = "Plugin - " + pluginName +" : Class - " +classNameString.substring(classNameString.lastIndexOf(".")+1)+ " : Thread - "+threadCount;
        Thread threadToStart = new Thread(runnable, name);
        pluginPackage.getThreads().putIfAbsent(threadToStart, runnable);
        threadToStart.start();
        ThreadEvent.eventSend_Permanent_Thread_Started(pluginPackage.getPLUGIN_NAME(), threadToStart);
    }
}

//TODO and task
