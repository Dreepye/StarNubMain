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

package utilities.connectivity.connection;

/**
 * Represents StarNubs PendingConnection Status
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class Pending implements ConnectionStatus {

    private final Connection CONNECTION;

    public Pending(Connection CONNECTION) {
        this.CONNECTION = CONNECTION;
    }

    /**
     * Recommended: For internal use.
     * <p>
     * Uses: This will check to see if the connection is alive
     *
     * @return boolean representing if the connection is alive
     */
    @Override
    public boolean isConnected() {
        if (CONNECTION instanceof ProxyConnection){
            ProxyConnection proxyConnection = (ProxyConnection) CONNECTION;
            return proxyConnection.getCLIENT_CTX().channel().isActive() && proxyConnection.getSERVER_CTX().channel().isActive();
        } else {
            return CONNECTION.getCLIENT_CTX().channel().isActive();
        }
    }

    /**
     * Recommended: For internal use.
     * <p>
     * Uses: This will not work if the connection is disconnected
     *
     * @return boolean representing if the disconnect was successful
     */
    @Override
    public boolean disconnect() {
        return false;
    }
}
