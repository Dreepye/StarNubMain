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

package starnubserver.cache.wrappers;

import starnubserver.Connections;
import starnubserver.StarNubTaskManager;
import utilities.cache.wrappers.CacheWrapper;

import java.util.concurrent.TimeUnit;

public class StarNubCacheWrapper<E1> extends CacheWrapper<E1> {

    /**
     * Basic constructor. RECOMMENDED.
     *
     * @param CACHE_OWNER                    String representing the owner of this utilities.cache, should be set to the plugins exact name
     * @param CACHE_NAME                     String representing the name for this specific utilities.cache implementation, to be used to task thread purging
     * @param REGISTER_EVENTS                boolean you must create a auto utilities.cache purger implementation if once that you need does
     *                                       not exist already, if you will not be using this which is not recommended, use null in its place.
     * @param multiplier                     int representing the multiplier of cache, the base cache is player expected player count from the configuration, multipliers will change this value
     */
    public StarNubCacheWrapper(String CACHE_OWNER, String CACHE_NAME, boolean REGISTER_EVENTS, int multiplier) {
        super(CACHE_OWNER, CACHE_NAME, REGISTER_EVENTS, StarNubTaskManager.getInstance(), Connections.getExpectedPlayers(multiplier), Connections.getExpectedThreads());
    }

    /**
     * Basic constructor. RECOMMENDED.
     *
     * @param CACHE_OWNER                    String representing the owner of this utilities.cache, should be set to the plugins exact name
     * @param CACHE_NAME                     String representing the name for this specific utilities.cache implementation, to be used to task thread purging
     * @param REGISTER_EVENTS                boolean you must create a auto utilities.cache purger implementation if once that you need does
     *                                       not exist already, if you will not be using this which is not recommended, use null in its place.
     */
    public StarNubCacheWrapper(String CACHE_OWNER, String CACHE_NAME, boolean REGISTER_EVENTS) {
        super(CACHE_OWNER, CACHE_NAME, REGISTER_EVENTS, StarNubTaskManager.getInstance(), Connections.getExpectedPlayers(1), Connections.getExpectedThreads());
    }

    /**
     * Time specific constructor. RECOMMENDED.
     *
     * @param CACHE_OWNER                    String representing the owner of this utilities.cache, should be set to the plugins exact name
     * @param CACHE_NAME                     String representing the name for this specific utilities.cache implementation, to be used to task thread purging
     * @param REGISTER_EVENTS                boolean you must create a auto utilities.cache purger implementation if once that you need does
     *                                       not exist already, if you will not be using this which is not recommended, use null in its place.
     * @param multiplier                     int representing the multiplier of cache, the base cache is player expected player count from the configuration, multipliers will change this value
     * @param TIME_UNIT                      TimeUnit representing the time units to set the auto prune and purge to set 0 for off (Not recommended)
     * @param CACHE_PRUNE_TASK_TIME          int representing the time units to to automatically remove cache of this age at the set interval of this time unit
     * @param CACHE_PURGE_TAKE_TIME          int representing the time to purge all utilities.cache entirely
     */
    public StarNubCacheWrapper(String CACHE_OWNER, String CACHE_NAME, boolean REGISTER_EVENTS, int multiplier, TimeUnit TIME_UNIT, int CACHE_PRUNE_TASK_TIME, int CACHE_PURGE_TAKE_TIME) {
        super(CACHE_OWNER, CACHE_NAME, REGISTER_EVENTS, StarNubTaskManager.getInstance(), Connections.getExpectedPlayers(multiplier), Connections.getExpectedThreads(), TIME_UNIT, CACHE_PRUNE_TASK_TIME, CACHE_PURGE_TAKE_TIME);
    }

    /**
     * Time specific constructor. RECOMMENDED.
     *
     * @param CACHE_OWNER                    String representing the owner of this utilities.cache, should be set to the plugins exact name
     * @param CACHE_NAME                     String representing the name for this specific utilities.cache implementation, to be used to task thread purging
     * @param REGISTER_EVENTS                boolean you must create a auto utilities.cache purger implementation if once that you need does
     *                                       not exist already, if you will not be using this which is not recommended, use null in its place.
     * @param TIME_UNIT                      TimeUnit representing the time units to set the auto prune and purge to set 0 for off (Not recommended)
     * @param CACHE_PRUNE_TASK_TIME          int representing the time units to to automatically remove cache of this age at the set interval of this time unit
     * @param CACHE_PURGE_TAKE_TIME          int representing the time to purge all utilities.cache entirely
     */
    public StarNubCacheWrapper(String CACHE_OWNER, String CACHE_NAME, boolean REGISTER_EVENTS, TimeUnit TIME_UNIT, int CACHE_PRUNE_TASK_TIME, int CACHE_PURGE_TAKE_TIME) {
        super(CACHE_OWNER, CACHE_NAME, REGISTER_EVENTS, StarNubTaskManager.getInstance(), Connections.getExpectedPlayers(1), Connections.getExpectedThreads(), TIME_UNIT, CACHE_PRUNE_TASK_TIME, CACHE_PURGE_TAKE_TIME);
    }

    /**
     * You must implement your own methods or use another AutoCachePurge class that fits your needs
     */
    @Override
    public void registerEvents() {

    }

}
