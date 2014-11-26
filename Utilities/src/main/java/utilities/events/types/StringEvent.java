package utilities.events.types;

/**
 * Represents a IntegerEvent with a Event Key (String) and Event Data (String)
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public class StringEvent extends Event<String, String> {

    public StringEvent(String EVENT_KEY, String EVENT_DATA) {
        super(EVENT_KEY, EVENT_DATA);
    }
}
