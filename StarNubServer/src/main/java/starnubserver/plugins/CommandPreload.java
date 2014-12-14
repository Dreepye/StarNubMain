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

import starnubserver.StarNub;
import utilities.file.utility.JarResourceLoadFromDisk;
import utilities.file.yaml.YAMLWrapper;

import java.io.File;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Represents Java Plugin Loader.
 * <p>
 * This enum singleton holds and runs all things important
 * to loading Java Plugins
 * <p>
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
class CommandPreload {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final CommandPreload instance = new CommandPreload();

    /**
     * This constructor is private - Singleton Pattern
     */
    private CommandPreload() {
    }

    public static CommandPreload getInstance() {
        return instance;
    }

    private void preloadPluginCommands(){



    }


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
//    private ArrayList<String> COMMANDS;
//    private ArrayList<String> MAIN_ARGS;
//    private HashSet<String> PERMISSIONS;
//    private CanUse CAN_USE;
//    private String DESCRIPTION;
//    private String COMMAND_CLASS;
//    private Command COMMAND;
    @SuppressWarnings("unchecked")
    private CommandPackage commandPackageLoader(Object sender, String commandsYMLString, String pluginName, URLClassLoader classLoader, String pluginCommandName) {
        final YAMLWrapper COMMAND_FILE = new YAMLWrapper(pluginName, commandsYMLString, classLoader.getResourceAsStream(commandsYMLString), "");
        if (COMMAND_FILE.getDATA() == null) {
            StarNub.getLogger().cErrPrint("StarNub", "StarNub could not load a specific mainArgs .yml: "+commandsYMLString+" from "+pluginName+" plugin.");
            return null;
        }
        Object commandObject = COMMAND_FILE.getValue("commands");
        Object mainArgsObject = COMMAND_FILE.getValue("main_args");
        String commandClassString = (String) COMMAND_FILE.getValue("class");
        int canUse = (int) COMMAND_FILE.getValue("can_use");
        String description = (String) COMMAND_FILE.getValue("description");
        String permission = pluginCommandName + "." + commandObject + "." + mainArgsObject;

        ArrayList<String> commandName = new ArrayList<>();
        if (mainArgsObject instanceof String){
            commandName.add(((String) mainArgsObject).toLowerCase());
        } else if (mainArgsObject instanceof List) {
            commandName.addAll(((ArrayList<String>) mainArgsObject).stream().map(loadingCommand -> loadingCommand.toLowerCase()).collect(Collectors.toList()));
        }
        /* Command main args is used for plugin only, mainArgs is used as the Command Package name */
        ArrayList<String> mainArgs = new ArrayList<>();
        if (mainArgsObject instanceof String){
            mainArgs.add(((String) mainArgsObject).toLowerCase());
        } else if (mainArgsObject instanceof List) {
            mainArgs.addAll(((ArrayList<String>) mainArgsObject).stream().map(loadingCommand -> loadingCommand.toLowerCase()).collect(Collectors.toList()));
        }

        /* Load Class into a java class, then cast to a Plugin */
        Command command;
        Class<?> javaClass;
        try {
            javaClass = classLoader.loadClass(commandClassString);
            try {
                Class<? extends Command> commandClass = javaClass.asSubclass(Command.class);
                try {
                    command = commandClass.newInstance();
                } catch (InstantiationException e) {
//                    StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName+" command Load Error: Command " + commandName + " could not instantiate command.");
                    return null;
                } catch (IllegalAccessException e) {
//                    StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName+" command Load Error: Command " + commandName + " could not instantiate plugin, illegal access exception.");
                    return null;
                }
            } catch (ClassCastException e) {
//                StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName+" command Load Error: Command " + commandName + " does not represent a command.");
                return null;
            }
        } catch (ClassNotFoundException e) {
//            StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName+" command Load Error: Command " + commandName + " class not found or issue with plugin imports: \"" + mainCommandClass + "\" for \"class\" value in: \"" + commandsYMLString + "\".");
            return null;
        } catch (NoClassDefFoundError e) {
//            StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName+" command Load Error: Command " + commandName + " package not found or issue with plugin imports: \"" + mainCommandClass + "\" for \"class\" value in: \"" + commandsYMLString + "\".");
            return null;
        }
        try {
            return new CommandPackage(
                    commandName,
                    mainArgs,
                    commandClassString,
                    permission,
                    canUse,
                    description,
                    command
            );
        } catch (NullPointerException e) {
//            StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName+" command Load Error:  : Command " + commandName + " null value or empty: \"command.yml\".");
        }
        return null;
    }
}




