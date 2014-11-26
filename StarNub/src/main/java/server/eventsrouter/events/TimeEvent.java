package server.eventsrouter.events;

public class TimeEvent extends StarNubEvent<String> {


    private final long TIME;

    public TimeEvent(String EVENT_KEY, long TIME) {
        super(EVENT_KEY);
        this.TIME = TIME;
    }

    public long getTIME() {
        return TIME;
    }

    public String getExtraEventData(){
        return "Time: " + getTIME();
    }
}
