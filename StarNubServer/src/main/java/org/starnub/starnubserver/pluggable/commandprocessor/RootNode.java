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

package org.starnub.starnubserver.pluggable.commandprocessor;

import org.starnub.starnubserver.connections.player.session.PlayerSession;
import org.starnub.starnubserver.pluggable.Command;
import org.starnub.starnubserver.pluggable.PluggableManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RootNode {

    private final EndNode BASE_NODE;

    /**
     * This should be used when there is default handling for the command as such as /plugin in which you want to execute a method.
     *
     * @param BASE_NODE SubNod representing the next Node to handle this command
     */
    public RootNode(EndNode BASE_NODE) {
        this.BASE_NODE = BASE_NODE;
    }

    public void processCommand(PlayerSession playerSession, String command, int argsCount, String [] args) throws CommandProcessorError {
        if (BASE_NODE == null){
            throwCommandProcessError(playerSession, "Missing BaseNode & Command Handler");
            return;
        }
        String commandNode = BASE_NODE.getNODE_ARGUMENT();
        if (!command.equalsIgnoreCase(commandNode)) {
            throwCommandProcessError(playerSession, "Command Mismatch");
            return;
        }
        String builtForReply = "/" + command;
        boolean handledNode = handleNode(playerSession, command, argsCount, args, BASE_NODE, 0, builtForReply);
        if (handledNode){
            return;
        }
        int argIndex = 1;
        EndNode workingNode = BASE_NODE;
        for (String arg : args) {
            boolean containsKey = ((SubNode) workingNode).getNODES().containsKey(arg);
            if (containsKey) {
                workingNode = ((SubNode) workingNode).getNODES().get(arg);
            } else {
                boolean containsKey1 = ((SubNode) workingNode).getNODES().containsKey("{default-variable-arguments}");
                if (containsKey1) {
                    workingNode = ((SubNode) workingNode).getNODES().get("{default-variable-arguments}");
                } else {
                    Object[] possibleArgs;
                    if(argIndex == 1) {
                        possibleArgs = possibleArgsBuilder(playerSession, BASE_NODE.getNODE_ARGUMENT());
                    } else {
                        HashMap<String, EndNode> nodes = ((SubNode) workingNode).getNODES();
                        possibleArgs = possibleArgsBuilder(nodes);
                    }
                    playerSession.sendBroadcastMessageToClient(
                            "ServerName",
                            "You did not supply the correct arguments for the command \"" + commandNode + "\"." +
                                    " Possible arguments: \"" + builtForReply + " " + Arrays.toString(possibleArgs) + "\"."
                    );
                    break;
                }
            }
            builtForReply = builtForReply + " " + arg;
            handledNode = handleNode(playerSession, arg, argsCount, args, workingNode, argIndex, builtForReply);
            if (handledNode){
                break;
            }
            argIndex++;
        }
    }

    private boolean handleNode(PlayerSession playerSession, String commandNode, int argsCount, String[] args, EndNode node, int argIndex, String builtForReply) throws CommandProcessorError {
        if (!(node instanceof SubNode)){
            CommandHandler commandHandler = node.getCOMMAND_HANDLER();
            if (commandHandler != null) {
                commandHandler.handle(playerSession, argsCount, args);
                return true;
            } else {
                throwCommandProcessError(playerSession, "EndNode Missing Command Handler");
            }
        } else if (argsCount == argIndex) {
            CommandHandler commandHandler = node.getCOMMAND_HANDLER();
            if (commandHandler != null) {
                commandHandler.handle(playerSession, argsCount, args);
                return true;
            } else {
                Object[] possibleArgs;
                if(argIndex == 0) {
                    possibleArgs = possibleArgsBuilder(playerSession, BASE_NODE.getNODE_ARGUMENT());
                } else {
                    HashMap<String, EndNode> nodes = ((SubNode) node).getNODES();
                    possibleArgs = possibleArgsBuilder(nodes);
                }
                playerSession.sendBroadcastMessageToClient(
                        "ServerName",
                        "You did not supply enough arguments for the command \"" + commandNode + "\"." +
                                " Possible arguments: \"" + builtForReply + " " + Arrays.toString(possibleArgs) + "\"."
                );
                return true;
            }
        }
        return false;
    }

    private void throwCommandProcessError(PlayerSession playerSession, String message) throws CommandProcessorError {
            playerSession.sendBroadcastMessageToClient(
                    "ServerName",
                    "An error occurred with a Commands, Command Processor. Please contact the Server Administrator. Error: " + message + "."
            );
            throw new CommandProcessorError(message);
    }

    private static String[] possibleArgsBuilder(HashMap<String, EndNode> nodes){
        String[] possibleArgs = new String[nodes.size()];
        int index = 0;
        for(EndNode endNode : nodes.values()){
            String nodeArgument = endNode.getNODE_ARGUMENT();
            possibleArgs[index] = nodeArgument;
            index++;
        }
        return possibleArgs;
    }

    private static Object[] possibleArgsBuilder(PlayerSession playerSession, String commandName){
        Command command = PluggableManager.getInstance().getCOMMANDS().get(commandName.toLowerCase());
        String orgString = command.getDetails().getORGANIZATION().toLowerCase();
        String nameString = command.getDetails().getNAME().toLowerCase();
        HashSet<String> availableArgs =
                Stream.of(command.getMainArgs())
                .sorted(String::compareTo)
                .filter(arg -> playerSession.hasPermission(orgString, nameString, arg, true) || (arg.startsWith("{") && arg.endsWith("}"))).collect(Collectors.toCollection(HashSet::new));
        return availableArgs.toArray();
    }

    @Override
    public String toString() {
        return "RootNode{" +
                ", BASE_NODE=" + BASE_NODE +
                '}';
    }
}


