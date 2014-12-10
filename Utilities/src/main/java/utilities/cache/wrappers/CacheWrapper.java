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

package utilities.cache.wrappers;



import utilities.cache.objects.TimeCache;
import utilities.concurrency.task.InternalTask;
import utilities.concurrency.task.ScheduledTask;
import utilities.concurrency.task.TaskManager;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Represents Abstract CacheWrapper that can be transformed to accept any type of key and be used with any
 * specific types of utilities.cache found in (@link starnubserver.utilities.cache.objects}.
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public abstract class CacheWrapper<E1> {

    private final TimeUnit TIME_UNIT;
    private final int CACHE_PRUNE_TASK_TIME;
    private final int CACHE_PURGE_TAKE_TIME;
    private final boolean AUTO_CACHE_PURGER;
    private final ConcurrentHashMap<E1, TimeCache> CACHE_MAP;
    private final String CACHE_OWNER;
    private final String CACHE_NAME;
    private final String ERROR_MSG;

    /**
     * Basic constructor. RECOMMENDED.
     *
     * @param CACHE_OWNER       String representing the owner of this utilities.cache, should be set to the plugins exact name
     * @param CACHE_NAME        String representing the name for this specific utilities.cache implementation, to be used to task thread purging
     * @param AUTO_CACHE_PURGER boolean you must create a auto utilities.cache purger implementation if once that you need does
     *                          not exist already, if you will not be using this which is not recommended, use null in its place.
     * @param SCHEDULED_THREAD_POOL_EXECUTOR ScheduledThreadPoolExecutor of which we have scheduled a auto dumping task to
     * @param expectedElements  int representing the max number of elements that will be in the utilities.cache at one time
     * @param expectedThreads   int representing the max number of threads you expect to be accessing the elements at one time
     */
    public CacheWrapper(String CACHE_OWNER, String CACHE_NAME, boolean AUTO_CACHE_PURGER, ScheduledThreadPoolExecutor SCHEDULED_THREAD_POOL_EXECUTOR, int expectedElements, int expectedThreads) {
        this.CACHE_OWNER = CACHE_OWNER;
        this.CACHE_NAME = CACHE_NAME;
        this.TIME_UNIT = TimeUnit.MINUTES;
        this.CACHE_PRUNE_TASK_TIME = 15;
        this.CACHE_PURGE_TAKE_TIME = 60;
        this.AUTO_CACHE_PURGER = AUTO_CACHE_PURGER;
        this.CACHE_MAP = new ConcurrentHashMap<E1, TimeCache>(expectedElements, 1.0f, expectedThreads);
        startEventListener();
        cachePruneTask(SCHEDULED_THREAD_POOL_EXECUTOR);
        cachePurgeTask(SCHEDULED_THREAD_POOL_EXECUTOR);
        this.ERROR_MSG = "CACHE OWNER: " + CACHE_OWNER +". CACHE NAME: "+ CACHE_NAME +
                ". ERROR MESSAGE: A StarNub Cache system being used is not being used properly. CACHE OWNER: %s, CACHE NAME: %s - NULL KEY OR VALUE USED.";
    }

    /**
     * Time specific constructor. RECOMMENDED.
     *
     * @param CACHE_OWNER           String representing the owner of this utilities.cache, should be set to the plugins exact name
     * @param CACHE_NAME            String representing the name for this specific utilities.cache implementation, to be used to task thread purging
     * @param AUTO_CACHE_PURGER     boolean you must create a auto utilities.cache purger implementation if once that you need does
     *                              not exist already, if you will not be using this which is not recommended, use null in its place.
     * @param SCHEDULED_THREAD_POOL_EXECUTOR ScheduledThreadPoolExecutor of which we have scheduled a auto dumping task to
     * @param TIME_UNIT             TimeUnit representing the time units to set the auto prune and purge to set 0 for off (Not recommended)
     * @param CACHE_PRUNE_TASK_TIME int representing the time units to to automatically remove cache of this age at the set interval of this time unit
     * @param CACHE_PURGE_TAKE_TIME int representing the time to purge all utilities.cache entirely
     * @param expectedElements  int representing the max number of elements that will be in the utilities.cache at one time
     * @param expectedThreads   int representing the max number of threads you expect to be accessing the elements at one time
     */
    public CacheWrapper(String CACHE_OWNER, String CACHE_NAME, boolean AUTO_CACHE_PURGER, ScheduledThreadPoolExecutor SCHEDULED_THREAD_POOL_EXECUTOR, int expectedElements, int expectedThreads, TimeUnit TIME_UNIT, int CACHE_PRUNE_TASK_TIME, int CACHE_PURGE_TAKE_TIME) {
        this.CACHE_OWNER = CACHE_OWNER;
        this.CACHE_NAME = CACHE_NAME;
        this.TIME_UNIT = TIME_UNIT;
        this.CACHE_PRUNE_TASK_TIME = CACHE_PRUNE_TASK_TIME;
        this.CACHE_PURGE_TAKE_TIME = CACHE_PURGE_TAKE_TIME;
        this.AUTO_CACHE_PURGER = AUTO_CACHE_PURGER;
        this.CACHE_MAP = new ConcurrentHashMap<E1, TimeCache>(expectedElements, 1.0f, expectedThreads);
        startEventListener();
        cachePruneTask(SCHEDULED_THREAD_POOL_EXECUTOR);
        cachePurgeTask(SCHEDULED_THREAD_POOL_EXECUTOR);
        this.ERROR_MSG = "CACHE OWNER: " + CACHE_OWNER +". CACHE NAME: "+ CACHE_NAME +
                ". ERROR MESSAGE: A StarNub Cache system being used is not being used properly. CACHE OWNER: %s, CACHE NAME: %s - NULL KEY OR VALUE USED.";
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

    public ConcurrentHashMap<E1, TimeCache> getCACHE_MAP() {
        return CACHE_MAP;
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

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will add a utilities.cache item to the utilities.cache. Cache that is added must represent a TimeCache
     * class.
     * <p/>
     *
     * @param key           E1 representing a contactable key
     * @param timeCache TimeCache representing a utilities.cache object
     */
    public void addCache(E1 key, TimeCache timeCache) {
        try {
            CACHE_MAP.put(key, timeCache);
        } catch (NullPointerException e) {
            System.out.println(ERROR_MSG + " " +e.getMessage());
        }
    }

    /**
     * Uses: This will remove a key and its value from cache and return it
     * <p/>
     *
     * @param key E1 representing a contactable key
     */
    public TimeCache removeCache(E1 key) {
        try {
            return CACHE_MAP.remove(key);
        } catch (NullPointerException e) {
            System.out.println(ERROR_MSG + " " +e.getMessage());
            return null;
        }
    }

    /**
     * Uses: This will replace a utilities.cache object if the key exist, and if not it will add the item to cache
     * <p/>
     *
     * @param key           E1 representing a contactable key
     * @param timeCache TimeCache representing a utilities.cache object
     */
    public void replaceCache(E1 key, TimeCache timeCache) {
        try {
            CACHE_MAP.put(key, timeCache);
        } catch (NullPointerException e) {
            System.out.println(ERROR_MSG + " " +e.getMessage());
        }
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will get a cache object associated to the key supplied
     * <p/>
     *
     * @param key E1 representing a contactable key
     * @return TimeCache representing the utilities.cache object
     */
    public TimeCache getCache(E1 key) {
        try {
            return CACHE_MAP.get(key);
        } catch (NullPointerException e) {
            System.out.println(ERROR_MSG + " " +e.getMessage());
            return null;
        }
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will look through the cache to see if it past the time given. If so it will remove the cache and add the key
     * to a HasSet to be returned to the caller.
     * <p/>
     *
     * @param pastTime long time that is to be checked in the utilities.cache for it being past the time
     * @return HashSet containing all of the removed keys
     */
    public HashSet<E1> bulkCacheRemove(long pastTime) {
        HashSet<E1> toRemove = new HashSet<>();
        CACHE_MAP.keySet().stream().filter(element -> CACHE_MAP.get(element).isPastDesignatedTime(pastTime)).forEach(element -> {
            toRemove.add(element);
            CACHE_MAP.remove(element);
        });
        return toRemove;
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will submit a task to StarNub to at a fixed rate schedule a prune task based on the time supplied.
     * It will remove utilities.cache older then the prune time when the prune triggers.
     * <p/>
     * @param SCHEDULED_THREAD_POOL_EXECUTOR ScheduledThreadPoolExecutor of which we have scheduled a auto dumping task to
     */
    public void cachePruneTask(ScheduledThreadPoolExecutor SCHEDULED_THREAD_POOL_EXECUTOR) {
        if (CACHE_PRUNE_TASK_TIME != 0) {
            Runnable runnable = this::cachePrune;
            if (SCHEDULED_THREAD_POOL_EXECUTOR instanceof TaskManager) {
                ScheduledFuture scheduledFuture =  SCHEDULED_THREAD_POOL_EXECUTOR.scheduleWithFixedDelay(runnable, 30, 30, TimeUnit.SECONDS);
                String taskName = String.format("%s - %s - StarNub Cache Wrapper - Prune Task", CACHE_OWNER, CACHE_NAME);
                ScheduledTask scheduledTask = new InternalTask(CACHE_OWNER, taskName, runnable,  scheduledFuture);
                TaskManager taskManager = (TaskManager) SCHEDULED_THREAD_POOL_EXECUTOR;
                taskManager.getTASK_LIST().put(CACHE_OWNER, new ConcurrentHashMap<>());
                taskManager.getTASK_LIST().get(CACHE_OWNER).put(taskName, scheduledTask);
            } else {
                SCHEDULED_THREAD_POOL_EXECUTOR.scheduleWithFixedDelay(runnable, CACHE_PRUNE_TASK_TIME, CACHE_PRUNE_TASK_TIME, TIME_UNIT);
            }
        }
    }

    /**
     * This will prune cache
     */
    public void cachePrune(){
        CACHE_MAP.keySet().stream().filter(key -> CACHE_MAP.get(key).getCacheAge() > TimeUnit.MILLISECONDS.convert(CACHE_PRUNE_TASK_TIME, TIME_UNIT)).forEach(CACHE_MAP::remove);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will submit a task to StarNub to at a fixed rate schedule a purge task which will completely clear the cache
     * <p/>
     * @param SCHEDULED_THREAD_POOL_EXECUTOR ScheduledThreadPoolExecutor of which we have scheduled a auto dumping task to
     */
    public void cachePurgeTask(ScheduledThreadPoolExecutor SCHEDULED_THREAD_POOL_EXECUTOR) {
        if (CACHE_PURGE_TAKE_TIME != 0) {
            Runnable runnable = this::cachePurge;
            if (SCHEDULED_THREAD_POOL_EXECUTOR instanceof TaskManager) {
                ScheduledFuture scheduledFuture =  SCHEDULED_THREAD_POOL_EXECUTOR.scheduleWithFixedDelay(runnable, 30, 30, TimeUnit.SECONDS);
                String taskName = String.format("%s - %s - StarNub Cache Wrapper - Purge Task", CACHE_OWNER, CACHE_NAME);
                ScheduledTask scheduledTask = new InternalTask(CACHE_OWNER, taskName, runnable,  scheduledFuture);
                TaskManager taskManager = (TaskManager) SCHEDULED_THREAD_POOL_EXECUTOR;
                taskManager.getTASK_LIST().put(CACHE_OWNER, new ConcurrentHashMap<>());
                taskManager.getTASK_LIST().get(CACHE_OWNER).put(taskName, scheduledTask);
            } else {
                SCHEDULED_THREAD_POOL_EXECUTOR.scheduleAtFixedRate(runnable, CACHE_PRUNE_TASK_TIME, CACHE_PRUNE_TASK_TIME, TIME_UNIT);
            }
        }
    }

    /**
     * This will purge all cache
     */
    public void cachePurge(){
        CACHE_MAP.clear();
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: This will execute the registerEvents() method which will register any event listeners with starnubserver which
     * will enable auto cache removal based on the event and the implementers event method(s).
     * <p/>
     */
    public void startEventListener() {
        if (AUTO_CACHE_PURGER) {
            /* Subscribe any events router in this method to the event listeners contained within */
            registerEvents();
        }
    }

    /**
     * You must implement your own methods or use another AutoCachePurge class that fits your needs
     */
    public abstract void registerEvents();
}