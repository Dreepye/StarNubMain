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

package org.starnub.starnubserver.cache.wrappers;

import io.netty.channel.ChannelHandlerContext;
import org.starnub.starnubserver.StarNubTask;
import org.starnub.starnubserver.cache.objects.StarNubTaskCache;
import org.starnub.starnubserver.connections.player.session.PlayerSession;
import org.starnub.starnubserver.events.events.DisconnectData;
import org.starnub.starnubserver.events.events.StarNubEvent;
import org.starnub.starnubserver.events.starnub.StarNubEventSubscription;
import org.starnub.utilities.cache.objects.TimeCache;
import org.starnub.utilities.events.Priority;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PlayerAutoCancelTask extends PlayerCtxCacheWrapper {

    private PlayerCtxCacheWrapper PLAYER_CTX_CACHE;

    /**
     * Time specific constructor. RECOMMENDED.
     *
     * @param CACHE_OWNER String representing the owner of this utilities.cache, should be set to the plugins exact name
     */
    public PlayerAutoCancelTask(String CACHE_OWNER, String typeName) {
        super(CACHE_OWNER, CACHE_OWNER + " Auto Task Cancel - " + typeName, true, TimeUnit.MINUTES, 5, 5);
        PLAYER_CTX_CACHE = new PlayerCtxCacheWrapper(getCACHE_OWNER(), getCACHE_NAME(), false, TimeUnit.MINUTES, 5, 5);
    }

    public void registerTask(ChannelHandlerContext channelHandlerContext, StarNubTask starNubTask){
        this.addCache(channelHandlerContext, new StarNubTaskCache(starNubTask));
    }

    public boolean recentlyCanceled(ChannelHandlerContext channelHandlerContext){
        return PLAYER_CTX_CACHE.removeCache(channelHandlerContext) != null;
    }

    /**
     * This will prune cache
     */
    @Override
    public void cachePrune() {
        for (Map.Entry<ChannelHandlerContext, TimeCache> timeCacheEntry : this.getCACHE_MAP().entrySet()){
            ChannelHandlerContext channelHandlerContext = timeCacheEntry.getKey();
            TimeCache timeCache = timeCacheEntry.getValue();
            StarNubTaskCache starNubTaskCache = (StarNubTaskCache) timeCache;
            StarNubTask starNubTask = starNubTaskCache.getSTARNUB_TASK();
            ScheduledFuture<?> scheduledFuture = starNubTask.getScheduledFuture();
            boolean canPrune = timeCache.getCacheAge() > TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES);
            if (scheduledFuture.isDone() && canPrune){
                this.getCACHE_MAP().remove(channelHandlerContext);
            }
        }
    }

    /**
     * This will purge all cache
     */
    @Override
    public void cachePurge() {
        for (Map.Entry<ChannelHandlerContext, TimeCache> timeCacheEntry : getCACHE_MAP().entrySet()){
            ChannelHandlerContext channelHandlerContext = timeCacheEntry.getKey();
            TimeCache timeCache = timeCacheEntry.getValue();
            StarNubTaskCache starNubTaskCache = (StarNubTaskCache) timeCache;
            StarNubTask starNubTask = starNubTaskCache.getSTARNUB_TASK();
            ScheduledFuture<?> scheduledFuture = starNubTask.getScheduledFuture();
            if(scheduledFuture.isDone()){
               this.getCACHE_MAP().remove(channelHandlerContext);
            }
        }
    }

    /**
     * This will purge ChannelHandlerContext from the list when players disconnect
     */
    @Override
    public void registerEvents() {
        new StarNubEventSubscription(getCACHE_OWNER(), Priority.HIGH, "Player_Disconnected", eventData -> {
            DisconnectData disconnectData = (DisconnectData) eventData.getEVENT_DATA();
            PlayerSession playerSession = disconnectData.getPLAYER_SESSION();
            ChannelHandlerContext channelHandlerContext = playerSession.getCONNECTION().getCLIENT_CTX();
            TimeCache timeCache = removeCache(channelHandlerContext);
            if(timeCache != null){
                StarNubTaskCache starNubTaskCache = (StarNubTaskCache) timeCache;
                StarNubTask starNubTask = starNubTaskCache.getSTARNUB_TASK();
                ScheduledFuture<?> scheduledFuture = starNubTask.getScheduledFuture();
                if (!scheduledFuture.isDone()) {
                    scheduledFuture.cancel(true);
                    PLAYER_CTX_CACHE.addCache(channelHandlerContext, new TimeCache());
                }
                new StarNubEvent("StarNub_Task_Cancelled_Player_Disconnect", this);
            }
        });
    }
}
