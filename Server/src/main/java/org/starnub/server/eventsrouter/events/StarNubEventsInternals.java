package org.starnub.server.eventsrouter.events;

import org.starnub.server.StarNub;

public class StarNubEventsInternals extends ObjectEvent{

    public StarNubEventsInternals(String EVENT_KEY, Object OBJECT) {
        super(EVENT_KEY, OBJECT);
    }

    public static void eventSend_StarNub_Startup_Complete (Object eventData) {
        StarNub.getStarNubEventRouter().notify(new ObjectEvent("StarNub_Startup_Complete", eventData));
    }

    public static void eventSend_StarNub_Checks (long eventData) {
        StarNub.getStarNubEventRouter().notify(new TimeEvent("StarNub_Checks", eventData));
    }

    public static void eventSend_StarNub_Socket_Connection_Attempt_Client (Object eventData) {
        StarNub.getStarNubEventRouter().notify(new ObjectEvent("StarNub_Socket_Connection_Attempt_Client", eventData));
    }

    public static void eventSend_StarNub_Socket_Connection_Attempt_IP_Blocked_Spam_Client (Object eventData) {
        StarNub.getStarNubEventRouter().notify(new ObjectEvent("StarNub_Socket_Connection_Attempt_IP_Blocked_Spam_Client", eventData));
    }

    public static void eventSend_StarNub_Socket_Connection_Success_Client (Object eventData) {
        StarNub.getStarNubEventRouter().notify(new ObjectEvent("StarNub_Socket_Connection_Success_Client", eventData));
    }

    public static void eventSend_StarNub_Socket_Connection_Failed_Blocked_IP_Client (Object eventData) {
        StarNub.getStarNubEventRouter().notify(new ObjectEvent("StarNub_Socket_Connection_Failed_Blocked_IP_Client", eventData));
    }

    public static void eventSend_StarNub_Socket_Connection_Attempt_Server (Object eventData) {
        StarNub.getStarNubEventRouter().notify(new ObjectEvent("StarNub_Socket_Connection_Attempt_Server", eventData));
    }

    public static void eventSend_StarNub_Socket_Connection_Success_Server (Object eventData) {
        StarNub.getStarNubEventRouter().notify(new ObjectEvent("StarNub_Socket_Connection_Success_Server", eventData));
    }

}
