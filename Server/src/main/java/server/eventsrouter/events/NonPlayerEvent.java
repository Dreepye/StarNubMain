package server.eventsrouter.events;

import lombok.Getter;
import server.StarNub;
import server.connectedentities.Sender;

public class NonPlayerEvent extends StarNubEvent<String> {

    @Getter
    private final Sender NON_PLAYER;

    @Getter
    private final Object EVENT_DATA_OBJECT;

    /**
     *
     * @param NON_PLAYER Player representing the player involved in this event
     * @param EVENT_DATA_OBJECT String representing the data (Reason, Command...)
     */
    public NonPlayerEvent(String EVENT_KEY, Sender NON_PLAYER, Object EVENT_DATA_OBJECT) {
        super(EVENT_KEY);
        this.NON_PLAYER = NON_PLAYER;
        this.EVENT_DATA_OBJECT = EVENT_DATA_OBJECT;
    }

    public static void eventSend_NonPlayer_Connection_Attempt (Sender nonPlayer) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Connection_Attempt", nonPlayer, null));
    }

    public static void eventSend_NonPlayer_Connection_Failure (Sender nonPlayer) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Connection_Failure", nonPlayer, null));
    }

    public static void eventSend_NonPlayer_Connection_Success (Sender nonPlayer) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Connection_Success", nonPlayer, null));
    }

    public static void eventSend_NonPlayer_Disconnect(Sender nonPlayer, String reason) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Disconnected", nonPlayer, reason));
    }

    public static void eventSend_NonPlayer_Command_Used(Sender nonPlayer, String command) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Command_Used", nonPlayer, command));
    }

    public static void eventSend_NonPlayer_Command_Delivered_To_Plugin(Sender nonPlayer, String command) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Command_Delivered_To_Plugin", nonPlayer, command));
    }

    public static void eventSend_NonPlayer_Command_Failed_Spam(Sender nonPlayer, String command) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Command_Failed_Spam", nonPlayer, command));
    }

    public static void eventSend_NonPlayer_Command_Failed_Command_Blocked(Sender nonPlayer, String command) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Command_Failed_CommandBlocked", nonPlayer, command));
    }

    public static void eventSend_NonPlayer_Command_Failed_Permissions(Sender nonPlayer, String command) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Command_Failed_Permissions", nonPlayer, command));
    }

    public static void eventSend_NonPlayer_Command_Failed_No_Account(Sender nonPlayer, String command) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Command_Failed_No_Account", nonPlayer, command));
    }

    public static void eventSend_NonPlayer_Command_Failed_No_Plugin(Sender nonPlayer, String command) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Command_Failed_No_Plugin", nonPlayer, command));
    }

    public static void eventSend_NonPlayer_Command_Failed_No_Command(Sender nonPlayer, String command) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Command_Failed_No_Command", nonPlayer, command));
    }

    public static void eventSend_NonPlayer_Command_Failed_Argument_Count(Sender nonPlayer, String command) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Command_Failed_Argument_Count", nonPlayer, command));
    }

    public static void eventSend_NonPlayer_Command_Failed_NonPlayer_Cannot_Use(Sender nonPlayer, String command) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Command_Failed_Player_Cannot_Use", nonPlayer, command));
    }

    public static void eventSend_NonPlayer_Chat_Success_Public_(Sender nonPlayer, String channelName, String chatMessage) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Chat_Success_Public_" + channelName, nonPlayer, chatMessage));
    }

    public static void eventSend_NonPlayer_Chat_Success_Private_(Sender nonPlayer, String channelName, String chatMessage) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Chat_Success_Private_" + channelName, nonPlayer, chatMessage));
    }

    public static void eventSend_NonPlayer_Chat_Success_Local(Sender nonPlayer, String chatMessage) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Chat_Success_Local", nonPlayer, chatMessage));
    }

    public static void eventSend_NonPlayer_Chat_Success_Whisper(Sender nonPlayer, String chatMessage) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Chat_Success_Whisper", nonPlayer, chatMessage));
    }

    public static void eventSend_NonPlayer_Chat_Failed_Muted(Sender nonPlayer, String chatMessage) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Chat_Failed_Muted", nonPlayer, chatMessage));
    }

    public static void eventSend_NonPlayer_Chat_Failed_Spam(Sender nonPlayer, String chatMessage) {
        StarNub.getStarNubEventRouter().notify(new NonPlayerEvent("NonPlayer_Chat_Failed_Spam", nonPlayer, chatMessage));
    }

    public String getExtraEventData(){
        return "Non-Player: " + getNON_PLAYER() + ", Object Data: " + getEVENT_DATA_OBJECT() + ".";
    }
}
