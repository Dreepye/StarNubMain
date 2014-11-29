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

package network;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import network.handlers.StarNubMessageDecoder;
import network.handlers.StarNubMessageEncoder;
import network.handlers.StarNubMessageReader;
import utilities.events.EventRouter;


public class StarNubMessageClientInitializer extends ChannelInitializer<SocketChannel> {

    private final EventRouter EVENT_ROUTER;

    public StarNubMessageClientInitializer(EventRouter EVENT_ROUTER) {
        this.EVENT_ROUTER = EVENT_ROUTER;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addFirst(new StarNubMessageDecoder(ClassResolvers.weakCachingResolver(ClassLoader.getSystemClassLoader())));
        ch.pipeline().addFirst(new StarNubMessageEncoder());
        ch.pipeline().addLast(new StarNubMessageReader(EVENT_ROUTER));
    }
}
