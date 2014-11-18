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

package org.starnub.server.plugins;

import org.starnub.server.StarNub;
import org.starnub.server.plugins.runnable.StarNubRunnable;

import java.io.File;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents Java Runnable Loader.
 * <p>
 * This enum singleton will create a concurrent hashmap of threads and runnables from a plugin.
 * <p>
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
enum ThreadsAndRunnablesPreload {
    INSTANCE;

    /**
     * Default constructor
     */
    ThreadsAndRunnablesPreload() {}

    @SuppressWarnings("unchecked")
    protected ConcurrentHashMap<Thread, StarNubRunnable> preloadThreadsAndRunnables(Object sender, File prospectivePlugin, String pluginName, URLClassLoader classLoader, ArrayList<String> classes) {
        ConcurrentHashMap<Thread, StarNubRunnable> threads = new ConcurrentHashMap<Thread, StarNubRunnable>();

        int threadCount = 0;
        for (String classString : classes) {
            String name = "Plugin - " + pluginName +" : Class - " +classString.substring(classString.lastIndexOf(".")+1)+ " : Thread - "+threadCount;
            StarNubRunnable runnable = runnablePackageLoader(sender, pluginName, classLoader, classString);
            threads.putIfAbsent(new Thread(runnable, name), runnable);
            threadCount++;
        }
        return threads;
    }

    @SuppressWarnings("unchecked")
    private StarNubRunnable runnablePackageLoader(Object sender, String pluginName, URLClassLoader classLoader, String classString) {

        /* Load Class into a java class, then cast to a Plugin */
        StarNubRunnable starNubRunnable;
        Class<?> javaClass;
        try {
            javaClass = classLoader.loadClass(classString);
            try {
                Class<? extends StarNubRunnable> starNubRunnableClass = javaClass.asSubclass(StarNubRunnable.class);
                try {
                    return starNubRunnableClass.newInstance();
                } catch (InstantiationException e) {
                    StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName + " runnable load error, could not instantiate StarNubRunnable.");
                    return null;
                } catch (IllegalAccessException e) {
                    StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName + " runnable load error, could not instantiate plugin, illegal access exception.");
                    return null;
                }
            } catch (ClassCastException e) {
                StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName + " runnable load error, does not represent a StarNubRunnable.");
                return null;
            }
        } catch (ClassNotFoundException e) {
            StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName + " runnable load error, class not found or issue with plugin imports: \"" + classString + "\".");
            return null;
        } catch (NoClassDefFoundError e) {
            StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, pluginName + " runnable load error, package not found or issue with plugin imports: \"" + classString + "\".");
            return null;
        }
    }
}




