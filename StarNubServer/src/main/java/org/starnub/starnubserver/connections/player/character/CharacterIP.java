/*
* Copyright (C) 2014 www.StarNub.org - Underbalanced
*
* This utilities.file is part of org.starnub a Java Wrapper for Starbound.
*
* This above mentioned StarNub software is free software:
* you can redistribute it and/or modify it under the terms
* of the GNU General Public License as published by the Free
* Software Foundation, either version  3 of the License, or
* any later version. This above mentioned CodeHome software
* is distributed in the hope that it will be useful, but
* WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
* the GNU General Public License for more details. You should
* have received a copy of the GNU General Public License in
* this StarNub Software.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.starnub.starnubserver.connections.player.character;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.commons.lang3.StringUtils;
import org.starnub.starnubserver.database.tables.CharacterIPLog;
import org.starnub.starnubserver.events.events.StarNubEvent;
import org.starnub.starnubserver.database.tables.CharacterIPLog;
import org.starnub.starnubserver.events.events.StarNubEvent;

import java.net.InetAddress;
import java.util.*;

/**
 * This class represents a character ip log entry
 * <p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 *
 */
@DatabaseTable(tableName = "CHARACTER_IP_LOG")
public class CharacterIP {

    private final static CharacterIPLog CHARACTER_IP_LOG_DB = CharacterIPLog.getInstance();

    /* COLUMN NAMES */
    private final static String CHARACTER_IP_LOG_ID_COLUMN = "CHARACTER_IP_LOG_ID";
    private final static String CHARACTER_ID_COLUMN = "CHARACTER_ID";
    private final static String IP_COLUMN = "IP";

    @DatabaseField(generatedId = true, columnName = CHARACTER_IP_LOG_ID_COLUMN)
    private int characterIPLogId;

    @DatabaseField(foreign = true, canBeNull = false, uniqueCombo = true, columnName = CHARACTER_ID_COLUMN)
    private PlayerCharacter playerCharacter;

    @DatabaseField(dataType = DataType.STRING, canBeNull = false,  uniqueCombo = true, columnName = IP_COLUMN)
    private String sessionIpString;

    /**
     * Constructor for database purposes
     */
    public CharacterIP(){}

    /**
     * Constructor used in adding, removing or updating a object in the database table character_ip_log
     *
     * @param playerCharacter PlayerCharacter of the character that was seen with this ip
     * @param sessionIpString String representing the IP Address as a string
     */
    public CharacterIP(PlayerCharacter playerCharacter, String sessionIpString, boolean createEntry) {
        this.playerCharacter = playerCharacter;
        this.sessionIpString = sessionIpString;
        if (createEntry){
            CHARACTER_IP_LOG_DB.createOrUpdate(this);
        }
    }

    /**
     * Constructor used in adding, removing or updating a object in the database table character_ip_log
     *
     * @param playerCharacter PlayerCharacter of the character that was seen with this ip
     * @param sessionIp InetAddress representing the IP Address
     */
    public CharacterIP(PlayerCharacter playerCharacter, InetAddress sessionIp, boolean createEntry) {
        this.playerCharacter = playerCharacter;
        this.sessionIpString = StringUtils.remove(sessionIp.toString(), "/");
        if (createEntry){
            CHARACTER_IP_LOG_DB.createOrUpdate(this);
        }
    }

    public void logCharacterIp(){
        if (!ipCharacterLogCheck(this.playerCharacter)) {
            CHARACTER_IP_LOG_DB.createOrUpdate(this);
            new StarNubEvent("Player_Character_New_IP", this);
        }
    }

    public int getCharacterIPLogId() {
        return characterIPLogId;
    }

    public void setCharacterIPLogId(int characterIPLogId) {
        this.characterIPLogId = characterIPLogId;
    }

    public PlayerCharacter getPlayerCharacter() {
        return playerCharacter;
    }

    public void setPlayerCharacter(PlayerCharacter playerCharacter) {
        this.playerCharacter = playerCharacter;
    }

    public String getSessionIpString() {
        return sessionIpString;
    }

    public void setSessionIpString(String sessionIpString) {
        this.sessionIpString = sessionIpString;
    }

    public boolean ipCharacterLogCheck(PlayerCharacter playerCharacter){
        CharacterIP characterIpLogs = CHARACTER_IP_LOG_DB.getMatchingColumn1FirstSimilarColumn2(CHARACTER_ID_COLUMN, playerCharacter, IP_COLUMN, sessionIpString);
        return characterIpLogs != null;
    }

    /* DB METHODS*/

    public List<CharacterIP> getCharacterIpLogsByCharacter(){
        return getCharacterIpLogsByCharacter(playerCharacter);
    }

    public static List<CharacterIP> getCharacterIpLogsByCharacter(PlayerCharacter playerCharacter){
        return CHARACTER_IP_LOG_DB.getAllExact(CHARACTER_ID_COLUMN, playerCharacter);
    }

    public List<CharacterIP> getCharacterIpLogsByIP(){
        return getCharacterIpLogsByIP(sessionIpString);
    }

    public static List<CharacterIP> getCharacterIpLogsByIP(String ip){
        return CHARACTER_IP_LOG_DB.getAllExact(IP_COLUMN, ip);
    }

    public HashSet<CharacterIP> getCompleteAssociations(PlayerCharacter playerCharacter){
        HashSet<CharacterIP> completeSeenList = new HashSet<>();
        HashMap<Object, Boolean> hasNotBeenChecked = new HashMap<>();
        hasNotBeenChecked.put(playerCharacter, false);
        while (hasNotBeenChecked.containsValue(false)){
            for (Map.Entry<Object, Boolean> entry : hasNotBeenChecked.entrySet()){
                Object key = entry.getKey();
                List<CharacterIP> characterIPs;
                if (key instanceof PlayerCharacter) {
                    characterIPs = CharacterIP.getCharacterIpLogsByCharacter((PlayerCharacter) key);
                } else {
                    characterIPs = CharacterIP.getCharacterIpLogsByIP((String) key);
                }
                entry.setValue(true);
                completeSeenList.addAll(characterIPs);
                for (CharacterIP characterIP : characterIPs){
                    PlayerCharacter playerCharacter1 = characterIP.getPlayerCharacter();
                    String ipString = characterIP.getSessionIpString();
                    if(hasNotBeenChecked.containsKey(playerCharacter1)){
                        hasNotBeenChecked.put(playerCharacter1, false);
                    }
                    if (hasNotBeenChecked.containsKey(ipString)){
                        hasNotBeenChecked.put(ipString, false);
                    }
                }
            }
        }
        return completeSeenList;
    }

    @Override
    public String toString() {
        return "CharacterIP{" +
                "characterIPLogId=" + characterIPLogId +
                ", playerCharacter=" + playerCharacter +
                ", sessionIpString='" + sessionIpString + '\'' +
                '}';
    }
}
