package starnub.events.starnub;

import starnub.StarNub;
import starnub.StarNubTask;
import starnub.plugins.runnable.StarNubRunnable;
import utilities.events.EventRouter;
import utilities.events.EventSubscription;
import utilities.events.types.Event;

import java.util.HashSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Represents StarNubs StarNubEventRouter to be used with {@link starnub.events.starnub.StarNubEventSubscription} and
 * {@link starnub.events.starnub.StarNubEventHandler}
 *
 * NOTE: TODO THIS CLASS WILL BE PENDING REFACTORING ONCE MORE TO FINISH THE WORKER ALGORITHM BUT IS SUFFICIENT ENOUGH TO RUN PRODUCTION
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class StarNubEventRouter extends EventRouter<String, Event<String>, Boolean> {

    private final Object HASHSET_LOCK_OBJECT_2 = new Object();
    private volatile int maxThreadCount;
    private volatile HashSet<StarNubRunnable> currentThreads;
    private ArrayBlockingQueue<Event<String>> eventsQue;

    private boolean shuttingDown;

    public StarNubEventRouter(){
        super();
        setResources();
    }

    public void setResources(){
        eventsQue = new ArrayBlockingQueue<>((int) StarNub.getConfiguration().getNestedValue("event_que_size", "resources"));
        maxThreadCount = (int) StarNub.getConfiguration().getNestedValue("event_thread_count", "resources");
        currentThreads = new HashSet<>(maxThreadCount);
        shuttingDown = false;
    }

    public void startEventRouter(){
        new StarNubTask("StarNub", "StarNub - Event Router - Internal Event Handler Processor", true , 10, 10, TimeUnit.SECONDS, this::checkQue);
        if (currentThreads.isEmpty()){
            startNewRunnable();
        }
    }

    public void checkQue(){
        int quePercentage = (int) (((double) eventsQue.remainingCapacity() / eventsQue.size()) * 100);
        while (quePercentage <= 10) {
            if (startNewRunnable()) {
                System.out.println("Starting new runnable to handle overloaded event que.");
            }
        }
        if (quePercentage <= 25) {
            startNewRunnable();
        }
        if (quePercentage > 90) {
            synchronized (HASHSET_LOCK_OBJECT_2) {
                if (currentThreads.size() > 1) {
                    for (StarNubRunnable starNubRunnable : currentThreads) {
                        starNubRunnable.setShuttingDown(true);
                        currentThreads.remove(starNubRunnable);
                        System.out.println("Shutting down idle event thread.");
                    }
                }
            }
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
                    try {
                        handleEvent(eventsQue.take());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        synchronized (HASHSET_LOCK_OBJECT_2){
            currentThreads.add(starNubRunnable);
        }
        new Thread(starNubRunnable, "StarNub - Event Handler : Thread - " + currentThreads.size()).start();
        return true;
    }

    public Boolean eventNotify(Event<String> event){
        return eventsQue.add(event);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Event<String> handleEvent(Event<String> event) {
            String eventKey = event.getEVENT_KEY();
            HashSet<EventSubscription> eventSubscriptions = getEVENT_SUBSCRIPTION_MAP().get(eventKey);
            if (eventSubscriptions != null){
                for (EventSubscription eventSubscription : eventSubscriptions){
                    eventSubscription.getEVENT_HANDLER().onEvent(event);
                }
            }
            if (!eventKey.equals("StarNub_Log_Event") && StarNub.getLogger().isLogEvent()) {
                StarNub.getLogger().cEvePrint("StarNub", "Key: " + eventKey + ". Event Data Type: " + event.getClass().getSimpleName() + ".class. Event Data: " + event.getEVENT_DATA());
            }
        return null;
    }
}
