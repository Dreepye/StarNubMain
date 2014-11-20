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

package server.cache.wrappers;

import org.apache.commons.lang3.exception.ExceptionUtils;
import server.StarNub;
import server.cache.objects.AbstractCache;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Represents Abstract CacheWrapper that can be transformed to accept any type of key and be used with any
 * specific types of cache found in (@link server.cache.objects}.
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public abstract class CacheWrapper<E1> {

    private final TimeUnit TIME_UNIT;
    private final int CACHE_PRUNE_TASK_TIME;
    private final int CACHE_PURGE_TAKE_TIME;
    private final boolean AUTO_CACHE_PURGER;
    private final ConcurrentHashMap<E1, AbstractCache> cacheMap;
    private final String CACHE_OWNER;
    private final String CACHE_NAME;
    private final String ERROR_MSG = "A StarNub Cache system being used is not being used properly. CACHE OWNER: %s, CACHE NAME: %s - NULL KEY OR VALUE USED.";

    /**
     * Basic constructor. RECOMMENDED.
     *
     * @param CACHE_OWNER       String representing the owner of this cache, should be set to the plugins exact name
     * @param CACHE_NAME        String representing the name for this specific cache implementation, to be used to task thread purging
     * @param AUTO_CACHE_PURGER boolean you must create a auto cache purger implementation if once that you need does
     *                          not exist already, if you will not be using this which is not recommended, use null in its place.
     */
    public CacheWrapper(String CACHE_OWNER, String CACHE_NAME, boolean AUTO_CACHE_PURGER) {
        this.CACHE_OWNER = CACHE_OWNER;
        this.CACHE_NAME = CACHE_NAME;
        this.TIME_UNIT = TimeUnit.MINUTES;
        this.CACHE_PRUNE_TASK_TIME = 15;
        this.CACHE_PURGE_TAKE_TIME = 60;
        this.AUTO_CACHE_PURGER = AUTO_CACHE_PURGER;
        int elements = (int) ((Map) StarNub.getConfiguration().getConfiguration().get("resources")).get("player_limit") +
                (int) ((Map) StarNub.getConfiguration().getConfiguration().get("resources")).get("player_limit_reserved") + 2;
        int threads = (int) ((Map) StarNub.getConfiguration().getConfiguration().get("resources")).get("event_thread_count");
        this.cacheMap = new ConcurrentHashMap<E1, AbstractCache>(elements, 1.0f, threads);
        startEventListener();
        cachePruneTask();
        cachePurgeTask();
    }

    /**
     * Time specific constructor. RECOMMENDED.
     *
     * @param CACHE_OWNER           String representing the owner of this cache, should be set to the plugins exact name
     * @param CACHE_NAME            String representing the name for this specific cache implementation, to be used to task thread purging
     * @param AUTO_CACHE_PURGER     boolean you must create a auto cache purger implementation if once that you need does
     *                              not exist already, if you will not be using this which is not recommended, use null in its place.
     * @param TIME_UNIT             TimeUnit representing the time units to set the auto prune and purge to set 0 for off (Not recommended)
     * @param CACHE_PRUNE_TASK_TIME int representing the time units to to automatically remove cache of this age at the set interval of this time unit
     * @param CACHE_PURGE_TAKE_TIME int representing the time to purge all cache entirely
     */
    public CacheWrapper(String CACHE_OWNER, String CACHE_NAME, boolean AUTO_CACHE_PURGER, TimeUnit TIME_UNIT, int CACHE_PRUNE_TASK_TIME, int CACHE_PURGE_TAKE_TIME) {
        this.CACHE_OWNER = CACHE_OWNER;
        this.CACHE_NAME = CACHE_NAME;
        this.TIME_UNIT = TIME_UNIT;
        this.CACHE_PRUNE_TASK_TIME = CACHE_PRUNE_TASK_TIME;
        this.CACHE_PURGE_TAKE_TIME = CACHE_PURGE_TAKE_TIME;
        this.AUTO_CACHE_PURGER = AUTO_CACHE_PURGER;
        int elements = (int) ((Map) StarNub.getConfiguration().getConfiguration().get("resources")).get("player_limit") +
                (int) ((Map) StarNub.getConfiguration().getConfiguration().get("resources")).get("player_limit_reserved") + 2;
        int threads = (int) ((Map) StarNub.getConfiguration().getConfiguration().get("resources")).get("event_thread_count");
        this.cacheMap = new ConcurrentHashMap<E1, AbstractCache>(elements, 1.0f, threads);
        startEventListener();
        cachePruneTask();
        cachePurgeTask();
    }

    /**
     * Cache size specific constructor, NOT RECOMMENDED FOR USE UNLESS YOU NEED MORE THEN PLAYER COUNT WORTH OF ELEMENTS.
     *
     * @param CACHE_OWNER       String representing the owner of this cache, should be set to the plugins exact name
     * @param CACHE_NAME        String representing the name for this specific cache implementation, to be used to task thread purging
     * @param AUTO_CACHE_PURGER boolean you must create a auto cache purger implementation if once that you need does
     *                          not exist already, if you will not be using this which is not recommended, use null in its place.
     * @param elementMultiple   int representing the multiple of elements this will = the total player count, multiplied by this number
     */
    public CacheWrapper(String CACHE_OWNER, String CACHE_NAME, boolean AUTO_CACHE_PURGER, int elementMultiple) {
        this.CACHE_OWNER = CACHE_OWNER;
        this.CACHE_NAME = CACHE_NAME;
        this.TIME_UNIT = TimeUnit.MINUTES;
        this.CACHE_PRUNE_TASK_TIME = 15;
        this.CACHE_PURGE_TAKE_TIME = 60;
        this.AUTO_CACHE_PURGER = AUTO_CACHE_PURGER;
        int elements = (int) ((Map) StarNub.getConfiguration().getConfiguration().get("resources")).get("player_limit") +
                (int) ((Map) StarNub.getConfiguration().getConfiguration().get("resources")).get("player_limit_reserved") + 2;
        int threads = (int) ((Map) StarNub.getConfiguration().getConfiguration().get("resources")).get("event_thread_count");
        this.cacheMap = new ConcurrentHashMap<E1, AbstractCache>(elements * elementMultiple, 1.0f, threads);
        startEventListener();
        cachePruneTask();
        cachePurgeTask();
    }

    /**
     * Cache size specific constructor, NOT RECOMMENDED FOR USE UNLESS YOU NEED MORE THEN PLAYER COUNT WORTH OF ELEMENTS.
     *
     * @param CACHE_OWNER       String representing the owner of this cache, should be set to the plugins exact name
     * @param CACHE_NAME        String representing the name for this specific cache implementation, to be used to task thread purging
     * @param AUTO_CACHE_PURGER boolean you must create a auto cache purger implementation if once that you need does
     *                          not exist already, if you will not be using this which is not recommended, use null in its place.
     * @param elements          int representing the multiple of elements this will = the total player count, multiplied by this number
     * @param dummyBoolean      boolean that represents nothing and used to create a new signature //TODO
     */
    public CacheWrapper(String CACHE_OWNER, String CACHE_NAME, boolean AUTO_CACHE_PURGER, int elements, boolean dummyBoolean) {
        this.CACHE_OWNER = CACHE_OWNER;
        this.CACHE_NAME = CACHE_NAME;
        this.TIME_UNIT = TimeUnit.MINUTES;
        this.CACHE_PRUNE_TASK_TIME = 15;
        this.CACHE_PURGE_TAKE_TIME = 60;
        this.AUTO_CACHE_PURGER = AUTO_CACHE_PURGER;
        int threads = (int) ((Map) StarNub.getConfiguration().getConfiguration().get("resources")).get("event_thread_count");
        this.cacheMap = new ConcurrentHashMap<E1, AbstractCache>(elements, 1.0f, threads);
        startEventListener();
        cachePruneTask();
        cachePurgeTask();
    }

    public TimeUnit getTIME_UNIT() {
        return TIME_UNIT;
    }

    public int getCACHE_PRUNE_TASK_TIME() {
        return CACHE_PRUNE_TASK_TIME;
    }

    public int getCACHE_PURGE_TAKE_TIME() {
        return CACHE_PURGE_TAKE_TIME;
    }

    public boolean isAUTO_CACHE_PURGER() {
        return AUTO_CACHE_PURGER;
    }

    public ConcurrentHashMap<E1, AbstractCache> getCacheMap() {
        return cacheMap;
    }

    public String getCACHE_OWNER() {
        return CACHE_OWNER;
    }

    public String getCACHE_NAME() {
        return CACHE_NAME;
    }

    public String getERROR_MSG() {
        return ERROR_MSG;
    }

//    /**
//     * Everything constructor. NOT RECOMMENDED FOR USE.
//     * @param CACHE_OWNER String representing the owner of this cache, should be set to the plugins exact name
//     * @param CACHE_NAME String representing the name for this specific cache implementation, to be used to task thread purging
//     * @param AUTO_CACHE_PURGER boolean you must create a auto cache purger implementation if once that you need does
//     *                          not exist already, if you will not be using this which is not recommended, use null in its place.
//     * @param TIME_UNIT TimeUnit representing the time units to set the auto prune and purge to set 0 for off (Not recommended)
//     * @param CACHE_PRUNE_TASK_TIME int representing the time units to to automatically remove cache of this age at the set interval of this time unit
//     * @param CACHE_PURGE_TAKE_TIME int representing the time to purge all cache entirely
//     * @param elements int representing the number of elements you will be holding in this cache
//     * @param load float representing when to resize the maps if 0.75f, then when 75% full the maps will resize
//     * @param threads int representing the number of threads that will be accessing concurrently this cache
//     */
//    public CacheWrapper(String CACHE_OWNER, String CACHE_NAME, boolean AUTO_CACHE_PURGER, TimeUnit TIME_UNIT, int CACHE_PRUNE_TASK_TIME, int CACHE_PURGE_TAKE_TIME, int elements, float load, int threads) {
//        this.CACHE_OWNER = CACHE_OWNER;
//        this.CACHE_NAME = CACHE_NAME;
//        this.TIME_UNIT = TIME_UNIT;
//        this.CACHE_PRUNE_TASK_TIME = CACHE_PRUNE_TASK_TIME;
//        this.CACHE_PURGE_TAKE_TIME = CACHE_PURGE_TAKE_TIME;
//        this.AUTO_CACHE_PURGER = AUTO_CACHE_PURGER;
//        this.cacheMap = new ConcurrentHashMap<E1, AbstractCache>(elements, load, threads);
//        startEventListener();
//    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will add a cache item to the cache. Cache that is added must represent a AbstractCache
     * class.
     * <p/>
     *
     * @param key           E1 representing a contactable key
     * @param abstractCache AbstractCache representing a cache object
     */
    public void addCache(E1 key, AbstractCache abstractCache) {
        try {
            cacheMap.put(key, abstractCache);
        } catch (NullPointerException e) {
            StarNub.getLogger().cErrPrint(CACHE_OWNER, ERROR_MSG + ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will remove a key and its value you from cache
     * <p/>
     *
     * @param key E1 representing a contactable key
     */
    public void removeCache(E1 key) {
        try {
            cacheMap.remove(key);
        } catch (NullPointerException e) {
            StarNub.getLogger().cErrPrint(CACHE_OWNER, String.format(ERROR_MSG, CACHE_OWNER, CACHE_NAME));
        }
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will replace a cache object if the key exist, and if not it will add the item to cache
     * <p/>
     *
     * @param key           E1 representing a contactable key
     * @param abstractCache AbstractCache representing a cache object
     */
    public void replaceCache(E1 key, AbstractCache abstractCache) {
        try {
            cacheMap.put(key, abstractCache);
        } catch (NullPointerException e) {
            StarNub.getLogger().cErrPrint(CACHE_OWNER, String.format(ERROR_MSG, CACHE_OWNER, CACHE_NAME));
        }
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will get a cache object associated to the key supplied
     * <p/>
     *
     * @param key E1 representing a contactable key
     * @return AbstractCache representing the cache object
     */
    public AbstractCache getCache(E1 key) {
        try {
            return cacheMap.get(key);
        } catch (NullPointerException e) {
            StarNub.getLogger().cErrPrint(CACHE_OWNER, String.format(ERROR_MSG, CACHE_OWNER, CACHE_NAME));
            return null;
        }
    }


    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will look through the cache to see if it past the time given. If so it will remove the cache and add the key
     * to a HasSet to be returned to the caller.
     * <p/>
     *
     * @param pastTime long time that is to be checked in the cache for it being past the time
     * @return HashSet containing all of the removed keys
     */
    public HashSet<E1> bulkCacheRemove(long pastTime) {
        HashSet<E1> toRemove = new HashSet<>();
        cacheMap.keySet().stream().filter(element -> cacheMap.get(element).isPastDesignatedTime(pastTime)).forEach(element -> {
            toRemove.add(element);
            cacheMap.remove(element);
        });
        return toRemove;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will submit a task to StarNub to at a fixed rate schedule a prune task based on the time supplied.
     * It will remove cache older then the prune time when the prune triggers.
     * <p/>
     */
    public void cachePruneTask() {
        if (CACHE_PRUNE_TASK_TIME != 0) {
            StarNub.getTask().getTaskScheduler().scheduleWithFixedDelayRepeatingTask(CACHE_OWNER, String.format("%s - %s - StarNub Cache Wrapper - Prune Task", CACHE_OWNER, CACHE_NAME), new Runnable() {
                @Override
                public void run() {
                    cacheMap.keySet().stream().filter(key -> cacheMap.get(key).getCacheAge() > TimeUnit.MILLISECONDS.convert(CACHE_PRUNE_TASK_TIME, TIME_UNIT)).forEach(cacheMap::remove);
                }
            }, CACHE_PRUNE_TASK_TIME, CACHE_PRUNE_TASK_TIME, TIME_UNIT);
        }
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will submit a task to StarNub to at a fixed rate schedule a purge task which will completely clear the cache
     * <p/>
     */
    public void cachePurgeTask() {
        if (CACHE_PURGE_TAKE_TIME != 0) {
            StarNub.getTask().getTaskScheduler().scheduleWithFixedDelayRepeatingTask(CACHE_OWNER, String.format("%s - %s - StarNub Cache Wrapper - Purge Task", CACHE_OWNER, CACHE_NAME), new Runnable() {
                @Override
                public void run() {
                    cacheMap.clear();
                }
            }, CACHE_PURGE_TAKE_TIME, CACHE_PURGE_TAKE_TIME, TIME_UNIT);
        }
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will execute the registerEvents() method which will register any event listeners with starnub which
     * will enable auto cache removal based on the event and the implementers event method(s).
     * <p/>
     */
    public void startEventListener() {
        if (AUTO_CACHE_PURGER) {
            /* Subscribe any eventsrouter in this method to the event listeners contained within */
            registerEvents();
        }
    }

    /**
     * You must implement your own methods or use another AutoCachePurge class that fits your needs
     */
    public abstract void registerEvents();
}