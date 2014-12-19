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

package starnubserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
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

    private static NioEventLoopGroup connectionBossGroup;
    private static NioEventLoopGroup connectionWorkerGroup;
    private static boolean tcpNoDelay;
    private static PooledByteBufAllocator socketBuffer;
    private static int recvBuffer;
    private static int sendBuffer;
    private static int writeHighWaterMark;
    private static int writeLowWaterMark;
    private static Channel BIND;
//    new EpollEventLoopGroup()
//    EpollSocketChannel.class

    public TCPProxyServer() {
        setNetworkThreading();
        setSocketSettings();
        start();
    }

    public static boolean isTcpNoDelay() {
        return tcpNoDelay;
    }

    public static PooledByteBufAllocator getSocketBuffer() {
        return socketBuffer;
    }

    public static int getRecvBuffer() {
        return recvBuffer;
    }

    public static int getSendBuffer() {
        return sendBuffer;
    }

    public static int getWriteHighWaterMark() {
        return writeHighWaterMark;
    }

    public static int getWriteLowWaterMark() {
        return writeLowWaterMark;
    }

    public static void setWriteLowWaterMark(int writeLowWaterMark) {
        TCPProxyServer.writeLowWaterMark = writeLowWaterMark;
    }

    public static void setNetworkThreading() {
        if (connectionBossGroup == null) {
            connectionBossGroup =
                    new NioEventLoopGroup(
                            1,
                            (new NamedThreadFactory("StarNub - TCP Proxy : Connection Thread")));
        }
        if (connectionWorkerGroup == null) {
            connectionWorkerGroup =
                    new NioEventLoopGroup(1, new NamedThreadFactory("StarNub - TCP Proxy : Worker Thread"));
        }

//        400,
//                Executors.newCachedThreadPool(new NamedThreadFactory("StarNub - TCP Proxy : Connection Worker"))

    }

    public static void setSocketSettings(){
        tcpNoDelay = true;
        socketBuffer = new PooledByteBufAllocator(true);
        writeHighWaterMark = 32 * 1024;
        writeLowWaterMark = 8 * 1024;
        recvBuffer = 512 * 1024;
        sendBuffer = 512 * 1024;
    }

    public void start() {
        ServerBootstrap starNubInbound_TCP_Socket = new ServerBootstrap();
        BIND = starNubInbound_TCP_Socket
                .group(connectionBossGroup, connectionWorkerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 9999)
//                .childOption(ChannelOption.SO_RCVBUF, recvBuffer)
//                .childOption(ChannelOption.SO_SNDBUF, sendBuffer)
//                .childOption(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, writeHighWaterMark)
//                .childOption(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, writeLowWaterMark)
                .childOption(ChannelOption.TCP_NODELAY, tcpNoDelay)
                .childOption(ChannelOption.ALLOCATOR, socketBuffer)
//                .childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .childHandler(new TCPProxyServerInitializer())
                .bind((int) StarNub.getConfiguration().getNestedValue("starnub_settings", "starnub_port")).channel();

    }


    public static void shutdownNetworkThreads(){
        if (connectionBossGroup != null) {
            connectionBossGroup.shutdownGracefully();
            connectionBossGroup = null;
        }
        if (connectionWorkerGroup != null){
            connectionWorkerGroup.shutdownGracefully();
            connectionWorkerGroup = null;
        }
    }

}