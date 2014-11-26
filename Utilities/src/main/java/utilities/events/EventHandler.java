package utilities.events;

public abstract class EventHandler<T1> {

    public abstract T1 onEvent(T1 eventData);

    public abstract void submitRegistration();
}
