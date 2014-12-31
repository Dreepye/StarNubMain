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

package starnubserver.servers.starbound;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import utilities.concurrent.thread.NamedThreadFactory;


/**
 * Represents the TCPProxyServer (Public Facing).
 * <p>
 * This class SHOULD NOT BE REUSED.
 * <p>
 * The NioEventLoopGroup specifies a specific amount
 * of threads. The default amount of threads are (CPUs * 2).
 * <p>
 * Credit goes to Netty.io (Asynchronous API) examples.
 * <p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
class TCPProxyServer {

    private final int starnubPort;
    private final String starboundAddress;
    private final int starboundPort;
    private EventLoopGroup connectionBossGroup;
    private EventLoopGroup connectionWorkerGroup;
    private Class<NioServerSocketChannel> channelClass;
    private final int serverBacklog = 9999;
    private final boolean noDelay = true;
    private final PooledByteBufAllocator socketBuffer = new PooledByteBufAllocator(true);
    private Channel serverChannel;

    public TCPProxyServer(boolean isLinux, int starnubPort, String starboundAddress, int starboundPort) {
        this.starnubPort = starnubPort;
        this.starboundAddress = starboundAddress;
        this.starboundPort = starboundPort;
        setNetworkThreading(isLinux);
    }

    public PooledByteBufAllocator getSocketBuffer() {
        return socketBuffer;
    }

    public Channel getServerChannel() {
        return serverChannel;
    }

    public boolean isNoDelay() {
        return noDelay;
    }

    public int getServerBacklog() {
        return serverBacklog;
    }

    public EventLoopGroup getConnectionWorkerGroup() {
        return connectionWorkerGroup;
    }

    public EventLoopGroup getConnectionBossGroup() {
        return connectionBossGroup;
    }

    public void setNetworkThreading(boolean isLinux) {
        boolean useEpoll = false; //(boolean) StarNub.getConfiguration().getNestedValue("starnub_settings", "use_linux_epoll");
        if (useEpoll && isLinux){
//            channelClass = EpollServerSocketChannel.class;
//            connectionBossGroup = new EpollEventLoopGroup(1, (new NamedThreadFactory("StarNub - TCP Proxy : Connection Thread")));
//            connectionWorkerGroup = new EpollEventLoopGroup(500, new NamedThreadFactory("StarNub - TCP Proxy : Worker Thread"));
        } else {
            channelClass = NioServerSocketChannel.class;
            connectionBossGroup = new NioEventLoopGroup(1, (new NamedThreadFactory("StarNub - TCP Proxy : Connection Thread")));
            connectionWorkerGroup = new NioEventLoopGroup(500, new NamedThreadFactory("StarNub - TCP Proxy : Worker Thread"));
        }
    }//con closed

    public void start() {
        ServerBootstrap starNubInbound_TCP_Socket = new ServerBootstrap();
        serverChannel = starNubInbound_TCP_Socket
                .group(connectionBossGroup, connectionWorkerGroup)
                .channel(channelClass)
                .option(ChannelOption.SO_BACKLOG, serverBacklog)
                .childOption(ChannelOption.TCP_NODELAY, noDelay)
                .childOption(ChannelOption.ALLOCATOR, socketBuffer)
                .childHandler(new TCPProxyServerInitializer(starboundAddress, starboundPort))
                .bind(starnubPort).channel();
    }

    public void shutdown(){
        serverChannel.close();
        shutdownNetworkThreads();
    }

    public void shutdownNetworkThreads(){
        connectionBossGroup.shutdownGracefully();
        connectionWorkerGroup.shutdownGracefully();
    }
}