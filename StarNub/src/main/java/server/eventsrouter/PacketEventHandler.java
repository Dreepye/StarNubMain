package server.eventsrouter;

import starbounddata.packets.Packet;
import utilities.events.EventHandler;

public abstract class PacketEventHandler extends EventHandler<Packet> {

    @Override
    public abstract Packet onEvent(Packet eventData);

}
