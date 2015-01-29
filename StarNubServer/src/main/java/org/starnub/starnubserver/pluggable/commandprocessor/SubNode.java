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

import java.util.HashMap;

public class SubNode extends EndNode {

    private final HashMap<String, EndNode> NODES = new HashMap<>();

    /**
     * This constructor is used for a SubNode which has does not have a default command handler so that if the command is used, without the proper
     * arguments, such as /plugin loadData, the command user will receive a message of the possible arguments for /plugin loadData, such as /plugin loadData all or /plugin loadData {plugin-name}
     *
     * @param NODE_ARGUMENT The argument that will be handled by this command handler
     * @param NODES CommandNode[] representing the nodes you will have branching from this sub node
     */
    public SubNode(String NODE_ARGUMENT, EndNode... NODES) {
        super(NODE_ARGUMENT, ArgumentType.STATIC, null);
        processCommandNodes(NODES);
    }

    /**
     * This will create the Nodes map
     *
     * @param NODES EndNode[] the nodes attached to this SubNod
     */
    private void processCommandNodes(EndNode[] NODES) {
        for (EndNode endNode : NODES){
            String endNodeMapString = endNode.getMapString();
            this.NODES.put(endNodeMapString, endNode);
        }
    }

    public HashMap<String, EndNode> getNODES() {
        return NODES;
    }

    @Override
    public String toString() {
        return "SubNode{" +
                "NODES=" + NODES +
                "} " + super.toString();
    }
}