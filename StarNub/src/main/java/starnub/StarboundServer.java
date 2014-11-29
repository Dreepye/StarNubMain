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
* this CodeHome Software.  If not, see <http://www.gnu.org/licenses/>.
*/

package starnub;

import starbounddata.color.GameColors;
import starboundmanager.StarboundManager;
import starnub.events.packet.PacketEventRouter;

/**
 * Represents the Starbound Server core.
 * <p>
 * This enum singleton holds and runs all things important
 * to Starbound and connect players.
 * <p>
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public class StarboundServer {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final StarboundServer instance = new StarboundServer();

    /**
     * This constructor is private - Singleton Pattern
     */
    private StarboundServer() {
    }

    private static final GameColors gameColors = GameColors.getInstance();
    private static final PacketEventRouter packetEventRouter = new PacketEventRouter();
    private static final StarboundManager starboundManager = new StarboundManager(StarNub.getStarNubEventRouter());
    private static final TCPProxyServer tcpProxyServer = new TCPProxyServer();
    private static final Thread udpProxyServer = new Thread(new UDPProxyServer(),"StarNub - UDP Proxy : Connection_Worker Thread");

    public static StarboundServer getInstance() {
        return instance;
    }

    public StarboundManager getStarboundManager() {
        return starboundManager;
    }
    public GameColors getGameColors() {
        return gameColors;
    }
    public PacketEventRouter getPacketEventRouter() {
        return packetEventRouter;
    }
    public TCPProxyServer getTcpProxyServer() {
        return tcpProxyServer;
    }
    public Thread getUdpProxyServer() {
        return udpProxyServer;
    }

    public void start() {

    }

}





