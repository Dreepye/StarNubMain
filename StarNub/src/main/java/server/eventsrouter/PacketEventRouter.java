package server.eventsrouter;

import server.eventsrouter.subscriptions.EventSubscription;
import starbounddata.packets.Packet;

import java.util.HashSet;

public class PacketEventRouter extends EventRouter<Class<? extends Packet>, Packet, Packet> {

    public PacketEventRouter(){
        super();
    }

    @Override
    public Packet eventNotify(Packet packet) {
        return handleEvent(packet);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Packet handleEvent(Packet packet){
        HashSet<EventSubscription> eventSubscriptions = getEVENT_SUBSCRIPTION_MAP().get(packet.getClass());
        if (eventSubscriptions == null){
            return packet;
        } else {
            for (EventSubscription<PacketEventHandler> packetEventSubscription : eventSubscriptions){
                packet = packetEventSubscription.getEVENT_HANDLER().onEvent(packet);
                try {
                    if (packet.isRecycle()) {
                        packet.recycle();
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
