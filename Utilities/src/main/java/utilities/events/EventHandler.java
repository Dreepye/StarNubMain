package utilities.events;

/**
 * Represents a EventHandler to be used with an {@link utilities.events.EventSubscription} and
 * {@link utilities.events.EventRouter}
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public abstract class EventHandler<T> {

    public abstract void onEvent(T eventData);
}
