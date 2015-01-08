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

package org.starnub.starnubdata.network.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.starnub.starnubdata.central.handlers.CentralReaderClient;


public class StarNubDataClientInitializer extends ChannelInitializer<SocketChannel> {

    private final String HOST;
    private final int PORT;
    private final Object MESSAGE_TYPE;

    public StarNubDataClientInitializer(Object MESSAGE_TYPE, String HOST, int PORT) {
        this.HOST = HOST;
        this.PORT = PORT;
        this.MESSAGE_TYPE = MESSAGE_TYPE;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addFirst(new ObjectDecoder(ClassResolvers.weakCachingResolver(MESSAGE_TYPE.getClass().getClassLoader())));
        ch.pipeline().addFirst(new ObjectEncoder());
        ch.pipeline().addLast(new CentralReaderClient());
    }
}
