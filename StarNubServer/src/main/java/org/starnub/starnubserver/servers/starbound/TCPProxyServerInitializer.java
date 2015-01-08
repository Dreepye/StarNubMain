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

package org.starnub.starnubserver.servers.starbound;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import org.starnub.starbounddata.packets.Packet;

/**
 * Creates a newly configured {@link io.netty.channel.ChannelPipeline} for a new channel.
 * <p>
 * Credit goes to Netty.io (Asynchronous API) examples.
 * <p>
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
class TCPProxyServerInitializer extends ChannelInitializer<SocketChannel> {

    private final String starboundAddress;
    private final int starboundPort;

    public TCPProxyServerInitializer(String starboundAddress, int starboundPort) {
        this.starboundAddress = starboundAddress;
        this.starboundPort = starboundPort;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addFirst(new TCPProxyServerPacketDecoder(starboundAddress, starboundPort, Packet.Direction.TO_STARBOUND_CLIENT));
    }
}
