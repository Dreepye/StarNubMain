package utilities.events.types;

/**
 * Represents a LongEvent with a Event Key (String) and Event Data (Long)
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public class LongEvent extends Event<String> {

    public LongEvent(String EVENT_KEY, Long EVENT_DATA) {
        super(EVENT_KEY, EVENT_DATA);
    }
}
