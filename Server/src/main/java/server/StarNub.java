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

import org.codehome.utilities.files.FileToList;
import org.joda.time.DateTime;
import server.database.DatabaseTables;
import server.eventsrouter.PacketEventRouter;
import server.eventsrouter.StarNubEventRouter;
import server.eventsrouter.events.StarNubEventsInternals;
import server.logger.MultiOutputLogger;
import server.plugins.PluginManager;
import server.resources.BannedIPs;
import server.resources.Configuration;
import server.resources.ResourceManager;
import server.senders.MessageSender;
import server.senders.PacketSender;
import server.server.Server;
import utilities.concurrency.task.TaskManager;
import utilities.yaml.YAMLWrapper;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents the StarNubs core.
 * <p>
 * This final class will load in all important modules to managing StarNub
 * and the associated Starbound related modules.
 * <p>
 * Additionally our packages are arranged in a way as to prevent access to some classes that
 * we did not want to make singletons out of, but should not be accessed directly.
 * <p>
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */

public final class StarNub {

    private static ResourceManager resourceManager = ResourceManager.getInstance();
    private static Configuration configuration = new Configuration(ResourceManager.getStarnubResources());



    private static MultiOutputLogger logger = MultiOutputLogger.getInstance();
//    private static DateAndTimes dateAndTimes = DateAndTimes.getInstance();
    private static MessageSender messageSender;
//    private static StarNubVersion versionInstance = StarNubVersion.getInstance();
    private static PacketSender packetSender;
    private static DatabaseTables databaseTables;
    private static BannedIPs internallyBannedIps = new BannedIPs(ResourceManager.getStarnubResources());
    private static StarNubEventRouter starNubEventRouter;
    private static PacketEventRouter packetEventRouter;
    private static PluginManager pluginManager;
    private static Server server;
    private static TaskManager taskManager;//(int) ((Map)StarNub.getConfiguration().getConfiguration().get("resources")).get("scheduled_task_thread_count");


    /**
     * References the messageSender variable to the {@link server.senders.MessageSender} Enum singleton.
     */
    private static void setMessageSender() {
        StarNub.messageSender = MessageSender.INSTANCE;
    }


    /**
     * References the packetSender variable to the {@link server.senders.PacketSender} Enum singleton.
     */
    private static void setPacketSender() {
        StarNub.packetSender = PacketSender.INSTANCE;
    }

    /**
     * References the database variable to the {@link server.database.DatabaseTables} Enum singleton.
     */
    private static void setDatabase() {
        StarNub.databaseTables = DatabaseTables.INSTANCE;
    }



    private static void setStarNubEventRouter(){
        StarNub.starNubEventRouter = new StarNubEventRouter();
    }

    /**
     * References the packetEventRouter variable to the {@link org.starnub.eventsrouter} Enum singleton.
     */
    private static void setPacketEventRouter() {
        StarNub.packetEventRouter = new PacketEventRouter();
    }

    /**
     * References the pluginManager variable to the {@link server.plugins.PluginManager} Enum singleton.
     */
    private static void setPluginManager() {
        StarNub.pluginManager = PluginManager.INSTANCE;
        pluginManager.initializePluginManager();
        pluginManager.initialStartup();
    }

    /**
     * References the starbounddata.packets.starbounddata.packets.server variable to the {@link server.server.Server} Enum singleton.
     */
    private static void setServer() {
        server = Server.INSTANCE;
    }

    /**
     * References the task variable to the {@link Task} Enum singleton.
     */
    private static void setTask() {
        StarNub.task = Task.INSTANCE;
    }

    public static void main(String[] args) {
        start();
    }

    /**
     * This method will set the various singletons.
     */
    private static void start () {
//        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID); //NETTY.IO MEMORY DEBUGGING

        DateTime starnubStarTime = DateTime.now();
        Thread.currentThread().setName("StarNub - Main");
        setResourceManager();
        setConfiguration(server.resources.ResourceManager.getStarnubResources());
        setDateAndTimes();
        setMessageSender();
        setLogger();
        setStarNubEventRouter();
        setPacketEventRouter();
        setTask();
        logger.eventListenerRegistration(); /*  */
        starNubEventRouter.startEventRouter();
        setVersionInstance();
        setServerStats();
        serverStats.setSnOnlineTime(starnubStarTime);
        setCommandSender();
        setPacketSender();
        setDatabase();
        setInternallyBannedIps();
        setPluginManager();
        setServer();
        StarNubEventsInternals.eventSend_StarNub_Startup_Complete(DateTime.now().getMillis() - starnubStarTime.getMillis());
        task.start();
    }


}



