package utilities.events;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents an EventRouter to be used with {@link utilities.events.EventSubscription} and {@link utilities.events.EventHandler}
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 *
 * @param <T1> Representing the event key
 * @param <T2> Representing the event data
 * @param <T3> Representing the return type of the eventNotify() method
 */
public abstract class EventRouter<T1, T2, T3> {

    private final Object HASHSET_LOCK_OBJECT_1 = new Object();

    private final ConcurrentHashMap<T1, HashSet<EventSubscription>> EVENT_SUBSCRIPTION_MAP = new ConcurrentHashMap<>();

    public ConcurrentHashMap<T1, HashSet<EventSubscription>> getEVENT_SUBSCRIPTION_MAP() {
        return EVENT_SUBSCRIPTION_MAP;
    }

    /**
     * Provides a means for registering an {@link utilities.events.EventSubscription}
     *
     * @param eventKey T1 representing the event key
     * @param eventSubscription EventSubscription representing some event subscription
     */
    @SuppressWarnings("unchecked")
    public void registerEventSubscription(T1 eventKey, EventSubscription eventSubscription){
            if (!EVENT_SUBSCRIPTION_MAP.containsKey(eventKey)){
                EVENT_SUBSCRIPTION_MAP.put(eventKey, new HashSet<>());
            }
            synchronized (HASHSET_LOCK_OBJECT_1) {
                EVENT_SUBSCRIPTION_MAP.get(eventKey).add(eventSubscription);
        }
    }

    /**
     * Provides a means for removing an {@link utilities.events.EventSubscription} based on the subscribers name
     *
     * @param subscriberName String representing the event subscriber
     */
    public void removeEventSubscription(String subscriberName) {
        for (T1 eventKey : EVENT_SUBSCRIPTION_MAP.keySet()) {
            HashSet<EventSubscription> EVENT_SUBSCRIPTION_SET = EVENT_SUBSCRIPTION_MAP.get(eventKey);
            synchronized (HASHSET_LOCK_OBJECT_1) {
                EVENT_SUBSCRIPTION_SET.stream().filter(eventSubscription -> eventSubscription.getSUBSCRIBER_NAME().equalsIgnoreCase(subscriberName)).forEach(EVENT_SUBSCRIPTION_SET::remove);
            }
            if (EVENT_SUBSCRIPTION_MAP.isEmpty()) {
                    EVENT_SUBSCRIPTION_MAP.remove(eventKey);
            }
        }
    }

    /**
     * Provides a means for removing an {@link utilities.events.EventSubscription} based on the EventSubscription
     *
     * @param eventSubscription EventSubscription representing the event subscriber
     */
    public void removeEventSubscription(EventSubscription eventSubscription){
        for (T1 eventKey : EVENT_SUBSCRIPTION_MAP.keySet()) {
            HashSet<EventSubscription> EVENT_SUBSCRIPTION_SET = EVENT_SUBSCRIPTION_MAP.get(eventKey);
            synchronized (HASHSET_LOCK_OBJECT_1) {
                EVENT_SUBSCRIPTION_SET.stream().filter(eventSubscription::equals).forEach(EVENT_SUBSCRIPTION_SET::remove);
            }
            if (EVENT_SUBSCRIPTION_MAP.isEmpty()) {
                EVENT_SUBSCRIPTION_MAP.remove(eventKey);
            }
        }
    }

    /**
     * This method must be extended and enhanced to handle event notification of specific data types which may vary
     *
     * @param event T2 event data of sometime
     * @return T3 some data back, can be event data or boolean, anything
     */
    public abstract T3 eventNotify(T2 event);

    /**
     * This method must be extended and enhanced to handle events of specific data types which may vary
     *
     * @param event T2 event data of sometime
     * @return T2 returns the handled event data back
     */
    public abstract T2 handleEvent(T2 event);
}
