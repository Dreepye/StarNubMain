package server.eventsrouter.events;

import lombok.Getter;

public class ObjectEvent extends StarNubEvent<String> {

    @Getter
    private final Object OBJECT;

    public ObjectEvent(String EVENT_KEY, Object OBJECT) {
        super(EVENT_KEY);
        this.OBJECT = OBJECT;
    }

    public String getExtraEventData(){
        return "Object Data: " + getOBJECT() + ".";
    }
}
