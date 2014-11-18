package org.starnub.server.eventsrouter.events;

import lombok.Getter;

public class TimeEvent extends StarNubEvent<String> {

    @Getter
    private final long TIME;

    public TimeEvent(String EVENT_KEY, long TIME) {
        super(EVENT_KEY);
        this.TIME = TIME;
    }

    public String getExtraEventData(){
        return "Time: " + getTIME();
    }
}
