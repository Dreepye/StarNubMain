package server.eventsrouter.events;

public abstract class StarNubEvent<T> {


    private final T EVENT_KEY;

    public StarNubEvent(T EVENT_KEY){
        this.EVENT_KEY = EVENT_KEY;
    }

    public abstract String getExtraEventData();
}
