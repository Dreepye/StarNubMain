package org.starnub.server.eventsrouter.handlers;

import org.starnub.server.server.packets.Packet;

public abstract class PacketEventHandler extends EventHandler{

    public abstract Packet onEvent(Packet eventData);
}
