package org.starnub.server.eventsrouter.events;

import lombok.Getter;

public class LogEvent extends StarNubEvent<String> {

    @Getter
    private final String LOG_STRING;

    public LogEvent(String EVENT_KEY, String LOG_STRING) {
        super(EVENT_KEY);
        this.LOG_STRING = LOG_STRING;
    }

    public String getExtraEventData(){
//        return "Log String: " + LOG_STRING + ".";
        return "String";
    }
}
