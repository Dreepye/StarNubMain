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

package starnubserver;

import io.netty.channel.Channel;
import org.joda.time.DateTime;
import starnubserver.events.events.StarNubEvent;
import starnubserver.logger.MultiOutputLogger;
import starnubserver.resources.ResourceManager;
import starnubserver.resources.files.Configuration;
import starnubserver.resources.files.GroupsManagement;

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
    private static final Configuration configuration = Configuration.getInstance();
    private static final MultiOutputLogger logger = MultiOutputLogger.getInstance();
    private static final Connections connections = Connections.getInstance();
    private static final StarNubVersion versionInstance = StarNubVersion.getInstance();
//    private static final PluginManager pluginManager = PluginManager.getInstance();
    private static final StarboundServer STARBOUND_SERVER = StarboundServer.getInstance();

    private StarNub() {
    }

    private static Channel tcpClientChannel;

    public static Connections getConnections() {
        return connections;
    }

    public static ResourceManager getResourceManager() {
        return resourceManager;
    }

    public static Configuration getConfiguration() {
        return configuration;
    }

    public static MultiOutputLogger getLogger() {
        return logger;
    }

    public static StarNubVersion getVersionInstance() {
        return versionInstance;
    }


    public static StarboundServer getStarboundServer() {
        return STARBOUND_SERVER;
    }

    public static void main(String[] args) {
        start();
    }

    private static void start () {
                /* This Resource detector is for debugging only */
//        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID); //NETTY.IO MEMORY DEBUGGING

        DateTime starnubStarTime = DateTime.now();
        Thread.currentThread().setName("StarNub - Main");



        StarboundServer.getInstance().start();
        new StarNubEvent("StarNub_Startup_Complete", DateTime.now().getMillis() - starnubStarTime.getMillis());
        GroupsManagement.getInstance().groupSetup();

//        new StarNubTask("StarNub", "StarNub - Up Time Notification", true, 30, 30, TimeUnit.SECONDS, new StarNubEvent("StarNub_Up_Time", StarNub::tempTime));
    }
}



