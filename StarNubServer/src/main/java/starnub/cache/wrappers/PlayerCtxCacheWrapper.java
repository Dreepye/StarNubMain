/*
* Copyright (C) 2014 www.StarNub.org - Underbalanced
*
* This utilities.file is part of org.starnub a Java Wrapper for Starbound.
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

package starnub.cache.wrappers;

import io.netty.channel.ChannelHandlerContext;
import starnub.connections.player.session.Player;
import starnub.events.starnub.StarNubEventHandler;
import starnub.events.starnub.StarNubEventSubscription;
import utilities.cache.wrappers.CacheWrapper;
import utilities.events.types.Event;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Represents {@link io.netty.channel.ChannelHandlerContext} CacheWrapper which uses a {@link io.netty.channel.ChannelHandlerContext}
 * as a key and can be used with any CacheObjects found at (@link starnub.utilities.cache.objects}
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class PlayerCtxCacheWrapper extends CacheWrapper<ChannelHandlerContext> {

    /**
     * Basic constructor. RECOMMENDED.
     *
     * @param CACHE_OWNER                    String representing the owner of this utilities.cache, should be set to the plugins exact name
     * @param CACHE_NAME                     String representing the name for this specific utilities.cache implementation, to be used to task thread purging
     * @param AUTO_CACHE_PURGER              boolean you must create a auto utilities.cache purger implementation if once that you need does
     *                                       not exist already, if you will not be using this which is not recommended, use null in its place.
     * @param SCHEDULED_THREAD_POOL_EXECUTOR ScheduledThreadPoolExecutor of which we have scheduled a auto dumping task to
     * @param expectedElements               int representing the max number of elements that will be in the utilities.cache at one time
     * @param expectedThreads                int representing the max number of threads you expect to be accessing the elements at one time
     */
    public PlayerCtxCacheWrapper(String CACHE_OWNER, String CACHE_NAME, boolean AUTO_CACHE_PURGER, ScheduledThreadPoolExecutor SCHEDULED_THREAD_POOL_EXECUTOR, int expectedElements, int expectedThreads) {
        super(CACHE_OWNER, CACHE_NAME, AUTO_CACHE_PURGER, SCHEDULED_THREAD_POOL_EXECUTOR, expectedElements, expectedThreads);
    }

    /**
     * Time specific constructor. RECOMMENDED.
     *
     * @param CACHE_OWNER                    String representing the owner of this utilities.cache, should be set to the plugins exact name
     * @param CACHE_NAME                     String representing the name for this specific utilities.cache implementation, to be used to task thread purging
     * @param AUTO_CACHE_PURGER              boolean you must create a auto utilities.cache purger implementation if once that you need does
     *                                       not exist already, if you will not be using this which is not recommended, use null in its place.
     * @param SCHEDULED_THREAD_POOL_EXECUTOR ScheduledThreadPoolExecutor of which we have scheduled a auto dumping task to
     * @param expectedElements               int representing the max number of elements that will be in the utilities.cache at one time
     * @param expectedThreads                int representing the max number of threads you expect to be accessing the elements at one time
     * @param TIME_UNIT                      TimeUnit representing the time units to set the auto prune and purge to set 0 for off (Not recommended)
     * @param CACHE_PRUNE_TASK_TIME          int representing the time units to to automatically remove utilities.cache of this age at the set interval of this time unit
     * @param CACHE_PURGE_TAKE_TIME          int representing the time to purge all utilities.cache entirely
     */
    public PlayerCtxCacheWrapper(String CACHE_OWNER, String CACHE_NAME, boolean AUTO_CACHE_PURGER, ScheduledThreadPoolExecutor SCHEDULED_THREAD_POOL_EXECUTOR, int expectedElements, int expectedThreads, TimeUnit TIME_UNIT, int CACHE_PRUNE_TASK_TIME, int CACHE_PURGE_TAKE_TIME) {
        super(CACHE_OWNER, CACHE_NAME, AUTO_CACHE_PURGER, SCHEDULED_THREAD_POOL_EXECUTOR, expectedElements, expectedThreads, TIME_UNIT, CACHE_PRUNE_TASK_TIME, CACHE_PURGE_TAKE_TIME);
    }

    /**
     * This will purge ChannelHandlerContext from the list when players disconnect
     */
    @Override
    public void registerEvents() {
        new StarNubEventSubscription("StarNub", "Player_Disconnected", true, new StarNubEventHandler<Event<String>>() {
            @Override
            @SuppressWarnings("unchecked")
            public Event onEvent(Event eventData) {
                Player player = (Player) eventData.getEVENT_DATA();
                getCACHE_MAP().remove(player.getCLIENT_CTX());
                return null;
            }
        });
    }
}
