package org.starnub.utilities.events;

import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents an EventRouter to be used with {@link EventSubscription} and {@link EventHandler}
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 *
 * @param <T1> Representing the events key
 * @param <T2> Representing the events data
 */
public abstract class EventRouter<T1, T2> {

    private final ConcurrentHashMap<T1, CopyOnWriteArrayList<EventSubscription>> EVENT_SUBSCRIPTION_MAP = new ConcurrentHashMap<>();

    public EventRouter() {
    }

    public ConcurrentHashMap<T1, CopyOnWriteArrayList<EventSubscription>> getEVENT_SUBSCRIPTION_MAP() {
        return EVENT_SUBSCRIPTION_MAP;
    }

    /**
     * Provides a means for registering an {@link EventSubscription}
     *
     * @param eventKey T1 representing the events key
     * @param eventSubscription EventSubscription representing some events subscription
     */
    @SuppressWarnings("unchecked")
    public void registerEventSubscription(T1 eventKey, EventSubscription eventSubscription){
        if (!EVENT_SUBSCRIPTION_MAP.containsKey(eventKey)){
            EVENT_SUBSCRIPTION_MAP.put(eventKey, new CopyOnWriteArrayList<>());
        }
        CopyOnWriteArrayList<EventSubscription> eventSubscriptions = EVENT_SUBSCRIPTION_MAP.get(eventKey);
        eventSubscriptions.add(eventSubscription);
        eventSubscriptions.sort(Comparator.comparing(EventSubscription::getPRIORITY));
    }

    /**
     * Provides a means for removing an {@link EventSubscription} based on the subscribers name
     *
     * @param subscriberName String representing the events subscriber
     */
    public void removeEventSubscription(String subscriberName) {
        for (T1 eventKey : EVENT_SUBSCRIPTION_MAP.keySet()) {
            CopyOnWriteArrayList<EventSubscription> EVENT_SUBSCRIPTION_SET = EVENT_SUBSCRIPTION_MAP.get(eventKey);
            EVENT_SUBSCRIPTION_SET.stream().filter(eventSubscription -> eventSubscription.getSUBSCRIBER_NAME().equalsIgnoreCase(subscriberName)).forEach(EVENT_SUBSCRIPTION_SET::remove);
            if (EVENT_SUBSCRIPTION_SET.isEmpty()) {
                EVENT_SUBSCRIPTION_MAP.remove(eventKey);
            }
        }
    }

    /**
     * Provides a means for removing an {@link EventSubscription} based on the EventSubscription
     *
     * @param eventSubscription EventSubscription representing the events subscriber
     */
    public void removeEventSubscription(EventSubscription eventSubscription){
        for (T1 eventKey : EVENT_SUBSCRIPTION_MAP.keySet()) {
            CopyOnWriteArrayList<EventSubscription> EVENT_SUBSCRIPTION_SET = EVENT_SUBSCRIPTION_MAP.get(eventKey);
            EVENT_SUBSCRIPTION_SET.stream().filter(eventSubscription::equals).forEach(EVENT_SUBSCRIPTION_SET::remove);
            if (EVENT_SUBSCRIPTION_SET.isEmpty()) {
                EVENT_SUBSCRIPTION_MAP.remove(eventKey);
            }
        }
    }

    /**
     * This method must be extended and enhanced to handle events notification of specific data types which may vary
     *
     * @param event T2 events data of some type
     */
    public abstract void eventNotify(T2 event);

    /**
     * This method can be used to check if the Executors are null or now before submitting a task
     *
     * @param event T2 events data of some type
     */
    public abstract void eventNotifyNullCheck(T2 event);

    /**
     * This method must be extended and enhanced to handle starnubdata.events of specific data types which may vary
     *
     * @param event T2 events data of some type
     */
    public abstract void handleEvent(T2 event);

    public String printString() {
        return "EventRouter{" +
                ", EVENT_SUBSCRIPTION_MAP=" + EVENT_SUBSCRIPTION_MAP +
                '}';
    }
}
