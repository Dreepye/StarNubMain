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
import starbounddata.chat.ChatReceiveChannel;
import starbounddata.packets.Packet;
import starbounddata.packets.tile.DamageTileGroupPacket;
import starnubserver.StarNub;
import starnubserver.cache.wrappers.PlayerCtxCacheWrapper;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.events.packet.PacketEventHandler;
import utilities.cache.objects.BooleanCache;

public class DamageTileGroupHandler extends PacketEventHandler {

    private final PlayerCtxCacheWrapper CTX_CACHE;//TODO Permission reload clear casch

    public DamageTileGroupHandler(int expectedPlayers) {
        CTX_CACHE = new PlayerCtxCacheWrapper("StarNub","StarNub - Tile Break Permission Check", true, StarNub.getTaskManager(), expectedPlayers, expectedPlayers);
    }


    /**
     * This is for internal use - permission checks.
     *
     * @param eventData Packet representing the packet being routed
     */
    @Override
    public void onEvent(Packet eventData) {
        DamageTileGroupPacket damageTileGroupPacket = (DamageTileGroupPacket) eventData;
        long time = System.nanoTime();

        ChannelHandlerContext ctx = damageTileGroupPacket.getDESTINATION_CTX();
//MOVE CACHE WIRING ELSE WHERE INERNAL
        BooleanCache cache = (BooleanCache) CTX_CACHE.getCache(ctx);
        if (cache != null){
            if (!cache.isBool()){
                PlayerSession playerSession = PlayerSession.getPlayerSession(damageTileGroupPacket);
                damageTileGroupPacket.recycle();
                playerSession.sendChatMessage("StarNub", ChatReceiveChannel.UNIVERSE, "You do not have permission to break tiles. Permission required: \"starnub.tile.break\".");
                return;
            }
        } else {
            PlayerSession playerSession = PlayerSession.getPlayerSession(damageTileGroupPacket);
            /* Check for Tile Break Permission */
            boolean hasPermission = playerSession.hasPermission("starnub.tile.break", true);
            CTX_CACHE.addCache(ctx, new BooleanCache(hasPermission));
            if (!hasPermission) {
                damageTileGroupPacket.recycle();
                playerSession.sendChatMessage("StarNub", ChatReceiveChannel.UNIVERSE, "You do not have permission to break tiles. Permission required: \"starnub.tile.break\".");
                return;
            }
        }


        System.out.println(System.nanoTime() - time);
    }
}
