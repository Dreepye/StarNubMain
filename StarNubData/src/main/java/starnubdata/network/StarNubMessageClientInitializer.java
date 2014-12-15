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

package starnubdata.network;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.ssl.SslContext;
import starnubdata.messages.StarNubMessage;
import starnubdata.network.handlers.StarNubMessageReaderClient;


public class StarNubMessageClientInitializer extends ChannelInitializer<SocketChannel> {

//    private final EventRouter EVENT_ROUTER;
//
//    public StarNubMessageClientInitializer(EventRouter EVENT_ROUTER) {
//        this.EVENT_ROUTER = EVENT_ROUTER;
//    }
    private final SslContext SSL_CTX;
    private final String HOST;
    private final int PORT;

    public StarNubMessageClientInitializer(SslContext SSL_CTX, String HOST, int PORT) {
        this.SSL_CTX = SSL_CTX;
        this.HOST = HOST;
        this.PORT = PORT;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
//        SslContext sslContext = SslContext.newClientContext(new File("c_pub.pem"));
//        SSLEngine sslEngine = sslContext.newEngine(ch.alloc());
//        ch.pipeline().addFirst(new SslHandler(sslEngine));
        ch.pipeline().addFirst(new ObjectDecoder(ClassResolvers.weakCachingResolver(StarNubMessage.class.getClassLoader())));
        ch.pipeline().addFirst(new ObjectEncoder());
        ch.pipeline().addLast(new StarNubMessageReaderClient());
    }
}
