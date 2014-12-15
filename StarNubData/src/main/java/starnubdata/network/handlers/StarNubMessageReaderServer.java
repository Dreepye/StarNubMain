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

package starnubdata.network.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import starnubdata.messages.StarNubMessage;
import starnubdata.messages.StarNubMessageBan;

public class StarNubMessageReaderServer extends SimpleChannelInboundHandler<StarNubMessage> {

//    private final EventRouter EVENT_ROUTER;
//
//    public StarNubMessageReaderServer(EventRouter EVENT_ROUTER) {
//        this.EVENT_ROUTER = EVENT_ROUTER;
//    }

    @Override
    @SuppressWarnings("unchecked")
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, StarNubMessage starNubMessage) throws Exception {

        System.out.println("I got a message");
        System.out.println(starNubMessage);
        StarNubMessageBan starNubMessageBan = (StarNubMessageBan) starNubMessage;
        System.out.println(starNubMessageBan.toString());
//        EVENT_ROUTER.eventNotify(new CentralEvent(starNubMessage.getTYPE(), starNubMessage));
    }


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        System.out.println("Test");
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
