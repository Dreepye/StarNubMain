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

package starnub;

import lombok.Getter;
import org.codehome.utilities.timers.ThreadSleep;
import org.joda.time.DateTime;
import starnub.events.events.EventsInternals;

import java.util.concurrent.TimeUnit;

///TODO
///RETIRE

public enum Task {
    INSTANCE;


    private TaskScheduler taskScheduler;
    private ServerStats serverStats;
    private boolean shuttingDown = false;

    {
        setTask();
    }

    public TaskScheduler getTaskScheduler() {
        return taskScheduler;
    }

    public ServerStats getServerStats() {
        return serverStats;
    }

    public boolean isShuttingDown() {
        return shuttingDown;
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
            //purge starbounddata.packets.chat rooms over 30 days once a day

        }

        shutdown();
    }

    private void registerTask() {
        //TODO Change to method once one time are removed from the task list

        /* 5 Second Task */
        taskScheduler.scheduleAtFixedRateRepeatingTask("StarNub", "StarNub - Connected Players Check", new Runnable() {
            @Override
            public void run() {
                StarNub.getServer().getConnectionss().connectedPlayerLostConnectionCheck();
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
                StarNub.getServer().getConnectionss().openSocketPurge();
            }
        }, 30, 30, TimeUnit.SECONDS);

        taskScheduler.scheduleAtFixedRateRepeatingTask("StarNub", "StarNub - Pending Connections Purge", new Runnable() {
            @Override
            public void run() {
                StarNub.getServer().getConnectionss().pendingConnectionPurge();
            }
        }, 30, 30, TimeUnit.SECONDS);

        taskScheduler.scheduleAtFixedRateRepeatingTask("StarNub", "StarNub - Player Played Time Update", new Runnable() {
            @Override
            public void run() {
                StarNub.getServer().getConnectionss().connectedPlayerPlayedTimeUpdate();
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
                if (!StarNub.getServer().getOLDStarboundManager().getStarboundStatus().isRestarting()) {
                    StarNub.getServer().getOLDStarboundManager().statusQuery();
                }
            }
        }, 30, 30, TimeUnit.SECONDS);

        taskScheduler.scheduleAtFixedRateRepeatingTask("StarNub", "StarNub - StarNub Uptime", new Runnable() {
            @Override
            public void run() {
                EventsInternals.eventSend_StarNub_Checks(StarNub.getServerStats().getSnUptime());
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
        StarNub.getLogger().cDebPrint("StarNub", StarNub.getServer().getConnectionss().getOnlinePlayersNameList("StarNub", true, true));
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
