package server.eventsrouter;

import server.eventsrouter.events.StarNubEvent;
import utilities.events.EventHandler;

public abstract class StarNubEventHandler extends EventHandler<StarNubEvent<String>> {

    @Override
    public abstract StarNubEvent<String> onEvent(StarNubEvent<String> eventData);

}
