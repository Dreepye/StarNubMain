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
import lombok.NoArgsConstructor;
import org.codehome.utilities.files.FileToList;
import org.joda.time.DateTime;
import org.starnub.server.database.DatabaseTables;
import org.starnub.server.eventsrouter.PacketEventRouter;
import org.starnub.server.eventsrouter.StarNubEventRouter;
import org.starnub.server.eventsrouter.events.StarNubEventsInternals;
import org.starnub.server.logger.MultiOutputLogger;
import org.starnub.server.plugins.PluginManager;
import org.starnub.server.senders.CommandParser;
import org.starnub.server.senders.MessageSender;
import org.starnub.server.senders.PacketSender;
import org.starnub.server.server.Server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**TODO - UPDATE
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
@NoArgsConstructor
public final class StarNub {

    private final static Object INTERNALLY_BANNED_IPS_LOCK = new Object();
    @Getter
    private static ResourceManager resourceManager = ResourceManager.getInstance();
    @Getter
    private static Configuration configuration = setConfiguration();
    @Getter
    private static MultiOutputLogger logger = MultiOutputLogger.getInstance();
    @Getter
    private static DateAndTimes dateAndTimes = DateAndTimes.getInstance();
    @Getter
    private static MessageSender messageSender;
    @Getter
    private static StarNubVersion versionInstance = StarNubVersion.getInstance();
    @Getter
    private static ServerStats serverStats;
    @Getter
    private static CommandParser commandParser;
    @Getter
    private static PacketSender packetSender;
    @Getter
    private static DatabaseTables databaseTables;
    @Getter
    private static Set<InetAddress> internallyBannedIps;
    @Getter
    private static StarNubEventRouter starNubEventRouter;
    @Getter
    private static PacketEventRouter packetEventRouter;
    @Getter
    private static PluginManager pluginManager;
    @Getter
    private static Server server;
    @Getter
    private static Task task;

    /**
     * Attempts to set the {@link Configuration} .
     * <p>
     * This cannot be done if the Configuration is already set.
     */
    private static Configuration setConfiguration() {
        if (StarNub.configuration != null) {
            throw new UnsupportedOperationException("Cannot redefine StarNub Configuration");
        }
        return new Configuration(
                "StarNub Configuration",
                "org/starnub/server/fileextracts/default_configuration.yml",
                "StarNub/starnub_config.yml");
    }

    /**
     * References the messageSender variable to the {@link org.starnub.server.senders.MessageSender} Enum singleton.
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
     * References the messageSender variable to the {@link org.starnub.server.senders.CommandParser} Enum singleton.
     */
    private static void setCommandSender() {
        StarNub.commandParser = CommandParser.INSTANCE;
    }

    /**
     * References the packetSender variable to the {@link org.starnub.server.senders.PacketSender} Enum singleton.
     */
    private static void setPacketSender() {
        StarNub.packetSender = PacketSender.INSTANCE;
    }

    /**
     * References the database variable to the {@link org.starnub.server.database.DatabaseTables} Enum singleton.
     */
    private static void setDatabase() {
        StarNub.databaseTables = DatabaseTables.INSTANCE;
    }

    /**
     * References the database variable to the {@link org.starnub.server.database.DatabaseTables} Enum singleton.
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
            logger.cErrPrint("StarNub", "Unable to load \"StarNub/Databases/blocked_ips.txt\". Please check your file. If the " +
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
     * active connections after the initial connection start. One thread
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
     * References the pluginManager variable to the {@link org.starnub.server.plugins.PluginManager} Enum singleton.
     */
    private static void setPluginManager() {
        StarNub.pluginManager = PluginManager.INSTANCE;
        pluginManager.initializePluginManager();
        pluginManager.initialStartup();
    }

    /**
     * References the server variable to the {@link org.starnub.server.server.Server} Enum singleton.
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
        setConfiguration();
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



