package utilities.events;

public abstract class EventSubscription<T>{

    private final String SUBSCRIBER_NAME;
    private final EventHandler<T> EVENT_HANDLER;

    public String getSUBSCRIBER_NAME() {
        return SUBSCRIBER_NAME;
    }

    public EventHandler<T> getEVENT_HANDLER() {
        return EVENT_HANDLER;
    }

    public EventSubscription(String SUBSCRIBER_NAME, EventHandler<T> EVENT_HANDLER) {
        this.SUBSCRIBER_NAME = SUBSCRIBER_NAME;
        this.EVENT_HANDLER = EVENT_HANDLER;
    }

    public abstract void submitRegistration();

    public abstract void removeRegistration();

}

