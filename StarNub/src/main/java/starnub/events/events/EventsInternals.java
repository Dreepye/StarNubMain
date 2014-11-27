package starnub.events.events;

import utilities.events.types.StringEvent;

public class EventsInternals extends StringEvent {

    public EventsInternals(String EVENT_KEY, Object OBJECT) {
        super(EVENT_KEY, OBJECT);
    }

    public static void eventSend_StarNub_Startup_Complete (Object eventData) {

    }

    public static void eventSend_StarNub_Checks (long eventData) {
        new StarNubEvent("StarNub_Checks", eventData);
    }


    public static void eventSend_StarNub_Socket_Connection_Attempt_IP_Blocked_Spam_Client (Object eventData) {
        new StarNubEvent("StarNub_Socket_Connection_Attempt_IP_Blocked_Spam_Client", eventData);
    }

    public static void eventSend_StarNub_Socket_Connection_Success_Client (Object eventData) {

    }

    public static void eventSend_StarNub_Socket_Connection_Failed_Blocked_IP_Client (Object eventData) {

    }


    public static void eventSend_StarNub_Socket_Connection_Success_Server (Object eventData) {

    }

}
