package server.eventsrouter.handlers;

import server.eventsrouter.events.StarNubEvent;

public abstract class StarNubEventHandler extends EventHandler {

    public abstract void onEvent(StarNubEvent<String> eventData);

}
