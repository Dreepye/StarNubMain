package org.starnub.utilities.events.types;

/**
 * Represents a IntegerEvent with a Event Key (String) and Event Data (Integer)
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public class IntegerEvent extends Event<String> {

    public IntegerEvent(String EVENT_KEY, Integer EVENT_DATA) {
        super(EVENT_KEY, EVENT_DATA);
    }

}
