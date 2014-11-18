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

package org.starnub.server;

import lombok.Getter;
import org.codehome.utilities.timers.ThreadSleep;
import org.joda.time.DateTime;
import org.starnub.server.eventsrouter.events.StarNubEventsInternals;

import java.util.concurrent.TimeUnit;

public enum Task {
    INSTANCE;

    @Getter
    private TaskScheduler taskScheduler;
    @Getter
    private ServerStats serverStats;
    @Getter
    private boolean shuttingDown = false;

    {
        setTask();
    }

    /**
     * References version instance variable to the {@link TaskScheduler} Enum singleton.
     */
    private void setTaskScheduler() {
        if (taskScheduler == null) {
            taskScheduler = TaskScheduler.INSTANCE;
        }
    }

    /**
     * References version instance variable to the {@link ServerStats} Enum singleton.
     */
    private void setServerStats() {
        if (serverStats == null) {
            serverStats = ServerStats.INSTANCE;
        }
    }

    public void setTask(){
        setServerStats();
        setTaskScheduler();
        taskScheduler.setScheduledTask();
        serverStats.setTodaysDay(new DateTime().getDayOfMonth());
        registerTask();
    }

    public void setShuttingDown(boolean shuttingDown) {
        this.shuttingDown = shuttingDown;
    }

    protected void start() {
        /* Initial and single task */
        int counter = 0;
        while (!shuttingDown) {

        new ThreadSleep().timerSeconds(30);
            /* These are debug message just for information */
            if (StarNub.getLogger().isLogDebug()) {
                debugMessages();
            }
            //purge chat rooms over 30 days once a day

        }

        shutdown();
    }

    private void registerTask() {
        //TODO Change to method once one time are removed from the task list

        /* 5 Second Task */
        taskScheduler.scheduleAtFixedRateRepeatingTask("StarNub", "StarNub - Connected Players Check", new Runnable() {
            @Override
            public void run() {
                StarNub.getServer().getConnections().connectedPlayerLostConnectionCheck();
            }
        }, 5, 5, TimeUnit.SECONDS);

        /* 15 Second Task */
        taskScheduler.scheduleAtFixedRateRepeatingTask("StarNub", "StarNub - Log Flush", new Runnable() {
            @Override
            public void run() {
                StarNub.getLogger().flushAllLogs();
            }
        }, 30, 30, TimeUnit.SECONDS);

        /* 30 Second Task */
        taskScheduler.scheduleAtFixedRateRepeatingTask("StarNub", "StarNub - Log Rotate Check", new Runnable() {
            @Override
            public void run() {
                StarNub.getLogger().logRotateCheckAllLogs();
            }
        }, 30, 30, TimeUnit.SECONDS);

        taskScheduler.scheduleAtFixedRateRepeatingTask("StarNub", "StarNub - Open Socket Purge", new Runnable() {
            @Override
            public void run() {
                StarNub.getServer().getConnections().openSocketPurge();
            }
        }, 30, 30, TimeUnit.SECONDS);

        taskScheduler.scheduleAtFixedRateRepeatingTask("StarNub", "StarNub - Pending Connections Purge", new Runnable() {
            @Override
            public void run() {
                StarNub.getServer().getConnections().pendingConnectionPurge();
            }
        }, 30, 30, TimeUnit.SECONDS);

        taskScheduler.scheduleAtFixedRateRepeatingTask("StarNub", "StarNub - Player Played Time Update", new Runnable() {
            @Override
            public void run() {
                StarNub.getServer().getConnections().connectedPlayerPlayedTimeUpdate();
            }
        }, 30, 30, TimeUnit.SECONDS);

        taskScheduler.scheduleAtFixedRateRepeatingTask("StarNub", "StarNub - Remove Empty Chat Channels", new Runnable() {
            @Override
            public void run() {
                StarNub.getServer().getServerChat().removeEmptyChannels();
            }
        }, 30, 30, TimeUnit.SECONDS);

        taskScheduler.scheduleAtFixedRateRepeatingTask("StarNub", "StarNub - Starbound Server Query", new Runnable() {
            @Override
            public void run() {
                if (!StarNub.getServer().getStarboundManager().getStarboundStatus().isRestarting()) {
                    StarNub.getServer().getStarboundManager().statusQuery();
                }
            }
        }, 30, 30, TimeUnit.SECONDS);

        taskScheduler.scheduleAtFixedRateRepeatingTask("StarNub", "StarNub - StarNub Uptime", new Runnable() {
            @Override
            public void run() {
                StarNubEventsInternals.eventSend_StarNub_Checks(StarNub.getServerStats().getSnUptime());
            }
        }, 30, 30, TimeUnit.SECONDS);

        taskScheduler.scheduleAtFixedRateRepeatingTask("StarNub", "StarNub - StarNub One Time Task Purger", new Runnable() {
            @Override
            public void run() {
                StarNub.getTask().getTaskScheduler().oneTimeTaskPurge();
            }
        }, 30, 30, TimeUnit.SECONDS);

        /* 1 Min Task */
        taskScheduler.scheduleAtFixedRateRepeatingTask("StarNub", "StarNub - Player Search Cache Purge", new Runnable() {
            @Override
            public void run() {
//                StarNub.shutdownNetworkThreads();
//                StarNub.setNetworkThreading();
//                StarNub.getServer().startTCPServer();
//                StarNub.getServer().getConnections().playerQueryCachePurge();
            }
        }, 1, 1, TimeUnit.MINUTES);

    }

    private void debugMessages() {
        StarNub.getLogger().cDebPrint("StarNub", StarNub.getServer().getServerChat().getChatRoomPlayersListAllRooms("StarNub"));
        StarNub.getLogger().cDebPrint("StarNub", StarNub.getServer().getConnections().getOnlinePlayersNameList("StarNub", true, true));
    }

    public void shutdown() {
        //PLACE_HOLDER - EVENTS
        StarNub.getLogger().cInfoPrint("StarNub", "Shutting StarNub down, it may take a few moments.");
        StarNub.getLogger().cInfoPrint("StarNub", "Exiting StarNub. Goodbye!");
        //PLACE_HOLDER - EVENTS
        //PLACE_HOLDER - onWrapper complete graceful shutdown checks
        System.exit(0);
    }

    protected void threadCheck () {
        //PLACE_HOLDER - Check boss, worker group, udp proxy, environment, reactors
    }

    protected void updateCheck () {

    }

    protected void publicTask () {
    }


}
