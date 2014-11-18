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

package server.plugins;

import org.codehome.utilities.files.JarResourceLoadFromDisk;
import org.codehome.utilities.files.YamlLoader;
import server.StarNub;

import java.io.File;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents Java Plugin Loader.
 * <p>
 * This enum singleton holds and runs all things important
 * to loading Java Plugins
 * <p>
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
enum CommandPreload {
    INSTANCE;

    /**
     * Default constructor
     */
    CommandPreload() {}

    @SuppressWarnings("unchecked")
    protected ConcurrentHashMap<ArrayList<String>, CommandPackage> preloadPluginCommands(Object sender, File prospectivePlugin, String pluginName, URLClassLoader classLoader, String commandName) {
        ConcurrentHashMap<ArrayList<String>, CommandPackage> commandPackages = new ConcurrentHashMap<ArrayList<String>, CommandPackage>();


        List<String> commandFiles = new JarResourceLoadFromDisk().loadJarResources(prospectivePlugin,"commands/",".yml");
            for (String command : commandFiles) {

                for (ArrayList<String> commands : commandPackages.keySet())
                    if (commands.contains(command)) {
                        System.out.println("This command \"" + command + "\" is already loaded.");
                        return null;
                    }

                CommandPackage commandPackage = commandPackageLoader(sender, command, pluginName, classLoader, commandName);
                if (commandPackage != null) {
                    commandPackages.putIfAbsent(commandPackage.getCOMMANDS(), commandPackage);
                }
            }

        return commandPackages;
    }

    @SuppressWarnings("unchecked")
    private CommandPackage commandPackageLoader(Object sender, String commandsYMLString, String pluginName, URLClassLoader classLoader, String pluginCommandName) {

        Map<String, Object> commandFile = new YamlLoader().resourceStreamYamlLoader(classLoader.getResourceAsStream(commandsYMLString));
        if (commandFile == null) {
            StarNub.getLogger().cErrPrint("StarNub", "StarNub could not load a specific mainArgs .yml: "+commandsYMLString+" from "+pluginName+" plugin.");
            return null;
        }

        commandFile = commandYamlValidator(commandFile, pluginCommandName, pluginName);

        ArrayList<String> commandName = new ArrayList<String>();
        Object commandNameObject = commandFile.get("commands");
        if (commandNameObject instanceof String){
            commandName.add(((String) commandNameObject).toLowerCase());
        } else if (commandNameObject instanceof List) {
            for (String loadingCommand : (ArrayList<String>) commandNameObject) {
                commandName.add(loadingCommand.toLowerCase());
            }
        }

        /* Command main args is used for plugin only, mainArgs is used as the Command Package name */
        ArrayList<String> mainArgs = new ArrayList<String>();
        Object commandObject = commandFile.get("main_args");
        if (commandObject instanceof String){
            mainArgs.add(((String) commandObject).toLowerCase());
        } else if (commandObject instanceof List) {
            for (String loadingCommand : (ArrayList<String>) commandObject) {
                mainArgs.add(loadingCommand.toLowerCase());
            }
        }

        String mainCommandClass = (String) commandFile.get("class");

        /* Load Class into a java class, then cast to a Plugin */
        Command command;
        Class<?> javaClass;
        try {
            javaClass = classLoader.loadClass(mainCommandClass);
            try {
                Class<? extends Command> commandClass = javaClass.asSubclass(Command.class);
                try {
                    command = commandClass.newInstance();
                } catch (InstantiationException e) {
                    StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName+" command Load Error: Command " + commandName + " could not instantiate command.");
                    return null;
                } catch (IllegalAccessException e) {
                    StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName+" command Load Error: Command " + commandName + " could not instantiate plugin, illegal access exception.");
                    return null;
                }
            } catch (ClassCastException e) {
                StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName+" command Load Error: Command " + commandName + " does not represent a command.");
                return null;
            }
        } catch (ClassNotFoundException e) {
            StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName+" command Load Error: Command " + commandName + " class not found or issue with plugin imports: \"" + mainCommandClass + "\" for \"class\" value in: \"" + commandsYMLString + "\".");
            return null;
        } catch (NoClassDefFoundError e) {
            StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName+" command Load Error: Command " + commandName + " package not found or issue with plugin imports: \"" + mainCommandClass + "\" for \"class\" value in: \"" + commandsYMLString + "\".");
            return null;
        }
        try {
            return new CommandPackage(
                    commandName,
                    mainArgs,
                    mainCommandClass,
                    pluginCommandName + "." + commandFile.get("command") + "." + mainArgs,
                    (int) commandFile.get("can_use"),
                    (String) commandFile.get("description"),
                    command);
        } catch (NullPointerException e) {
            StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName+" command Load Error:  : Command " + commandName + " null value or empty: \"command.yml\".");
        }
        return null;
    }

    private Map<String, Object> commandYamlValidator(Map<String, Object> commandFile, String commandName, String pluginName) {
        Map<String, Object> defaultCommandYaml = new YamlLoader().resourceYamlLoader("servers/integrity/default_command.yml");

        for (String commandItemString : defaultCommandYaml.keySet()) {

            Object commandItem = null;
            try {
                commandItem = (Object) commandFile.get(commandItemString);
            } catch (NullPointerException e) {
                StarNub.getLogger().cErrPrint("StarNub", "StarNub had issues verifying the type safety of a \""+commandName+"\" command{name}.yml, \""+commandItemString+"\" appears to be null, empty or not correctly typed?");
            }

            Object defaultCommandItem = (Object) defaultCommandYaml.get(commandItemString);

            try {
                if (defaultCommandItem instanceof String) {
                    if (!(commandItem instanceof String)) {
                        commandFile.replace(commandItemString, commandItem.toString());
                    }
                } else if (defaultCommandItem instanceof Integer) {
                    if (!(commandItem instanceof Integer)) {
                        commandFile.replace(commandItemString, Integer.parseInt(commandItem.toString()));
                    }
                } else if (defaultCommandItem instanceof Double) {
                    if (!(commandItem instanceof Double)) {
                        commandFile.replace(commandItemString, Double.parseDouble(commandItem.toString()));
                    }
                } else if (defaultCommandItem instanceof Boolean) {
                    if (!(commandItem instanceof Boolean)) {
                        if (commandItem.toString().equalsIgnoreCase("true")) {
                            commandFile.replace(commandItemString, true);
                        } else if (commandItem.toString().equalsIgnoreCase("false")) {
                            commandFile.replace(commandItemString, false);
                        }
                    }
                }
            } catch (NullPointerException e) {
                StarNub.getLogger().cErrPrint("StarNub", "Plugin Manager: Command Loader: Plugin " + pluginName + ". Command: " +
                        commandFile.get("name") + " Should be updated to the newest StarNub standard. Please contact " +
                        "the plugin maker to have this updated or if you are the plugin maker visit www.StarNub.org " +
                        "- StarNubPlugin project and find the commands configurations of that project which will be set to" +
                        "the standard.");
            }
        }
        return commandFile;
    }
}




