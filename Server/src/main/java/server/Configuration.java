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

package server;

import org.codehome.utilities.files.YamlDumper;
import org.codehome.utilities.files.YamlLoader;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents StarNub's Configuration and Language it can be instantiated
 * for the sole purpose of setting StarNub and plug-in's Configurations.
 * <p>
 * This class will configure StarNub if not configured.
 * It will also remove old or invalid configuration items.
 * If invalid items exist in configuration it will remove them.
 * The configuration is in YAML and must be syntax correct. You can use
 * "Online YAML Parsers" like "http://yaml-online-parser.appspot.com/". To verify
 * the correct formatting after changes are made manually.
 * </p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */

///TODO
///RETIRE

public final class Configuration {

    private final String configType;
    private ConcurrentHashMap<String, Object> configuration;
    private final String diskConfigString;
    private final Object defaultConfigObject;

    public String getConfigType() {
        return configType;
    }

    public ConcurrentHashMap<String, Object> getConfiguration() {
        return configuration;
    }

    public String getDiskConfigString() {
        return diskConfigString;
    }

    public Object getDefaultConfigObject() {
        return defaultConfigObject;
    }

    /**
     * Public Constructor
     *
     * @param configType String name of the configuration "StarNub Configuration" or "Essentials Plugin Configuration"
     * @param defaultConfigObject Object which is a String path inside of StarNub or a InputStream for another jar
     * @param diskConfigString String representing the location on disk of the users configuration "StarNub/starnub_config.yml" or "StarNub/Plugins/{PluginName}/{PluginName}.yml"
     */
    public Configuration(String configType, Object defaultConfigObject, String diskConfigString) {
        this.configuration = new ConcurrentHashMap<String, Object>();
        this.configType = configType;
        this.defaultConfigObject = defaultConfigObject;
        this.diskConfigString = diskConfigString;
        loadConfiguration(true);
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This class will load a configuration from disk, if no configuration
     * exist from disk, it will then load the default from the Jar and dump it to
     * disk and then shut down allowing the user to set the values before start up.
     * <p>
     * Notes:
     * 1. Configurations are in YAML if making changes to a configuration on disk
     * or in another Jar that will be loaded by this program, please ensure you verify
     * the syntax by searching the internet for "Online YAML Parsers" like
     * "http://yaml-online-parser.appspot.com/".
     * 2. This method will validate the configuration items to make sure each item exist and
     * that no extra or old configuration items are in the StarNub user configuration. Old items
     * will be removed, and new items added. If a new item is added it will shut down and wait for
     * you to verify the item is configured.
     * 3. Finally items in the configuration values from disk will be checked and corrected against the
     * values in the default configuration for data type to ensure safety of StarNub and associated add ons.
     * 4. After validation the configuration will be saved to /StarNub or /StarNub/Plugins/{PluginName}
     * <p>
     * @param snConfig boolean representing if this is a StarNub configuration. If not and you put true, you will be unable to load your plugins
     *                 configuration
     */
    public void loadConfiguration(boolean snConfig) {
        Map<String, Object> defaultConfig = loadResourceFile(defaultConfigObject, snConfig);
        if (defaultConfig == null) {
            StarNub.getLogger().cErrPrint("StarNub", "ERROR LOADING DEFAULT CONFIGURATION. THIS SHOULD NEVER APPEAR IF USING A LEGIT STARNUB" +
                    "PLEASE VISIT \"WWW.STARNUB.ORG\" AND PUT A TICKET IN... EXITING STARNUB.");
            System.exit(0);
        }
        if (!snConfig) {
            File file = new File(diskConfigString);
            if (!file.exists()){
                this.configuration.putAll(defaultConfig);
                saveConfiguration();
            }
        }
        this.configuration.putAll(new YamlLoader().filePathYamlLoader(diskConfigString));
        configValidateClean(defaultConfig, configType, snConfig);
        saveConfiguration();
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method will load a resource utilities.file from StarNub or a Plugin
     * <p>
     *
     * @param defaultConfigObject Object which is a String path inside of StarNub or a InputStream for another jar
     * @param snConfig boolean representing if this is a StarNub configuration.
     * @return Map representing the default configuration
     */
    private Map<String, Object> loadResourceFile(Object defaultConfigObject, boolean snConfig){
        if (snConfig) {
            return new YamlLoader().resourceYamlLoader((String) defaultConfigObject);
        } else {
            return new YamlLoader().resourceStreamYamlLoader((InputStream) defaultConfigObject);
        }
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This class will reload the configuration utilities.file
     * <p>
     */
    public void reloadConfiguration(boolean snConfig){
        loadConfiguration(snConfig);
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method will load a resource utilities.file from StarNub or a Plugin
     * <p>
     */
    private void saveConfiguration() {
        new YamlDumper().toFileYamlDump(this.configuration, this.diskConfigString);
    }


    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method will validate the configuration and add missing
     * configuration values. If extra or old configuration items exist,
     * they will be removed. This method uses the default_configuration.yml
     * within the classpath / or resources.
     *
     * @param defaultConfig Map of the defaultConfiguration
     * @param configType String name of the configuration "StarNub Configuration"
     * @param snConfig boolean representing if this is a StarNub configuration
     */
    private void configValidateClean(Map<String, Object> defaultConfig, String configType, boolean snConfig) {
        /*  Add missing values */
        boolean missingItem = false;
        if (snConfig) {
            for (String key : defaultConfig.keySet()) {
                if (!this.configuration.containsKey(key)) {
                    System.err.println("Missing configuration value \"" + key + "\" for configuration " + configType + ".");
                    this.configuration.put(key, defaultConfig.get(key));
                    missingItem = true;
                }
            }
            if (missingItem) {
                System.out.println("You were missing some StarNub configuration values, we added them to the  " +
                        " configuration on your hard disk. StarNub will now exit and allow you to check the configuration at" +
                        "\"StarNub/starnub_config.yml\"");
            }
        }
        /* Remove extra or old values */
        HashSet<String> removeKeys = new HashSet<>();
        this.configuration.keySet().stream().filter(key -> !defaultConfig.containsKey(key)).forEach(key -> {
            System.err.println("Removing old or invalid configuration item \"" + key + "\" for configuration "+ configType +".");
            removeKeys.add(key);
        });
        removeKeys.forEach(this.configuration::remove);
        /* Verify type safety and auto correct */
        configDataTypeVerify(defaultConfig, this.configuration, configType, true);
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method will take the default configuration along with user configuration and check the data type
     * safety as well as optionally replace the user configuration data type with the correct one.
     * <p>
     * Notes: This currently only supports reading and verify, Strings, Integers, Doubles, Booleans, List and Maps. For added
     * features please request them.
     *
     * @param defaultConfig Map representing the default configuration
     * @param userConfig Map representing the user configuration
     * @param typeSafeAutoCorrect boolean representing
     */
    public void configDataTypeVerify(Map<String, Object> defaultConfig, Map<String, Object> userConfig, String configType, boolean typeSafeAutoCorrect){

    }
}
