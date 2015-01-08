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

package org.starnub.starnubserver.plugins.resources;

import org.starnub.starnubserver.StarNub;
import org.starnub.starnubserver.StarNubTask;
import org.starnub.starnubserver.events.events.StarNubEvent;

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

    public PluginRunnables() {
    }

    public PluginRunnables(ConcurrentHashMap<Thread, StarNubRunnable> runnables){
        RUNNABLES.putAll(runnables);
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




