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
import io.netty.channel.ChannelFuture;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import network.StarNubMessageClientInitializer;
import org.joda.time.DateTime;
import starnubserver.events.events.StarNubEvent;
import starnubserver.events.starnub.StarNubEventRouter;
import starnubserver.logger.MultiOutputLogger;
import starnubserver.plugins.PluginManager;
import starnubserver.resources.ResourceManager;
import starnubserver.resources.files.Configuration;
import utilities.concurrency.task.TaskManager;
import utilities.connectivity.client.TCPClient;

import javax.net.ssl.SSLException;

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
    private static final TCPClient tcpClient = new TCPClient("StarNub - Central Client - Worker Thread");
    private static final TaskManager taskManager = new TaskManager((int) configuration.getNestedValue("advanced_settings", "resources", "scheduled_task_thread_count"), "StarNub - Scheduled Task");
    private static final StarNubEventRouter starNubEventRouter = new StarNubEventRouter();
    private static final MultiOutputLogger logger = MultiOutputLogger.getInstance();
    private static final StarNubVersion versionInstance = StarNubVersion.getInstance(resourceManager.getStarnubResources());
    private static final PluginManager pluginManager = PluginManager.getInstance();
    private static final StarboundServer STARBOUND_SERVER = StarboundServer.getInstance();
    private static final Connections connections = Connections.getInstance();

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

    public static StarNubEventRouter getStarNubEventRouter() {
        return starNubEventRouter;
    }

    public static PluginManager getPluginManager() {
        return pluginManager;
    }

    public static StarboundServer getStarboundServer() {
        return STARBOUND_SERVER;
    }

    public static TaskManager getTaskManager() {
        return taskManager;
    }

    public static void connectToCentralServer(String host, int port) throws SSLException {
        final SslContext SSL_CTX = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
        ChannelFuture channelFuture = tcpClient.connect(host, port, new StarNubMessageClientInitializer(SSL_CTX, host, port));
        tcpClientChannel = channelFuture.channel();
    }

    public static void disconnectFromCentralServer(){
        tcpClient.shutdown();
    }

    public static void main(String[] args) {
        start();
    }

    private static void start () {
        /* This Resource detector is for debugging only */
//        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID); //NETTY.IO MEMORY DEBUGGING
//        try {
//            connectToCentralServer("127.0.0.1", 666);
//        } catch (SSLException e) {
//            e.printStackTrace();
//        }
//        ThreadSleep.timerSeconds(10);
//        Ban ban = new Ban();
//        ban.setBan("127.0.0.1", "Poop", "Test", new DateTime());
//        for (int i = 0; i < 10; i++) {
//            StarNubMessage starNubMessage = new StarNubMessageBan(
//                    StarNubMessage.Type.BAN_ADD,
//                    ban
//            );
//            System.out.println(starNubMessage);
//            tcpClientChannel.writeAndFlush(starNubMessage);
//        }

        /* Setting Temporary Time - Measuring StarNub Start Up Time */
        DateTime starnubStarTime = DateTime.now();
        /* Identify the main thread as StarNub - Main for debugging and OS Task List Identification */
        Thread.currentThread().setName("StarNub - Main");

//        logger.eventListenerRegistration(); /*  */
        starNubEventRouter.startEventRouter();
        STARBOUND_SERVER.getUdpProxyServer().start();
        new StarNubEvent("StarNub_Startup_Complete", DateTime.now().getMillis() - starnubStarTime.getMillis());

//        new StarNubTask("StarNub", "StarNub - Up Time Notification", true, 30, 30, TimeUnit.SECONDS, new StarNubEvent("StarNub_Up_Time", StarNub::tempTime));
    }
}



