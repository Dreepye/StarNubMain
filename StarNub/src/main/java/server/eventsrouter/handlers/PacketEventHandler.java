package server.eventsrouter.handlers;

import starbounddata.packets.Packet;

public abstract class PacketEventHandler extends EventHandler{

    public abstract Packet onEvent(Packet eventData);
}
