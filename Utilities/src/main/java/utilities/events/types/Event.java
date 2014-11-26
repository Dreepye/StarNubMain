package utilities.events.types;

/**
 * Represents a Event with a Event Key and Event Data
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public abstract class Event<T1, T2> {

    private final T1 EVENT_KEY;
    private final T2 EVENT_DATA;

    public Event(T1 EVENT_KEY, T2 EVENT_DATA){
        this.EVENT_KEY = EVENT_KEY;
        this.EVENT_DATA = EVENT_DATA;
    }

    public T1 getEVENT_KEY() {
        return EVENT_KEY;
    }

    public T2 getEVENT_DATA() {
        return EVENT_DATA;
    }
}
