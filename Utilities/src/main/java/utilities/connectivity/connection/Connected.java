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
 * Represents StarNubs Connected Status
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class Connected implements ConnectionStatus {

    private final Connection CONNECTION;

    public Connected(Connection CONNECTION) {
        this.CONNECTION = CONNECTION;
    }

    /**
     * Recommended: For internal use.
     * <p>
     * Uses: This will see if the connection is still alive
     *
     * @return boolean representing if the connection is alive
     */
    @Override
    public boolean isConnected() {
        boolean connected;
        if (CONNECTION instanceof ProxyConnection){
            ProxyConnection proxyConnection = (ProxyConnection) CONNECTION;
            connected = proxyConnection.getCLIENT_CTX().channel().isActive() && proxyConnection.getSERVER_CTX().channel().isActive();
        } else {
            connected = CONNECTION.getCLIENT_CTX().channel().isActive();
        }
        if (!connected){
            CONNECTION.setConnectionStatus(CONNECTION.getDISCONNECTED());
        }
        return connected;
    }

    /**
     * Recommended: For internal use.
     * <p>
     * Uses: This will attempt to disconnect this connection
     *
     * @return boolean representing if the disconnection was successful
     */
    @Override
    public boolean disconnect() {
        CONNECTION.getCLIENT_CTX().close(CONNECTION.getCLIENT_CTX().voidPromise());
        if (CONNECTION instanceof ProxyConnection){
            ProxyConnection proxyConnection = (ProxyConnection) CONNECTION;
            proxyConnection.getSERVER_CTX().close(proxyConnection.getSERVER_CTX().voidPromise());
        }
        boolean connected = isConnected();
        if (!connected){
            CONNECTION.setConnectionStatus(CONNECTION.getDISCONNECTED());
        }
        return connected;
    }
}
