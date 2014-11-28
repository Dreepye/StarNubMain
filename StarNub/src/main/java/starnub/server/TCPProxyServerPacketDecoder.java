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

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.handler.codec.ReplayingDecoder;
import org.reflections.Reflections;
import starbounddata.packets.Packet;
import starbounddata.packets.misc.PassThroughPacket;
import starnub.StarNub;
import starnub.connections.player.StarNubProxyConnection;
import starnub.events.events.EventsInternals;
import starnub.events.events.StarNubEvent;
import utilities.connectivity.connection.ProxyConnection;
import utilities.events.EventSubscription;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.*;

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
    private final String CONNECTION_SIDE;
    private HashMap<Byte, Packet> packetPool;
    private int vlqLength;
    private int payloadLength;
    private boolean compressed;
    private int packetIDIndex;
    private Packet packet;

    private StarNubProxyConnection starNubProxyConnection;

    public TCPProxyServerPacketDecoder(String connectionSide) {
        super(DecoderState.READ_PACKET_ID );
        this.CONNECTION_SIDE = connectionSide;
    }

    private TCPProxyServerPacketDecoder(String connectionSide, ChannelHandlerContext clientCTX) {
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
                packet = packetPool.get(packetId);
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
                HashSet<EventSubscription> hashSet = StarNub.getServer().getPacketEventRouter().getEVENT_SUBSCRIPTION_MAP().get(packet.getClass());
                /* Handle Packet if there is an event handler for it, else do not create objects */
                if (hashSet != null) {
                    in.skipBytes(1+vlqLength);
                    try {
                        if (compressed) {
                            packet.read(Unpooled.wrappedBuffer(1, decompress(in.readBytes(payloadLength).array())));
                        } else {
                            packet.read(in.readBytes(payloadLength));
                        }
                    } catch (ArrayIndexOutOfBoundsException e){
                        return;
                    }
                    for (EventSubscription<Packet> packetEventSubscription : hashSet) {
                        if (packet.isRecycle()) {
                            break;
                        }
                        packet = packetEventSubscription.getEVENT_HANDLER().onEvent(packet);
                    }
                    /* Write Packet Out, if not recycling */
                    if (!packet.isRecycle()) {
                        packet.routeToDestination();
                    } else {
                        packet.resetRecycle();
                    }
                } else {
                    destinationCTX.writeAndFlush(in.readSlice(1 + vlqLength + payloadLength).retain(), destinationCTX.voidPromise());
                }
                packetPool.put(packet.getPACKET_ID(), packet);
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
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {//TODO add connection
        if (CONNECTION_SIDE.equalsIgnoreCase("ServerSide")) {
            new StarNubEvent("StarNub_Socket_Connection_Attempt_Server", ctx);
            EventsInternals.eventSend_StarNub_Socket_Connection_Success_Server(ctx);
            setPacketPool(ctx);
            new StarNubEvent("StarNub_Socket_Connection_Success_Server", ctx);
        } else if (CONNECTION_SIDE.equalsIgnoreCase("ClientSide")) {
            setClientConnection(ctx);
        }
        StarNub.getConnections().getOPEN_SOCKETS().put(ctx, System.currentTimeMillis());
    }

    private void setClientConnection(ChannelHandlerContext ctx){
        InetAddress connectingIp = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress();
        try {
            boolean isBlocked = StarNub.getConnections().getINTERNALLY_BLOCKED_IPS().collectionContains(connectingIp, "ips");
            if (isBlocked) {
                new StarNubEvent("StarNub_Socket_Connection_Failed_Blocked_IP_Client", ctx);
                ctx.close();
                return;
            }
        } catch (IOException e) {
            /* Silent Catch */
        }
        Bootstrap starNubMainOutboundSocket = new Bootstrap();
        starNubMainOutboundSocket
                .group(ctx.channel().eventLoop())
                .channel(ctx.channel().getClass())
                .option(ChannelOption.TCP_NODELAY, TCPProxyServer.isTcpNoDelay())
                .option(ChannelOption.ALLOCATOR, TCPProxyServer.getSocketBuffer())
                .option(ChannelOption.SO_RCVBUF, TCPProxyServer.getRecvBuffer())
                .option(ChannelOption.SO_SNDBUF, TCPProxyServer.getSendBuffer())
                .option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, TCPProxyServer.getWriteHighWaterMark())
                .handler(new TCPProxyServerPacketDecoder("ServerSide", ctx));
        ChannelFuture f = starNubMainOutboundSocket.connect("127.0.0.1", (int) (StarNub.getConfiguration().getNestedValue("starbound_port", "starnub settings")));
        destinationCTX = f.channel().pipeline().firstContext();
        EventsInternals.eventSend_StarNub_Socket_Connection_Success_Client(ctx);
        setPacketPool(ctx);
        new StarNubEvent("StarNub_Socket_Connection_Success_Server", ctx);
        starNubProxyConnection = new StarNubProxyConnection(StarNub.getStarNubEventRouter(), ctx, destinationCTX);
    }

    @SuppressWarnings("unchecked")
    private void setPacketPool(ChannelHandlerContext ctx) {


//        this.packetPool = new HashMap<>();
//        Map<String, Object> packetCountMap = new YamlLoader().resourceYamlLoader("servers/resources.yml");
//        int packetCount = (int) packetCountMap.get("packet_count");
//        for (int index = 0; index <= packetCount; index++) {
//            try {
//                PassThroughPacket packetToSet = PassThroughPacket.class.newInstance();
//                packetToSet.setPacketId((byte) index);
//                this.packetPool.put((byte) index, packetToSet);
//            } catch (InstantiationException | IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }
//        Map<String, Object> implementedPackets = new YamlLoader().resourceYamlLoader("starboundmanager/starbound_packet_types.yml");
//        Reflections reflections = new Reflections("org.starnub.starbounddata.packets.starbounddata.packets.starnub.starbounddata.packets");
//        Set<Class<? extends Packet>> allClasses = reflections.getSubTypesOf(Packet.class);
//        for (Object value : implementedPackets.values()) {
//            ArrayList<Object> list = (ArrayList<Object>) value;
//            for (Class c : allClasses) {
//                String className = c.getName();
//                if (className.substring(className.lastIndexOf(".") + 1).equals((String) list.get(0))) {
//                    /* Add to known packet */
//                    try {
//                        this.packetPool.replace((byte) Integer.parseInt((String) list.get(1)), Class.forName(className).asSubclass(Packet.class).newInstance());
//                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//        for (Packet packet : this.packetPool.values()) {
//            packet.setDestinationSenderCTX(destinationCTX, ctx);
//        }
    }

    /**
     * This method will send a Player Disconnect Event {@link starnub.events.events.PlayerEvent}
     *
     * After channel is inactive we want to remove the channel from
     * the starbounddata.packets.global list.
     * @param ctx
     */
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
//        cause.printStackTrace();
        closeConnection(ctx);
    }

    @Override
    protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
        closeConnection(ctx);
    }

    private void closeConnection(ChannelHandlerContext ctx){
        try {
            StarNub.getConnections().getCONNECTED_PLAYERS().
            StarNub.getServer().getConnectionss().playerDisconnectPurposely(ctx, "Disconnected");
            ctx.channel().close();
            ctx.channel().eventLoop().shutdownGracefully();
        } catch (NullPointerException e){
            /* Silent Catch */
        }
    }
}