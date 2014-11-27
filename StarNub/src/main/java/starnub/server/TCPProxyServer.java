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

package starnub.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import starnub.StarNub;
import utilities.concurrency.thread.NamedThreadFactory;

import java.util.concurrent.Executors;


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

    public static void setNetworkThreading() {
        if (connectionBossGroup == null) {
            connectionBossGroup =
                    new NioEventLoopGroup(
                            1,
                            Executors.newCachedThreadPool(new NamedThreadFactory("StarNub - TCP Proxy : Connection Thread")));
        }
    }

    public static void setSocketSettings(){
        tcpNoDelay = true;
        socketBuffer = PooledByteBufAllocator.DEFAULT;
        recvBuffer = 512 * 1024;
        sendBuffer = 512 * 1024;
        writeHighWaterMark = 32 * 1024;
    }

    /**
     * This start() method will start a new TCP Server Socket
     * listening on the "starnub_port" variable found in the
     * user configuration. It will use CPU*2 of threads as
     * users connect, each is issues their own thread. When
     * threads have been expended new clients will start to share
     * threads. This also uses pooled memory inside of the Java
     * Virtual Machine (JVM) heap, which is faster but requires a little more
     * memory allocated at start up. If it used unpooled a slower method
     * the memory comes from outside of the JVM Heap
     */
    public static void start() {
        ServerBootstrap starNubInbound_TCP_Socket = new ServerBootstrap();
        starNubInbound_TCP_Socket
                .group(connectionBossGroup, new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, tcpNoDelay)
                .childOption(ChannelOption.ALLOCATOR, socketBuffer)
                .option(ChannelOption.SO_RCVBUF, recvBuffer)
                .option(ChannelOption.SO_SNDBUF, sendBuffer)
                .option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, writeHighWaterMark)
                .childHandler(new TCPProxyServerInitializer())
                .bind((int) StarNub.getConfiguration().getNestedValue("starnub_port", "starnub settings"));
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