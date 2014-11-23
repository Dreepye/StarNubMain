package server.eventsrouter.events;

public class ObjectEvent extends StarNubEvent<String> {

    private final Object OBJECT;

    public Object getOBJECT() {
        return OBJECT;
    }

    public ObjectEvent(String EVENT_KEY, Object OBJECT) {
        super(EVENT_KEY);
        this.OBJECT = OBJECT;
    }

    public String getExtraEventData(){
        return "Object Data: " + getOBJECT() + ".";
    }
}
