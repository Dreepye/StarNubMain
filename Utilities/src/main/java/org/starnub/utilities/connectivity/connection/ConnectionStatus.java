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

package org.starnub.utilities.connectivity.connection;

/**
 * Represents StarNubs ConnectionStatus interface
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public interface ConnectionStatus {

    /**
     * Recommended: For connections use.
     * <p>
     * Uses: This will see if the connection is still alive
     *
     * @return boolean representing if the connection is alive
     */
    public boolean isConnected();

    /**
     * Recommended: For connections use.
     * <p>
     * Uses: This will attempt to disconnect this connection
     *
     * @return boolean representing if the disconnection was successful
     */
    public boolean disconnect();
}
