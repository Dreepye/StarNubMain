package utilities.events;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public abstract class EventRouter<T1, T2, T3> {

    private final Object HASHSET_LOCK_OBJECT_1 = new Object();

    private final ConcurrentHashMap<T1, HashSet<EventSubscription>> EVENT_SUBSCRIPTION_MAP = new ConcurrentHashMap<>();

    public ConcurrentHashMap<T1, HashSet<EventSubscription>> getEVENT_SUBSCRIPTION_MAP() {
        return EVENT_SUBSCRIPTION_MAP;
    }

    @SuppressWarnings("unchecked")
    public void registerEventSubscription(T1 eventKey, EventSubscription eventSubscription){
            if (!EVENT_SUBSCRIPTION_MAP.containsKey(eventKey)){
                EVENT_SUBSCRIPTION_MAP.put(eventKey, new HashSet<>());
            }
            synchronized (HASHSET_LOCK_OBJECT_1) {
                EVENT_SUBSCRIPTION_MAP.get(eventKey).add(eventSubscription);
        }
    }

    public void removeEventSubscription(String pluginName) {
        for (T1 eventKey : EVENT_SUBSCRIPTION_MAP.keySet()) {
            HashSet<EventSubscription> EVENT_SUBSCRIPTION_SET = EVENT_SUBSCRIPTION_MAP.get(eventKey);
            synchronized (HASHSET_LOCK_OBJECT_1) {
                EVENT_SUBSCRIPTION_SET.stream().filter(eventSubscription -> eventSubscription.getSUBSCRIBER_NAME().equalsIgnoreCase(pluginName)).forEach(EVENT_SUBSCRIPTION_SET::remove);
            }
            if (EVENT_SUBSCRIPTION_MAP.isEmpty()) {
                    EVENT_SUBSCRIPTION_MAP.remove(eventKey);
            }
        }
    }

    public abstract T3 eventNotify(T2 event);

    public abstract T2 handleEvent(T2 event);
}
