package server.eventsrouter.handlers;

import server.server.packets.Packet;

public abstract class PacketEventHandler extends EventHandler{

    public abstract Packet onEvent(Packet eventData);
}
