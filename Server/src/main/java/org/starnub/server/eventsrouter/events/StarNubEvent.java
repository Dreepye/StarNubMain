package org.starnub.server.eventsrouter.events;

import lombok.Getter;

public abstract class StarNubEvent<T> {

    @Getter
    private final T EVENT_KEY;

    public StarNubEvent(T EVENT_KEY){
        this.EVENT_KEY = EVENT_KEY;
    }

    public abstract String getExtraEventData();
}
