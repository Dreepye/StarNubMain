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

package server.server.starbound;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import server.StarNub;

import java.util.List;
import java.util.Map;

/**
 * Represents the StarNub Query client which is used
 * to elicit a response from the starbounddata.packets.starbounddata.packets.server. If responsive
 * we will send a StarboundServerStatus event.
 * <p>
 * Credit goes to Netty.io (Asynchronous API) examples.
 * <p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
class QueryClient {

    /**
     * This method actually opens a starbounddata.packets.connection to the Starbound Server
     * using the configuration variables and the QueryPacketDecoder
     */
    public void serverQuery() {
        try {
            Bootstrap snTxQuerySb = new Bootstrap();
            snTxQuerySb
                    .group(StarNub.getServer().getStarboundQueryGroup())
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        public void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addFirst(new ByteToMessageDecoder(
                                                   ) {
                                                       /**
                                                        * This method is similar to PacketDecoderHandler method so no explanation will be given here
                                                        *
                                                        * @see {@link org.starnub.server.TCPProxyServer}
                                                        */
                                                       @Override
                                                       protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                                                           if (in.readableBytes() <= 3) {
                                                               return;
                                                           }
                                                           // Rewrite integrating the event system instead
                                                           int versionNumber = in.skipBytes(2).readInt();
//                                       System.out.println(versionNumber);
                                                           if (versionNumber == 0) {
                                                               StatusQuery.setOnline(false);
                                                           } else if (StarNub.getServer().getProtocolVersionPacket().getProtocolVersion() == 0) {
                                                               StarNub.getServer().updateProtocolVersionPacket(versionNumber);
                                                               StatusQuery.setOnline(true);
                                                           } else if (versionNumber != StarNub.getServer().getProtocolVersionPacket().getProtocolVersion()) {
                                                               StatusQuery.setOnline(false);
                                                           }
                                                           ctx.close();
                                                       }

                                                       /**
                                                        * This method is similar to PacketDecoderHandler method so no explanation will be given here
                                                        *
                                                        * @see {@link org.starnub.server.TCPProxyServerPacketDecoder}
                                                        */
                                                       @Override
                                                       public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                                           StarNub.getLogger().cFatPrint("StarNub", "Error in Query starbounddata.packets.Packet Decoder Handler.");

                                                       }
                                                   }

                            );
                        }
                    })
                    .connect("127.0.0.1", (int) ((Map)StarNub.getConfiguration().getConfiguration().get("starnub settings")).get("starbound_port")).channel().closeFuture().sync();
        } catch (Exception e) {

        }
    }
}

