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

package org.starnub.starnubserver.resources.predicates;

import io.netty.channel.ChannelHandlerContext;

import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;

public class CTXPredicates {

    public static Predicate<ChannelHandlerContext> isListed(Collection collection) {
        return collection::contains;
    }

    public static Predicate<ChannelHandlerContext> isNotListed(Collection collection) {
        return ctx -> !collection.contains(ctx);
    }

    public static Predicate<ChannelHandlerContext> isListed(Map map) {
        return map::containsKey;
    }

    public static Predicate<ChannelHandlerContext> isNotListed(Map map) {
        return ctx -> !map.containsKey(ctx);
    }
}
