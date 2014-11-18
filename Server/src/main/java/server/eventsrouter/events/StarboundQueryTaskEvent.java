package server.eventsrouter.events;

import server.StarNub;

public class StarboundQueryTaskEvent extends TaskEvent {

    /**
     * @param starting boolean set isStarting
     * @param running  boolean to set isRunning
     * @param complete boolean to set isComplete
     */
    public StarboundQueryTaskEvent(String EVENT_KEY, boolean starting, boolean running, boolean complete) {
        super(EVENT_KEY, starting, running, complete);
    }

    /**
     * @param starting boolean set isStarting
     * @param running  boolean to set isRunning
     * @param complete boolean to set isComplete
     */
    public void setTaskStatus(boolean starting, boolean running, boolean complete) {
        setStarting(starting);
        setRunning(running);
        setComplete(complete);
    }

    /**
     * Sent when the starbounddata.packets.starbounddata.packets.server query task has started. This is used to set a status on
     * {@link server.server.starbound.StarboundManager} this prevents the methods
     * being executed while the query is starting
     */
    public static void eventSend_Server_Query_Task_Starting() {
        StarNub.getStarNubEventRouter().notify(new StarboundQueryTaskEvent("Server_Query_Task_Starting", true, false, false));
        StarNub.getServer().getStarboundManager().getStarboundQueryTask().setTaskStatus(true, false, false);
    }

    /**
     * Sent when the starbounddata.packets.starbounddata.packets.server query task is running. This is used to set a status on
     * {@link server.server.starbound.StarboundManager} this prevents the methods
     * being executed while the a query task is running
     */
    public static void eventSend_Server_Query_Task_Running() {
        StarNub.getStarNubEventRouter().notify(new StarboundQueryTaskEvent("Server_Query_Task_Running", true, false, false));
        StarNub.getServer().getStarboundManager().getStarboundQueryTask().setTaskStatus(false, true, false);
    }

    /**
     * Sent when the starbounddata.packets.starbounddata.packets.server query task has completed and the methods on
     * {@link server.server.starbound.StarboundManager} are available for use
     */
    public static void eventSend_Server_Query_Task_Complete() {
        StarNub.getStarNubEventRouter().notify(new StarboundQueryTaskEvent("Server_Query_Task_Complete", true, false, false));
        StarNub.getServer().getStarboundManager().getStarboundQueryTask().setTaskStatus(false, false, true);
    }


}
