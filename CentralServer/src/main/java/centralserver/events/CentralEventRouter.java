///*
// * Copyright (C) 2014 www.StarNub.org - Underbalanced
// *
// * This file is part of org.starnub a Java Wrapper for Starbound.
// *
// * This above mentioned StarNub software is free software:
// * you can redistribute it and/or modify it under the terms
// * of the GNU General Public License as published by the Free
// * Software Foundation, either version  3 of the License, or
// * any later version. This above mentioned CodeHome software
// * is distributed in the hope that it will be useful, but
// * WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
// * the GNU General Public License for more details. You should
// * have received a copy of the GNU General Public License in
// * this StarNub Software.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//package centralserver.starnubdata.events;
//
//import starnubdata.events.event.CentralEvent;
//import starnubdata.messages.StarNubMessage;
//import starnubserver.StarNub;
//import starnubserver.StarNubTask;
//import starnubserver.plugins.runnable.StarNubRunnable;
//import utilities.starnubdata.events.EventRouter;
//import utilities.starnubdata.events.EventSubscription;
//import utilities.starnubdata.events.types.Event;
//
//import java.util.HashSet;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.TimeUnit;
//
///**
// * Represents StarNubs StarNubEventRouter to be used with {@link starnubserver.starnubdata.events.starnub.StarNubEventSubscription} and
// * {@link starnubserver.starnubdata.events.starnub.StarNubEventHandler}
// *
// * NOTE: TODO THIS CLASS WILL BE PENDING REFACTORING ONCE MORE TO FINISH THE WORKER ALGORITHM BUT IS SUFFICIENT ENOUGH TO RUN PRODUCTION
// *
// * @author Daniel (Underbalanced) (www.StarNub.org)
// * @since 1.0 Beta
// */
//public class CentralEventRouter extends EventRouter<String, CentralEvent, Boolean> {
//
//    private final Object HASHSET_LOCK_OBJECT_2 = new Object();
//    private volatile int maxThreadCount;
//    private volatile HashSet<Runnable> currentThreads;
//    private ArrayBlockingQueue<CentralEvent> eventsQue;
//
//    private boolean shuttingDown;
//
//    public CentralEventRouter(){
//        super();
//        setResources();
//    }
//
//    public void setResources(){
//        eventsQue = new ArrayBlockingQueue<>(1000);
//        maxThreadCount = 3;
//        currentThreads = new HashSet<>(maxThreadCount);
//        shuttingDown = false;
//    }
//
//    public void startEventRouter(){
//        if (currentThreads.isEmpty()){
//            startNewRunnable();
//        }
//    }
//
//    public void checkQue(){
//        int quePercentage = (int) (((double) eventsQue.remainingCapacity() / eventsQue.size()) * 100);
//        while (quePercentage <= 10) {
//            if (startNewRunnable()) {
//                System.out.println("Starting new runnable to handle overloaded event que.");
//            }
//        }
//        if (quePercentage <= 25) {
//            startNewRunnable();
//        }
//        if (quePercentage > 90) {
//            synchronized (HASHSET_LOCK_OBJECT_2) {
//                if (currentThreads.size() > 1) {
//                    for (StarNubRunnable starNubRunnable : currentThreads) {
//                        starNubRunnable.setShuttingDown(true);
//                        currentThreads.remove(starNubRunnable);
//                        System.out.println("Shutting down idle event thread.");
//                    }
//                }
//            }
//        }
//    }
//
//    public boolean startNewRunnable(){
//        if (currentThreads.size() == maxThreadCount){
//            System.err.println("STARNUB - CRITICAL EVENT HANDLING ERROR - QUE FULL - MAX THREADS REACHED.");
//            return false;
//        }
//        StarNubRunnable starNubRunnable = (new StarNubRunnable(){
//            /**
//             * When an object implementing interface <code>Runnable</code> is used
//             * to create a thread, starting the thread causes the object's
//             * <code>run</code> method to be called in that separately executing
//             * thread.
//             * <p>
//             * The general contract of the method <code>run</code> is that it may
//             * take any action whatsoever.
//             *
//             * @see Thread#run()
//             */
//            @Override
//            public void run() {
//                while (!shuttingDown){
//                    try {
//                        handleEvent(eventsQue.take());
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//        synchronized (HASHSET_LOCK_OBJECT_2){
//            currentThreads.add(starNubRunnable);
//        }
//        new Thread(starNubRunnable, "StarNub - Event Handler : Thread - " + currentThreads.size()).start();
//        return true;
//    }
//
//    public void eventNotify(Event<String> event){
//        eventsQue.add(event);
//    }
//
//    @Override
//    @SuppressWarnings("unchecked")
//    public void handleEvent(Event<String> event) {
//            String eventKey = event.getEVENT_KEY();
//            HashSet<EventSubscription> eventSubscriptions = getEVENT_SUBSCRIPTION_MAP().get(eventKey);
//            if (eventSubscriptions != null){
//                for (EventSubscription eventSubscription : eventSubscriptions){
//                    eventSubscription.getEVENT_HANDLER().onEvent(event);
//                }
//            }
//            if (!eventKey.equals("StarNub_Log_Event") && StarNub.getLogger().isLogEvent()) {
//                StarNub.getLogger().cEvePrint("StarNub", "Key: " + eventKey + ". Event Data Type: " + event.getClass().getSimpleName() + ".class. Event Data: " + event.getEVENT_DATA());
//            }
//    }
//}
