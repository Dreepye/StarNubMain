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

package server.connections.states.connectionstatus;

import server.connections.Connection;

/**
 * Represents StarNubs Disconnected Status
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class Disconnected implements Status {

    Connection connection;

    public Disconnected(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void initializeConnection() {

    }

    @Override
    public void pendingConnection() {

    }

    @Override
    public void connect() {

    }

    @Override
    public void disconnect() {

    }
}
