package starnub.events.events;

import starnub.StarNub;
import starnub.connections.player.session.PendingPlayer;
import starnub.connections.player.session.Player;
import utilities.events.types.Event;
import utilities.events.types.StringEvent;

public class PlayerEvent extends StringEvent {

    private final Player PLAYER_SESSION;

    public Player getPLAYER_SESSION() {
        return PLAYER_SESSION;
    }

    /**
     *
     * @param PLAYER_SESSION Player representing the player involved in this event
     * @param EVENT_DATA_OBJECT String representing the data (Reason, Command...)
     */
    public PlayerEvent(String EVENT_KEY, Player PLAYER_SESSION, String EVENT_DATA_OBJECT) {
        super(EVENT_KEY, EVENT_DATA_OBJECT);
        this.PLAYER_SESSION = PLAYER_SESSION;
    }

    public static void eventSend_Player_Connection_Attempt (PendingPlayer pendingPlayer) {
        StarNub.getStarNubEventRouter().notify(new PlayerEvent("Player_Connection_Attempt", null, pendingPlayer));
    }

    public static void eventSend_Player_Connection_Failure_Whitelist (PendingPlayer pendingPlayer) {
        StarNub.getStarNubEventRouter().notify(new PlayerEvent("Player_Connection_Failure_Whitelist", null, pendingPlayer));
    }

    public static void eventSend_Player_Connection_Failure_Character_Already_Online (PendingPlayer pendingPlayer) {
        StarNub.getStarNubEventRouter().notify(new PlayerEvent("Player_Connection_Failure_Character_Already_Online", null, pendingPlayer));
    }

    public static void eventSend_Player_Connection_Failure_Banned_Temporary (PendingPlayer pendingPlayer) {
        StarNub.getStarNubEventRouter().notify(new PlayerEvent("Player_Connection_Failure_Banned_Temporary", null, pendingPlayer));
    }

    public static void eventSend_Player_Connection_Failure_Banned_Permanent (PendingPlayer pendingPlayer) {
        StarNub.getStarNubEventRouter().notify(new PlayerEvent("Player_Connection_Failure_Banned_Permanent", null, pendingPlayer));
    }

    public static void eventSend_Player_Connection_Success(Player playerSession) {
        StarNub.getStarNubEventRouter().notify(new PlayerEvent("Player_Connection_Success",playerSession, null));
    }

    public static void eventSend_Player_Disconnect(Player playerSession, String reason) {
        StarNub.getStarNubEventRouter().notify(new PlayerEvent("Player_Disconnected", playerSession, reason));
    }

    public static void eventSend_Player_Command_Used(Player playerSession, String command) {
        StarNub.getStarNubEventRouter().notify(new PlayerEvent("Player_Command_Used", playerSession, command));
    }

    public static void eventSend_Player_Command_Delivered_To_Plugin(Player playerSession, String command) {
        StarNub.getStarNubEventRouter().notify(new PlayerEvent("Player_Command_Delivered_To_Plugin", playerSession, command));
    }

    public static void eventSend_Player_Command_Failed_Spam(Player playerSession, String command) {
        StarNub.getStarNubEventRouter().notify(new PlayerEvent("Player_Command_Failed_Spam", playerSession, command));
    }

    public static void eventSend_Player_Command_Failed_Command_Blocked(Player playerSession, String command) {
        StarNub.getStarNubEventRouter().notify(new PlayerEvent("Player_Command_Failed_CommandBlocked", playerSession, command));
    }

    public static void eventSend_Player_Command_Failed_Permissions(Player playerSession, String command) {
        StarNub.getStarNubEventRouter().notify(new PlayerEvent("Player_Command_Failed_Permissions", playerSession, command));
    }

    public static void eventSend_Player_Command_Failed_No_Account(Player playerSession, String command) {
        StarNub.getStarNubEventRouter().notify(new PlayerEvent("Player_Command_Failed_No_Account", playerSession, command));
    }

    public static void eventSend_Player_Command_Failed_No_Plugin(Player playerSession, String command) {
        StarNub.getStarNubEventRouter().notify(new PlayerEvent("Player_Command_Failed_No_Plugin", playerSession, command));
    }

    public static void eventSend_Player_Command_Failed_No_Command(Player playerSession, String command) {
        StarNub.getStarNubEventRouter().notify(new PlayerEvent("Player_Command_Failed_No_Command", playerSession, command));
    }

    public static void eventSend_Player_Command_Failed_Argument_Count(Player playerSession, String command) {
        StarNub.getStarNubEventRouter().notify(new PlayerEvent("Player_Command_Failed_Argument_Count", playerSession, command));
    }

    public static void eventSend_Player_Command_Failed_Player_Cannot_Use(Player playerSession, String command) {
        StarNub.getStarNubEventRouter().notify(new PlayerEvent("Player_Command_Failed_Player_Cannot_Use", playerSession, command));
    }

    public static void eventSend_Player_Chat_Success_Public_(Player playerSession, String channelName, String chatMessage) {
        StarNub.getStarNubEventRouter().notify(new PlayerEvent("Player_Chat_Success_Public_" + channelName, playerSession, chatMessage));
    }

    public static void eventSend_Player_Chat_Success_Private_(Player playerSession, String channelName, String chatMessage) {
        StarNub.getStarNubEventRouter().notify(new PlayerEvent("Player_Chat_Success_Private_" + channelName, playerSession, chatMessage));
    }

    public static void eventSend_Player_Chat_Success_Local(Player playerSession, String chatMessage) {
        StarNub.getStarNubEventRouter().notify(new PlayerEvent("Player_Chat_Success_Local", playerSession, chatMessage));
    }

    public static void eventSend_Player_Chat_Success_Whisper(Player playerSession, String chatMessage) {
        StarNub.getStarNubEventRouter().notify(new PlayerEvent("Player_Chat_Success_Whisper", playerSession, chatMessage));
    }

    public static void eventSend_Player_Chat_Failed_Muted(Player playerSession, String chatMessage) {
        StarNub.getStarNubEventRouter().notify(new PlayerEvent("Player_Chat_Failed_Muted", playerSession, chatMessage));
    }

    public static void eventSend_Player_Chat_Failed_Spam(Player playerSession, String chatMessage) {
        StarNub.getStarNubEventRouter().notify(new PlayerEvent("Player_Chat_Failed_Spam", playerSession, chatMessage));
    }

    public String getExtraEventData(){
        return "Player Session: " + getPLAYER_SESSION() + ", Event Object: " + getEVENT_DATA_OBJECT() + ".";
    }


}
