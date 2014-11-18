package org.starnub.server.eventsrouter.subscriptions;

import lombok.Getter;

public class EventSubscription<T> {

    @Getter
    private final String SUBSCRIBER_NAME;
    @Getter
    private final T EVENT_HANDLER;

    public EventSubscription(String SUBSCRIBER_NAME, T EVENT_HANDLER) {
        this.SUBSCRIBER_NAME = SUBSCRIBER_NAME;
        this.EVENT_HANDLER = EVENT_HANDLER;
    }
}
