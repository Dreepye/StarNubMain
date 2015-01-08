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

package org.starnub.utilities.connectivity.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.starnub.utilities.concurrent.thread.NamedThreadFactory;


public class TCPServer {

    private NioEventLoopGroup connectionBossGroup;
    private NioEventLoopGroup connectionWorkerGroup;

    public TCPServer(String connectThreadName, String workerThreadName) {
        connectionBossGroup = new NioEventLoopGroup(1, new NamedThreadFactory(connectThreadName));
        connectionWorkerGroup = new NioEventLoopGroup(1, new NamedThreadFactory(workerThreadName));
    }


    public Channel start(int port, ChannelInitializer<SocketChannel> channelInitializer) {
        ServerBootstrap sb = new ServerBootstrap();
        sb.group(connectionBossGroup, connectionWorkerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(channelInitializer);
        return sb.bind(port).channel();

    }

    public void shutdown() {
        if (connectionBossGroup != null) {
            connectionBossGroup.shutdownGracefully();
            connectionBossGroup = null;
        }
        if (connectionWorkerGroup != null) {
            connectionWorkerGroup.shutdownGracefully();
            connectionWorkerGroup = null;
        }
    }
}