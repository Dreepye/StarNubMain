package starnub;/*
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

import starnub.resources.BannedIPs;
import starnub.resources.internalmaps.ConnectedPlayers;
import starnub.resources.internalmaps.OpenConnections;
import starnub.resources.internalmaps.OpenSockets;

public class Connections {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final Connections instance = new Connections();

    /**
     * This constructor is private - Singleton Pattern
     */
    private Connections(){}

    /**
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This will set and get Connections Singleton instance
     ** @return Connections Singleton Instance
     */
    @SuppressWarnings("unchecked")
    public static Connections getInstance() {
        return instance;
    }

    private static final BannedIPs INTERNALLY_BLOCKED_IPS = new BannedIPs(StarNub.getResourceManager().getStarnubResources());

    private final OpenSockets OPEN_SOCKETS = new OpenSockets();
    private final OpenConnections OPEN_CONNECTIONS = new OpenConnections();
    private final ConnectedPlayers CONNECTED_PLAYERS = new ConnectedPlayers();

    public BannedIPs getINTERNALLY_BLOCKED_IPS() {
        return INTERNALLY_BLOCKED_IPS;
    }

    public OpenSockets getOPEN_SOCKETS() {
        return OPEN_SOCKETS;
    }

    public OpenConnections getOPEN_CONNECTIONS() {
        return OPEN_CONNECTIONS;
    }

    public ConnectedPlayers getCONNECTED_PLAYERS() {
        return CONNECTED_PLAYERS;
    }


/**
     * Restrictions map
     *
     * Open Sockets - All open sockets go here no class
     * Connections - Remove if becomes player Connection any connection goes here but removed per type and purged after 5 minutes of not being removed
     *
     * Connected Players (Pending is now just a status)
     *
     * Purging Task for Open Sockets and Pending Players
     *
     * Banned Stuff
     *
     */




    //List for packet 7 and other connect packet







}


