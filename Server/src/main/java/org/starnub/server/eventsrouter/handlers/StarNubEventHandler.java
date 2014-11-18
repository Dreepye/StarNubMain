package org.starnub.server.eventsrouter.handlers;

import org.starnub.server.eventsrouter.events.StarNubEvent;

public abstract class StarNubEventHandler extends EventHandler {

    public abstract void onEvent(StarNubEvent<String> eventData);

}
