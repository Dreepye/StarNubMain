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

package starnubserver.connections.player.character;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import starnubserver.StarNub;
import starnubserver.database.tables.CharacterIPLog;
import starnubserver.events.events.StarNubEvent;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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

    @DatabaseField(generatedId = true, columnName = "IP_LOG_ID")
    private int characterIPLogId;

    @DatabaseField(foreign = true, canBeNull = false, uniqueCombo = true, columnName = "CHARACTER_ID")
    private PlayerCharacter playerCharacter;

    @DatabaseField(dataType = DataType.STRING, canBeNull = false,  uniqueCombo = true, columnName = "IP")
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

    public static void logCharacterIp(PlayerCharacter playerCharacter, InetAddress playerIP){
        CharacterIP characterIP = new CharacterIP(playerCharacter, playerIP, false);
        if (!CHARACTER_IP_LOG_DB.isCharacterIDAndIPComboRecorded(characterIP)) {
            CharacterIPLog.getInstance().create(characterIP);
            new StarNubEvent("Player_Character_New_IP", characterIP);
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

    public boolean isCharacterIDAndIPComboRecorded(CharacterIP characterIP){
        try {
            List<CharacterIP> characterIpLogs = getTableDao().queryForMatching(characterIP);
            if(characterIpLogs.size() >= 1){
                return true;
            }
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        return false;
    }

    public ArrayList<InetAddress> getCharactersAssociatedIPListFromCharacterId(int characterId) {
        try {
            ArrayList<InetAddress> associatedIps = new ArrayList<InetAddress>();
            /* Get IPs with that match the Starnub ID */
            GenericRawResults<String[]> rawResults = getTableDao().queryRaw("select IP from CHARACTER_IP_LOG where CHARACTER_ID = "+characterId);
            /* Get results of the query */
            List<String[]> results = rawResults.getResults();
            /* Get results set one */
            for (String[] result : results) {
                for (String stringResult : result) {
                    try {
                        InetAddress inetAddress = InetAddress.getByName(stringResult);
                        if (!associatedIps.contains(inetAddress)) {
                            associatedIps.add(inetAddress);
                        }
                    } catch (UnknownHostException e) {
                            /* Just means invalid ip, add error later TODO */
                    }
                }
            }
            return associatedIps;
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        return null;
    }

    public ArrayList<InetAddress> getCharactersAssociatedIPListFromCharacterIds(ArrayList<Integer> characterIds) {
        ArrayList<InetAddress> associatedIps = new ArrayList<>();
        for (Integer characterId : characterIds) {
            try {
                /* Get IPs with that match the Starnub ID */
                GenericRawResults<String[]> rawResults = getTableDao().queryRaw("select IP from CHARACTER_IP_LOG where CHARACTER_ID = "+characterId);
                /* Get results of the query */
                List<String[]> results = rawResults.getResults();
                /* Get results set one */
                for (String[] result : results) {
                    for (String stringResult : result) {
                        try {
                            InetAddress inetAddress = InetAddress.getByName(stringResult);
                            if (!associatedIps.contains(inetAddress)) {
                                associatedIps.add(inetAddress);
                            }
                        } catch (UnknownHostException e) {
                            /* Just means invalid ip, add error later TODO */
                        }
                    }
                }
            } catch (Exception e) {
                StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            }
        }
        return associatedIps;
    }

    public List<CharacterIP> getCharacterIpLogs(PlayerCharacter playerCharacter){
        try {
            return getTableDao().queryBuilder().where()
                    .eq("CHARACTER_ID", playerCharacter)
                    .query();
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        return null;
    }

    public List<CharacterIP> getCharacterIpLogs(String ip){
        try {
            return getTableDao().queryBuilder().where()
                    .eq("IP", ip)
                    .query();
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        return null;
    }

    public HashSet<CharacterIP> getAllCharactersAllIpsList(PlayerCharacter playerCharacter){
        List<CharacterIP> characterIPs = getCharacterIpLogs(playerCharacter);
        HashSet<CharacterIP> characterIPHashSet = new HashSet<>();
        characterIPHashSet.addAll(characterIPs);
        for (CharacterIP characterIP : characterIPs){
            List<CharacterIP> characterIPsSub = getCharacterIpLogs(characterIP.getSessionIpString());
            characterIPHashSet.addAll(characterIPsSub);
        }
        return characterIPHashSet;
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
