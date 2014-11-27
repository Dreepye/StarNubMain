package starnub.events.events;

import starnub.StarNub;
import utilities.connectivity.connection.Connection;
import utilities.events.types.Event;

public class NonPlayerEvent extends Event<String> {


    private final Connection NON_PLAYER;
    private final Object EVENT_DATA_OBJECT;

    public Connection getNON_PLAYER() {
        return NON_PLAYER;
    }

    public Object getEVENT_DATA_OBJECT() {
        return EVENT_DATA_OBJECT;
    }

    /**
     *
     * @param NON_PLAYER Player representing the player involved in this event
     * @param EVENT_DATA_OBJECT String representing the data (Reason, Command...)
     */
    public NonPlayerEvent(String EVENT_KEY, Connection NON_PLAYER, Object EVENT_DATA_OBJECT) {
        super(EVENT_KEY, event_data);
        this.NON_PLAYER = NON_PLAYER;
        this.EVENT_DATA_OBJECT = EVENT_DATA_OBJECT;
    }

    public static void eventSend_NonPlayer_Connection_Attempt (Connection nonPlayer) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Connection_Attempt", nonPlayer, null));
    }

    public static void eventSend_NonPlayer_Connection_Failure (Connection nonPlayer) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Connection_Failure", nonPlayer, null));
    }

    public static void eventSend_NonPlayer_Connection_Success (Connection nonPlayer) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Connection_Success", nonPlayer, null));
    }

    public static void eventSend_NonPlayer_Disconnect(Connection nonPlayer, String reason) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Disconnected", nonPlayer, reason));
    }

    public static void eventSend_NonPlayer_Command_Used(Connection nonPlayer, String command) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Command_Used", nonPlayer, command));
    }

    public static void eventSend_NonPlayer_Command_Delivered_To_Plugin(Connection nonPlayer, String command) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Command_Delivered_To_Plugin", nonPlayer, command));
    }

    public static void eventSend_NonPlayer_Command_Failed_Spam(Connection nonPlayer, String command) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Command_Failed_Spam", nonPlayer, command));
    }

    public static void eventSend_NonPlayer_Command_Failed_Command_Blocked(Connection nonPlayer, String command) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Command_Failed_CommandBlocked", nonPlayer, command));
    }

    public static void eventSend_NonPlayer_Command_Failed_Permissions(Connection nonPlayer, String command) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Command_Failed_Permissions", nonPlayer, command));
    }

    public static void eventSend_NonPlayer_Command_Failed_No_Account(Connection nonPlayer, String command) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Command_Failed_No_Account", nonPlayer, command));
    }

    public static void eventSend_NonPlayer_Command_Failed_No_Plugin(Connection nonPlayer, String command) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Command_Failed_No_Plugin", nonPlayer, command));
    }

    public static void eventSend_NonPlayer_Command_Failed_No_Command(Connection nonPlayer, String command) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Command_Failed_No_Command", nonPlayer, command));
    }

    public static void eventSend_NonPlayer_Command_Failed_Argument_Count(Connection nonPlayer, String command) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Command_Failed_Argument_Count", nonPlayer, command));
    }

    public static void eventSend_NonPlayer_Command_Failed_NonPlayer_Cannot_Use(Connection nonPlayer, String command) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Command_Failed_Player_Cannot_Use", nonPlayer, command));
    }

    public static void eventSend_NonPlayer_Chat_Success_Public_(Connection nonPlayer, String channelName, String chatMessage) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Chat_Success_Public_" + channelName, nonPlayer, chatMessage));
    }

    public static void eventSend_NonPlayer_Chat_Success_Private_(Connection nonPlayer, String channelName, String chatMessage) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Chat_Success_Private_" + channelName, nonPlayer, chatMessage));
    }

    public static void eventSend_NonPlayer_Chat_Success_Local(Connection nonPlayer, String chatMessage) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Chat_Success_Local", nonPlayer, chatMessage));
    }

    public static void eventSend_NonPlayer_Chat_Success_Whisper(Connection nonPlayer, String chatMessage) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Chat_Success_Whisper", nonPlayer, chatMessage));
    }

    public static void eventSend_NonPlayer_Chat_Failed_Muted(Connection nonPlayer, String chatMessage) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Chat_Failed_Muted", nonPlayer, chatMessage));
    }

    public static void eventSend_NonPlayer_Chat_Failed_Spam(Connection nonPlayer, String chatMessage) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Chat_Failed_Spam", nonPlayer, chatMessage));
    }

    public String getExtraEventData(){
        return "Non-Player: " + getNON_PLAYER() + ", Object Data: " + getEVENT_DATA_OBJECT() + ".";
    }
}
