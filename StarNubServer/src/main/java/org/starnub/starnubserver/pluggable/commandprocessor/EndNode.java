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

public class EndNode {

    private final String NODE_ARGUMENT;
    private final ArgumentType ARGUMENT_TYPE;
    private final CommandHandler COMMAND_HANDLER;

    /**
     * This is the final command node, when your argument gets called this EndNode will be used for your command
     *
     * @param NODE_ARGUMENT The argument that will be handled by this command handler
     * @param ARGUMENT_TYPE ArgumentType enumeration is static or variable
     * @param COMMAND_HANDLER CommandHandler command handler used to handle the end of the linked command & args
     */
    public EndNode(String NODE_ARGUMENT, ArgumentType ARGUMENT_TYPE, CommandHandler COMMAND_HANDLER) {
        this.COMMAND_HANDLER = COMMAND_HANDLER;
        this.ARGUMENT_TYPE = ARGUMENT_TYPE;
        if (ARGUMENT_TYPE == ArgumentType.VARIABLE) {
            this.NODE_ARGUMENT = correctArgs(NODE_ARGUMENT);
        } else {
            this.NODE_ARGUMENT = NODE_ARGUMENT;
        }

    }

    public String getNODE_ARGUMENT() {
        return NODE_ARGUMENT;
    }

    public ArgumentType getARGUMENT_TYPE() {
        return ARGUMENT_TYPE;
    }

    public CommandHandler getCOMMAND_HANDLER() {
        return COMMAND_HANDLER;
    }

    /**
     * This will correct any variables args format by placing them in code brackets {...} for uniformity
     *
     * @param variableArg String of arg to be checked for correction
     * @return String[] corrected variable args
     */
    public static String correctArgs(String variableArg){
        if (!variableArg.startsWith("{")) {
            variableArg = "{" + variableArg;
        }
        if (!variableArg.endsWith("}")) {
            variableArg = variableArg + "}";
        }
        return variableArg;
    }


    /**
     * This is used to get the variable or the variable node string
     *
     * @return String representing the key to use for the Node calling this nodes method
     */
    public String getMapString(){
        if (ARGUMENT_TYPE == ArgumentType.VARIABLE){
            return "{default-variable-arguments}";
        } else {
            return NODE_ARGUMENT;
        }
    }

    @Override
    public String toString() {
        return "EndNode{" +
                "NODE_ARGUMENT='" + NODE_ARGUMENT + '\'' +
                ", ARGUMENT_TYPE=" + ARGUMENT_TYPE +
                ", COMMAND_HANDLER=" + COMMAND_HANDLER +
                '}';
    }
}
