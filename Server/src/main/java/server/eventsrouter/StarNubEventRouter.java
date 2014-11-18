package server.eventsrouter;

import lombok.Setter;
import server.StarNub;
import server.eventsrouter.events.StarNubEvent;
import server.eventsrouter.handlers.StarNubEventHandler;
import server.eventsrouter.subscriptions.EventSubscription;
import server.plugins.runnable.StarNubRunnable;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class StarNubEventRouter extends EventRouter<String> {

    private final Object HASHSET_LOCK_OBJECT_2 = new Object();
    private volatile int maxThreadCount;
    private volatile HashSet<StarNubRunnable> currentThreads;
    private ArrayBlockingQueue<StarNubEvent<String>> starNubEventsQue;
    @Setter
    private boolean shuttingDown;

    public StarNubEventRouter(){
        super();
    }

    {
        setResources();
    }

    public void setResources(){
        starNubEventsQue = new ArrayBlockingQueue<>(
                (int) ((Map) StarNub.getConfiguration().getConfiguration().get("resources")).get("event_que_size"));
        maxThreadCount =
                (int) ((Map) StarNub.getConfiguration().getConfiguration().get("resources")).get("event_thread_count");
        currentThreads = new HashSet<>(maxThreadCount);
        shuttingDown = false;
    }

    public void startEventRouter(){
        StarNub.getTask().getTaskScheduler().scheduleAtFixedRateRepeatingTask(
                "StarNub",
                "StarNub - Internal Event Handler Processor",
                () -> {
                    int quePercentage = (int)(((double) starNubEventsQue.remainingCapacity()/starNubEventsQue.size())*100);
                    while (quePercentage <= 10){
                        if(startNewRunnable()){
                            System.out.println("Starting new runnable to handle overloaded event que.");
                        }
                    }
                    if(quePercentage <= 25){
                        startNewRunnable();
                    }
                    if(quePercentage > 90){
                        synchronized (HASHSET_LOCK_OBJECT_2){
                            if (currentThreads.size() > 1) {
                                for (StarNubRunnable starNubRunnable : currentThreads) {
                                    starNubRunnable.setShuttingDown(true);
                                    currentThreads.remove(starNubRunnable);
                                    System.out.println("Shutting down idle event thread.");
                                }
                            }
                        }
                    }
                },
                10,
                10,
                TimeUnit.SECONDS);
        if (currentThreads.isEmpty()){
            startNewRunnable();
        }
    }

    public boolean startNewRunnable(){
        if (currentThreads.size() == maxThreadCount){
            System.err.println("STARNUB - CRITICAL EVENT HANDLING ERROR - QUE FULL - MAX THREADS REACHED.");
            return false;
        }
        StarNubRunnable starNubRunnable = (new StarNubRunnable(){
            /**
             * When an object implementing interface <code>Runnable</code> is used
             * to create a thread, starting the thread causes the object's
             * <code>run</code> method to be called in that separately executing
             * thread.
             * <p>
             * The general contract of the method <code>run</code> is that it may
             * take any action whatsoever.
             *
             * @see Thread#run()
             */
            @Override
            public void run() {
                while (!shuttingDown){
                    handleStarNubEvent();
                }
            }
        });
        synchronized (HASHSET_LOCK_OBJECT_2){
            currentThreads.add(starNubRunnable);
        }
        new Thread(starNubRunnable, "StarNub - Event Handler : Thread - " + currentThreads.size()).start();
        return true;
    }

    public void notify(StarNubEvent<String> starNubEvent){
        starNubEventsQue.add(starNubEvent);
    }

    @SuppressWarnings("unchecked")
    public void handleStarNubEvent(){
        try {
            StarNubEvent<String> starNubEvent = starNubEventsQue.take();
            String eventKey = starNubEvent.getEVENT_KEY();
            HashSet<EventSubscription> eventSubscriptions = getEVENT_SUBSCRIPTION_MAP().get(eventKey);
            if (eventSubscriptions != null){
                for (EventSubscription<StarNubEventHandler> starNubEventHandler : eventSubscriptions){
                    starNubEventHandler.getEVENT_HANDLER().onEvent(starNubEvent);
                }
            }
            if (!eventKey.equals("StarNub_Log_Event") && StarNub.getLogger().isLogEvent()) {
                StarNub.getLogger().cEvePrint("StarNub", "Key: " + eventKey + ". Event Data Type: " + starNubEvent.getClass().getSimpleName() + ".class. Event Data: " +starNubEvent.getExtraEventData());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
