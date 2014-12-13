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

package starnubserver.cache.wrappers;

        import io.netty.channel.ChannelHandlerContext;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.events.events.DisconnectData;
import starnubserver.events.events.StarNubEvent;
import starnubserver.events.starnub.StarNubEventHandler;
import starnubserver.events.starnub.StarNubEventSubscription;
import utilities.events.Priority;

import java.util.concurrent.TimeUnit;

/**
 * Represents {@link io.netty.channel.ChannelHandlerContext} CacheWrapper which uses a {@link io.netty.channel.ChannelHandlerContext}
 * as a key and can be used with any CacheObjects found at (@link starnubserver.utilities.cache.objects}
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class PlayerCtxCacheWrapper extends StarNubCacheWrapper<ChannelHandlerContext> {

    /**
     * Basic constructor. RECOMMENDED.
     *
     * @param CACHE_OWNER     String representing the owner of this utilities.cache, should be set to the plugins exact name
     * @param CACHE_NAME      String representing the name for this specific utilities.cache implementation, to be used to task thread purging
     * @param REGISTER_EVENTS boolean you must create a auto utilities.cache purger implementation if once that you need does
     *                        not exist already, if you will not be using this which is not recommended, use null in its place.
     * @param multiplier      int representing the multiplier of cache, the base cache is player expected player count from the configuration, multipliers will change this value
     */
    public PlayerCtxCacheWrapper(String CACHE_OWNER, String CACHE_NAME, boolean REGISTER_EVENTS, int multiplier) {
        super(CACHE_OWNER, CACHE_NAME, REGISTER_EVENTS, multiplier);
    }

    /**
     * Basic constructor. RECOMMENDED.
     *
     * @param CACHE_OWNER     String representing the owner of this utilities.cache, should be set to the plugins exact name
     * @param CACHE_NAME      String representing the name for this specific utilities.cache implementation, to be used to task thread purging
     * @param REGISTER_EVENTS boolean you must create a auto utilities.cache purger implementation if once that you need does
     */
    public PlayerCtxCacheWrapper(String CACHE_OWNER, String CACHE_NAME, boolean REGISTER_EVENTS) {
        super(CACHE_OWNER, CACHE_NAME, REGISTER_EVENTS);
    }

    /**
     * Time specific constructor. RECOMMENDED.
     *
     * @param CACHE_OWNER           String representing the owner of this utilities.cache, should be set to the plugins exact name
     * @param CACHE_NAME            String representing the name for this specific utilities.cache implementation, to be used to task thread purging
     * @param REGISTER_EVENTS       boolean you must create a auto utilities.cache purger implementation if once that you need does
     *                              not exist already, if you will not be using this which is not recommended, use null in its place.
     * @param multiplier            int representing the multiplier of cache, the base cache is player expected player count from the configuration, multipliers will change this value
     * @param TIME_UNIT             TimeUnit representing the time units to set the auto prune and purge to set 0 for off (Not recommended)
     * @param CACHE_PRUNE_TASK_TIME int representing the time units to to automatically remove cache of this age at the set interval of this time unit
     * @param CACHE_PURGE_TAKE_TIME int representing the time to purge all utilities.cache entirely
     */
    public PlayerCtxCacheWrapper(String CACHE_OWNER, String CACHE_NAME, boolean REGISTER_EVENTS, int multiplier, TimeUnit TIME_UNIT, int CACHE_PRUNE_TASK_TIME, int CACHE_PURGE_TAKE_TIME) {
        super(CACHE_OWNER, CACHE_NAME, REGISTER_EVENTS, multiplier, TIME_UNIT, CACHE_PRUNE_TASK_TIME, CACHE_PURGE_TAKE_TIME);
    }

    /**
     * Time specific constructor. RECOMMENDED.
     *
     * @param CACHE_OWNER           String representing the owner of this utilities.cache, should be set to the plugins exact name
     * @param CACHE_NAME            String representing the name for this specific utilities.cache implementation, to be used to task thread purging
     * @param REGISTER_EVENTS       boolean you must create a auto utilities.cache purger implementation if once that you need does
     *                              not exist already, if you will not be using this which is not recommended, use null in its place.
     * @param TIME_UNIT             TimeUnit representing the time units to set the auto prune and purge to set 0 for off (Not recommended)
     * @param CACHE_PRUNE_TASK_TIME int representing the time units to to automatically remove cache of this age at the set interval of this time unit
     * @param CACHE_PURGE_TAKE_TIME int representing the time to purge all utilities.cache entirely
     */
    public PlayerCtxCacheWrapper(String CACHE_OWNER, String CACHE_NAME, boolean REGISTER_EVENTS, TimeUnit TIME_UNIT, int CACHE_PRUNE_TASK_TIME, int CACHE_PURGE_TAKE_TIME) {
        super(CACHE_OWNER, CACHE_NAME, REGISTER_EVENTS, TIME_UNIT, CACHE_PRUNE_TASK_TIME, CACHE_PURGE_TAKE_TIME);
    }

    /**
     * This will purge ChannelHandlerContext from the list when players disconnect
     */
    @Override
    public void registerEvents() {
        new StarNubEventSubscription("StarNub", Priority.MEDIUM, "Player_Disconnected", new StarNubEventHandler() {
            @Override
            public void onEvent(StarNubEvent eventData) {
                DisconnectData disconnectData = (DisconnectData) eventData.getEVENT_DATA();
                PlayerSession playerSession = disconnectData.getPLAYER_SESSION();
                removeCacheEvent(playerSession);
            }
        });
    }

    private void removeCacheEvent(PlayerSession playerSession){
        ChannelHandlerContext clientCtx = playerSession.getCONNECTION().getCLIENT_CTX();
        getCACHE_MAP().remove(clientCtx);
    }
}
