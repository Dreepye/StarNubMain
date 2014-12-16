package starnubserver.events.starnub;

import starnubserver.StarNub;
import starnubserver.StarNubTaskManager;
import utilities.events.EventRouter;
import utilities.events.EventSubscription;
import utilities.events.types.ObjectEvent;

import java.util.HashSet;

/**
 * Represents StarNubs StarNubEventRouter to be used with {@link starnubserver.events.starnub.StarNubEventSubscription} and
 * {@link starnubserver.events.starnub.StarNubEventHandler}
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class StarNubEventRouter extends EventRouter<String, ObjectEvent, Boolean> {

    /**
     * This is instantiated to build out the Packet Event Router
     */
    private static final StarNubEventRouter INSTANCE = new StarNubEventRouter();

    private StarNubEventRouter(){
        super();
    }

    public static StarNubEventRouter getInstance() {
        return INSTANCE;
    }

    public void eventNotify(ObjectEvent event){
        StarNubTaskManager.getInstance().submit(() -> handleEvent(event));
    }

    public void eventNotifyNullCheck(ObjectEvent event){
        if (StarNub.getConfiguration() != null) {
            StarNubTaskManager starNubTaskManager = StarNubTaskManager.getInstance();
            if (starNubTaskManager != null) {
                starNubTaskManager.submit(() -> handleEvent(event));
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleEvent(ObjectEvent event) {
            Object eventKey = event.getEVENT_KEY();
            HashSet<EventSubscription> eventSubscriptions = getEVENT_SUBSCRIPTION_MAP().get(eventKey);
            if (eventSubscriptions != null){
                for (EventSubscription eventSubscription : eventSubscriptions){
                    eventSubscription.getEVENT_HANDLER().onEvent(event);
                }
            }
            if (!eventKey.equals("StarNub_Log_Event") && StarNub.getLogger().isLogEvent()) {
                StarNub.getLogger().cEvePrint("StarNub", "Key: " + eventKey + ". Event Data Type: " + event.getClass().getSimpleName() + ".class. Event Data: " + event.getEVENT_DATA());
            }
    }
}
