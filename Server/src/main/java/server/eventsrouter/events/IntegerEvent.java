package server.eventsrouter.events;

public class IntegerEvent extends StarNubEvent<String> {


    private final int COUNT;

    public IntegerEvent(String EVENT_KEY, int COUNT) {
        super(EVENT_KEY);
        this.COUNT = COUNT;
    }

    public int getCOUNT() {
        return COUNT;
    }

    public String getExtraEventData(){
        return "Count: " + getCOUNT() + ".";
    }
}
