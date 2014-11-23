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

package server.cache.objects;


import cache.objects.TimeCache;

/**
 * Represents a ChannelHandlerContext Cache. This cache can be used in any Cache Wrapper
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class ChannelHandlerContextCache extends TimeCache {

    private volatile io.netty.channel.ChannelHandlerContext ctx;

    public ChannelHandlerContextCache(io.netty.channel.ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public io.netty.channel.ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(io.netty.channel.ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }
}
