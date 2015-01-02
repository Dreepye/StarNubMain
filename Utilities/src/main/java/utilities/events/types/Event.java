package utilities.events.types;

/**
 * Represents a Event with a Event Key and Event Data
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public abstract class Event<T1> {

    private final T1 EVENT_KEY;
    private final Object EVENT_DATA;
    private boolean recycle = false;

    public Event(T1 EVENT_KEY, Object EVENT_DATA){
        this.EVENT_KEY = EVENT_KEY;
        this.EVENT_DATA = EVENT_DATA;
    }

    public T1 getEVENT_KEY() {
        return EVENT_KEY;
    }

    public Object getEVENT_DATA() {
        return EVENT_DATA;
    }

    public boolean isRecycle() {
        return recycle;
    }

    public void recycle() {
        this.recycle = true;
    }

    public void resetRecycle() {
        this.recycle = false;
    }
}
