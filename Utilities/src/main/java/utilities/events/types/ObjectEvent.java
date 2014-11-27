package utilities.events.types;

/**
 * Represents a ObjectEvent with a Event Key (String) and Event Data (Object)
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public class ObjectEvent extends Event<String> {

    public ObjectEvent(String EVENT_KEY, Object EVENT_DATA) {
        super(EVENT_KEY, EVENT_DATA);
    }
}
