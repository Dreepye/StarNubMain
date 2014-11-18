package org.starnub.server.eventsrouter.events;

import lombok.Getter;

public class IntegerEvent extends StarNubEvent<String> {

    @Getter
    private final int COUNT;

    public IntegerEvent(String EVENT_KEY, int COUNT) {
        super(EVENT_KEY);
        this.COUNT = COUNT;
    }

    public String getExtraEventData(){
        return "Count: " + getCOUNT() + ".";
    }
}
