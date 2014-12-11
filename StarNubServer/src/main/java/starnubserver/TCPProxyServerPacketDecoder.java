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

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.handler.codec.ReplayingDecoder;
import starbounddata.packets.Packet;
import starbounddata.packets.Packets;
import starnubserver.cache.wrappers.IPCacheWrapper;
import starnubserver.connections.player.StarNubProxyConnection;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.events.events.StarNubEvent;
import utilities.cache.objects.IntegerCache;
import utilities.events.EventSubscription;
import utilities.exceptions.CacheWrapperOperationException;
import utilities.numbers.RandomNumber;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static utilities.compression.Zlib.decompress;

/**
 * Represents the starbounddata.packets.Packet Decoder for StarNub Client and Server Connections.
 * <p>
 * This class SHOULD NOT BE REUSED.
 * <p>
 * When this class is instantiated by TCPProxyServer's Channel Initializer,
 * it will set the side its on in order to set the packet routing variables
 * known as ChannelHandlerContexts or "CTX".
 * <p>
 * Credit goes to Netty.io (Asynchronous API) examples.
 * <p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
class TCPProxyServerPacketDecoder extends ReplayingDecoder<TCPProxyServerPacketDecoder.DecoderState> {

    private ChannelHandlerContext destinationCTX;
    private final Packet.Direction CONNECTION_SIDE;
    private final HashMap<Byte, Packet> PACKET_POOL = new HashMap<>();
    private int vlqLength;
    private int payloadLength;
    private boolean compressed;
    private int packetIDIndex;
    private Packet packet;

    private StarNubProxyConnection starNubProxyConnection;

    public TCPProxyServerPacketDecoder(Packet.Direction connectionSide) {
        super(DecoderState.READ_PACKET_ID );
        this.CONNECTION_SIDE = connectionSide;
    }

    private TCPProxyServerPacketDecoder(Packet.Direction connectionSide, ChannelHandlerContext clientCTX) {
        super(DecoderState.READ_PACKET_ID );
        this.CONNECTION_SIDE = connectionSide;
        this.destinationCTX = clientCTX;
    }

    public enum DecoderState {
        READ_PACKET_ID,
        READ_VLQ,
        ROUTE_DATA,
        READ_PAYLOAD
    }

    public void clearVLQ(){
        vlqLength = 0;
        payloadLength = 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        switch (state()) {
            case READ_PACKET_ID: {
                packetIDIndex = in.readerIndex();
                byte packetId = in.getByte(packetIDIndex);
                packet = PACKET_POOL.get(packetId);
                checkpoint(DecoderState.READ_VLQ);
            }
            case READ_VLQ: {
                clearVLQ();
                int tempIndexMarker = packetIDIndex + 1;
                while (vlqLength <= 10) {
                    int tmpByte = in.getByte(tempIndexMarker);
                    payloadLength = (payloadLength << 7) | (tmpByte & 0x7f);
                    vlqLength++;
                    if ((tmpByte & 0x80) == 0) {
                        break;
                    }
                    tempIndexMarker++;
                }
                if ((payloadLength & 1) == 0x00) {
                    payloadLength = payloadLength >> 1;
                } else {
                    payloadLength = -((payloadLength >> 1) + 1);
                }
                compressed = payloadLength < 0;
                if (compressed) {
                    payloadLength = -payloadLength;
                }
                checkpoint(DecoderState.READ_PAYLOAD);
            }
            case READ_PAYLOAD: {
                if (packet != null) {
                    HashSet<EventSubscription> hashSet = StarNub.getStarboundServer().getPacketEventRouter().getEVENT_SUBSCRIPTION_MAP().get(packet.getClass());
                    /* Handle Packet if there is an event handler for it, else do not create objects */
                    if (hashSet != null) {
                        in.skipBytes(1 + vlqLength);
                        if (compressed) {
                            packet.read(Unpooled.wrappedBuffer(1, decompress(in.readBytes(payloadLength).array())));
                        } else {
                            packet.read(in.readBytes(payloadLength));
                        }
                        for (EventSubscription<Packet> packetEventSubscription : hashSet) {
                            if (packet.isRecycle()) {
                                break;
                            }
                            packetEventSubscription.getEVENT_HANDLER().onEvent(packet);
                        }
                        /* Write packet out, if not recycling */
                        if (!packet.isRecycle()) {
                            packet.routeToDestination();
                        } else {
                            packet.resetRecycle();
                        }
                    } else {
                        destinationCTX.writeAndFlush(in.readSlice(1 + vlqLength + payloadLength).retain(), destinationCTX.voidPromise());
                    }
                } else {
                    destinationCTX.writeAndFlush(in.readSlice(1 + vlqLength + payloadLength).retain(), destinationCTX.voidPromise());
                }
                checkpoint(DecoderState.READ_PACKET_ID);
                break;
            }
            default:
                throw new Error("Error Decoding - Reached the unreachable void.");
        }
    }

    /**
     * When we add this handler we want to set the routing information for both
     * sides, if the handler is instantiated on the StarNub Server Socket,
     * then this will spawn a starbounddata.packets.connection through the proxy to the Starbound Server.
     *
     * @param ctx ChannelHandlerContext represents the context this handler is attached to
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        StarNub.getConnections().getOPEN_SOCKETS().put(ctx, System.currentTimeMillis());
         if (CONNECTION_SIDE == Packet.Direction.TO_STARBOUND_CLIENT) {
            new StarNubEvent("StarNub_Socket_Connection_Attempt_Client", ctx);
            setClientConnection(ctx);
            new StarNubEvent("StarNub_Socket_Connection_Success_Client", ctx);
        }
        setPACKET_POOL(ctx);
    }

    private void setClientConnection(ChannelHandlerContext ctx) throws CacheWrapperOperationException {
        InetAddress connectingIp = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress();
        IPCacheWrapper lastActiveCache = StarNub.getConnections().getINTERNAL_IP_WATCHLIST();
        IntegerCache cache = (IntegerCache) lastActiveCache.getCache(connectingIp);
        int randomInt = RandomNumber.randInt(3000, 6000); //DEBUG CLOSE THREADS CHECK - RESOURCE CLEAN UP
        if (cache != null) {
            int cachedTimer = cache.getInteger();
            boolean isPastTime = cache.isPastDesignatedTimeRefreshTimeNowIfPast(cachedTimer);
            System.out.println(cachedTimer);
            if(!isPastTime){
                cache.setInteger(cachedTimer + randomInt);
                ctx.close();
            } else {
                cache.setInteger(randomInt);
                openServerConnection(ctx);
            }
        } else {
            lastActiveCache.addCache(connectingIp, new IntegerCache(randomInt));
            openServerConnection(ctx);
        }
    }

    private void openServerConnection(ChannelHandlerContext ctx){
        new StarNubEvent("StarNub_Socket_Connection_Attempt_Server", ctx);
        Bootstrap starNubMainOutboundSocket = new Bootstrap();
        starNubMainOutboundSocket
                .group(ctx.channel().eventLoop())
                .channel(ctx.channel().getClass())
                .option(ChannelOption.TCP_NODELAY, TCPProxyServer.isTcpNoDelay())
                .option(ChannelOption.ALLOCATOR, TCPProxyServer.getSocketBuffer())
                .option(ChannelOption.SO_RCVBUF, TCPProxyServer.getRecvBuffer())
                .option(ChannelOption.SO_SNDBUF, TCPProxyServer.getSendBuffer())
                .option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, TCPProxyServer.getWriteHighWaterMark())
                .handler(new TCPProxyServerPacketDecoder(Packet.Direction.TO_STARBOUND_SERVER, ctx));
        ChannelFuture f = starNubMainOutboundSocket.connect("127.0.0.1", (int) (StarNub.getConfiguration().getNestedValue("starnub_settings", "starbound_port")));
        destinationCTX = f.channel().pipeline().firstContext();
        new StarNubEvent("StarNub_Socket_Connection_Success_Server", ctx);
        starNubProxyConnection = new StarNubProxyConnection(StarNub.getStarNubEventRouter(), StarNubProxyConnection.ConnectionProcessingType.PLAYER_NO_DECODING, ctx, destinationCTX);
    }

    /**
     * Recommended: For connections StarNub usage.
     * <p>
     * Uses: This will create new packets from the packet cache and insert them into a HashMap Packet Pool to be used in
     * this sockets connection.
     *
     * @param ctx ChannelHandlerContext representing this chanels context
     */
    @SuppressWarnings("unchecked")
    private void setPACKET_POOL(ChannelHandlerContext ctx) {
        HashMap<Packets, Class> packetClasses = Packets.getPacketClasses();
        for (Map.Entry<Packets, Class> packetsClassEntry : packetClasses.entrySet()) {
            Class packetClass = packetsClassEntry.getValue();
            Packets packet = packetsClassEntry.getKey();
            Constructor constructor;
            Packet packetToConstruct = null;
            Packet.Direction direction = null;
            if (packet.getDirection() != CONNECTION_SIDE) {
                direction = CONNECTION_SIDE == Packet.Direction.TO_STARBOUND_CLIENT ? Packet.Direction.TO_STARBOUND_SERVER : Packet.Direction.TO_STARBOUND_CLIENT;
                try {
                    constructor = packetClass.getConstructor(new Class[]{Packet.Direction.class, ChannelHandlerContext.class, ChannelHandlerContext.class});
                    packetToConstruct = (Packet) constructor.newInstance(new Object[]{direction, ctx, destinationCTX});
                    PACKET_POOL.put(packet.getPacketId(), packetToConstruct);
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void channelInactive (ChannelHandlerContext ctx) {
        closeConnection(ctx);
    }

    /**
     * This is set so we can collect information about starbounddata.packets.connection issues.
     *
     * @param ctx   ChannelHandlerContext represents the context this handler is attached to
     * @param cause Throwable reason
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace(); /* Debug Print */
        closeConnection(ctx);
    }

    @Override
    protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
        closeConnection(ctx);
    }

    private void closeConnection(ChannelHandlerContext ctx){
        PlayerSession playerSession = StarNub.getConnections().getCONNECTED_PLAYERS().getOnlinePlayerByAnyIdentifier(ctx);
        playerSession.disconnectReason("Closed_By_Handler");
    }
}