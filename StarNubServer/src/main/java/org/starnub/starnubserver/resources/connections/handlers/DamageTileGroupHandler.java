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

package org.starnub.starnubserver.resources.connections.handlers;

import io.netty.channel.ChannelHandlerContext;
import org.starnub.starbounddata.packets.Packet;
import org.starnub.starbounddata.packets.tile.DamageTileGroupPacket;
import org.starnub.starnubserver.cache.wrappers.PermissionCacheWrapper;
import org.starnub.starnubserver.connections.player.session.PlayerSession;
import org.starnub.starnubserver.events.packet.PacketEventHandler;
import org.starnub.utilities.cache.objects.BooleanCache;

public class DamageTileGroupHandler implements PacketEventHandler {

    private final PermissionCacheWrapper CTX_CACHE = new PermissionCacheWrapper("StarNub", "starnub.tile.break");

    public DamageTileGroupHandler() {
    }

    /**
     * This is for internal use - permission checks.
     *
     * @param eventData Packet representing the packet being routed
     */
    @Override
    public void onEvent(Packet eventData) {
        DamageTileGroupPacket damageTileGroupPacket = (DamageTileGroupPacket) eventData;
        ChannelHandlerContext ctx = damageTileGroupPacket.getSENDER_CTX();
        BooleanCache cache = (BooleanCache) CTX_CACHE.getCache(ctx);
        if (!cache.isBool()){
            PlayerSession playerSession = PlayerSession.getPlayerSession(damageTileGroupPacket);
            damageTileGroupPacket.recycle();
            if (cache.isPastDesignatedTimeRefreshTimeNowIfPast(5000)) {
                playerSession.sendBroadcastMessageToClient("StarNub", "You do not have permission to break tile. Permission required: \"starnub.tile.break\".");
            }
        }
    }
}
