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

package utilities.cache.objects;

/**
 * Represents a Time Cache. This utilities.cache can be used in any Cache Wrapper. Other utilities.cache must extend this to work with CacheWrapper classes
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public class TimeCache {

    /**
     * Represents the time the object was cached on the local machine.
     */
    private volatile long cacheTime;

    /**
     * No Args constructor will the set cacheTime to the current system time.
     */
    public TimeCache() {
        cacheTime = System.currentTimeMillis();
    }

    public long getCacheTime() {
        return cacheTime;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: Sets the cacheTime for this Cache to the current system time.
     * <p/>
     */
    public void setCacheTimeNow() {
        this.cacheTime = System.currentTimeMillis();
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: Sets the cacheTime for this Cache to the current system time and returns the previous time in utilities.cache
     * <p/>
     *
     * @return long representing the cacheTime that was just replaced
     */
    public long getCacheTimeSetCacheTimeNow() {
        long cacheTimeReturn = cacheTime;
        cacheTime = System.currentTimeMillis();
        return cacheTimeReturn;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: Returns the time in milliseconds from the time this cacheTime was set on the Cache and the current system time
     * <p/>
     *
     * @return long representing the cacheTime subtracted from the current system time, to equal the age in milliseconds
     */
    public long getCacheAge() {
        return System.currentTimeMillis() - cacheTime;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: Returns the time in milliseconds from the time this cacheTime was set on the Cache and the current system time as well as refreshed
     * the cacheTime to the current system time
     * <p/>
     *
     * @return long representing the cacheTime subtracted from the current system time, to equal the age in milliseconds
     */
    public long getCacheAgeRefreshTimeNow() {
        long now = System.currentTimeMillis();
        long cacheTimeReturn = now - cacheTime;
        cacheTime = now;
        return cacheTimeReturn;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: Evaluates the supplied time in milliseconds to the cacheTime for this Cache subtracting the current system time
     * to get the cacheAge and returns if the time is past
     * <p/>
     *
     * @param pastTime long representing some time in milliseconds to check for
     * @return boolean if the time is past
     */
    public boolean isPastDesignatedTime(long pastTime) {
        return pastTime < getCacheAge();
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: Evaluates the supplied time in milliseconds to the cacheTime for this Cache subtracting the current system time
     * to get the cacheAge and returns if the time is past
     * <p/>
     *
     * @param timePassed long representing some time in milliseconds to check for
     * @return boolean if the time is past
     */
    public boolean isPastDesignatedTimeRefreshTimeNowIfPast(long timePassed) {
        long now = System.currentTimeMillis();
        boolean pastDesignatedTime = timePassed < (now - cacheTime);
        if (pastDesignatedTime) {
            cacheTime = now;
            return true;
        } else {
            return false;
        }
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For Plugin Developers & Anyone else.
     * <p/>
     * Uses: Evaluates the supplied time in milliseconds to the cacheTime for this Cache subtracting the current system time
     * to get the cacheAge and returns if the time is past
     * <p/>
     *
     * @param timePassed long representing some time in milliseconds to check for
     * @return boolean if the time is past
     */
    public boolean isPastDesignatedTimeRefreshTimeNowAlways(long timePassed) {
        long now = System.currentTimeMillis();
        boolean pastDesignatedTime = timePassed < (now - cacheTime);
        if (pastDesignatedTime) {
            cacheTime = now;
            return true;
        } else {
            cacheTime = now;
            return false;
        }
    }
}
