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

package org.starnub.starnubserver;

import org.joda.time.DateTime;
import org.starnub.starnubserver.events.events.StarNubEvent;
import org.starnub.starnubserver.logger.MultiOutputLogger;
import org.starnub.starnubserver.pluggable.PluggableManager;
import org.starnub.starnubserver.resources.ResourceManager;
import org.starnub.starnubserver.resources.StringTokens;
import org.starnub.starnubserver.resources.files.Configuration;
import org.starnub.starnubserver.resources.files.GroupsManagement;
import org.starnub.starnubserver.servers.starbound.StarboundServer;

import java.util.concurrent.TimeUnit;

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

    private static final long STARNUB_START_TIME = System.currentTimeMillis();
    private static final ResourceManager RESOURCE_MANAGER = ResourceManager.getInstance();
    private static final Configuration CONFIGURATION = Configuration.getInstance();
    private static final MultiOutputLogger LOGGER = MultiOutputLogger.getInstance();
    private static final Connections CONNECTIONS = Connections.getInstance();
    private static final StarNubVersion VERSION = StarNubVersion.getInstance();
    private static final PluggableManager PLUGIN_MANAGER = PluggableManager.getInstance();
    private static final StarboundServer STARBOUND_SERVER = StarboundServer.getInstance();

    private StarNub() {}

    public static long getStarnubStartTime() {
        return STARNUB_START_TIME;
    }

    public static ResourceManager getResourceManager() {
        return RESOURCE_MANAGER;
    }

    public static Configuration getConfiguration() {
        return CONFIGURATION;
    }

    public static MultiOutputLogger getLogger() {
        return LOGGER;
    }

    public static Connections getConnections() {
        return CONNECTIONS;
    }

    public static StarNubVersion getVersion() {
        return VERSION;
    }

    public static PluggableManager getPluginManager() {
        return PLUGIN_MANAGER;
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
        Thread.currentThread().setName("StarNub - Main");

        StarboundServer.getInstance().startServers();

        GroupsManagement.getInstance().groupSetup();

//        PluginManager.getInstance().loadAllPlugins(false, true);
        PluggableManager.getInstance().loadAllPlugins(true);
        PluggableManager.getInstance().loadAllCommands(true);


        setUptimeTask();

        StringTokens.getInstance().registerInternalTokens();

        new StarNubEvent("StarNub_Startup_Complete", System.currentTimeMillis() - STARNUB_START_TIME);




        //PYTHON TODO SET CACHE LOCATION THROUGH THIS
//        PythonInterpreter interpreter = new PythonInterpreter();
//        interpreter.execfile("StarNub/Plugins/TestPlugin.py");
////        interpreter.exec("from TestPlugin import Vec2IPlus");
//        PyObject buildingClass = interpreter.get("Vec2IPlus");
//        PyObject buildingObject = buildingClass.__call__();
//        Vec2I vec2I = (Vec2I) buildingObject.__tojava__(Vec2I.class);
//        System.out.println(vec2I.getX());
//        System.out.println(vec2I.getY());

//        PyInstance pyInstance = interpreter.eval(className + "(" + opts + ")");
//        PyInstance hello = ie.createClass("Hello", "None");
//        hello.invoke("run");

//        interpreter.exec("from Building import Building");
//        PyObject pyObject = interpreter.get("test");
//        pyObject.invoke();
//        pyObject.invoke("printer");
//        pyObject.invoke("getX");


    }

    private static void setUptimeTask(){
        new StarNubTask("StarNub", "StarNub - Uptime", true, 15, 15, TimeUnit.SECONDS, () -> new StarNubEvent("StarNub_Uptime",  DateTime.now().getMillis() - STARNUB_START_TIME));
        new StarNubTask("StarNub", "Starbound - Uptime", true, 15, 15, TimeUnit.SECONDS, () -> new StarNubEvent("Starbound_Uptime", StarboundServer.getInstance().getUptime()));
    }
}



