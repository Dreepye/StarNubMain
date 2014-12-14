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

package starnubserver.plugins.resources;

import starnubserver.StarNub;
import starnubserver.StarNubTask;
import starnubserver.events.events.StarNubEvent;

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Represents Java Runnable Loader.
 * <p>
 * This enum singleton will create a concurrent hashmap of threads and runnables from a plugin.
 * <p>
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public class PluginRunnables {

    private final ConcurrentHashMap<Thread, StarNubRunnable> RUNNABLES = new ConcurrentHashMap<>();

    private HashMap<String, String> preloadRunnables(String pluginName, URLClassLoader classLoader, ArrayList<String> classes){
        int threadCount = 0;
        HashMap<String, String> results = new HashMap<>();
        for (String classString : classes) {
            String name = "Plugin - " + pluginName +" : Class - " +classString.substring(classString.lastIndexOf(".") + 1) + " : Thread - " + threadCount;
            threadCount++;
            Class<?> javaClass;
            StarNubRunnable starNubRunnable = null;
            try {
                javaClass = classLoader.loadClass(classString);
                try {
                    Class<? extends StarNubRunnable> starNubRunnableClass = javaClass.asSubclass(StarNubRunnable.class);
                    try {
                        starNubRunnable = starNubRunnableClass.newInstance();
                    } catch (InstantiationException e) {
                        results.put(classString, "Failed: Runnable load error, could not instantiate StarNubRunnable.");
                    } catch (IllegalAccessException e) {
                        results.put(classString, "Failed: Runnable load error, could not instantiate plugin, illegal access exception.");
                    }
                } catch (ClassCastException e) {
                    results.put(classString, "Failed: Runnable load error, does not represent a StarNubRunnable.");
                }
            } catch (ClassNotFoundException e) {
                results.put(classString, "Failed: Runnable load error, class not found or issue with plugin imports: \"" + classString + "\".");
            } catch (NoClassDefFoundError e) {
                results.put(classString, "Failed: Runnable load error, package not found or issue with plugin imports: \"" + classString + "\".");
            }
            if (starNubRunnable != null) {
                RUNNABLES.putIfAbsent(new Thread(starNubRunnable, name), starNubRunnable);
                results.put(classString, "Success");

            }
        }
        return results;
    }

    public void startThreads(){
        for (Thread thread : RUNNABLES.keySet()){
            StarNub.getLogger().cInfoPrint("StarNub", "Starting thread: "+thread.getName()+".");
            thread.start();
            new StarNubEvent("Permanent_Thread_Started", thread);
        }
    }

    public void shutdownThreads(){
        for(Map.Entry<Thread, StarNubRunnable> entry : RUNNABLES.entrySet()){
            Thread thread = entry.getKey();
            StarNubRunnable starNubRunnable = entry.getValue();
            StarNub.getLogger().cInfoPrint("StarNub", "Stopping thread: "+thread.getName() + ".");
            new StarNubEvent("Permanent_Thread_Stopped", thread);
            starNubRunnable.shutdownGracefully();
            new StarNubTask("StarNub", "Thread Stopper - Plugin - " + thread.getName(), 1, TimeUnit.MINUTES, () -> forceShutdown(thread));
        }
    }

    public void forceShutdown(Thread thread){
        if (thread.isAlive()) {
            thread.interrupt();
            new StarNubEvent("Permanent_Thread_Force_Stopped", thread);
        }
    }
}




