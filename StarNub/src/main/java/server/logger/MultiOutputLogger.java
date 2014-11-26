package server.logger;

import lombok.Getter;
import org.joda.time.DateTime;
import server.StarNub;
import server.eventsrouter.events.LogEvent;
import server.eventsrouter.events.StarNubEvent;
import server.eventsrouter.handlers.StarNubEventHandler;

import java.util.Map;

/**
 * StarNub's Logger Class
 * <p>
 * Notes: When referring to a log level it can be switched to
 * various levels 0 - Not Logging, 1 - Log To Screen, 2 - Log To File and
 * 3 - Log to Screen and File.
 * <p>
 * The defaults are not to log eventsrouter and debug, log starbounddata.packets.chat to screen and utilities.file and
 * everything else to utilities.file only.
 *
 *
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public class MultiOutputLogger {


    private static final MultiOutputLogger instance = new MultiOutputLogger();

    private final FileLog EVENT_DEBUG_LOGGER;
    private volatile int logEvent = 0;
    private volatile int logDebug = 0;

    private final FileLog CHAT_LOGGER;
    private volatile int logChat = 3;

    private final FileLog COMMAND_LOGGER;
    private volatile int logCommand = 2;

    private final FileLog INFORMATION_WARNING_LOGGER;
    private volatile int logInformation = 2;
    private volatile int logWarning = 2;

    private final FileLog ERROR_FATAL_LOGGER;
    private volatile int logError = 3;
    private volatile int logFatal = 3;

    private volatile int dayOfMonth;

    private MultiOutputLogger() {
        setLoggingLevels();
        this.EVENT_DEBUG_LOGGER = new FileLog("StarNub/Logs/Events_Debug/");
        this.CHAT_LOGGER = new FileLog("StarNub/Logs/Chat/");
        this.COMMAND_LOGGER = new FileLog("StarNub/Logs/Commands/");
        this.INFORMATION_WARNING_LOGGER = new FileLog("StarNub/Logs/Information_Warning/");
        this.ERROR_FATAL_LOGGER = new FileLog("StarNub/Logs/Error/");
        this.dayOfMonth = new DateTime().getDayOfMonth();
    }

    public static MultiOutputLogger getInstance() {
        return instance;
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This represents what items shall not be logged by StarNub.
     * <p>
     *

     */
    @SuppressWarnings("unchecked")
    private void setLoggingLevels() {
        Map<String, Object> logLevels = (Map) ((Map) StarNub.getConfiguration().getConfiguration().get("starnub settings")).get("log_levels");
        String illegal = "Illegal %s log level. Choose (0 - No Logging, 1 - Console Only, 2 - File Only, 3 - Console and File. Exiting StarNub....";
        for (String key : logLevels.keySet()){
            switch (key){
                case "events": {
                    int logLevelEvents = (int) logLevels.get(key);
                    if (logLevelEvents > 3) {
                        System.err.printf(illegal, "events");
                        System.exit(0);
                    }
                    this.logEvent = (int) logLevels.get(key);
                    break;
                }
                case "debug": {
                    int logLevelDebug = (int) logLevels.get(key);
                    if (logLevelDebug > 3) {
                        System.err.printf(illegal, "debug");
                        System.exit(0);
                    }
                    this.logDebug = (int) logLevels.get(key);
                    break;
                }
                case "packets/chat": {
                    int logLevelChat = (int) logLevels.get(key);
                    if (logLevelChat > 3) {
                        System.out.printf(illegal, "packets/chat");
                        System.exit(0);
                    }
                    this.logChat = (int) logLevels.get(key);
                    break;
                }
                case "command":{
                    int logLevelCommand = (int) logLevels.get(key);
                    if (logLevelCommand > 3) {
                        System.out.printf(illegal, "command");
                        System.exit(0);
                    }
                    this.logCommand = (int) logLevels.get(key);
                    break;
                }
                case "information":{
                    int logLevelInformation = (int) logLevels.get(key);
                    if (logLevelInformation > 3) {
                        System.out.printf(illegal, "information");
                        System.exit(0);
                    }
                    this.logInformation = (int) logLevels.get(key);
                    break;
                }
                case "warning":{
                    int logLevelWarning = (int) logLevels.get(key);
                    if (logLevelWarning > 3) {
                        System.err.printf(illegal, "warning");
                        System.exit(0);
                    }
                    this.logWarning = (int) logLevels.get(key);
                    break;
                }
                case "error":{
                    int logLevelError = (int) logLevels.get(key);
                    if (logLevelError != 3) {
                        if (logLevelError == 2) {
                            this.logError = 2;
                        } else {
                            System.err.println("StarNub Setup - Logging - Cannot Set Error - Error logging will at a minimum log to utilities.file.");
                        }
                        break;
                    }
                }
                case "fatal": {
                    int logLevelFatal = (int) logLevels.get(key);
                    if (logLevelFatal != 3) {
                        if (logLevelFatal == 2) {
                            this.logError = 2;
                        } else {
                            System.err.println("StarNub Setup - Logging - Cannot Set Fatal - Fatal logging will at a minimum log to utilities.file.");
                        }
                        break;
                    }
                }
            }
        }
    }

    public void eventListenerRegistration(){
        StarNub.getStarNubEventRouter().registerEventSubscription("StarNub", "StarNub_Log_Event", new StarNubEventHandler() {
            @Override
            public void onEvent(StarNubEvent eventData) {
                String logString = ((LogEvent) eventData).getLOG_STRING();
                switch (logEvent){
                    case 0: { /* Not Logging */ break; }
                    case 1: { System.err.println(logString); break; }
                    case 2: { EVENT_DEBUG_LOGGER.getFileWriter().writeToBuffer(logString); break; }
                    case 3: { System.out.println(logString); EVENT_DEBUG_LOGGER.getFileWriter().writeToBuffer(logString); break; }
                }
            }});
        StarNub.getStarNubEventRouter().registerEventSubscription("StarNub", "StarNub_Log_Debug", new StarNubEventHandler() {
                @Override
                public void onEvent(StarNubEvent eventData) {
                    String logString = ((LogEvent) eventData).getLOG_STRING();
                switch (logDebug){
                    case 0: { /* Not Logging */ break; }
                    case 1: { System.err.println(logString); break; }
                    case 2: { EVENT_DEBUG_LOGGER.getFileWriter().writeToBuffer(logString); break; }
                    case 3: { System.err.println(logString); EVENT_DEBUG_LOGGER.getFileWriter().writeToBuffer(logString); break; }
                }
            }});

        StarNub.getStarNubEventRouter().registerEventSubscription("StarNub", "StarNub_Log_Chat", new StarNubEventHandler() {
            @Override
            public void onEvent(StarNubEvent eventData) {
                String logString = ((LogEvent) eventData).getLOG_STRING();
                switch (logChat){
                    case 0: { /* Not Logging */ break; }
                    case 1: { System.out.println(logString); break; }
                    case 2: { CHAT_LOGGER.getFileWriter().writeToBuffer(logString); break; }
                    case 3: { System.out.println(logString); CHAT_LOGGER.getFileWriter().writeToBuffer(logString); break; }
                }
            }});
        StarNub.getStarNubEventRouter().registerEventSubscription("StarNub", "StarNub_Log_Command", new StarNubEventHandler() {
                @Override
                public void onEvent(StarNubEvent eventData) {
                    String logString = ((LogEvent) eventData).getLOG_STRING();
                    switch (logCommand){
                        case 0: { /* Not Logging */ break; }
                        case 1: { System.out.println(logString); break; }
                        case 2: { COMMAND_LOGGER.getFileWriter().writeToBuffer(logString) ;break; }
                        case 3: { System.out.println(logString); COMMAND_LOGGER.getFileWriter().writeToBuffer(logString); break; }
                    }
            }});
        StarNub.getStarNubEventRouter().registerEventSubscription("StarNub", "StarNub_Log_Information", new StarNubEventHandler() {
            @Override
            public void onEvent(StarNubEvent eventData) {
                String logString = ((LogEvent) eventData).getLOG_STRING();
                switch (logInformation){
                    case 0: { /* Not Logging */ break; }
                    case 1: { System.out.println(logString); break; }
                    case 2: { INFORMATION_WARNING_LOGGER.getFileWriter().writeToBuffer(logString); break; }
                    case 3: { System.out.println(logString); INFORMATION_WARNING_LOGGER.getFileWriter().writeToBuffer(logString); break; }
                }
        }});
        StarNub.getStarNubEventRouter().registerEventSubscription("StarNub", "StarNub_Log_Warning", new StarNubEventHandler() {
                @Override
                public void onEvent(StarNubEvent eventData) {
                    String logString = ((LogEvent) eventData).getLOG_STRING();
                    switch (logWarning){
                        case 0: { /* Not Logging */ break; }
                        case 1: { System.out.println(logString); break; }
                        case 2: { INFORMATION_WARNING_LOGGER.getFileWriter().writeToBuffer(logString); break; }
                        case 3: { System.out.println(logString); INFORMATION_WARNING_LOGGER.getFileWriter().writeToBuffer(logString); break; }
                    }
        }});
        StarNub.getStarNubEventRouter().registerEventSubscription("StarNub", "StarNub_Log_Error", new StarNubEventHandler() {
            @Override
            public void onEvent(StarNubEvent eventData) {
                String logString = ((LogEvent) eventData).getLOG_STRING();
                switch (logError){
                    case 0: { /* Not Logging */ break; }
                    case 1: { System.out.println(logString); break; }
                    case 2: { ERROR_FATAL_LOGGER.getFileWriter().writeToBuffer(logString); break; }
                    case 3: { System.out.println(logString); ERROR_FATAL_LOGGER.getFileWriter().writeToBuffer(logString); ERROR_FATAL_LOGGER.getFileWriter().flushLogs(); break; }
                }
        }});
        StarNub.getStarNubEventRouter().registerEventSubscription("StarNub", "StarNub_Log_Fatal", new StarNubEventHandler() {
                @Override
                public void onEvent(StarNubEvent eventData) {
                    String logString = ((LogEvent) eventData).getLOG_STRING();
                    switch (logFatal){
                        case 0: { /* Not Logging */ break; }
                        case 1: { System.out.println(logString); break; }
                        case 2: { ERROR_FATAL_LOGGER.getFileWriter().writeToBuffer(logString); break; }
                        case 3: { System.out.println(logString); ERROR_FATAL_LOGGER.getFileWriter().writeToBuffer(logString); ERROR_FATAL_LOGGER.getFileWriter().flushLogs(); break; }
                    }
            }});
    }


    public void flushAllLogs(){
        this.EVENT_DEBUG_LOGGER.getFileWriter().flushLogs();
        this.CHAT_LOGGER.getFileWriter().flushLogs();
        this.COMMAND_LOGGER.getFileWriter().flushLogs();
        this.INFORMATION_WARNING_LOGGER.getFileWriter().flushLogs();
        this.ERROR_FATAL_LOGGER.getFileWriter().flushLogs();
    }

    public void logRotateCheckAllLogs(){
        double logSize = (double) ((Map)StarNub.getConfiguration().getConfiguration().get("starnub settings")).get("log_size");
        this.EVENT_DEBUG_LOGGER.logRotateNewDateFileSize(dayOfMonth, logSize, "kilobytes");
        this.CHAT_LOGGER.logRotateNewDateFileSize(dayOfMonth, logSize, "kilobytes");
        this.COMMAND_LOGGER.logRotateNewDateFileSize(dayOfMonth, logSize, "kilobytes");;
        this.INFORMATION_WARNING_LOGGER.logRotateNewDateFileSize(dayOfMonth, logSize, "kilobytes");
        this.ERROR_FATAL_LOGGER.logRotateNewDateFileSize(dayOfMonth, logSize, "kilobytes");
    }

    public void closeAllLogs(){
        this.EVENT_DEBUG_LOGGER.closeLog();
        this.CHAT_LOGGER.closeLog();
        this.COMMAND_LOGGER.closeLog();
        this.INFORMATION_WARNING_LOGGER.closeLog();
        this.ERROR_FATAL_LOGGER.closeLog();
    }

    public void startAllLogs(){
        this.EVENT_DEBUG_LOGGER.startNewLog();
        this.CHAT_LOGGER.startNewLog();
        this.COMMAND_LOGGER.startNewLog();
        this.INFORMATION_WARNING_LOGGER.startNewLog();
        this.ERROR_FATAL_LOGGER.startNewLog();
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Checks to see if Event logging is on.
     * <p>
     *
     * @return boolean true if on
     */
    public boolean isLogEvent() {
        return logEvent != 0 ;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Checks to see if Event logging to utilities.file is on.
     * <p>
     *
     * @return boolean true if on
     */
    public boolean isLogEventToFile() {
        return logEvent == 2 || logEvent == 3;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Checks to see if Event logging to screen is on.
     * <p>
     *
     * @return boolean true if on
     */
    public boolean isLogEventToScreen() {
        return logEvent == 1 || logEvent == 3;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Checks to see if Debug logging is on.
     * <p>
     *
     * @return boolean true if on
     */
    public boolean isLogDebug() {
        return logDebug != 0;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Checks to see if Debug logging to utilities.file is on.
     * <p>
     *
     * @return boolean true if on
     */
    public boolean isLogDebugToFile() {
        return logDebug == 2 || logDebug == 3;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Checks to see if Debug logging to screen is on.
     * <p>
     *
     * @return boolean true if on
     */
    public boolean isLogDebugToScreen() {
        return logDebug == 1 || logDebug == 3;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Checks to see if Chat logging is on.
     * <p>
     *
     * @return boolean true if on
     */
    public boolean isLogChat() {
        return logChat != 0;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Checks to see if Chat logging to utilities.file is on.
     * <p>
     *
     * @return boolean true if on
     */
    public boolean isLogChatToFile() {
        return logChat == 2 || logChat == 3;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Checks to see if Chat logging to screen is on.
     * <p>
     *
     * @return boolean true if on
     */
    public boolean isLogChatToScreen() {
        return logChat == 1 || logChat == 3;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Checks to see if Command logging is on.
     * <p>
     *
     * @return boolean true if on
     */
    public boolean isLogCommand() {
        return logCommand != 0;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Checks to see if Command logging to utilities.file is on.
     * <p>
     *
     * @return boolean true if on
     */
    public boolean isLogCommandToFile() {
        return logCommand == 2 || logCommand == 3;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Checks to see if Command logging to screen is on.
     * <p>
     *
     * @return boolean true if on
     */
    public boolean isLogCommandToScreen() {
        return logCommand == 1 || logCommand == 3;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Checks to see if Info logging is on.
     * <p>
     *
     * @return boolean true if on
     */
    public boolean isLogInformation() {
        return logInformation != 0;
    }


    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Checks to see if Info logging to utilities.file is on.
     * <p>
     *
     * @return boolean true if on
     */
    public boolean isLogInformationToFile() {
        return logInformation == 2 || logInformation == 3;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Checks to see if Info logging to screen is on.
     * <p>
     *
     * @return boolean true if on
     */
    public boolean isLogInformationToScreen() {
        return logInformation == 1 || logInformation == 3;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Checks to see if Warn logging is on.
     * <p>
     *
     * @return boolean true if on
     */
    public boolean isLogWarning() {
        return logWarning != 0;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Checks to see if Warn logging to utilities.file is on.
     * <p>
     *
     * @return boolean true if on
     */
    public boolean isLogWarningToFile() {
        return logWarning == 2 || logWarning == 3;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Checks to see if Warn logging to screen is on.
     * <p>
     *
     * @return boolean true if on
     */
    public boolean isLogWarningToScreen() {
        return logWarning == 1 || logWarning == 3;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Checks to see if Error logging is on.
     * <p>
     *
     * @return boolean true if on
     */
    public boolean isLogError() {
        return logError != 0;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Checks to see if Error logging to utilities.file is on.
     * <p>
     *
     * @return boolean true if on
     */
    public boolean isLogErrorToFile() {
        return logError == 2 || logError == 3;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Checks to see if Error logging to screen is on.
     * <p>
     *
     * @return boolean true if on
     */
    public boolean isLogErrorToScreen() {
        return logError == 1 || logError == 3;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Checks to see if Fatal logging is on.
     * <p>
     *
     * @return boolean true if on
     */
    public boolean isLogFatal() {
        return logFatal != 0;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Checks to see if Fatal logging to utilities.file is on.
     * <p>
     *
     * @return boolean true if on
     */
    public boolean isLogFatalToFile() {
        return logFatal == 2 || logFatal == 3;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: Checks to see if Fatal logging to screen is on.
     * <p>
     *
     * @return boolean true if on
     */
    public boolean isLogFatalToScreen() {
        return logFatal == 1 || logFatal == 3;
    }

    //TODO

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will print a "Event" console message with a current time stamp and append it into
     * StarNub/Server Logs/{date stamp}.log. If the Server owner turn this type of logging off, they
     * will not see any of these messages.
     * <p>
     *
     * @param message String that represents the Chat message
     */
    public void cEvePrint(Object sender, String message) {
        StarNub.getStarNubEventRouter().notify(new LogEvent(
                "StarNub_Log_Event",
                stringBuilder(sender, " Event", message)));
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will print a "Debug" console message with a current time stamp and append it into
     * StarNub/Server Logs/{date stamp}.log. If the Server owner turn this type of logging off, they
     * will not see any of these messages.
     * <p>
     *
     * @param message String that represents the Chat message
     */
    public void cDebPrint(Object sender, String message) {
        StarNub.getStarNubEventRouter().notify(new LogEvent(
                "StarNub_Log_Debug",
                stringBuilder(sender, " Debug", message)));
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will print a "Chat" console message with a current time stamp and append it into
     * StarNub/Server Logs/{date stamp}.log. If the Server owner turn this type of logging off, they
     * will not see any of these messages.
     * <p>
     *
     * @param sender      Object which represents the message sender can be a string, class, player, management client
     * @param message     String message to be sent to the starbounddata.packets.starbounddata.packets.server
     * @param destination String where the message is going
     */
    public void cChatPrint(Object sender, String message, Object destination) {
        boolean charName = (boolean) ((Map) StarNub.getConfiguration().getConfiguration().get("starnub settings")).get("log_character_names");
        StarNub.getStarNubEventRouter().notify(new LogEvent(
                "StarNub_Log_Chat",
                stringBuilder(
                        "StarNub",
                        " Chat",
                        "[" + StarNub.getMessageSender().cUnknownNameBuilder(sender, true, charName) + " -> " +
                                StarNub.getMessageSender().cUnknownNameBuilder(destination, true, charName) + "]: "
                                + StarNub.getServer().getServerChat().getChatFilter().removeColors(message))));
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will print a "Chat" console message with a current time stamp and append it into
     * StarNub/Server Logs/{date stamp}.log. If the Server owner turn this type of logging off, they
     * will not see any of these messages.
     * <p>
     *
     * @param senderReceiver      String which represents the message sender and receiver of the message
     * @param message     String message to be sent to the starbounddata.packets.starbounddata.packets.server
     */
    public void cWhisperPrint(String senderReceiver, String message) {
        StarNub.getStarNubEventRouter().notify(new LogEvent(
                "StarNub_Log_Chat",
                stringBuilder(
                        "StarNub",
                        " Chat",
                        "[" + senderReceiver + "]: "
                                + StarNub.getServer().getServerChat().getChatFilter().removeColors(message))));
    }


    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This will print a "Command-Fail" console message with a current time stamp and append it into
     * StarNub/Server Logs/{date stamp}.log. If the Server owner turn this type of logging off, they
     * will not see any of these messages.
     * <p>
     *
     * @param sender Object so that we can receive multiple sender types
     * @param message String message that was sent to the command sender, the reasons for failure
     */
    public void cCommandFailurePrint(Object sender, String message) {
        boolean charName = (boolean) ((Map) StarNub.getConfiguration().getConfiguration().get("starnub settings")).get("log_character_names");
        StarNub.getStarNubEventRouter().notify(new LogEvent(
                "StarNub_Log_Command",
                stringBuilder(
                        "StarNub",
                        " Command-Fail",
                        StarNub.getMessageSender().cUnknownNameBuilder(sender, true, charName) + " -> " + message)));
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This will print a "Command-Success" console message with a current time stamp and append it into
     * StarNub/Server Logs/{date stamp}.log. If the Server owner turn this type of logging off, they
     * will not see any of these messages.
     * <p>
     *
     * @param sender Object so that we can receive multiple sender types
     * @param message String representing the command that was successfully executed
     */
    public void cCommandSuccessPrint(Object sender, String message) {
        boolean charName = (boolean) ((Map) StarNub.getConfiguration().getConfiguration().get("starnub settings")).get("log_character_names");
        StarNub.getStarNubEventRouter().notify(new LogEvent(
                "StarNub_Log_Command",
                stringBuilder(
                        "StarNub",
                        " Command-Success",
                        StarNub.getMessageSender().cUnknownNameBuilder(sender, true, charName) + " -> " + message)));
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will print a "Info" console message with a current time stamp and append it into
     * StarNub/Server Logs/{date stamp}.log. If the Server owner turn this type of logging off, they
     * will not see any of these messages.
     * <p>
     *
     * @param sender Object so that we can receive multiple sender types
     * @param message String that represents the Info message
     */
    public void cInfoPrint(Object sender, String message) {
        StarNub.getStarNubEventRouter().notify(new LogEvent(
                "StarNub_Log_Information",
                stringBuilder(sender, " Info", message)));
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will print a "Warn" console message with a current time stamp and append it into
     * StarNub/Error Logs/{date stamp}.log. If the Server owner turn this type of logging off, they
     * will not see any of these messages.
     * <p>
     *
     * @param sender Object so that we can receive multiple sender types
     * @param message String that represents the error message
     */
    public void cWarnPrint(Object sender, String message) {
        StarNub.getStarNubEventRouter().notify(new LogEvent(
                "StarNub_Log_Warning",
                stringBuilder(sender, " Warning", message)));
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will print a "Error" console message with a current time stamp and append it into
     * StarNub/Error Logs/{date stamp}.log. If the Server owner turn this type of logging off, they
     * will not see any of these messages.
     * <p>
     *
     * @param sender Object so that we can receive multiple sender types
     * @param message String that represents the error message
     */
    public void cErrPrint(Object sender, String message) {
        StarNub.getStarNubEventRouter().notify(new LogEvent(
                "StarNub_Log_Error",
                stringBuilder(sender, " Fatal", message)));
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will print a "Fatal" console message with a current time stamp and append it into
     * StarNub/Error Logs/{date stamp}.log. If the Server owner turn this type of logging off, they
     * will not see any of these messages.
     * <p>
     *
     * @param sender Object so that we can receive multiple sender types
     * @param message String that represents the error message
     */
    public void cFatPrint(Object sender, String message) {
        StarNub.getStarNubEventRouter().notify(new LogEvent(
                "StarNub_Log_Fatal",
                stringBuilder(sender, " Fatal", message)));
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This is the final console print method, this prints to standard error
     * <p>
     */

    private String stringBuilder(Object sender, String type, String message) {
        return timeStamp() + "[" + StarNub.getMessageSender().cNonPlayerNameBuild(sender) + type + "]: " + message;
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This will return a current time stamp based on a 24 hour clock
     * <p>
     *
     * @return String that is now timed stamped
     */
    private String timeStamp() {
        return StarNub.getDateAndTimes().getFormattedTimeNow("[HH:mm:ss]");
    }

}
