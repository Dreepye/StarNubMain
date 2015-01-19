package org.starnub.utilities.events;

/**
 * Represents a EventHandler to be used with an {@link EventSubscription} and
 * {@link EventRouter}
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
@FunctionalInterface
public interface EventHandler<T> {

    public abstract void onEvent(T eventData);
}
