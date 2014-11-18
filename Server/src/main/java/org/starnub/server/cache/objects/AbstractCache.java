package org.starnub.server.cache.objects;

import lombok.Getter;

public abstract class AbstractCache {

    /**
     * Represents the time the object was cached on the local machine.
     */
    @Getter
    private volatile long cacheTime;

    /**
     * No Args constructor will the set cacheTime to the current system time.
     */
    protected AbstractCache(){
        cacheTime = System.currentTimeMillis();
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Sets the cacheTime for this Cache to the current system time.
     * <p>
     *
     */
    public void setCacheTimeNow(){
        this.cacheTime = System.currentTimeMillis();
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Sets the cacheTime for this Cache to the current system time and returns the previous time in cache
     * <p>
     * @return long representing the cacheTime that was just replaced
     */
    public long getCacheTimeSetCacheTimeNow(){
        long cacheTimeReturn = cacheTime;
        cacheTime = System.currentTimeMillis();
        return cacheTimeReturn;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Returns the time in milliseconds from the time this cacheTime was set on the Cache and the current system time
     * <p>
     * @return long representing the cacheTime subtracted from the current system time, to equal the age in milliseconds
     */
    public long getCacheAge(){
        return System.currentTimeMillis() - cacheTime;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Returns the time in milliseconds from the time this cacheTime was set on the Cache and the current system time as well as refreshed
     * the cacheTime to the current system time
     * <p>
     * @return long representing the cacheTime subtracted from the current system time, to equal the age in milliseconds
     */
    public long getCacheAgeRefreshTimeNow(){
        long now = System.currentTimeMillis();
        long cacheTimeReturn = now - cacheTime;
        cacheTime = now;
        return cacheTimeReturn;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Evaluates the supplied time in milliseconds to the cacheTime for this Cache subtracting the current system time
     * to get the cacheAge and returns if the time is past
     * <p>
     * @param pastTime long representing some time in milliseconds to check for
     * @return boolean if the time is past
     */
    public boolean isPastDesignatedTime(long pastTime){
        return pastTime < getCacheAge();
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Evaluates the supplied time in milliseconds to the cacheTime for this Cache subtracting the current system time
     * to get the cacheAge and returns if the time is past
     * <p>
     * @param timePassed long representing some time in milliseconds to check for
     * @return boolean if the time is past
     */
    public boolean isPastDesignatedTimeRefreshTimeNowIfPast(long timePassed){
        long now = System.currentTimeMillis();
        boolean pastDesignatedTime = timePassed < (now - cacheTime);
        if (pastDesignatedTime){
            cacheTime = now;
            return true;
        } else {
            return false;
        }
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Evaluates the supplied time in milliseconds to the cacheTime for this Cache subtracting the current system time
     * to get the cacheAge and returns if the time is past
     * <p>
     * @param timePassed long representing some time in milliseconds to check for
     * @return boolean if the time is past
     */
    public boolean isPastDesignatedTimeRefreshTimeNowAlways(long timePassed){
        long now = System.currentTimeMillis();
        boolean pastDesignatedTime = timePassed < (now - cacheTime);
        if (pastDesignatedTime){
            cacheTime = now;
            return true;
        } else {
            cacheTime = now;
            return false;
        }
    }
}
