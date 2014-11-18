package server.eventsrouter;

import lombok.Getter;
import server.eventsrouter.handlers.EventHandler;
import server.eventsrouter.subscriptions.EventSubscription;

import java.util.HashMap;
import java.util.HashSet;

public abstract class EventRouter<T> {

    private final Object HASHMAP_LOCK_OBJECT_1 = new Object();
    private final Object HASHSET_LOCK_OBJECT_1 = new Object();
    @Getter
    private final HashMap<T, HashSet<EventSubscription>> EVENT_SUBSCRIPTION_MAP;

    {
        EVENT_SUBSCRIPTION_MAP = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public void registerEventSubscription(String pluginName, T eventKey, EventHandler eventHandler){
//        String resolvedPluginName = "StarNub";
//        if (!pluginName.equalsIgnoreCase("StarNub")) {
//            resolvedPluginName = StarNub.getPluginManager().resolvePlugin(pluginName);
//        }
//        if (resolvedPluginName == null){
//            StarNub.getLogger().cErrPrint("StarNub", "Could not resolve plugin " + pluginName + " " +
//                    "which was used to try and register an event handler. Please consult the plugin developer to fix this.");
//            return;
//        } else {
            EventSubscription eventSubscription = new EventSubscription(pluginName, eventHandler);
            if (!EVENT_SUBSCRIPTION_MAP.containsKey(eventKey)){
                synchronized (HASHMAP_LOCK_OBJECT_1) {
                    EVENT_SUBSCRIPTION_MAP.put(eventKey, new HashSet<>());
                }
            }
            synchronized (HASHSET_LOCK_OBJECT_1) {
                EVENT_SUBSCRIPTION_MAP.get(eventKey).add(eventSubscription);
//            }
        }
    }

    public void removeEventSubscription(String pluginName) {
        for (T eventKey : EVENT_SUBSCRIPTION_MAP.keySet()) {
            HashSet<EventSubscription> EVENT_SUBSCRIPTION_SET = EVENT_SUBSCRIPTION_MAP.get(eventKey);
            EVENT_SUBSCRIPTION_SET.stream().filter(eventSubscription -> eventSubscription.getSUBSCRIBER_NAME().equalsIgnoreCase(pluginName)).forEach(packetEventWrapper -> {
                synchronized (HASHSET_LOCK_OBJECT_1) {
                    EVENT_SUBSCRIPTION_SET.remove(packetEventWrapper);
                }
            });
            if (EVENT_SUBSCRIPTION_MAP.isEmpty()) {
                synchronized (HASHMAP_LOCK_OBJECT_1) {
                    EVENT_SUBSCRIPTION_MAP.remove(eventKey);
                }
            }
        }
    }


}
