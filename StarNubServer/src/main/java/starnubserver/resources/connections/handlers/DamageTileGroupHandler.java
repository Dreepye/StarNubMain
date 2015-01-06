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

package starnubserver.resources.connections.handlers;

import io.netty.channel.ChannelHandlerContext;
import starbounddata.packets.Packet;
import starbounddata.packets.tile.DamageTileGroupPacket;
import starnubserver.cache.wrappers.PermissionCacheWrapper;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.events.packet.PacketEventHandler;
import utilities.cache.objects.BooleanCache;

public class DamageTileGroupHandler extends PacketEventHandler {

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
                playerSession.broadcastMessageToClient("StarNub", "You do not have permission to break tile. Permission required: \"starnub.tile.break\".");
            }
        }
    }
}
