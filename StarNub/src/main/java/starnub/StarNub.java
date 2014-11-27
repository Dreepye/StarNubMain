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

import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;
import starboundmanager.StarboundManagement;
import starnub.database.DatabaseTables;
import starnub.events.events.EventsInternals;
import starnub.events.events.StarNubEvent;
import starnub.events.packet.PacketEventRouter;
import starnub.events.starnub.StarNubEventRouter;
import starnub.logger.MultiOutputLogger;
import starnub.plugins.PluginManager;
import starnub.resources.Configuration;
import starnub.resources.ResourceManager;
import utilities.concurrency.task.TaskManager;
import utilities.time.DateAndTimes;

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

    private static final ResourceManager resourceManager = ResourceManager.getInstance();
    private static final Configuration configuration = new Configuration(resourceManager.getStarnubResources());
    private static final DateAndTimes dateAndTimes = DateAndTimes.getInstance();
    private static final TaskManager taskManager = new TaskManager((int) configuration.getNestedValue("scheduled_task_thread_count", "resources"), "StarNub - Scheduled Task");
    private static final MultiOutputLogger logger = MultiOutputLogger.getInstance();
    private static final StarNubVersion versionInstance = StarNubVersion.getInstance(resourceManager.getStarnubResources());
    private static final Connections connections = Connections.getInstance();
    private static final DatabaseTables databaseTables = DatabaseTables.getInstance();
    private static final StarNubEventRouter starNubEventRouter = new StarNubEventRouter();
    private static final PluginManager pluginManager;
    private static final Server server;

    public static Connections getConnections() {
        return connections;
    }

    public static ResourceManager getResourceManager() {
        return resourceManager;
    }

    public static Configuration getConfiguration() {
        return configuration;
    }

    public static DateAndTimes getDateAndTimes() {
        return dateAndTimes;
    }

    public static MultiOutputLogger getLogger() {
        return logger;
    }

    public static StarNubVersion getVersionInstance() {
        return versionInstance;
    }

    public static DatabaseTables getDatabaseTables() {
        return databaseTables;
    }

    public static StarNubEventRouter getStarNubEventRouter() {
        return starNubEventRouter;
    }

    public static PluginManager getPluginManager() {
        return pluginManager;
    }

    public static Server getServer() {
        return server;
    }

    public static TaskManager getTaskManager() {
        return taskManager;
    }

    public static void main(String[] args) {
        start();
    }

    /**
     * This method will set the various singletons.
     */
    private static void start () {
        /* This Resource detector is for debugging only */
//        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID); //NETTY.IO MEMORY DEBUGGING

        /* Setting Temporary Time - Measuring StarNub Start Up Time */
        DateTime starnubStarTime = DateTime.now();
        /* Identify the main thread as StarNub - Main for debugging and OS Task List Identification */
        Thread.currentThread().setName("StarNub - Main");

        logger.eventListenerRegistration(); /*  */
        starNubEventRouter.startEventRouter();
        new StarNubEvent("StarNub_Startup_Complete", DateTime.now().getMillis() - starnubStarTime.getMillis());
    }


}



