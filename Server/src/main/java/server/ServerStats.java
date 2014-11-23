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

package server;

import org.joda.time.DateTime;

/**
 * This will turn into a database log with time stamps and simple querying
 */
public enum ServerStats {
    INSTANCE;

    //TODO PLUGIN

    private volatile int todaysDay;

    /* Time Trackers */

    private volatile static DateTime starnubOnlineTime; /* Java time stamp in milliseconds */

    private volatile static DateTime starboundOnlineTime; /* Divide by /1000 for Unix time stamp */

    private volatile static long lastAutoRestart;

    /* For Ever Tracker */

    private volatile static int starboundAutoRestarts = 0;

    private volatile static int starboundCrashes = 0;

    private volatile static int starboundUnresponsive = 0;

    /* Since Wrapper Start */

    private volatile static int sbAutoRestartsWrap = 0;

    private volatile static int sbCrashesWrap = 0;

    private volatile static int sbUnresponsiveWrap = 0;

    /* Since SB Last Auto Restart */

    private volatile static int sbCrashesTemp = 0;

    private volatile static int sbUnresponsiveTemp = 0;



    public void setTodaysDay(int todaysDay) {
        this.todaysDay = todaysDay;
    }

    private void statsLoader() {

    }

    private void statsSaver() {

    }

    public long getSnUptime() {
        return DateTime.now().getMillis() - starnubOnlineTime.getMillis();
    }

    public long getSbUptime() {
        return DateTime.now().getMillis() - starboundOnlineTime.getMillis();
    }


    public void setSnOnlineTime(DateTime starUpTime) {
        starnubOnlineTime = starUpTime;
    }

    public void setSbOnlineTime(DateTime startUpTime) {
        starboundOnlineTime = startUpTime;
    }

    public void setLastAutoRestart() {
        lastAutoRestart = System.currentTimeMillis();
    }

    public void addSbAutoRestarts() {
        starboundAutoRestarts += 1;
        sbAutoRestartsWrap += 1;
    }

    public void addSbCrashes() {
        starboundCrashes += 1;
        sbCrashesWrap += 1;
        sbCrashesTemp += 1;
    }

    public void addSbUnresponsive() {
        starboundUnresponsive += 1;
        sbUnresponsiveWrap += 1;
        sbUnresponsiveTemp += 1;
    }

    public void resetTempStats() {
        sbCrashesTemp = 0;
        sbUnresponsiveTemp = 0;
    }
}
