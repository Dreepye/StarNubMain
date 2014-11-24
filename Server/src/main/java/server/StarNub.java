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
    private static ServerStats serverStats;
    private static PacketSender packetSender;
    private static DatabaseTables databaseTables;
    private static BannedIPs internallyBannedIps = new BannedIPs(ResourceManager.getStarnubResources());
    private static StarNubEventRouter starNubEventRouter;
    private static PacketEventRouter packetEventRouter;
    private static PluginManager pluginManager;
    private static Server server;
    private static TaskManager taskManager;//(int) ((Map)StarNub.getConfiguration().getConfiguration().get("resources")).get("scheduled_task_thread_count");

    public static ResourceManager getResourceManager() {
        return resourceManager;
    }

    public static Configuration getConfiguration() {
        return configuration;
    }

    public static MultiOutputLogger getLogger() {
        return logger;
    }

    public static DateAndTimes getDateAndTimes() {
        return dateAndTimes;
    }

    public static MessageSender getMessageSender() {
        return messageSender;
    }

    public static StarNubVersion getVersionInstance() {
        return versionInstance;
    }

    public static ServerStats getServerStats() {
        return serverStats;
    }

    public static PacketSender getPacketSender() {
        return packetSender;
    }

    public static DatabaseTables getDatabaseTables() {
        return databaseTables;
    }

    public static Set<InetAddress> getInternallyBannedIps() {
        return internallyBannedIps;
    }

    public static StarNubEventRouter getStarNubEventRouter() {
        return starNubEventRouter;
    }

    public static PacketEventRouter getPacketEventRouter() {
        return packetEventRouter;
    }

    public static PluginManager getPluginManager() {
        return pluginManager;
    }

    public static Server getServer() {
        return server;
    }

    public static Task getTask() {
        return task;
    }

    /**
     * Attempts to set the {@link Configuration} .
     * <p>
     * This cannot be done if the Configuration is already set.
     * @param starnubResources
     */
    private static Configuration setConfiguration(YAMLWrapper starnubResources) {
        if (StarNub.configuration != null) {
            throw new UnsupportedOperationException("Cannot redefine StarNub Configuration");
        }
        return new Configuration(
                "StarNub Configuration",
                "servers/starnub.fileextracts/default_configuration.yml",
                "StarNub/starnub_config.yml");
    }

    /**
     * References the messageSender variable to the {@link server.senders.MessageSender} Enum singleton.
     */
    private static void setMessageSender() {
        StarNub.messageSender = MessageSender.INSTANCE;
    }

    /**
     * References the serverStats variable to the {@link ServerStats} Enum singleton.
     */
    private static void setServerStats() {
        StarNub.serverStats = ServerStats.INSTANCE;
    }

    /**
     * References the messageSender variable to the {@link server.senders.CommandParser} Enum singleton.
     */
    private static void setCommandSender() {
        StarNub.commandParser = CommandParser.INSTANCE;
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

    /**
     * References the database variable to the {@link server.database.DatabaseTables} Enum singleton.
     */
    @SuppressWarnings("unchecked")
    private static void setInternallyBannedIps() {
        HashSet hashSet = new HashSet();
        StarNub.internallyBannedIps = Collections.synchronizedSet(hashSet);
        internallyBannedIpsLoad();
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method is used for reloading the internally banned ips. This is not the same
     * as banned players, this method is for dos and ddos protection and is used to kill connections
     * when they happen at starnub.
     * <p>
     */
    private static void internallyBannedIpsLoad() {
        HashSet<InetAddress> noDuplicateList = new HashSet<>();
        List<String> internallyBannedIps = null;
        try {
            internallyBannedIps = new FileToList().readFileLinesString("StarNub/Databases/blocked_ips.txt");
        } catch (Exception e) {
            logger.cErrPrint("StarNub", "Unable to load \"StarNub/Databases/blocked_ips.txt\". Please check your utilities.file. If the " +
                    "issue persist, please put a issue in at www.StarNub.org under StarNub.");
            return;
        }
        for (String internallyBannedIpsString : internallyBannedIps) {
            InetAddress internallyBannedIpsInetAddress;
            try {
                internallyBannedIpsInetAddress = InetAddress.getByName(internallyBannedIpsString);
                noDuplicateList.add(internallyBannedIpsInetAddress);
            } catch (UnknownHostException e) {
                /* Do Nothing */
            }
        }
        synchronized (INTERNALLY_BANNED_IPS_LOCK) {
            StarNub.internallyBannedIps.addAll(noDuplicateList);
        }
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will try and reload the internally bannedIps
     * <p>
     */
    public static boolean internallyBannedIpsReload() {
        synchronized (INTERNALLY_BANNED_IPS_LOCK) {
            internallyBannedIps.clear();
        }
        internallyBannedIpsLoad();
        return !internallyBannedIps.isEmpty();
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method will check to see if an InetAddress is
     * on the internallyBannedIps list.
     * <p>
     */
    public boolean onInternallyBannedIps(InetAddress inetAddress) {
        return internallyBannedIps.contains(inetAddress);
    }

    /**
     * This EventLoopGroup bossGroup is responsible for incoming
     * connections. bossGroup threads will pass data to workerGroup
     * threads
     * <p>
     * SHOULD NOT BE USED FOR ANYTHING BUT ACCEPTING CONNECTIONS
     * AFTER CONNECTION ACCEPT connectionBossGroup PASSES CONNECTION TO
     * connectionWorkerGroup OR MANAGEMENT_CONNECTION_GROUP
     * <p>
     * @return EventLoopGroup bossGroup
     */


    /**
     * This EventLoopGroup workGroup is responsible for handling
     * active connections after the initial starbounddata.packets.connection start. One thread
     * per client, until exhausted, then clients share threads.
     * <p>
     * SHOULD NOT BE USED FOR ANYTHING BUT MAKING CONNECTIONS OR MANAGING
     * CONNECTIONS FOR STARBOUND AND ASSOCIATED APPLICATIONS.
     * <p>
     * @return EventLoopGroup work Group
     */

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



