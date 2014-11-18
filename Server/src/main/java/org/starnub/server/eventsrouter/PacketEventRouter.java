package org.starnub.server.eventsrouter;

import org.starnub.server.eventsrouter.handlers.PacketEventHandler;
import org.starnub.server.eventsrouter.subscriptions.EventSubscription;
import org.starnub.server.server.packets.Packet;

import java.util.HashSet;

public class PacketEventRouter extends EventRouter<Class<? extends Packet>> {

    private final Object HASHMAP_LOCK_OBJECT = new Object();
    private final Object HASHSET_LOCK_OBJECT = new Object();

    public PacketEventRouter(){
        super();
    }



    @SuppressWarnings("unchecked")
    public Packet handlePacket(Packet packet){
        HashSet<EventSubscription> eventSubscriptions = getEVENT_SUBSCRIPTION_MAP().get(packet.getClass());
        if (eventSubscriptions == null){
            return packet;
        } else {
            for (EventSubscription<PacketEventHandler> packetEventSubscription : eventSubscriptions){
                packet = packetEventSubscription.getEVENT_HANDLER().onEvent(packet);
                try {
                    if (packet.isRecycle()) {
                        packet.setRecycle(false);
                        return packet;
                    }
                } catch (NullPointerException e){
                    System.err.println("CRITICAL ERROR. A PLUGIN DID NOT RETURN A PACKET. THIS WILL CAUSE A CLIENT TO DISCONNECT.");
                }
            }
            return packet;
        }
    }
}
